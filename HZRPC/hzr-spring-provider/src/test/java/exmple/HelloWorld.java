package exmple;

import hzr.spring.provider.anocation.RpcService;

@RpcService(HelloWorld.class)
public interface HelloWorld {
    String say(String hello);

    int sum(int a, int b);
    int max(Integer a, Integer b);
}
