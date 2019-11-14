# 72. Circuit Breaker with Hystrix

## 72.带Hystrix的断路器

通过将此启动程序包含在项目pom.xml：中，应用程序可以使用Spring Cloud Netflix项目提供的Hystrix断路器`spring-cloud-starter-hystrix`。Hystrix不依赖Netflix Discovery Client。的`@EnableHystrix`注释应被放置在配置类（通常是主类）。然后可以对方法进行注释，`@HystrixCommand`以使其受到断路器的保护。有关更多详细信息，请参见[文档](https://projects.spring.io/spring-cloud/spring-cloud.html#_circuit_breaker_hystrix_clients)。