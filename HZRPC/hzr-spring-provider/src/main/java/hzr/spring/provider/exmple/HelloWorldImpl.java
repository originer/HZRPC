package hzr.spring.provider.exmple;

import hzr.spring.provider.exmple.service.HelloWorld;

/**
 */
public class HelloWorldImpl implements HelloWorld {
    @Override
    public String say(String hello) {
        return "server: "+hello;
    }

    @Override
    public int sum(int a, int b) {
        return a+b;
    }

    @Override
    public int max(Integer a, Integer b) {
        return a <= b ? b : a;
    }

    public HelloWorldImpl() {
    }
}
