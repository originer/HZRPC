package hzr.rpc;

import api.HelloService;
import api.TestService;
import hzr.rpc.client.RpcProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloClient {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-client.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        System.out.println("HelloClient");

        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello("World");
        System.out.println(result);

        TestService t = rpcProxy.create(TestService.class);
        t.test();

        System.exit(0);
    }
}
