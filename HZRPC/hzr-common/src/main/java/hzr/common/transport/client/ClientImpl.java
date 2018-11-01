package hzr.common.transport.client;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import hzr.common.disruptor.MessageConsumer;
import hzr.common.disruptor.RingBufferWorkerPoolFactory;
import hzr.common.protocol.Request;
import hzr.common.protocol.Response;
import hzr.common.proxy.CGLIBProxy;
import hzr.common.proxy.RPCProxy;
import hzr.common.transport.ChannelHolder;
import hzr.common.transport.server.MessageConsumerImpl4Server;
import hzr.common.util.Constants;
import hzr.common.util.ResponseMapCache;
import hzr.register.impl.ZooKeeperServiceDiscovery;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
@Slf4j
public class ClientImpl implements Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientImpl.class);
    //用于生成请求序列号
    private static AtomicLong atomicLong = new AtomicLong();
    // 通过此发布的服务名称,来寻找对应的服务提供者
    private String serviceName;
    // 响应超时时间
    private int requestTimeoutMillis = 100;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
    private String zkConn;
    //select channel 的策略，默认采用顺序选取
    private int STRATEGY = Constants.SELECT_IN_ORDER;
    private String serviceAddress;
    // 代理方式
    private Class<? extends RPCProxy> clientProxyClass;
    private RPCProxy RPCProxy;
    // 缓存
    // 存放ChannelHolder到一个CopyOnWriteArrayList中，这个本就是读多写少的场景(服务注册后很少会发生状态改变)，所以很适用
    // CopyOnWriteArrayList容器即写时复制的容器，可以做到读写分离，在高并发的情况下不需要上锁
    private static CopyOnWriteArrayList<ChannelHolder> channelCachePool = new CopyOnWriteArrayList<>();

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

        MessageConsumer[] conusmers = new MessageConsumer[64];
        for (int i = 0; i < conusmers.length; i++) {
            MessageConsumer messageConsumer = new MessageConsumerImpl4Client("code:clientId:" + i);
            conusmers[i] = messageConsumer;
        }
        RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
                1024 * 1024,
                new YieldingWaitStrategy(),
                //new BlockingWaitStrategy(),
                conusmers);
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
        //添加服务选择策略
        int size = channelCachePool.size();
        log.info("channel连接池中的连接数量：{}", size);
        List<ChannelHolder> channelHolderList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (channelCachePool.get(i).getConnStr().equals(conn)) {
                if (STRATEGY == Constants.SELECT_IN_ORDER) {
                    return channelCachePool.get(i);
                } else if (STRATEGY == Constants.RANDOMLY_SELECTED) {
                    channelHolderList.add(channelCachePool.get(i));
                }
            }
        }
        log.info("可用连接数量：{}", channelHolderList.size());

        Random random = new Random();
        int rIndex = random.nextInt(channelHolderList.size());
        return channelHolderList.get(rIndex);
    }


    public void setClientProxyClass(Class<? extends hzr.common.proxy.RPCProxy> clientProxyClass) {
        this.clientProxyClass = clientProxyClass;
    }

    @Override
    public Response invokeMethod(Class<?> clazz, Method method, Object[] args) throws Exception {
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

        log.info("获取可用的Channel connectStr:{}", channelHolder.getConnStr());
        if (channelHolder.getChannelObjectPool().getNumIdle() < 0) {
            Response response = new Response();
            RuntimeException runtimeException = new RuntimeException("Channel is not active now");
            response.setThrowable(runtimeException);
            return response;
        }

        Channel channel = channelHolder.getChannelObjectPool().borrowObject();

        if (channel == null) {
            Response response = new Response();
            RuntimeException runtimeException = new RuntimeException("Channel is not available now");
            response.setThrowable(runtimeException);
            return response;
        }
        try {
            //TODO 把Queue替换成disrutor框架
            //这里要先对每个请求申请blockingQueue，否则高并发环境下RpcClientHandler可能获取不到response
            BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue<>(1);
            ResponseMapCache.responseMap.put(request.getRequestId(), blockingQueue);
            channel.writeAndFlush(request);

            // 建立一个ResponseMap，将RequestId作为键，服务端回应的内容作为值保存于BlockingQueue，
            // 最后一起保存在这个ResponseMap中

            //poll(time):取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,取不到时返回null
            return blockingQueue.poll(requestTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("service" + serviceName + " method " + method + " timeout");
        } finally {
            channelHolder.getChannelObjectPool().returnObject(channel);
            //删除此键值对，help GC
            ResponseMapCache.responseMap.remove(request.getRequestId());
        }
    }

    @Override
    public <T> T proxyInterface(Class<T> serviceInterface) {
        //默认使用cglib
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

    public int getSTRATEGY() {
        return STRATEGY;
    }

    public void setSTRATEGY(int STRATEGY) {
        this.STRATEGY = STRATEGY;
    }

}