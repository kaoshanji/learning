# 23.日志

### 介绍

Apache Tomcat的内部日志记录使用JULI，JULI是[Apache Commons Logging](https://commons.apache.org/logging)的打包重命名的fork ，经过硬编码以使用该`java.util.logging`框架。这样可以确保Tomcat的内部日志记录和任何Web应用程序日志都将保持独立，即使Web应用程序使用Apache Commons Logging。

要将Tomcat配置为使用备用日志记录框架进行内部日志记录，请遵循备用日志记录框架提供的指示信息，以重定向使用的应用程序的日志记录 `java.util.logging`。请记住，替代的日志记录框架将需要能够在不同类加载器中可能存在具有相同名称的不同记录器的环境中工作。

在Apache Tomcat上运行的Web应用程序可以：

- 使用其选择的任何日志记录框架。
- 使用系统日志记录API `java.util.logging`。
- 使用Java Servlets规范提供的日志记录API， `javax.servlet.ServletContext.log(...)`

不同的Web应用程序使用的日志记录框架是独立的。有关更多详细信息，请参见[类加载](http://tomcat.apache.org/tomcat-9.0-doc/class-loader-howto.html)。此规则的例外是`java.util.logging`。如果它由日志库直接或间接使用，则它的元素将在Web应用程序之间共享，因为它是由系统类加载器加载的。

#### Java日志记录API — java.util.logging

Apache Tomcat具有自己的`java.util.logging`API 几个关键元素的实现 。此实现称为JULI。关键组件有一个自定义的LogManager实现，该实现了解Tomcat上运行的不同Web应用程序（及其不同的类加载器）。它支持每个应用程序的专用日志记录配置。当从内存中卸载Web应用程序时，Tomcat也会通知它，以便可以清除对其类的引用，从而防止内存泄漏。

`java.util.logging`通过在启动Java时提供某些系统属性来启用 此实现。Apache Tomcat启动脚本会为您执行此操作，但是如果您使用其他工具运行Tomcat（例如jsvc或从IDE内部运行Tomcat），则应该自己照顾它们。

有关java.util.logging的更多详细信息，可以在您的JDK文档以及该`java.util.logging` 软件包的Javadoc页面中找到。

可以在下面找到有关Tomcat JULI的更多详细信息。

#### Servlet日志记录API

`javax.servlet.ServletContext.log(...)`写入日志消息 的调用由内部Tomcat日志处理。这样的消息被记录到名为

```
org.apache.catalina.core.ContainerBase.[${engine}].[${host}].[${context}]
```

该日志记录是根据Tomcat日志记录配置执行的。您不能在Web应用程序中覆盖它。

Servlet日志记录API早于`java.util.logging`Java现在提供的API。因此，它没有为您提供太多选择。例如，您无法控制日志级别。但是，应该指出，在Apache Tomcat实现中，对的调用`ServletContext.log(String)` 或`GenericServlet.log(String)`记录在INFO级别。呼叫`ServletContext.log(String, Throwable)`或 `GenericServlet.log(String, Throwable)` 记录在SEVERE级别。

#### 控制台

在Unix上运行Tomcat时，控制台输出通常会重定向到名为的文件`catalina.out`。该名称可使用环境变量进行配置。（请参阅启动脚本）。写入的内容`System.err/out`将被捕获到该文件中。其中可能包括：

- 未被记录的异常 `java.lang.ThreadGroup.uncaughtException(..)`
- 线程转储（如果通过系统信号请求它们）

在Windows上作为服务运行时，也会捕获并重定向控制台输出，但是文件名不同。

Apache Tomcat中的默认日志记录配置将相同的消息写入控制台和日志文件。使用Tomcat进行开发时，这很好，但在生产中通常不需要。

仍 可以使用`System.out`或`System.err`可以通过设置[Context](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)`swallowOutput`属性 来欺骗老应用程序。如果该属性设置为 ，则将截取在请求处理期间对的调用，并将使用其调用将其输出馈送到日志子系统 。**请注意**，该 功能实际上是一个技巧，它有其局限性。它仅适用于直接调用`true``System.out/err``javax.servlet.ServletContext.log(...)`
`swallowOutput``System.out/err`，并且仅在请求处理周期内。它可能无法在应用程序可能创建的其他线程中工作。它不能用来拦截本身写入系统流的日志记录框架，因为它们早就开始并且可以在重定向发生之前获得对流的直接引用。

#### 访问日志

访问日志记录是一个相关但不同的功能，该功能实现为`Valve`。它使用独立的逻辑来写入其日志文件。访问日志记录的基本要求是以低开销处理大量连续的数据流，因此它仅对自己的调试消息使用Apache Commons Logging。这种实施方法避免了额外的开销和潜在的复杂配置。请参阅[阀门](http://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Access_Logging) 文档以获取有关其配置的更多详细信息，包括各种报告格式。

### 使用java.util.logging（默认）

JDK中提供的java.util.logging的默认实现太受限制而无法使用。关键限制是无法进行每个Web应用程序日志记录，因为该配置是基于每个VM的。结果，Tomcat将以默认配置用名为JULI的容器友好实现替换默认LogManager实现，从而解决了这些缺点。

JULI支持`java.util.logging`使用编程方法或属性文件与标准JDK相同的配置机制 。主要区别在于可以设置每个类加载器的属性文件（这使得可以轻松地重新部署友好的Webapp配置），并且属性文件支持扩展的结构，从而为定义处理程序并将其分配给记录程序提供了更大的自由度。

JULI默认情况下处于启用状态，除了常规的全局java.util.logging配置外，还支持每个类加载器配置。这意味着可以在以下几层上配置日志记录：

- 在全球范围内。这通常是在`${catalina.base}/conf/logging.properties`文件中完成的 。该文件`java.util.logging.config.file` 由启动脚本设置的System属性指定。如果它不可读或未配置，则默认为使用`${java.home}/lib/logging.properties`JRE中的 文件。
- 在Web应用程序中。该文件将是 `WEB-INF/classes/logging.properties`

`logging.properties`JRE中 的默认值指定一个 `ConsoleHandler`将日志记录路由到System.err的路径。`conf/logging.properties`Apache Tomcat中的默认值还会添加多个`AsyncFileHandler`写入文件的。

处理程序的日志级别阈值是`INFO`通过默认设置，可以使用设置 `SEVERE`，`WARNING`，`INFO`，`CONFIG`， `FINE`，`FINER`，`FINEST`或`ALL`。您还可以定位特定的软件包以从中收集日志并指定级别。

要为Tomcat内部的一部分启用调试日志记录，应同时配置适当的记录器和适当的处理程序以使用`FINEST`或`ALL`级别。例如：

```
org.apache.catalina.session.level=ALL
java.util.logging.ConsoleHandler.level=ALL
```

启用调试日志记录时，建议在尽可能小的范围内启用它，因为调试日志记录会生成大量信息。

JULI使用的配置与plain支持的配置相同 `java.util.logging`，但是使用了一些扩展名以允许更好地灵活配置记录器和处理程序。主要区别在于：

- 可以在处理程序名称中添加前缀，以便可以实例化单个类的多个处理程序。前缀是一个字符串，以数字开头，以“。”结尾。例如，`22foobar.`是一个有效的前缀。
- 系统属性替换将对包含的属性值执行`${systemPropertyName}`。
- 如果使用实现`org.apache.juli.WebappProperties`接口的类加载器 （Tomcat的Web应用程序类加载器执行），则还将对进行属性替换`${classloader.webappName}`， `${classloader.hostName}`并 `${classloader.serviceName}`分别用Web应用程序名称，主机名和服务名替换。
- 默认情况下，如果记录器具有关联的处理程序，则它们不会委派给其父级。每个记录器都可以使用`loggerName.useParentHandlers`属性来更改此 属性，该属性接受布尔值。
- 根记录器可以使用`.handlers`属性定义其处理程序集 。
- 默认情况下，日志文件将在文件系统上保留 `90`几天。可以使用该`handlerName.maxDays`属性针对每个处理程序进行更改 。如果该属性的指定值为，`≤0`则日志文件将永远保存在文件系统上，否则将保留指定的最长天数。

还有几个其他的实现类，可以与Java提供的实现类一起使用。值得注意的是 `org.apache.juli.FileHandler`和`org.apache.juli.AsyncFileHandler`。

`org.apache.juli.FileHandler`支持日志的缓冲。默认情况下不启用缓冲。要配置它，请使用`bufferSize`处理程序的 属性。`0` 使用系统默认缓冲的值（通常将使用8K缓冲）。`<0`写入器在每次写入日志时强制刷新的值。值`>0`使用带有已定义值的BufferedOutputStream，但请注意，还将应用系统默认缓冲。

`org.apache.juli.AsyncFileHandler`是子类，`FileHandler` 它使日志消息排队并将它们异步写入日志文件。可以通过设置一些[系统属性](http://tomcat.apache.org/tomcat-9.0-doc/config/systemprops.html#Logging)来配置它的其他行为 。

放置在$ CATALINA_BASE / conf中的示例logging.properties文件：

```properties
handlers = 1catalina.org.apache.juli.FileHandler, \
           2localhost.org.apache.juli.FileHandler, \
           3manager.org.apache.juli.FileHandler, \
           java.util.logging.ConsoleHandler

.handlers = 1catalina.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler

############################################################
# Handler specific properties.
# Describes specific configuration info for Handlers.
############################################################

1catalina.org.apache.juli.FileHandler.level = FINE
1catalina.org.apache.juli.FileHandler.directory = ${catalina.base}/logs
1catalina.org.apache.juli.FileHandler.prefix = catalina.
1catalina.org.apache.juli.FileHandler.maxDays = 90
1catalina.org.apache.juli.FileHandler.encoding = UTF-8

2localhost.org.apache.juli.FileHandler.level = FINE
2localhost.org.apache.juli.FileHandler.directory = ${catalina.base}/logs
2localhost.org.apache.juli.FileHandler.prefix = localhost.
2localhost.org.apache.juli.FileHandler.maxDays = 90
2localhost.org.apache.juli.FileHandler.encoding = UTF-8

3manager.org.apache.juli.FileHandler.level = FINE
3manager.org.apache.juli.FileHandler.directory = ${catalina.base}/logs
3manager.org.apache.juli.FileHandler.prefix = manager.
3manager.org.apache.juli.FileHandler.bufferSize = 16384
3manager.org.apache.juli.FileHandler.maxDays = 90
3manager.org.apache.juli.FileHandler.encoding = UTF-8

java.util.logging.ConsoleHandler.level = FINE
java.util.logging.ConsoleHandler.formatter = java.util.logging.OneLineFormatter
java.util.logging.ConsoleHandler.encoding = UTF-8

############################################################
# Facility specific properties.
# Provides extra control for each logger.
############################################################

org.apache.catalina.core.ContainerBase.[Catalina].[localhost].level = INFO
org.apache.catalina.core.ContainerBase.[Catalina].[localhost].handlers = \
   2localhost.org.apache.juli.FileHandler

org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/manager].level = INFO
org.apache.catalina.core.ContainerBase.[Catalina].[localhost].[/manager].handlers = \
   3manager.org.apache.juli.FileHandler

# For example, set the org.apache.catalina.util.LifecycleBase logger to log
# each component that extends LifecycleBase changing state:
#org.apache.catalina.util.LifecycleBase.level = FINE
```

servlet-examples Web应用程序的示例logging.properties将放置在Web应用程序内的WEB-INF / classs中：

```properties
handlers = org.apache.juli.FileHandler, java.util.logging.ConsoleHandler

############################################################
# Handler specific properties.
# Describes specific configuration info for Handlers.
############################################################

org.apache.juli.FileHandler.level = FINE
org.apache.juli.FileHandler.directory = ${catalina.base}/logs
org.apache.juli.FileHandler.prefix = ${classloader.webappName}.

java.util.logging.ConsoleHandler.level = FINE
java.util.logging.ConsoleHandler.formatter = java.util.logging.OneLineFormatter
```

#### 文档参考

更多信息请参考以下资源：

- [`org.apache.juli`](http://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/juli/package-summary.html) 软件包的Apache Tomcat Javadoc 。
- [`java.util.logging`](https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html) 软件包的Oracle Java 8 Javadoc 。

#### 生产使用注意事项

您可能需要注意以下几点：

- 考虑`ConsoleHandler`从配置中删除。默认情况下（由于`.handlers`设置），日志记录同时进入a `FileHandler`和a `ConsoleHandler`。后者的输出通常捕获到文件中，例如 `catalina.out`。因此，您最终得到相同消息的两个副本。
- 考虑`FileHandler`为不使用的应用程序删除。例如，用于的那个`host-manager`。
- 默认情况下，处理程序使用系统默认编码来写入日志文件。可以使用`encoding`属性进行配置。有关详细信息，请参见Javadoc。
- 考虑配置 [访问日志](http://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Access_Logging)。