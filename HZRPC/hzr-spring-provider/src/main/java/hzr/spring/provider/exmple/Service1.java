package hzr.spring.provider.exmple;

import hzr.spring.provider.anocation.RpcService;
import hzr.spring.provider.exmple.service.IServiceTest;

/**
 * Description:
 */
@RpcService(value = IServiceTest.class,serviceName = "Service1")
public class Service1 implements IServiceTest {
	public String say(String hello) {
		return "call service1 success!";
	}

	public int sum(int a, int b) {
		return a + b;
	}

	public int sum(Integer a, Integer b) {
		return a + b * 3;
	}

}
