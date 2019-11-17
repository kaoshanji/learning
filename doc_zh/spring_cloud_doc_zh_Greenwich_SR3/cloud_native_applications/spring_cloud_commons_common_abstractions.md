# 3. Spring Cloud 公共包：公共抽象

## 3. Spring Cloud Commons：通用抽象

服务发现，负载平衡和电路断路器等模式将它们带到一个通用的抽象层，可以由所有Spring Cloud客户端使用，而与实现无关（例如，使用Eureka或Consul进行发现）。

## 3.1 @EnableDiscoveryClient

Spring Cloud Commons提供了`@EnableDiscoveryClient`注释。这将查找具有的`DiscoveryClient`接口的实现`META-INF/spring.factories`。Discovery Client的实现将一个配置类添加到`spring.factories`该`org.springframework.cloud.client.discovery.EnableDiscoveryClient`键下。`DiscoveryClient`实现示例包括[Spring Cloud Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/)，[Spring Cloud Consul Discovery](https://cloud.spring.io/spring-cloud-consul/)和[Spring Cloud Zookeeper Discovery](https://cloud.spring.io/spring-cloud-zookeeper/)。

默认情况下，`DiscoveryClient`将本地Spring Boot服务器自动注册到远程发现服务器的实现。可以通过`autoRegister=false`在中设置禁用此行为`@EnableDiscoveryClient`。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `@EnableDiscoveryClient`不再需要。您可以将`DiscoveryClient`实现放在类路径上，以使Spring Boot应用程序向服务发现服务器注册。 |

### 3.1.1健康指标

Commons创建了一个Spring Boot `HealthIndicator`，`DiscoveryClient`实现可以通过实现来参与`DiscoveryHealthIndicator`。要禁用复合`HealthIndicator`，请设置`spring.cloud.discovery.client.composite-indicator.enabled=false`。自动配置了通用`HealthIndicator`依据。要禁用它，请设置。要禁用的描述字段，请设置。否则，它可以冒泡作为的卷起。`DiscoveryClient``DiscoveryClientHealthIndicator``spring.cloud.discovery.client.health-indicator.enabled=false``DiscoveryClientHealthIndicator``spring.cloud.discovery.client.health-indicator.include-description=false``description``HealthIndicator`

### 3.1.2订购`DiscoveryClient`实例

`DiscoveryClient`接口扩展`Ordered`。当使用多个发现客户端时，这很有用，因为它允许您定义返回的发现客户端的顺序，类似于如何订购Spring应用程序加载的bean。默认情况下，any的顺序`DiscoveryClient`设置为 `0`。如果要为自定义`DiscoveryClient`实现设置不同的顺序，则只需要重写该`getOrder()`方法，以便它返回适合您的设置的值。除了这个，你可以使用属性来设置的顺序`DiscoveryClient` 由Spring提供的云计算等等的实现`ConsulDiscoveryClient`，`EurekaDiscoveryClient`和 `ZookeeperDiscoveryClient`。为此，您只需将 `spring.cloud.{clientIdentifier}.discovery.order`（或`eureka.client.order`Eureka）属性设置为所需值。

## 3.2服务注册

Commons现在提供一个`ServiceRegistry`接口，该接口提供诸如`register(Registration)`和的方法`deregister(Registration)`，这些方法使您可以提供自定义的注册服务。 `Registration`是标记界面。

以下示例显示了`ServiceRegistry`正在使用的：

```java
@Configuration
@EnableDiscoveryClient(autoRegister=false)
public class MyConfiguration {
    private ServiceRegistry registry;

    public MyConfiguration(ServiceRegistry registry) {
        this.registry = registry;
    }

    // called through some external process, such as an event or a custom actuator endpoint
    public void register() {
        Registration registration = constructRegistration();
        this.registry.register(registration);
    }
}
```

每个`ServiceRegistry`实现都有自己的`Registry`实现。

- `ZookeeperRegistration` 用于 `ZookeeperServiceRegistry`
- `EurekaRegistration` 用于 `EurekaServiceRegistry`
- `ConsulRegistration` 用于 `ConsulServiceRegistry`

如果使用的是`ServiceRegistry`接口，则将需要为使用`Registry`的`ServiceRegistry`实现传递正确的实现。

### 3.2.1 ServiceRegistry自动注册

默认情况下，`ServiceRegistry`实现会自动注册正在运行的服务。要禁用该行为，您可以设置：* `@EnableDiscoveryClient(autoRegister=false)`永久禁用自动注册。* `spring.cloud.service-registry.auto-registration.enabled=false`通过配置禁用行为。

#### ServiceRegistry自动注册事件

服务自动注册时将触发两个事件。在`InstancePreRegisteredEvent`注册服务之前会触发名为的第一个事件 。`InstanceRegisteredEvent`注册服务后会触发第二个事件，称为。您可以注册一个 `ApplicationListener`或多个以收听和响应这些事件。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果`spring.cloud.service-registry.auto-registration.enabled`将设置为，则不会触发这些事件`false`。 |

### 3.2.2服务注册表执行器端点

Spring Cloud Commons提供了一个`/service-registry`执行器端点。该端点依赖于`Registration`Spring Application Context中的bean。`/service-registry`使用GET 调用会返回的状态`Registration`。对具有JSON正文的同一端点使用POST会将当前状态更改`Registration`为新值。JSON正文必须包含`status`具有首选值的字段。请`ServiceRegistry`在更新状态时查看用于允许值的实现文档，并为状态返回值。例如，尤里卡支持的状态是`UP`，`DOWN`，`OUT_OF_SERVICE`，和`UNKNOWN`。

## 3.3 Spring RestTemplate作为负载均衡器客户端

`RestTemplate`可以自动配置为在后台使用负载均衡器客户端。要创建负载均衡的`RestTemplate`，请创建一个`RestTemplate` `@Bean`并使用`@LoadBalanced`限定符，如以下示例所示：

```java
@Configuration
public class MyConfiguration {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

public class MyClass {
    @Autowired
    private RestTemplate restTemplate;

    public String doOtherStuff() {
        String results = restTemplate.getForObject("http://stores/stores", String.class);
        return results;
    }
}
```

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/caution.png) | 警告 |
| ------------------------------------------------------------ | ---- |
| 一`RestTemplate`豆不再通过自动配置创建。各个应用程序必须创建它。 |      |

