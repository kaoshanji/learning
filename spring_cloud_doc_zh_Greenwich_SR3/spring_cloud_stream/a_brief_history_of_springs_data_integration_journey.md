# 24. A Brief History of Spring’s Data Integration Journey

## 24. Spring的数据集成之旅简史

Spring的Data Integration之旅始于[Spring Integration](https://projects.spring.io/spring-integration/)。通过其编程模型，它为开发人员提供了一致的开发经验，以构建可以包含[企业集成模式](http://www.enterpriseintegrationpatterns.com/)以与外部系统（例如数据库，消息代理等）连接的应用程序。

快进到云时代，微服务已在企业环境中变得突出。[Spring Boot](https://projects.spring.io/spring-boot/)改变了开发人员构建应用程序的方式。借助Spring的编程模型和Spring Boot处理的运行时职责，开发独立的，生产级的基于Spring的微服务变得无缝。

为了将其扩展到数据集成工作负载，Spring Integration和Spring Boot被放到一个新项目中。Spring Cloud Stream诞生了。

借助Spring Cloud Stream，开发人员可以：*独立地构建，测试，迭代和部署以数据为中心的应用程序。*应用现代微服务架构模式，包括通过消息传递进行组合。*以事件为中心的思维将应用程序职责分离。事件可以表示及时发生的事件，下游消费者应用程序可以在不知道事件起源或生产者身份的情况下做出反应。*将业务逻辑移植到消息代理（例如RabbitMQ，Apache Kafka，Amazon Kinesis）上。*通过使用Project Reactor的Flux和Kafka Streams API，可以在基于通道的应用程序和基于非通道的应用程序绑定方案之间进行互操作，以支持无状态和有状态的计算。*依靠框架对常见用例的自动内容类型支持。