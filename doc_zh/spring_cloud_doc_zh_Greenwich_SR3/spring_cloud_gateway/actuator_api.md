# 119. Actuator API

## 119.执行器API

该`/gateway`驱动器的端点允许监视和使用Spring的云网关应用程序进行交互。为了可远程访问，必须在应用程序属性中[通过HTTP或JMX ](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-endpoints-exposing-endpoints)[启用](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-endpoints-enabling-endpoints)和[公开](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-endpoints-exposing-endpoints)端点。

**application.properties。** 

```properties
management.endpoint.gateway.enabled=true # default value
management.endpoints.web.exposure.include=gateway
```



## 119.1详细执行器格式

一种新的，更详细的格式已添加到网关。这为每个路由增加了更多细节，从而允许查看与每个路由关联的谓词和过滤器以及任何可用的配置。

```json
/actuator/gateway/routes
[
  {
    "predicate": "(Hosts: [**.addrequestheader.org] && Paths: [/headers], match trailing slash: true)",
    "route_id": "add_request_header_test",
    "filters": [
      "[[AddResponseHeader X-Response-Default-Foo = 'Default-Bar'], order = 1]",
      "[[AddRequestHeader X-Request-Foo = 'Bar'], order = 1]",
      "[[PrefixPath prefix = '/httpbin'], order = 2]"
    ],
    "uri": "lb://testservice",
    "order": 0
  }
]
```

要启用此功能，请设置以下属性：

**application.properties。** 

```properties
spring.cloud.gateway.actuator.verbose.enabled=true
```



在将来的版本中，它将默认为true。

## 119.2检索路由过滤器

### 119.2.1全局过滤器

要检索应用于所有路由的[全局过滤器](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__actuator_api.html)，`GET`请向发出请求`/actuator/gateway/globalfilters`。产生的响应类似于以下内容：

```json
{
  "org.springframework.cloud.gateway.filter.LoadBalancerClientFilter@77856cc5": 10100,
  "org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter@4f6fd101": 10000,
  "org.springframework.cloud.gateway.filter.NettyWriteResponseFilter@32d22650": -1,
  "org.springframework.cloud.gateway.filter.ForwardRoutingFilter@106459d9": 2147483647,
  "org.springframework.cloud.gateway.filter.NettyRoutingFilter@1fbd5e0": 2147483647,
  "org.springframework.cloud.gateway.filter.ForwardPathFilter@33a71d23": 0,
  "org.springframework.cloud.gateway.filter.AdaptCachedBodyGlobalFilter@135064ea": 2147483637,
  "org.springframework.cloud.gateway.filter.WebsocketRoutingFilter@23c05889": 2147483646
}
```

该响应包含适当的全局过滤器的详细信息。为每个全局过滤器提供过滤器对象（例如`org.springframework.cloud.gateway.filter.LoadBalancerClientFilter@77856cc5`）的字符串表示形式以及过滤器链中的相应[顺序](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__global_filters.html#_combined_global_filter_and_gatewayfilter_ordering)。

### 119.2.2路由过滤器

要检索应用于路由的[GatewayFilter工厂](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__actuator_api.html)，`GET`请向发出请求`/actuator/gateway/routefilters`。产生的响应类似于以下内容：

```json
{
  "[AddRequestHeaderGatewayFilterFactory@570ed9c configClass = AbstractNameValueGatewayFilterFactory.NameValueConfig]": null,
  "[SecureHeadersGatewayFilterFactory@fceab5d configClass = Object]": null,
  "[SaveSessionGatewayFilterFactory@4449b273 configClass = Object]": null
}
```

该响应包含应用于任何特定路由的GatewayFilter工厂的详细信息。为每个工厂提供了相应对象（例如`[SecureHeadersGatewayFilterFactory@fceab5d configClass = Object]`）的字符串表示形式。请注意，该`null`值是由于端点控制器的实现不完整而导致的，因为它试图设置对象在过滤器链中的顺序，而该顺序不适用于GatewayFilter工厂对象。

## 119.3刷新路由缓存

要清除路由缓存，`POST`请向发送请求`/actuator/gateway/refresh`。该请求返回200，但没有响应主体。

## 119.4检索网关中定义的路由

要检索网关中定义的路由，`GET`请向发出请求`/actuator/gateway/routes`。产生的响应类似于以下内容：

```json
[{
  "route_id": "first_route",
  "route_object": {
    "predicate": "org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory$$Lambda$432/1736826640@1e9d7e7d",
    "filters": [
      "OrderedGatewayFilter{delegate=org.springframework.cloud.gateway.filter.factory.PreserveHostHeaderGatewayFilterFactory$$Lambda$436/674480275@6631ef72, order=0}"
    ]
  },
  "order": 0
},
{
  "route_id": "second_route",
  "route_object": {
    "predicate": "org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory$$Lambda$432/1736826640@cd8d298",
    "filters": []
  },
  "order": 0
}]
```

该响应包含网关中定义的所有路由的详细信息。下表描述了响应的每个元素（即路线）的结构。

| 路径                     | 类型 | 描述                                                         |
| ------------------------ | ---- | ------------------------------------------------------------ |
| `route_id`               | 串   | 路线编号。                                                   |
| `route_object.predicate` | 宾语 | 路由谓词。                                                   |
| `route_object.filters`   | 数组 | 该[GatewayFilter工厂](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__actuator_api.html)使用的路由。 |
| `order`                  | 数   | 路线顺序。                                                   |

## 119.5检索有关特定路线的信息

要检索有关一条路线的信息，`GET`请向发出请求`/actuator/gateway/routes/{id}`（例如`/actuator/gateway/routes/first_route`）。产生的响应类似于以下内容：

```json
{
  "id": "first_route",
  "predicates": [{
    "name": "Path",
    "args": {"_genkey_0":"/first"}
  }],
  "filters": [],
  "uri": "https://www.uri-destination.org",
  "order": 0
}]
```

下表描述了响应的结构。

| 路径         | 类型 | 描述                                                   |
| ------------ | ---- | ------------------------------------------------------ |
| `id`         | 串   | 路线编号。                                             |
| `predicates` | 数组 | 路由谓词的集合。每个项目都定义给定谓词的名称和自变量。 |
| `filters`    | 数组 | 应用于路线的过滤器集合。                               |
| `uri`        | 串   | 路由的目标URI。                                        |
| `order`      | 数   | 路线顺序。                                             |

## 119.6创建和删除特定路线

要创建路由，`POST`请`/gateway/routes/{id_route_to_create}`使用JSON主体发送请求，以指定路由的字段（请参见上一小节）。

要删除路线，`DELETE`请向发出请求`/gateway/routes/{id_route_to_delete}`。

## 119.7概述：所有端点的列表

下表总结了Spring Cloud Gateway执行器端点。请注意，每个端点都有`/actuator/gateway`作为基本路径。

| ID              | HTTP方法 | 描述                                        |
| --------------- | -------- | ------------------------------------------- |
| `globalfilters` | 得到     | 显示应用于路由的全局过滤器列表。            |
| `routefilters`  | 得到     | 显示应用于特定路由的GatewayFilter工厂列表。 |
| `refresh`       | 开机自检 | 清除路由缓存。                              |
| `routes`        | 得到     | 显示网关中定义的路由列表。                  |
| `routes/{id}`   | 得到     | 显示有关特定路线的信息。                    |
| `routes/{id}`   | 开机自检 | 将新路由添加到网关。                        |
| `routes/{id}`   | 删除     | 从网关删除现有路由。                        |