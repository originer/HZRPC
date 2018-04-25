package hzradmin.hzradmin.client;

import hzr.spring.provider.bean.ClientFactoryBean;
import hzr.spring.provider.exmple.IServiceTest;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Configuration
@RestController
@RequestMapping("/test")
@ComponentScan(basePackages = "hzr.spring.provider.*")

public class SpringClientConfig {
    @Bean
    public IServiceTest rpcClient() {
        ClientFactoryBean<IServiceTest> clientFactoryBean = new ClientFactoryBean<>();
        clientFactoryBean.setZkConn("127.0.0.1:2181");
        clientFactoryBean.setServiceName("HelloImpl");
        clientFactoryBean.setServiceInterface(IServiceTest.class);
        return clientFactoryBean.getObject();//通过ClientBuilder获取Client实例
    }

    @Resource
    private IServiceTest rpcClient;

    @RequestMapping("/hello")
    public String hello(String say) {
        return rpcClient.say("123");
    }
    public static void main(String[] args) {
        SpringApplication.run(SpringClientConfig.class, "--server.port=9091");
    }
}
