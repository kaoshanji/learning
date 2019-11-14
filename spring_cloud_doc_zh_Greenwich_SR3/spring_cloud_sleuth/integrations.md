# 64. Integrations

## 64.整合

## 64.1 OpenTracing

Spring Cloud Sleuth与[OpenTracing](https://opentracing.io/)兼容。如果您在类路径上具有OpenTracing，我们将自动注册OpenTracing `Tracer`bean。如果要禁用此功能，请设置`spring.sleuth.opentracing.enabled`为`false`

## 64.2可运行和可调用

如果将逻辑包装在`Runnable`或中`Callable`，则可以将这些类包装在它们的Sleuth代表中，如以下示例所示`Runnable`：

```java
Runnable runnable = new Runnable() {
	@Override
	public void run() {
		// do some work
	}

	@Override
	public String toString() {
		return "spanNameFromToStringMethod";
	}
};
// Manual `TraceRunnable` creation with explicit "calculateTax" Span name
Runnable traceRunnable = new TraceRunnable(this.tracing, spanNamer, runnable,
		"calculateTax");
// Wrapping `Runnable` with `Tracing`. That way the current span will be available
// in the thread of `Runnable`
Runnable traceRunnableFromTracer = this.tracing.currentTraceContext()
		.wrap(runnable);
```

以下示例显示了如何执行以下操作`Callable`：

```java
Callable<String> callable = new Callable<String>() {
	@Override
	public String call() throws Exception {
		return someLogic();
	}

	@Override
	public String toString() {
		return "spanNameFromToStringMethod";
	}
};
// Manual `TraceCallable` creation with explicit "calculateTax" Span name
Callable<String> traceCallable = new TraceCallable<>(this.tracing, spanNamer,
		callable, "calculateTax");
// Wrapping `Callable` with `Tracing`. That way the current span will be available
// in the thread of `Callable`
Callable<String> traceCallableFromTracer = this.tracing.currentTraceContext()
		.wrap(callable);
```

这样，您可以确保为每个执行创建并关闭新的跨度。

## 64.3 Hystrix

### 64.3.1自定义并发策略

我们注册了一个[`HystrixConcurrencyStrategy`](https://github.com/Netflix/Hystrix/wiki/Plugins#concurrencystrategy)名为的风俗`TraceCallable`，该风俗将所有`Callable`实例包装在其Sleuth代表中。该策略将开始或继续跨度，具体取决于在调用Hystrix命令之前是否已经在进行跟踪。要禁用自定义的Hystrix并发策略，请将设置`spring.sleuth.hystrix.strategy.enabled`为`false`。

### 64.3.2手动命令设置

假设您具有以下条件`HystrixCommand`：

```java
HystrixCommand<String> hystrixCommand = new HystrixCommand<String>(setter) {
	@Override
	protected String run() throws Exception {
		return someLogic();
	}
};
```

要传递跟踪信息，必须在的Sleuth版本中包装相同的逻辑，`HystrixCommand`称为 `TraceCommand`，如以下示例所示：

```java
TraceCommand<String> traceCommand = new TraceCommand<String>(tracer, setter) {
	@Override
	public String doRun() throws Exception {
		return someLogic();
	}
};
```

## 64.4 RxJava

我们注册了一个[`RxJavaSchedulersHook`](https://github.com/ReactiveX/RxJava/wiki/Plugins#rxjavaschedulershook)将所有`Action0`实例包装在其Sleuth代表（称为）中的自定义`TraceAction`。挂钩将开始或继续跨度，具体取决于在计划操作之前是否已经进行了跟踪。要禁用自定义`RxJavaSchedulersHook`，请将设置`spring.sleuth.rxjava.schedulers.hook.enabled`为`false`。

您可以为不想创建跨度的线程名称定义一个正则表达式列表。为此，请在`spring.sleuth.rxjava.schedulers.ignoredthreads`属性中提供用逗号分隔的正则表达式列表。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 建议的反应式编程和Sleuth方法是使用Reactor支持。              |      |

## 64.5 HTTP集成

可以通过将`spring.sleuth.web.enabled`属性值设置为来禁用此部分的功能`false`。

### 64.5.1 HTTP筛选器

通过`TracingFilter`，所有采样的传入请求都将创建Span。Span的名称是`http:`请求发送到的路径。例如，如果将请求发送到，`/this/that`则名称为`http:/this/that`。您可以通过设置`spring.sleuth.web.skipPattern`属性来配置要跳过的URI 。如果`ManagementServerProperties`在classpath上，则将其值`contextPath`附加到提供的跳过模式中。如果您想重用Sleuth的默认跳过模式并追加自己的模式，请使用传递这些模式`spring.sleuth.web.additionalSkipPattern`。

默认情况下，所有弹簧启动执行器端点都会自动添加到跳过模式中。如果要禁用此行为，请设置`spring.sleuth.web.ignore-auto-configured-skip-patterns` 为`true`。

要更改跟踪过滤器注册的顺序，请设置 `spring.sleuth.web.filter-order`属性。

要禁用记录未捕获异常的过滤器，可以禁用该 `spring.sleuth.web.exception-throwing-filter-enabled`属性。

### 64.5.2 HandlerInterceptor

由于我们希望跨度名称精确，因此我们使用`TraceHandlerInterceptor`来包装现有名称`HandlerInterceptor`或将其直接添加到现有清单中`HandlerInterceptors`。在`TraceHandlerInterceptor`增加了一个特殊的请求属性来定`HttpServletRequest`。如果`TracingFilter`没有看到此属性，它将创建一个“ fallback ”跨度，这是在服务器端创建的一个附加跨度，以便在UI中正确显示跟踪。如果发生这种情况，可能是缺少仪器。在这种情况下，请在Spring Cloud Sleuth中提出问题。

### 64.5.3异步Servlet支持

如果您的控制器返回a `Callable`或a `WebAsyncTask`，则Spring Cloud Sleuth会继续现有范围，而不是创建一个新范围。

### 64.5.4 WebFlux支持

通过`TraceWebFilter`，所有采样的传入请求都将导致跨度的创建。Span的名称是`http:`请求发送到的路径。例如，如果请求发送到`/this/that`，则名称为`http:/this/that`。您可以使用`spring.sleuth.web.skipPattern`属性配置要跳过的URI 。如果`ManagementServerProperties`在类路径上，则将其值`contextPath`附加到提供的跳过模式中。如果要重用Sleuth的默认跳过模式并追加自己的模式，请使用传递这些模式`spring.sleuth.web.additionalSkipPattern`。

要更改跟踪过滤器注册的顺序，请设置 `spring.sleuth.web.filter-order`属性。

### 64.5.5 Dubbo RPC支持

通过与Brave的集成，Spring Cloud Sleuth支持[Dubbo](https://dubbo.apache.org/)。添加`brave-instrumentation-dubbo-rpc`依赖项就足够了：

```xml
<dependency>
    <groupId>io.zipkin.brave</groupId>
    <artifactId>brave-instrumentation-dubbo-rpc</artifactId>
</dependency>
```

您还需要设置一个`dubbo.properties`包含以下内容的文件：

```properties
dubbo.provider.filter=tracing
dubbo.consumer.filter=tracing
```

您可以[在此处](https://github.com/openzipkin/brave/tree/master/instrumentation/dubbo-rpc)阅读有关Brave-Dubbo集成的更多信息。可以在[此处](https://github.com/openzipkin/sleuth-webmvc-example/compare/add-dubbo-tracing)找到Spring Cloud Sleuth和Dubbo的示例。

## 64.6 HTTP客户端集成

### 64.6.1同步休息模板

我们注入一个`RestTemplate`拦截器，以确保所有跟踪信息都传递给请求。每次拨打电话时，都会创建一个新的跨度。收到响应后关闭。要阻止同步`RestTemplate`功能，请设置`spring.sleuth.web.client.enabled`为`false`。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 您必须注册`RestTemplate`为bean，以便拦截器被注入。如果`RestTemplate`使用`new`关键字创建实例，则检测无效。 |      |

### 64.6.2异步休息模板

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 从Sleuth开始`2.0.0`，我们不再注册`AsyncRestTemplate`类型的bean 。创建此类Bean由您自己决定。然后我们对其进行检测。 |      |

要阻止`AsyncRestTemplate`功能，请设置`spring.sleuth.web.async.client.enabled`为`false`。要禁用默认设置`TraceAsyncClientHttpRequestFactoryWrapper`，请设置`spring.sleuth.web.async.client.factory.enabled` 为`false`。如果根本不想创建`AsyncRestClient`，请设置`spring.sleuth.web.async.client.template.enabled`为`false`。

#### 多个异步休息模板

有时您需要使用异步休息模板的多种实现。在以下代码段中，您可以看到有关如何设置这样的自定义的示例`AsyncRestTemplate`：

```java
@Configuration
@EnableAutoConfiguration
static class Config {

	@Bean(name = "customAsyncRestTemplate")
	public AsyncRestTemplate traceAsyncRestTemplate() {
		return new AsyncRestTemplate(asyncClientFactory(),
				clientHttpRequestFactory());
	}

	private ClientHttpRequestFactory clientHttpRequestFactory() {
		ClientHttpRequestFactory clientHttpRequestFactory = new CustomClientHttpRequestFactory();
		// CUSTOMIZE HERE
		return clientHttpRequestFactory;
	}

	private AsyncClientHttpRequestFactory asyncClientFactory() {
		AsyncClientHttpRequestFactory factory = new CustomAsyncClientHttpRequestFactory();
		// CUSTOMIZE HERE
		return factory;
	}

}
```

### 64.6.3 `WebClient`

我们注入了一个`ExchangeFilterFunction`创建跨度的实现，并通过成功和错误时回调来关闭客户端跨度。

要阻止此功能，请设置`spring.sleuth.web.client.enabled`为`false`。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 您必须注册`WebClient`为bean，以便应用跟踪工具。如果`WebClient`使用`new`关键字创建实例，则检测无效。 |      |

### 64.6.4特拉弗森

如果使用[Traverson](https://docs.spring.io/spring-hateoas/docs/current/reference/html/#client.traverson)库，则可以将a `RestTemplate`作为bean注入到Traverson对象中。由于`RestTemplate`已经被拦截，您将获得对客户端中跟踪的全面支持。以下伪代码显示了如何执行此操作：

```java
@Autowired RestTemplate restTemplate;

Traverson traverson = new Traverson(URI.create("http://some/address"),
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8).setRestOperations(restTemplate);
// use Traverson
```

### 64.6.5 Apache `HttpClientBuilder`和`HttpAsyncClientBuilder`

我们对`HttpClientBuilder`和进行检测，`HttpAsyncClientBuilder`以便将跟踪上下文注入已发送的请求中。

要阻止这些功能，请设置`spring.sleuth.web.client.enabled`为`false`。

### 64.6.6净值 `HttpClient`

我们对Netty的进行计量`HttpClient`。

要阻止此功能，请设置`spring.sleuth.web.client.enabled`为`false`。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 您必须注册`HttpClient`为bean，以便进行检测。如果`HttpClient`使用`new`关键字创建实例，则检测无效。 |      |

### 64.6.7 `UserInfoRestTemplateCustomizer`

我们对Spring Security的进行检测`UserInfoRestTemplateCustomizer`。

要阻止此功能，请设置`spring.sleuth.web.client.enabled`为`false`。

## 64.7假装

默认情况下，Spring Cloud Sleuth通过提供与Feign的集成`TraceFeignClientAutoConfiguration`。您可以将设置`spring.sleuth.feign.enabled`为来完全禁用它`false`。如果这样做，则不会进行任何与Feign相关的检测。

Feign仪器的一部分是通过`FeignBeanPostProcessor`。您可以通过设置`spring.sleuth.feign.processor.enabled`为禁用它`false`。如果将其设置为`false`，Spring Cloud Sleuth不会检测任何自定义Feign组件。但是，所有默认工具仍然存在。

## 64.8 gRPC

Spring Cloud Sleuth 通过提供了[gRPC的](https://grpc.io/)工具`TraceGrpcAutoConfiguration`。您可以将设置`spring.sleuth.grpc.enabled`为来完全禁用它`false`。

### 64.8.1变体1

#### 依存关系

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| gRPC集成依赖于两个外部库来检测客户端和服务器，并且这两个库都必须位于类路径中才能启用检测。 |      |

Maven：

```xml
		<dependency>
			<groupId>io.github.lognet</groupId>
			<artifactId>grpc-spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>io.zipkin.brave</groupId>
			<artifactId>brave-instrumentation-grpc</artifactId>
		</dependency>
```

摇篮：

```json
    compile("io.github.lognet:grpc-spring-boot-starter")
    compile("io.zipkin.brave:brave-instrumentation-grpc")
```

#### 服务器检测

Spring Cloud Sleuth利用grpc-spring-boot-starter向带有标记的所有服务注册Brave的gRPC服务器拦截器`@GRpcService`。

#### 客户端工具

gRPC客户端利用`ManagedChannelBuilder`来构造一个`ManagedChannel`用于与gRPC服务器通信的。本机`ManagedChannelBuilder`提供静态方法作为构造`ManagedChannel`实例的入口点，但是，此机制不受Spring应用程序上下文的影响。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| Spring Cloud Sleuth提供了一个`SpringAwareManagedChannelBuilder`可通过Spring应用程序上下文进行自定义并由gRPC客户端注入的。**创建`ManagedChannel`实例时必须使用此构建器。** |      |

Sleuth创建一个`TracingManagedChannelBuilderCustomizer`将Brave的客户端拦截器注入的`SpringAwareManagedChannelBuilder`。

### 64.8.2变体2

[Grpc Spring Boot Starter](https://github.com/yidongnan/grpc-spring-boot-starter)自动检测Spring Cloud Sleuth和brave的gRPC工具的存在，并注册必要的客户端和/或服务器工具。

## 64.9异步通信

### 64.9.1带 `@Async`注释的方法

在Spring Cloud Sleuth中，我们检测与异步相关的组件，以便在线程之间传递跟踪信息。您可以通过设置的值，禁用此行为`spring.sleuth.async.enabled`来`false`。

如果您使用注释方法`@Async`，我们将自动创建具有以下特征的新跨度：

- 如果使用标记方法，则注释`@SpanName`的值是Span的名称。
- 如果该方法未使用注释`@SpanName`，则Span名称为注释的方法名称。
- 该范围用方法的类名和方法名标记。

### 64.9.2 `@Scheduled`注释方法

在Spring Cloud Sleuth中，我们检测调度方法的执行情况，以便在线程之间传递跟踪信息。您可以通过设置的值，禁用此行为`spring.sleuth.scheduled.enabled`来`false`。

如果您使用注释方法`@Scheduled`，我们将自动创建具有以下特征的新跨度：

- 跨度名称是带注释的方法名称。
- 该范围用方法的类名和方法名标记。

如果要跳过某些带`@Scheduled`注释类的跨度创建，则可以使用`spring.sleuth.scheduled.skipPattern`与带`@Scheduled`注释类的全限定名称匹配的正则表达式设置。如果同时使用`spring-cloud-sleuth-stream`和`spring-cloud-netflix-hystrix-stream`，则将为每个Hystrix指标创建一个范围并将其发送给Zipkin。此行为可能很烦人。这就是默认情况下的原因`spring.sleuth.scheduled.skipPattern=org.springframework.cloud.netflix.hystrix.stream.HystrixStreamTask`。

### 64.9.3 Executor，ExecutorService和ScheduledExecutorService

我们提供`LazyTraceExecutor`，`TraceableExecutorService`和`TraceableScheduledExecutorService`。每次提交，调用或计划新任务时，这些实现都会创建跨度。

以下示例显示在使用`TraceableExecutorService`时如何传递跟踪信息`CompletableFuture`：

```java
CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> {
	// perform some logic
	return 1_000_000L;
}, new TraceableExecutorService(beanFactory, executorService,
		// 'calculateTax' explicitly names the span - this param is optional
		"calculateTax"));
```

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| Sleuth不能立即使用`parallelStream()`。如果要使跟踪信息通过流传播，则必须将方法与一起使用`supplyAsync(…)`，如前面所示。 |      |

如果有些bean实现了`Executor`您希望从跨度创建中排除的接口，则可以使用该`spring.sleuth.async.ignored-beans` 属性在其中提供bean名称的列表。

#### 定制执行者

有时，您需要设置的自定义实例`AsyncExecutor`。以下示例显示了如何设置这样的自定义`Executor`：

```java
@Configuration
@EnableAutoConfiguration
@EnableAsync
// add the infrastructure role to ensure that the bean gets auto-proxied
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
static class CustomExecutorConfig extends AsyncConfigurerSupport {

	@Autowired
	BeanFactory beanFactory;

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// CUSTOMIZE HERE
		executor.setCorePoolSize(7);
		executor.setMaxPoolSize(42);
		executor.setQueueCapacity(11);
		executor.setThreadNamePrefix("MyExecutor-");
		// DON'T FORGET TO INITIALIZE
		executor.initialize();
		return new LazyTraceExecutor(this.beanFactory, executor);
	}

}
```

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 为确保您的配置得到后期处理，请记住`@Role(BeanDefinition.ROLE_INFRASTRUCTURE)`在您的`@Configuration`课程上 添加 |

## 64.10消息传递

可以通过将`spring.sleuth.messaging.enabled`属性值设置为来禁用此部分的功能`false`。

### 64.10.1 Spring集成和Spring Cloud Stream

Spring Cloud Sleuth与[Spring Integration集成](https://projects.spring.io/spring-integration/)。它为发布和订阅事件创建跨度。要禁用Spring Integration工具，请设置`spring.sleuth.integration.enabled`为`false`。

您可以提供`spring.sleuth.integration.patterns`模式以显式提供要包括在跟踪中的通道的名称。默认情况下，`hystrixStreamOutput`包含除通道之外的所有通道。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 使用`Executor`来构建Spring Integration时`IntegrationFlow`，必须使用的未跟踪版本`Executor`。使用来装饰Spring Integration Executor通道`TraceableExecutorService`会导致跨度被不正确地关闭。 |      |

如果要定制从消息头读取和写入跟踪上下文的方式，那么足以注册类型的bean：

- `Propagation.Setter` -用于将标头写入消息
- `Propagation.Getter` -从邮件中读取标题

### 64.10.2春季RabbitMq

我们对进行检测，`RabbitTemplate`以便将跟踪标头注入到消息中。

要阻止此功能，请设置`spring.sleuth.messaging.rabbit.enabled`为`false`。

### 64.10.3春天卡夫卡

我们对Spring Kafka `ProducerFactory`和进行检测，`ConsumerFactory` 以便将跟踪标头注入到创建的Spring Kafka `Producer`和中`Consumer`。

要阻止此功能，请设置`spring.sleuth.messaging.kafka.enabled`为`false`。

### 64.10.4 Spring JMS

我们对进行检测，`JmsTemplate`以便将跟踪标头注入到消息中。我们还在`@JmsListener`消费者方面支持带注释的方法。

要阻止此功能，请设置`spring.sleuth.messaging.jms.enabled`为`false`。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 我们不支持JMS的行李运送                                      |      |

## 64.11祖尔

我们通过使用跟踪信息丰富功能区请求来检测Zuul功能区集成。要禁用Zuul支持，请将`spring.sleuth.zuul.enabled`属性设置为`false`。