#   SpringCloud组件及相关技术演示

文档说明，配上演示

##  示例

部分组件可能不再被更新，对应的功能换成[Spring Cloud Alibaba](https://github.com/spring-cloud-incubator/spring-cloud-alibaba/blob/master/README-zh.md)，目前处于孵化期。

感受一下，眼睛看的不如动手试试，跑起来！

每个文件夹下面的项目都是单独的，可以直接运行，并且包含说明。

-   [起步：依赖调用](client-depend/README.md)
-   [基础：服务中心和服务断路](hystrix-eureka/README.md)
-   [熔断状态1：某个服务](hystrix-dashboard/README.md)
-   [熔断状态2：多个服务-turbine](hystrix-turbine/README.md)
-   监控1：追踪服务调用过程-zipkin
    -   [内存](sleuth-zipkin/README.md)
-   配置1：示例
    -   [Git](config-git/README.md)，父目录下：Lifecycle --> clean/package 执行可得`可运行Jar包`
    -   [MySQL](config-mysql/README.md)
-   [配置2：让配置服务作为应用](config-git-eureka/README.md)
-   网关
    -   [zuul](gateway-zuul/README.md)
    -   [//Gateway，未实现](gateway-gateway/README.md)

**引入消息中间件：[RabbitMQ](../rabbitmq/README.md)**

-   [解耦应用：消息驱动-RabbitMQ](stream-rabbit/README.md)
-   [配置3：消息发布配置-RabbitMQ](config-git-rabbit/README.md)
-   [断路3：消息发布断路-turbine-RabbitMQ](hystrix-turbine-rabbit/README.md)
-   [监控2：追踪服务调用过程-zipkin-MySQL-RabbitMQ](sleuth-zipkin-mysql/README.md)

-   版本

    ```
        <dependency>
            <!-- Import dependency management from Spring Boot -->
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.1.4.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <!-- Spring cloud 依赖版本号是伦敦地铁站 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Greenwich.SR1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    ```

##  环境依赖

|名称|版本|描述|下载|备注|
|----|----|----|----|----|
|JDK|1.8|Java平台环境|[地址](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)|选择OS对应版本|
|OS|win/mac/ubuntu|操作系统|-|三种系统只是在环境配置上不同|
|IDEA|较新版|开发工具|[地址](https://www.jetbrains.com/idea/)|----|
|Maven|最新版|依赖包管理|[地址](https://maven.apache.org/)|第三方包管理，部署打包|
|[MySQL](../mysql/README.md)|5.7|关系型数据库|[地址](https://www.mysql.com/)|mysql示例|
|[RabbitMQ](../rabbitmq/README.md)|3.6|消息代理服务器|[地址](http://www.rabbitmq.com/)|RabbitMQ示例|
||||||

##  涉及的组件
-   spring-boot-starter-web：定义web应用依赖
-   spring-cloud-starter-netflix-eureka-server：服务治理中心，提供服务发现、注册功能，是服务大管家
-   spring-cloud-starter-netflix-eureka-client：服务治理客户端，把自己注册到服务治理，让其他服务调用
-   spring-cloud-starter-netflix-hystrix：服务降级、断路，兜底方案
-   spring-cloud-starter-openfeign：声明式服务调用，集成了 Ribbon(客户端侧负载均衡)，当服务提供者有多个实例或地址端口变动对客户端没有影响
-   spring-boot-starter-actuator：发布服务状态信息
-   spring-cloud-starter-netflix-hystrix-dashboard：服务断路状态数据展示
-   spring-cloud-starter-netflix-turbine：整个系统断路监控
-   spring-cloud-starter-sleuth：服务调用链路追踪
-   spring-cloud-starter-zipkin：集成zipkin，并展示追踪数据
-   spring-cloud-config-server：配置管理中心服务端
-   spring-cloud-starter-config：配置管理客户端
-   spring-cloud-stream-binder-rabbit：集成RabbitMQ
-   spring-cloud-bus：消息总线


##  资料
-   [spring-cloud官网](https://spring.io/projects/spring-cloud)
-   [Spring Cloud 中文索引](http://springcloud.fun/)
-   [GitHub-spring-cloud](https://github.com/spring-cloud)
-   [Spring Cloud 从入门到精通](http://blog.didispace.com/spring-cloud-learning/)
-   [windmt](https://windmt.com/tags/Spring-Cloud/)

----
