package hzr.spring.provider.exmple;

import hzr.spring.provider.anocation.RpcService;
import hzr.spring.provider.exmple.service.IServiceTest;

/**
 * @author Zz
 **/
@RpcService(value = IServiceTest.class,serviceName = "服务3")
public class Service2 implements IServiceTest {
    public String say(String hello) {
        return "call 服务3 success!";
    }

    public int sum(int a, int b) {
        return a + b;
    }

    @Override
    public int sum(Integer a, Integer b) {
        return 0;
    }

}
