# 40. Apache Kafka Streams Binder

## 40. Apache Kafka Streams活页夹

## 40.1使用

要使用Kafka Streams绑定程序，只需使用以下Maven坐标将其添加到Spring Cloud Stream应用程序中：

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-stream-binder-kafka-streams</artifactId>
</dependency>
```

## 40.2 Kafka Streams Binder概述

Spring Cloud Stream的Apache Kafka支持还包括明确为Apache Kafka Streams绑定设计的绑定器实现。通过这种本机集成，Spring Cloud Stream“处理器”应用程序可以在核心业务逻辑中直接使用 [Apache Kafka Streams](https://kafka.apache.org/documentation/streams/developer-guide) API。

Kafka Streams活页夹实现基于[Spring Kafka](https://docs.spring.io/spring-kafka/reference/html/_reference.html#kafka-streams) 项目中[Kafka Streams](https://docs.spring.io/spring-kafka/reference/html/_reference.html#kafka-streams)提供的基础。

Kafka Streams活页夹为Kafka Streams中的三种主要类型（KStream，KTable和GlobalKTable）提供了绑定功能。

作为本机集成的一部分， Kafka Streams API提供的高级[Streams DSL](https://docs.confluent.io/current/streams/developer-guide/dsl-api.html)可用于业务逻辑。

还提供了[处理器API](https://docs.confluent.io/current/streams/developer-guide/processor-api.html) 支持的早期版本。

如前所述，Spring Cloud Stream中的Kafka Streams支持仅在处理器模型中严格可用。可以应用一种模型，在该模型中，可以从入站主题读取消息，进行业务处理，然后可以将转换后的消息写入出站主题。它也可以用于无出站目的地的处理器应用程序中。

### 40.2.1流DSL

此应用程序使用来自Kafka主题的数据（例如`words`），在5秒的时间窗口内为每个唯一单词计算单词计数，并将计算出的结果发送到下游主题（例如`counts`）进行进一步处理。

```java
@SpringBootApplication
@EnableBinding(KStreamProcessor.class)
public class WordCountProcessorApplication {

	@StreamListener("input")
	@SendTo("output")
	public KStream<?, WordCount> process(KStream<?, String> input) {
		return input
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, value) -> value)
                .windowedBy(TimeWindows.of(5000))
                .count(Materialized.as("WordCounts-multi"))
                .toStream()
                .map((key, value) -> new KeyValue<>(null, new WordCount(key.key(), value, new Date(key.window().start()), new Date(key.window().end()))));
    }

	public static void main(String[] args) {
		SpringApplication.run(WordCountProcessorApplication.class, args);
	}