URI需要使用虚拟主机名（即服务名，而不是主机名）。功能区客户端用于创建完整的物理地址。有关如何设置的详细信息，请参见[RibbonAutoConfiguration](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/spring-cloud-netflix-ribbon/src/main/java/org/springframework/cloud/netflix/ribbon/RibbonAutoConfiguration.java)`RestTemplate`。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 为了使用负载均衡`RestTemplate`，您需要在类路径中有一个负载均衡器实现。推荐的实现是`BlockingLoadBalancerClient` -添加`org.springframework.cloud:spring-cloud-loadbalancer`以使用它。该 `RibbonLoadBalancerClient`也可以使用，但现在正在维修它，我们不建议将其添加到新的项目。 |      |

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 如果要使用`BlockingLoadBalancerClient`，请确保您没有 `RibbonLoadBalancerClient`在项目类路径中，因为出于向后兼容的原因，默认情况下将使用它。 |

## 3.4 Spring WebClient作为负载均衡器客户端

`WebClient`可以自动配置为使用负载平衡器客户端。要创建负载均衡的`WebClient`，请创建一个`WebClient.Builder` `@Bean`并使用`@LoadBalanced`限定符，如以下示例所示：

```java
@Configuration
public class MyConfiguration {

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}
}

public class MyClass {
    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<String> doOtherStuff() {
        return webClientBuilder.build().get().uri("http://stores/stores")
        				.retrieve().bodyToMono(String.class);
    }
}
```

