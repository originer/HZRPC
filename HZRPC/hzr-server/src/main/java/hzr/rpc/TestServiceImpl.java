package hzr.rpc;

import api.TestService;
import hzr.rpc.server.RpcService;

/**
 * @author Zz
 **/
@RpcService(TestService.class)
public class TestServiceImpl implements TestService {
    @Override
    public void test() {
        System.out.println("TestServiceImpl method invoke ...");
    }
}