```

一旦构建为uber-jar（例如`wordcount-processor.jar`），您就可以像下面一样运行上面的示例。

```bash
java -jar wordcount-processor.jar  --spring.cloud.stream.bindings.input.destination=words --spring.cloud.stream.bindings.output.destination=counts
```

该应用程序将使用来自Kafka主题的消息`words`，并将计算的结果发布到输出主题`counts`。

Spring Cloud Stream将确保来自传入和传出主题的消息都自动绑定为KStream对象。作为开发人员，您可以专注于代码的业务方面，即编写处理器中所需的逻辑。框架自动处理设置Kafka Streams基础结构所需的Streams DSL特定配置。

## 40.3配置选项

本节包含Kafka Streams绑定程序使用的配置选项。

有关与活页夹有关的常见配置选项和属性，请参阅[核心文档](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__configuration_options.html#binding-properties)。

### 40.3.1 Kafka Streams属性

以下属性在活页夹级别可用，并且必须以`spring.cloud.stream.kafka.streams.binder.` 文字作为前缀。

- 组态

  使用包含与Apache Kafka Streams API有关的属性的键/值对进行映射。此属性必须以开头`spring.cloud.stream.kafka.streams.binder.`。以下是使用此属性的一些示例。

```properties
spring.cloud.stream.kafka.streams.binder.configuration.default.key.serde=org.apache.kafka.common.serialization.Serdes$StringSerde
spring.cloud.stream.kafka.streams.binder.configuration.default.value.serde=org.apache.kafka.common.serialization.Serdes$StringSerde
spring.cloud.stream.kafka.streams.binder.configuration.commit.interval.ms=1000
```

有关可能用于流配置的所有属性的更多信息，请参阅Apache Kafka Streams文档中的StreamsConfig JavaDocs。

- 经纪人

  经纪人网址默认： `localhost`

- zkNodes

  Zookeeper URL默认： `localhost`

- serdeError

  反序列化错误处理程序类型。可能的值是- `logAndContinue`，`logAndFail`或`sendToDlq`默认： `logAndFail`

- applicationId

  在绑定程序级别全局设置Kafka Streams应用程序的application.id的简便方法。如果应用程序包含多个`StreamListener`方法，则应在每个输入绑定的绑定级别上设置application.id。默认： `none`

以下属性*仅*可用于Kafka Streams生产者，并且必须以`spring.cloud.stream.kafka.streams.bindings..producer.`文字作为前缀。为了方便起见，如果存在多个输出绑定，并且它们都需要一个公共值，则可以使用prefix进行配置`spring.cloud.stream.kafka.streams.default.producer.`。

- 钥匙串

  要使用的密钥序列默认值：`none`。

- valueSerde

  使用价值服务默认值：`none`。

- useNativeEncoding

  标志以启用本机编码默认值：`false`。

以下属性*仅*适用于Kafka Streams使用者，并且必须以开头`spring.cloud.stream.kafka.streams.bindings..consumer.`literal. For convenience, if there multiple input bindings and they all require a common value, that can be configured by using the prefix `spring.cloud.stream.kafka.streams.default.consumer.`。

- applicationId

  设置每个输入绑定的application.id。默认： `none`

- 钥匙串

  要使用的密钥序列默认值：`none`。

- valueSerde

  使用价值服务默认值：`none`。

- 物化

  状态存储在使用传入的KTable类型时实现默认值：`none`。

- useNativeDecoding

  标志以启用本机解码默认值：`false`。

- dlqName

  DLQ主题名称。默认值：`none`。

### 40.3.2 TimeWindow属性：

窗口化是流处理应用程序中的重要概念。以下属性可用于配置时间窗口计算。

- spring.cloud.stream.kafka.streams.timeWindow.length

  赋予此属性后，您可以将`TimeWindows`bean自动连接到应用程序中。该值以毫秒为单位。默认值：`none`。

- spring.cloud.stream.kafka.streams.timeWindow.advanceBy

  值以毫秒为单位。默认值：`none`。

## 40.4多个输入绑定

对于需要多个传入KStream对象或KStream与KTable对象的组合的用例，Kafka Streams绑定程序提供了多个绑定支持。

让我们来看看它的作用。

### 40.4.1多个输入绑定作为接收器

```java
@EnableBinding(KStreamKTableBinding.class)
.....
.....
@StreamListener
public void process(@Input("inputStream") KStream<String, PlayEvent> playEvents,
                    @Input("inputTable") KTable<Long, Song> songTable) {
                    ....
                    ....
}

interface KStreamKTableBinding {

    @Input("inputStream")
    KStream<?, ?> inputStream();

    @Input("inputTable")
    KTable<?, ?> inputTable();
}
```

在上面的示例中，应用程序被编写为接收器，即没有输出绑定，并且应用程序必须决定有关下游处理的内容。当您以这种方式编写应用程序时，您可能希望向下游发送信息或将它们存储在状态存储中（有关可查询状态存储，请参见下文）。

对于传入的KTable，如果要将计算具体化为状态存储，则必须通过以下属性将其表示。

```properties
spring.cloud.stream.kafka.streams.bindings.inputTable.consumer.materializedAs: all-songs
```

上面的示例显示了使用KTable作为输入绑定。绑定器还支持GlobalKTable的输入绑定。当您必须确保应用程序的所有实例都可以访问主题中的数据更新时，GlobalKTable绑定非常有用。KTable和GlobalKTable绑定仅在输入上可用。活页夹支持KStream的输入和输出绑定。

### 40.4.2作为处理器的多个输入绑定

```java
@EnableBinding(KStreamKTableBinding.class)
....
....

