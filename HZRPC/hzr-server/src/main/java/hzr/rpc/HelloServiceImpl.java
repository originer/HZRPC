package hzr.rpc;

import api.HelloService;
import hzr.rpc.server.RpcService;

/**
 * @author Zz
 **/
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }
}
