# 23. Declarative REST Client: Feign

## 23.声明式REST客户端：伪装

[Feign](https://github.com/Netflix/feign)是声明性Web服务客户端。它使编写Web服务客户端更加容易。要使用Feign，请创建一个接口并对其进行注释。它具有可插入的注释支持，包括Feign注释和JAX-RS注释。Feign还支持可插拔编码器和解码器。Spring Cloud添加了对Spring MVC注释的支持，并支持使用`HttpMessageConverters`Spring Web中默认使用的注释。当使用Feign时，Spring Cloud集成了Ribbon和Eureka以提供负载平衡的http客户端。

## 23.1如何包含假装

要将Feign包含在您的项目中，请使用具有组`org.springframework.cloud` 和工件ID 的启动器`spring-cloud-starter-openfeign`。有关 使用当前Spring Cloud Release Train设置构建系统的详细信息，请参见[Spring Cloud Project页面](https://projects.spring.io/spring-cloud/)。

春季启动应用程序示例

```java
@SpringBootApplication
@EnableFeignClients
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

**StoreClient.java。** 

```java
@FeignClient("stores")
public interface StoreClient {
    @RequestMapping(method = RequestMethod.GET, value = "/stores")
    List<Store> getStores();

    @RequestMapping(method = RequestMethod.POST, value = "/stores/{storeId}", consumes = "application/json")
    Store update(@PathVariable("storeId") Long storeId, Store store);
}
```



在`@FeignClient`注释中，String值（上面的“ stores”）是一个任意的客户端名称，用于创建Ribbon负载均衡器（请参见[下面的Ribbon支持的详细信息](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-ribbon.html)）。您也可以使用`url`属性（绝对值或仅主机名）指定URL 。应用程序上下文中的Bean名称是接口的标准名称。要指定自己的别名值，可以使用注释的`qualifier`值`@FeignClient`。

上面的功能区客户端将要发现“商店”服务的物理地址。如果您的应用程序是Eureka客户端，则它将在Eureka服务注册表中解析该服务。如果您不想使用Eureka，则可以在外部配置中简单地配置服务器列表（[例如，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-ribbon.html#spring-cloud-ribbon-without-eureka)参见 ）。

## 23.2覆盖伪装默认值

Spring Cloud的Feign支持中的中心概念是指定客户端的概念。每个虚拟客户端都是组件集合的一部分，这些组件可以一起工作以按需联系远程服务器，并且该集合的名称是您使用`@FeignClient`注释作为应用程序开发人员提供的。Spring Cloud `ApplicationContext`使用来为每个命名客户端按需创建一个新集合 `FeignClientsConfiguration`。这包含（除其他事项外）an `feign.Decoder`，a `feign.Encoder`和a `feign.Contract`。通过使用批注的`contextId` 属性，可以覆盖该集合的名称`@FeignClient`。

春云，您可以通过声明额外的配置（在顶部取佯客户端的完全控制`FeignClientsConfiguration`使用）`@FeignClient`。例：

```java
@FeignClient(name = "stores", configuration = FooConfiguration.class)
public interface StoreClient {
    //..
}
```

在这种情况下，客户端由已包含的组件`FeignClientsConfiguration`和任何包含的组件组成`FooConfiguration`（后者将覆盖前者）。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `FooConfiguration`不需要用注释`@Configuration`。但是，如果是的话，照顾到任何排除`@ComponentScan`，否则将包括此配置，它将成为默认信号源`feign.Decoder`，`feign.Encoder`，`feign.Contract`规定当等。可以通过将其与`@ComponentScan`or `@SpringBootApplication`或放在单独的，不重叠的包中来避免这种情况，也可以在中明确排除它`@ComponentScan`。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `serviceId`现在不推荐使用该属性，而推荐使用该`name`属性。    |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 除了更改集合的名称以外，还使用注释的`contextId`属性，它将覆盖客户端名称的别名，并将其用作为该客户端创建的配置Bean名称的一部分。`@FeignClient``ApplicationContext` |

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 以前，使用`url`属性不需要`name`属性。`name`现在需要使用。    |

`name`和`url`属性中支持占位符。

```java
@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface StoreClient {
    //..
}
```

Spring Cloud Netflix默认为伪装提供以下bean（`BeanType`beanName：）`ClassName`：

- `Decoder`feignDecoder ：（`ResponseEntityDecoder`包含`SpringDecoder`）
- `Encoder` feignEncoder： `SpringEncoder`
- `Logger` feignLogger： `Slf4jLogger`
- `Contract` feignContract： `SpringMvcContract`
- `Feign.Builder` feignBuilder： `HystrixFeign.Builder`
- `Client`feignClient：如果启用了Ribbon，则为`LoadBalancerFeignClient`，否则使用默认的feign客户端。

OkHttpClient和ApacheHttpClient伪装客户端可以通过分别将`feign.okhttp.enabled`或设置`feign.httpclient.enabled`为来使用`true`，并将它们放在类路径中。您可以通过`ClosableHttpClient`在使用Apache或`OkHttpClient`使用OK HTTP 时提供Bean来定制所使用的HTTP客户端。

默认情况下，Spring Cloud Netflix *不会*为伪装提供以下bean，但仍会从应用程序上下文中查找这些类型的bean以创建伪装客户端：

- `Logger.Level`
- `Retryer`
- `ErrorDecoder`
- `Request.Options`
- `Collection`
- `SetterFactory`

创建这些类型之一的bean并将其放置在`@FeignClient`配置中（例如`FooConfiguration`上述配置），您可以覆盖所描述的每个bean。例：

```java
@Configuration
public class FooConfiguration {
    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("user", "password");
    }
}
```

替换为`SpringMvcContract`，并向的集合`feign.Contract.Default`添加。`RequestInterceptor``RequestInterceptor`

`@FeignClient` 也可以使用配置属性进行配置。

application.yml

```properties
feign:
  client:
    config:
      feignName:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: full
        errorDecoder: com.example.SimpleErrorDecoder
        retryer: com.example.SimpleRetryer
        requestInterceptors:
          - com.example.FooRequestInterceptor
          - com.example.BarRequestInterceptor
        decode404: false
        encoder: com.example.SimpleEncoder
        decoder: com.example.SimpleDecoder
        contract: com.example.SimpleContract
```

可以按照与上述类似的方式在`@EnableFeignClients`属性`defaultConfiguration`中指定默认配置。不同之处在于此配置将适用于*所有*伪客户端。

如果您希望使用配置属性来配置all `@FeignClient`，则可以使用`default`假名创建配置属性。

application.yml

```properties
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: basic
```

如果我们同时创建`@Configuration`bean和配置属性，则配置属性将获胜。它将覆盖`@Configuration`值。但是，如果要将优先级更改为`@Configuration`，则可以更改`feign.client.default-to-properties`为`false`。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果您需要使用`ThreadLocal`绑定变量`RequestInterceptor`s you will need to either set the thread isolation strategy for Hystrix to `SEMAPHORE`或在Feign中禁用Hystrix。 |

application.yml

```properties
# To disable Hystrix in Feign
feign:
  hystrix:
    enabled: false

# To set thread isolation to SEMAPHORE
hystrix:
  command:
    default:
      execution:
        isolation:
          strategy: SEMAPHORE
```

如果我们要创建多个具有相同名称或URL的伪客户端，以便它们指向同一台服务器，但每个客户端具有不同的自定义配置，则必须使用的`contextId`属性，`@FeignClient`以避免这些配置Bean发生名称冲突。

```java
@FeignClient(contextId = "fooClient", name = "stores", configuration = FooConfiguration.class)
public interface FooClient {
    //..
}

@FeignClient(contextId = "barClient", name = "stores", configuration = BarConfiguration.class)
public interface BarClient {
    //..
}
```

## 23.3手动创建假客户

在某些情况下，可能有必要使用上述方法无法实现的方式自定义Feign客户。在这种情况下，您可以使用[Feign Builder API](https://github.com/OpenFeign/feign/#basics)创建客户端 。下面是一个示例，该示例创建具有相同接口的两个Feign Client，但为每个Feign Client配置一个单独的请求拦截器。

```java
@Import(FeignClientsConfiguration.class)
class FooController {

	private FooClient fooClient;

	private FooClient adminClient;

    	@Autowired
	public FooController(Decoder decoder, Encoder encoder, Client client, Contract contract) {
		this.fooClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.contract(contract)
				.requestInterceptor(new BasicAuthRequestInterceptor("user", "user"))
				.target(FooClient.class, "http://PROD-SVC");

		this.adminClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.contract(contract)
				.requestInterceptor(new BasicAuthRequestInterceptor("admin", "admin"))
				.target(FooClient.class, "http://PROD-SVC");
    }
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 在上面的示例中，`FeignClientsConfiguration.class`是Spring Cloud Netflix提供的默认配置。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `PROD-SVC` 是客户将向其请求的服务的名称。                    |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| Feign `Contract`对象定义在接口上有效的注释和值。自动装配的`Contract`bean提供对SpringMVC注释的支持，而不是默认的Feign本机注释。 |

## 23.4 Feign Hystrix支持

如果Hystrix在classpath和上`feign.hystrix.enabled=true`，Feign将使用断路器包装所有方法。`com.netflix.hystrix.HystrixCommand`还可以返回a 。这允许您使用反应模式（与以打电话`.toObservable()`或`.observe()`或异步使用（通过调用`.queue()`）。

要基于每个客户端禁用Hystrix支持，请创建`Feign.Builder`具有“原型”范围的香草，例如：

```java
@Configuration
public class FooConfiguration {
    	@Bean
	@Scope("prototype")
	public Feign.Builder feignBuilder() {
		return Feign.builder();
	}
}
```

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 在Spring Cloud Dalston发行之前，如果Hystrix在类路径中，Feign默认会将所有方法包装在断路器中。Spring Cloud Dalston中更改了此默认行为，以支持选择加入方法。 |

## 23.5 Feign Hystrix后备

Hystrix支持回退的概念：当它们的电路断开或出现错误时执行的默认代码路径。要为给定`@FeignClient`集启用后备，该`fallback`属性应为实现后备的类名称。您还需要将实现声明为Spring bean。

```java
@FeignClient(name = "hello", fallback = HystrixClientFallback.class)
protected interface HystrixClient {
    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    Hello iFailSometimes();
}

static class HystrixClientFallback implements HystrixClient {
    @Override
    public Hello iFailSometimes() {
        return new Hello("fallback");
    }
}
```

如果需要访问引起回退触发器的原因，则可以使用`fallbackFactory`inside 的属性`@FeignClient`。

```java
@FeignClient(name = "hello", fallbackFactory = HystrixClientFallbackFactory.class)
protected interface HystrixClient {
	@RequestMapping(method = RequestMethod.GET, value = "/hello")
	Hello iFailSometimes();
}

@Component
static class HystrixClientFallbackFactory implements FallbackFactory<HystrixClient> {
	@Override
	public HystrixClient create(Throwable cause) {
		return new HystrixClient() {
			@Override
			public Hello iFailSometimes() {
				return new Hello("fallback; reason was: " + cause.getMessage());
			}
		};
	}
}
```

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| Feign中的后备实现以及Hystrix后备如何工作存在局限性。目前，返回`com.netflix.hystrix.HystrixCommand`和的方法不支持后备`rx.Observable`。 |

## 23.6假装和 `@Primary`

将Feign与Hystrix后备一起使用`ApplicationContext`时，同一类型的多个bean 。这将导致`@Autowired`无法正常工作，因为没有一个bean或一个标记为主要的bean。为了解决这个问题，Spring Cloud Netflix将所有Feign实例标记为`@Primary`，因此Spring Framework将知道要注入哪个bean。在某些情况下，这可能不是理想的。要关闭此行为，请将`primary`属性设置`@FeignClient`为false。

```java
@FeignClient(name = "hello", primary = false)
public interface HelloClient {
	// methods here
}
```

## 23.7假继承支持

Feign通过单继承接口支持样板API。这允许将常用操作分组为方便的基本接口。

**UserService.java。** 

```java
public interface UserService {

    @RequestMapping(method = RequestMethod.GET, value ="/users/{id}")
    User getUser(@PathVariable("id") long id);
}
```



**UserResource.java。** 

```java
@RestController
public class UserResource implements UserService {

}
```



**UserClient.java。** 

```java
package project.user;

@FeignClient("users")
public interface UserClient extends UserService {

}
```



| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 通常不建议在服务器和客户端之间共享接口。它引入了紧密耦合，并且实际上也不能以当前形式与Spring MVC一起使用（方法参数映射不被继承）。 |

## 23.8伪装请求/响应压缩

您可以考虑为您的Feign请求启用请求或响应GZIP压缩。您可以通过启用以下属性之一来做到这一点：

```properties
feign.compression.request.enabled=true
feign.compression.response.enabled=true
```

伪装请求压缩为您提供的设置类似于您为Web服务器设置的设置：

```properties
feign.compression.request.enabled=true
feign.compression.request.mime-types=text/xml,application/xml,application/json
feign.compression.request.min-request-size=2048
```

这些属性使您可以选择压缩媒体类型和最小请求阈值长度。

## 23.9假装伐木

为每个创建的Feign客户端创建一个记录器。默认情况下，记录器的名称是用于创建Feign客户端的接口的全类名称。伪日志仅响应该`DEBUG`级别。

**application.yml。** 

```properties
logging.level.project.user.UserClient: DEBUG
```



`Logger.Level`您可以为每个客户端配置的对象告诉Feign要记录多少。选择是：

- `NONE`，不记录（**DEFAULT**）。
- `BASIC`，仅记录请求方法和URL以及响应状态代码和执行时间。
- `HEADERS`，记录基本信息以及请求和响应标头。
- `FULL`，记录请求和响应的标题，正文和元数据。

例如，以下将将设置`Logger.Level`为`FULL`：

```java
@Configuration
public class FooConfiguration {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```

## 23.10 Feign @QueryMap支持

OpenFeign `@QueryMap`批注支持将POJO用作GET参数映射。不幸的是，默认的OpenFeign QueryMap注释与Spring不兼容，因为它缺少`value`属性。

Spring Cloud OpenFeign提供了等效的`@SpringQueryMap`注释，该注释用于将POJO或Map参数注释为查询参数映射。

例如，`Params`该类定义参数`param1`和`param2`：

```java
// Params.java
public class Params {
    private String param1;
    private String param2;

    // [Getters and setters omitted for brevity]
}
```

以下伪装客户端`Params`通过使用`@SpringQueryMap`注释使用该类：

```java
@FeignClient("demo")
public class DemoTemplate {

    @GetMapping(path = "/demo")
    String demoEndpoint(@SpringQueryMap Params params);
}
```