URI需要使用虚拟主机名（即服务名，而不是主机名）。功能区客户端用于创建完整的物理地址。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 如果要使用`@LoadBalanced WebClient.Builder`，则需要在类路径中有一个loadbalancer实现。建议您将`org.springframework.cloud:spring-cloud-loadbalancer`依赖项添加 到项目中。然后，`ReactiveLoadBalancer`将在下面使用。或者，此功能也可以与spring-cloud-starter-netflix-ribbon一起使用，但是该请求将由后台的非反应式处理`LoadBalancerClient`。此外，spring-cloud-starter-netflix-ribbon已经处于维护模式，因此我们不建议您将其添加到新项目中。 |      |

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 在`ReactorLoadBalancer`下面使用的支持缓存。如果`cacheManager`检测到，`ServiceInstanceSupplier`将使用的缓存版本。如果没有，我们将从发现服务中检索实例而不对其进行缓存。如果您使用，建议您在项目中[启用缓存](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html)`ReactiveLoadBalancer`。 |

### 3.4.1重试失败的请求

`RestTemplate`可以配置负载平衡以重试失败的请求。默认情况下，禁用此逻辑。您可以通过将[Spring Retry](https://github.com/spring-projects/spring-retry)添加到应用程序的类路径来启用它。负载平衡`RestTemplate`遵循与重试失败请求有关的某些功能区配置值。您可以使用`client.ribbon.MaxAutoRetries`，`client.ribbon.MaxAutoRetriesNextServer`和`client.ribbon.OkToRetryOnAllOperations`性能。如果要在类路径上使用Spring Retry禁用重试逻辑，可以设置`spring.cloud.loadbalancer.retry.enabled=false`。有关这些属性的说明，请参见[功能区文档](https://github.com/Netflix/ribbon/wiki/Getting-Started#the-properties-file-sample-clientproperties)。

如果您想`BackOffPolicy`在重试中实现a ，则需要创建一个类型为bean的bean `LoadBalancedRetryFactory`并重写该`createBackOffPolicy`方法：

```java
@Configuration
public class MyConfiguration {
    @Bean
    LoadBalancedRetryFactory retryFactory() {
        return new LoadBalancedRetryFactory() {
            @Override
            public BackOffPolicy createBackOffPolicy(String service) {
        		return new ExponentialBackOffPolicy();
        	}
        };
    }
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `client` 在上述示例中，应将其替换为Ribbon客户的名称。        |

如果要向`RetryListener`重试功能添加一个或多个实现，则需要创建一个类型的bean `LoadBalancedRetryListenerFactory`并返回`RetryListener`要用于给定服务的数组，如以下示例所示：

```java
@Configuration
public class MyConfiguration {
    @Bean
    LoadBalancedRetryListenerFactory retryListenerFactory() {
        return new LoadBalancedRetryListenerFactory() {
            @Override
            public RetryListener[] createRetryListeners(String service) {
                return new RetryListener[]{new RetryListener() {
                    @Override
                    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                        //TODO Do you business...
                        return true;
                    }

                    @Override
                     public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                        //TODO Do you business...
                    }

                    @Override
                    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                        //TODO Do you business...
                    }
                }};
            }
        };
    }
}
```

## 3.5多个RestTemplate对象

如果您想要一个`RestTemplate`没有负载平衡的，创建一个`RestTemplate`bean并注入它。要访问负载均衡的`RestTemplate`，请`@LoadBalanced`在创建时使用限定符`@Bean`，如以下示例所示：\

```java
@Configuration
public class MyConfiguration {

    @LoadBalanced
    @Bean
    RestTemplate loadBalanced() {
        return new RestTemplate();
    }

    @Primary
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

public class MyClass {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @LoadBalanced
    private RestTemplate loadBalanced;

    public String doOtherStuff() {
        return loadBalanced.getForObject("http://stores/stores", String.class);
    }

