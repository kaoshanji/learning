# 23、SpringBootApplication

本`SpringApplication`类提供了一个方便的方式来引导该从开始Spring应用程序`main()`的方法。在许多情况下，您可以委派给静态`SpringApplication.run`方法，如以下示例所示：

```java
public static void main(String[] args) {
	SpringApplication.run(MySpringConfiguration.class, args);
}
```

当您的应用程序启动时，您应该看到类似于以下输出的内容：

```bash
 .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::   v2.1.9.RELEASE

2019-04-31 13:09:54.117  INFO 56603 --- [           main] o.s.b.s.app.SampleApplication            : Starting SampleApplication v0.1.0 on mycomputer with PID 56603 (/apps/myapp.jar started by pwebb)
2019-04-31 13:09:54.166  INFO 56603 --- [           main] ationConfigServletWebServerApplicationContext : Refreshing org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@6e5a8246: startup date [Wed Jul 31 00:08:16 PDT 2013]; root of context hierarchy
2019-04-01 13:09:56.912  INFO 41370 --- [           main] .t.TomcatServletWebServerFactory : Server initialized with port: 8080
2019-04-01 13:09:57.501  INFO 41370 --- [           main] o.s.b.s.app.SampleApplication            : Started SampleApplication in 2.992 seconds (JVM running for 3.658)
```

