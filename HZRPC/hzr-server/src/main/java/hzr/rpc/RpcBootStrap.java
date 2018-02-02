package hzr.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Zz
 **/
public class RpcBootStrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
