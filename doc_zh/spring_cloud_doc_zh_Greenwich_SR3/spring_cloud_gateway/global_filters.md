# 114. Global Filters

## 114.全局过滤器

该`GlobalFilter`接口具有与相同的签名`GatewayFilter`。这些是特殊过滤器，有条件地应用于所有路由。（此界面和用法可能会在将来的里程碑中更改）。

## 114.1组合的全局过滤器和GatewayFilter排序

当请求进入（并与路由匹配）时，过滤Web处理程序会将的所有实例`GlobalFilter`和所有特定`GatewayFilter`于路由的实例添加到过滤器链中。该组合的过滤器链按`org.springframework.core.Ordered`接口排序，可以通过实现该`getOrder()`方法或通过使用`@Order`注释来设置。

由于Spring Cloud Gateway区分过滤器逻辑执行的“前”和“后”阶段（请参见：工作原理），因此优先级最高的过滤器将在“前”阶段中处于第一个阶段，而在“后”阶段中处于最后一个阶段“-相。

**ExampleConfiguration.java。** 

```java
@Bean
@Order(-1)
public GlobalFilter a() {
    return (exchange, chain) -> {
        log.info("first pre filter");
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("third post filter");
        }));
    };
}

@Bean
@Order(0)
public GlobalFilter b() {
    return (exchange, chain) -> {
        log.info("second pre filter");
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("second post filter");
        }));
    };
}

@Bean
@Order(1)
public GlobalFilter c() {
    return (exchange, chain) -> {
        log.info("third pre filter");
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("first post filter");
        }));
    };
}
```



## 114.2转发路由过滤器

将`ForwardRoutingFilter`在交换属性查找一个URI `ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR`。如果url有一个`forward`方案（即`forward:///localendpoint`），它将使用Spring `DispatcherHandler`处理请求。请求URL的路径部分将被转发URL中的路径覆盖。未经修改的原始url将附加到`ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR`属性中的列表。

## 114.3 LoadBalancerClient筛选器

将`LoadBalancerClientFilter`在交换属性查找一个URI `ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR`。如果url有一个`lb`方案（即`lb://myservice`），它将使用Spring Cloud `LoadBalancerClient`将名称（`myservice`在前面的示例中）解析为实际的主机和端口，并在同一属性中替换URI。未经修改的原始url将附加到`ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR`属性中的列表。过滤器还将在`ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR`属性中查找是否相等`lb`，然后应用相同的规则。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: myRoute
        uri: lb://service
        predicates:
        - Path=/service/**
```



| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 默认情况下，当不能在找到一个服务实例`LoadBalancer`一`503`将被退回。您可以`404`通过设置将网关配置为返回`spring.cloud.gateway.loadbalancer.use404=true`。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 从返回的`isSecure`值将覆盖对网关的请求中指定的方案。例如，如果请求通过网关进入网关， 但指示该请求不安全，则将通过下游请求 。相反的情况也可以适用。但是，如果在“网关”配置中为路由指定了该前缀，则将删除前缀，并且从路由URL生成的方案将覆盖该配置。`ServiceInstance``LoadBalancer``HTTPS``ServiceInstance``HTTP``GATEWAY_SCHEME_PREFIX_ATTR``ServiceInstance` |

## 114.4 Netty路由筛选器

如果位于`ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR`交换属性中的URL 具有`http`或`https`方案，则将运行Netty路由筛选器。它使用Netty `HttpClient`发出下游代理请求。响应将放入`ServerWebExchangeUtils.CLIENT_RESPONSE_ATTR`exchange属性中，以供以后的过滤器使用。（有一个`WebClientHttpRoutingFilter`执行相同功能的实验，但不需要净值）

## 114.5 Netty写响应过滤器

的`NettyWriteResponseFilter`，如果有一个运行的Netty `HttpClientResponse`在`ServerWebExchangeUtils.CLIENT_RESPONSE_ATTR`交换属性。它在所有其他筛选器完成后运行，并将代理响应写回到网关客户端响应。（有一个`WebClientWriteResponseFilter`执行相同功能的实验，但不需要净值）

## 114.6 RouteToRequestUrl过滤器

的`RouteToRequestUrlFilter`，如果有一个运行`Route`中的对象`ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR`交换属性。它基于请求URI创建一个新URI，但使用`Route`对象的URI属性进行更新。新的URI放置在`ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR`exchange属性中。

如果URI具有方案前缀（例如）`lb:ws://serviceid`，则将`lb`方案从URI中剥离，并放入中以`ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR`供稍后在过滤器链中使用。

## 114.7 Websocket路由过滤器

如果位于`ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR`交换属性中的URL 具有`ws`或`wss`方案，则Websocket路由筛选器将运行。它使用Spring Web Socket基础结构向下游转发Websocket请求。

的WebSockets可以是负载平衡用前缀的URI `lb`，如`lb:ws://serviceid`。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果您将[SockJS](https://github.com/sockjs)用作常规http的后备，则应配置常规HTTP路由以及Websocket路由。 |

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      # SockJS route
      - id: websocket_sockjs_route
        uri: http://localhost:3001
        predicates:
        - Path=/websocket/info/**
      # Normwal Websocket route
      - id: websocket_route
        uri: ws://localhost:3001
        predicates:
        - Path=/websocket/**
```



## 114.8网关指标过滤器

要启用网关度量标准，请添加spring-boot-starter-actuator作为项目依赖项。然后，默认情况下，只要属性`spring.cloud.gateway.metrics.enabled`未设置为，网关衡量指标筛选器就会运行`false`。此过滤器添加一个带有以下标记的名为“ gateway.requests”的计时器度量标准：

- `routeId`：路线ID
- `routeUri`：API将被路由到的URI
- `outcome`：按[HttpStatus.Series](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpStatus.Series.html)分类的结果
- `status`：请求返回给客户端的Http状态
- `httpStatusCode`：请求返回给客户端的Http状态
- `httpMethod`：用于请求的Http方法

然后可以从这些指标中[删除](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/gateway-grafana-dashboard.jpeg)这些指标，`/actuator/metrics/gateway.requests`并可以将它们轻松地与Prometheus集成以创建[Grafana ](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/gateway-grafana-dashboard.jpeg)[仪表板](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/gateway-grafana-dashboard.json)。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 要启用Prometheus端点，请添加micrometer-registry-prometheus作为项目依赖项。 |

## 114.9将交换标记为已路由

网关路由完成后，`ServerWebExchange`它将通过添加`gatewayAlreadyRouted` 到交换属性来将该交换标记为“已路由” 。将请求标记为已路由后，其他路由筛选器将不会再次路由请求，实质上会跳过该筛选器。您可以使用方便的方法将交换标记为已路由或检查交换是否已路由。

- `ServerWebExchangeUtils.isAlreadyRouted`接受一个`ServerWebExchange`对象并检查它是否已被“路由”
- `ServerWebExchangeUtils.setAlreadyRouted`接受一个`ServerWebExchange`对象并将其标记为“已路由”