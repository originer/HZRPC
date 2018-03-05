package hzr.spring.provider.bean;

import hzr.common.bootstrap.ServerBuilder;
import hzr.common.transport.server.Server;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author Zz
 **/
@Data
@Slf4j
public class ServerFactoryBean<T> implements FactoryBean<T> {

    private Class<?> serviceInterface;
//    private Object serviceImpl;
//    private String ip
//    private String serviceName;

    private Map<String, Object> serviceMap;
    private int port;
    private String zkConn;
    private Server rpcServer;

    //服务注册并提供
    public void start() {
        rpcServer = ServerBuilder
                .builder()
                .port(port)
                .zkConn(zkConn)
                .serviceMap(serviceMap)
                .build();
        rpcServer.start();
    }


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
