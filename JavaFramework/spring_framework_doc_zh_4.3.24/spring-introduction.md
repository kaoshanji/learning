# 第一部分：spring framework 综述

## 概述

Spring Framework是一个轻量级的解决方案，也是构建企业级应用程序的潜在一站式服务。但是，Spring是模块化的，允许您只使用您需要的那些部件，而无需引入其余部分。您可以使用IoC容器，顶部有任何Web框架，但您也可以只使用 Hibernate集成代码或[JDBC抽象层](spring-data/jdbc-jdbc-introduction.md)。Spring Framework支持声明式事务管理，通过RMI或Web服务远程访问您的逻辑，以及用于持久保存数据的各种选项。它提供了全功能的[MVC框架](spring-web/mvc-mvc-introduction.md)，使您能够将[AOP](spring-core/aop-aop-introduction.md)透明地集成到您的软件中。

Spring被设计为非侵入式的，这意味着您的域逻辑代码通常不依赖于框架本身。在集成层（例如数据访问层）中，将存在对数据访问技术和Spring库的一些依赖性。但是，应该很容易将这些依赖项与其余代码库隔离开来。

----

## Spring入门

本参考指南提供有关Spring Framework的详细信息。它提供了所有功能的全面文档，以及Spring已经接受的基础概念（例如“依赖注入”）的一些背景知识。

