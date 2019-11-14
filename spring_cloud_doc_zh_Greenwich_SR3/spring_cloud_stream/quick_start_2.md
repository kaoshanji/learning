# 25. Quick Start

## 25.快速入门

您可以按照以下三步指南在不到5分钟的时间内尝试使用Spring Cloud Stream，甚至无需跳入任何细节。

我们向您展示了如何创建一个Spring Cloud Stream应用程序，该应用程序接收来自您选择的消息传递中间件的消息（稍后会详细介绍），并将接收到的消息记录到控制台。我们称之为`LoggingConsumer`。尽管不是很实用，但是它很好地介绍了一些主要概念和抽象，使您更容易理解本用户指南的其余部分。

三个步骤如下：

1. [第25.1节“使用Spring Initializr创建示例应用程序”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__quick_start_2.html#spring-cloud-stream-preface-creating-sample-application)
2. [第25.2节“将项目导入IDE”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__quick_start_2.html#spring-cloud-stream-preface-importing-project)
3. [第25.3节“添加消息处理程序，构建和运行”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__quick_start_2.html#spring-cloud-stream-preface-adding-message-handler)

## 25.1使用Spring Initializr创建一个示例应用程序

首先，请访问[Spring Initializr](https://start.spring.io/)。从那里，您可以生成我们的`LoggingConsumer`应用程序。为此：

1. 在“ **依赖关系”**部分，开始输入`stream`。当“ 云流 ”选项出现时，选择它。

2. 开始输入“ kafka”或“兔子”。

3. 选择“ Kafka ”或“ RabbitMQ ”。

   基本上，您选择应用程序绑定到的消息传递中间件。我们建议您使用已经安装的一种，或者对安装和运行感到更自在。另外，从“初始化程序”屏幕中可以看到，还有一些其他选项可以选择。例如，您可以选择Gradle作为构建工具，而不是Maven（默认设置）。

4. 在**工件**字段中，输入“ logging-consumer”。

   **Artifact**字段的值成为应用程序名称。如果您选择RabbitMQ作为中间件，那么Spring Initializr现在应该如下所示：

   ![流初始化](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/stream-initializr.png)

5. 单击**生成项目**按钮。

   这样做会将生成的项目的压缩版本下载到硬盘上。

6. 将文件解压缩到要用作项目目录的文件夹中。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 我们鼓励您探索Spring Initializr中可用的许多可能性。它使您可以创建许多不同种类的Spring应用程序。 |

## 25.2将项目导入IDE

现在，您可以将项目导入到IDE中。请记住，取决于IDE，您可能需要遵循特定的导入过程。例如，根据项目的生成方式（Maven或Gradle），您可能需要遵循特定的导入过程（例如，在Eclipse或STS中，您需要使用File→Import→Maven→Existing Maven Project）。

导入后，该项目必须没有任何类型的错误。另外，`src/main/java`应包含`com.example.loggingconsumer.LoggingConsumerApplication`。

从技术上讲，此时，您可以运行应用程序的主类。它已经是一个有效的Spring Boot应用程序。但是，它没有任何作用，因此我们想添加一些代码。

## 25.3添加消息处理程序，构建并运行

修改`com.example.loggingconsumer.LoggingConsumerApplication`类，如下所示：

```java
@SpringBootApplication
@EnableBinding(Sink.class)
public class LoggingConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoggingConsumerApplication.class, args);
	}

	@StreamListener(Sink.INPUT)
	public void handle(Person person) {
		System.out.println("Received: " + person);
	}

	public static class Person {
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String toString() {
			return this.name;
		}
	}
}
```

从前面的清单中可以看到：

- 我们已经`Sink`通过使用启用了绑定（input-no-output）`@EnableBinding(Sink.class)`。这样做会向框架发出信号，以启动对消息传递中间件的绑定，并在其中自动创建绑定到`Sink.INPUT`通道的目的地（即队列，主题等）。
- 我们添加了`handler`一种接收类型为的传入消息的方法`Person`。这样做可以使您看到框架的核心功能之一：它尝试将传入的消息有效负载自动转换为type `Person`。

现在，您有了一个功能齐全的Spring Cloud Stream应用程序，该应用程序确实侦听消息。从这里开始，为简单起见，我们假设您在[第一步中](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__quick_start_2.html#spring-cloud-stream-preface-creating-sample-application)选择RabbitMQ 。假设已安装并运行RabbitMQ，则可以通过`main`在IDE中运行其方法来启动应用程序。

您应该看到以下输出：

```properties
	--- [ main] c.s.b.r.p.RabbitExchangeQueueProvisioner : declaring queue for inbound: input.anonymous.CbMIwdkJSBO1ZoPDOtHtCg, bound to: input
	--- [ main] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
	--- [ main] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#2a3a299:0/SimpleConnection@66c83fc8. . .
	. . .
	--- [ main] o.s.i.a.i.AmqpInboundChannelAdapter      : started inbound.input.anonymous.CbMIwdkJSBO1ZoPDOtHtCg
	. . .
	--- [ main] c.e.l.LoggingConsumerApplication         : Started LoggingConsumerApplication in 2.531 seconds (JVM running for 2.897)	-[[main] csbrpRabbitExchangeQueueProvisioner：声明入站队列：input.anonymous.CbMIwdkJSBO1ZoPDOtHtCg，绑定到：input
	--- [main] osarcCachingConnectionFactory：尝试连接到：[localhost：5672]
	--- [main] osarcCachingConnectionFactory：创建了新的连接：rabbitConnectionFactory＃2a3a299：0 / SimpleConnection @ 66c83fc8。。。
	。。。
	-[[main] osiaiAmqpInboundChannelAdapter：已启动inbound.input.anonymous.CbMIwdkJSBO1ZoPDOtHtCg
	。。。
	--- [main] celLoggingConsumerApplication：在2.531秒内启动LoggingConsumerApplication（JVM运行2.897）
```

转到RabbitMQ管理控制台或任何其他RabbitMQ客户端，然后向发送消息`input.anonymous.CbMIwdkJSBO1ZoPDOtHtCg`。该`anonymous.CbMIwdkJSBO1ZoPDOtHtCg`部分表示组名并已生成，因此它在您的环境中必然是不同的。对于更可预测的内容，可以通过设置`spring.cloud.stream.bindings.input.group=hello`（或您喜欢的任何名称）使用显式组名。

消息的内容应为`Person`该类的JSON表示形式，如下所示：

```json
{“ name”：“ Sam Spade”}
```

然后，在控制台中，您应该看到：

```bash
Received: Sam Spade
```

您还可以将应用程序构建并打包到引导jar中（通过使用`./mvnw clean install`），并使用以下`java -jar`命令来运行构建的JAR 。

现在，您有了一个正在运行的（尽管非常基础）Spring Cloud Stream应用程序。