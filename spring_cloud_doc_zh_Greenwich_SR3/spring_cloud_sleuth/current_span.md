# 56. Current Span

## 56.当前跨度

Brave支持代表飞行中操作的“ 当前跨度 ”概念。您可以`Tracer.currentSpan()`用来将自定义标签添加到跨度并`Tracer.nextSpan()`创建正在进行中的子项。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 在Sleuth中，您可以`Tracer`通过`tracer.currentSpan()`方法自动装配bean以检索当前范围 。要检索当前上下文，只需调用 `tracer.currentSpan().context()`。要以String的形式获取当前跟踪ID，可以使用如下`traceIdString()`方法：`tracer.currentSpan().context().traceIdString()`。 |      |

## 56.1手动设置范围

在编写新的仪器时，将您创建的跨度作为当前跨度放置在示波器中很重要。这样做不仅使用户可以使用对其进行访问`Tracer.currentSpan()`，而且还允许诸如SLF4J MDC之类的自定义项查看当前的跟踪ID。

`Tracer.withSpanInScope(Span)`通过使用try-with-resources惯用语可以简化此过程，并且使用起来最方便。每当可能调用外部代码（例如进行拦截器或其他操作）时，请将范围放在范围内，如以下示例所示：

```java
@Autowired Tracer tracer;

try (SpanInScope ws = tracer.withSpanInScope(span)) {
  return inboundRequest.invoke();
} finally { // note the scope is independent of the span
  span.finish();
}
```

在某些情况下，您可能需要暂时清除当前范围（例如，启动不应该与当前请求关联的任务）。为此`withSpanInScope`，请将null传递给，如以下示例所示：

```java
@Autowired Tracer tracer;

try (SpanInScope cleared = tracer.withSpanInScope(null)) {
  startBackgroundThread();
}
```