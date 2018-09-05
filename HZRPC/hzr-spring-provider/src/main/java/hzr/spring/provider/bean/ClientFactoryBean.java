package hzr.spring.provider.bean;

import hzr.common.bootstrap.ClientBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

/**
 * @author Zz
 **/
@Slf4j
@Data
public class ClientFactoryBean<T> implements FactoryBean<T> {
    private Class<T> serviceInterface;
    private String serviceName;
    private String zkConn;

    public T getObject() {
        return ClientBuilder.<T>builder().zkConn(zkConn)
                .serviceName(serviceName)
                .serviceInterface(serviceInterface)
                .build();
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
