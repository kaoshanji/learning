# 113. GatewayFilter Factories

## 113. GatewayFilter工厂

路由过滤器允许以某种方式修改传入的HTTP请求或传出的HTTP响应。路由过滤器适用于特定路由。Spring Cloud Gateway包括许多内置的GatewayFilter工厂。

注意有关如何使用以下任何过滤器的更多详细示例，请查看[单元测试](https://github.com/spring-cloud/spring-cloud-gateway/tree/master/spring-cloud-gateway-core/src/test/java/org/springframework/cloud/gateway/filter/factory)。

## 113.1 AddRequestHeader GatewayFilter工厂

AddRequestHeader GatewayFilter工厂采用名称和值参数。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: add_request_header_route
        uri: https://example.org
        filters:
        - AddRequestHeader=X-Request-Foo, Bar
```



这会将`X-Request-Foo:Bar`标头添加到所有匹配请求的下游请求的标头中。

AddRequestHeader知道用于匹配路径或主机的URI变量。URI变量可用于该值，并将在运行时扩展。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: add_request_header_route
        uri: https://example.org
        predicates:
        - Path=/foo/{segment}
        filters:
        - AddRequestHeader=X-Request-Foo, Bar-{segment}
```



## 113.2 AddRequestParameter GatewayFilter工厂

AddRequestParameter GatewayFilter工厂采用名称和值参数。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: add_request_parameter_route
        uri: https://example.org
        filters:
        - AddRequestParameter=foo, bar
```



这将添加`foo=bar`到所有匹配请求的下游请求的查询字符串中。

AddRequestParameter知道用于匹配路径或主机的URI变量。URI变量可用于该值，并将在运行时扩展。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: add_request_parameter_route
        uri: https://example.org
        predicates:
        - Host: {segment}.myhost.org
        filters:
        - AddRequestParameter=foo, bar-{segment}
```



## 113.3 AddResponseHeader GatewayFilter工厂

AddResponseHeader GatewayFilter工厂采用名称和值参数。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: add_response_header_route
        uri: https://example.org
        filters:
        - AddResponseHeader=X-Response-Foo, Bar
```



这会将`X-Response-Foo:Bar`标头添加到所有匹配请求的下游响应的标头中。

AddResponseHeader知道用于匹配路径或主机的URI变量。URI变量可用于该值，并将在运行时扩展。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: add_response_header_route
        uri: https://example.org
        predicates:
        - Host: {segment}.myhost.org
        filters:
        - AddResponseHeader=foo, bar-{segment}
```



## 113.4 DedupeResponseHeader GatewayFilter工厂

DedupeResponseHeader GatewayFilter工厂采用一个`name`参数和一个可选`strategy`参数。`name`可以包含标题名称列表，以空格分隔。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: dedupe_response_header_route
        uri: https://example.org
        filters:
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin
```



如果网关CORS逻辑和下游逻辑都添加了重复的值`Access-Control-Allow-Credentials`和`Access-Control-Allow-Origin`响应标头，则这将删除它们。

DedupeResponseHeader过滤器还接受可选`strategy`参数。可接受的值为`RETAIN_FIRST`（默认值）`RETAIN_LAST`，和`RETAIN_UNIQUE`。

## 113.5 Hystrix GatewayFilter工厂

[Hystrix](https://github.com/Netflix/Hystrix)是Netflix的一个库，用于实现[断路器模式](https://martinfowler.com/bliki/CircuitBreaker.html)。Hystrix GatewayFilter允许您将断路器引入网关路由，保护您的服务免受级联故障的影响，并允许您在下游故障的情况下提供后备响应。

要在项目中启用Hystrix GatewayFilters，请`spring-cloud-starter-netflix-hystrix`从[Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/)添加依赖项。

Hystrix GatewayFilter工厂需要一个`name`参数，它是的名称`HystrixCommand`。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: hystrix_route
        uri: https://example.org
        filters:
        - Hystrix=myCommandName
```



这会将其余的过滤器包装在`HystrixCommand`带有命令名的中`myCommandName`。

Hystrix过滤器还可以接受可选`fallbackUri`参数。当前，仅`forward:`支持计划的URI。如果调用了后备，则请求将被转发到与URI相匹配的控制器。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: hystrix_route
        uri: lb://backing-service:8088
        predicates:
        - Path=/consumingserviceendpoint
        filters:
        - name: Hystrix
          args:
            name: fallbackcmd
            fallbackUri: forward:/incaseoffailureusethis
        - RewritePath=/consumingserviceendpoint, /backingserviceendpoint
```



`/incaseoffailureusethis`调用Hystrix后备时，它将转发到URI。请注意，此示例还通过`lb`目标URI 上的前缀演示了（可选）Spring Cloud Netflix Ribbon负载平衡。

主要方案是对`fallbackUri`网关应用程序中的内部控制器或处理程序使用。但是，也可以将请求重新路由到外部应用程序中的控制器或处理程序，如下所示：

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: ingredients
        uri: lb://ingredients
        predicates:
        - Path=//ingredients/**
        filters:
        - name: Hystrix
          args:
            name: fetchIngredients
            fallbackUri: forward:/fallback
      - id: ingredients-fallback
        uri: http://localhost:9994
        predicates:
        - Path=/fallback
```



在此示例中，`fallback`网关应用程序中没有终结点或处理程序，但是另一个应用程序中有一个终结点或处理程序，在下注册`http://localhost:9994`。

如果将请求转发给后备，则Hystrix网关过滤器还会提供`Throwable`引起请求的。它已`ServerWebExchange`作为 `ServerWebExchangeUtils.HYSTRIX_EXECUTION_EXCEPTION_ATTR`属性添加到，可以在网关应用程序中处理后备时使用。

对于外部控制器/处理程序方案，可以添加带有异常详细信息的标头。您可以在[FallbackHeaders GatewayFilter Factory部分中](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__gatewayfilter_factories.html#fallback-headers)找到有关它的更多信息。

Hystrix设置（例如超时）可以使用全局默认值配置，也可以使用[Hystrix Wiki](https://github.com/Netflix/Hystrix/wiki/Configuration)上说明的应用程序属性在[逐条](https://github.com/Netflix/Hystrix/wiki/Configuration)路由的基础上进行配置。

要为上述示例路由设置5秒超时，将使用以下配置：

**application.yml。** 

```properties
hystrix.command.fallbackcmd.execution.isolation.thread.timeoutInMilliseconds: 5000
```



## 113.6 FallbackHeaders GatewayFilter工厂

该`FallbackHeaders`工厂可以让你在转发到请求的头部添加猬执行异常的详细信息`fallbackUri`在以下情况下在外部应用程序，如：

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: ingredients
        uri: lb://ingredients
        predicates:
        - Path=//ingredients/**
        filters:
        - name: Hystrix
          args:
            name: fetchIngredients
            fallbackUri: forward:/fallback
      - id: ingredients-fallback
        uri: http://localhost:9994
        predicates:
        - Path=/fallback
        filters:
        - name: FallbackHeaders
          args:
            executionExceptionTypeHeaderName: Test-Header
```



在此示例中，在运行时发生执行异常后`HystrixCommand`，该请求将转发到在上`fallback`运行的应用中的端点或处理程序`localhost:9994`。具有异常类型，消息和-if available-根本原因异常类型和消息的标头将由`FallbackHeaders`过滤器添加到该请求。

通过设置下面列出的参数的值及其默认值，可以在配置中覆盖标头的名称：

- `executionExceptionTypeHeaderName`（`"Execution-Exception-Type"`）
- `executionExceptionMessageHeaderName`（`"Execution-Exception-Message"`）
- `rootCauseExceptionTypeHeaderName`（`"Root-Cause-Exception-Type"`）
- `rootCauseExceptionMessageHeaderName`（`"Root-Cause-Exception-Message"`）

您可以在[Hystrix GatewayFilter Factory部分中](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__gatewayfilter_factories.html#hystrix)找到有关Hystrix如何与Gateway一起工作的更多信息。

## 113.7 MapRequestHeader GatewayFilter工厂

MapRequestHeader GatewayFilter工厂采用'fromHeader'和'toHeader'参数。它创建一个新的命名标头（toHeader），并从传入的HTTP请求中从现有的命名标头（fromHeader）中提取值。如果输入标头不存在，则过滤器不起作用。如果新的命名标头已经存在，则其值将使用新值进行扩充。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: map_request_header_route
        uri: https://example.org
        filters:
        - MapRequestHeader=Bar, X-Request-Foo
```



这会将`X-Request-Foo:`标头添加到下游请求的标头中，其中包含来自传入的HTTP请求`Bar`标头的更新值。

## 113.8 PrefixPath GatewayFilter工厂

PrefixPath GatewayFilter工厂采用单个`prefix`参数。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: prefixpath_route
        uri: https://example.org
        filters:
        - PrefixPath=/mypath
```



这将`/mypath`作为所有匹配请求的路径的前缀。因此，对的请求`/hello`将发送给`/mypath/hello`。

## 113.9 PreserveHostHeader GatewayFilter工厂

PreserveHostHeader GatewayFilter工厂没有参数。此过滤器设置一个请求属性，路由过滤器将检查该请求属性以确定是否应发送原始主机头，而不是由HTTP客户端确定的主机头。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: preserve_host_route
        uri: https://example.org
        filters:
        - PreserveHostHeader
```



## 113.10 RequestRateLimiter GatewayFilter工厂

RequestRateLimiter GatewayFilter Factory使用一种`RateLimiter`实现来确定是否允许继续当前请求。如果不是，`HTTP 429 - Too Many Requests`则返回状态（默认）。

此过滤器采用一个可选`keyResolver`参数和特定于速率限制器的参数（请参见下文）。

`keyResolver`是实现`KeyResolver`接口的bean 。在配置中，使用SpEL按名称引用bean。`#{@myKeyResolver}`是SpEL表达式，它引用名称为的bean `myKeyResolver`。

**KeyResolver.java。** 

```java
public interface KeyResolver {
	Mono<String> resolve(ServerWebExchange exchange);
}
```



该`KeyResolver`接口允许可插拔策略派生用于限制请求的密钥。在未来的里程碑中，将有一些`KeyResolver`实现。

的默认实现`KeyResolver`是，`PrincipalNameKeyResolver`它`Principal`从`ServerWebExchange`和调用检索`Principal.getName()`。

默认情况下，如果`KeyResolver`找不到密钥，则请求将被拒绝。可以使用`spring.cloud.gateway.filter.request-rate-limiter.deny-empty-key`（true或false）和`spring.cloud.gateway.filter.request-rate-limiter.empty-key-status-code`属性来调整此行为。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 无法通过“快捷方式”符号配置RequestRateLimiter。以下示例*无效* |

**application.properties。** 

```properties
# INVALID SHORTCUT CONFIGURATION
spring.cloud.gateway.routes[0].filters[0]=RequestRateLimiter=2, 2, #{@userkeyresolver}
```



### 113.10.1 Redis RateLimiter

redis实现基于[Stripe](https://stripe.com/blog/rate-limiters)所做的工作。它需要使用`spring-boot-starter-data-redis-reactive`Spring Boot启动器。

使用的算法是[令牌桶算法](https://en.wikipedia.org/wiki/Token_bucket)。

该`redis-rate-limiter.replenishRate`是多么的每秒许多请求你希望用户被允许做，没有任何下降的请求。这是令牌桶被填充的速率。

的`redis-rate-limiter.burstCapacity`是允许用户在一个单一的第二做请求的最大数目。这是令牌桶可以容纳的令牌数。将此值设置为零将阻止所有请求。

通过在`replenishRate`和中设置相同的值，可以达到稳定的速率`burstCapacity`。设置`burstCapacity`大于可以允许临时爆发`replenishRate`。在这种情况下，速率限制器需要在突发之间间隔一段时间（根据`replenishRate`），因为2个连续的突发将导致请求丢失（`HTTP 429 - Too Many Requests`）。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: requestratelimiter_route
        uri: https://example.org
        filters:
        - name: RequestRateLimiter
          args:
            redis-rate-limiter.replenishRate: 10
            redis-rate-limiter.burstCapacity: 20
```



**Config.java。** 

```java
@Bean
KeyResolver userKeyResolver() {
    return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("user"));
}
```



这定义了每个用户10的请求速率限制。允许20个突发，但是下一秒只有10个请求可用。这`KeyResolver`是一个简单的获取`user`请求参数的参数（注意：不建议在生产中使用）。

速率限制器也可以定义为实现`RateLimiter`接口的Bean 。在配置中，使用SpEL按名称引用bean。`#{@myRateLimiter}`是SpEL表达式，它引用名称为的bean `myRateLimiter`。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: requestratelimiter_route
        uri: https://example.org
        filters:
        - name: RequestRateLimiter
          args:
            rate-limiter: "#{@myRateLimiter}"
            key-resolver: "#{@userKeyResolver}"
```



## 113.11重定向到GatewayFilter工厂

RedirectTo GatewayFilter工厂采用`status`和`url`参数。状态应该是300系列重定向http代码，例如301。URL应该是有效的URL。这将是`Location`标题的值。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: prefixpath_route
        uri: https://example.org
        filters:
        - RedirectTo=302, https://acme.org
```



这将发送带有`Location:https://acme.org`标头的状态302 以执行重定向。

## 113.12 RemoveHopByHopHeadersFilter GatewayFilter工厂

RemoveHopByHopHeadersFilter GatewayFilter工厂从转发的请求中删除标头。被删除的头的默认列表来自[IETF](https://tools.ietf.org/html/draft-ietf-httpbis-p1-messaging-14#section-7.1.3)。

**默认删除的标题是：**

- 连接
- 活着
- 代理验证
- 代理授权
- TE
- 预告片
- 传输编码
- 升级

要更改此设置，请将`spring.cloud.gateway.filter.remove-non-proxy-headers.headers`属性设置为要删除的标题名称列表。

## 113.13 RemoveRequestHeader GatewayFilter工厂

RemoveRequestHeader GatewayFilter工厂采用一个`name`参数。它是要删除的标题的名称。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: removerequestheader_route
        uri: https://example.org
        filters:
        - RemoveRequestHeader=X-Request-Foo
```



这将删除`X-Request-Foo`标头，然后再将其发送到下游。

## 113.14 RemoveResponseHeader GatewayFilter工厂

RemoveResponseHeader GatewayFilter工厂采用一个`name`参数。它是要删除的标题的名称。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: removeresponseheader_route
        uri: https://example.org
        filters:
        - RemoveResponseHeader=X-Response-Foo
```



这将从`X-Response-Foo`响应中删除标头，然后将其返回到网关客户端。

要删除任何类型的敏感标头，应为可能要配置的任何路由配置此过滤器。另外，您可以使用一次配置此过滤器，`spring.cloud.gateway.default-filters` 并将其应用于所有路由。

## 113.15 RewritePath GatewayFilter工厂

RewritePath GatewayFilter工厂采用路径`regexp`参数和`replacement`参数。这使用Java正则表达式提供了一种灵活的方式来重写请求路径。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: rewritepath_route
        uri: https://example.org
        predicates:
        - Path=/foo/**
        filters:
        - RewritePath=/foo/(?<segment>.*), /$\{segment}
```



对于的请求路径`/foo/bar`，这会将路径设置为`/bar`在发出下游请求之前。注意`$\`，`$`由于YAML规范，将替换为。

## 113.16 RewriteLocationResponseHeader GatewayFilter工厂

RewriteLocationResponseHeader GatewayFilter工厂`Location`通常会修改响应标头的值，以摆脱后端特定的详细信息。这需要`stripVersionMode`，`locationHeaderName`，`hostValue`，和`protocolsRegex`参数。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: rewritelocationresponseheader_route
        uri: http://example.org
        filters:
        - RewriteLocationResponseHeader=AS_IN_REQUEST, Location, ,
```



例如，对于请求，响应标头值将被重写为。`POST https://api.example.com/some/object/name``Location``https://object-service.prod.example.net/v2/some/object/id``https://api.example.com/some/object/id`

参数`stripVersionMode`具有以下可能的值：`NEVER_STRIP`，`AS_IN_REQUEST`（默认）`ALWAYS_STRIP`，。

- `NEVER_STRIP` -即使原始请求路径不包含任何版本，也不会剥离版本
- `AS_IN_REQUEST` -仅当原始请求路径不包含版本时，版本才会被剥离
- `ALWAYS_STRIP` -即使原始请求路径包含版本，也会删除版本

参数`hostValue`（如果提供）将用于替换`host:port`响应`Location`头的一部分。如果未提供，`Host`则将使用请求标头的值。

参数`protocolsRegex`必须是有效的regex `String`，协议名称将与该regex 匹配。如果不匹配，过滤器将不执行任何操作。默认值为`http|https|ftp|ftps`。

## 113.17 RewriteResponseHeader GatewayFilter工厂

该RewriteResponseHeader GatewayFilter厂需要`name`，`regexp`和`replacement`参数。它使用Java正则表达式以灵活的方式重写响应标头值。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: rewriteresponseheader_route
        uri: https://example.org
        filters:
        - RewriteResponseHeader=X-Response-Foo, , password=[^&]+, password=***
```



对于标头值为`/42?user=ford&password=omg!what&flag=true`，将`/42?user=ford&password=***&flag=true`在发出下游请求后将其设置为。由于YAML规范，请使用`$\`表示`$`。

## 113.18 SaveSession GatewayFilter工厂

SaveSession GatewayFilter Factory *在*向下游转发呼叫*之前*强制执行`WebSession::save`操作。这在将[Spring Session之](https://projects.spring.io/spring-session/)类的东西与惰性数据存储一起使用时特别有用，并且需要确保在进行转发呼叫之前已保存会话状态。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: save_session
        uri: https://example.org
        predicates:
        - Path=/foo/**
        filters:
        - SaveSession
```



如果您将[Spring Security](https://projects.spring.io/spring-security/)与Spring Session 集成在一起，并且想要确保安全性详细信息已转发到远程进程，那么这一点至关重要。

## 113.19 SecureHeaders GatewayFilter工厂

根据[此博客文章](https://blog.appcanary.com/2017/http-security-headers.html)的建议，SecureHeaders GatewayFilter Factory在响应中添加了许多标头。

**添加了以下标头（以及默认值）：**

- `X-Xss-Protection:1; mode=block`
- `Strict-Transport-Security:max-age=631138519`
- `X-Frame-Options:DENY`
- `X-Content-Type-Options:nosniff`
- `Referrer-Policy:no-referrer`
- `Content-Security-Policy:default-src 'self' https:; font-src 'self' https: data:; img-src 'self' https: data:; object-src 'none'; script-src https:; style-src 'self' https: 'unsafe-inline'`
- `X-Download-Options:noopen`
- `X-Permitted-Cross-Domain-Policies:none`

要更改默认值，请在`spring.cloud.gateway.filter.secure-headers`名称空间中设置适当的属性：

**要更改的属性：**

- `xss-protection-header`
- `strict-transport-security`
- `frame-options`
- `content-type-options`
- `referrer-policy`
- `content-security-policy`
- `download-options`
- `permitted-cross-domain-policies`

要禁用默认值，请将该属性设置为`spring.cloud.gateway.filter.secure-headers.disable`逗号分隔的值。

**例：** `spring.cloud.gateway.filter.secure-headers.disable=frame-options,download-options`

## 113.20 SetPath GatewayFilter工厂

SetPath GatewayFilter工厂采用路径`template`参数。通过允许路径的模板段，它提供了一种操作请求路径的简单方法。这使用了Spring Framework中的uri模板。允许多个匹配段。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: setpath_route
        uri: https://example.org
        predicates:
        - Path=/foo/{segment}
        filters:
        - SetPath=/{segment}
```



对于的请求路径`/foo/bar`，这会将路径设置为`/bar`在发出下游请求之前。

## 113.21 SetRequestHeader GatewayFilter工厂

SetRequestHeader GatewayFilter工厂采用`name`和`value`参数。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: setrequestheader_route
        uri: https://example.org
        filters:
        - SetRequestHeader=X-Request-Foo, Bar
```



该GatewayFilter用给定的名称替换所有标头，而不是添加。因此，如果下游服务器以响应`X-Request-Foo:1234`，则将替换为`X-Request-Foo:Bar`，这是下游服务将收到的内容。

SetRequestHeader知道用于匹配路径或主机的URI变量。URI变量可用于该值，并将在运行时扩展。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: setrequestheader_route
        uri: https://example.org
        predicates:
        - Host: {segment}.myhost.org
        filters:
        - SetRequestHeader=foo, bar-{segment}
```



## 113.22 SetResponseHeader GatewayFilter工厂

SetResponseHeader GatewayFilter工厂采用`name`和`value`参数。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: setresponseheader_route
        uri: https://example.org
        filters:
        - SetResponseHeader=X-Response-Foo, Bar
```



该GatewayFilter用给定的名称替换所有标头，而不是添加。因此，如果下游服务器以响应`X-Response-Foo:1234`，则将替换为`X-Response-Foo:Bar`，这是网关客户端将收到的内容。

SetResponseHeader知道用于匹配路径或主机的URI变量。URI变量可用于该值，并将在运行时扩展。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: setresponseheader_route
        uri: https://example.org
        predicates:
        - Host: {segment}.myhost.org
        filters:
        - SetResponseHeader=foo, bar-{segment}
```



## 113.23 SetStatus GatewayFilter工厂

SetStatus GatewayFilter工厂采用单个`status`参数。它必须是有效的Spring `HttpStatus`。它可以是整数值`404`或枚举的字符串表示形式`NOT_FOUND`。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: setstatusstring_route
        uri: https://example.org
        filters:
        - SetStatus=BAD_REQUEST
      - id: setstatusint_route
        uri: https://example.org
        filters:
        - SetStatus=401
```



无论哪种情况，响应的HTTP状态都将设置为401。

## 113.24 StripPrefix GatewayFilter工厂

StripPrefix GatewayFilter工厂采用一个参数`parts`。该`parts`参数指示在向下游发送请求之前，要从请求中剥离的路径中的零件数。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: nameRoot
        uri: http://nameservice
        predicates:
        - Path=/name/**
        filters:
        - StripPrefix=2
```



当通过网关提出`/name/bar/foo`请求时，发出的请求`nameservice`将看起来像`http://nameservice/foo`。

## 113.25重试GatewayFilter工厂

重试GatewayFilter工厂支持以下参数集：

- `retries`：应尝试的重试次数
- `statuses`：应重试的HTTP状态代码，用表示 `org.springframework.http.HttpStatus`
- `methods`：应重试的HTTP方法，使用表示 `org.springframework.http.HttpMethod`
- `series`：要重试的一系列状态代码，用表示 `org.springframework.http.HttpStatus.Series`
- `exceptions`：应重试引发的异常列表
- `backoff`：为重试配置了指数补偿。重试的退避间隔之后执行`firstBackoff * (factor ^ n)`，其中`n`是迭代。如果`maxBackoff`已配置，则应用的最大退避将被限制为`maxBackoff`。如果`basedOnPreviousValue`为true，将使用计算退避`prevBackoff * factor`。

`Retry`如果启用了以下默认过滤器配置：

- `retries` - 3次
- `series` — 5XX系列
- `methods` — GET方法
- `exceptions` —  `IOException`和`TimeoutException`
- `backoff` —禁用

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: retry_test
        uri: http://localhost:8080/flakey
        predicates:
        - Host=*.retry.com
        filters:
        - name: Retry
          args:
            retries: 3
            statuses: BAD_GATEWAY
            backoff:
              firstBackoff: 10ms
              maxBackoff: 50ms
              factor: 2
              basedOnPreviousValue: false
```



| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 重试过滤器当前不支持使用主体重试（例如，使用主体进行POST或PUT请求）。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 当使用带有`forward:`前缀URL 的重试过滤器时，应仔细编写目标端点，以便在发生错误的情况下不会执行任何可能导致响应发送到客户端并提交的操作。例如，如果目标端点是带注释的控制器，则目标控制器方法不应返回`ResponseEntity`错误状态码。相反，它应该抛出`Exception`或通过`Mono.error(ex)`返回值发出错误信号，例如通过返回值，可以将重试过滤器配置为通过重试来处理。 |

## 113.26 RequestSize GatewayFilter工厂

当请求大小大于允许的限制时，RequestSize GatewayFilter Factory可以限制请求到达下游服务。过滤器将`RequestSize`参数作为请求的允许大小限制（以字节为单位）。

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: request_size_route
      uri: http://localhost:8080/upload
      predicates:
      - Path=/upload
      filters:
      - name: RequestSize
        args:
          maxSize: 5000000
```



当请求由于大小而被拒绝时，RequestSize GatewayFilter工厂将响应状态设置为`413 Payload Too Large`带有其他标头`errorMessage`。以下是此类示例`errorMessage`。

```
errorMessage` ： `Request size is larger than permissible limit. Request size is 6.0 MB where permissible limit is 5.0 MB
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果未在路由定义中作为过滤器参数提供，则默认请求大小将设置为5 MB。 |

## 113.27修改请求正文GatewayFilter工厂

**该过滤器被认为是BETA，API将来可能会更改**

此过滤器可用于在网关将请求主体发送到下游之前修改请求主体。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 只能使用Java DSL配置此过滤器                                 |

```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("rewrite_request_obj", r -> r.host("*.rewriterequestobj.org")
            .filters(f -> f.prefixPath("/httpbin")
                .modifyRequestBody(String.class, Hello.class, MediaType.APPLICATION_JSON_VALUE,
                    (exchange, s) -> return Mono.just(new Hello(s.toUpperCase())))).uri(uri))
        .build();
}

static class Hello {
    String message;

    public Hello() { }

    public Hello(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

## 113.28修改响应主体GatewayFilter工厂

**该过滤器被认为是BETA，API将来可能会更改**

此过滤器可用于在将响应正文发送回客户端之前对其进行修改。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 只能使用Java DSL配置此过滤器                                 |

```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("rewrite_response_upper", r -> r.host("*.rewriteresponseupper.org")
            .filters(f -> f.prefixPath("/httpbin")
        		.modifyResponseBody(String.class, String.class,
        		    (exchange, s) -> Mono.just(s.toUpperCase()))).uri(uri)
        .build();
}
```

## 113.29默认过滤器

如果您想添加过滤器并将其应用于所有路由，则可以使用`spring.cloud.gateway.default-filters`。该属性采用过滤器列表

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      default-filters:
      - AddResponseHeader=X-Response-Default-Foo, Default-Bar
      - PrefixPath=/httpbin
```