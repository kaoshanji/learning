# 32. Content Type Negotiation

## 32.内容类型协商

数据转换是任何消息驱动的微服务体系结构的核心功能之一。鉴于此，在Spring Cloud Stream中，此类数据表示为Spring `Message`，在到达目的地之前，可能必须将消息转换为所需的形状或大小。这样做有两个原因：

1. 转换传入消息的内容以匹配应用程序提供的处理程序的签名。
2. 将外发邮件的内容转换为有线格式。

有线格式通常是`byte[]`（对于Kafka和Rabbit活页夹来说是正确的），但是它由活页夹实现方式控制。

在Spring Cloud Stream中，消息转换是使用来完成的`org.springframework.messaging.converter.MessageConverter`。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 作为后续细节的补充，您可能还需要阅读以下[博客文章](https://spring.io/blog/2018/02/26/spring-cloud-stream-2-0-content-type-negotiation-and-transformation)。 |

## 32.1力学

为了更好地理解内容类型协商的机制和必要性，我们以下面的消息处理程序为例，看一个非常简单的用例：

```java
@StreamListener(Processor.INPUT)
@SendTo(Processor.OUTPUT)
public String handle(Person person) {..}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 为简单起见，我们假设这是应用程序中唯一的处理程序（我们假设没有内部管道）。 |

上例中显示的处理程序将一个`Person`对象作为参数，并产生一个`String`类型作为输出。为了使框架成功将传入`Message`的参数作为参数传递给此处理程序，它必须以某种方式将`Message`类型的有效负载从有线格式转换为`Person`类型。换句话说，框架必须找到并应用适当的`MessageConverter`。为此，框架需要用户的一些指示。这些指令之一已经由处理程序方法本身（`Person`类型）的签名提供。因此，从理论上讲，这应该是（并且在某些情况下是足够的）。但是，对于大多数用例来说，为了选择合适的`MessageConverter`，框架需要其他信息。那块缺失的是`contentType`。

Spring Cloud Stream提供了三种定义机制`contentType`（按优先顺序排列）：

1. **HEADER**：`contentType`可以通过Message本身进行通信。通过提供`contentType`标头，您可以声明用于定位和应用适当内容的内容类型`MessageConverter`。

2. **BINDING**：`contentType`可以通过设置`spring.cloud.stream.bindings.input.content-type`属性为每个目标绑定设置。

   | ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
   | ------------------------------------------------------------ |
   | `input`属性名称中的段对应于目的地的实际名称（在我们的示例中为“输入”）。通过这种方法，您可以按绑定声明用于查找和应用适当内容的内容类型`MessageConverter`。 |

3. **缺省值**：如果标题或绑定中`contentType`不存在`Message`缺省值，`application/json`则使用默认的内容类型来查找和应用适当的`MessageConverter`。

如前所述，前面的列表还演示了平局时的优先顺序。例如，标头提供的内容类型优先于任何其他内容类型。对于按绑定设置的内容类型也是如此，这实际上使您可以覆盖默认内容类型。但是，它也提供了明智的默认设置（由社区反馈确定）。

`application/json`设置默认值的另一个原因是由分布式微服务架构驱动的互操作性要求，在该架构中，生产者和使用者不仅可以在不同的JVM中运行，而且还可以在不同的非JVM平台上运行。

当非无效处理程序方法返回时，如果返回值已经是`Message`，则`Message`成为有效负载。但是，当返回值不是a时`Message`，将`Message`使用返回值作为有效负载构造新的对象，同时从输入中继承标题，再`Message`减去定义或过滤的标题`SpringIntegrationProperties.messageHandlerNotPropagatedHeaders`。默认情况下，此处仅设置一个标头：`contentType`。这意味着新的文件头`Message`没有`contentType`设置，从而确保`contentType`可以发展。您始终可以选择不`Message`从处理程序方法中返回a ，您可以在其中注入所需的任何标头。

如果有内部管道，`Message`则通过相同的转换过程将其发送到下一个处理程序。但是，如果没有内部管道，或者您已经到达内部管道的末端，则将其`Message`发送回输出目标。

### 32.1.1内容类型与参数类型

如前所述，要使框架选择适当的框架`MessageConverter`，它需要参数类型以及（可选）内容类型信息。选择合适的逻辑时，`MessageConverter`参数解析器（`HandlerMethodArgumentResolvers`）驻留，该解析器在调用用户定义的处理程序方法之前（即当框架知道实际的参数类型时）触发。如果参数类型与当前有效负载的类型不匹配，则框架将委派给预配置的堆栈，`MessageConverters`以查看其中是否有一个可以转换有效负载。如您所见，`Object fromMessage(Message message, Class targetClass);` MessageConverter 的操作`targetClass`作为其参数之一。该框架还确保提供的内容`Message`始终包含`contentType`标头。当没有contentType标头时，它会注入每绑定`contentType`标头或默认`contentType`标头。`contentType`参数类型的组合是框架确定消息是否可以转换为目标类型的机制。如果找不到合适的`MessageConverter`对象，则会引发异常，您可以通过添加定制来处理该异常`MessageConverter`（请参见“ [第32.3节，“用户定义的消息转换器”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_content-type-management.html#spring-cloud-stream-overview-user-defined-message-converters) ”）。

但是，如果有效负载类型与处理程序方法声明的目标类型匹配，该怎么办？在这种情况下，没有任何要转换的内容，并且有效载荷未经修改地传递。尽管这听起来很简单且合乎逻辑，但请记住使用a `Message`或`Object`作为参数的处理程序方法。通过声明目标类型为`Object`（`instanceof`Java中的所有内容），实际上就放弃了转换过程。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 不要期望`Message`仅基于转换为其他类型`contentType`。请记住，`contentType`是对目标类型的补充。如果需要，可以提供一个提示，`MessageConverter`可以考虑也可以不考虑。 |

### 32.1.2消息转换器

`MessageConverters` 定义两种方法：

```java
Object fromMessage(Message<?> message, Class<?> targetClass);

Message<?> toMessage(Object payload, @Nullable MessageHeaders headers);
```

了解这些方法的约定及其用法非常重要，尤其是在Spring Cloud Stream的上下文中。

该`fromMessage`方法将传入的`Message`转换为参数类型。的有效载荷`Message`可以是任何类型，并且取决于的实际实现`MessageConverter`以支持多种类型。例如，某些转换器JSON可以支持有效载荷类型为`byte[]`，`String`和其他。当应用程序包含内部管道（即输入→handler1→handler2→..→输出）并且上游处理程序的输出结果`Message`可能不是初始连线格式时，这一点很重要。

但是，该`toMessage`方法的合同更为严格，必须始终转换`Message`为有线格式：`byte[]`。

因此，出于所有意图和目的（尤其是在实现自己的转换器时），您将这两种方法视为具有以下签名：

```java
Object fromMessage(Message<?> message, Class<?> targetClass);

Message<byte[]> toMessage(Object payload, @Nullable MessageHeaders headers);
```

## 32.2提供的MessageConverters

如前所述，该框架已经提供了`MessageConverters`处理最常见用例的堆栈。以下列表`MessageConverters`按优先级描述了提供的，（使用了第一个`MessageConverter`有效的）：

1. `ApplicationJsonMessageMarshallingConverter`：的变化`org.springframework.messaging.converter.MappingJackson2MessageConverter`。的有效载荷的支撑件转换`Message`到/从POJO为情况下，当`contentType`是`application/json`（默认）。
2. `TupleJsonMessageConverter`：**DEPRECATED**支持`Message`to / from 的有效载荷的转换`org.springframework.tuple.Tuple`。
3. `ByteArrayMessageConverter`：的有效载荷的支架转换`Message`从`byte[]`到`byte[]`用于情况下，当`contentType`是`application/octet-stream`。它本质上是一个传递，主要是为了向后兼容而存在。
4. `ObjectStringMessageConverter`：支持将任何类型转换为`String`when `contentType`is `text/plain`。它调用Object的`toString()`方法，或者，如果有效载荷为`byte[]`new ，则调用它`String(byte[])`。
5. `JavaSerializationMessageConverter`：**DEPRECATED**支持在`contentType`is 时基于Java序列化进行转换`application/x-java-serialized-object`。
6. `KryoMessageConverter`：**DEPRECATED**支持在`contentType`is 时基于Kryo序列化的转换`application/x-java-object`。
7. `JsonUnmarshallingConverter`：类似于`ApplicationJsonMessageMarshallingConverter`。支持`contentType`is 时任何类型的转换`application/x-java-object`。它期望将实际的类型信息`contentType`作为属性嵌入到中（例如`application/x-java-object;type=foo.bar.Cat`）。

当找不到合适的转换器时，框架将引发异常。发生这种情况时，您应该检查代码和配置，并确保您没有错过任何内容（即，确保`contentType`通过使用绑定或标头提供了a）。但是，很可能您发现了一些不常见的情况（例如自定义的情况`contentType`），并且提供的当前堆栈`MessageConverters` 不知道如何转换。在这种情况下，您可以添加custom `MessageConverter`。请参见[第32.3节“用户定义的消息转换器”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_content-type-management.html#spring-cloud-stream-overview-user-defined-message-converters)。

## 32.3用户定义的消息转换器

Spring Cloud Stream公开了一种定义和注册其他机制`MessageConverters`。要使用它，请实施并将其`org.springframework.messaging.converter.MessageConverter`配置为`@Bean`，并使用进行注释`@StreamMessageConverter`。然后将其附加到MessageConverter的现有堆栈中。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 重要的是要了解将自定义`MessageConverter`实现添加到现有堆栈的头部。因此，自定义`MessageConverter`实现优先于现有实现，这样您就可以覆盖现有实现并将其添加到现有转换器中。 |

以下示例说明如何创建消息转换器Bean以支持称为的新内容类型`application/bar`：

```java
@EnableBinding(Sink.class)
@SpringBootApplication
public static class SinkApplication {

    ...

    @Bean
    @StreamMessageConverter
    public MessageConverter customMessageConverter() {
        return new MyCustomMessageConverter();
    }
}

public class MyCustomMessageConverter extends AbstractMessageConverter {

    public MyCustomMessageConverter() {
        super(new MimeType("application", "bar"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return (Bar.class.equals(clazz));
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        Object payload = message.getPayload();
        return (payload instanceof Bar ? payload : new Bar((byte[]) payload));
    }
}
```

Spring Cloud Stream还支持基于Avro的转换器和模式演变。有关详细信息[，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html)请参见“ [第33章，*模式演化支持*](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html) ”。