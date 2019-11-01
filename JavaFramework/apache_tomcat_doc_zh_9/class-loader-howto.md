# 11.类加载

### 总览

与许多服务器应用程序一样，Tomcat安装了各种类装入器（即实现的类`java.lang.ClassLoader`），以允许容器的不同部分以及容器上运行的Web应用程序可以访问可用类和资源的不同存储库。该机制用于提供Servlet规范2.4版中定义的功能-特别是9.4和9.6节。

在Java环境中，类加载器排列在父子树中。通常，当要求类加载器加载特定的类或资源时，它首先将请求委派给父类加载器，然后仅在父类加载器找不到所请求的类或资源时才在其自己的存储库中查找。请注意，Web应用程序类加载器的模型*与*此略有*不同*，如下所述，但是主要原理是相同的。

启动Tomcat时，它将创建一组类加载器，这些类加载器被组织为以下父子关系，其中父类加载器位于子类加载器之上：

```
      Bootstrap
          |
       System
          |
       Common
       /     \
  Webapp1   Webapp2 ...
```

下一节将详细讨论这些类加载器的每个特征，包括它们的类源和可见的资源。

### 类加载器定义

如上图所示，Tomcat在初始化时会创建以下类加载器：

- **引导程序** -该类加载器包含Java虚拟机提供的基本运行时类，以及系统扩展目录（`$JAVA_HOME/jre/lib/ext`）中存在的JAR文件中的所有类。 *注意*：某些JVM可能将其实现为多个类加载器，或者根本不可见（作为类加载器）。

