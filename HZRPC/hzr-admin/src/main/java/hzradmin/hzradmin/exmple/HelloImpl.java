package hzradmin.hzradmin.exmple;

import hzr.spring.provider.anocation.RpcService;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
@RpcService(value = IHello.class,serviceName = "HelloImpl")
public class HelloImpl implements IHello {
	public String say(String hello) {
		return "return " + hello;
	}

	public int sum(int a, int b) {
		return a + b;
	}

	public int sum(Integer a, Integer b) {
		return a + b * 3;
	}

}
