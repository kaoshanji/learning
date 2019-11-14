# 70. Consul Retry

## 70.领事重试

如果您希望在应用启动时领事代理有时不可用，则可以要求其在失败后继续尝试。您需要添加 `spring-retry`和`spring-boot-starter-aop`到您的类路径。默认行为是重试6次，初始回退间隔为1000ms，随后的回退的指数乘数为1.1。您可以使用`spring.cloud.consul.retry.*`配置属性来配置这些属性（和其他属性）。这适用于Spring Cloud Consul Config和Discovery注册。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 要完全控制重试，请添加ID为“ consulRetryInterceptor” `@Bean`的类型 `RetryOperationsInterceptor`。Spring Retry具有一个`RetryInterceptorBuilder`易于创建的功能。 |