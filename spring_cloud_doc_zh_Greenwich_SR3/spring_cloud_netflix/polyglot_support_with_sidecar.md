# 19. Polyglot support with Sidecar

## 19. Sidecar支持多语种

您是否要使用非JVM语言来利用Eureka，Ribbon和Config Server？Spring Cloud Netflix Sidecar的灵感来自[Netflix Prana](https://github.com/Netflix/Prana)。它包括一个HTTP API，用于获取给定服务的所有实例（按主机和端口）。您也可以通过嵌入式Zuul代理来代理服务呼叫，该代理从Eureka获取其路由条目。可以通过主机查找或通过Zuul代理直接访问Spring Cloud Config Server。非JVM应用程序应实施运行状况检查，以便Sidecar可以向Eureka报告该应用程序是否启动。

要将Sidecar包含在您的项目中，请使用组ID为`org.springframework.cloud` 和工件ID为或的依赖项`spring-cloud-netflix-sidecar`。

要启用Sidecar，请使用创建一个Spring Boot应用程序`@EnableSidecar`。这个注解包括`@EnableCircuitBreaker`，`@EnableDiscoveryClient`，和`@EnableZuulProxy`。在与非JVM应用程序相同的主机上运行结果应用程序。

要配置的车边，加`sidecar.port`和`sidecar.health-uri`来`application.yml`。该`sidecar.port`属性是非JVM应用程序侦听的端口。这样，Sidecar可以在Eureka中正确注册该应用程序。这些`sidecar.secure-port-enabled`选项提供了一种启用流量安全端口的方法。该`sidecar.health-uri`是一个URI的非JVM的应用程序，模仿春引导健康指标接近。它应该返回类似于以下内容的JSON文档：

**health-uri-document。** 

```json
{ 
  “ status”：“ UP” 
}
```



以下application.yml示例显示了Sidecar应用程序的示例配置：

**application.yml。** 

```properties
server:
  port: 5678
spring:
  application:
    name: sidecar

sidecar:
  port: 8000
  health-uri: http://localhost:8000/health.json
```



该`DiscoveryClient.getInstances()`方法的API 是`/hosts/{serviceId}`。以下示例响应用于`/hosts/customers`返回不同主机上的两个实例：

**/ hosts / customers。** 

```json
[
    {
        "host": "myhost",
        "port": 9000,
        "uri": "http://myhost:9000",
        "serviceId": "CUSTOMERS",
        "secure": false
    },
    {
        "host": "myhost2",
        "port": 9000,
        "uri": "http://myhost2:9000",
        "serviceId": "CUSTOMERS",
        "secure": false
    }
]
```



非JVM应用程序（如果Sidecar在端口5678上）可通过访问该API `http://localhost:5678/hosts/{serviceId}`。

Zuul代理会自动为Eureka中已知的每个服务添加路由`/`，因此可以在上使用客户服务`/customers`。非JVM应用程序可以在以下位置访问客户服务`http://localhost:5678/customers`（假设Sidecar正在监听5678端口）。

如果Config Server已向Eureka注册，则非JVM应用程序可以通过Zuul代理对其进行访问。如果`serviceId`ConfigServer的为`configserver`，并且Sidecar在端口5678上，则可以通过[http：// localhost：5678 / configserver](http://localhost:5678/configserver)对其进行访问。

非JVM应用程序可以利用Config Server返回YAML文档的功能。例如，调用https://sidecar.local.spring.io:5678/configserver/default-master.yml 可能会导致YAML文档类似于以下内容：

```properties
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  password: password
info:
  description: Spring Cloud Samples
  url: https://github.com/spring-cloud-samples
```

为了使健康检查请求在使用设置`sidecar.accept-all-ssl-certificates`为true的HTTP时能够接受所有证书。