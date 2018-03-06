package hzr.common.transport;

import hzr.common.pool.ConnectionObjectFactory;
import io.netty.channel.Channel;
import lombok.Data;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

@Data
public class ChannelHolder {
    private String connStr;
    private String host;
    private int ip;
    private Channel channel;
    private ObjectPool<Channel> channelObjectPool;
    public ChannelHolder(String host, int port) {
        this.host = host;
        this.ip = port;
        this.connStr = host + ":" + ip;
        channelObjectPool = new GenericObjectPool<>(new ConnectionObjectFactory(host, port));
    }
    public void close() {
        channelObjectPool.close();
    }

}