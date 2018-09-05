package hzr.spring.provider.bean;

import hzr.common.bootstrap.ServerBuilder;
import hzr.common.transport.server.Server;
import hzr.common.transport.server.ServerImpl;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Zz
 **/
@Data
public class ServerFactoryBean implements FactoryBean<Object> {

    private Class<?> serviceInterface;
    private Object serviceImpl;
    private int port;
    private String zkConn;
    private ServerImpl rpcServer;

    private Map<String,Object> serviceMap;

    public void start() {
        Server build = ServerBuilder.builder().zkConn(zkConn)
                .serviceMap(serviceMap)
                .port(port)
                .zkConn(zkConn).build2();
        build.start();
    }

    public void destroy() {
        rpcServer.shutdown();
    }

    @Nullable
    @Override
    public Object getObject() throws Exception {
        return this;
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