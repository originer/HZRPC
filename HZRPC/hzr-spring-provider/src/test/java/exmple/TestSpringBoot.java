package exmple;

import hzr.common.bootstrap.ClientBuilder;
import hzr.spring.provider.exmple.IHello;
import org.junit.Test;

/**
 * @author Zz
 **/
public class TestSpringBoot {
    @Test
    public void testSpringServer() {
        IHello hello = ClientBuilder.<IHello>builder().zkConn("127.0.0.1:2181")
                .serviceName("TestImpl")
                .serviceInterface(IHello.class).build();
        String result = hello.say("test1");
        System.out.println(result);
    }
}
