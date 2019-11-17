# 54. Propagation

## 54.传播

需要进行传播以确保源自同一根的活动被收集到同一条迹线中。最常见的传播方法是通过将RPC请求发送到接收它的服务器来从客户端复制跟踪上下文。

例如，进行下游HTTP调用时，其跟踪上下文被编码为请求标头，并与请求标头一起发送，如下图所示：

```bash
   Client Span                                                Server Span
┌──────────────────┐                                       ┌──────────────────┐
│                  │                                       │                  │
│   TraceContext   │           Http Request Headers        │   TraceContext   │
│ ┌──────────────┐ │          ┌───────────────────┐        │ ┌──────────────┐ │
│ │ TraceId      │ │          │ X─B3─TraceId      │        │ │ TraceId      │ │
│ │              │ │          │                   │        │ │              │ │
│ │ ParentSpanId │ │ Extract  │ X─B3─ParentSpanId │ Inject │ │ ParentSpanId │ │
│ │              ├─┼─────────>│                   ├────────┼>│              │ │
│ │ SpanId       │ │          │ X─B3─SpanId       │        │ │ SpanId       │ │
│ │              │ │          │                   │        │ │              │ │
│ │ Sampled      │ │          │ X─B3─Sampled      │        │ │ Sampled      │ │
│ └──────────────┘ │          └───────────────────┘        │ └──────────────┘ │
│                  │                                       │                  │
└──────────────────┘                                       └──────────────────┘
```

