package hzr.common.bootstrap;

import com.google.common.base.Preconditions;
import hzr.common.proxy.CGLIBProxy;
import hzr.common.proxy.RPCProxy;
import hzr.common.transport.client.ClientImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * Client bootstrap
 *
 * @author Zz
 **/
@Slf4j
public class ClientBuilder<T> {
    private String serviceName;
    private String zkConn;
    private Class<T> serviceInterface;
    private int requestTimeoutMillis = 10000;
    private Class<? extends RPCProxy> clientProxyClass = CGLIBProxy.class;
    private int STRATEGY;

    public static <T> ClientBuilder<T> builder() {
        return new ClientBuilder<>();
    }

    public ClientBuilder<T> serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public ClientBuilder<T> zkConn(String zkConn) {
        this.zkConn = zkConn;
        return this;
    }

    public ClientBuilder<T> serviceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
        return this;
    }

    public ClientBuilder<T> requestTimeout(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
        return this;
    }

    public ClientBuilder<T> clientProxyClass(Class<? extends RPCProxy> clientProxyClass) {
        this.clientProxyClass = clientProxyClass;
        return this;
    }

    public ClientBuilder<T> strategy(int STRATEGY) {
        this.STRATEGY = STRATEGY;
        return this;
    }

    public T build() {
        Preconditions.checkNotNull(serviceInterface);
        Preconditions.checkNotNull(zkConn);
        Preconditions.checkNotNull(serviceName);

        ClientImpl client = new ClientImpl(this.serviceName);
        client.setZkConn(this.zkConn);
        client.setRequestTimeoutMillis(this.requestTimeoutMillis);
        client.setClientProxyClass(clientProxyClass);
        client.setSTRATEGY(STRATEGY);
        client.init();
        log.info("客户端创建成功");
        return client.proxyInterface(this.serviceInterface);
    }
}
