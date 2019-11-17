# 116. Configuration

## 116.配置

Spring Cloud Gateway的配置由RouteDefinitionLocator的集合驱动。

**RouteDefinitionLocator.java。** 

```java
public interface RouteDefinitionLocator {
	Flux<RouteDefinition> getRouteDefinitions();
}
```



默认情况下，`PropertiesRouteDefinitionLocator`使用Spring Boot的`@ConfigurationProperties`机制加载属性。

上面的所有配置示例都使用一种快捷方式符号，该快捷方式符号使用位置参数而不是命名参数。以下两个示例是等效的：

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: setstatus_route
        uri: https://example.org
        filters:
        - name: SetStatus
          args:
            status: 401
      - id: setstatusshortcut_route
        uri: https://example.org
        filters:
        - SetStatus=401
```



对于网关的某些用法，属性将是足够的，但是某些生产用例将受益于从外部源（例如数据库）加载配置。未来的里程碑版本将`RouteDefinitionLocator`基于Spring数据存储库实现，例如：Redis，MongoDB和Cassandra。

## 116.1 Fluent Java Routes API

为了在Java中进行简单的配置，在`RouteLocatorBuilder`bean中定义了一个流畅的API 。

**GatewaySampleApplication.java。** 

```java
// static imports from GatewayFilters and RoutePredicates
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder, ThrottleGatewayFilterFactory throttle) {
    return builder.routes()
            .route(r -> r.host("**.abc.org").and().path("/image/png")
                .filters(f ->
                        f.addResponseHeader("X-TestHeader", "foobar"))
                .uri("http://httpbin.org:80")
            )
            .route(r -> r.path("/image/webp")
                .filters(f ->
                        f.addResponseHeader("X-AnotherHeader", "baz"))
                .uri("http://httpbin.org:80")
            )
            .route(r -> r.order(-1)
                .host("**.throttle.org").and().path("/get")
                .filters(f -> f.filter(throttle.apply(1,
                        1,
                        10,
                        TimeUnit.SECONDS)))
                .uri("http://httpbin.org:80")
            )
            .build();
}
```



此样式还允许更多自定义谓词断言。`RouteDefinitionLocator`bean 定义的谓词使用逻辑组合`and`。通过使用流利的Java API，你可以使用`and()`，`or()`并且`negate()`对运营`Predicate`类。

## 116.2 DiscoveryClient路由定义定位器

可以将网关配置为基于在`DiscoveryClient`兼容服务注册表中注册的服务来创建路由。

要启用此功能，请设置`spring.cloud.gateway.discovery.locator.enabled=true`并确保`DiscoveryClient`实现在类路径上并已启用（例如Netflix Eureka，Consul或Zookeeper）。

### 116.2.1为DiscoveryClient路由配置谓词和过滤器

默认情况下，网关为通过创建的路由定义单个谓词和过滤器`DiscoveryClient`。

默认谓词是使用模式定义的路径谓词`/serviceId/**`，其中`serviceId`是来自的服务ID `DiscoveryClient`。

默认的过滤器是带有正则表达式`/serviceId/(?.*)`和替换的 重写路径过滤器`/${remaining}`。这只是在将请求发送到下游之前从路径中剥离服务ID。

如果您想自定义`DiscoveryClient`路线使用的谓词和/或过滤器，可以通过设置`spring.cloud.gateway.discovery.locator.predicates[x]`和来实现`spring.cloud.gateway.discovery.locator.filters[y]`。这样做时，如果要保留该功能，则需要确保在上面包含默认谓词和过滤器。以下是此示例的示例。

**application.properties。** 

```properties
spring.cloud.gateway.discovery.locator.predicates[0].name: Path
spring.cloud.gateway.discovery.locator.predicates[0].args[pattern]: "'/'+serviceId+'/**'"
spring.cloud.gateway.discovery.locator.predicates[1].name: Host
spring.cloud.gateway.discovery.locator.predicates[1].args[pattern]: "'**.foo.com'"
spring.cloud.gateway.discovery.locator.filters[0].name: Hystrix
spring.cloud.gateway.discovery.locator.filters[0].args[name]: serviceId
spring.cloud.gateway.discovery.locator.filters[1].name: RewritePath
spring.cloud.gateway.discovery.locator.filters[1].args[regexp]: "'/' + serviceId + '/(?<remaining>.*)'"
spring.cloud.gateway.discovery.locator.filters[1].args[replacement]: "'/${remaining}'"
```