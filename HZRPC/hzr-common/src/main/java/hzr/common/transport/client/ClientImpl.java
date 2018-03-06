package hzr.common.transport.client;

import com.google.common.base.Splitter;
import hzr.common.message.Request;
import hzr.common.message.Response;
import hzr.common.proxy.CglibRpcProxy;
import hzr.common.proxy.RpcProxy;
import hzr.common.transport.ChannelHolder;
import hzr.common.util.ResponseMapCache;
import hzr.register.impl.ZooKeeperServiceDiscovery;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 对接口方法调用进行request包装
 * 根据相应服务来得到channel包装
 * 根据channel = channelHolder.getChannelObjectPool().borrowObject();得到channel
 * 将请求写入channel并刷出去channel.writeAndFlush(request);
 */
public class ClientImpl implements Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientImpl.class);
    //用于生成请求序列号
    private static AtomicLong atomicLong = new AtomicLong();
    // 通过此发布的服务名称,来寻找对应的服务提供者
    private String serviceName;
    private int requestTimeoutMillis = 10 * 1000;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
    private String zkConn;
    //    private CuratorFramework curatorFramework;
    private ZooKeeperServiceDiscovery zkDiscover;
    private String serviceAddress;
    private String serviceVersion;
    //TODO proxy实现方式 后续可以改成工厂模式，有默认实现
    private Class<? extends RpcProxy> clientProxyClass;
    private RpcProxy rpcProxy;
    // 缓存
    // 存放ChannelConf到一个CopyOnWriteArrayList中，这个本就是读多写少的场景(服务注册后很少会发生状态改变)，所以很适用
    // CopyOnWriteArrayList容器即写时复制的容器，可以做到读写分离，在高并发的情况下不需要上锁
    public static CopyOnWriteArrayList<ChannelHolder> channelCachePool = new CopyOnWriteArrayList<>();

    public ClientImpl(String serviceName) {
        this.serviceName = serviceName;
    }

    public void init() {

        zkDiscover = new ZooKeeperServiceDiscovery(getZkConn());
        if (zkDiscover != null) {
            //TODO 此处可以添加verision做负载均衡
//            if (StringUtils.isNotEmpty(serviceName)) {
//                serviceName += "-" + serviceVersion;
//            }
            serviceAddress = zkDiscover.discover(serviceName);
            LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
        }

//        String[] array = StringUtils.split(serviceAddress, ":");
//        String host = array[0];
//        int port = Integer.parseInt(array[1]);
        // 关闭删除本地缓存中多出的channel
        for (ChannelHolder cw : channelCachePool) {
            String serviceAddress = cw.getConnStr();
            if (!serviceAddress.contains(serviceAddress)) {
                cw.close();
                LOGGER.info("Remove channel {},{}", serviceAddress,cw);
                channelCachePool.remove(cw);
            }
        }
        // 增加本地缓存中不存在的连接地址
        boolean containThis = false;
        for (ChannelHolder cw : channelCachePool) {
            if (serviceAddress != null && serviceAddress.equals(cw.getConnStr())) {
                containThis = true;
            }
        }
        if (!containThis) {
            addNewChannel(serviceAddress);
        }
    }

    private void addNewChannel(String serviceAddress) {
        try {
            String[] array = StringUtils.split(serviceAddress, ":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            
            ChannelHolder channelHolder = new ChannelHolder(host, port);
            channelCachePool.add(channelHolder);
            LOGGER.info("Add New Channel {},{}", serviceAddress,channelHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ChannelHolder selectChannel() {
        Random random = new Random();
        //同一个服务下有好几个链接地址的实现，那就选一个就是，其实为集群部署考虑，
        // 每一台服务器部署有相同的服务，选择其一来处理即可，假如是nginx代理那就无所谓了
        int size = channelCachePool.size();
        if (size < 1) {
            return null;
        }
        int i = random.nextInt(size);
        return channelCachePool.get(i);
    }

    @Override
    public Response sendMessage(Class<?> clazz, Method method, Object[] args) {
        //编写 request 信息
        Request request = new Request();
        request.setRequestId(atomicLong.incrementAndGet());
        request.setMethod(method.getName());
        request.setParams(args);
        request.setClazz(clazz);
        request.setParameterTypes(method.getParameterTypes());
        request.setServiceName(serviceName);

        ChannelHolder channelHolder = selectChannel();
        if (channelHolder == null) {
            Response response = new Response();
            RuntimeException runtimeException = new RuntimeException("Channel is not active now");
            response.setThrowable(runtimeException);
            return response;
        }
        //当channel的配置链接不为空的时候，就可以取到channel了
        Channel channel = null;
        try {
            channel = channelHolder.getChannelObjectPool().borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (channel == null) {
            Response response = new Response();
            RuntimeException runtimeException = new RuntimeException("Channel is not available now");
            response.setThrowable(runtimeException);
            return response;
        }
        try {
            //TODO  把request信息发送给服务器
            channel.writeAndFlush(request);
            //建立一个ResponseMap，将RequestId作为键，服务端回应的内容作为值保存于BlockingQueue，
            // 最后一起保存在这个ResponseMap中
            BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue<>(1);
            ResponseMapCache.responseMap.put(request.getRequestId(), blockingQueue);
            //poll(time):取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,取不到时返回null
            return blockingQueue.poll(requestTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            //这个异常是自定义的，只是为了说明字面意思
            throw new RuntimeException("service" + serviceName + " method " + method + " timeout");
        } finally {
            try {
                //拿出去的channel记得还回去
                channelHolder.getChannelObjectPool().returnObject(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //删除此键值对，help GC
            ResponseMapCache.responseMap.remove(request.getRequestId());
        }
    }

    @Override
    public <T> T proxyInterface(Class<T> serviceInterface) {
//        默认使用cglib
        if (clientProxyClass == null) {
            clientProxyClass = CglibRpcProxy.class;
        }
        try {
            rpcProxy = clientProxyClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return rpcProxy.proxyInterface(this, serviceInterface);
    }

    @Override
    public void close() {
        //注意要关三处地方，一个是先关闭zookeeper的连接，另一个是channel池对象，最后是netty的断开关闭
//        if (curatorFramework != null) {
//            curatorFramework.close();
//        }
        try {
            for (ChannelHolder cw : channelCachePool) {
                cw.close();
            }
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public String getZkConn() {
        return zkConn;
    }

    public void setZkConn(String zkConn) {
        this.zkConn = zkConn;
    }

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }


}