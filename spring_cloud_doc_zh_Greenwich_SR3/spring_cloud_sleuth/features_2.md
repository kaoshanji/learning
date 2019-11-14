# 52. Features

## 52.特点

- 将跟踪和跨度ID添加到Slf4J MDC，因此您可以在日志聚合器中从给定的跟踪或跨度提取所有日志，如以下示例日志所示：

  ```properties
  2016-02-02 15:30:57.902  INFO [bar,6bfd228dc00d216b,6bfd228dc00d216b,false] 23030 --- [nio-8081-exec-3] ...
  2016-02-02 15:30:58.372 ERROR [bar,6bfd228dc00d216b,6bfd228dc00d216b,false] 23030 --- [nio-8081-exec-3] ...
  2016-02-02 15:31:01.936  INFO [bar,46ab0d418373cbc9,46ab0d418373cbc9,false] 23030 --- [nio-8081-exec-4] ...
  ```

  注意`[appname,traceId,spanId,exportable]`来自MDC 的条目：

  - **`spanId`**：发生的特定操作的ID。
  - **`appname`**：记录跨度的应用程序的名称。
  - **`traceId`**：包含跨度的延迟图的ID。
  - **`exportable`**：是否应将日志导出到Zipkin。您何时希望跨度不可导出？当您要将某些操作包装在Span中并将其仅写入日志时。

- 提供对常见的分布式跟踪数据模型的抽象：跟踪，跨度（形成DAG），注释和键值注释。Spring Cloud Sleuth宽松地基于HTrace，但与Zipkin（Dapper）兼容。

- Sleuth记录计时信息以帮助进行延迟分析。通过使用侦探，您可以查明应用程序中延迟的原因。

- 编写Sleuth时不要过多记录日志，也不会导致生产应用程序崩溃。为此，Sleuth：

  - 在带内传播有关调用图的结构数据，并在带外传播其余数据。
  - 包括对诸如HTTP之类的层的自觉检测。
  - 包括用于管理数量的采样策略。
  - 可以报告给Zipkin系统进行查询和可视化。

- 从Spring应用程序（Servlet过滤器，异步端点，Rest模板，计划的操作，消息通道，Zuul过滤器和Feign客户端）检测常见的入口和出口点。

- Sleuth包含默认逻辑以跨HTTP或消息传递边界加入跟踪。例如，HTTP传播在与Zipkin兼容的请求标头上工作。

- 侦查可以在进程之间传播上下文（也称为行李）。因此，如果您在Span上设置了行李元素，则会通过HTTP或消息传递将其下游发送到其他进程。

- 提供一种创建或继续跨度以及通过注释添加标签和日志的方法。

- 如果`spring-cloud-sleuth-zipkin`在类路径中，则该应用程序会生成并收集与Zipkin兼容的跟踪。默认情况下，它通过HTTP将它们发送到本地主机（端口9411）上的Zipkin服务器。您可以通过设置来配置服务的位置`spring.zipkin.baseUrl`。

  - 如果您依赖`spring-rabbit`，则您的应用会将跟踪发送到RabbitMQ代理，而不是HTTP。
  - 如果您依赖`spring-kafka`并设置`spring.zipkin.sender.type: kafka`，则您的应用会将跟踪发送到Kafka代理，而不是HTTP。

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/caution.png) | 警告 |
| ------------------------------------------------------------ | ---- |
| `spring-cloud-sleuth-stream` 已弃用，不应再使用。            |      |

