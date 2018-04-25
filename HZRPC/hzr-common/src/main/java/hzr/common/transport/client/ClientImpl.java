package hzr.common.transport.client;

import hzr.common.protocol.Request;
import hzr.common.protocol.Response;
import hzr.common.proxy.CGLIBProxy;
import hzr.common.proxy.RPCProxy;
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
    // 响应超时时间
    private int requestTimeoutMillis = 10000;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
    private String zkConn;

    private String serviceAddress;
    // 代理方式
    private Class<? extends RPCProxy> clientProxyClass;
    private RPCProxy RPCProxy;
    // 缓存
    // 存放ChannelHolder到一个CopyOnWriteArrayList中，这个本就是读多写少的场景(服务注册后很少会发生状态改变)，所以很适用
    // CopyOnWriteArrayList容器即写时复制的容器，可以做到读写分离，在高并发的情况下不需要上锁
    public static CopyOnWriteArrayList<ChannelHolder> channelCachePool = new CopyOnWriteArrayList<>();

    public ClientImpl(String serviceName) {
        this.serviceName = serviceName;
    }

    public void init() {
        ZooKeeperServiceDiscovery zkDiscover = new ZooKeeperServiceDiscovery(getZkConn());
            //TODO 此处可以添加verision做负载均衡
        serviceAddress = zkDiscover.discover(serviceName);
        LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);

        // 增加本地缓存中不存在的连接地址
        // 采用延迟加载策略，当一个服务第一次被调用时，会创建一个Channel并保存到 channelCachePool
        boolean containThis = false;
        for (ChannelHolder cw : channelCachePool) {
            if (serviceAddress != null && serviceAddress.equals(cw.getConnStr())) {
                containThis = true;
            }
        }
        if (!containThis) {
            addNewChannel(serviceName, serviceAddress);
        }
    }

    private void addNewChannel(String serviceName, String serviceAddress) {
        try {
            String[] array = StringUtils.split(serviceAddress, ":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelHolder channelHolder = new ChannelHolder(serviceName, host, port);
            channelCachePool.add(channelHolder);
            LOGGER.info("Add New Channel {}", serviceAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ChannelHolder selectChannel(String conn) {
//        TODO 这里的工作交由ZooKeeper处理
        Random random = new Random();
        //同一个服务下有好几个链接地址的实现，那就选一个就是，其实为集群部署考虑，
        int size = channelCachePool.size();
        for (int i = 0; i < size; i++) {
            if (channelCachePool.get(i).getConnStr().equals(conn)) {
                return channelCachePool.get(i);
            }
        }
        return null;
    }


    public void setClientProxyClass(Class<? extends hzr.common.proxy.RPCProxy> clientProxyClass) {
        this.clientProxyClass = clientProxyClass;
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
        //从CopyOnWriteArrayList容器中根据服务名获取可用的channel连接
        ChannelHolder channelHolder = selectChannel(serviceAddress);
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
            //这里要先对每个请求申请blockingQueue，否则高并发环境下RpcClientHandler可能获取不blockingQueue
            BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue<>(1);
            ResponseMapCache.responseMap.put(request.getRequestId(), blockingQueue);
            channel.writeAndFlush(request);

            // 建立一个ResponseMap，将RequestId作为键，服务端回应的内容作为值保存于BlockingQueue，
            // 最后一起保存在这个ResponseMap中

            //poll(time):取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,取不到时返回null

            Response response = blockingQueue.poll(requestTimeoutMillis, TimeUnit.MILLISECONDS);
            return response;
        } catch (InterruptedException e) {
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


//    public RPCFuture sendMessageByAnsc(Class<?> clazz, Method method, Object[] args) {
//
//    }

    @Override
    public <T> T proxyInterface(Class<T> serviceInterface) {
//        默认使用cglib
        if (clientProxyClass == null) {
            clientProxyClass = CGLIBProxy.class;
        }
        try {
            RPCProxy = clientProxyClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return RPCProxy.proxyInterface(this, serviceInterface);
    }

    @Override
    public void close() {
        //注意要关三处地方，一个是先关闭zookeeper的连接，另一个是channel池对象，最后是netty的断开关闭

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