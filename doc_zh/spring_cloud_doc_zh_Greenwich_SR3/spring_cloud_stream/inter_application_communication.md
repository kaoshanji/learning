# 34. Inter-Application Communication

## 34.应用程序间通信

Spring Cloud Stream支持应用程序之间的通信。应用程序间通信是一个涉及多个问题的复杂问题，如以下主题所述：

- “ [第34.1节“连接多个应用程序实例”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__inter_application_communication.html#spring-cloud-stream-overview-connecting-multiple-application-instances) ”
- “ [第34.2节“实例索引和实例计数”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__inter_application_communication.html#spring-cloud-stream-overview-instance-index-instance-count) ”
- “ [第34.3节“分区”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__inter_application_communication.html#spring-cloud-stream-overview-partitioning) ”

## 34.1连接多个应用程序实例

虽然Spring Cloud Stream使单个Spring Boot应用程序易于连接到消息传递系统，但是Spring Cloud Stream的典型方案是创建多应用程序管道，在该管道中，微服务应用程序会相互发送数据。您可以通过关联“ 相邻 ”应用程序的输入和输出目标来实现此方案。

假设设计要求Time Source应用程序将数据发送到Log Sink应用程序。您可以`ticktock`在两个应用程序中使用为绑定命名的公共目标。

时间来源（具有渠道名称`output`）将设置以下属性：

```properties
spring.cloud.stream.bindings.output.destination=ticktock
```

Log Sink（具有通道名称`input`）将设置以下属性：

```properties
spring.cloud.stream.bindings.input.destination=ticktock
```

## 34.2实例索引和实例计数

在扩展Spring Cloud Stream应用程序时，每个实例都可以接收有关同一应用程序还存在多少其他实例以及它自己的实例索引是什么的信息。Spring Cloud Stream通过`spring.cloud.stream.instanceCount`和`spring.cloud.stream.instanceIndex`属性执行此操作。例如，如果有一个HDFS的三个实例宿应用，所有这三个实例都`spring.cloud.stream.instanceCount`设置为`3`，与单独的应用已经`spring.cloud.stream.instanceIndex`设置为`0`，`1`，和`2`分别。

当通过Spring Cloud Data Flow部署Spring Cloud Stream应用程序时，这些属性会自动配置。当独立启动Spring Cloud Stream应用程序时，必须正确设置这些属性。默认情况下，`spring.cloud.stream.instanceCount`是`1`和`spring.cloud.stream.instanceIndex`是`0`。

在按比例放大的方案中，正确地配置这两个属性通常对于解决分区行为很重要（请参见下文），并且某些绑定程序（例如，Kafka绑定程序）始终需要这两个属性，以确保数据在多个使用者实例之间正确划分。

## 34.3分区

在Spring Cloud Stream中进行分区包括两个任务：

- “ [第34.3.1节“配置用于分区的输出绑定”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__inter_application_communication.html#spring-cloud-stream-overview-configuring-output-bindings-partitioning) ”
- “ [第34.3.2节“配置用于分区的输入绑定”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__inter_application_communication.html#spring-cloud-stream-overview-configuring-input-bindings-partitioning) ”

### 34.3.1配置用于分区的输出绑定

您可以配置输出结合通过设置一个且只有它的人送分区数据`partitionKeyExpression`或`partitionKeyExtractorName`性质，以及它的`partitionCount`属性。

例如，以下是有效的典型配置：

```properties
spring.cloud.stream.bindings.output.producer.partitionKeyExpression=payload.id
spring.cloud.stream.bindings.output.producer.partitionCount=5
```

基于该示例配置，通过使用以下逻辑将数据发送到目标分区。

基于，为发送到分区输出通道的每个消息计算分区键的值`partitionKeyExpression`。的`partitionKeyExpression`是针对出站消息进行评估，用于提取分区键SpeI位表达。

如果SpEL表达式不足以满足您的需要，则可以通过提供的实现`org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy`并将其配置为Bean（通过使用`@Bean`注释）来计算分区键值。如果`org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy`在Application Context中有一个以上可用的类型的bean，则可以通过使用`partitionKeyExtractorName`属性指定其名称来进一步过滤它，如以下示例所示：

```java
--spring.cloud.stream.bindings.output.producer.partitionKeyExtractorName=customPartitionKeyExtractor
--spring.cloud.stream.bindings.output.producer.partitionCount=5
. . .
@Bean
public CustomPartitionKeyExtractorClass customPartitionKeyExtractor() {
    return new CustomPartitionKeyExtractorClass();
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 在早期版本的Spring Cloud Stream中，您可以`org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy`通过设置`spring.cloud.stream.bindings.output.producer.partitionKeyExtractorClass`属性来指定的实现。从2.0版开始，不推荐使用此属性，并且在将来的版本中将删除对此属性的支持。 |

一旦计算出消息密钥，分区选择过程就会将目标分区确定为`0`和之间的值`partitionCount - 1`。适用于大多数情况的默认计算基于以下公式：`key.hashCode() % partitionCount`。可以在绑定上进行自定义，方法是设置SpEL表达式以针对“键”（通过`partitionSelectorExpression`属性）进行评估，或者通过将as 的实现配置`org.springframework.cloud.stream.binder.PartitionSelectorStrategy`为bean（通过使用@Bean批注）。与相似，如以下示例所示，当应用程序上下文中有多个这种类型的bean可用时`PartitionKeyExtractorStrategy`，您可以使用`spring.cloud.stream.bindings.output.producer.partitionSelectorName`属性进一步过滤它：

```java
--spring.cloud.stream.bindings.output.producer.partitionSelectorName=customPartitionSelector
. . .
@Bean
public CustomPartitionSelectorClass customPartitionSelector() {
    return new CustomPartitionSelectorClass();
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 在早期版本的Spring Cloud Stream中，您可以`org.springframework.cloud.stream.binder.PartitionSelectorStrategy`通过设置`spring.cloud.stream.bindings.output.producer.partitionSelectorClass`属性来指定的实现。从2.0版开始，不推荐使用此属性，并且在将来的版本中将不再支持该属性。 |

### 34.3.2配置用于分区的输入绑定

输入绑定（具有通道名称`input`）配置为通过设置分区`partitioned`属性以及应用程序本身的`instanceIndex`和`instanceCount`属性来接收分区数据，如以下示例所示：

```properties
spring.cloud.stream.bindings.input.consumer.partitioned=true
spring.cloud.stream.instanceIndex=3
spring.cloud.stream.instanceCount=5
```

该`instanceCount`值表示应在其之间分区数据的应用程序实例的总数。的`instanceIndex`必须是跨多个实例的唯一值，值介`0`和`instanceCount - 1`。实例索引可帮助每个应用程序实例识别从中接收数据的唯一分区。活页夹要求使用不支持本地分区的技术。例如，对于RabbitMQ，每个分区都有一个队列，该队列名称包含实例索引。对于Kafka，如果`autoRebalanceEnabled`为`true`（默认值），则Kafka负责在实例之间分配分区，并且这些属性不是必需的。如果`autoRebalanceEnabled`设置为false，则`instanceCount`and`instanceIndex`绑定程序使用来确定实例所预订的分区（您必须拥有至少与实例一样多的分区）。活页夹分配分区而不是Kafka。如果您希望特定分区的消息始终发送到同一实例，这可能会很有用。当活页夹配置需要它们时，重要的是正确设置两个值，以确保使用所有数据并且应用程序实例接收互斥的数据集。

尽管在单独情况下使用多个实例进行分区数据处理可能会很复杂，但Spring Cloud Dataflow可以通过正确填充输入和输出值以及让您依赖运行时基础架构来显着简化流程。提供有关实例索引和实例计数的信息。