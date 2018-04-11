package hzradmin.hzradmin.exmple;

import hzr.spring.provider.bean.ClientFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Configuration
@RestController
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

    @RequestMapping("/hello")
    public String hello(String say) {
        return rpcClient.say(say);
    }
    public static void main(String[] args) {
        SpringApplication.run(SpringClientConfig.class, "--server.port=9091");
    }
}
