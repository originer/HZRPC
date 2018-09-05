package hzr.spring.provider.xmlboot;

import hzr.common.transport.server.Server;
import hzr.spring.provider.bean.ServerBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Zz
 **/
public class ServerBoot {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-server.xml");
        ServerBean serverBuilder = (ServerBean) ctx.getBean("serverBean");

        Server server = serverBuilder.getServer();
        server.start();
    }
}



