# 112. Route Predicate Factories

## 112.路由谓词工厂

Spring Cloud Gateway将路由作为Spring WebFlux `HandlerMapping`基础架构的一部分进行匹配。Spring Cloud Gateway包括许多内置的Route Predicate工厂。所有这些谓词都与HTTP请求的不同属性匹配。多个Route Predicate工厂可以合并，也可以通过逻辑合并`and`。

## 112.1路由谓词工厂之后

After Route Predicate Factory采用一个参数，即日期时间。该谓词匹配在当前日期时间之后发生的请求。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: after_route
        uri: https://example.org
        predicates:
        - After=2017-01-20T17:42:47.789-07:00[America/Denver]
```



此路线与2017年1月20日17:42山区时间（丹佛）之后的所有请求匹配。

## 112.2路由谓词工厂之前

路由谓词前工厂采用一个参数，即日期时间。该谓词匹配当前日期时间之前发生的请求。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: before_route
        uri: https://example.org
        predicates:
        - Before=2017-01-20T17:42:47.789-07:00[America/Denver]
```



此路线与2017年1月20日17:42山区时间（丹佛）之前的所有请求匹配。

## 112.3路由谓词工厂之间

路由谓词间工厂之间有两个参数，datetime1和datetime2。该谓词匹配在datetime1之后和datetime2之前发生的请求。datetime2参数必须在datetime1之后。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: between_route
        uri: https://example.org
        predicates:
        - Between=2017-01-20T17:42:47.789-07:00[America/Denver], 2017-01-21T17:42:47.789-07:00[America/Denver]
```



该路线与2017年1月20日山区时间（丹佛）之后和2017年1月21日17:42山区时间（丹佛）之后的所有请求匹配。这对于维护时段可能很有用。

## 112.4 Cookie路线谓词工厂

Cookie Route Predicate Factory采用两个参数，即cookie名称和正则表达式。该谓词匹配具有给定名称的cookie，并且值匹配正则表达式。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: cookie_route
        uri: https://example.org
        predicates:
        - Cookie=chocolate, ch.p
```



此路由与请求匹配，并具有一个名为`chocolate`who的值与`ch.p`正则表达式匹配的cookie 。

## 112.5标头路由谓词工厂

标头路由谓词工厂采用两个参数，标头名称和正则表达式。该谓词与具有给定名称的标头匹配，并且值与正则表达式匹配。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: header_route
        uri: https://example.org
        predicates:
        - Header=X-Request-Id, \d+
```



如果请求具有名为`X-Request-Id`whos值的标头与`\d+`正则表达式匹配（具有一个或多个数字的值），则此路由匹配。

## 112.6主机路由谓词工厂

主机路由谓词工厂采用一个参数：主机名模式列表。该模式是带有`.`作为分隔符的Ant样式模式。谓词与`Host`匹配模式的标头匹配。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: host_route
        uri: https://example.org
        predicates:
        - Host=**.somehost.org,**.anotherhost.org
```



还支持URI模板变量`{sub}.myhost.org`。

如果请求的`Host`标头中包含值`www.somehost.org`or `beta.somehost.org`或，则此路由将匹配`www.anotherhost.org`。

