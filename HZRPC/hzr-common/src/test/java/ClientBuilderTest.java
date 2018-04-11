
import api.TestImpl;
import api.ITest;
import api.TestImpl2;
import hzr.common.bootstrap.ClientBuilder;
import hzr.common.bootstrap.ServerBuilder;
import hzr.common.proxy.CGLIBProxy;
import hzr.common.proxy.JDKProxy;
import hzr.common.proxy.RPCProxy;
import hzr.common.transport.server.Server;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 */
@Slf4j
public class ClientBuilderTest {

    /**
     * 记录一个问题，当启动多个Server，而且每个Server提供的服务不完全相同时
     * 由于Client调用时，会随机取一个Channel连接Server，所以就没法保证连接的是哪一个Server
     * 可能会出现找不到服务的BUG
     * @throws Exception
     */
    @BeforeClass
    public static void before() throws Exception {
        Map<String, Object> serviceMap1 = new HashMap<>();
        Map<String, Object> serviceMap2 = new HashMap<>();
        Map<String, Object> serviceMap3 = new HashMap<>();
        serviceMap1.put("s1",new TestImpl());
//        serviceMap1.put("s2",new TestImpl());
//        serviceMap1.put("s3",new TestImpl());
//        serviceMap1.put("s4",new TestImpl());

        serviceMap2.put("s1",new TestImpl());
        serviceMap2.put("s2",new TestImpl());
//        serviceMap2.put("s3",new TestImpl());
        serviceMap3.put("s3",new TestImpl2());

        Server testBuilder = ServerBuilder.builder()
                .port(8998)
                .zkConn("127.0.0.1:2181")
                .serviceMap(serviceMap1)
                .build2();
        testBuilder.start();

//        Server testBuilder1 = ServerBuilder.builder()
//                .port(8999)
//                .zkConn("127.0.0.1:2182")
//                .serviceMap(serviceMap2)
//                .build2();
//        testBuilder1.start();
//        Server testBuilder2 = ServerBuilder.builder()
//                .port(9000)
//                .zkConn("127.0.0.1:2183")
//                .serviceMap(serviceMap3)
//                .build2();
//        testBuilder2.start();
    }

//    @AfterClass
//    public static void after() throws Exception {
//        testBuilder.shutdown();
//    }




    /**
     * 测试Channel池
     * 创建两个服务端， Client调用服务时，如果Channel未创建，那么会创建一个Channel，并放入池中
     * 当再次调用该服务时，不会重新创建Channel，而是从池中取
     * 所以服务调用三次，只添加两次Channel
     */
    @Test
    public void proxyTest() {

        ITest hello = ClientBuilder.<ITest>builder().zkConn("127.0.0.1:2183")
                .serviceName("s2")
                .serviceInterface(ITest.class).build();
        String result = hello.say("test1");
        System.out.println(result);
//


        ITest hello2 = ClientBuilder.<ITest>builder().zkConn("127.0.0.1:2181")
                .serviceName("s1")
                .serviceInterface(ITest.class).build();
        String result2 = hello2.say("test2");
        System.out.println(result2);

//
        ITest hello3 = ClientBuilder.<ITest>builder().zkConn("127.0.0.1:2181")
                .serviceName("s1")
                .serviceInterface(ITest.class).build();
        String result3 = hello3.say("test3");
        System.out.println(result3);
    }

    /**
     * 测试ZooKeeper集群
     * 1：在ZK上部署的结点,访问任意ZK地址都可以获取到
     *
     */
    @Test
    public void testZk() {
        ITest hello3 = ClientBuilder.<ITest>builder().zkConn("127.0.0.1:2181")
                .serviceName("s1")
                .serviceInterface(ITest.class).build();
        int a  = hello3.sum(1,2);

//        String result3 = hello3.say("test3");
        System.out.println(hello3.toString());

    }

    /**
     * 使用JDK代理时必须用接口接收数据
     */
    @Test
    public void testProxy() {
        TestImpl hello3 =  ClientBuilder.<TestImpl>builder().zkConn("127.0.0.1:2181")
                .serviceName("s2")
                .serviceInterface(TestImpl.class)
                .clientProxyClass(JDKProxy.class).build();
        int a  = hello3.sum(1,2);

//        String result3 = hello3.say("test3");
        System.out.println(hello3.toString());

    }

    /**
     * 测试网络传输模块性能
     */
    @Test
    public void testTransport() {
        ITest hello1 =  ClientBuilder.<ITest>builder().zkConn("127.0.0.1:2181")
                .serviceName("s1")
                .serviceInterface(ITest.class)
                .clientProxyClass(JDKProxy.class)
                .build();

        List<Long> arr = new ArrayList<>();
        long n=0;
        for (int i = 0; i < 10; i++) {
            long s = System.currentTimeMillis();
            for (int j = 0; j < 10000; j++) {
                hello1.say("123");
            }
            long e = System.currentTimeMillis();
            arr.add(e-s);
            n+=(e-s);
        }
        System.out.println("调用10000次耗费时间/10组 :"+arr+"\n 平均每次调用耗费时间:"+ n);
//        System.out.println("p"arr);

    }

    @Test
    public void testTransport2(){

        ITest hello1 =  ClientBuilder.<ITest>builder().zkConn("127.0.0.1:2181")
                .serviceName("s1")
                .serviceInterface(ITest.class)
                .clientProxyClass(JDKProxy.class)
                .build();

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(100);
        Long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i1 = 0; i1 < 1000; i1++) {
                        hello1.say("123");
                    }
                }
            });
        }
        fixedThreadPool.shutdown();
        while (true) {
            if (fixedThreadPool.isTerminated()) {
                System.out.println("Finally do something ");
                long end = System.currentTimeMillis();
                System.out.println("10个线程全部完成1000次调用耗时: " + (end - start) + "ms");
                break;
            }
        }
    }


}