- **系统** -通常从`CLASSPATH`环境变量的内容初始化该类加载器。所有这些类对于Tomcat内部类和Web应用程序都是可见的。但是，标准Tomcat启动脚本（`$CATALINA_HOME/bin/catalina.sh`或 `%CATALINA_HOME%\bin\catalina.bat`）完全忽略`CLASSPATH`环境变量本身的内容，而是从以下存储库构建System类加载器：

  - *$ CATALINA_HOME / bin / bootstrap.jar* —包含用于初始化Tomcat服务器的main（）方法，以及它依赖的类加载器实现类。

  - *$ CATALINA_BASE / bin / tomcat-juli.jar*或 *$ CATALINA_HOME / bin / tomcat-juli.jar* —记录实现类。其中包括对`java.util.logging`API的增强类 ，称为Tomcat JULI，以及由Tomcat内部使用的Apache Commons Logging库的程序包重命名副本。有关更多详细信息，请参见[日志记录文档](http://tomcat.apache.org/tomcat-9.0-doc/logging.html)。

    如果`tomcat-juli.jar`是出现在 *$ CATALINA_BASE / bin中*，它被用来代替一个在 *$ CATALINA_HOME / bin中*。在某些日志记录配置中很有用

  - *$ CATALINA_HOME / bin / commons-daemon.jar* — [Apache Commons Daemon](https://commons.apache.org/daemon/)项目中的类。此JAR文件在| `CLASSPATH`创建者 `catalina.bat`|中不存在。`.sh`脚本，但从*bootstrap.jar*的清单文件引用。

- **通用** -该类加载器包含对Tomcat内部类和所有Web应用程序都可见的其他类。

  通常情况下，应用类应该**不** 放在这里。此类加载器搜索的位置由`common.loader`$ CATALINA_BASE / conf / catalina.properties中的属性定义。默认设置将按列出的顺序搜索以下位置：

  - 解压后的类和资源 `$CATALINA_BASE/lib`
  - JAR文件 `$CATALINA_BASE/lib`
  - 解压后的类和资源 `$CATALINA_HOME/lib`
  - JAR文件 `$CATALINA_HOME/lib`

  默认情况下，这包括以下内容：

  - *注解-api.jar* — JavaEE注解类。
  - *catalina.jar* — Tomcat的Catalina servlet容器部分的实现。
  - *catalina-ant.jar* — Tomcat Catalina Ant任务。
  - *catalina-ha.jar* —高可用性软件包。
  - *catalina-storeconfig.jar* —从当前状态生成XML配置文件
  - *catalina-tribes.jar* —组通信程序包。
  - *ecj-\*。jar* — Eclipse JDT Java编译器。
  - *el-api.jar* — EL 3.0 API。
  - *jasper.jar* — Tomcat Jasper JSP编译器和运行时。
  - *jasper-el.jar* — Tomcat Jasper EL实现。
  - *jsp-api.jar* — JSP 2.3 API。
  - *servlet-api.jar* — Servlet 4.0 API。
  - *tomcat-api.jar* — Tomcat定义的几个接口。
  - *tomcat-coyote.jar* -Tomcat连接器和实用程序类。
  - *tomcat-dbcp.jar* —基于程序包重命名的Apache Commons Pool 2和Apache Commons DBCP 2的数据库连接池实现。
  - *tomcat-i18n-**。jar* —包含其他语言资源包的可选JAR。由于默认捆绑包还包含在每个单独的JAR中，因此如果不需要消息的国际化，可以安全地删除它们。
  - *tomcat-jdbc.jar* —一种替代的数据库连接池实现，称为Tomcat JDBC池。有关更多详细信息，请参见 [文档](http://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html)。
  - *tomcat-util.jar* -Apache Tomcat的各种组件使用的通用类。
  - *tomcat-websocket.jar* — WebSocket 1.1的实现
  - *websocket-api.jar* — WebSocket 1.1 API

- **WebappX** —为部署在单个Tomcat实例中的每个Web应用程序创建一个类加载器。`/WEB-INF/classes`Web应用程序目录中的所有解压缩类和资源，以及Web应用程序`/WEB-INF/lib`目录下的JAR文件中的类和资源，对于此Web应用程序均可见，但对其他Web应用程序不可见。

如上所述，Web应用程序类加载器与默认的Java委托模型有所不同（根据Servlet规范2.4版，第9.7.2节Web应用程序类加载器中的建议）。处理从Web应用程序的*WebappX*类加载器加载类的请求时，该类加载器将**首先**在本地存储库中查找，而不是在看前委派。也有例外。属于JRE基类的类不能被覆盖。有一些例外，例如可以使用适当的JVM功能覆盖XML解析器组件，JVM功能是Java <= 8的认可标准重写功能，而Java 9+是可升级的模块功能。最后，对于由Tomcat（Servlet，JSP，EL，WebSocket）实现的规范，Web应用程序类加载器将始终首先委托JavaEE API类。Tomcat中的所有其他类装入器都遵循通常的委托模式。

因此，从Web应用程序的角度来看，类或资源的加载按以下顺序查找以下存储库：

- JVM的Bootstrap类
- */ WEB-INF /*您的网络应用程序*类*
- Web应用程序的*/WEB-INF/lib/\*.jar*
- 系统类加载器类（如上所述）
- 通用类加载器类（如上所述）

如果使用[配置](http://tomcat.apache.org/tomcat-9.0-doc/config/loader.html)了Web应用程序类加载器 ， `<Loader delegate="true"/>` 那么顺序将变为：

- JVM的Bootstrap类
- 系统类加载器类（如上所述）
- 通用类加载器类（如上所述）
- */ WEB-INF /*您的网络应用程序*类*
- Web应用程序的*/WEB-INF/lib/\*.jar*

### XML解析器和Java

从Java 1.4开始，JRE内包装了JAXP API和XML解析器的副本。这会对希望使用自己的XML解析器的应用程序产生影响。

在旧版本的Tomcat中，您可以简单地替换Tomcat库目录中的XML解析器以更改所有Web应用程序使用的解析器。但是，当您运行Java的现代版本时，此技术将无效，因为通常的类加载器委托过程将始终在JDK内部选择实现，而不是选择该实现。

Java <= 8支持称为“认可标准覆盖机制”的机制，以允许替换在JCP外部创建的API（即，来自W3C的DOM和SAX）。它还可以用于更新XML解析器实现。有关更多信息，请参见：[ http](http://docs.oracle.com/javase/1.5.0/docs/guide/standards/index.html) : [//docs.oracle.com/javase/1.5.0/docs/guide/standards/index.html](http://docs.oracle.com/javase/1.5.0/docs/guide/standards/index.html)。对于Java 9+，请使用可升级模块功能。

Tomcat通过`-Djava.endorsed.dirs=$JAVA_ENDORSED_DIRS`在启动容器的命令行中包括系统属性设置来利用认可的机制 。此选项的默认值为 *$ CATALINA_HOME / endorsed*。默认情况下，不创建该*认可*目录。请注意，Java 9不再支持已认可的功能，并且仅在目录*$ CATALINA_HOME / endorsed*存在或`JAVA_ENDORSED_DIRS`已设置变量的情况下， 才设置上述系统属性。

请注意，覆盖任何JRE组件都会带来风险。如果覆盖的组件不提供100％兼容的API（例如Xerces提供的API与JRE提供的XML API不100％兼容），则存在Tomcat和/或已部署的应用程序会遇到错误的风险。

### 在安全管理器下运行

在安全管理器下运行时，允许加载类的位置也将取决于策略文件的内容。有关 更多信息，请参见[Security Manager How-To](http://tomcat.apache.org/tomcat-9.0-doc/security-manager-howto.html)。

### 进阶设定

也可以配置更复杂的类加载器层次结构。请参见下图。默认情况下， 未定义**服务器**和**共享**类加载器，并使用上面显示的简化层次结构。通过为中的`server.loader`和/或`shared.loader`属性 定义值，可以使用这种更复杂的层次结构 `conf/catalina.properties`。

```
  Bootstrap
      |
    System
      |
    Common
     /  \
Server  Shared
         /  \
   Webapp1  Webapp2 ...
```

该**服务器**类加载器是唯一到Tomcat内部可见，并且是Web应用程序完全不可见。

所述**共享**类加载器是将所有的web应用程序可见，并且可以在所有的web应用程序被用来共享代码。但是，对此共享代码的任何更新将需要重新启动Tomcat。