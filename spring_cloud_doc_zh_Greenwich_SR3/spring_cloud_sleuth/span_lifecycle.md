# 58. Span lifecycle

## 58.跨度生命周期

您可以通过以下方式在Span上执行以下操作`brave.Tracer`：

- [start](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__span_lifecycle.html#creating-and-finishing-spans)：开始跨度时，将分配其名称并记录开始时间戳。
- [close](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__span_lifecycle.html#creating-and-finishing-spans)：跨度已完成（记录了跨度的结束时间），并且，如果对跨度进行了采样，则可以进行收集（例如，收集到Zipkin）。
- [继续](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__span_lifecycle.html#continuing-spans)：创建一个新的span实例。它是继续的副本。
- [detach](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__span_lifecycle.html#continuing-spans)：跨度不会停止或关闭。它只会从当前线程中删除。
- [使用显式父项创建](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__span_lifecycle.html#creating-spans-with-explicit-parent)：您可以创建一个新的跨度并为其设置一个显式父项。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| Spring Cloud Sleuth `Tracer`为您创建一个实例。为了使用它，您可以对其进行自动接线。 |

## 58.1创建和整理跨度

您可以使用来手动创建跨度`Tracer`，如以下示例所示：

```java
// Start a span. If there was a span present in this thread it will become
// the `newSpan`'s parent.
Span newSpan = this.tracer.nextSpan().name("calculateTax");
try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(newSpan.start())) {
	// ...
	// You can tag a span
	newSpan.tag("taxValue", taxValue);
	// ...
	// You can log an event on a span
	newSpan.annotate("taxCalculated");
}
finally {
	// Once done remember to finish the span. This will allow collecting
	// the span to send it to Zipkin
	newSpan.finish();
}
```

在前面的示例中，我们可以看到如何创建跨度的新实例。如果此线程中已经有一个跨度，它将成为新跨度的父级。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 创建跨度后，请始终保持清洁。另外，请始终完成要发送给Zipkin的所有跨度。 |      |

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 如果您的跨度包含一个大于50个字符的名称，该名称将被截断为50个字符。您的姓名必须明确明确。知名人士会导致延迟问题，有时甚至会引发例外情况。 |      |

## 58.2连续跨度

有时，您不想创建一个新跨度，但想继续一个跨度。这种情况的示例如下：

- **AOP**：如果在达到宽高比之前已经创建了一个跨度，则您可能不想创建一个新的跨度。
- **Hystrix**：执行Hystrix命令很可能是当前处理的逻辑部分。实际上，它仅仅是技术实现细节，您不一定要在跟踪中将其反映为一个单独的实体。

要继续跨度，可以使用`brave.Tracer`，如以下示例所示：

```java
// let's assume that we're in a thread Y and we've received
// the `initialSpan` from thread X
Span continuedSpan = this.tracer.toSpan(newSpan.context());
try {
	// ...
	// You can tag a span
	continuedSpan.tag("taxValue", taxValue);
	// ...
	// You can log an event on a span
	continuedSpan.annotate("taxCalculated");
}
finally {
	// Once done remember to flush the span. That means that
	// it will get reported but the span itself is not yet finished
	continuedSpan.flush();
}
```

## 58.3使用显式父级创建跨度

您可能要开始一个新的跨度并提供该跨度的显式父项。假定范围的父级在一个线程中，而您想在另一个线程中开始一个新的范围。在《勇敢传说》中，无论何时调用`nextSpan()`，它都会参考当前范围内的范围创建一个范围。您可以将范围放入范围中，然后调用`nextSpan()`，如以下示例所示：

```java
// let's assume that we're in a thread Y and we've received
// the `initialSpan` from thread X. `initialSpan` will be the parent
// of the `newSpan`
Span newSpan = null;
try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(initialSpan)) {
	newSpan = this.tracer.nextSpan().name("calculateCommission");
	// ...
	// You can tag a span
	newSpan.tag("commissionValue", commissionValue);
	// ...
	// You can log an event on a span
	newSpan.annotate("commissionCalculated");
}
finally {
	// Once done remember to finish the span. This will allow collecting
	// the span to send it to Zipkin. The tags and events set on the
	// newSpan will not be present on the parent
	if (newSpan != null) {
		newSpan.finish();
	}
}
```

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 创建这样的跨度后，必须完成它。否则，不会报告（例如，向Zipkin报告）。 |      |