    public String doStuff() {
        return restTemplate.getForObject("https://example.com", String.class);
    }
}
```

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 请注意，在前面的示例中，`@Primary`在普通`RestTemplate`声明上使用了注释，以消除不合格的`@Autowired`注入的歧义。 |      |

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 如果看到诸如此类的错误`java.lang.IllegalArgumentException: Can not set org.springframework.web.client.RestTemplate field com.my.app.Foo.restTemplate to com.sun.proxy.$Proxy89`，请尝试注入`RestOperations`或设置`spring.aop.proxyTargetClass=true`。 |

## 3.6 Spring WebFlux WebClient作为负载均衡器客户端

### 3.6.1带有响应式负载均衡器的Spring WebFlux WebClient

`WebClient`可以配置为使用`ReactiveLoadBalancer`。如果添加`org.springframework.cloud:spring-cloud-loadbalancer`到项目中， `ReactorLoadBalancerExchangeFilterFunction`则`spring-webflux`在类路径中自动配置。以下示例说明如何配置以`WebClient`在后台使用无功负载均衡器：

```java
public class MyClass {
    @Autowired
    private ReactorLoadBalancerExchangeFilterFunction lbFunction;

    public Mono<String> doOtherStuff() {
        return WebClient.builder().baseUrl("http://stores")
            .filter(lbFunction)
            .build()
            .get()
            .uri("/stores")
            .retrieve()
            .bodyToMono(String.class);
    }
}
```

URI需要使用虚拟主机名（即服务名，而不是主机名）。将`ReactorLoadBalancerClient`用于创建一个完整的物理地址。

### 3.6.2具有非反应式负载均衡器客户端的Spring WebFlux WebClient

如果您没有`org.springframework.cloud:spring-cloud-loadbalancer`项目，但是有spring-cloud-starter-netflix-ribbon，则仍然可以`WebClient`与一起使用`LoadBalancerClient`。`LoadBalancerExchangeFilterFunction` 如果`spring-webflux`在类路径上，则将自动配置。但是请注意，这是在后台使用非反应性客户端。以下示例显示如何配置一个`WebClient`以使用负载均衡器：

```java
public class MyClass {
    @Autowired
    private LoadBalancerExchangeFilterFunction lbFunction;

    public Mono<String> doOtherStuff() {
        return WebClient.builder().baseUrl("http://stores")
            .filter(lbFunction)
            .build()
            .get()
            .uri("/stores")
            .retrieve()
            .bodyToMono(String.class);
    }
}
```

URI需要使用虚拟主机名（即服务名，而不是主机名）。将`LoadBalancerClient`用于创建一个完整的物理地址。

警告：现在不建议使用此方法。我们建议您将[WebFlux与电抗性负载平衡器一起](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_commons_common_abstractions.html#webflux-with-reactive-loadbalancer) 使用。

### 3.6.3传递自己的负载均衡客户端配置

您还可以使用`@LoadBalancerClient`注释传递您自己的负载平衡器客户端配置，传递负载平衡器客户端的名称和配置类，如下所示：

```java
@Configuration
@LoadBalancerClient(value = "stores", configuration = StoresLoadBalancerClientConfiguration.class)
public class MyConfiguration {

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}
}
```

也可以通过`@LoadBalancerClients`注释将多个配置（对于一个以上的负载均衡器客户端）一起传递，如下所示：

```java
@Configuration
@LoadBalancerClients({@LoadBalancerClient(value = "stores", configuration = StoresLoadBalancerClientConfiguration.class), @LoadBalancerClient(value = "customers", configuration = CustomersLoadBalancerClientConfiguration.class)})
public class MyConfiguration {

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}
}
```

## 3.7忽略网络接口

有时，忽略某些命名的网络接口很有用，以便可以将它们从服务发现注册中排除（例如，在Docker容器中运行时）。可以设置正则表达式列表以使所需的网络接口被忽略。以下配置将忽略该`docker0`接口以及所有以开头的接口`veth`：

**application.yml。** 

```properties
spring:
  cloud:
    inetutils:
      ignoredInterfaces:
        - docker0
        - veth.*
```



您还可以通过使用正则表达式列表来强制仅使用指定的网络地址，如以下示例所示：

**bootstrap.yml。** 

```properties
spring:
  cloud:
    inetutils:
      preferredNetworks:
        - 192.168
        - 10.0
```



您也可以只使用站点本地地址，如以下示例所示：.application.yml

```properties
spring:
  cloud:
    inetutils:
      useOnlySiteLocalInterfaces: true
