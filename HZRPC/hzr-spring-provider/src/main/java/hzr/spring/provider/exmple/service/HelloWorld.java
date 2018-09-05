package hzr.spring.provider.exmple.service;

import hzr.spring.provider.anocation.RpcService;

@RpcService(value = HelloWorld.class,serviceName = "HelloWorld")
public interface HelloWorld {
    String say(String hello);

    int sum(int a, int b);
    int max(Integer a, Integer b);
}
