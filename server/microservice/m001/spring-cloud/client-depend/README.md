#   起步：依赖调用


##  效果

相互依赖的服务通过URL调用


##  项目列表
-   app-producer-service：服务提供者
-   app-consumer-service：服务消费者

没有使用任何Spring Cloud组件，显示了一下Spring Boot实现REST接口

##  访问

地址：http://localhost:8100/helloConsumer

响应：kaoshanji

效果：![20190516100432](../images/20190516100432.png)

##  主要代码

-   app-producer-service：top.kaoshanji.HelloController#hello
-   app-consumer-service：top.kaoshanji.ConsumerController#helloConsumer


##  备注

这种请求地址在消费者端写死，并且提供者实例也只有一个，绑定死了

如果有多个提供者实例，消费者该如何调用？提供者或消费者只有一个实例是不稳定的，并且在调用依赖服务出现错误，比如，请求超时、服务挂了等，又需要怎样解决？

服务之间存在多级依赖，如果出现一处不可用而延伸到很多地方，这样比集中式还不稳定

----