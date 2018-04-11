package hzradmin.hzradmin.server;

import hzr.spring.provider.anocation.RpcService;
import hzr.spring.provider.bean.ServerFactoryBean;
import hzr.spring.provider.exmple.IHello;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

@EnableAutoConfiguration
@ComponentScan(basePackages = "hzradmin.hzradmin.exmple.*")
public class SpringServerConfig implements ApplicationContextAware, InitializingBean {
    private static Map<String, Object> serviceMap = new HashMap<>();
    @Bean
    public ServerFactoryBean serverFactoryBean() {
        final ServerFactoryBean serverFactoryBean = new ServerFactoryBean();
        serverFactoryBean.setPort(9090);
        serverFactoryBean.setServiceInterface(IHello.class);
        serverFactoryBean.setZkConn("127.0.0.1:2181");
        serverFactoryBean.setServiceMap(serviceMap);

        new Thread(() -> {
            try {
                serverFactoryBean.getObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "RpcServer").start();
        return serverFactoryBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringServerConfig.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serverFactoryBean().start();
    }

    /**
     * server注册之前先扫描所有的服务
     * 标注有@RpcService的都会被存入serviceMap
     * @param ctx
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.serviceName();
                serviceMap.put(serviceName, serviceBean);
            }
        }
    }
}
