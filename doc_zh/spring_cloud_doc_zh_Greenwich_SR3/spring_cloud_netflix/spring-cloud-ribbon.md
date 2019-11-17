# 16. Client Side Load Balancer: Ribbon

## 16.客户端负载均衡器：功能区

Ribbon是客户端负载均衡器，可让您对HTTP和TCP客户端的行为进行大量控制。Feign已经使用了Ribbon，因此，如果使用`@FeignClient`，则此部分也适用。

Ribbon中的中心概念是指定客户端的概念。每个负载平衡器都是组件的一部分，这些组件可以一起工作以按需联系远程服务器，并且该组件具有您作为应用程序开发人员提供的名称（例如，通过使用`@FeignClient`批注）。根据需要，Spring Cloud `ApplicationContext`通过使用为每个命名客户端 创建一个新的集合`RibbonClientConfiguration`。这包含（除其他事项外）an `ILoadBalancer`，a `RestClient`和a `ServerListFilter`。

## 16.1如何包括功能区

要将Ribbon包含在项目中，请使用组ID为`org.springframework.cloud`和工件ID为的启动器`spring-cloud-starter-netflix-ribbon`。有关使用当前Spring Cloud Release Train设置构建系统的详细信息，请参见[Spring Cloud Project页面](https://projects.spring.io/spring-cloud/)。

## 16.2自定义功能区客户端

您可以使用中的外部属性来配置Ribbon客户端的某些位`.ribbon.*`，这与本地使用Netflix API相似，不同之处在于可以使用Spring Boot配置文件。可以将本机选项检查为[`CommonClientConfigKey`](https://github.com/Netflix/ribbon/blob/master/ribbon-core/src/main/java/com/netflix/client/config/CommonClientConfigKey.java)（功能区核心的一部分）中的静态字段。

Spring Cloud还允许您通过使用声明其他配置（位于之上`RibbonClientConfiguration`）来完全控制客户端`@RibbonClient`，如以下示例所示：

```java
@Configuration
@RibbonClient(name = "custom", configuration = CustomConfiguration.class)
public class TestConfiguration {
}
```

在这种情况下，客户端由in中的组件`RibbonClientConfiguration`以及in中的组件组成`CustomConfiguration`（其中后者通常会覆盖前者）。

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 该`CustomConfiguration`CLAS必须是`@Configuration`一流的，但照顾，这是不是在`@ComponentScan`主应用程序上下文。否则，将由所有共享`@RibbonClients`。如果您使用`@ComponentScan`（或`@SpringBootApplication`），则需要采取措施避免将其包括在内（例如，您可以将其放在单独的，不重叠的程序包中，或在中指定要扫描的程序包`@ComponentScan`）。 |

下表显示了Spring Cloud Netflix默认为Ribbon提供的bean：

| 豆类型              | 豆名                      | 班级名称                         |
| ------------------- | ------------------------- | -------------------------------- |
| `IClientConfig`     | `ribbonClientConfig`      | `DefaultClientConfigImpl`        |
| `IRule`             | `ribbonRule`              | `ZoneAvoidanceRule`              |
| `IPing`             | `ribbonPing`              | `DummyPing`                      |
| `ServerList`        | `ribbonServerList`        | `ConfigurationBasedServerList`   |
| `ServerListFilter`  | `ribbonServerListFilter`  | `ZonePreferenceServerListFilter` |
| `ILoadBalancer`     | `ribbonLoadBalancer`      | `ZoneAwareLoadBalancer`          |
| `ServerListUpdater` | `ribbonServerListUpdater` | `PollingServerListUpdater`       |

创建这些类型之一的Bean并将其放置在`@RibbonClient`配置中（例如`FooConfiguration`上述配置），您可以覆盖所描述的每个Bean，如以下示例所示：

```java
@Configuration
protected static class FooConfiguration {

	@Bean
	public ZonePreferenceServerListFilter serverListFilter() {
		ZonePreferenceServerListFilter filter = new ZonePreferenceServerListFilter();
		filter.setZone("myTestZone");
		return filter;
	}

	@Bean
	public IPing ribbonPing() {
		return new PingUrl();
	}

}
```

前面示例中的include语句替换`NoOpPing`为`PingUrl`并提供了一个custom `serverListFilter`。

## 16.3为所有功能区客户端自定义默认值

通过使用`@RibbonClients`注释并注册默认配置，可以为所有Ribbon客户提供默认配置，如以下示例所示：

```java
@RibbonClients(defaultConfiguration = DefaultRibbonConfig.class)
public class RibbonClientDefaultConfigurationTestsConfig {

	public static class BazServiceList extends ConfigurationBasedServerList {

		public BazServiceList(IClientConfig config) {
			super.initWithNiwsConfig(config);
		}

	}

}

@Configuration
class DefaultRibbonConfig {

	@Bean
	public IRule ribbonRule() {
		return new BestAvailableRule();
	}

	@Bean
	public IPing ribbonPing() {
		return new PingUrl();
	}

	@Bean
	public ServerList<Server> ribbonServerList(IClientConfig config) {
		return new RibbonClientDefaultConfigurationTestsConfig.BazServiceList(config);
	}

	@Bean
	public ServerListSubsetFilter serverListFilter() {
		ServerListSubsetFilter filter = new ServerListSubsetFilter();
		return filter;
	}

}
```

## 16.4通过设置属性来自定义功能区客户端

从1.2.0版开始，Spring Cloud Netflix现在通过将属性设置为与[Ribbon文档](https://github.com/Netflix/ribbon/wiki/Working-with-load-balancers#components-of-load-balancer)兼容来支持自定义Ribbon客户端。

这使您可以在启动时在不同环境中更改行为。

以下列表显示了受支持的属性>：

- `.ribbon.NFLoadBalancerClassName`：应实施 `ILoadBalancer`
- `.ribbon.NFLoadBalancerRuleClassName`：应实施 `IRule`
- `.ribbon.NFLoadBalancerPingClassName`：应实施 `IPing`
- `.ribbon.NIWSServerListClassName`：应实施 `ServerList`
- `.ribbon.NIWSServerListFilterClassName`：应实施 `ServerListFilter`

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 这些属性中定义的类优先于通过使用定义的bean `@RibbonClient(configuration=MyRibbonConfig.class)`和Spring Cloud Netflix提供的默认值。 |

要`IRule`为名为的服务名称`users`设置，您可以设置以下属性：

**application.yml。** 

```properties
users:
  ribbon:
    NIWSServerListClassName: com.netflix.loadbalancer.ConfigurationBasedServerList
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule
```



有关[功能区](https://github.com/Netflix/ribbon/wiki/Working-with-load-balancers)提供的实现，请参见[功能区文档](https://github.com/Netflix/ribbon/wiki/Working-with-load-balancers)。

## 16.5将功能区与Eureka一起使用

当Eureka与Ribbon结合使用时（也就是说，两者都在类路径上），`ribbonServerList`会被扩展名覆盖，该扩展名`DiscoveryEnabledNIWSServerList`会填充Eureka中的服务器列表。它还将替换为`IPing`接口`NIWSDiscoveryPing`，该接口委托Eureka确定服务器是否启动。该`ServerList`由默认安装的是`DomainExtractingServerList`。其目的是使元数据对负载均衡器可用，而无需使用AWS AMI元数据（这是Netflix依赖的）。默认情况下，服务器列表是使用实例元数据中提供的“ zone ”信息构建的（因此，在远程客户端上为set `eureka.instance.metadataMap.zone`）。如果缺少，并且`approximateZoneFromHostname`如果设置了flag，它可以使用服务器主机名中的域名作为区域的代理。一旦区域信息可用，就可以在中使用它`ServerListFilter`。默认情况下，它用于在与客户端相同的区域中定位服务器，因为默认值为a `ZonePreferenceServerListFilter`。默认情况下，以与远程实例相同的方式（即，通过`eureka.instance.metadataMap.zone`）确定客户端的区域。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 设置客户端区域的传统“ archaius ”方法是通过名为“ @zone”的配置属性。如果可用，Spring Cloud会优先使用所有其他设置（请注意，密钥必须在YAML配置中用引号引起来）。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果没有其他区域数据源，则根据客户端配置（而不是实例配置）进行猜测。我们采用`eureka.client.availabilityZones`，这是从区域名称到区域列表的地图，并为实例自己的区域拉出第一个区域（即`eureka.client.region`，默认为“ us-east-1”，以与本机Netflix兼容） 。 |

## 16.6示例：如何在没有尤里卡的情况下使用色带

Eureka是一种抽象发现远程服务器的便捷方法，因此您不必在客户端中对它们的URL进行硬编码。但是，如果您不想使用Eureka，Ribbon和Feign也可以使用。假设您`@RibbonClient`为“ stores” 声明了a ，并且Eureka未被使用（甚至不在类路径上）。功能区客户端默认为配置的服务器列表。您可以提供以下配置：

**application.yml。** 

```properties
stores:
  ribbon:
    listOfServers: example.com,google.com
```



## 16.7示例：禁用功能区中的尤里卡使用

将该`ribbon.eureka.enabled`属性设置为`false`显式禁用在功能区中使用Eureka，如以下示例所示：

**application.yml。** 

```properties
ribbon:
  eureka:
   enabled: false
```



## 16.8直接使用Ribbon API

您也可以`LoadBalancerClient`直接使用direct，如以下示例所示：

```java
public class MyClass {
    @Autowired
    private LoadBalancerClient loadBalancer;

    public void doStuff() {
        ServiceInstance instance = loadBalancer.choose("stores");
        URI storesUri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));
        // ... do something with the URI
    }
}
```

## 16.9缓存功能区配置

每个名为Ribbon的客户端都有一个Spring Cloud维护的相应子应用程序上下文。此应用程序上下文在对命名客户端的第一个请求上延迟加载。通过指定功能区客户端的名称，可以更改此延迟加载行为，以代替在启动时急于加载这些子应用程序上下文，如以下示例所示：

**application.yml。** 

```properties
ribbon:
  eager-load:
    enabled: true
    clients: client1, client2, client3
```



## 16.10如何配置Hystrix线程池

如果更改`zuul.ribbonIsolationStrategy`为`THREAD`，则所有路由均使用Hystrix的线程隔离策略。在这种情况下，`HystrixThreadPoolKey`设置`RibbonCommand`为默认值。这意味着所有路由的HystrixCommands在同一个Hystrix线程池中执行。可以使用以下配置更改此行为：

**application.yml。** 

```properties
zuul:
  threadPool:
    useSeparateThreadPools: true
```



前面的示例导致在Hystrix线程池中为每个路由执行HystrixCommands。

在这种情况下，默认值`HystrixThreadPoolKey`与每个路由的服务ID相同。要向添加前缀`HystrixThreadPoolKey`，请设置`zuul.threadPool.threadPoolKeyPrefix`为要添加的值，如以下示例所示：

**application.yml。** 

```properties
zuul:
  threadPool:
    useSeparateThreadPools: true
    threadPoolKeyPrefix: zuulgw
```



## 16.11如何提供功能区的密钥 `IRule`

如果您需要提供自己的`IRule`实现来处理特殊的路由要求（例如“ canary ”测试），请将一些信息传递给的`choose`方法`IRule`。

**com.netflix.loadbalancer.IRule.java。** 

```java
public interface IRule{
    public Server choose(Object key);
         :
```



您可以提供一些信息，供您的`IRule`实现用于选择目标服务器，如以下示例所示：

```java
RequestContext.getCurrentContext()
              .set(FilterConstants.LOAD_BALANCER_KEY, "canary-test");
```

如果您`RequestContext`使用键将任何对象放入`FilterConstants.LOAD_BALANCER_KEY`，则会将其传递给实现的`choose`方法`IRule`。前面示例中显示的代码必须在执行之前`RibbonRoutingFilter`执行。Zuul的前置过滤器是执行此操作的最佳位置。您可以通过`RequestContext`in pre过滤器访问HTTP标头和查询参数，因此可以用来确定`LOAD_BALANCER_KEY`传递到Ribbon的。如果不使用`LOAD_BALANCER_KEY`in 放置任何值，则将`RequestContext`null传递为`choose`方法的参数。