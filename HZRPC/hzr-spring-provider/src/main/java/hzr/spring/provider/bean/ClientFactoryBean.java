package hzr.spring.provider.bean;

import hzr.common.proxy.RpcProxy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

/**
 * @author Zz
 **/
@Data
@Slf4j
public class ClientFactoryBean<T> implements FactoryBean<T> {

    private Class<T> serviceInterface;
    private String serviceName;
    private String zkConn;

//    private Class<? extends RpcProxy> clientProxyClass;

    @Nullable
    @Override
    public T getObject() throws Exception {
        return (T) this;
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return this.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
