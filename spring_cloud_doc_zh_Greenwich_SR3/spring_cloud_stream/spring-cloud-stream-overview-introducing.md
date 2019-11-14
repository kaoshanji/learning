# 27. Introducing Spring Cloud Stream

## 27.引入Spring Cloud Stream

Spring Cloud Stream是用于构建消息驱动的微服务应用程序的框架。Spring Cloud Stream基于Spring Boot来创建独立的生产级Spring应用程序，并使用Spring Integration提供与消息代理的连接。它提供了来自多家供应商的中间件的合理配置，并介绍了持久性发布-订阅语义，使用者组和分区的概念。

您可以将`@EnableBinding`注释添加到您的应用程序中，以立即连接到消息代理，还可以添加`@StreamListener`一种方法以使它接收事件以进行流处理。以下示例显示了接收外部消息的接收器应用程序：

```java
@SpringBootApplication
@EnableBinding(Sink.class)
public class VoteRecordingSinkApplication {

  public static void main(String[] args) {
    SpringApplication.run(VoteRecordingSinkApplication.class, args);
  }

  @StreamListener(Sink.INPUT)
  public void processVote(Vote vote) {
      votingService.recordVote(vote);
  }
}
```

的`@EnableBinding`批注采用一个或多个接口作为参数（在这种情况下，该参数是一个单一的`Sink`接口）。接口声明输入和输出通道。春季云流提供`Source`，`Sink`和`Processor`接口。您也可以定义自己的接口。

以下清单显示了`Sink`接口的定义：

```java
public interface Sink {
  String INPUT = "input";

  @Input(Sink.INPUT)
  SubscribableChannel input();
}
```

该`@Input`注释来识别输入的信道，通过该接收的消息输入应用程序。的`@Output`注释表示一个输出通道，通过它发布的消息退出程序。的`@Input`和`@Output`注解可以采取频道名称作为参数。如果未提供名称，则使用带注释的方法的名称。

Spring Cloud Stream为您创建接口的实现。您可以通过自动装配在应用程序中使用它，如以下示例所示（来自测试用例）：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VoteRecordingSinkApplication.class)
@WebAppConfiguration
@DirtiesContext
public class StreamApplicationTests {

  @Autowired
  private Sink sink;

  @Test
  public void contextLoads() {
    assertNotNull(this.sink.input());
  }
}
```