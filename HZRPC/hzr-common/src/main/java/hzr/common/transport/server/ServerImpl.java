package hzr.common.transport.server;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import hzr.common.codec.V2.RPCDecoder;
import hzr.common.codec.V2.RPCEncoder;
import hzr.common.disruptor.MessageConsumer;
import hzr.common.disruptor.RingBufferWorkerPoolFactory;
import hzr.common.protocol.Request;
import hzr.common.protocol.Response;
import hzr.common.transport.RpcServerHandler;
import hzr.common.util.NetUtils;
import hzr.register.ServiceRegistry;
import hzr.register.impl.ZooKeeperServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ServerImpl implements Server {

    private int port;

    /**
     * 单服务注册
     */
    private Object serviceImpl;
    private String serviceName;

    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private Map<String, Object> serviceMap = new HashMap<>();

    private String zkConn;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * 注册单个服务
     *
     * @param port
     * @param serviceImpl
     * @param serviceName
     * @param zkConn
     */
    public ServerImpl(int port, Object serviceImpl, String serviceName, String zkConn) {
        this.port = port;
        this.serviceImpl = serviceImpl;
        this.serviceName = serviceName;
        this.zkConn = zkConn;
    }

    /**
     * 注册多个服务
     *
     * @param port
     * @param serviceMap
     * @param zkConn
     */
    public ServerImpl(int port, Map<String, Object> serviceMap, String zkConn) {
        this.port = port;
        this.serviceMap = serviceMap;
        this.zkConn = zkConn;
    }

    @Override
    public void start() {

        MessageConsumer[] consumers = new MessageConsumer[64];
        for (int i = 0; i < consumers.length; i++) {
            MessageConsumer messageConsumer = new MessageConsumerImpl4Server("code:serverId:" + i);
            consumers[i] = messageConsumer;
        }

        RingBufferWorkerPoolFactory.getInstance("server").initAndStart(ProducerType.MULTI,
                1024 * 1024,
                new YieldingWaitStrategy(),
                //new BlockingWaitStrategy(),
                consumers);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new RPCDecoder(Request.class))
                                .addLast(new RpcServerHandler(serviceMap))
                                .addLast(new RPCEncoder(Response.class));
                    }
                });
        try {
            //调用bind等待客户端来连接
            ChannelFuture future = serverBootstrap.bind(port).sync();
            //接着注册服务
            registerService();
            log.info("Server Started At {}", port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册服务
     */
    private void registerService() {
        ServiceRegistry serviceRegistry = new ZooKeeperServiceRegistry(getZkConn());
        String serviceAddress = NetUtils.getLocalIp() + ":" + port;

        if (StringUtils.isNotEmpty(serviceName)) {
            serviceRegistry.register(serviceName, serviceAddress);
        } else if (serviceMap.size() > 0) {
            serviceMap.forEach((k, v) -> {
                serviceRegistry.register(k, serviceAddress);
                log.info("注册服务 serviceName: {}", k);
            });
        }
    }

    @Override
    public void shutdown() {
        //关停相关服务
        log.info("Shutting down server {}", serviceName);
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private void unRegister() {
        log.info("unRegister zookeeper");
        //try {
        //    curatorFramework.delete().forPath(ZK_DATA_PATH+serviceName+"/"+localIp+":"+port);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    public String getZkConn() {
        return zkConn;
    }

    public void setZkConn(String zkConn) {
        this.zkConn = zkConn;
    }

}