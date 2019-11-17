# 26、日志

Spring Boot使用[Commons Logging](https://commons.apache.org/logging)进行所有内部日志记录，但是使底层日志实现保持打开状态。为[Java Util Logging](https://docs.oracle.com/javase/8/docs/api//java/util/logging/package-summary.html)，[Log4J2](https://logging.apache.org/log4j/2.x/)和[Logback](https://logback.qos.ch/)提供了默认配置。在每种情况下，记录器都已预先配置为使用控制台输出，同时还提供可选文件输出。

默认情况下，如果使用“启动器”，则使用Logback进行日志记录。还包括适当的Logback路由，以确保使用Java Util Logging，Commons Logging，Log4J或SLF4J的从属库都可以正常工作。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| Java有许多可用的日志记录框架。如果上面的列表看起来令人困惑，请不要担心。通常，您不需要更改日志记录依赖项，并且Spring Boot默认值可以正常工作。 |

## 26.1日志格式

Spring Boot的默认日志输出类似于以下示例：

```bash
2019-03-05 10:57:51.112  INFO 45469 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet Engine: Apache Tomcat/7.0.52
2019-03-05 10:57:51.253  INFO 45469 --- [ost-startStop-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2019-03-05 10:57:51.253  INFO 45469 --- [ost-startStop-1] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1358 ms
2019-03-05 10:57:51.698  INFO 45469 --- [ost-startStop-1] o.s.b.c.e.ServletRegistrationBean        : Mapping servlet: 'dispatcherServlet' to [/]
2019-03-05 10:57:51.702  INFO 45469 --- [ost-startStop-1] o.s.b.c.embedded.FilterRegistrationBean  : Mapping filter: 'hiddenHttpMethodFilter' to: [/*]
```

输出以下项目：

- 日期和时间：毫秒精度，易于排序。
- 日志级别：`ERROR`，`WARN`，`INFO`，`DEBUG`，或`TRACE`。
- 进程ID。
- 一个`---`分离器来区分实际日志消息的开始。
- 线程名称：用方括号括起来（对于控制台输出可能会被截断）。
- 记录器名称：这通常是源类名称（通常缩写）。
- 日志消息。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| Logback没有`FATAL`级别。它映射到`ERROR`。                    |

## 26.2控制台输出

默认日志配置在消息写入时将消息回显到控制台。默认情况下，将记录`ERROR`-level，`WARN`-level和`INFO`-level消息。您还可以通过使用`--debug`标志启动应用程序来启用“调试”模式。

```bash
$ java -jar myapp.jar --debug
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 您也可以`debug=true`在中指定`application.properties`。       |

启用调试模式后，将配置一些核心记录器（嵌入式容器，Hibernate和Spring Boot）以输出更多信息。启用调试模式并*没有*配置您的应用程序记录所有消息`DEBUG`的水平。

另外，您可以通过使用`--trace`标志（或`trace=true`在中`application.properties`）启动应用程序来启用“跟踪”模式。这样做可以为某些核心记录器（嵌入式容器，Hibernate模式生成以及整个Spring产品组合）启用跟踪记录。

### 26.2.1颜色编码的输出

如果您的终端支持ANSI，则使用彩色输出来提高可读性。您可以设置`spring.output.ansi.enabled`为[支持的值](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/ansi/AnsiOutput.Enabled.html)以覆盖自动检测。

通过使用`%clr`转换字来配置颜色编码。转换器以最简单的形式根据对数级别为输出着色，如以下示例所示：

```bash
%clr(%5p)
```

下表描述了日志级别到颜色的映射：

| 级别    | 颜色 |
| ------- | ---- |
| `FATAL` | 红色 |
| `ERROR` | 红色 |
| `WARN`  | 黄色 |
| `INFO`  | 绿色 |
| `DEBUG` | 绿色 |
| `TRACE` | 绿色 |

另外，您可以通过将其提供为转换的选项来指定应使用的颜色或样式。例如，要使文本变黄，请使用以下设置：

```java
%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){yellow}
```

支持以下颜色和样式：

- `blue`
- `cyan`
- `faint`
- `green`
- `magenta`
- `red`
- `yellow`

## 26.3文件输出

默认情况下，Spring Boot仅记录到控制台，不写日志文件。如果除了控制台输出外还想写日志文件，则需要设置一个`logging.file`或`logging.path`属性（例如，在中`application.properties`）。

下表显示了如何`logging.*`一起使用这些属性：



**表26.1 记录属性**

| `logging.file` | `logging.path` | 例         | 描述                                                         |
| -------------- | -------------- | ---------- | ------------------------------------------------------------ |
| *（没有）*     | *（没有）*     |            | 仅控制台记录。                                               |
| 特定档案       | *（没有）*     | `my.log`   | 写入指定的日志文件。名称可以是确切位置，也可以是相对于当前目录的位置。 |
| *（没有）*     | 具体目录       | `/var/log` | 写入`spring.log`指定的目录。名称可以是确切位置，也可以是相对于当前目录的位置。 |



日志文件达到10 MB时会旋转，并且与控制台输出一样，默认情况下会记录`ERROR`-level，`WARN`-level和`INFO`-level消息。可以使用该`logging.file.max-size`属性更改大小限制。除非`logging.file.max-history`已设置属性，否则以前旋转的文件将无限期存档。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 日志记录系统在应用程序生命周期的早期进行了初始化。因此，在通过`@PropertySource`注释加载的属性文件中找不到日志记录属性。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 日志记录属性独立于实际的日志记录基础结构。结果，`logback.configurationFile`Spring Boot不会管理特定的配置密钥（例如Logback）。 |

## 26.4日志级别

所有支持的日志系统可以在弹簧设置的记录器级别`Environment`（例如，`application.properties`通过使用）`logging.level.<logger-name>=<level>`，其中`level`为TRACE，DEBUG，INFO，WARN，ERROR，FATAL或OFF之一。该`root`记录器可以通过使用被配置`logging.level.root`。

以下示例显示了中的潜在日志记录设置`application.properties`：

```bash
logging.level.root=warn
logging.level.org.springframework.web=debug
logging.level.org.hibernate=error
```

也可以使用环境变量设置日志记录级别。例如，`LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG`将设置`org.springframework.web`为`DEBUG`。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 以上方法仅适用于程序包级别的日志记录。由于宽松的绑定总是将环境变量转换为小写，因此无法以这种方式为单个类配置日志记录。如果需要为类配置日志记录，则可以使用[SPRING_APPLICATION_JSON](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-application-json)变量。 |

## 26.5日志组

能够将相关记录器分组在一起通常很有用，以便可以同时配置它们。例如，您可能通常会更改*所有*与Tomcat相关的记录器的记录级别，但是您不容易记住顶层软件包。

为了解决这个问题，Spring Boot允许您在Spring中定义日志记录组`Environment`。例如，这是通过将“ tomcat”组添加到您的方式来定义它的方法`application.properties`：

```bash
logging.group.tomcat=org.apache.catalina, org.apache.coyote, org.apache.tomcat
```

定义后，您可以使用一行更改该组中所有记录器的级别：

```bash
logging.level.tomcat=TRACE
```

Spring Boot包含以下预定义的日志记录组，它们可以直接使用：

| 名称 | 记录仪                                                       |
| ---- | ------------------------------------------------------------ |
| 网络 | `org.springframework.core.codec`，`org.springframework.http`，`org.springframework.web`，`org.springframework.boot.actuate.endpoint.web`，`org.springframework.boot.web.servlet.ServletContextInitializerBeans` |
| sql  | `org.springframework.jdbc.core`， `org.hibernate.SQL`        |

## 26.6自定义日志配置

可以通过在类路径中包含适当的库来激活各种日志记录系统，并可以通过在类路径的根目录或以下Spring `Environment`属性指定的位置中提供适当的配置文件来进一步自定义各种日志记录系统`logging.config`。

您可以通过使用`org.springframework.boot.logging.LoggingSystem`system属性来强制Spring Boot使用特定的日志系统。该值应该是实现的完全限定的类名`LoggingSystem`。您还可以通过使用值完全禁用Spring Boot的日志记录配置`none`。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 由于日志记录是**在**`ApplicationContext`创建**之前**初始化的，因此无法从`@PropertySources`Spring `@Configuration`文件中控制日志记录。更改日志记录系统或完全禁用它的唯一方法是通过系统属性。 |

根据您的日志记录系统，将加载以下文件：

| 日志系统                    | 客制化                                                       |
| --------------------------- | ------------------------------------------------------------ |
| Logback                     | `logback-spring.xml`，`logback-spring.groovy`，`logback.xml`，或者`logback.groovy` |
| Log4j2                      | `log4j2-spring.xml` 要么 `log4j2.xml`                        |
| JDK（Java实用程序日志记录） | `logging.properties`                                         |

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果可能，我们建议您将`-spring`变体用于日志记录配置（例如，`logback-spring.xml`而不是`logback.xml`）。如果使用标准配置位置，Spring将无法完全控制日志初始化。 |

| ![[警告]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/warning.png) |
| ------------------------------------------------------------ |
| 从“可执行jar”运行时，Java Util Logging存在一些已知的类加载问题，这些问题会引起问题。我们建议您尽可能从“可执行jar”运行时避免使用它。 |

为了帮助进行自定义，`Environment`如下表所述，一些其他属性从Spring转移到System属性：

| 春季环境                            | 系统属性                        | 评论                                                         |
| ----------------------------------- | ------------------------------- | ------------------------------------------------------------ |
| `logging.exception-conversion-word` | `LOG_EXCEPTION_CONVERSION_WORD` | 记录异常时使用的转换字。                                     |
| `logging.file`                      | `LOG_FILE`                      | 如果定义，它将在默认日志配置中使用。                         |
| `logging.file.max-size`             | `LOG_FILE_MAX_SIZE`             | 最大日志文件大小（如果启用了LOG_FILE）。（仅默认的Logback设置受支持。） |
| `logging.file.max-history`          | `LOG_FILE_MAX_HISTORY`          | 要保留的最大归档日志文件数（如果启用了LOG_FILE）。（仅默认的Logback设置受支持。） |
| `logging.path`                      | `LOG_PATH`                      | 如果定义，它将在默认日志配置中使用。                         |
| `logging.pattern.console`           | `CONSOLE_LOG_PATTERN`           | 控制台上使用的日志模式（stdout）。（仅默认的Logback设置受支持。） |
| `logging.pattern.dateformat`        | `LOG_DATEFORMAT_PATTERN`        | 记录日期格式的附加模式。（仅默认的Logback设置受支持。）      |
| `logging.pattern.file`              | `FILE_LOG_PATTERN`              | 文件中使用的日志模式（如果`LOG_FILE`已启用）。（仅默认的Logback设置受支持。） |
| `logging.pattern.level`             | `LOG_LEVEL_PATTERN`             | 呈现日志级别时使用的格式（默认`%5p`）。（仅默认的Logback设置受支持。） |
| `PID`                               | `PID`                           | 当前进程ID（如果可能，并且尚未将其定义为OS环境变量时，将被发现）。 |

所有受支持的日志记录系统在解析其配置文件时都可以查阅系统属性。有关`spring-boot.jar`示例，请参见中的默认配置：

- [登陆回](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/logback/defaults.xml)
- [Log4j 2](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/log4j2/log4j2.xml)
- [Java Util日志记录](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/java/logging-file.properties)

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果要在日志记录属性中使用占位符，则应使用[Spring Boot的语法](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-placeholders-in-properties)而不是基础框架的语法。值得注意的是，如果您使用Logback，则应将其`:`用作属性名称与其默认值之间的分隔符，而不应使用`:-`。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您可以通过仅覆盖`LOG_LEVEL_PATTERN`（或`logging.pattern.level`使用Logback）将MDC和其他临时内容添加到日志行。例如，如果使用`logging.pattern.level=user:%X{user} %5p`，则默认日志格式包含“ user”的MDC条目（如果存在），如以下示例所示。`2019-08-30 12：30：04.031用户：某人INFO 22174-[[nio-8080-exec-0] demo.Controller 处理已认证的请求` |

## 26.7 Logback扩展

Spring Boot包含许多Logback扩展，可以帮助进行高级配置。您可以在`logback-spring.xml`配置文件中使用这些扩展名。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 由于标准`logback.xml`配置文件加载太早，因此您不能在其中使用扩展名。您需要使用`logback-spring.xml`或定义一个`logging.config`属性。 |

| ![[警告]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/warning.png) |
| ------------------------------------------------------------ |
| 这些扩展不能与Logback的[配置扫描一起使用](https://logback.qos.ch/manual/configuration.html#autoScan)。如果尝试这样做，则对配置文件进行更改将导致类似于以下记录之一的错误： |

```bash
ERROR in ch.qos.logback.core.joran.spi.Interpreter@4:71 - no applicable action for [springProperty], current ElementPath is [[configuration][springProperty]]
ERROR in ch.qos.logback.core.joran.spi.Interpreter@4:71 - no applicable action for [springProfile], current ElementPath is [[configuration][springProfile]]
```

### 26.7.1特定于配置文件的配置

使用`<springProfile>`标签，您可以根据活动的Spring配置文件选择包括或排除配置部分。概要文件部分在`<configuration>`元素内的任何位置都受支持。使用`name`属性指定哪个配置文件接受配置。所述`<springProfile>`标记可包含一个简单的配置文件的名称（例如`staging`）或轮廓表达。配置文件表达式允许例如表达更复杂的配置文件逻辑`production & (eu-central | eu-west)`。有关更多详细信息，请参阅[参考指南](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#beans-definition-profiles-java)。以下清单显示了三个样本概要文件：

```xml
<springProfile name="staging">
	<!-- configuration to be enabled when the "staging" profile is active -->
</springProfile>

<springProfile name="dev | staging">
	<!-- configuration to be enabled when the "dev" or "staging" profiles are active -->
</springProfile>

<springProfile name="!production">
	<!-- configuration to be enabled when the "production" profile is not active -->
</springProfile>
```

### 26.7.2环境属性

该`<springProperty>`标签可以让你从Spring公开属性`Environment`的范围内的logback使用。如果您想从`application.properties`Logback配置中访问文件中的值，这样做会很有用。该标签的工作方式类似于Logback的标准`<property>`标签。但是，`value`您无需指定direct ，而是指定`source`属性的（来自`Environment`）。如果需要将属性存储在`local`范围之外的其他位置，则可以使用该`scope`属性。如果需要后备值（如果未在中设置属性`Environment`），则可以使用`defaultValue`属性。以下示例显示如何公开在Logback中使用的属性：

```xml
<springProperty scope="context" name="fluentHost" source="myapp.fluentd.host"
		defaultValue="localhost"/>
<appender name="FLUENT" class="ch.qos.logback.more.appenders.DataFluentAppender">
	<remoteHost>${fluentHost}</remoteHost>
	...
</appender>
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 在`source`必须在串的情况下（如指定`my.property-name`）。但是，可以`Environment`使用宽松规则将属性添加到中。 |