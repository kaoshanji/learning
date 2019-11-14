# 53. Sampling

## 53.抽样

可以采用采样来减少收集和报告的过程外数据。如果未对跨度进行采样，则不会增加开销（无操作）。

采样是一项前期决策，这意味着报告数据的决策是在跟踪的第一个操作中做出的，并且该决策会向下游传播。

默认情况下，全局采样器将单个速率应用于所有跟踪的操作。 `Tracer.Builder.sampler`控制此设置，它默认为跟踪每个请求。

## 53.1声明式采样

一些应用程序需要根据java方法的类型或注释进行采样。

大多数用户使用框架拦截器来自动化这种策略。以下示例显示了它如何在内部工作：

```java
@Autowired Tracer tracer;

// derives a sample rate from an annotation on a java method
DeclarativeSampler<Traced> sampler = DeclarativeSampler.create(Traced::sampleRate);

@Around("@annotation(traced)")
public Object traceThing(ProceedingJoinPoint pjp, Traced traced) throws Throwable {
  // When there is no trace in progress, this decides using an annotation
  Sampler decideUsingAnnotation = declarativeSampler.toSampler(traced);
  Tracer tracer = tracer.withSampler(decideUsingAnnotation);

  // This code looks the same as if there was no declarative override
  ScopedSpan span = tracer.startScopedSpan(spanName(pjp));
  try {
    return pjp.proceed();
  } catch (RuntimeException | Error e) {
    span.error(e);
    throw e;
  } finally {
    span.finish();
  }
}
```

## 53.2自定义采样

根据操作的不同，您可能需要应用不同的策略。例如，您可能不想跟踪对静态资源（例如图像）的请求，或者您想跟踪所有对新api的请求。

大多数用户使用框架拦截器来自动化这种策略。以下示例显示了它如何在内部工作：

```java
@Autowired Tracer tracer;
@Autowired Sampler fallback;

Span nextSpan(final Request input) {
  Sampler requestBased = Sampler() {
    @Override public boolean isSampled(long traceId) {
      if (input.url().startsWith("/experimental")) {
        return true;
      } else if (input.url().startsWith("/static")) {
        return false;
      }
      return fallback.isSampled(traceId);
    }
  };
  return tracer.withSampler(requestBased).nextSpan();
}
```

## 53.3在Spring Cloud Sleuth中采样

默认情况下，Spring Cloud Sleuth将所有范围设置为不可导出。这意味着跟踪将出现在日志中，但不会出现在任何远程存储中。测试默认值通常就足够了，如果仅使用日志（例如，使用ELK聚合器），则可能只需要它即可。如果将跨度数据导出到Zipkin，则还有一个`Sampler.ALWAYS_SAMPLE`设置可以导出所有内容，还有一个`ProbabilityBasedSampler`设置可以对固定比例的跨度进行采样。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `ProbabilityBasedSampler`如果使用，则为默认值`spring-cloud-sleuth-zipkin`。您可以通过设置配置导出`spring.sleuth.sampler.probability`。传递的价值需要从双`0.0`至`1.0`。 |

可以通过创建bean定义来安装采样器，如以下示例所示：

```java
@Bean
public Sampler defaultSampler() {
	return Sampler.ALWAYS_SAMPLE;
}
```

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 您可以将HTTP标头设置`X-B3-Flags`为`1`，或者在进行消息传递时将`spanFlags`标头设置为`1`。这样做将强制电流跨度可导出，而不管采样决定如何。 |

为了使用速率受限的采样器，请设置`spring.sleuth.sampler.rate`属性以选择每秒钟间隔要接受的跟踪量。最小数字为0，最大数字为2,147,483,647（最大整数）。