- Spring Cloud Sleuth与[OpenTracing](https://opentracing.io/)兼容。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 如果使用Zipkin，请通过设置`spring.sleuth.sampler.probability` （默认值：0.1，即10％）配置导出跨度的概率。否则，您可能会认为Sleuth无法正常工作，因为它忽略了一些跨度。 |      |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 始终设置SLF4J MDC，并且按先前显示的示例，登录用户可以立即在日志中看到跟踪和跨度ID。其他日志记录系统必须配置自己的格式化程序才能获得相同的结果。默认值如下： `logging.pattern.level`设置为`%5p [${spring.zipkin.service.name:${spring.application.name:-}},%X{X-B3-TraceId:-},%X{X-B3-SpanId:-},%X{X-Span-Export:-}]` （这是Logback用户的Spring Boot功能）。如果您不使用SLF4J，则不会自动应用此模式。 |

## 52.1勇敢简介

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 从version开始`2.0.0`，Spring Cloud Sleuth使用 [Brave](https://github.com/openzipkin/brave)作为跟踪库。为了您的方便，我们在此处嵌入了Brave文档的一部分。 |      |

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 在大多数情况下，您只需要使用Sleuth提供的Brave中的`Tracer` or或`SpanCustomizer`bean。以下文档高度概述了Brave是什么以及它如何工作。 |      |

勇敢是一个库，用于捕获有关分布式操作的延迟信息并将其报告给Zipkin。大多数用户不直接使用Brave。他们使用库或框架，而不是代表他们使用Brave。

该模块包括一个跟踪器，该跟踪器创建并连接跨度，以对潜在的分布式工作的延迟进行建模。它还包括用于在网络边界上传播跟踪上下文的库（例如，使用HTTP标头）。

### 52.1.1跟踪

最重要的是，您需要`brave.Tracer`配置为[向Zipkin报告](https://github.com/openzipkin/zipkin-reporter-java)。

以下示例安装程序通过HTTP（与Kafka相对）将跟踪数据（跨度）发送到Zipkin：

```java
class MyClass {

    private final Tracer tracer;

    // Tracer will be autowired
    MyClass(Tracer tracer) {
        this.tracer = tracer;
    }

    void doSth() {
        Span span = tracer.newTrace().name("encode").start();
        // ...
    }
}
```

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 如果您的跨度包含的名称长于50个字符，则该名称将被截断为50个字符。您的姓名必须明确明确。知名人士会导致延迟问题，有时甚至会引发异常。 |      |

跟踪器创建并连接跨度，以对潜在的分布式工作的延迟进行建模。它可以采用采样来减少处理过程中的开销，减少发送到Zipkin的数据量或两者。

跟踪器返回的跨距在完成后将数据报告给Zipkin，如果未采样则不执行任何操作。开始跨度后，您可以注释感兴趣的事件或添加包含详细信息或查找键的标签。

跨度具有包含跟踪标识符的上下文，该标识符将跨度放置在代表分布式操作的树中的正确位置。

### 52.1.2本地跟踪

跟踪永远不会离开进程的代码时，请在范围范围内运行它。

```java
@Autowired Tracer tracer;

// Start a new trace or a span within an existing trace representing an operation
ScopedSpan span = tracer.startScopedSpan("encode");
try {
  // The span is in "scope" meaning downstream code such as loggers can see trace IDs
  return encoder.encode();
} catch (RuntimeException | Error e) {
  span.error(e); // Unless you handle exceptions, you might not know the operation failed!
  throw e;
} finally {
  span.finish(); // always finish the span
}
```

当您需要更多功能或更好的控制时，请使用以下`Span`类型：

```java
@Autowired Tracer tracer;

// Start a new trace or a span within an existing trace representing an operation
Span span = tracer.nextSpan().name("encode").start();
// Put the span in "scope" so that downstream code such as loggers can see trace IDs
try (SpanInScope ws = tracer.withSpanInScope(span)) {
  return encoder.encode();
} catch (RuntimeException | Error e) {
  span.error(e); // Unless you handle exceptions, you might not know the operation failed!
  throw e;
} finally {
  span.finish(); // note the scope is independent of the span. Always finish a span.
}
```

上面的两个示例都报告了完全相同的跨度！

在上面的示例中，范围将是新的根范围或现有跟踪中的下一个子级。

### 52.1.3自定义跨度

一旦具有跨度，就可以向其添加标签。标记可用作查找键或详细信息。例如，您可以在运行时版本中添加标签，如以下示例所示：

```java
span.tag("clnt/finagle.version", "6.36.0");
```

向第三方公开自定义跨度的功能时，最好选择`brave.SpanCustomizer`而不是`brave.Span`。前者更易于理解和测试，不会用跨度生命周期挂钩吸引用户。

```java
interface MyTraceCallback {
  void request(Request request, SpanCustomizer customizer);
}
```

自从`brave.Span`实现以来`brave.SpanCustomizer`，您可以将其传递给用户，如以下示例所示：

```java
for (MyTraceCallback callback : userCallbacks) {
  callback.request(request, span);
}
```

### 52.1.4隐式查找当前跨度

有时，您不知道跟踪是否正在进行，并且您不希望用户执行空检查。 `brave.CurrentSpanCustomizer`通过将数据添加到正在进行或删除的任何跨度中来解决此问题，如以下示例所示：

例如

```java
// The user code can then inject this without a chance of it being null.
@Autowired SpanCustomizer span;

void userCode() {
  span.annotate("tx.started");
  ...
}
```

### 52.1.5 RPC跟踪

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 在滚动自己的RPC工具之前，请检查[此处编写](https://github.com/openzipkin/brave/tree/master/instrumentation)的[工具](https://github.com/openzipkin/brave/tree/master/instrumentation)以及[Zipkin的列表](https://zipkin.io/pages/existing_instrumentations.html)。 |

RPC跟踪通常由拦截器自动完成。它们在幕后添加了与其在RPC操作中的角色相关的标签和事件。

以下示例显示如何添加客户端范围：

```java
@Autowired Tracing tracing;
@Autowired Tracer tracer;

// before you send a request, add metadata that describes the operation
span = tracer.nextSpan().name(service + "/" + method).kind(CLIENT);
span.tag("myrpc.version", "1.0.0");
span.remoteServiceName("backend");
span.remoteIpAndPort("172.3.4.1", 8108);

// Add the trace context to the request, so it can be propagated in-band
tracing.propagation().injector(Request::addHeader)
                     .inject(span.context(), request);

// when the request is scheduled, start the span
span.start();

// if there is an error, tag the span
span.tag("error", error.getCode());
// or if there is an exception
span.error(exception);

// when the response is complete, finish the span
span.finish();
```

#### 单向跟踪

有时，您需要对有请求但无响应的异步操作进行建模。在正常的RPC跟踪中，您`span.finish()` 用来表示已收到响应。在单向跟踪中，`span.flush()`因为您不期望响应，所以您改用它 。

下面的示例显示客户端如何建模单向操作：

```java
@Autowired Tracing tracing;
@Autowired Tracer tracer;

// start a new span representing a client request
oneWaySend = tracer.nextSpan().name(service + "/" + method).kind(CLIENT);

// Add the trace context to the request, so it can be propagated in-band
tracing.propagation().injector(Request::addHeader)
                     .inject(oneWaySend.context(), request);

// fire off the request asynchronously, totally dropping any response
request.execute();

// start the client side and flush instead of finish
oneWaySend.start().flush();
```

以下示例显示服务器如何处理单向操作：

```java
@Autowired Tracing tracing;
@Autowired Tracer tracer;

// pull the context out of the incoming request
extractor = tracing.propagation().extractor(Request::getHeader);

// convert that context to a span which you can name and add tags to
oneWayReceive = nextSpan(tracer, extractor.extract(request))
    .name("process-request")
    .kind(SERVER)
    ... add tags etc.

// start the server side and flush instead of finish
oneWayReceive.start().flush();

// you should not modify this span anymore as it is complete. However,
// you can create children to represent follow-up work.
next = tracer.newSpan(oneWayReceive.context()).name("step2").start();
```