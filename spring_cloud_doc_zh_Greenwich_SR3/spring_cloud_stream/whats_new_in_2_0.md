# 26. What’s New in 2.0?

## 26. 2.0中有什么新功能？

Spring Cloud Stream引入了许多新功能，增强功能和更改。以下各节概述了最值得注意的部分：

- [第26.1节“新功能和组件”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__whats_new_in_2_0.html#spring-cloud-stream-preface-new-features)
- [第26.2节“显着增强”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__whats_new_in_2_0.html#spring-cloud-stream-preface-notable-enhancements)

## 26.1新功能和组件

- **轮询使用者**：引入轮询使用者，使应用程序可以控制消息处理速率。请参见 “ [第29.3.5，‘使用轮询消费者](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__programming_model.html#spring-cloud-streams-overview-using-polled-consumers) ’ ”的更多细节。您也可以阅读[此博客文章](https://spring.io/blog/2018/02/27/spring-cloud-stream-2-0-polled-consumers)以获取更多详细信息。
- **千分尺支持**：度量标准已切换为使用[千分尺](https://micrometer.io/)。 `MeterRegistry`还以Bean的形式提供，以便自定义应用程序可以将其自动连接以捕获自定义指标。有关更多详细信息[，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-stream-overview-metrics-emitter.html)请参见 “ [第37章，*度量标准发射器*](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-stream-overview-metrics-emitter.html) ”。
- **新的执行器绑定控件**：新的执行器绑定控件使您可以可视化并控制绑定的生命周期。有关更多详细信息，请参见[第30.6节“绑定可视化和控件”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-stream-overview-binders.html#_binding_visualization_and_control)。
- **可配置的RetryTemplate**：除了提供要配置的属性外`RetryTemplate`，我们现在还允许您提供自己的模板，有效地覆盖框架提供的模板。要使用它，请将其配置为`@Bean`您的应用程序。

## 26.2显着增强

此版本包括以下显着增强：

- [第26.2.1节“执行器和Web依赖关系现在都是可选的”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__whats_new_in_2_0.html#spring-cloud-stream-preface-actuator-web-dependencies)
- [第26.2.2节“内容类型协商的改进”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__whats_new_in_2_0.html#spring-cloud-stream-preface-content-type-negotiation-improvements)
- [第26.3节“显着的弃用”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__whats_new_in_2_0.html#spring-cloud-stream-preface-notable-deprecations)

### 26.2.1现在，执行器和Web依赖项都是可选的

如果既不需要执行器也不需要Web依赖项，此更改将减少已部署应用程序的占用空间。还可以通过手动添加以下依赖项之一，在反应式和常规Web范例之间进行切换。

以下清单显示了如何添加常规Web框架：

```xml
<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

以下清单显示了如何添加反应式Web框架：

```xml
<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

下表显示了如何添加执行器依赖性：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 26.2.2内容类型协商的改进

Verion 2.0的核心主题之一是围绕内容类型协商和消息转换的改进（在一致性和性能方面）。以下摘要概述了该领域的显着变化和改进。有关更多详细信息[，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_content-type-management.html)请参见“ [第32章，*内容类型协商*](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_content-type-management.html) ”部分。此外，[此博客文章还](https://spring.io/blog/2018/02/26/spring-cloud-stream-2-0-content-type-negotiation-and-transformation)包含更多详细信息。

- 现在，所有消息转换**仅**由`MessageConverter`对象处理。
- 我们引入了`@StreamMessageConverter`注释以提供自定义`MessageConverter`对象。
- 我们介绍了默认`Content Type`为`application/json`，需要迁移1.3应用程序或在混合模式下操作（即，1.3生产者→2.0消费者）时要考虑进去。
- 与文本消息的有效载荷和`contentType`的`text/…`或`…/json`不再转换为`Message`对于其中提供的参数类型的情况下`MessageHandler`不能确定（即，`public void handle(Message message)`或`public void handle(Object payload)`）。此外，强参数类型可能不足以正确转换消息，因此`contentType`header可以用作some的补充`MessageConverters`。

## 26.3显着弃用

从2.0版开始，不推荐使用以下项目：

- [第26.3.1节“ Java序列化（Java Native和Kryo）”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__whats_new_in_2_0.html#spring-cloud-stream-preface-deprecation-java-serialization)
- [第26.3.2节“不推荐使用的类和方法”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__whats_new_in_2_0.html#spring-cloud-stream-preface-deprecation-classes-methods)

### 26.3.1 Java序列化（Java本机和Kryo）

`JavaSerializationMessageConverter`并`KryoMessageConverter`暂时保留。但是，我们计划将来将它们移出核心软件包和支持。弃用此文件的主要原因是要标记分布式环境中生产者和使用者可能依赖于不同的JVM版本或具有不同版本的支持库（即Kryo）的，基于类型，特定于语言的序列化可能导致的问题。我们还想提请注意这样一个事实，即消费者和生产者甚至可能都不基于Java，因此，多语言风格的序列化（即JSON）更适合。

### 26.3.2不推荐使用的类和方法

以下是显着弃用的快速摘要。有关更多详细信息，请参见相应的{spring-cloud-stream-javadoc-current} [javadoc]。

- `SharedChannelRegistry`。使用`SharedBindingTargetRegistry`。
- `Bindings`。通过它合格豆已经通过独特的类型识别-例如，提供`Source`，`Processor`或自定义绑定：

```java
public interface Sample {
	String OUTPUT = "sampleOutput";

	@Output(Sample.OUTPUT)
	MessageChannel output();
}
```

- `HeaderMode.raw`。使用`none`，`headers`或`embeddedHeaders`
- `ProducerProperties.partitionKeyExtractorClass`赞成`partitionKeyExtractorName`和`ProducerProperties.partitionSelectorClass`赞成`partitionSelectorName`。这项更改确保了两个组件都由Spring配置和管理，并且以对Spring友好的方式被引用。
- `BinderAwareRouterBeanPostProcessor`。虽然该组件仍然存在，但不再是`BeanPostProcessor`，以后会重命名。
- `BinderProperties.setEnvironment(Properties environment)`。使用`BinderProperties.setEnvironment(Map environment)`。

本节将详细介绍如何使用Spring Cloud Stream。它涵盖了诸如创建和运行流应用程序之类的主题。