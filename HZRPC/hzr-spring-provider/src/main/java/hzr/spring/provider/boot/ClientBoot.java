package hzr.spring.provider.boot;

import hzr.spring.provider.bean.ClientBean;
import hzr.spring.provider.exmple.HelloWorld;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Zz
 **/
public class ClientBoot {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-client.xml");
        ClientBean clientBean = (ClientBean) ctx.getBean("clientBean");
        HelloWorld h = (HelloWorld) clientBean.create();
        System.out.println(h.say("123"));
    }
}
