# 18. Router and Filter: Zuul

## 18.路由器和过滤器：Zuul

路由是微服务架构不可或缺的一部分。例如，`/`可能被映射到您的Web应用程序，`/api/users`被映射到用户服务以及`/api/shop`被映射到商店服务。 [Zuul](https://github.com/Netflix/zuul)是Netflix的基于JVM的路由器和服务器端负载平衡器。

[Netflix将Zuul](https://www.slideshare.net/MikeyCohen1/edge-architecture-ieee-international-conference-on-cloud-engineering-32240146/27)用于以下[用途](https://www.slideshare.net/MikeyCohen1/edge-architecture-ieee-international-conference-on-cloud-engineering-32240146/27)：

- 认证方式
- 见解
- 压力测试
- 金丝雀测试
- 动态路由
- 服务迁移
- 减载
- 安全
- 静态响应处理
- 主动/主动流量管理

Zuul的规则引擎使规则和过滤器基本上可以用任何JVM语言编写，并具有对Java和Groovy的内置支持。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 配置属性`zuul.max.host.connections`已被替换为两个新属性，`zuul.host.maxTotalConnections`并且`zuul.host.maxPerRouteConnections`分别默认为200和20。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `ExecutionIsolationStrategy`所有路由的默认Hystrix隔离模式（）为`SEMAPHORE`。 `zuul.ribbonIsolationStrategy`可以更改`THREAD`为首选该隔离模式。 |

## 18.1如何包括Zuul

要将Zuul包括在您的项目中，请使用组ID为`org.springframework.cloud`和工件ID为的启动器`spring-cloud-starter-netflix-zuul`。有关使用当前Spring Cloud Release Train设置构建系统的详细信息，请参见[Spring Cloud Project页面](https://projects.spring.io/spring-cloud/)。

## 18.2嵌入式Zuul反向代理

Spring Cloud创建了一个嵌入式Zuul代理，以简化UI应用程序要对一个或多个后端服务进行代理调用的常见用例的开发。此功能对于用户界面代理所需的后端服务很有用，从而避免了为所有后端独立管理CORS和身份验证问题的需求。

要启用它，请使用注释Spring Boot主类`@EnableZuulProxy`。这样做会导致将本地呼叫转发到适当的服务。按照惯例，ID为的服务`users`从位于的代理`/users`（去掉前缀）接收请求。代理使用功能区来定位要通过发现转发到的实例。所有请求均在[hystrix命令](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__router_and_filter_zuul.html#hystrix-fallbacks-for-routes)中执行，因此失败会显示在Hystrix指标中。一旦电路断开，代理就不会尝试与服务联系。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| Zuul启动程序不包括发现客户端，因此，对于基于服务ID的路由，您还需要在类路径中提供其中之一（Eureka是一种选择）。 |

要跳过自动添加服务的步骤，请设置`zuul.ignored-services`为服务ID模式的列表。如果服务与被忽略但仍包含在显式配置的路由映射中的模式匹配，则将其忽略，如以下示例所示：

**application.yml。** 

```properties
 zuul:
  ignoredServices: '*'
  routes:
    users: /myusers/**
```



在上面的例子中，所有的服务都将被忽略，**除了**为`users`。

要增加或更改代理路由，可以添加外部配置，如下所示：

**application.yml。** 

```properties
 zuul:
  routes:
    users: /myusers/**
```



前面的示例意味着HTTP调用要`/myusers`转发到`users`服务（例如`/myusers/101`转发到`/101`）。

要对路由进行更细粒度的控制，可以分别指定路径和serviceId，如下所示：

**application.yml。** 

```properties
 zuul:
  routes:
    users:
      path: /myusers/**
      serviceId: users_service
```



前面的示例意味着HTTP调用将`/myusers`转发到该`users_service`服务。路由必须具有`path`可以指定为蚂蚁样式模式的，因此`/myusers/*`只能匹配一个级别，但可以`/myusers/**`分层匹配。

后端的位置可以指定为`serviceId`（用于发现服务）或`url`（物理位置），如以下示例所示：

**application.yml。** 

```properties
 zuul:
  routes:
    users:
      path: /myusers/**
      url: https://example.com/users_service
```



这些简单的url-routes不会以形式执行`HystrixCommand`，也不会使用Ribbon平衡多个URL的负载。为了实现这些目标，您可以指定一个`serviceId`带有静态服务器列表的，如下所示：

**application.yml。** 

```properties
zuul:
  routes:
    echo:
      path: /myusers/**
      serviceId: myusers-service
      stripPrefix: true

hystrix:
  command:
    myusers-service:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: ...

myusers-service:
  ribbon:
    NIWSServerListClassName: com.netflix.loadbalancer.ConfigurationBasedServerList
    listOfServers: https://example1.com,http://example2.com
    ConnectTimeout: 1000
    ReadTimeout: 3000
    MaxTotalHttpConnections: 500
    MaxConnectionsPerHost: 100
```



另一种方法是指定一个服务路由并配置一个Ribbon客户端`serviceId`（这样做需要在Ribbon中禁用Eureka支持- [有关更多信息](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-ribbon.html#spring-cloud-ribbon-without-eureka)，请参见[上文](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-ribbon.html#spring-cloud-ribbon-without-eureka)），如以下示例所示：

**application.yml。** 

```properties
zuul:
  routes:
    users:
      path: /myusers/**
      serviceId: users

ribbon:
  eureka:
    enabled: false

users:
  ribbon:
    listOfServers: example.com,google.com
```



您可以使用来提供`serviceId`和之间的约定`regexmapper`。它使用正则表达式命名组从中提取变量`serviceId`并将其注入到路由模式中，如以下示例所示：

**ApplicationConfiguration.java。** 

```java
@Bean
public PatternServiceRouteMapper serviceRouteMapper() {
    return new PatternServiceRouteMapper(
        "(?<name>^.+)-(?<version>v.+$)",
        "${version}/${name}");
}
```



上面的示例指的是`serviceId`的`myusers-v1`被映射到路线`/v1/myusers/**`。可以接受任何正则表达式，但所有命名组都必须同时存在于`servicePattern`和中`routePattern`。如果`servicePattern`与不匹配`serviceId`，则使用默认行为。在前面的示例中，将`serviceId`of `myusers`映射到“ / myusers / **”路由（未检测到版本）。默认情况下，此功能是禁用的，仅适用于发现的服务。

要为所有映射添加前缀，请设置`zuul.prefix`一个值，例如`/api`。默认情况下，代理前缀会从请求中剥离，然后再转发请求（您可以使用来关闭此行为`zuul.stripPrefix=false`）。您还可以关闭从单个路由中剥离特定于服务的前缀，如以下示例所示：

**application.yml。** 

```properties
 zuul:
  routes:
    users:
      path: /myusers/**
      stripPrefix: false
```



| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `zuul.stripPrefix`仅适用于中设置的前缀`zuul.prefix`。它对给定路由中定义的前缀没有任何影响`path`。 |

在前面的例子中，请求到`/myusers/101`转发到`/myusers/101`在`users`服务。

这些`zuul.routes`条目实际上绑定到类型的对象`ZuulProperties`。如果查看该对象的属性，则可以看到它也有一个`retryable`标志。设置该标志以`true`使功能区客户端自动重试失败的请求。您还可以将该标志设置为何`true`时需要修改使用功能区客户端配置的重试操作的参数。

默认情况下，`X-Forwarded-Host`标头被添加到转发的请求中。要关闭它，请设置`zuul.addProxyHeaders = false`。默认情况下，前缀路径被剥离，并且到后端的请求选择一个`X-Forwarded-Prefix`标头（`/myusers`在前面显示的示例中）。

如果设置默认路由（`/`），则具有的应用程序`@EnableZuulProxy`可以充当独立服务器。例如，`zuul.route.home: /`将所有流量（“ / **”）路由到“家庭”服务。

如果需要更细粒度的忽略，则可以指定要忽略的特定模式。这些模式在路线定位过程开始时进行评估，这意味着模式中应包含前缀以保证匹配。被忽略的模式跨越所有服务，并取代任何其他路由规范。以下示例显示了如何创建忽略的模式：

**application.yml。** 

```properties
 zuul:
  ignoredPatterns: /**/admin/**
  routes:
    users: /myusers/**
```



前面的示例装置，其所有的呼叫（例如`/myusers/101`）被转发到`/101`对`users`服务。但是，包括在内的呼叫`/admin/`无法解决。

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 如果您需要保留路线的顺序，则需要使用YAML文件，因为使用属性文件时顺序会丢失。以下示例显示了这样的YAML文件： |

**application.yml。** 

```properties
 zuul:
  routes:
    users:
      path: /myusers/**
    legacy:
      path: /**
```



如果要使用属性文件，则该`legacy`路径可能会终止于该`users` 路径的前面，从而导致该`users`路径不可访问。

## 18.3 Zuul Http客户端

Zuul使用的默认HTTP客户端现在由Apache HTTP客户端而不是已弃用的Ribbon支持`RestClient`。要使用`RestClient`或，分别`okhttp3.OkHttpClient`设置`ribbon.restclient.enabled=true`或`ribbon.okhttp.enabled=true`。如果要定制Apache HTTP客户端或OK HTTP客户端，请提供类型为`ClosableHttpClient`或的Bean `OkHttpClient`。

## 18.4 Cookie和敏感标题

您可以在同一系统中的服务之间共享标头，但是您可能不希望敏感标头泄漏到下游到外部服务器中。您可以在路由配置中指定忽略的标头列表。Cookies发挥着特殊的作用，因为它们在浏览器中具有定义明确的语义，并且始终将它们视为敏感内容。如果代理的使用者是浏览器，那么下游服务的cookie也会给用户带来麻烦，因为它们都混杂在一起（所有下游服务看起来都来自同一位置）。

如果您对服务的设计很谨慎（例如，如果只有一个下游服务设置cookie），则可以让它们从后端一直流到调用者。另外，如果您的代理设置cookie，并且所有后端服务都属于同一系统，则自然可以简单地共享它们（例如，使用Spring Session将它们链接到某些共享状态）。除此之外，由下游服务设置的任何cookie可能对调用者都没有用，因此建议您（至少）将`Set-Cookie`其`Cookie`放入不属于域的路由的敏感标头中。即使对于属于您网域的路由，在让Cookie在它们和代理之间流动之前，也请尝试仔细考虑其含义。

可以将敏感头配置为每个路由的逗号分隔列表，如以下示例所示：

**application.yml。** 

```properties
 zuul:
  routes:
    users:
      path: /myusers/**
      sensitiveHeaders: Cookie,Set-Cookie,Authorization
      url: https://downstream
```



| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 这是的默认值`sensitiveHeaders`，因此除非您希望它与众不同，否则无需进行设置。这是Spring Cloud Netflix 1.1中的新增功能（在1.0中，用户无法控制标题，并且所有cookie都双向流动）。 |

该`sensitiveHeaders`是一个黑名单，默认是不为空。因此，要使Zuul发送所有标头（`ignored`那些标头除外），必须将其显式设置为空列表。如果要将Cookie或授权标头传递到后端，则必须这样做。以下示例显示如何使用`sensitiveHeaders`：

**application.yml。** 

```properties
 zuul:
  routes:
    users:
      path: /myusers/**
      sensitiveHeaders:
      url: https://downstream
```



您还可以通过设置设置敏感的标题`zuul.sensitiveHeaders`。如果`sensitiveHeaders`在路径上设置了，则它将覆盖全局`sensitiveHeaders`设置。

## 18.5忽略标题

除了对路由敏感的标头之外，您还可以设置一个称为`zuul.ignoredHeaders`值的全局值（请求和响应），在与下游服务进行交互时应将其丢弃。默认情况下，如果Spring Security不在类路径中，则它们为空。否则，它们将初始化为Spring Security指定的一组众所周知的“ security ”标头（例如，涉及缓存）。在这种情况下，假设下游服务也可以添加这些标头，但是我们需要来自代理的值。要在Spring Security位于类路径上时不丢弃这些众所周知的安全标头，可以设置`zuul.ignoreSecurityHeaders`为`false`。如果您在Spring Security中禁用了HTTP Security响应标头，并且想要下游服务提供的值，则这样做很有用。

## 18.6管理端点

默认情况下，如果`@EnableZuulProxy`与Spring Boot Actuator一起使用，则启用两个附加端点：

- 路线
- 筛选器

### 18.6.1路由端点

到路由端点的GET `/routes`返回映射的路由列表：

**GET /路线。** 

```json
{
  /stores/**: "http://localhost:8081"
}
```



可以通过将`?format=details`查询字符串添加到来请求其他路线详细信息`/routes`。这样做会产生以下输出：

**获取/ routes / details。** 

```json
{
  "/stores/**": {
    "id": "stores",
    "fullPath": "/stores/**",
    "location": "http://localhost:8081",
    "path": "/**",
    "prefix": "/stores",
    "retryable": false,
    "customSensitiveHeaders": false,
    "prefixStripped": true
  }
}
```



一`POST`到`/routes`部队现有路由的更新（例如，当在服务目录进行了变更）。您可以通过设置`endpoints.routes.enabled`为禁用该端点`false`。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 路由应自动响应服务目录中的更改，但是`POST`to `/routes`是强制更改立即发生的一种方法。 |

### 18.6.2过滤器端点

`GET`到过滤器端点的A `/filters`按类型返回Zuul过滤器的映射。对于地图中的每种过滤器类型，您将获得该类型的所有过滤器的列表以及它们的详细信息。

## 18.7扼杀模式和本地转发

迁移现有应用程序或API时，常见的模式是“ 勒死 ”旧的端点，并用不同的实现方式慢慢替换它们。Zuul代理是一个有用的工具，因为您可以使用它来处理来自旧端点客户端的所有流量，但可以将某些请求重定向到新请求。

以下示例显示“ 扼杀 ”方案的配置详细信息：

**application.yml。** 

```properties
 zuul:
  routes:
    first:
      path: /first/**
      url: https://first.example.com
    second:
      path: /second/**
      url: forward:/second
    third:
      path: /third/**
      url: forward:/3rd
    legacy:
      path: /**
      url: https://legacy.example.com
```



在前面的示例中，我们扼杀了“ legacy ”应用程序，该应用程序映射到与其他模式之一不匹配的所有请求。输入的路径`/first/**`已使用外部URL提取到新服务中。`/second/**`转发路径，以便可以在本地处理它们（例如，使用常规Spring `@RequestMapping`）。中的路径`/third/**`也被转发，但是具有不同的前缀（`/third/foo`转发到`/3rd/foo`）。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 被忽略的模式不会被完全忽略，它们不会由代理处理（因此它们也会在本地有效转发）。 |

## 18.8通过Zuul上传文件

如果使用`@EnableZuulProxy`，则可以使用代理路径上载文件，只要文件很小，它就可以工作。对于大文件`DispatcherServlet`，“ / zuul / *”中有一个替代路径绕过Spring （以避免进行多部分处理）。换句话说，如果您有`zuul.routes.customers=/customers/**`，则可以将`POST`大文件添加到`/zuul/customers/*`。servlet路径通过外部化`zuul.servletPath`。如果代理路由带您通过功能区负载平衡器，则极大的文件也需要提高的超时设置，如以下示例所示：

**application.yml。** 

```properties
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 60000
ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 60000
```



请注意，为了使流处理能够处理大文件，您需要在请求中使用分块编码（某些浏览器默认情况下不这样做），如以下示例所示：

```bash
$ curl -v -H "Transfer-Encoding: chunked" \
    -F "file=@mylarge.iso" localhost:9999/zuul/simple/file
```

## 18.9查询字符串编码

在处理传入请求时，查询参数将被解码，以便可以在Zuul过滤器中进行可能的修改。然后将它们重新编码，在路由过滤器中重建后端请求。如果（例如）使用Javascript `encodeURIComponent()`方法对结果进行编码，则结果可能不同于原始输入。尽管这在大多数情况下不会引起问题，但是某些Web服务器可能对复杂查询字符串的编码很挑剔。

要强制对查询字符串进行原始编码，可以向传递一个特殊标志，`ZuulProperties`以便按此方法使用查询字符串`HttpServletRequest::getQueryString`，如以下示例所示：

**application.yml。** 

```properties
 zuul:
  forceOriginalQueryStringEncoding: true
```



| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 此特殊标志仅适用于`SimpleHostRoutingFilter`。而且，您松散了使用轻松覆盖查询参数的功能`RequestContext.getCurrentContext().setRequestQueryParams(someOverriddenParameters)`，因为现在直接在原始上获取查询字符串`HttpServletRequest`。 |

## 18.10请求URI编码

处理传入请求时，在将请求URI与路由匹配之前，先对其进行解码。然后在路由过滤器中重建后端请求时，将对请求URI进行重新编码。如果您的URI包含编码的“ /”字符，则可能导致某些意外行为。

要使用原始请求URI，可以向'ZuulProperties'传递一个特殊标志，以便该URI与该`HttpServletRequest::getRequestURI`方法一样被使用，如以下示例所示：

**application.yml。** 

```properties
 zuul:
  decodeUrl: false
```



| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果使用`requestURI`RequestContext属性覆盖请求URI，并且此标志设置为false，则不会对在请求上下文中设置的URL进行编码。确保URL已被编码是您的责任。 |

## 18.11纯嵌入式Zuul

如果使用`@EnableZuulServer`（而不是`@EnableZuulProxy`），则也可以运行Zuul服务器而无需代理或有选择地打开代理平台的某些部分。您添加到类型应用程序中的所有bean都会`ZuulFilter`自动安装（与一起使用`@EnableZuulProxy`），但是不会自动添加任何代理过滤器。

在这种情况下，仍然可以通过配置“ zuul.routes。*”来指定进入Zuul服务器的路由，但是没有服务发现也没有代理。因此，“ serviceId”和“ url”设置将被忽略。以下示例将“ / api / **”中的所有路径映射到Zuul过滤器链：

**application.yml。** 

```properties
 zuul:
  routes:
    api: /api/**
```



## 18.12禁用Zuul过滤器

Zuul for Spring Cloud随附了许多`ZuulFilter`在代理和服务器模式下默认启用的Bean。有关可启用的过滤器列表，请参见[Zuul过滤器包](https://github.com/spring-cloud/spring-cloud-netflix/tree/master/spring-cloud-netflix-zuul/src/main/java/org/springframework/cloud/netflix/zuul/filters)。如果要禁用一个，请设置`zuul...disable=true`。按照惯例，后面的包`filters`是Zuul过滤器类型。例如禁用`org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter`，设置`zuul.SendResponseFilter.post.disable=true`。

## 18.13提供路线的Hystrix后备

当Zuul中给定路线的电路跳闸时，可以通过创建type的bean提供后备响应`FallbackProvider`。在此Bean中，您需要指定回退的路由ID，并提供一个`ClientHttpResponse`作为回退的返回。以下示例显示了一个相对简单的`FallbackProvider`实现：

```java
class MyFallbackProvider implements FallbackProvider {

    @Override
    public String getRoute() {
        return "customers";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, final Throwable cause) {
        if (cause instanceof HystrixTimeoutException) {
            return response(HttpStatus.GATEWAY_TIMEOUT);
        } else {
            return response(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ClientHttpResponse response(final HttpStatus status) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return status;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return status.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return status.getReasonPhrase();
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("fallback".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
```

以下示例显示了上一个示例的路由配置可能如何显示：

```properties
zuul:
  routes:
    customers: /customers/**
```

如果您想为所有路由提供默认的后备，则可以创建一个类型为的Bean，`FallbackProvider`并使用`getRoute`return `*`或方法`null`，如以下示例所示：

```java
class MyFallbackProvider implements FallbackProvider {
    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable throwable) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200;
            }

            @Override
            public String getStatusText() throws IOException {
                return "OK";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("fallback".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
```

## 18.14 Zuul超时

如果要为通过Zuul代理的请求配置套接字超时和读取超时，则根据您的配置，有两种选择：

- 如果Zuul使用服务发现，则需要使用`ribbon.ReadTimeout`和`ribbon.SocketTimeout`功能区属性配置这些超时 。

如果通过指定URL配置了Zuul路由，则需要使用 `zuul.host.connect-timeout-millis`和`zuul.host.socket-timeout-millis`。

## 18.15重写`Location`标题

如果Zuul在Web应用程序的前面，则`Location`当Web应用程序通过HTTP状态代码重定向时，您可能需要重新编写标头`3XX`。否则，浏览器将重定向到Web应用程序的URL，而不是Zuul URL。您可以配置`LocationRewriteFilter`Zuul过滤器以将`Location`标头重写为Zuul的URL。它还添加回去的全局前缀和特定于路由的前缀。以下示例使用Spring Configuration文件添加过滤器：

```java
import org.springframework.cloud.netflix.zuul.filters.post.LocationRewriteFilter;
...

@Configuration
@EnableZuulProxy
public class ZuulConfig {
    @Bean
    public LocationRewriteFilter locationRewriteFilter() {
        return new LocationRewriteFilter();
    }
}
```

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/caution.png) | 警告 |
| ------------------------------------------------------------ | ---- |
| 小心使用此过滤器。筛选器作用于`Location`所有`3XX`响应代码的标头，这可能不适用于所有情况，例如将用户重定向到外部URL时。 |      |

## 18.16启用跨源请求

默认情况下，Zuul将所有跨源请求（CORS）路由到服务。如果您想让Zuul处理这些请求，可以通过提供自定义`WebMvcConfigurer`bean 来完成：

```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/path-1/**")
                    .allowedOrigins("https://allowed-origin.com")
                    .allowedMethods("GET", "POST");
        }
    };
}
```

在上面的示例中，我们允许`GET`和`POST`方法从`https://allowed-origin.com`将跨域请求发送到以开头的端点`path-1`。您可以使用`/**`映射将CORS配置应用于特定的路径模式，也可以全局应用于整个应用程序。您可以自定义属性：`allowedOrigins`，`allowedMethods`，`allowedHeaders`，`exposedHeaders`，`allowCredentials`并`maxAge`通过此配置。

## 18.17指标

Zuul将在执行器指标端点下提供指标，以解决路由请求时可能发生的任何故障。您可以点击来查看这些指标`/actuator/metrics`。指标将具有格式为的名称 `ZUUL::EXCEPTION:errorCause:statusCode`。

## 18.18 Zuul开发人员指南

有关Zuul的工作原理的一般概述，请参见[Zuul Wiki](https://github.com/Netflix/zuul/wiki/How-it-Works)。

### 18.18.1 Zuul Servlet

Zuul被实现为Servlet。对于一般情况，Zuul已嵌入到Spring Dispatch机制中。这使Spring MVC可以控制路由。在这种情况下，Zuul缓冲请求。如果需要不缓存请求就通过Zuul（例如，对于大文件上传），则Servlet也会安装在Spring Dispatcher的外部。默认情况下，该servlet的地址为`/zuul`。可以使用该`zuul.servlet-path`属性更改此路径。

### 18.18.2 Zuul RequestContext

要在过滤器之间传递信息，Zuul使用[`RequestContext`](https://github.com/Netflix/zuul/blob/1.x/zuul-core/src/main/java/com/netflix/zuul/context/RequestContext.java)。它的数据保存在`ThreadLocal`每个请求的特定内容中。有关在何处路由请求，错误以及实际`HttpServletRequest`和`HttpServletResponse`的信息存储在此处。在`RequestContext`扩展`ConcurrentHashMap`，所以什么都可以存储在上下文。[`FilterConstants`](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/spring-cloud-netflix-zuul/src/main/java/org/springframework/cloud/netflix/zuul/filters/support/FilterConstants.java)包含由Spring Cloud Netflix安装的过滤器使用的密钥（[稍后会](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__router_and_filter_zuul.html#zuul-developer-guide-enable-filters)详细介绍）。

### 18.18.3 `@EnableZuulProxy`与`@EnableZuulServer`

Spring Cloud Netflix安装了许多过滤器，具体取决于用于启用Zuul的注释。`@EnableZuulProxy`是的超集`@EnableZuulServer`。换句话说，`@EnableZuulProxy`包含所安装的所有过滤器`@EnableZuulServer`。“ 代理 ”中的其他过滤器启用路由功能。如果要“ 空白 ” Zuul，则应使用`@EnableZuulServer`。

### 18.18.4 `@EnableZuulServer`过滤器

`@EnableZuulServer`创建一个`SimpleRouteLocator`从Spring Boot配置文件加载路由定义的。

安装了以下过滤器（作为普通的Spring Bean）：

- 前置过滤器：
  - `ServletDetectionFilter`：检测请求是否通过Spring Dispatcher。设置键为的布尔值`FilterConstants.IS_DISPATCHER_SERVLET_REQUEST_KEY`。
  - `FormBodyWrapperFilter`：解析表单数据并为下游请求重新编码。
  - `DebugFilter`：如果`debug`设置了请求参数，则将`RequestContext.setDebugRouting()`和设置`RequestContext.setDebugRequest()`为`true`。*路由过滤器：
  - `SendForwardFilter`：通过使用Servlet转发请求`RequestDispatcher`。转发位置存储在`RequestContext`属性中`FilterConstants.FORWARD_TO_KEY`。这对于转发到当前应用程序中的端点很有用。
- 帖子过滤器：
  - `SendResponseFilter`：将代理请求的响应写入当前响应。
- 错误过滤器：
  - `SendErrorFilter`：`/error`如果`RequestContext.getThrowable()`不为null，则转发到（默认情况下）。您可以`/error`通过设置`error.path`属性来更改默认转发路径（）。

### 18.18.5 `@EnableZuulProxy`过滤器

创建一个`DiscoveryClientRouteLocator`可从`DiscoveryClient`（例如Eureka）以及从属性加载路线定义的。路线为每个创建`serviceId`从所述`DiscoveryClient`。添加新服务后，将刷新路由。

除了前面描述的过滤器之外，还安装了以下过滤器（作为普通的Spring Bean）：

- 前置过滤器：
  - `PreDecorationFilter`：根据提供的确定位置和路线`RouteLocator`。它还为下游请求设置了各种与代理相关的标头。
- 路线过滤器：
  - `RibbonRoutingFilter`：使用Ribbon，Hystrix和可插拔HTTP客户端发送请求。服务ID在`RequestContext`属性中找到`FilterConstants.SERVICE_ID_KEY`。此过滤器可以使用不同的HTTP客户端：
    - Apache `HttpClient`：默认客户端。
    - Squareup `OkHttpClient`v3：通过将`com.squareup.okhttp3:okhttp`库放在类路径上并设置来启用`ribbon.okhttp.enabled=true`。
    - Netflix Ribbon HTTP客户端：通过设置启用`ribbon.restclient.enabled=true`。该客户端具有局限性，包括不支持PATCH方法，但是还具有内置的重试功能。
  - `SimpleHostRoutingFilter`：通过Apache HttpClient将请求发送到预定的URL。网址位于中`RequestContext.getRouteHost()`。

### 18.18.6自定义Zuul过滤器示例

下面的大多数“如何编写”示例都包含在[示例Zuul过滤器](https://github.com/spring-cloud-samples/sample-zuul-filters)项目中。在该存储库中也有一些处理请求或响应正文的示例。

本节包括以下示例：

- [名为“如何编写预过滤器”的部分](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__router_and_filter_zuul.html#zuul-developer-guide-sample-pre-filter)
- [名为“如何编写路由过滤器”的部分](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__router_and_filter_zuul.html#zuul-developer-guide-sample-route-filter)
- [名为“如何编写后置过滤器”的部分](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__router_and_filter_zuul.html#zuul-developer-guide-sample-post-filter)

#### 如何编写预过滤器

前置过滤器会在中设置数据，以`RequestContext`供下游过滤器使用。主要用例是设置路由过滤器所需的信息。以下示例显示了Zuul预过滤器：

```java
public class QueryParamPreFilter extends ZuulFilter {
	@Override
	public int filterOrder() {
		return PRE_DECORATION_FILTER_ORDER - 1; // run before PreDecoration
	}

	@Override
	public String filterType() {
		return PRE_TYPE;
	}

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return !ctx.containsKey(FORWARD_TO_KEY) // a filter has already forwarded
				&& !ctx.containsKey(SERVICE_ID_KEY); // a filter has already determined serviceId
	}
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		if (request.getParameter("sample") != null) {
		    // put the serviceId in `RequestContext`
    		ctx.put(SERVICE_ID_KEY, request.getParameter("foo"));
    	}
        return null;
    }
}
```

前面的过滤器`SERVICE_ID_KEY`从`sample`request参数填充。实际上，您不应该执行这种直接映射。而是应从相反的值中查找服务ID `sample`。

现在`SERVICE_ID_KEY`已填充，将`PreDecorationFilter`无法运行`RibbonRoutingFilter`。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 如果要路由到完整URL，请致电`ctx.setRouteHost(url)`。         |

要修改路由过滤器转发到的路径，请设置`REQUEST_URI_KEY`。

#### 如何编写路由过滤器

路由过滤器在预过滤器之后运行，并向其他服务发出请求。这里的许多工作是将请求和响应数据与客户端所需的模型相互转换。以下示例显示了Zuul路由过滤器：

```java
public class OkHttpRoutingFilter extends ZuulFilter {
	@Autowired
	private ProxyRequestHelper helper;

	@Override
	public String filterType() {
		return ROUTE_TYPE;
	}

	@Override
	public int filterOrder() {
		return SIMPLE_HOST_ROUTING_FILTER_ORDER - 1;
	}

	@Override
	public boolean shouldFilter() {
		return RequestContext.getCurrentContext().getRouteHost() != null
				&& RequestContext.getCurrentContext().sendZuulResponse();
	}

    @Override
    public Object run() {
		OkHttpClient httpClient = new OkHttpClient.Builder()
				// customize
				.build();

		RequestContext context = RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();

		String method = request.getMethod();

		String uri = this.helper.buildZuulRequestURI(request);

		Headers.Builder headers = new Headers.Builder();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			Enumeration<String> values = request.getHeaders(name);

			while (values.hasMoreElements()) {
				String value = values.nextElement();
				headers.add(name, value);
			}
		}

		InputStream inputStream = request.getInputStream();

		RequestBody requestBody = null;
		if (inputStream != null && HttpMethod.permitsRequestBody(method)) {
			MediaType mediaType = null;
			if (headers.get("Content-Type") != null) {
				mediaType = MediaType.parse(headers.get("Content-Type"));
			}
			requestBody = RequestBody.create(mediaType, StreamUtils.copyToByteArray(inputStream));
		}

		Request.Builder builder = new Request.Builder()
				.headers(headers.build())
				.url(uri)
				.method(method, requestBody);

		Response response = httpClient.newCall(builder.build()).execute();

		LinkedMultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();

		for (Map.Entry<String, List<String>> entry : response.headers().toMultimap().entrySet()) {
			responseHeaders.put(entry.getKey(), entry.getValue());
		}

		this.helper.setResponse(response.code(), response.body().byteStream(),
				responseHeaders);
		context.setRouteHost(null); // prevent SimpleHostRoutingFilter from running
		return null;
    }
}
```

前面的过滤器将Servlet请求信息转换为OkHttp3请求信息，执行HTTP请求，并将OkHttp3响应信息转换为Servlet响应。

#### 如何编写帖子过滤器

后置过滤器通常操纵响应。以下过滤器将随机数添加`UUID`为`X-Sample`标题：

```java
public class AddResponseHeaderFilter extends ZuulFilter {
	@Override
	public String filterType() {
		return POST_TYPE;
	}

	@Override
	public int filterOrder() {
		return SEND_RESPONSE_FILTER_ORDER - 1;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext context = RequestContext.getCurrentContext();
    	HttpServletResponse servletResponse = context.getResponse();
		servletResponse.addHeader("X-Sample", UUID.randomUUID().toString());
		return null;
	}
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 其他操作，例如转换响应主体，则更加复杂且计算量大。           |

### 18.18.7 Zuul错误的工作方式

如果在Zuul过滤器生命周期的任何部分引发异常，则将执行错误过滤器。该`SendErrorFilter`如果只运行`RequestContext.getThrowable()`不`null`。然后，它`javax.servlet.error.*`在请求中设置特定的属性，并将请求转发到Spring Boot错误页面。

### 18.18.8 Zuul Eager应用程序上下文加载

Zuul在内部使用Ribbon来调用远程URL。默认情况下，丝带云客户端在第一次调用时由Spring Cloud延迟加载。可以使用以下配置为Zuul更改此行为，这将导致在应用程序启动时急于加载与子Ribbon相关的子应用程序上下文。以下示例显示了如何启用即时加载：

**application.yml。** 

```properties
zuul:
  ribbon:
    eager-load:
      enabled: true
```