package hzr.spring.provider.bean;

import hzr.common.bootstrap.ClientBuilder;

/**
 * @author Zz
 **/
public class ClientBean<T> {
    private Class<T> serviceInterface;
    private String serviceName;
    private String zkConn;

    public ClientBean(Class<T> serviceInterface, String serviceName, String zkConn) {
        this.serviceInterface = serviceInterface;
        this.serviceName = serviceName;
        this.zkConn = zkConn;
    }

    public T create() {
        return (T) ClientBuilder.builder().serviceName(this.serviceName)
                .serviceInterface((Class<Object>) serviceInterface)
                .zkConn(this.zkConn)
                .build();
    }
}
