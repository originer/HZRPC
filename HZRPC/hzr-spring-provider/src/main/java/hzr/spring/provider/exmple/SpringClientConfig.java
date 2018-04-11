package hzr.spring.provider.exmple;

import hzr.spring.provider.bean.ClientFactoryBean;
import hzr.spring.provider.mode.ServiceModel;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@Controller
@SpringBootApplication
@RequestMapping("/test")
public class SpringClientConfig {
    @Bean
    public IHello rpcClient() {
        ClientFactoryBean<IHello> clientFactoryBean = new ClientFactoryBean<>();
        clientFactoryBean.setZkConn("127.0.0.1:2181");
        clientFactoryBean.setServiceName("HelloImpl");
        clientFactoryBean.setServiceInterface(IHello.class);
        return clientFactoryBean.getObject();//通过ClientBuilder获取Client实例
    }

    @Resource
    private IHello rpcClient;

    @RequestMapping("/list")
    public String getServerList(Model model) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181");
        List<String> services = zkClient.getChildren("/service");
        final List<ServiceModel> serviceModels = new ArrayList<>();
        if (!CollectionUtils.isEmpty(services)) {
            for (String serviceName : services) {
                ServiceModel serviceModel = new ServiceModel();
                serviceModel.setServiceName(serviceName);
//                List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>();
//                String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
//                List<String> addressList = zkClient.getChildren(servicePath);
//                if (!CollectionUtils.isEmpty(addressList)) {
//                    for (String address : addressList) {
//                        address = zkClient.readData(address);
//                        ServiceProvider serviceProvider = new ServiceProvider();
//                        List<String> serviceProviderPayLoadTokens = Splitter.on(":").splitToList(address);
////                        serviceProvider.setIp(serviceProviderPayLoadTokens.get(0));
////                        serviceProvider.setPort(serviceProviderPayLoadTokens.get(1));
//                        serviceProviders.add(serviceProvider);
//                    }
//                }
//                serviceModel.setServiceProviders(serviceProviders);
                serviceModels.add(serviceModel);
                model.addAttribute("service", serviceModel);
            }
        }
        model.addAttribute("services", serviceModels);
        String index = "index";
        return index;
    }

    @RequestMapping(value = "/index")
    public String index(Model  model)
    {
        ServiceModel serviceModel = new ServiceModel();
        serviceModel.setServiceName("testServiceModel");
        List<ServiceModel> serviceModelList = new ArrayList<ServiceModel>();
        serviceModelList.add(serviceModel);
        model.addAttribute("serviceModelList",serviceModelList);
        model.addAttribute("serviceModel",serviceModel);
        return "test";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(String say) {
        long s = System.currentTimeMillis();
        rpcClient.say(say);
        long e = System.currentTimeMillis();

        log.info("调用服务耗时:{}",(e-s)+"ms");

        return rpcClient.say(say);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringClientConfig.class, "--server.port=9092");
    }
}
