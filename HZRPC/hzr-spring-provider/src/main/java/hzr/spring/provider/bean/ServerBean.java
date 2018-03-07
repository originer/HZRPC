package hzr.spring.provider.bean;

import hzr.common.bootstrap.ServerBuilder;
import hzr.common.transport.server.Server;
import lombok.Data;

import java.util.Map;

/**
 * @author Zz
 **/
@Data
public class ServerBean {
    private int port;
    private String zkConn;
    private Map<String,Object> serviceMap;

    public ServerBean(int port, String zkConn, Map<String, Object> serviceMap) {
        this.port = port;
        this.zkConn = zkConn;
        this.serviceMap = serviceMap;
    }

    public Server getServer() {
        return ServerBuilder.builder()
                .serviceMap(this.serviceMap)
                .port(this.port)
                .zkConn(this.zkConn)
                .build2();

    }
}