如果您刚刚开始使用Spring，您可能希望通过创建基于[Spring Boot](https://spring.io/projects/spring-boot)的应用程序来开始使用Spring Framework 。Spring Boot提供了一种快速的方式来创建一个生产就绪的基于Spring的应用程序。它基于Spring Framework，支持约定优于配置，旨在帮助您尽快启动和运行。

----

##	Spring框架简介

Spring Framework是一个Java平台，为开发Java应用程序提供全面的基础架构支持。Spring处理基础架构，因此您可以专注于您的应用程序。

Spring允许您从“普通旧Java对象”（POJO）构建应用程序，并以非侵入方式将企业服务应用于POJO。此功能适用于Java SE编程模型以及完整和部分Java EE。

作为应用程序开发人员，您可以从Spring平台中受益的示例如下：

- 使Java方法在数据库事务中执行，而不必处理事务API。
- 使本地Java方法成为HTTP端点，而无需处理Servlet API。
- 使本地Java方法成为消息处理程序，而无需处理JMS API。
- 使本地Java方法成为管理操作，而无需处理JMX API。



##	2.1依赖注入和控制反转

Java应用程序 - 从受约束的嵌入式应用程序到n层服务器端企业应用程序的宽松术语 - 通常由协作形成应用程序的对象组成。因此，应用程序中的对象彼此*依赖*。

尽管Java平台提供了丰富的应用程序开发功能，但它缺乏将基本构建块组织成一个整体的方法，将该任务留给架构师和开发人员。虽然您可以使用诸如*Factory*，*Abstract Factory*，*Builder*，*Decorator*和*Service Locator* 之类的设计模式来组成构成应用程序的各种类和对象实例，但这些模式只是：给出名称的最佳实践，描述为模式的作用，应用位置，解决的问题等等。

Spring框架*控制反转*（IoC）组件通过提供一种将不同组件组合成一个可以使用的完全工作的应用程序的形式化方法来解决这一问题。Spring Framework将形式化的设计模式编码为可以集成到您自己的应用程序中的第一类对象。许多组织和机构以这种方式使用Spring Framework来设计健壮，可*维护的*应用程序。



##	2.2框架模块

Spring Framework由大约20个模块组成的功能组成。这些模块分为核心容器，数据访问/集成，Web，AOP（面向方面编程），仪器，消息传递和测试，如下图所示。

**图2.1 Spring框架概述**

![spring-overview](images/spring-overview.png)

以下部分列出了每个功能的可用模块及其工件名称及其涵盖的主题。工件名称与依赖关系管理工具中使用的*工件ID*相关联。



### 2.2.1核心容器

所述[*核心容器*](spring-core/beans-introduction.md)由以下部分组成`spring-core`， `spring-beans`，`spring-context`，`spring-context-support`，和`spring-expression` （spring表达式语言）模块。

`spring-core`和`spring-beans`模块[提供框架的基本零件](spring-core/beans-introduction.md)，包括IOC和依赖注入特征。这 `BeanFactory`是工厂模式的复杂实现。它消除了对编程单例的需求，并允许您从实际的程序逻辑中分离出依赖关系的配置和规范。

所述[*上下文*](spring-core/beans-context-introduction.md)（`spring-context`）模块建立由设置在基体上[`spring-core`和`spring-beans`](spring-core/beans-introduction.md)模块：它是访问一个框架式的方式是类似于一个JNDI注册表对象的装置。Context模块从Beans模块继承其功能，并添加对国际化（例如，使用资源包），事件传播，资源加载以及通过例如Servlet容器透明创建上下文的支持。Context模块还支持Java EE功能，例如EJB，JMX和基本远程处理。该`ApplicationContext`接口是语境模块的焦点。 `spring-context-support`支持将常见的第三方库集成到Spring应用程序上下文中，用于缓存（EhCache，Guava，JCache），邮件（JavaMail），调度（CommonJ，Quartz）和模板引擎（FreeMarker，JasperReports，Velocity）。

该`spring-expression`模块提供了一种功能强大的*表达式语言，*用于在运行时查询和操作对象图。它是JSP 2.1规范中指定的统一表达式语言（统一EL）的扩展。该语言支持设置和获取属性值，属性赋值，方法调用，访问数组，集合和索引器的内容，逻辑和算术运算符，命名变量以及从Spring的IoC容器中按名称检索对象。它还支持列表投影和选择以及常用列表聚合。



### 2.2.2 AOP和仪表

该`spring-aop`模块提供了一个符合[*AOP*](spring-core/aop-aop-introduction.md) Alliance标准的面向方面的编程实现，允许您定义，例如，方法拦截器和切入点，以干净地解耦实现应该分离的功能的代码。使用源级元数据功能，您还可以以类似于.NET属性的方式将行为信息合并到代码中。

单独的`spring-aspects`模块提供与AspectJ的集成。

该`spring-instrument`模块提供了在某些应用程序服务器中使用的类检测支持和类加载器实现。该`spring-instrument-tomcat` 模块包含Spring的Tomcat检测代理。



### 2.2.3消息传递

Spring框架4包括`spring-messaging`从关键抽象模块 *Spring集成*项目，例如`Message`，`MessageChannel`，`MessageHandler`，和其他人作为基于消息的应用奠定了基础。该模块还包括一组用于将消息映射到方法的注释，类似于基于Spring MVC注释的编程模型。



### 2.2.4数据访问/集成

所述*数据访问/集成*层由JDBC，ORM，OXM，JMS和交易模块。

该`spring-jdbc`模块提供了一个[JDBC](spring-data/jdbc-jdbc-introduction.md) -abstraction层，无需进行繁琐的JDBC编码和解析数据库供应商特定的错误代码。

该`spring-tx`模块支持 对实现特殊接口的类和*所有POJO（普通旧Java对象）的*类进行[编程和声明式事务](spring-data/transaction-transaction-intro.md)管理。

该`spring-orm`模块为流行的对象关系映射 API 提供了集成层 ，包括JPA， JDO和Hibernate。使用该`spring-orm`模块，您可以将所有这些O / R映射框架与Spring提供的所有其他功能结合使用，例如前面提到的简单声明式事务管理功能。

该`spring-oxm`模块提供了一个抽象层，支持对象/ XML映射实现，如JAXB，Castor，XMLBeans，JiBX和XStream。

所述`spring-jms`模块（Java消息服务）包含用于生成和使用消息的功能。从Spring Framework 4.1开始，它提供了与`spring-messaging`模块的集成 。



### 2.2.5 Web

所述*网络*层由的`spring-web`，`spring-webmvc`，`spring-websocket`，和 `spring-webmvc-portlet`模块。

该`spring-web`模块提供基本的面向Web的集成功能，例如多部分文件上载功能以及使用Servlet侦听器和面向Web的应用程序上下文初始化IoC容器。它还包含一个HTTP客户端以及Spring的远程支持的Web相关部分。

该`spring-webmvc`模块（也称为*Web-Servlet*模块）包含用于Web应用程序的Spring的模型 - 视图 - 控制器（[*MVC*](spring-web/mvc-mvc-introduction.md)）和REST Web服务实现。Spring的MVC框架提供了域模型代码和Web表单之间的清晰分离，并与Spring Framework的所有其他功能集成在一起。

该`spring-webmvc-portlet`模块（也称为*Web-Portlet*模块）提供了在Portlet环境中使用的MVC实现，并镜像了基于Servlet的`spring-webmvc`模块的功能。



### 2.2.6测试

该`spring-test`模块支持使用JUnit或TestNG对Spring组件进行单元测试和集成测试。它提供了Spring 的一致加载`ApplicationContext`和这些上下文的缓存。它还提供了可用于独立测试代码的模拟对象。



## 2.3使用场景

前面描述的构建块使Spring成为许多场景中的合理选择，从在资源受限设备上运行的嵌入式应用程序到使用Spring的事务管理功能和Web框架集成的成熟企业应用程序。

**图2.2。典型的完整Spring Web应用程序**

![overview-full](images/overview-full.png)

Spring的[声明式事务管理功能](spring-data/transaction-transaction-declarative.md)使Web应用程序完全是事务性的，就像使用EJB容器管理的事务一样。您可以使用简单的POJO实现所有自定义业务逻辑，并由Spring的IoC容器管理。其他服务包括支持发送独立于Web层的电子邮件和验证，这使您可以选择执行验证规则的位置。Spring的ORM支持与JPA，Hibernate和JDO集成在一起; 例如，在使用Hibernate时，您可以继续使用现有的映射文件和标准的Hibernate `SessionFactory`配置。表单控制器将Web层与域模型无缝集成，无需使用`ActionForms` 或其他将HTTP参数转换为域模型值的类。



### 2.3.1依赖管理和命名约定

依赖管理和依赖注入是不同的事情。要将Spring的这些优秀功能集成到您的应用程序中（比如依赖注入），您需要组装所需的所有库（jar文件）并在运行时将它们放到类路径中，并且可能在编译时。这些依赖项不是注入的虚拟组件，而是文件系统中的物理资源（通常）。依赖关系管理的过程涉及定位这些资源，存储它们并将它们添加到类路径中。依赖关系可以是直接的（例如我的应用程序在运行时依赖于Spring），也可以是间接的（例如，我的应用程序依赖于依赖于`commons-dbcp`它`commons-pool`）。间接依赖性也称为“传递性”，并且最难识别和管理的是那些依赖性。

如果你打算使用Spring，你需要获得一个包含你需要的Spring部分的jar库的副本。为了使这更容易，Spring被打包为一组模块，尽可能地分离依赖项，因此，例如，如果您不想编写Web应用程序，则不需要spring-web模块。要参照本指南中，我们使用速记命名约定到Spring库模块`spring-*`或 `spring-*.jar,`其中`*`代表该模块的短名称（例如`spring-core`，`spring-webmvc`，`spring-jms`等）。您使用的实际jar文件名通常是与版本号连接的模块名称（例如*spring-core-4.3.24.RELEASE.jar*）.



**表2.1 Spring Framework工件**

| GroupId           | artifactId             | 描述                                                      |
| ------------------- | ------------------------ | --------------------------------------------------------- |
| org.springframework | spring-aop               | 基于代理的AOP支持                                         |
| org.springframework | spring-aspects           | AspectJ基于方面                                           |
| org.springframework | spring-beans             | Bean支持，包括Groovy                                      |
| org.springframework | spring-context           | 应用程序上下文运行时，包括调度和远程抽象                  |
| org.springframework | spring-context-support   | 支持将常见的第三方库集成到Spring应用程序上下文中的类      |
| org.springframework | spring-core              | 核心实用程序，许多其他Spring模块使用                      |
| org.springframework | spring-expression        | Spring表达语言（SpEL）                                    |
| org.springframework | spring-instrument        | 用于JVM引导的检测代理程序                                 |
| org.springframework | spring-instrument-tomcat | Tomcat的Instrumentation代理                               |
| org.springframework | spring-jdbc              | JDBC支持包，包括DataSource设置和JDBC访问支持              |
| org.springframework | spring-jms               | JMS支持包，包括用于发送/接收JMS消息的帮助程序类           |
| org.springframework | spring-messaging         | 支持消息传递体系结构和协议                                |
| org.springframework | spring-orm               | 对象/关系映射，包括JPA和Hibernate支持                     |
| org.springframework | spring-oxm               | 对象/ XML映射                                             |
| org.springframework | spring-test              | 支持单元测试和集成测试Spring组件                          |
| org.springframework | spring-tx                | 交易基础设施，包括DAO支持和JCA集成                        |
| org.springframework | spring-web               | 基础Web支持，包括Web客户端和基于Web的远程处理             |
| org.springframework | spring-webmvc            | 用于Servlet堆栈的基于HTTP的模型 - 视图 - 控制器和REST端点 |
| org.springframework | spring-webmvc-portlet    | 要在Portlet环境中使用的MVC实现                            |
| org.springframework | spring-websocket         | WebSocket和SockJS基础架构，包括STOMP消息传递支持          |



#### Spring依赖和依赖于Spring

尽管Spring为大量企业和其他外部工具提供集成和支持，但它有意将其强制依赖性保持在最低限度：您不必定位和下载（甚至自动）大量jar库以便将Spring用于简单的用例。对于基本依赖项注入，只有一个强制性外部依赖项，即用于日志记录。

我们概述了配置依赖于Spring的应用程序所需的基本步骤，首先是Maven。



#### Maven依赖管理

如果您使用[Maven](https://maven.apache.org/)进行依赖项管理，则甚至不需要显式提供日志记录依赖项。例如，要创建应用程序上下文并使用依赖项注入来配置应用程序，您的Maven依赖项将如下所示：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>4.3.24.RELEASE</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

而已。请注意，如果您不需要针对Spring API进行编译，则可以将范围声明为运行时，这通常是基本依赖项注入用例的情况。

上面的示例适用于Maven Central存储库。要使用Spring Maven存储库（例如，用于里程碑或开发人员快照），您需要在Maven配置中指定存储库位置。对于完整版本：

```xml
<repositories>
    <repository>
        <id>io.spring.repo.maven.release</id>
        <url>https://repo.spring.io/release/</url>
        <snapshots><enabled>false</enabled></snapshots>
    </repository>
</repositories>
```

对于里程碑：

```xml
<repositories>
    <repository>
        <id>io.spring.repo.maven.milestone</id>
        <url>https://repo.spring.io/milestone/</url>
        <snapshots><enabled>false</enabled></snapshots>
    </repository>
</repositories>
```

对于快照：

```xml
<repositories>
    <repository>
        <id>io.spring.repo.maven.snapshot</id>
        <url>https://repo.spring.io/snapshot/</url>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
```

#### Maven“物料清单”依赖

使用Maven时，可能会意外混合不同版本的Spring JAR。例如，您可能会发现第三方库或另一个Spring项目将旧版本的传递依赖性拉入其中。如果您忘记自己明确声明直接依赖，则可能会出现各种意外问题。

为了克服这些问题，Maven支持“物料清单”（BOM）依赖性的概念。您可以`spring-framework-bom`在您的`dependencyManagement` 部分中导入以确保所有Spring依赖项（直接和传递）都在同一版本。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-framework-bom</artifactId>
            <version>4.3.24.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

使用BOM的另一个好处是，`<version>` 在依赖Spring Framework工件时，您不再需要指定属性：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-web</artifactId>
    </dependency>
<dependencies>
```



### 2.3.2日志

日志记录是Spring的一个非常重要的依赖项，因为*a）*它是唯一强制性的外部依赖项，*b）*每个人都喜欢从他们使用的工具中看到一些输出，并且*c）* Spring集成了许多其他工具，所有这些工具也都是选择日志记录依赖项。应用程序开发人员的目标之一通常是在整个应用程序的中心位置配置统一日志记录，包括所有外部组件。由于有很多日志框架选择，因此这比以往更难。

Spring中的强制日志记录依赖是Jakarta Commons Logging API（JCL）。我们针对JCL进行编译，并且我们还使`Log`扩展Spring Framework的类可以看到JCL 对象。对于用户来说，所有版本的Spring都使用相同的日志库是很重要的：迁移很容易，因为即使扩展Spring的应用程序也可以保留向后兼容性。我们这样做的方法是让Spring中的一个模块明确依赖`commons-logging`（JCL的规范实现），然后让所有其他模块在编译时依赖于它。例如，如果你正在使用Maven，并想知道你在哪里获得依赖`commons-logging`，那么它来自Spring，特别是来自中央模块的调用`spring-core`。

好处`commons-logging`是你不需要任何其他东西来使你的应用程序工作。它有一个运行时发现算法，可以在类路径中的众所周知的位置查找其他日志框架，并使用它认为合适的一个（或者如果需要，可以告诉它哪一个）。如果没有其他可用的东西，你只需从JDK（java.util.logging或简称JUL）获得相当不错的日志。在大多数情况下，您应该会发现Spring应用程序可以正常工作并开箱即用地登录到控制台，这很重要。



#### 将SLF4J与Log4j或Logback一起使用

Simple Logging Facade for Java（[SLF4J](https://www.slf4j.org)）是一种常用的API，供其他常用于Spring的库使用。它通常与[Logback一起](https://logback.qos.ch/)使用， 后者是SLF4J API的本机实现。

SLF4J提供了对许多常见日志框架（包括Log4j）的绑定，它也反过来了：其他日志框架与其自身之间的桥梁。因此，要在Spring中使用SLF4J，您需要`commons-logging`使用SLF4J-JCL桥替换依赖项。完成后，从Spring中记录调用将转换为对SLF4J API的日志记录调用，因此，如果应用程序中的其他库使用该API，那么您只需一个位置即可配置和管理日志记录。



SLF4J用户中使用较少步骤并生成较少依赖关系的更常见选择是直接绑定到[Logback](https://logback.qos.ch)。这消除了额外的绑定步骤，因为Logback直接实现了SLF4J，所以你只需要依赖两个库，即`jcl-over-slf4j`和`logback`）：

```xml
<dependencies>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jcl-over-slf4j</artifactId>
        <version>1.7.21</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.1.7</version>
    </dependency>
</dependencies>
```

