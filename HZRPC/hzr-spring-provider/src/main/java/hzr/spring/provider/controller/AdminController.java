package hzr.spring.provider.controller;

import com.google.common.base.Splitter;
import hzr.common.bootstrap.ClientBuilder;
import hzr.common.proxy.JDKProxy;
import hzr.register.Constant;
import hzr.spring.provider.exmple.service.IServiceTest;
import hzr.spring.provider.model.ServiceModel;
import hzr.spring.provider.model.ServiceProvider;
import hzr.spring.provider.model.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zz
 **/
@RequestMapping("/test")
@Controller
@Slf4j
public class AdminController {


    @RequestMapping("/list")
    public String getServerList(Model model) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181");
        List<String> services = zkClient.getChildren("/service");
        final List<ServiceModel> serviceModels = new ArrayList<>();
        if (!CollectionUtils.isEmpty(services)) {
            for (String serviceName : services) {
                ServiceModel serviceModel = new ServiceModel();
                serviceModel.setServiceName(serviceName);
                String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
                List<String> addressList = zkClient.getChildren(servicePath);

                if (addressList.size() == 0) {
                    zkClient.deleteRecursive(servicePath);
                } else {
                    ServiceProvider serviceProvider = new ServiceProvider();
                    if (!CollectionUtils.isEmpty(addressList)) {
                        for (String address : addressList) {
                            address = zkClient.readData(servicePath + "/" + address);
                            List<String> serviceProviderPayLoadTokens = Splitter.on(":").splitToList(address);
                            serviceProvider.setIp(serviceProviderPayLoadTokens.get(0));
                            serviceProvider.setPort(serviceProviderPayLoadTokens.get(1));
                        }
                    }
                    serviceModel.setServiceProvider(serviceProvider);
                    serviceModels.add(serviceModel);
                }
            }
        }
        model.addAttribute("serviceList", serviceModels);
        String index = "index";
        return index;
    }

    @RequestMapping(value = "/index")
    public String index(Model model) {
        return "index2";
    }

    @RequestMapping("/callService")
    @ResponseBody
    public ServiceResult callService(String serviceName, String funcName, Model model) {

        IServiceTest client = ClientBuilder.<IServiceTest>builder().zkConn("127.0.0.1:2181")
                .serviceName(serviceName)
                .serviceInterface(IServiceTest.class)
                .clientProxyClass(JDKProxy.class)
                .build();
        Class c = client.getClass();//得到对象
        try {
            Method method = c.getMethod(funcName, String.class);
            long s = System.currentTimeMillis();
            Object result = method.invoke(client, "123");
            long e = System.currentTimeMillis();
            log.info("调用服务耗时:{}", (e - s) + "ms");
            ServiceResult serviceResult = new ServiceResult();
            serviceResult.setResult(result.toString());
            serviceResult.setConsumTime(e - s);
            serviceResult.setStatus(true);
            return serviceResult;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
