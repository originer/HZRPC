package hzr.register.impl;

import hzr.register.Constant;
import hzr.register.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

/**
 * @author Zz
 **/
@Slf4j
public class ZooKeeperServiceRegistry implements ServiceRegistry {

    private final ZkClient zkClient;

    public ZooKeeperServiceRegistry(String zkAddress) {
        // 创建 ZooKeeper 客户端
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        log.info("connect zookeeper");
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        String registryPath = Constant.ZK_REGISTRY_PATH;
        //创建 registry 结点并持久化
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            log.info("create registry node: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            log.info("create service node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        log.info("create address node: {}", addressNode);
    }
}
