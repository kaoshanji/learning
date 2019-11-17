# 39. Apache Kafka Binder

## 39. Apache Kafka活页夹

## 39.1使用

要使用Apache Kafka活页夹，您需要将其`spring-cloud-stream-binder-kafka`作为依赖项添加到Spring Cloud Stream应用程序中，如以下Maven示例所示：

```xml
<dependency> 
  <groupId> org.springframework.cloud </ groupId> 
  <artifactId> spring-cloud-stream-binder-kafka </ artifactId> 
</ dependency>
```

另外，您也可以使用Spring Cloud Stream Kafka Starter，如以下针对Maven的示例所示：

```xml
<dependency> 
  <groupId> org.springframework.cloud </ groupId> 
  <artifactId> spring-cloud-starter-stream-kafka </ artifactId> 
</ dependency>
```

## 39.2 Apache Kafka Binder概述

下图显示了Apache Kafka活页夹的工作方式的简化图：



**图39.1。卡夫卡·宾德**

![卡夫卡粘合剂](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/images/kafka-binder.png)



Apache Kafka Binder实现将每个目标映射到一个Apache Kafka主题。消费者组直接映射到相同的Apache Kafka概念。分区也直接映射到Apache Kafka分区。

活页夹当前使用Apache Kafka `kafka-clients`1.0.0 jar，并且设计用于至少该版本的代理。该客户端可以与较旧的代理进行通信（请参阅Kafka文档），但是某些功能可能不可用。例如，对于低于0.11.xx的版本，不支持本机头。此外，0.11.xx不支持该`autoAddPartitions`属性。

## 39.3配置选项

本节包含Apache Kafka活页夹使用的配置选项。

