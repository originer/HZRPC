**项目说明**

一个练手用的RPC框架，使用Netty作为底层通讯框架，ZooKeeper作为服务注册发现中心，支持使用SpringBoot快速启动。

- 使用Netty进行底层通讯
- 序列化方式采用SPI机制，支持hessain kryo protobuf进行序列化。
- 动态代理支持JDK代理 CGLIB代理
- 服务注册发现中心使用ZooKeeper实现
- 支持SpringBoot快速启动

**优化列表**
- [x] 对反射调用添加缓存
- [x] 引入disruptor框架，把事件处理从netty的工作线程中转移到disruptor中处理



**使用说明**

1. 首先启动ZooKeeper客户端，使用2181默认端口即可。
2. 在common的test目录里有测试用例可以直接测试，或者spring-provider下使用SpringBoot启动服务端和客户端。
3. 实现了SpringBoot的启动方式，有一个简单的界面可以使用。


![1530259463110](https://github.com/originer/HZRPC/blob/master/HZRPC/doc/%E5%90%AF%E5%8A%A8%E7%95%8C%E9%9D%A2.png)
