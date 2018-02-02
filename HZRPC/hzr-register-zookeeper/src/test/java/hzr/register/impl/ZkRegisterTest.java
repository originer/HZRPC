package hzr.register.impl;

import hzr.register.ServiceDiscovery;
import hzr.register.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试服务注册发现中心功能
 * @author Zz
 **/
@Slf4j
public class ZkRegisterTest {
    public static void main(String[] args) {
        ServiceRegistry registry = new ZooKeeperServiceRegistry("127.0.0.1:2181");
        registry.register("test","127.0.0.1:9999");
        ServiceDiscovery discovery = new ZooKeeperServiceDiscovery("127.0.0.1:2181");
        discovery.discover("test");
    }

}