上面的名称来自[B3 Propagation](https://github.com/openzipkin/b3-propagation)，它内置于Brave，并具有许多语言和框架的实现。

大多数用户使用框架拦截器来自动化传播。接下来的两个示例显示了这对于客户端和服务器的工作方式。

以下示例显示了客户端传播如何工作：

```java
@Autowired Tracing tracing;

// configure a function that injects a trace context into a request
injector = tracing.propagation().injector(Request.Builder::addHeader);

// before a request is sent, add the current span's context to it
injector.inject(span.context(), request);
```

以下示例显示了服务器端传播的工作方式：

```java
@Autowired Tracing tracing;
@Autowired Tracer tracer;

// configure a function that extracts the trace context from a request
extractor = tracing.propagation().extractor(Request::getHeader);

// when a server receives a request, it joins or starts a new trace
span = tracer.nextSpan(extractor.extract(request));
```

## 54.1传播额外的字段

有时您需要传播额外的字段，例如请求ID或备用跟踪上下文。例如，如果您在Cloud Foundry环境中，则可能要传递请求ID，如以下示例所示：

```java
// when you initialize the builder, define the extra field you want to propagate
Tracing.newBuilder().propagationFactory(
  ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "x-vcap-request-id")
);

// later, you can tag that request ID or use it in log correlation
requestId = ExtraFieldPropagation.get("x-vcap-request-id");
```

您可能还需要传播未使用的跟踪上下文。例如，您可能处于Amazon Web Services环境中，但没有向X-Ray报告数据。为了确保X射线可以正确共存，请传递其跟踪标头，如以下示例所示：

```java
tracingBuilder.propagationFactory(
  ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "x-amzn-trace-id")
);
```

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 在Spring Cloud Sleuth中，跟踪构建器的所有元素`Tracing.newBuilder()` 都定义为bean。因此，如果要传递自定义`PropagationFactory`，就足以创建该类型的Bean，我们将在该`Tracing`Bean中进行设置。 |

### 54.1.1前缀字段

如果它们遵循通用模式，则还可以在字段前面加上前缀。下面的例子说明了如何传播`x-vcap-request-id`的场原样但发送`country-code`和`user-id`在电线字段`x-baggage-country-code`和`x-baggage-user-id`分别：

```java
Tracing.newBuilder().propagationFactory(
  ExtraFieldPropagation.newFactoryBuilder(B3Propagation.FACTORY)
                       .addField("x-vcap-request-id")
                       .addPrefixedFields("x-baggage-", Arrays.asList("country-code", "user-id"))
                       .build()
);
```

以后，您可以调用以下代码来影响当前跟踪上下文的国家/地区代码：

```java
ExtraFieldPropagation.set("x-country-code", "FO");
String countryCode = ExtraFieldPropagation.get("x-country-code");
```

或者，如果您有对跟踪上下文的引用，则可以显式使用它，如以下示例所示：

```java
ExtraFieldPropagation.set(span.context(), "x-country-code", "FO");
String countryCode = ExtraFieldPropagation.get(span.context(), "x-country-code");
```

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 与Sleuth早期版本的不同之处在于，使用Brave，您必须传递行李钥匙列表。有两个属性可以实现此目的。使用`spring.sleuth.baggage-keys`，您可以`baggage-`为HTTP呼叫和`baggage_`消息传递设置带有前缀的键。您还可以使用该`spring.sleuth.propagation-keys`属性来传递已列入白名单且没有任何前缀的前缀键列表。注意，`x-`标题键前面没有。 |      |

为了自动将行李值设置为Slf4j的MDC，您必须`spring.sleuth.log.slf4j.whitelisted-mdc-keys`使用列入白名单的行李和传播密钥的列表来设置属性。例如，`spring.sleuth.log.slf4j.whitelisted-mdc-keys=foo`将`foo`行李价值设置为MDC。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 请记住，将条目添加到MDC可能会大大降低应用程序的性能！        |      |

如果要将行李条目添加为标签，以便可以通过行李条目搜索跨度，则可以`spring.sleuth.propagation.tag.whitelisted-keys`使用列入白名单的行李钥匙列表来设置的值 。要禁用该功能，您必须传递`spring.sleuth.propagation.tag.enabled=false`属性。

### 54.1.2提取传播的上下文

在`TraceContext.Extractor`从传入的请求或消息读取跟踪标识符和采样状态。载体通常是一个请求对象或标头。

该实用程序用于标准工具（例如`HttpServerHandler`），但也可用于自定义RPC或消息传递代码。

`TraceContextOrSamplingFlags`通常仅与一起使用`Tracer.nextSpan(extracted)`，除非您要在客户端和服务器之间共享范围ID。

### 54.1.3在客户端和服务器之间共享范围ID

正常的检测模式是创建一个跨度，该跨度代表RPC的服务器端。 `Extractor.extract`当应用于传入的客户端请求时，可能会返回完整的跟踪上下文。 `Tracer.joinSpan`尝试继续此跟踪，如果支持，则使用相同的跨度ID，否则，创建一个子跨度。当跨度ID被共享时，报告的数据包括这样的标记。

下图显示了B3传播的示例：

```bash
                              ┌───────────────────┐      ┌───────────────────┐
 Incoming Headers             │   TraceContext    │      │   TraceContext    │
┌───────────────────┐(extract)│ ┌───────────────┐ │(join)│ ┌───────────────┐ │
│ X─B3-TraceId      │─────────┼─┼> TraceId      │ │──────┼─┼> TraceId      │ │
│                   │         │ │               │ │      │ │               │ │
│ X─B3-ParentSpanId │─────────┼─┼> ParentSpanId │ │──────┼─┼> ParentSpanId │ │
│                   │         │ │               │ │      │ │               │ │
│ X─B3-SpanId       │─────────┼─┼> SpanId       │ │──────┼─┼> SpanId       │ │
└───────────────────┘         │ │               │ │      │ │               │ │
                              │ │               │ │      │ │  Shared: true │ │
                              │ └───────────────┘ │      │ └───────────────┘ │
                              └───────────────────┘      └───────────────────┘
```

某些传播系统仅转发父跨度ID（在时检测到）`Propagation.Factory.supportsJoin() == false`。在这种情况下，始终提供新的跨度ID，而传入上下文确定父ID。

下图显示了AWS传播的示例：

```bash
                              ┌───────────────────┐      ┌───────────────────┐
 x-amzn-trace-id              │   TraceContext    │      │   TraceContext    │
┌───────────────────┐(extract)│ ┌───────────────┐ │(join)│ ┌───────────────┐ │
│ Root              │─────────┼─┼> TraceId      │ │──────┼─┼> TraceId      │ │
│                   │         │ │               │ │      │ │               │ │
│ Parent            │─────────┼─┼> SpanId       │ │──────┼─┼> ParentSpanId │ │
└───────────────────┘         │ └───────────────┘ │      │ │               │ │
                              └───────────────────┘      │ │  SpanId: New  │ │
                                                         │ └───────────────┘ │
                                                         └───────────────────┘
                                                         ──────────────┘
```

注意：某些跨度报告程序不支持共享跨度ID。例如，如果您设置`Tracing.Builder.spanReporter(amazonXrayOrGoogleStackdrive)`，则应通过设置禁用联接`Tracing.Builder.supportsJoin(false)`。这样做迫使一个新的孩子跨过`Tracer.joinSpan()`。

### 54.1.4实施传播

`TraceContext.Extractor`由`Propagation.Factory`插件实现。在内部，此代码`TraceContextOrSamplingFlags`使用以下之一创建联合类型：* `TraceContext`如果存在跟踪和跨度ID。* `TraceIdContext`如果存在跟踪ID，但不存在跨度ID。* `SamplingFlags`如果不存在标识符。

一些`Propagation`实现从提取（例如，读取传入的标头）到注入（例如，写入输出的标头）的角度携带额外的数据。例如，它可能带有一个请求ID。当实现中有额外数据时，它们将按以下方式处理：*如果`TraceContext`提取了a ，则将额外数据添加为`TraceContext.extra()`。*否则，将其添加为`TraceContextOrSamplingFlags.extra()`进行`Tracer.nextSpan`处理。