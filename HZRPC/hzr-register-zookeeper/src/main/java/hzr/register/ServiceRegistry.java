package hzr.register;

public interface ServiceRegistry {

    /**
     * 注册服务
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     */
    void register(String serviceName, String serviceAddress);

    /**
     * 注销服务
     */
    void unRegister();
}