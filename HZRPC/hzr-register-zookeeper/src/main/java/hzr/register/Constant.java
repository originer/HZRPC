package hzr.register;

/**
 *  ZooKeeper配置
 */
public interface Constant {

    int ZK_SESSION_TIMEOUT = 50000;
    int ZK_CONNECTION_TIMEOUT = 100000;

    String ZK_REGISTRY_PATH = "/registry";
}