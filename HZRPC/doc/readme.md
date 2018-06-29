项目说明

一个练手用的RPC框架，使用Netty作为底层通讯框架，ZooKeeper作为服务注册发现中心，支持使用SpringBoot快速启动。

- 使用Netty进行底层通讯
- 序列化方式采用SPI机制，支持hessain kryo protobuf进行序列化。
- 动态代理支持JDK代理 CGLIB代理
- 服务注册发现中心使用ZooKeeper实现
- 支持SpringBoot快速启动

使用说明

1. 首先启动ZooKeeper客户端，使用2181默认端口即可。
2. 在common的test目录里有测试用例可以直接测试，或者spring-provider下使用SpringBoot启动服务端和客户端。
3. 实现了SpringBoot的启动方式，有一个简单的界面可以使用。
