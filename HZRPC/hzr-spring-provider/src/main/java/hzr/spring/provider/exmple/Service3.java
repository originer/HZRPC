package hzr.spring.provider.exmple;

import hzr.spring.provider.anocation.RpcService;
import hzr.spring.provider.exmple.service.IServiceTest;

/**
 * @author Zz
 **/
@RpcService(value = IServiceTest.class,serviceName = "Service2")
public class Service3 implements IServiceTest {
    public String say(String hello) {
        return "call service2 success!";
    }

    public int sum(int a, int b) {
        return a + b;
    }

    @Override
    public int sum(Integer a, Integer b) {
        return 0;
    }

}