默认情况下，显示`INFO`日志消息，包括一些相关的启动详细信息，例如启动应用程序的用户。如果您需要除以外的其他日志级别`INFO`，则可以进行设置，如[第26.4节“日志级别”所述](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-logging.html#boot-features-custom-log-levels)。

## 23.1启动失败

如果您的应用程序无法启动，则注册`FailureAnalyzers`后将有机会提供专门的错误消息和解决该问题的具体措施。例如，如果您在端口上启动Web应用程序`8080`并且该端口已在使用中，则应该看到类似以下消息的内容：

```bash
***************************
APPLICATION FAILED TO START
***************************

Description:

Embedded servlet container failed to start. Port 8080 was already in use.

Action:

Identify and stop the process that's listening on port 8080 or configure this application to listen on another port.
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| Spring Boot提供了许多`FailureAnalyzer`实现，您可以[添加自己的实现](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-spring-boot-application.html#howto-failure-analyzer)。 |

如果没有故障分析器能够处理该异常，您仍然可以显示完整情况报告以更好地了解出了什么问题。要做到这一点，你需要[使`debug`财产](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html)或[启用`DEBUG`日志记录](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-logging.html#boot-features-custom-log-levels)的`org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener`。

例如，如果使用来运行应用程序`java -jar`，则可以`debug`按如下所示启用属性：

```bash
$ java -jar myproject-0.0.1-SNAPSHOT.jar --debug
```

## 23.2自定义横幅

可以通过将`banner.txt`文件添加到类路径或将`spring.banner.location`属性设置为此类文件的位置来更改启动时打印的横幅。如果文件的编码不是UTF-8，则可以设置`spring.banner.charset`。除了一个文本文件，你还可以添加一个`banner.gif`，`banner.jpg`或`banner.png`图像文件到类路径或设置`spring.banner.image.location`属性。图像将转换为ASCII艺术作品并打印在任何文字横幅上方。

在`banner.txt`文件内部，可以使用以下任意占位符：



**表23.1 标语变量**

| 变量                                                         | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `${application.version}`                                     | 您的应用程序的版本号，如中所述`MANIFEST.MF`。例如，`Implementation-Version: 1.0`打印为`1.0`。 |
| `${application.formatted-version}`                           | 您的应用程序的版本号，已声明`MANIFEST.MF`并进行了格式显示（用括号括起来，并带有前缀`v`）。例如`(v1.0)`。 |
| `${spring-boot.version}`                                     | 您正在使用的Spring Boot版本。例如`2.1.9.RELEASE`。           |
| `${spring-boot.formatted-version}`                           | 您正在使用的Spring Boot版本，其格式用于显示（用括号括起来，并带有前缀`v`）。例如`(v2.1.9.RELEASE)`。 |
| `${Ansi.NAME}`（或`${AnsiColor.NAME}`，`${AnsiBackground.NAME}`，`${AnsiStyle.NAME}`） | `NAME`ANSI转义代码的名称在哪里。有关[`AnsiPropertySource`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/ansi/AnsiPropertySource.java)详细信息，请参见。 |
| `${application.title}`                                       | 您的应用程序的标题，如中所述`MANIFEST.MF`。例如`Implementation-Title: MyApp`打印为`MyApp`。 |



| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| `SpringApplication.setBanner(…)`如果要以编程方式生成横幅，则可以使用该方法。使用该`org.springframework.boot.Banner`接口并实现您自己的`printBanner()`方法。 |

您还可以使用该`spring.main.banner-mode`属性来确定横幅是否必须在`System.out`（`console`）上打印，发送到配置的记录器（`log`）或根本不制作（`off`）。

打印的横幅注册为下以下名称的单例的bean： `springBootBanner`。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| YAML映射`off`到`false`，因此，如果要在应用程序中禁用横幅，请确保添加引号，如以下示例所示：`spring:<br/>	main:<br/>		banner-mode: "off"` |

## 23.3自定义SpringApplication

如果`SpringApplication`默认设置不符合您的喜好，则可以创建一个本地实例并对其进行自定义。例如，要关闭横幅，您可以编写：

```java
public static void main(String[] args) {
	SpringApplication app = new SpringApplication(MySpringConfiguration.class);
	app.setBannerMode(Banner.Mode.OFF);
	app.run(args);
}
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 传递给构造函数的参数`SpringApplication`是Spring bean的配置源。在大多数情况下，这些是对`@Configuration`类的引用，但它们也可以是对XML配置或应扫描的程序包的引用。 |

也可以`SpringApplication`通过使用`application.properties`文件来配置。有关详细信息*，*请参见*第24章，**外部化配置*。

有关配置选项的完整列表，请参见[`SpringApplication`Javadoc](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/SpringApplication.html)。

## 23.4 Fluent Builder API

如果您需要构建`ApplicationContext`层次结构（具有父/子关系的多个上下文），或者您更喜欢使用“流利的”构建器API，则可以使用`SpringApplicationBuilder`。

在`SpringApplicationBuilder`让要链接的多个方法调用，并且包括`parent`和`child`方法，让你创建层次结构，以显示在下面的例子：

```java
new SpringApplicationBuilder()
		.sources(Parent.class)
		.child(Application.class)
		.bannerMode(Banner.Mode.OFF)
		.run(args);
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 创建`ApplicationContext`层次结构时有一些限制。例如，Web组件**必须**包含在子上下文中，并且`Environment`父和子上下文都使用相同的组件。有关完整的详细信息，请参见[`SpringApplicationBuilder`Javadoc](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/builder/SpringApplicationBuilder.html)。 |

## 23.5应用程序事件和监听器

除了通常的Spring Framework事件（例如）外[`ContextRefreshedEvent`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/javadoc-api/org/springframework/context/event/ContextRefreshedEvent.html)，`SpringApplication`还会发送一些其他应用程序事件。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 有些事件实际上`ApplicationContext`是在创建之前触发的，因此您无法将其注册为`@Bean`。您可以使用`SpringApplication.addListeners(…)`方法或`SpringApplicationBuilder.listeners(…)`方法注册它们。如果您希望这些侦听器自动注册，而不管创建应用程序的方式如何，都可以将`META-INF/spring.factories`文件添加到项目中，并使用`org.springframework.context.ApplicationListener`键引用您的侦听器，如以下示例所示：`org.springframework.context.ApplicationListener = com.example.project.MyListener` |

应用程序事件在您的应用程序运行时按以下顺序发送：

1. `ApplicationStartingEvent`在运行开始时发送an ，但在进行任何处理之前（侦听器和初始化程序的注册除外）发送。
2. 一个`ApplicationEnvironmentPreparedEvent`当被发送`Environment`到中已知的上下文中使用，但是在创建上下文之前。
3. `ApplicationPreparedEvent`在刷新开始之前但在加载bean定义之后发送an 。
4. 一个`ApplicationStartedEvent`上下文已被刷新后发送，但是任何应用程序和命令行亚军都被调用前。
5. 的`ApplicationReadyEvent`任何应用程序和命令行亚军被呼叫后发送。它指示该应用程序已准备就绪，可以处理请求。
6. 一个`ApplicationFailedEvent`如果在启动时异常发送。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您通常不需要使用应用程序事件，但是很容易知道它们的存在。在内部，Spring Boot使用事件来处理各种任务。 |

应用程序事件是通过使用Spring Framework的事件发布机制发送的。此机制的一部分确保在子级上下文中发布给侦听器的事件也可以在任何祖先上下文中发布给侦听器。结果，如果您的应用程序使用`SpringApplication`实例的层次结构，则侦听器可能会收到同一类型的应用程序事件的多个实例。

为了使您的侦听器能够区分其上下文的事件和后代上下文的事件，它应请求注入其应用程序上下文，然后将注入的上下文与事件的上下文进行比较。可以通过实现来注入上下文，`ApplicationContextAware`或者，如果侦听器是bean，则可以使用注入上下文`@Autowired`。

## 23.6 Web环境

一个`SpringApplication`试图创建正确类型的`ApplicationContext`代表您。确定a的算法`WebApplicationType`非常简单：

- 如果存在Spring MVC，`AnnotationConfigServletWebServerApplicationContext`则使用
- 如果不存在Spring MVC且存在Spring WebFlux，`AnnotationConfigReactiveWebServerApplicationContext`则使用
- 否则，`AnnotationConfigApplicationContext`使用

这意味着，如果您`WebClient`在同一应用程序中使用Spring MVC和Spring WebFlux中的新功能，则默认情况下将使用Spring MVC。您可以通过调用轻松覆盖它`setWebApplicationType(WebApplicationType)`。

也可以完全控制`ApplicationContext`调用所使用的类型`setApplicationContextClass(…)`。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 在JUnit测试中`setWebApplicationType(WebApplicationType.NONE)`使用时通常需要调用`SpringApplication`。 |

## 23.7访问应用程序参数

如果您需要访问传递给的应用程序参数，则`SpringApplication.run(…)`可以注入`org.springframework.boot.ApplicationArguments`Bean。该`ApplicationArguments`接口提供对原始`String[]`参数以及已解析`option`和`non-option`参数的访问，如以下示例所示：

```java
import org.springframework.boot.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class MyBean {

	@Autowired
	public MyBean(ApplicationArguments args) {
		boolean debug = args.containsOption("debug");
		List<String> files = args.getNonOptionArgs();
		// if run with "--debug logfile.txt" debug=true, files=["logfile.txt"]
	}

}
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| Spring Boot也`CommandLinePropertySource`向Spring 注册了一个`Environment`。这样，您还可以使用`@Value`注释注入单个应用程序参数。 |

## 23.8使用ApplicationRunner或CommandLineRunner

如果启动后需要运行一些特定的代码`SpringApplication`，则可以实现`ApplicationRunner`或`CommandLineRunner`接口。这两个接口以相同的方式工作，并提供一个单一的`run`方法，该方法在`SpringApplication.run(…)`完成之前就被调用。

所述`CommandLineRunner`接口提供访问的应用程序的参数作为一个简单的字符串数组，而`ApplicationRunner`使用了`ApplicationArguments`前面所讨论的接口。以下示例显示了一个`CommandLineRunner`with `run`方法：

```java
import org.springframework.boot.*;
import org.springframework.stereotype.*;

@Component
public class MyBean implements CommandLineRunner {

	public void run(String... args) {
		// Do something...
	}

}
```

如果几个`CommandLineRunner`或`ApplicationRunner`豆类中定义必须在一个特定的顺序被调用，您还可以实现`org.springframework.core.Ordered`接口或使用`org.springframework.core.annotation.Order`注解。

## 23.9申请退出

每个都`SpringApplication`向JVM注册一个关闭钩子，以确保`ApplicationContext`退出时正常关闭。可以使用所有标准的Spring生命周期回调（例如`DisposableBean`接口或`@PreDestroy`批注）。

另外，`org.springframework.boot.ExitCodeGenerator`如果bean 希望在`SpringApplication.exit()`调用时返回特定的退出代码，则可以实现该接口。然后可以将此退出代码传递给`System.exit()`它，以将其作为状态代码返回，如以下示例所示：

```java
@SpringBootApplication
public class ExitCodeApplication {

	@Bean
	public ExitCodeGenerator exitCodeGenerator() {
		return () -> 42;
	}

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(ExitCodeApplication.class, args)));
	}

}
```

而且，该`ExitCodeGenerator`接口可以通过异常来实现。当遇到这样的异常时，Spring Boot返回由实现的`getExitCode()`方法提供的退出代码。

## 23.10管理员功能

通过指定`spring.application.admin.enabled`属性，可以为应用程序启用与管理员相关的功能。这将[`SpringApplicationAdminMXBean`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/admin/SpringApplicationAdminMXBean.java)在平台上公开`MBeanServer`。您可以使用此功能来远程管理Spring Boot应用程序。此功能对于任何服务包装器实现也可能很有用。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果您想知道应用程序在哪个HTTP端口上运行，请通过键获取属性`local.server.port`。 |