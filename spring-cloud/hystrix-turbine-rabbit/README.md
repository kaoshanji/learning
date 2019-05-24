#   断路3：消息发布断路-turbine-RabbitMQ


##  效果

了解多个服务断路的状态，通过 RabbitMQ 发送信息

创建 hystrix-turbine 虚拟机

当启动 应用 时，RabbitMQ 控制台就会出现，当停止时就会消失

RabbitMQ 放在阿里云服务器上，本地监视效果会出现网络延时

![20190524170912](../images/20190524170912.png)
![20190524170931](../images/20190524170931.png)
![20190524170942](../images/20190524170942.png)
![20190524174450](../images/20190524174450.png)



##  项目列表
-   cloud-eureka-service：治理服务，第一个启动
-   app-producer-service：服务提供者，其次启动
-   app-consumer-service：服务消费者，再次启动
-   app-hystrixCommand-service：演示方法级断路，再次启动
-   cloud-hystrix-turbine-service：聚合监控信息，再次启动

spring cloud：
- spring-cloud-starter-netflix-eureka-server：服务治理中心，提供服务发现、注册功能，是服务大管家
- spring-cloud-starter-netflix-hystrix：服务降级、断路
- spring-cloud-starter-openfeign：声明式服务调用，集成了 Ribbon(客户端侧负载均衡)，当服务提供者有多个实例或地址端口变动对客户端没有影响
- spring-cloud-starter-netflix-eureka-client：服务治理客户端，把自己注册到服务治理，让其他服务调用
- spring-boot-starter-actuator：发布服务状态信息
- spring-cloud-starter-netflix-hystrix-dashboard：服务断路状态数据展示
- spring-cloud-starter-netflix-turbine：整个系统断路监控
- spring-cloud-starter-netflix-turbine-stream：接收断路消息
- spring-cloud-netflix-hystrix-stream：发送断路消息
- spring-cloud-stream-binder-rabbit：集成 rabbit


##  访问


##  备注

