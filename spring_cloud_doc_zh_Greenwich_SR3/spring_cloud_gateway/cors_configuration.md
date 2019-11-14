# 118. CORS Configuration

## 118. CORS配置

可以将网关配置为控制CORS行为。“全局” CORS配置是URL模式到[Spring Framework`CorsConfiguration`](https://docs.spring.io/spring/docs/5.0.x/javadoc-api/org/springframework/web/cors/CorsConfiguration.html)的映射。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "https://docs.spring.io"
            allowedMethods:
            - GET
```



在上面的示例中，将从docs.spring.io发出的所有GET请求路径的请求中允许CORS请求。

要为未被某些网关路由谓词处理的请求提供相同的CORS配置，请将属性设置`spring.cloud.gateway.globalcors.add-to-simple-url-handler-mapping`为true。当尝试支持CORS预检请求并且您的路由谓词未评估为true时，此方法很有用，因为http方法为`options`。