@StreamListener
@SendTo("output")
public KStream<String, Long> process(@Input("input") KStream<String, Long> userClicksStream,
                                     @Input("inputTable") KTable<String, String> userRegionsTable) {
....
....
}

interface KStreamKTableBinding extends KafkaStreamsProcessor {

    @Input("inputX")
    KTable<?, ?> inputTable();
}
```

## 40.5多个输出绑定（又名分支）

Kafka Streams允许根据某些谓词将出站数据分为多个主题。Kafka Streams绑定程序提供了对此功能的支持，而不会损害`StreamListener`最终用户应用程序中公开的编程模型。

您可以按照上面在字数示例中展示的常用方法编写应用程序。但是，使用分支功能时，您需要做一些事情。首先，您需要确保您的返回类型是`KStream[]` 常规类型`KStream`。其次，您需要`SendTo`按顺序使用包含输出绑定的注释（请参见下面的示例）。对于这些输出绑定中的每一个，您都需要配置目标，内容类型等，并符合标准Spring Cloud Stream期望。

这是一个例子：

```java
@EnableBinding(KStreamProcessorWithBranches.class)
@EnableAutoConfiguration
public static class WordCountProcessorApplication {

    @Autowired
    private TimeWindows timeWindows;

    @StreamListener("input")
    @SendTo({"output1","output2","output3})
    public KStream<?, WordCount>[] process(KStream<Object, String> input) {

			Predicate<Object, WordCount> isEnglish = (k, v) -> v.word.equals("english");
			Predicate<Object, WordCount> isFrench =  (k, v) -> v.word.equals("french");
			Predicate<Object, WordCount> isSpanish = (k, v) -> v.word.equals("spanish");

			return input
					.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
					.groupBy((key, value) -> value)
					.windowedBy(timeWindows)
					.count(Materialized.as("WordCounts-1"))
					.toStream()
					.map((key, value) -> new KeyValue<>(null, new WordCount(key.key(), value, new Date(key.window().start()), new Date(key.window().end()))))
					.branch(isEnglish, isFrench, isSpanish);
    }

    interface KStreamProcessorWithBranches {

    		@Input("input")
    		KStream<?, ?> input();

    		@Output("output1")
    		KStream<?, ?> output1();

    		@Output("output2")
    		KStream<?, ?> output2();

    		@Output("output3")
    		KStream<?, ?> output3();
    	}
}
```

特性：

```properties
spring.cloud.stream.bindings.output1.contentType: application/json
spring.cloud.stream.bindings.output2.contentType: application/json
spring.cloud.stream.bindings.output3.contentType: application/json
spring.cloud.stream.kafka.streams.binder.configuration.commit.interval.ms: 1000
spring.cloud.stream.kafka.streams.binder.configuration:
  default.key.serde: org.apache.kafka.common.serialization.Serdes$StringSerde
  default.value.serde: org.apache.kafka.common.serialization.Serdes$StringSerde
spring.cloud.stream.bindings.output1:
  destination: foo
  producer:
    headerMode: raw
spring.cloud.stream.bindings.output2:
  destination: bar
  producer:
    headerMode: raw
spring.cloud.stream.bindings.output3:
  destination: fox
  producer:
    headerMode: raw
spring.cloud.stream.bindings.input:
  destination: words
  consumer:
    headerMode: raw
```

## 40.6消息转换

与基于消息通道的活页夹应用程序类似，Kafka Streams活页夹可适应现成的内容类型转换，而不会做出任何妥协。

对于Kafka Streams操作而言，通常要知道用于正确转换键和值的SerDe类型。因此，在入站和出站转换时依靠Apache Kafka Streams库本身提供的SerDe功能可能比使用框架提供的内容类型转换更为自然。另一方面，您可能已经熟悉框架提供的内容类型转换模式，并且您希望继续用于入站和出站转换。

Kafka Streams联编程序实现支持这两个选项。

### 40.6.1出站序列化

如果禁用本机编码（这是默认设置），则框架将使用用户设置的contentType转换消息（否则，`application/json`将应用默认设置）。在这种情况下，它将忽略出站上设置的任何SerDe，以进行出站序列化。

这是在出站上设置contentType的属性。

```properties
spring.cloud.stream.bindings.output.contentType: application/json
```

这是启用本地编码的属性。

```properties
spring.cloud.stream.bindings.output.nativeEncoding: true
```

如果在输出绑定上启用了本机编码（用户必须如上所述明确启用它），则框架将在出站上跳过任何形式的自动消息转换。在这种情况下，它将切换到用户设置的Serde。`valueSerde`将使用在实际输出绑定上设置的属性。这是一个例子。

```properties
spring.cloud.stream.kafka.streams.bindings.output.producer.valueSerde: org.apache.kafka.common.serialization.Serdes$StringSerde
```

如果没有设置这个属性，那么它会使用“默认” SERDE： `spring.cloud.stream.kafka.streams.binder.configuration.default.value.serde`。

值得一提的是，Kafka Streams绑定程序不会在出站上序列化密钥-它仅依赖于Kafka本身。因此，您必须`keySerde`在绑定上指定属性，否则它将默认为应用程序范围的common `keySerde`。

绑定级别键序列号：

```properties
spring.cloud.stream.kafka.streams.bindings.output.producer.keySerde
```

公用密钥序列：

```properties
spring.cloud.stream.kafka.streams.binder.configuration.default.key.serde
```

如果使用分支，则需要使用多个输出绑定。例如，

```java
interface KStreamProcessorWithBranches {

    		@Input("input")
    		KStream<?, ?> input();

    		@Output("output1")
    		KStream<?, ?> output1();

    		@Output("output2")
    		KStream<?, ?> output2();

    		@Output("output3")
    		KStream<?, ?> output3();
    	}
```

如果`nativeEncoding`设置为，则可以如下对各个输出绑定设置不同的SerDe。

```properties
spring.cloud.stream.kafka.streams.bindings.output1.producer.valueSerde=IntegerSerde
spring.cloud.stream.kafka.streams.bindings.output2.producer.valueSerde=StringSerde
spring.cloud.stream.kafka.streams.bindings.output3.producer.valueSerde=JsonSerde
```

然后，如果您`SendTo`这样@SendTo（{“ output1”，“ output2”，“ output3”}），`KStream[]`则将分支中的from与上面定义的适当的SerDe对象一起应用。如果未启用`nativeEncoding`，则可以如下在输出绑定上设置不同的contentType值。在这种情况下，框架将使用适当的消息转换器来转换消息，然后再发送给Kafka。

```properties
spring.cloud.stream.bindings.output1.contentType: application/json
spring.cloud.stream.bindings.output2.contentType: application/java-serialzied-object
spring.cloud.stream.bindings.output3.contentType: application/octet-stream
```

### 40.6.2入站反序列化

类似的规则适用于入站数据反序列化。

如果禁用本机解码（这是默认设置），则框架将使用用户设置的contentType转换消息（否则，`application/json`将应用默认设置）。在这种情况下，它将针对入站反序列化而忽略入站上设置的任何SerDe。

这是在入站上设置contentType的属性。

```properties
spring.cloud.stream.bindings.input.contentType: application/json
```

这是启用本机解码的属性。

```properties
spring.cloud.stream.bindings.input.nativeDecoding: true
```

如果在输入绑定上启用了本机解码（用户必须如上所述明确启用它），则框架将跳过对入站进行的任何消息转换。在这种情况下，它将切换到用户设置的SerDe。`valueSerde` 将使用在实际输出绑定上设置的属性。这是一个例子。

```properties
spring.cloud.stream.kafka.streams.bindings.input.consumer.valueSerde: org.apache.kafka.common.serialization.Serdes$StringSerde
```

如果没有设置该属性，则使用默认的SERDE： `spring.cloud.stream.kafka.streams.binder.configuration.default.value.serde`。

值得一提的是，Kafka Streams绑定程序不会反序列化入站密钥-它仅依赖于Kafka本身。因此，您必须`keySerde`在绑定上指定属性，否则它将默认为应用程序范围的common `keySerde`。

绑定级别键序列号：

```properties
spring.cloud.stream.kafka.streams.bindings.input.consumer.keySerde
```

公用密钥序列：

```properties
spring.cloud.stream.kafka.streams.binder.configuration.default.key.serde
```

与在出站上进行KStream分支的情况一样，为每个绑定设置值SerDe的好处是，如果您有多个输入绑定（多个KStreams对象），并且它们都需要单独的值SerDe，则可以分别配置它们。如果使用通用配置方法，则此功能将不适用。

## 40.7错误处理

Apache Kafka Streams提供了本机处理反序列化错误引起的异常的功能。有关该支持的详细信息，请参阅[本](https://cwiki.apache.org/confluence/display/KAFKA/KIP-161%3A+streams+deserialization+exception+handlers) 开箱，Apache的卡夫卡流提供2种反序列化异常处理的- `logAndContinue`和`logAndFail`。顾名思义，前者将记录错误并继续处理下一条记录，而后者将记录错误并失败。`LogAndFail`是默认的反序列化异常处理程序。

### 40.7.1处理反序列化异常

Kafka Streams活页夹通过以下属性支持选择异常处理程序。

```properties
spring.cloud.stream.kafka.streams.binder.serdeError: logAndContinue
```

除了上述两个反序列化异常处理程序外，绑定程序还提供了第三个用于将错误记录（毒丸）发送到DLQ主题的代理。这是启用此DLQ异常处理程序的方法。

```properties
spring.cloud.stream.kafka.streams.binder.serdeError: sendToDlq
```

设置上述属性后，所有反序列化错误记录都会自动发送到DLQ主题。

```properties
spring.cloud.stream.kafka.streams.bindings.input.consumer.dlqName: foo-dlq
```

如果已设置，则错误记录将发送到topic `foo-dlq`。如果未设置，它将创建一个名为的DLQ主题`error..`。

在Kafka Streams活页夹中使用异常处理功能时，需要记住两件事。

- 该属性`spring.cloud.stream.kafka.streams.binder.serdeError`适用于整个应用程序。这意味着如果`StreamListener`同一应用程序中有多个方法，则此属性将应用于所有这些方法。
- 反序列化的异常处理与本机反序列化和框架提供的消息转换一致。

### 40.7.2处理非反序列化异常

对于Kafka Streams联编程序中的常规错误处理，最终用户应用程序可以处理应用程序级错误。作为为反序列化异常处理程序提供DLQ的副作用，Kafka Streams绑定程序提供了一种直接从您的应用程序访问DLQ发送bean的方法。一旦访问了该bean，就可以以编程方式将所有异常记录从应用程序发送到DLQ。

使用高级DSL仍然难以进行强大的错误处理。Kafka Streams本身还不支持错误处理。

但是，当您在应用程序中使用低级处理器API时，有一些选项可以控制此行为。见下文。

```java
@Autowired
private SendToDlqAndContinue dlqHandler;

@StreamListener("input")
@SendTo("output")
public KStream<?, WordCount> process(KStream<Object, String> input) {

    input.process(() -> new Processor() {
    			ProcessorContext context;

    			@Override
    			public void init(ProcessorContext context) {
    				this.context = context;
    			}

    			@Override
    			public void process(Object o, Object o2) {

    			    try {
    			        .....
    			        .....
    			    }
    			    catch(Exception e) {
    			        //explicitly provide the kafka topic corresponding to the input binding as the first argument.
                        //DLQ handler will correctly map to the dlq topic from the actual incoming destination.
                        dlqHandler.sendToDlq("topic-name", (byte[]) o1, (byte[]) o2, context.partition());
    			    }
    			}

    			.....
    			.....
    });
}
```

## 40.8国立商店

使用DSL时，Kafka Streams会自动创建状态存储。使用处理器API时，您需要手动注册状态存储。为此，您可以使用`KafkaStreamsStateStore`注释。您可以指定存储的名称和类型，控制日志的标志以及禁用缓存等。一旦在引导阶段由绑定程序创建了存储，就可以通过处理器API访问此状态存储。以下是一些执行此操作的原语。

创建状态存储：

```java
@KafkaStreamsStateStore(name="mystate", type= KafkaStreamsStateStoreProperties.StoreType.WINDOW, lengthMs=300000)
public void process(KStream<Object, Product> input) {
    ...
}
```

访问状态存储：

```java
Processor<Object, Product>() {

    WindowStore<Object, String> state;

    @Override
    public void init(ProcessorContext processorContext) {
        state = (WindowStore)processorContext.getStateStore("mystate");
    }
    ...
}
```

## 40.9交互式查询

作为公开的Kafka Streams绑定程序API的一部分，我们公开了一个名为的类`InteractiveQueryService`。您可以在应用程序中将其作为Spring bean访问。从您的应用程序访问该bean的一种简单方法是“自动装配”该bean。

```java
@Autowired
private InteractiveQueryService interactiveQueryService;
```

一旦获得对该bean的访问权限，就可以查询您感兴趣的特定状态存储。见下文。

```java
ReadOnlyKeyValueStore<Object, Object> keyValueStore =
						interactiveQueryService.getQueryableStoreType("my-store", QueryableStoreTypes.keyValueStore());
```

如果有多个Kafka Streams应用程序实例正在运行，则在以交互方式查询它们之前，您需要确定哪个应用程序实例承载密钥。 `InteractiveQueryService`API提供了识别主机信息的方法。

为了使它起作用，必须`application.server`按如下所示配置属性：

```properties
spring.cloud.stream.kafka.streams.binder.configuration.application.server: <server>:<port>
```

以下是一些代码段：

```java
org.apache.kafka.streams.state.HostInfo hostInfo = interactiveQueryService.getHostInfo("store-name",
						key, keySerializer);

if (interactiveQueryService.getCurrentHostInfo().equals(hostInfo)) {

    //query from the store that is locally available
}
else {
    //query from the remote host
}
```

## 40.10访问基础的KafkaStreams对象

StreamBuilderFactoryBean`从spring-kafka中负责构造`KafkaStreams`对象的对象可以通过编程方式进行访问。每个`StreamBuilderFactoryBean`都注册为方法名称，`stream-builder`并附加`StreamListener`方法名称。例如，如果您的`StreamListener`方法命名为`process`，则流构建器bean的命名为`stream-builder-process`。由于这是工厂bean，因此在以`&`编程方式访问它时，应在前面加上一个＆符号来对其进行访问。以下是一个示例，并假定该`StreamListener`方法名为`process

```java
StreamsBuilderFactoryBean streamsBuilderFactoryBean = context.getBean("&stream-builder-process", StreamsBuilderFactoryBean.class);
			KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
```

## 40.11状态清理

默认情况下，`Kafkastreams.cleanup()`绑定停止时将调用该方法。请参阅[Spring Kafka文档](https://docs.spring.io/spring-kafka/reference/html/_reference.html#_configuration)。要修改此行为，只需`CleanupConfig` `@Bean`向应用程序上下文中添加一个（配置为在启动，停止或都不清除时清除）；该bean将被检测到并连接到工厂bean中。