# 34、消息传递

Spring框架为与消息传递系统集成提供了广泛的支持，从JMS API的简化使用`JmsTemplate`到完整的异步接收消息的基础结构。Spring AMQP为高级消息队列协议提供了类似的功能集。Spring Boot还为`RabbitTemplate`和RabbitMQ 提供了自动配置选项。Spring WebSocket本身就包含对STOMP消息的支持，而Spring Boot通过启动程序和少量的自动配置对此提供了支持。Spring Boot还支持Apache Kafka。



## 34.2 AMQP

高级消息队列协议（AMQP）是面向消息中间件的与平台无关的有线级别协议。Spring AMQP项目将Spring的核心概念应用于基于AMQP的消息传递解决方案的开发。Spring Boot为通过RabbitMQ使用AMQP提供了许多便利，包括`spring-boot-starter-amqp`“启动器”。

### 34.2.1 RabbitMQ支持

[RabbitMQ](https://www.rabbitmq.com/)是基于AMQP协议的轻型，可靠，可伸缩和便携式消息代理。Spring用于`RabbitMQ`通过AMQP协议进行通信。

RabbitMQ配置由中的外部配置属性控制`spring.rabbitmq.*`。例如，您可以在中声明以下部分`application.properties`：

```bash
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=secret
```

如果`ConnectionNameStrategy`上下文中存在bean，它将自动用于命名由auto-configured创建的连接`ConnectionFactory`。请参阅[`RabbitProperties`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/amqp/RabbitProperties.java)以获取更多受支持的选项。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 有关更多详细信息[，](https://spring.io/blog/2010/06/14/understanding-amqp-the-protocol-used-by-rabbitmq/)请参阅[了解RabbitMQ使用的协议AMQP](https://spring.io/blog/2010/06/14/understanding-amqp-the-protocol-used-by-rabbitmq/)。 |

### 34.2.2发送消息

Spring `AmqpTemplate`和and `AmqpAdmin`是自动配置的，您可以将它们直接自动连接到自己的bean中，如以下示例所示：

```java
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyBean {

	private final AmqpAdmin amqpAdmin;
	private final AmqpTemplate amqpTemplate;

	@Autowired
	public MyBean(AmqpAdmin amqpAdmin, AmqpTemplate amqpTemplate) {
		this.amqpAdmin = amqpAdmin;
		this.amqpTemplate = amqpTemplate;
	}

	// ...

}
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| [`RabbitMessagingTemplate`](https://docs.spring.io/spring-amqp/docs/2.1.11.RELEASE/api/org/springframework/amqp/rabbit/core/RabbitMessagingTemplate.html)可以以类似方式注入。如果`MessageConverter`定义了bean，它将自动关联到auto-configured `AmqpTemplate`。 |

如有必要，任何`org.springframework.amqp.core.Queue`定义为bean的对象都会自动用于在RabbitMQ实例上声明相应的队列。

要重试操作，可以在以下位置启用重试`AmqpTemplate`（例如，在代理连接丢失的情况下）：

```bash
spring.rabbitmq.template.retry.enabled=true
spring.rabbitmq.template.retry.initial-interval=2s
```

默认情况下，重试是禁用的。您也可以`RetryTemplate`通过声明`RabbitRetryTemplateCustomizer`bean来以编程方式自定义。

### 34.2.3接收消息

存在Rabbit基础结构时，可以对任何bean进行注释`@RabbitListener`以创建侦听器端点。如果`RabbitListenerContainerFactory`未定义，则将`SimpleRabbitListenerContainerFactory`自动配置默认值，并且您可以使用`spring.rabbitmq.listener.type`属性切换到直接容器。如果定义了a `MessageConverter`或`MessageRecoverer`bean，它将自动与默认工厂关联。

以下示例组件在`someQueue`队列上创建一个侦听器端点：

```java
@Component
public class MyBean {

	@RabbitListener(queues = "someQueue")
	public void processMessage(String content) {
		// ...
	}

}
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 有关更多详细信息，请参见[的Javadoc`@EnableRabbit`](https://docs.spring.io/spring-amqp/docs/2.1.11.RELEASE/api/org/springframework/amqp/rabbit/annotation/EnableRabbit.html)。 |

如果您需要创建更多`RabbitListenerContainerFactory`实例，或者想要覆盖默认实例，Spring Boot提供了一个`SimpleRabbitListenerContainerFactoryConfigurer`和`DirectRabbitListenerContainerFactoryConfigurer`，您可以使用`SimpleRabbitListenerContainerFactory`和`DirectRabbitListenerContainerFactory`自动配置所使用的工厂相同的设置来初始化一个和。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 选择哪种容器都没有关系。这两个bean通过自动配置公开。         |

例如，以下配置类公开了另一个使用特定工厂的工厂`MessageConverter`：

```java
@Configuration
static class RabbitConfiguration {

	@Bean
	public SimpleRabbitListenerContainerFactory myFactory(
			SimpleRabbitListenerContainerFactoryConfigurer configurer) {
		SimpleRabbitListenerContainerFactory factory =
				new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setMessageConverter(myMessageConverter());
		return factory;
	}

}
```

然后，您可以按任何带`@RabbitListener`注释的方法使用工厂，如下所示：

```java
@Component
public class MyBean {

	@RabbitListener(queues = "someQueue", containerFactory="myFactory")
	public void processMessage(String content) {
		// ...
	}

}
```

您可以启用重试来处理侦听器引发异常的情况。默认情况下，`RejectAndDontRequeueRecoverer`使用，但是您可以定义`MessageRecoverer`自己的a。重试用尽后，如果将代理配置为这样做，则消息将被拒绝并被丢弃或路由到死信交换。默认情况下，重试是禁用的。您也可以`RetryTemplate`通过声明`RabbitRetryTemplateCustomizer`bean来以编程方式自定义。

| ![[重要]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 默认情况下，如果禁用了重试，并且侦听器抛出异常，则会无限期地重试传递。您可以通过两种方式修改此行为：将`defaultRequeueRejected`属性设置为，`false`以便尝试进行零次重新传递，或者抛出`AmqpRejectAndDontRequeueException`来指示应该拒绝该消息。后者是启用重试并达到最大传递尝试次数时使用的机制。 |      |

## 34.3 Apache Kafka支持

通过提供`spring-kafka`项目的自动配置来支持[Apache Kafka](https://kafka.apache.org/)。

Kafka配置由中的外部配置属性控制`spring.kafka.*`。例如，您可以在中声明以下部分`application.properties`：

```bash
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=myGroup
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 要在启动时创建主题，请添加类型为的Bean `NewTopic`。如果该主题已经存在，则将忽略Bean。 |

请参阅[`KafkaProperties`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/kafka/KafkaProperties.java)以获取更多受支持的选项。

### 34.3.1发送消息

Spring `KafkaTemplate`是自动配置的，您可以直接在自己的Bean中自动对其进行布线，如以下示例所示：

```java
@Component
public class MyBean {

	private final KafkaTemplate kafkaTemplate;

	@Autowired
	public MyBean(KafkaTemplate kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	// ...

}
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果`spring.kafka.producer.transaction-id-prefix`定义了属性，`KafkaTransactionManager`则会自动配置a。另外，如果`RecordMessageConverter`定义了bean，它将自动关联到auto-configured `KafkaTemplate`。 |

### 34.3.2接收消息

存在Apache Kafka基础结构时，可以对任何bean进行注释`@KafkaListener`以创建侦听器端点。如果`KafkaListenerContainerFactory`尚未定义，则会使用中定义的键自动配置默认值`spring.kafka.listener.*`。

以下组件在该`someTopic`主题上创建一个侦听器端点：

```java
@Component
public class MyBean {

	@KafkaListener(topics = "someTopic")
	public void processMessage(String content) {
		// ...
	}

}
```

如果`KafkaTransactionManager`定义了bean，它将自动关联到容器工厂。类似地，如果一个`RecordMessageConverter`，`ErrorHandler`或`AfterRollbackProcessor`豆被定义，它被自动关联为出厂默认。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| `ChainedKafkaTransactionManager`必须标记一个自定义，`@Primary`因为它通常引用自动配置的`KafkaTransactionManager`bean。 |

### 34.3.3 Kafka流

用于Apache Kafka的Spring提供了一个工厂bean来创建`StreamsBuilder`对象并管理其流的生命周期。`KafkaStreamsConfiguration`只要`kafka-streams`在类路径上是Spring Boot 并通过`@EnableKafkaStreams`注释启用Kafka Streams，Spring Boot就会自动配置所需的bean 。

启用Kafka Streams意味着必须设置应用程序ID和引导服务器。可以使用来配置前者`spring.kafka.streams.application-id`，`spring.application.name`如果未设置，则默认为。后者可以全局设置，也可以仅针对流进行覆盖。

使用专用属性可以使用几个附加属性。可以使用`spring.kafka.streams.properties`名称空间设置其他任意Kafka属性。另请参见[第34.3.4节“其他Kafka属性”](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-messaging.html#boot-features-kafka-extra-props)。

要使用工厂bean，只需将其连接`StreamsBuilder`到您的工厂，`@Bean`如以下示例所示：

```java
@Configuration
@EnableKafkaStreams
static class KafkaStreamsExampleConfiguration {

	@Bean
	public KStream<Integer, String> kStream(StreamsBuilder streamsBuilder) {
		KStream<Integer, String> stream = streamsBuilder.stream("ks1In");
		stream.map((k, v) -> new KeyValue<>(k, v.toUpperCase())).to("ks1Out",
				Produced.with(Serdes.Integer(), new JsonSerde<>()));
		return stream;
	}

}
```

默认情况下，由`StreamBuilder`它创建的对象管理的流将自动启动。您可以使用`spring.kafka.streams.auto-startup`属性来自定义此行为。

### 34.3.4 Kafka的其他属性

自动配置支持的属性显示在[附录A，*通用应用程序属性中*](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/common-application-properties.html)。请注意，在大多数情况下，这些属性（连字符或camelCase）直接映射到Apache Kafka点缀的属性。有关详细信息，请参阅Apache Kafka文档。

这些属性的前几个属性适用于所有组件（生产者，使用者，管理员和流），但如果您希望使用不同的值，则可以在组件级别上指定。Apache Kafka指定重要性为HIGH，MEDIUM或LOW的属性。Spring Boot自动配置支持所有HIGH重要性属性，一些选定的MEDIUM和LOW属性以及任何没有默认值的属性。

`KafkaProperties`该类直接提供了Kafka支持的属性的子集。如果希望使用不直接支持的其他属性来配置生产者或使用者，请使用以下属性：

```bash
spring.kafka.properties.prop.one=first
spring.kafka.admin.properties.prop.two=second
spring.kafka.consumer.properties.prop.three=third
spring.kafka.producer.properties.prop.four=fourth
spring.kafka.streams.properties.prop.five=fifth
```

这会将常见的`prop.one`Kafka属性设置为`first`（适用于生产者，消费者和管理员），将`prop.two`admin属性设置为`second`，将`prop.three`消费者属性设置为`third`，将`prop.four`生产者属性设置为`fourth`，并将`prop.five`stream属性设置为`fifth`。

您还可以`JsonDeserializer`按如下方式配置Spring Kafka ：

```bash
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.value.default.type=com.example.Invoice
spring.kafka.consumer.properties.spring.json.trusted.packages=com.example,org.acme
```

同样，您可以禁用`JsonSerializer`在标头中发送类型信息的默认行为：

```bash
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.producer.properties.spring.json.add.type.headers=false
```

| ![[重要]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 以这种方式设置的属性将覆盖Spring Boot显式支持的任何配置项。  |      |