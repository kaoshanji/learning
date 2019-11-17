# 42. Quick Start

## 42.快速入门

如果Spring Cloud Bus在类路径中检测到自身，则通过添加Spring Boot autconfiguration来工作。要启用总线，请在您的依赖项管理中添加`spring-cloud-starter-bus-amqp`或 `spring-cloud-starter-bus-kafka`。Spring Cloud负责其余的工作。确保代理（RabbitMQ或Kafka）可用并且已配置。在本地主机上运行时，您无需执行任何操作。如果您是远程运行，请使用Spring Cloud Connectors或Spring Boot约定定义代理凭据，如Rabbit的以下示例所示：

**application.yml。** 

```properties
spring:
  rabbitmq:
    host: mybroker.com
    port: 5672
    username: user
    password: secret
```



总线当前支持向所有侦听节点或特定服务（由Eureka定义）的所有节点发送消息。该`/bus/*`驱动器的命名空间具有一定的HTTP端点。当前，有两个已实现。首先，`/bus/env`发送键/值对以更新每个节点的Spring Environment。第二个`/bus/refresh`重新加载每个应用程序的配置，就好像它们都已在其`/refresh` 端点上被ping一样。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| Spring Cloud Bus入门文章涵盖Rabbit和Kafka，因为它们是两个最常见的实现。但是，Spring Cloud Stream非常灵活，并且绑定程序可与一起使用`spring-cloud-bus`。 |