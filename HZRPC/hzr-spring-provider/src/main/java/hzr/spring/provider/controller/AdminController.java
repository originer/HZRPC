package hzr.spring.provider.controller;

import com.google.common.base.Splitter;
import hzr.register.Constant;
import hzr.spring.provider.mode.ServiceModel;
import hzr.spring.provider.mode.ServiceProvider;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zz
 **/
@RequestMapping("/admin")
@Controller
public class AdminController {

    private ZkClient zkClient;
    @PostConstruct
    public void init() {
        //通过curatorFramework [zk的抽象API接口] 来连接zk
        ZkClient zkClient = new ZkClient("127.0.0.1:2181");

    }

    @RequestMapping("/list")
    public String getServerList(Model model) {
        List<String> services = zkClient.getChildren("/service");
        final List<ServiceModel> serviceModels = new ArrayList<ServiceModel>();
        if (!CollectionUtils.isEmpty(services)) {
            for (String serviceName : services) {
                ServiceModel serviceModel = new ServiceModel();
                serviceModel.setServiceName(serviceName);
                List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>();
                String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
                List<String> addressList = zkClient.getChildren(servicePath);
                if (!CollectionUtils.isEmpty(addressList)) {
                    for (String serverPayLoad : addressList) {
                        ServiceProvider serviceProvider = new ServiceProvider();
                        List<String> serviceProviderPayLoadTokens = Splitter.on(":").splitToList(serverPayLoad);
//                        serviceProvider.setIp(serviceProviderPayLoadTokens.get(0));
//                        serviceProvider.setPort(serviceProviderPayLoadTokens.get(1));
                        serviceProviders.add(serviceProvider);
                    }
                }
                serviceModel.setServiceProviders(serviceProviders);
                serviceModels.add(serviceModel);
            }
        }
        model.addAttribute("services", serviceModels);
        return "index2";
    }
}