有关与活页夹有关的常见配置选项和属性，请参阅[核心文档](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__configuration_options.html#binding-properties)。

### 39.3.1 Kafka活页夹属性

- spring.cloud.stream.kafka.binder.brokers

  Kafka活页夹连接到的经纪人列表。默认值：`localhost`。

- spring.cloud.stream.kafka.binder.defaultBrokerPort

  `brokers`允许指定带有或不带有端口信息的主机（例如`host1,host2:port2`）。当代理列表中未配置任何端口时，这将设置默认端口。默认值：`9092`。

- spring.cloud.stream.kafka.binder.configuration

  客户端属性（生产者和消费者）的键/值映射传递给绑定程序创建的所有客户端。由于生产者和消费者都使用了这些属性，因此应将使用限制为通用属性，例如安全性设置。此处的属性取代引导中设置的所有属性。默认值：空地图。

- spring.cloud.stream.kafka.binder.consumerProperties

  任意Kafka客户端使用者属性的键/值映射。此处的属性将取代引导和`configuration`上述属性中设置的所有属性。默认值：空地图。

- spring.cloud.stream.kafka.binder.headers

  活页夹传输的自定义标头列表。仅当与`kafka-clients`版本<0.11.0.0的较旧应用程序（⇐1.3.x ）通信时才需要。较新的版本本机支持标头。默认值：空。

- spring.cloud.stream.kafka.binder.healthTimeout

  等待获取分区信息的时间，以秒为单位。如果此计时器到期，运行状况将报告为已关闭。默认值：10

- spring.cloud.stream.kafka.binder.requiredAcks

  代理程序上所需的确认数。有关生产者`acks`属性，请参见Kafka文档。默认值：`1`。

- spring.cloud.stream.kafka.binder.minPartitionCount

  仅在`autoCreateTopics`或`autoAddPartitions`设置时有效。活页夹在生成或使用数据的主题上配置的全局最小分区数。可以通过`partitionCount`生产者的设置或生产者的`instanceCount * concurrency`设置值（如果较大者）代替。默认值：`1`。

- spring.cloud.stream.kafka.binder.producer属性

  任意Kafka客户端生产者属性的键/值映射。此处的属性将取代引导和`configuration`上述属性中设置的所有属性。默认值：空地图。

- spring.cloud.stream.kafka.binder.replicationFactor

  自动创建的主题的复制因子（如果`autoCreateTopics`处于活动状态）。可以在每个绑定上覆盖。默认值：`1`。

- spring.cloud.stream.kafka.binder.autoCreateTopics

  如果设置为`true`，活页夹将自动创建新主题。如果设置为`false`，则活页夹依赖于已配置的主题。在后一种情况下，如果主题不存在，则活页夹无法启动。![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png)此设置独立`auto.topic.create.enable`于代理的设置，并且不影响它。如果服务器设置为自动创建主题，则可以使用默认代理设置将它们作为元数据检索请求的一部分进行创建。默认值：`true`。

- spring.cloud.stream.kafka.binder.autoAddPartitions

  如果设置为`true`，则活页夹将根据需要创建新的分区。如果设置为`false`，则活页夹依赖于已配置的主题的分区大小。如果目标主题的分区数小于预期值，则活页夹无法启动。默认值：`false`。

- spring.cloud.stream.kafka.binder.transaction.transactionIdPrefix

  在活页夹中启用事务。请参阅`transaction.id`Kafka文档和[Transactions](https://docs.spring.io/spring-kafka/reference/html/_reference.html#transactions)中的`spring-kafka`文档。启用事务后，`producer`将忽略单个属性，并且所有生产者都将使用这些`spring.cloud.stream.kafka.binder.transaction.producer.*`属性。默认`null`（无交易）

- spring.cloud.stream.kafka.binder.transaction.producer。*

  交易绑定中生产者的全球生产者属性。见`spring.cloud.stream.kafka.binder.transaction.transactionIdPrefix`和[第39.3.3，“海边的卡夫卡制片属性”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__apache_kafka_binder.html#kafka-producer-properties)和所有粘合剂支持的总制片人的属性。默认值：请参见各个生产者属性。

- spring.cloud.stream.kafka.binder.headerMapperBeanName

  `KafkaHeaderMapper`用于将`spring-messaging`标头映射到Kafka标头或从标头映射标头的bean名称。例如，如果您希望自定义`DefaultKafkaHeaderMapper`对标头使用JSON反序列化的中的受信任软件包，请使用此选项。默认值：无。

### 39.3.2 Kafka消费者属性

以下属性仅适用于Kafka使用者，并且必须以开头`spring.cloud.stream.kafka.bindings..consumer.`。

- 管理员配置

  `Map`设置主题时使用的一个Kafka主题属性-例如，`spring.cloud.stream.kafka.bindings.input.consumer.admin.configuration.message.format.version=0.9.0.0`默认值：无。

- 管理员副本分配

  副本分配的Map <Integer，List <Integer >>，键为分区，值为分配。在配置新主题时使用。请参阅jar中的`NewTopic`Javadocs `kafka-clients`。默认值：无。

- 管理员复制因子

  设置主题时要使用的复制因子。覆盖活页夹范围的设置。忽略是否`replicas-assignments`存在。默认值：无（使用资料夹范围的默认值1）。

- autoRebalanceEnabled

  如果为`true`，则主题分区将在使用者组的成员之间自动重新平衡。何时`false`，将基于`spring.cloud.stream.instanceCount`和为每个使用者分配固定的一组分区`spring.cloud.stream.instanceIndex`。这要求在每个启动的实例上都正确设置`spring.cloud.stream.instanceCount`和`spring.cloud.stream.instanceIndex`属性。`spring.cloud.stream.instanceCount`在这种情况下，属性的值通常必须大于1。默认值：`true`。

- ackEachRecord

  当`autoCommitOffset`是`true`时，此设置使然是否提交每个记录处理后的偏移量。默认情况下，偏移量在返回的记录批次中的所有记录`consumer.poll()`都已处理后才提交。轮询返回的记录数可以使用`max.poll.records`Kafka属性控制，该属性是通过使用者`configuration`属性设置的。将此设置为`true`可能会导致性能下降，但是这样做会减少发生故障时重新传送记录的可能性。另外，请参见活页夹`requiredAcks`属性，它也影响提交偏移量的性能。默认值：`false`。

- autoCommitOffset

  处理消息后是否自动提交偏移量。如果设置为`false`，则入站消息中将出现带有`kafka_acknowledgment`类型`org.springframework.kafka.support.Acknowledgment`标头关键字的标头。应用程序可以使用此标头来确认消息。有关详细信息，请参见示例部分。当此属性设置`false`为时，Kafka活页夹将ack模式设置为，`org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode.MANUAL`应用程序负责确认记录。另请参阅`ackEachRecord`。默认值：`true`。

- autoCommitOnError

  仅当`autoCommitOffset`设置为时有效`true`。如果设置为`false`，它将抑制导致错误的消息的自动提交，并且仅对成功的消息进行提交。如果持续出现故障，它允许流从上次成功处理的消息自动重播。如果设置为`true`，它将始终自动提交（如果启用了自动提交）。如果未设置（默认值），则它的有效值与相同（`enableDlq`如果将错误消息发送到DLQ，则自动提交错误消息，否则不提交）。默认值：未设置。

- resetOffsets

  是否将使用者的偏移量重置为startOffset提供的值。默认值：`false`。

- startOffset

  新组的起始偏移量。允许的值：`earliest`和`latest`。如果为消费者“绑定”（通过`spring.cloud.stream.bindings..group`）显式设置了消费者组，则将“ startOffset”设置为`earliest`。否则，将其设置`latest`为`anonymous`消费者组。另请参阅`resetOffsets`（此列表的前面）。默认值：null（等效于`earliest`）。

- enableDlq

  设置为true时，它将为使用者启用DLQ行为。默认情况下，导致错误的消息将转发到名为的主题`error..`。该DLQ主题名称可以通过设置配置`dlqName`属性。当错误数量相对较小并且重放整个原始主题可能太麻烦时，这为更常见的Kafka重播方案提供了一个替代选项。有关更多信息[，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__apache_kafka_binder.html#kafka-dlq-processing)请参见[第39.6节“死信主题处理”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__apache_kafka_binder.html#kafka-dlq-processing)处理。从2.0版开始，发送到DLQ主题的消息已得到增强，以下标题：`x-original-topic`，`x-exception-message`，和`x-exception-stacktrace`作为`byte[]`。 **是****时不允许。`destinationIsPattern``true`**默认值：`false`。

- 组态

  使用包含通用Kafka使用者属性的键/值对进行映射。默认值：空地图。

- dlqName

  接收错误消息的DLQ主题的名称。默认值：null（如果未指定，则将导致错误的消息转发到名为的主题`error..`）。

- dlqProducerProperties

  使用此功能，可以设置特定于DLQ的生产者属性。通过kafka生产者属性可以使用的所有属性都可以通过该属性设置。默认值：默认的Kafka生产者属性。

- 标头

  指示入站通道适配器填充哪些标准头。允许值：`none`，`id`，`timestamp`，或`both`。如果使用本机反序列化并且第一个组件需要接收消息`id`（例如配置为使用JDBC消息存储的聚合器），则很有用。默认： `none`

- converterBeanName

  实现的bean的名称`RecordMessageConverter`。在入站通道适配器中用于替换默认适配器`MessagingMessageConverter`。默认： `null`

- idleEventInterval

  事件之间的间隔（以毫秒为单位），指示最近未接收到任何消息。使用`ApplicationListener`接收这些事件。有关使用示例，请参见[“示例：暂停和恢复](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__apache_kafka_binder.html#pause-resume)使用方[”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__apache_kafka_binder.html#pause-resume)一节。默认： `30000`

- destinationIsPattern

  如果为true，则目的地将被视为正则表达式，`Pattern`用于由代理匹配主题名称。设置为true时，将不会设置主题，`enableDlq`也不允许使用主题，因为绑定者在设置阶段不知道主题名称。请注意，检测与模式匹配的新主题所需的时间由消费者属性控制，该属性`metadata.max.age.ms`（在撰写本文时）默认为300,000ms（5分钟）。可以使用`configuration`上面的属性进行配置。默认： `false`

### 39.3.3 Kafka生产者属性

以下属性仅适用于Kafka生产者，并且必须以开头`spring.cloud.stream.kafka.bindings..producer.`。

- 管理员配置

  `Map`设置新主题时使用的一个Kafka主题属性-例如，`spring.cloud.stream.kafka.bindings.input.consumer.admin.configuration.message.format.version=0.9.0.0`默认值：无。

- 管理员副本分配

  副本分配的Map <Integer，List <Integer >>，键为分区，值为分配。在配置新主题时使用。参见jar中的`NewTopic`javadocs `kafka-clients`。默认值：无。

- 管理员复制因子

  设置新主题时要使用的复制因子。覆盖活页夹范围的设置。忽略是否`replicas-assignments`存在。默认值：无（使用资料夹范围的默认值1）。

- 缓冲区大小

  Kafka生产者在发送前尝试分批处理的数据量的上限（以字节为单位）。默认值：`16384`。

- 同步

  生产者是否同步。默认值：`false`。

- batchTimeout

  生产者在发送消息之前等待允许更多消息在同一批中累积的时间。（通常，生产者根本不等待，仅发送在上一次发送过程中累积的所有消息。）非零值可能会增加吞吐量，但会增加延迟。默认值：`0`。

- messageKeyExpression

  根据用于填充产生的Kafka消息密钥的传出消息评估SpEL表达式，例如`headers['myKey']`。有效负载无法使用，因为在评估此表达式时，有效负载已经采用a的形式`byte[]`。默认值：`none`。

- headerPatterns

  用逗号分隔的简单模式列表匹配春消息头被映射到卡夫卡`Headers`的`ProducerRecord`。模式可以以通配符（星号）开头或结尾。可以通过加前缀来否定模式`!`。比赛在第一个比赛（正数或负数）之后停止。例如`!ask,as*`将通过，`ash`但不会通过`ask`。 `id`并且`timestamp`永远不会被映射。默认值：（`*`所有标头- `id`和除外`timestamp`）

- 组态

  使用包含通用Kafka生产者属性的键/值对进行映射。默认值：空地图。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| Kafka活页夹使用`partitionCount`生产者的设置作为提示来创建具有给定分区数的主题（与结合使用`minPartitionCount`，两者中的最大值是所使用的值）。`minPartitionCount`为活页夹和`partitionCount`应用程序进行配置时，请谨慎使用，因为使用了较大的值。如果主题已经存在且分区数较小且`autoAddPartitions`已禁用（默认设置），则绑定器无法启动。如果已经存在具有较小分区数的主题，并且`autoAddPartitions`已启用该主题，那么将添加新的分区。如果已经存在的主题的分区数量大于（`minPartitionCount`或`partitionCount`）的最大值，则使用现有的分区数。 |

### 39.3.4使用示例

在本节中，我们将说明针对特定方案使用前面的属性。

#### 例如：设置`autoCommitOffset`要`false`依靠手动ACKING

此示例说明了如何在用户应用程序中手动确认偏移。

此示例要求将`spring.cloud.stream.kafka.bindings.input.consumer.autoCommitOffset`其设置为`false`。在您的示例中使用相应的输入通道名称。

```java
@SpringBootApplication
@EnableBinding(Sink.class)
public class ManuallyAcknowdledgingConsumer {

 public static void main(String[] args) {
     SpringApplication.run(ManuallyAcknowdledgingConsumer.class, args);
 }

 @StreamListener(Sink.INPUT)
 public void process(Message<?> message) {
     Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
     if (acknowledgment != null) {
         System.out.println("Acknowledgment provided");
         acknowledgment.acknowledge();
     }
 }
}
```

#### 示例：安全配置

Apache Kafka 0.9支持客户端和代理之间的安全连接。要利用此功能，请遵循[Apache Kafka文档](https://kafka.apache.org/090/documentation.html#security_configclients)中的[准则以及Confluent文档中](http://docs.confluent.io/2.0.0/kafka/security.html)的Kafka 0.9 [安全准则](http://docs.confluent.io/2.0.0/kafka/security.html)。使用该`spring.cloud.stream.kafka.binder.configuration`选项为活页夹创建的所有客户端设置安全属性。

例如，要设置`security.protocol`为`SASL_SSL`，请设置以下属性：

```
spring.cloud.stream.kafka.binder.configuration.security.protocol=SASL_SSL
```

可以以类似方式设置所有其他安全属性。

使用Kerberos时，请遵循[参考文档](https://kafka.apache.org/090/documentation.html#security_sasl_clientconfig)中的[说明](https://kafka.apache.org/090/documentation.html#security_sasl_clientconfig)来创建和引用JAAS配置。

Spring Cloud Stream支持通过使用JAAS配置文件和Spring Boot属性将JAAS配置信息传递到应用程序。

##### 使用JAAS配置文件

可以使用系统属性为Spring Cloud Stream应用程序设置JAAS和（可选）krb5文件位置。以下示例显示如何通过使用JAAS配置文件使用SASL和Kerberos启动Spring Cloud Stream应用程序：

```bash
 java -Djava.security.auth.login.config=/path.to/kafka_client_jaas.conf -jar log.jar \
   --spring.cloud.stream.kafka.binder.brokers=secure.server:9092 \
   --spring.cloud.stream.bindings.input.destination=stream.ticktock \
   --spring.cloud.stream.kafka.binder.configuration.security.protocol=SASL_PLAINTEXT
```

##### 使用Spring Boot属性

作为使用JAAS配置文件的替代方法，Spring Cloud Stream提供了一种通过使用Spring Boot属性为Spring Cloud Stream应用程序设置JAAS配置的机制。

以下属性可用于配置Kafka客户端的登录上下文：

- spring.cloud.stream.kafka.binder.jaas.loginModule

  登录模块名称。正常情况下无需设置。默认值：`com.sun.security.auth.module.Krb5LoginModule`。

- spring.cloud.stream.kafka.binder.jaas.controlFlag

  登录模块的控制标志。默认值：`required`。

- spring.cloud.stream.kafka.binder.jaas.options

  使用包含登录模块选项的键/值对进行映射。默认值：空地图。

以下示例显示如何通过使用Spring Boot配置属性使用SASL和Kerberos启动Spring Cloud Stream应用程序：

```bash
 java --spring.cloud.stream.kafka.binder.brokers=secure.server:9092 \
   --spring.cloud.stream.bindings.input.destination=stream.ticktock \
   --spring.cloud.stream.kafka.binder.autoCreateTopics=false \
   --spring.cloud.stream.kafka.binder.configuration.security.protocol=SASL_PLAINTEXT \
   --spring.cloud.stream.kafka.binder.jaas.options.useKeyTab=true \
   --spring.cloud.stream.kafka.binder.jaas.options.storeKey=true \
   --spring.cloud.stream.kafka.binder.jaas.options.keyTab=/etc/security/keytabs/kafka_client.keytab \
   --spring.cloud.stream.kafka.binder.jaas.options.principal=kafka-client-1@EXAMPLE.COM
```

前面的示例表示以下JAAS文件的等效项：

```java
KafkaClient {
    com.sun.security.auth.module.Krb5LoginModule required
    useKeyTab=true
    storeKey=true
    keyTab="/etc/security/keytabs/kafka_client.keytab"
    principal="kafka-client-1@EXAMPLE.COM";
};
```

如果所需的主题已经存在于代理上或将由管理员创建，则可以关闭自动创建，仅需要发送客户端JAAS属性。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 不要在同一应用程序中混合使用JAAS配置文件和Spring Boot属性。如果`-Djava.security.auth.login.config`系统属性已经存在，Spring Cloud Stream将忽略Spring Boot属性。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 与`autoCreateTopics`和`autoAddPartitions`一起使用Kerberos 时要小心。通常，应用程序可能使用在Kafka和Zookeeper中没有管理权限的主体。因此，依靠Spring Cloud Stream创建/修改主题可能会失败。在安全的环境中，我们强烈建议您使用Kafka工具创建主题并以管理方式管理ACL。 |

#### 示例：暂停和恢复使用方

如果您希望暂停使用但不引起分区重新平衡，则可以暂停并恢复使用方。通过将`Consumer`as作为参数添加到中，可以方便地进行操作`@StreamListener`。要恢复，您需要一个`ApplicationListener`for `ListenerContainerIdleEvent`实例。事件的发布频率由`idleEventInterval`媒体资源控制。由于使用者不是线程安全的，因此必须在调用线程上调用这些方法。

以下简单的应用程序显示了如何暂停和恢复：

```java
@SpringBootApplication
@EnableBinding(Sink.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@StreamListener(Sink.INPUT)
	public void in(String in, @Header(KafkaHeaders.CONSUMER) Consumer<?, ?> consumer) {
		System.out.println(in);
		consumer.pause(Collections.singleton(new TopicPartition("myTopic", 0)));
	}

	@Bean
	public ApplicationListener<ListenerContainerIdleEvent> idleListener() {
		return event -> {
			System.out.println(event);
			if (event.getConsumer().paused().size() > 0) {
				event.getConsumer().resume(event.getConsumer().paused());
			}
		};
	}

}
```

## 39.4错误通道

从版本1.3开始，绑定程序无条件地将异常发送到每个使用者目标的错误通道，也可以将其配置为将异步生产者发送失败消息发送到错误通道。有关更多信息[，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__programming_model.html#spring-cloud-stream-overview-error-handling)请参见[第29.4节“错误处理”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__programming_model.html#spring-cloud-stream-overview-error-handling)。

`ErrorMessage`发送失败的有效负载是`KafkaSendFailureException`具有属性的：

- `failedMessage`：`Message`未能发送的Spring Messaging 。
- `record`：`ProducerRecord`从中创建的原始`failedMessage`

没有生产者异常的自动处理（例如发送到[Dead-Letter队列](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__apache_kafka_binder.html#kafka-dlq-processing)）。您可以使用自己的Spring Integration流使用这些异常。

## 39.5卡夫卡指标

Kafka活页夹模块公开以下指标：

`spring.cloud.stream.binder.kafka.offset`：此指标指示给定的消费群体尚未从给定的活页夹主题中消费多少消息。提供的指标基于Mircometer指标库。度量标准包含消费者组信息，主题以及与主题上的最新偏移量有关的承诺偏移量的实际滞后时间。该指标对于向PaaS平台提供自动缩放反馈特别有用。

## 39.6死信主题处理

因为您无法预期用户将如何处置死信，所以该框架没有提供任何标准机制来处理它们。如果死信的原因是短暂的，则您可能希望将消息路由回原始主题。但是，如果问题是永久性问题，则可能导致无限循环。本主题中的示例Spring Boot应用程序是如何将这些消息路由回原始主题的示例，但是在尝试了三遍之后，它将它们移动到“ 停车场 ”主题。该应用程序是另一个从死信主题中读取的spring-cloud-stream应用程序。5秒钟未收到任何消息时，它将终止。

这些示例假定原始目的地为`so8400out`，而消费者组为`so8400`。

有两种策略可供考虑：

- 考虑仅在主应用程序未运行时才运行重新路由。否则，瞬态错误的重试会很快用完。
- 或者，使用两阶段方法：使用此应用程序将路由到第三个主题，将另一个应用程序从那里路由回到主主题。

以下代码清单显示了示例应用程序：

**application.properties。** 

```properties
spring.cloud.stream.bindings.input.group=so8400replay
spring.cloud.stream.bindings.input.destination=error.so8400out.so8400

spring.cloud.stream.bindings.output.destination=so8400out
spring.cloud.stream.bindings.output.producer.partitioned=true

spring.cloud.stream.bindings.parkingLot.destination=so8400in.parkingLot
spring.cloud.stream.bindings.parkingLot.producer.partitioned=true

spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest

spring.cloud.stream.kafka.binder.headers=x-retries
```



**应用。** 

```java
@SpringBootApplication
@EnableBinding(TwoOutputProcessor.class)
public class ReRouteDlqKApplication implements CommandLineRunner {

    private static final String X_RETRIES_HEADER = "x-retries";

    public static void main(String[] args) {
        SpringApplication.run(ReRouteDlqKApplication.class, args).close();
    }

    private final AtomicInteger processed = new AtomicInteger();

    @Autowired
    private MessageChannel parkingLot;

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public Message<?> reRoute(Message<?> failed) {
        processed.incrementAndGet();
        Integer retries = failed.getHeaders().get(X_RETRIES_HEADER, Integer.class);
        if (retries == null) {
            System.out.println("First retry for " + failed);
            return MessageBuilder.fromMessage(failed)
                    .setHeader(X_RETRIES_HEADER, new Integer(1))
                    .setHeader(BinderHeaders.PARTITION_OVERRIDE,
                            failed.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID))
                    .build();
        }
        else if (retries.intValue() < 3) {
            System.out.println("Another retry for " + failed);
            return MessageBuilder.fromMessage(failed)
                    .setHeader(X_RETRIES_HEADER, new Integer(retries.intValue() + 1))
                    .setHeader(BinderHeaders.PARTITION_OVERRIDE,
                            failed.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID))
                    .build();
        }
        else {
            System.out.println("Retries exhausted for " + failed);
            parkingLot.send(MessageBuilder.fromMessage(failed)
                    .setHeader(BinderHeaders.PARTITION_OVERRIDE,
                            failed.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID))
                    .build());
        }
        return null;
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            int count = this.processed.get();
            Thread.sleep(5000);
            if (count == this.processed.get()) {
                System.out.println("Idle, terminating");
                return;
            }
        }
    }

    public interface TwoOutputProcessor extends Processor {

        @Output("parkingLot")
        MessageChannel parkingLot();

    }

}
```



## 39.7使用Kafka活页夹进行分区

Apache Kafka本机支持主题分区。

有时，将数据发送到特定的分区是有好处的-例如，当您要严格订购消息处理时（特定客户的所有消息应转到同一分区）。

以下示例显示了如何配置生产方和消费者方：

```java
@SpringBootApplication
@EnableBinding(Source.class)
public class KafkaPartitionProducerApplication {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final String[] data = new String[] {
            "foo1", "bar1", "qux1",
            "foo2", "bar2", "qux2",
            "foo3", "bar3", "qux3",
            "foo4", "bar4", "qux4",
            };

    public static void main(String[] args) {
        new SpringApplicationBuilder(KafkaPartitionProducerApplication.class)
            .web(false)
            .run(args);
    }

    @InboundChannelAdapter(channel = Source.OUTPUT, poller = @Poller(fixedRate = "5000"))
    public Message<?> generate() {
        String value = data[RANDOM.nextInt(data.length)];
        System.out.println("Sending: " + value);
        return MessageBuilder.withPayload(value)
                .setHeader("partitionKey", value)
                .build();
    }

}
```

**application.yml。** 

```properties
spring:
  cloud:
    stream:
      bindings:
        output:
          destination: partitioned.topic
          producer:
            partitioned: true
            partition-key-expression: headers['partitionKey']
            partition-count: 12
```



| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 必须为该主题提供足够的分区，以实现所有消费者组所需的并发性。上面的配置最多支持12个使用者实例（如果实例`concurrency`为2，则为6；如果并发值为3，则为4，依此类推）。通常最好“ 过量供应 ”分区，以允许将来增加使用者或并发使用。 |      |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 前面的配置使用默认分区（`key.hashCode() % partitionCount`）。根据键值，这可能会或可能不会提供适当的平衡算法。您可以使用`partitionSelectorExpression`或`partitionSelectorClass`属性覆盖此默认设置。 |

由于分区是由Kafka本地处理的，因此在用户端不需要特殊配置。Kafka在实例之间分配分区。

以下Spring Boot应用程序侦听Kafka流，并打印（到控制台）每条消息去往的分区ID：

```java
@SpringBootApplication
@EnableBinding(Sink.class)
public class KafkaPartitionConsumerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(KafkaPartitionConsumerApplication.class)
            .web(false)
            .run(args);
    }

    @StreamListener(Sink.INPUT)
    public void listen(@Payload String in, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println(in + " received from partition " + partition);
    }

}
```

**application.yml。** 

```properties
spring:
  cloud:
    stream:
      bindings:
        input:
          destination: partitioned.topic
          group: myGroup
```



您可以根据需要添加实例。Kafka重新平衡分区分配。如果实例计数（或`instance count * concurrency`）超过分区数，则某些使用者处于空闲状态。