```

有关构成站点本地地址的详细信息，请参见[Inet4Address.html.isSiteLocalAddress（）](https://docs.oracle.com/javase/8/docs/api/java/net/Inet4Address.html#isSiteLocalAddress--)。

## 3.8 HTTP客户端工厂

Spring Cloud Commons提供了用于创建Apache HTTP客户端（`ApacheHttpClientFactory`）和OK HTTP客户端（`OkHttpClientFactory`）的bean 。该`OkHttpClientFactory`豆创建只有在确定HTTP罐子在classpath。另外，Spring Cloud Commons提供了用于创建两个客户端都使用的连接管理器的bean：`ApacheHttpClientConnectionManagerFactory`Apache HTTP客户端和`OkHttpClientConnectionPoolFactory`OK HTTP客户端。如果要自定义在下游项目中创建HTTP客户端的方式，则可以提供自己的这些Bean实现。另外，如果您提供类型为`HttpClientBuilder`或的Bean `OkHttpClient.Builder`，那么默认工厂将使用这些构建器作为返回到下游项目的构建器的基础。您还可以通过将`spring.cloud.httpclientfactories.apache.enabled`或设置`spring.cloud.httpclientfactories.ok.enabled`为来禁用这些Bean的创建`false`。

## 3.9启用的功能

Spring Cloud Commons提供了一个`/features`执行器端点。该端点返回类路径上可用的功能以及是否已启用它们。返回的信息包括功能类型，名称，版本和供应商。

### 3.9.1特征类型

“功能”有两种类型：抽象和命名。

抽象特征是其中的接口或抽象类定义的特性和一个实现的创造，例如`DiscoveryClient`，`LoadBalancerClient`，或`LockService`。抽象类或接口用于在上下文中查找该类型的Bean。显示的版本是`bean.getClass().getPackage().getImplementationVersion()`。

命名功能是没有实现的特定类的功能，例如“ Circuit Breaker”，“ API Gateway”，“ Spring Cloud Bus”等。这些功能需要名称和Bean类型。

### 3.9.2声明功能

任何模块都可以声明任意数量的`HasFeature`bean，如以下示例所示：

```java
@Bean
public HasFeatures commonsFeatures() {
  return HasFeatures.abstractFeatures(DiscoveryClient.class, LoadBalancerClient.class);
}

@Bean
public HasFeatures consulFeatures() {
  return HasFeatures.namedFeatures(
    new NamedFeature("Spring Cloud Bus", ConsulBusAutoConfiguration.class),
    new NamedFeature("Circuit Breaker", HystrixCommandAspect.class));
}

@Bean
HasFeatures localFeatures() {
  return HasFeatures.builder()
      .abstractFeature(Foo.class)
      .namedFeature(new NamedFeature("Bar Feature", Bar.class))
      .abstractFeature(Baz.class)
      .build();
}
```

这些豆中的每一个都应该装进适当的保护层`@Configuration`。

## 3.10 Spring Cloud兼容性验证

由于某些用户在设置Spring Cloud应用程序时遇到问题，我们决定添加兼容性验证机制。如果您当前的设置与Spring Cloud要求不兼容，它会中断，并附上一份报告，显示出确切的问题。

目前，我们验证将哪个版本的Spring Boot添加到您的类路径中。

报告范例

```bash
***************************
APPLICATION FAILED TO START
***************************

Description:

Your project setup is incompatible with our requirements due to following reasons:

- Spring Boot [2.1.0.RELEASE] is not compatible with this Spring Cloud release train


Action:

Consider applying the following actions:

- Change Spring Boot version to one of the following versions [1.2.x, 1.3.x] .
You can find the latest Spring Boot versions here [https://spring.io/projects/spring-boot#learn].
If you want to learn more about the Spring Cloud Release train compatibility, you can visit this page [https://spring.io/projects/spring-cloud#overview] and check the [Release Trains] section.
```

为了禁用此功能，请设置`spring.cloud.compatibility-verifier.enabled`为`false`。如果要覆盖兼容的Spring Boot版本，只需`spring.cloud.compatibility-verifier.compatible-boot-versions`用逗号分隔的兼容Spring Boot版本列表设置 属性。