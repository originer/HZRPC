package exmple;

import hzr.common.bootstrap.ClientBuilder;
import hzr.spring.provider.exmple.service.IServiceTest;
import org.junit.Test;

/**
 * @author Zz
 **/
public class TestSpringBoot {
    @Test
    public void testSpringServer() {
        IServiceTest hello = ClientBuilder.<IServiceTest>builder().zkConn("127.0.0.1:2181")
                .serviceName("TestImpl")
                .serviceInterface(IServiceTest.class).build();
        String result = hello.say("341");
        System.out.println(result);
    }
}
