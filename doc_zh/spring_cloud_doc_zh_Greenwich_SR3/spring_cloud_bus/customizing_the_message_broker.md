# 47. Customizing the Message Broker

## 47.定制消息代理

Spring Cloud Bus使用[Spring Cloud Stream](https://cloud.spring.io/spring-cloud-stream)广播消息。因此，要使消息流动，您只需要在类路径中包括您选择的活页夹实现即可。带有AMQP（RabbitMQ）和Kafka（`spring-cloud-starter-bus-[amqp|kafka]`）的总线有便捷的启动器。一般来说，Spring Cloud Stream依赖于Spring Boot自动配置约定来配置中间件。例如，可以使用`spring.rabbitmq.*`配置属性更改AMQP代理地址 。Spring Cloud Bus中具有少数本机配置属性`spring.cloud.bus.*`（例如， `spring.cloud.bus.destination`是用作外部中间件的主题名称）。通常，默认值就足够了。

要了解有关如何自定义消息代理设置的更多信息，请参阅Spring Cloud Stream文档。