该谓词提取URI模板变量（`sub`如上例中定义的那样）作为名称和值的映射，并`ServerWebExchange.getAttributes()`使用中定义的键将其放在中`ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE`。这些值可供[GatewayFilter工厂](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_gateway-request-predicates-factories.html#gateway-route-filters)使用。

## 112.7方法路线谓词工厂

方法路由谓词工厂使用一个参数：要匹配的HTTP方法。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: method_route
        uri: https://example.org
        predicates:
        - Method=GET
```



如果request方法为，则此路由将匹配`GET`。

## 112.8路径路线谓词工厂

路径路由谓词工厂采用两个参数：弹簧`PathMatcher`模式列表和的可选标志`matchOptionalTrailingSeparator`。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: host_route
        uri: https://example.org
        predicates:
        - Path=/foo/{segment},/bar/{segment}
```



如果请求路径是，例如这条路线将匹配：`/foo/1`或`/foo/bar`或`/bar/baz`。

该谓词提取URI模板变量（`segment`如上例中定义的那样）作为名称和值的映射，并`ServerWebExchange.getAttributes()`使用中定义的键将其放在中`ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE`。这些值可供[GatewayFilter工厂](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_gateway-request-predicates-factories.html#gateway-route-filters)使用。

可以使用实用程序方法来简化对这些变量的访问。

```java
Map<String, String> uriVariables = ServerWebExchangeUtils.getPathPredicateVariables(exchange);

String segment = uriVariables.get("segment");
```

## 112.9查询路由谓词工厂

查询路由谓词工厂采用两个参数：required `param`和optional `regexp`。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: query_route
        uri: https://example.org
        predicates:
        - Query=baz
```



如果请求包含`baz`查询参数，则此路由将匹配。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: query_route
        uri: https://example.org
        predicates:
        - Query=foo, ba.
```



如果请求包含一个`foo`查询参数，其值与`ba.`regexp 匹配，则此路由将匹配，`bar`并且`baz`将匹配。

## 112.10 RemoteAddr路由谓词工厂

RemoteAddr路由谓词工厂采用CIDR标记（IPv4或IPv6）字符串的列表（最小大小为1），例如`192.168.0.1/16`（其中`192.168.0.1`IP地址和`16`子网掩码）。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: remoteaddr_route
        uri: https://example.org
        predicates:
        - RemoteAddr=192.168.1.1/24
```



如果请求的远程地址为，则此路由将匹配`192.168.1.10`。

### 112.10.1修改解析远程地址的方式

默认情况下，RemoteAddr路由谓词工厂使用传入请求中的远程地址。如果Spring Cloud Gateway位于代理层后面，则此地址可能与实际的客户端IP地址不匹配。

您可以通过设置custom来定制解析远程地址的方式`RemoteAddressResolver`。春季云网关来与基于脱一个非默认的远程地址解析器的[X -转发，对于头](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-For)，`XForwardedRemoteAddressResolver`。

`XForwardedRemoteAddressResolver` 有两种静态构造方法，它们采用不同的安全性方法：

`XForwardedRemoteAddressResolver::trustAll`返回`RemoteAddressResolver`，该地址始终采用在`X-Forwarded-For`标头中找到的第一个IP地址。这种方法容易受到欺骗的攻击，因为恶意客户端可能会为其设置一个初始值，`X-Forwarded-For`解析器会接受该初始值。

`XForwardedRemoteAddressResolver::maxTrustedIndex`取得与Spring Cloud Gateway前面运行的受信任基础架构数量相关的索引。例如，如果只能通过HAProxy访问Spring Cloud Gateway，则应使用值1。如果在访问Spring Cloud Gateway之前需要两跳可信基础架构，则应使用值2。

给定以下标头值：

```bash
X-Forwarded-For: 0.0.0.1, 0.0.0.2, 0.0.0.3
```

`maxTrustedIndex`下面的值将产生以下远程地址。

| `maxTrustedIndex`         | 结果                                           |
| ------------------------- | ---------------------------------------------- |
| [ `Integer.MIN_VALUE`，0] | （`IllegalArgumentException`在初始化期间无效） |
| 1个                       | 0.0.0.3                                        |
| 2                         | 0.0.0.2                                        |
| 3                         | 0.0.0.1                                        |
| [4，`Integer.MAX_VALUE`]  | 0.0.0.1                                        |

使用Java配置：

网关配置文件

```java
RemoteAddressResolver resolver = XForwardedRemoteAddressResolver
    .maxTrustedIndex(1);

...

.route("direct-route",
    r -> r.remoteAddr("10.1.1.1", "10.10.1.1/24")
        .uri("https://downstream1")
.route("proxied-route",
    r -> r.remoteAddr(resolver,  "10.10.1.1", "10.10.1.1/24")
        .uri("https://downstream2")
)
```