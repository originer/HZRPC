package hzr.common.bootstrap;

import com.google.common.base.Preconditions;
import hzr.common.transport.server.Server;
import hzr.common.transport.server.ServerImpl;

import java.util.Map;

/**
 * Server bootstrap
 * @author Zz
 **/
public class ServerBuilder {
    private int port;
    private String serviceName;
    private Object serviceImpl;
    private String zkConn;
    private Map<String,Object> serviceMap;
    private ServerBuilder() {}

    public static ServerBuilder builder() {
        return new ServerBuilder();
    }
    public ServerBuilder port(int port) {
        this.port = port;
        return this;
    }
    public ServerBuilder serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    public ServerBuilder serviceImpl(Object serviceImpl) {
        this.serviceImpl = serviceImpl;
        return this;
    }
    public ServerBuilder zkConn(String zkConn) {
        this.zkConn = zkConn;
        return this;
    }
    public ServerBuilder serviceMap(Map<String,Object> serviceMap) {
        this.serviceMap = serviceMap;
        return this;
    }


    public Server build() {
        Preconditions.checkNotNull(serviceImpl);
        Preconditions.checkNotNull(serviceName);
        Preconditions.checkNotNull(zkConn);
        Preconditions.checkArgument(port > 0);
        return new ServerImpl(this.port, this.serviceImpl, this.serviceName, this.zkConn);
    }

    public Server build2() {
        Preconditions.checkNotNull(serviceMap);
        Preconditions.checkNotNull(zkConn);
        Preconditions.checkArgument(port > 0);
        return new ServerImpl(this.port, this.serviceMap, this.zkConn);
    }

}
