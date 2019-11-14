# 31. Configuration Options

## 31.配置选项

Spring Cloud Stream支持常规配置选项以及绑定和活页夹的配置。一些活页夹使附加的绑定属性支持特定于中间件的功能。

可以通过Spring Boot支持的任何机制将配置选项提供给Spring Cloud Stream应用程序。这包括应用程序参数，环境变量以及YAML或.properties文件。

## 31.1绑定服务属性

这些属性通过 `org.springframework.cloud.stream.config.BindingServiceProperties`

- spring.cloud.stream.instanceCount

  应用程序已部署实例的数量。必须在生产者端进行分区设置。如果使用RabbitMQ和Kafka，则必须在用户端设置`autoRebalanceEnabled=false`。默认值：`1`。

- spring.cloud.stream.instanceIndex

  应用程序的实例索引：从`0`到的数字`instanceCount - 1`。用于通过RabbitMQ和Kafka（如果使用）进行分区`autoRebalanceEnabled=false`。在Cloud Foundry中自动设置以匹配应用程序的实例索引。

- spring.cloud.stream.dynamic目的地

  可以动态绑定的目的地列表（例如，在动态路由方案中）。如果设置，则只能绑定列出的目的地。默认值：空（将任何目的地绑定）。

- spring.cloud.stream.defaultBinder

  如果配置了多个联编程序，则使用的默认联编程序。请参见[Classpath上的多个绑定器](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-stream-overview-binders.html#multiple-binders)。默认值：空。

- spring.cloud.stream.overrideCloudConnectors

  仅当`cloud`配置文件处于活动状态并且该应用程序提供了Spring Cloud Connectors 时，此属性才适用。如果属性是`false`（默认值），粘合剂检测合适的绑定的服务（例如，在云铸造开往RabbitMQ的粘合剂的RabbitMQ的服务），并使用它用于创建连接（通常通过弹簧云的连接器）。设置`true`为时，此属性指示绑定程序完全忽略绑定的服务，并依赖Spring Boot属性（例如，依赖于`spring.rabbitmq.*`环境中为RabbitMQ绑定程序提供的属性）。[连接到多个系统时](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-stream-overview-binders.html#multiple-systems)，此属性的典型用法是嵌套在自定义环境[中](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-stream-overview-binders.html#multiple-systems)。默认值：`false`。

- spring.cloud.stream.bindingRetryInterval

  例如，当绑定程序不支持后期绑定并且代理（例如，Apache Kafka）关闭时，重试绑定创建之间的间隔（以秒为单位）。将其设置为零可将此类情况视为致命情况，从而阻止应用程序启动。默认： `30`

## 31.2绑定属性

绑定属性是使用格式提供的`spring.cloud.stream.bindings..=`。的``表示被配置的信道的名称（例如，`output`为一`Source`）。

为了避免重复，Spring Cloud Stream支持以格式设置所有通道的值`spring.cloud.stream.default.=`。

在避免重复使用扩展绑定属性时，应使用-格式`spring.cloud.stream..default..=`。

在下面的内容中，我们将指出省略了`spring.cloud.stream.bindings..`前缀的位置，而只关注属性名称，前提是要在运行时包含前缀。

### 31.2.1通用绑定属性

这些属性通过 `org.springframework.cloud.stream.config.BindingProperties`

以下绑定属性可用于输入和输出绑定，并且必须加上前缀`spring.cloud.stream.bindings..`（例如`spring.cloud.stream.bindings.input.destination=ticktock`）。

可以使用`spring.cloud.stream.default`前缀设置默认值（例如“ spring.cloud.stream.default.contentType = application / json”）。

- 目的地

  绑定的中间件上的通道的目标位置（例如，RabbitMQ交换或Kafka主题）。如果将通道绑定为使用者，则可以将其绑定到多个目标，并且目标名称可以指定为逗号分隔的`String`值。如果未设置，则使用通道名称。此属性的默认值不能被覆盖。

- 组

  渠道的消费群体。仅适用于入站绑定。请参阅[消费者组](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__main_concepts.html#consumer-groups)。默认值：（`null`指示匿名使用者）。

- 内容类型

  频道的内容类型。请参见“ [第32章，*内容类型协商*](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_content-type-management.html) ”。默认值：`application/json`。

- 黏合剂

  此绑定使用的粘合剂。有关详细信息[，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-stream-overview-binders.html#multiple-binders)请参见“ [第30.4节“类路径上的多个绑定器”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-stream-overview-binders.html#multiple-binders) ”。默认值：（`null`使用默认的资料夹（如果存在）。

### 31.2.2消费者财产

这些属性通过 `org.springframework.cloud.stream.binder.ConsumerProperties`

以下绑定属性仅可用于输入绑定，并且必须带有前缀`spring.cloud.stream.bindings..consumer.`（例如`spring.cloud.stream.bindings.input.consumer.concurrency=3`）。

可以使用`spring.cloud.stream.default.consumer`前缀设置默认值（例如`spring.cloud.stream.default.consumer.headerMode=none`）。

- 并发

  入站使用者的并发。默认值：`1`。

- 分区的

  消费者是否从分区生产者那里接收数据。默认值：`false`。

- headerMode

  设置`none`为时，将禁用输入的标头解析。仅对不支持本地消息头并且需要消息头嵌入的消息中间件有效。当不支持本机头时，使用非Spring Cloud Stream应用程序中的数据时，此选项很有用。设置`headers`为时，它将使用中间件的本机头机制。设置`embeddedHeaders`为时，它将标头嵌入消息有效负载中。默认值：取决于活页夹的实现。

- maxAttempts

  如果处理失败，则尝试处理消息的次数（包括第一次）。设置`1`为禁用重试。默认值：`3`。

- backOffInitialInterval

  重试时的退避初始间隔。默认值：`1000`。

- backOffMaxInterval

  最大退避间隔。默认值：`10000`。

- backOffMultiplier

  退避乘数。默认值：`2.0`。

- defaultRetryable

  侦听器抛出的未在中列出的异常是否`retryableExceptions`可以重试。默认值：`true`。

- instanceIndex

  设置为大于零的值时，它允许自定义此使用者的实例索引（如果与不同`spring.cloud.stream.instanceIndex`）。设置为负值时，默认为`spring.cloud.stream.instanceIndex`。有关更多信息[，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__inter_application_communication.html#spring-cloud-stream-overview-instance-index-instance-count)请参见“ [第34.2节“实例索引和实例计数”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__inter_application_communication.html#spring-cloud-stream-overview-instance-index-instance-count) ”。默认值：`-1`。

- instanceCount

  设置为大于零的值时，它允许自定义此使用者的实例计数（如果与不同`spring.cloud.stream.instanceCount`）。设置为负值时，默认为`spring.cloud.stream.instanceCount`。有关更多信息[，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__inter_application_communication.html#spring-cloud-stream-overview-instance-index-instance-count)请参见“ [第34.2节“实例索引和实例计数”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__inter_application_communication.html#spring-cloud-stream-overview-instance-index-instance-count) ”。默认值：`-1`。

- retryableExceptions

  键中Throwable类名称的映射，值中布尔值的映射。指定将要重试的那些异常（和子类）。另请参阅`defaultRetriable`。范例：`spring.cloud.stream.bindings.input.consumer.retryable-exceptions.java.lang.IllegalStateException=false`。默认值：空。

- useNativeDecoding

  设置`true`为时，客户端库将直接对入站消息进行反序列化，必须对其进行相应的配置（例如，设置适当的Kafka生产者值反序列化器）。使用此配置时，入站消息解组不是基于`contentType`绑定的。使用本机解码时，生产者负责使用适当的编码器（例如，Kafka生产者值序列化程序）对出站消息进行序列化。同样，当使用本机编码和解码时，该`headerMode=embeddedHeaders`属性将被忽略并且标头不会嵌入消息中。请参阅生产者属性`useNativeEncoding`。默认值：`false`。

### 31.2.3生产者属性

这些属性通过 `org.springframework.cloud.stream.binder.ProducerProperties`

以下绑定属性仅可用于输出绑定，并且必须带有前缀`spring.cloud.stream.bindings..producer.`（例如`spring.cloud.stream.bindings.input.producer.partitionKeyExpression=payload.id`）。

可以使用前缀设置默认值`spring.cloud.stream.default.producer`（例如`spring.cloud.stream.default.producer.partitionKeyExpression=payload.id`）。

- partitionKeyExpression

  一个SpEL表达式，该表达式确定如何对出站数据进行分区。如果已设置，则`partitionKeyExtractorClass`对该通道上的出站数据进行分区。`partitionCount`必须将其设置为大于1的值才能生效。与互斥`partitionKeyExtractorClass`。请参见“ [第28.6节“分区支持”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__main_concepts.html#partitioning) ”。默认值：null。

- partitionKeyExtractorClass

  一个`PartitionKeyExtractorStrategy`实现。如果已设置，则`partitionKeyExpression`对该通道上的出站数据进行分区。`partitionCount`必须将其设置为大于1的值才能生效。与互斥`partitionKeyExpression`。请参见“ [第28.6节“分区支持”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__main_concepts.html#partitioning) ”。默认值：`null`。

- partitionSelectorClass

  一个`PartitionSelectorStrategy`实现。与互斥`partitionSelectorExpression`。如果两者均未设置，则将该分区选择为`hashCode(key) % partitionCount`，其中`key`通过`partitionKeyExpression`或计算`partitionKeyExtractorClass`。默认值：`null`。

- partitionSelectorExpression

  用于自定义分区选择的SpEL表达式。与互斥`partitionSelectorClass`。如果两者均未设置，则将该分区选择为`hashCode(key) % partitionCount`，其中`key`通过`partitionKeyExpression`或计算`partitionKeyExtractorClass`。默认值：`null`。

- partitionCount

  目标分区的数据的数量，如果分区已启用。如果生产者已分区，则必须将其设置为大于1的值。在卡夫卡，它被解释为一个提示。取其较大者，并使用目标主题的分区数。默认值：`1`。

- requiredGroups

  生产者必须确保将消息传递到的组的逗号分隔列表，即使它们是在创建消息之后开始的（例如，通过在RabbitMQ中预先创建持久队列）。

- headerMode

  设置`none`为时，它将禁用在输出中嵌入标头。它仅对本身不支持消息头并且需要消息头嵌入的消息中间件有效。当不支持本机头时，在为非Spring Cloud Stream应用程序生成数据时，此选项很有用。设置`headers`为时，它将使用中间件的本机头机制。设置`embeddedHeaders`为时，它将标头嵌入消息有效负载中。默认值：取决于活页夹的实现。

- useNativeEncoding

  设置`true`为时，出站消息由客户端库直接序列化，必须相应配置（例如，设置适当的Kafka生产者值序列化程序）。使用此配置时，出站消息编组不是基于`contentType`绑定的。使用本机编码时，消费者有责任使用适当的解码器（例如，Kafka消费者值反序列化器）对入站消息进行反序列化。同样，当使用本机编码和解码时，该`headerMode=embeddedHeaders`属性将被忽略并且标头不会嵌入消息中。看到消费者的财产`useNativeDecoding`。默认值：`false`。

- errorChannelEnabled

  设置为时`true`，如果活页夹支持异步发送结果，则发送失败将发送到目标的错误通道。参见“ [??? ](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__configuration_options.html)”以获取更多信息。默认值：`false`。

## 31.3使用动态绑定的目的地

除了使用定义的渠道外`@EnableBinding`，Spring Cloud Stream还允许应用程序将消息发送到动态绑定的目的地。例如，当需要在运行时确定目标目的地时，这很有用。应用程序可以通过使用`BinderAwareChannelResolver`由`@EnableBinding`注解自动注册的bean来实现。

“ spring.cloud.stream.dynamicDestinations”属性可用于将动态目标名称限制为已知集合（白名单）。如果未设置此属性，则可以动态绑定任何目标。

的`BinderAwareChannelResolver`，可直接使用，如图使用路径变量来决定所述目标信道一个REST控制器的下面的例子：

```java
@EnableBinding
@Controller
public class SourceWithDynamicDestination {

    @Autowired
    private BinderAwareChannelResolver resolver;

    @RequestMapping(path = "/{target}", method = POST, consumes = "*/*")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleRequest(@RequestBody String body, @PathVariable("target") target,
           @RequestHeader(HttpHeaders.CONTENT_TYPE) Object contentType) {
        sendMessage(body, target, contentType);
    }

    private void sendMessage(String body, String target, Object contentType) {
        resolver.resolveDestination(target).send(MessageBuilder.createMessage(body,
                new MessageHeaders(Collections.singletonMap(MessageHeaders.CONTENT_TYPE, contentType))));
    }
}
```

现在考虑当我们在默认端口（8080）上启动应用程序并使用CURL发出以下请求时会发生什么：

```bash
curl -H "Content-Type: application/json" -X POST -d "customer-1" http://localhost:8080/customers

curl -H "Content-Type: application/json" -X POST -d "order-1" http://localhost:8080/orders
```

在代理中创建目的地“客户”和“订单”（在Rabbit的交换中或在Kafka的主题中），名称为“客户”和“订单”，并将数据发布到适当的目的地。

它`BinderAwareChannelResolver`是通用的Spring Integration `DestinationResolver`，可以注入到其他组件中，例如，在基于`target`传入JSON消息字段使用SpEL表达式的路由器中。以下示例包含一个读取SpEL表达式的路由器：

```java
@EnableBinding
@Controller
public class SourceWithDynamicDestination {

    @Autowired
    private BinderAwareChannelResolver resolver;


    @RequestMapping(path = "/", method = POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleRequest(@RequestBody String body, @RequestHeader(HttpHeaders.CONTENT_TYPE) Object contentType) {
        sendMessage(body, contentType);
    }

    private void sendMessage(Object body, Object contentType) {
        routerChannel().send(MessageBuilder.createMessage(body,
                new MessageHeaders(Collections.singletonMap(MessageHeaders.CONTENT_TYPE, contentType))));
    }

    @Bean(name = "routerChannel")
    public MessageChannel routerChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "routerChannel")
    public ExpressionEvaluatingRouter router() {
        ExpressionEvaluatingRouter router =
            new ExpressionEvaluatingRouter(new SpelExpressionParser().parseExpression("payload.target"));
        router.setDefaultOutputChannelName("default-output");
        router.setChannelResolver(resolver);
        return router;
    }
}
```

该[路由器接收器应用程序](https://github.com/spring-cloud-stream-app-starters/router)使用此技术的按需创建的目的地。

如果预先知道通道名称，则可以像其他任何目的地一样配置生产者属性。另外，如果您注册一个`NewBindingCallback<>`bean，则在创建绑定之前调用它。回调采用绑定程序使用的扩展生产者属性的通用类型。它有一种方法：

```java
void configure(String channelName, MessageChannel channel, ProducerProperties producerProperties,
        T extendedProducerProperties);
```

以下示例显示了如何使用RabbitMQ绑定器：

```java
@Bean
public NewBindingCallback<RabbitProducerProperties> dynamicConfigurer() {
    return (name, channel, props, extended) -> {
        props.setRequiredGroups("bindThisQueue");
        extended.setQueueNameGroupOnly(true);
        extended.setAutoBindDlq(true);
        extended.setDeadLetterQueueName("myDLQ");
    };
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果您需要支持具有多种活页夹类型的动态目标，请使用`Object`泛型类型并`extended`根据需要强制转换参数。 |