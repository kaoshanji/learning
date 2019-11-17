# 42、计划任务

在`Executor`上下文中没有bean的情况下，Spring Boot会`ThreadPoolTaskExecutor`使用合理的默认值自动配置a ，这些默认值可以自动与异步任务执行（`@EnableAsync`）和Spring MVC异步请求处理相关联。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果您`Executor`在上下文中定义了一个自定义，则常规任务执行（即`@EnableAsync`）将透明地使用它，但由于需要`AsyncTaskExecutor`实现（名为`applicationTaskExecutor`），因此不会配置Spring MVC支持。根据你的目标的安排，你可以改变你`Executor`到一个`ThreadPoolTaskExecutor`或同时定义一个`ThreadPoolTaskExecutor`和`AsyncConfigurer`包装您的自定义`Executor`。通过自动配置`TaskExecutorBuilder`，您可以轻松创建实例，以重现默认情况下自动配置的功能。 |

线程池使用8个核心线程，这些线程可以根据负载增长和收缩。可以使用`spring.task.execution`名称空间对这些默认设置进行微调，如以下示例所示：

```bash
spring.task.execution.pool.max-size=16
spring.task.execution.pool.queue-capacity=100
spring.task.execution.pool.keep-alive=10s
```

这会将线程池更改为使用有界队列，以便在队列已满（100个任务）时，线程池最多增加到16个线程。池的收缩更加激进，因为当线程空闲10秒（而不是默认情况下的60秒）时，它们将被回收。

`ThreadPoolTaskScheduler`如果需要与计划的任务执行（`@EnableScheduling`）关联，也可以自动配置A。线程池默认使用一个线程，可以使用`spring.task.scheduling`名称空间对这些设置进行微调。

既是`TaskExecutorBuilder`豆和`TaskSchedulerBuilder`绿豆可在上下文提供如果自定义遗嘱执行人或调度需要创建。