# 8.7 Application上下文和资源路径

### 8.7.1构造应用程序上下文

应用程序上下文构造函数（对于特定的应用程序上下文类型）通常将字符串或字符串数组作为资源的位置路径（例如构成上下文定义的XML文件）。

当这样的位置路径没有前缀时，`Resource`从该路径构建并用于加载bean定义的特定类型取决于并且适合于特定的应用程序上下文。例如，如果您创建`ClassPathXmlApplicationContext`如下：

```java
ApplicationContext ctx = new ClassPathXmlApplicationContext("conf/appContext.xml");
```

bean定义将从类路径加载，`ClassPathResource`将被使用。但是如果你创建`FileSystemXmlApplicationContext`如下：

```java
ApplicationContext ctx =
    new FileSystemXmlApplicationContext("conf/appContext.xml");
```

bean定义将从文件系统位置加载，在这种情况下相对于当前工作目录。

请注意，在位置路径上使用特殊类路径前缀或标准URL前缀将覆盖`Resource`为加载定义而创建的默认类型。所以`FileSystemXmlApplicationContext`......

```java
ApplicationContext ctx =
    new FileSystemXmlApplicationContext("classpath:conf/appContext.xml");
```

1. 实际上将从类路径加载其bean定义。但是，它仍然是一个 `FileSystemXmlApplicationContext`。如果随后将其用作a `ResourceLoader`，则任何未加前缀的路径仍将被视为文件系统路径。

#### 构造ClassPathXmlApplicationContext实例 - 快捷方式

在`ClassPathXmlApplicationContext`提供了多种构造方法以便于实例。基本思想是，只提供一个字符串数组，只包含XML文件本身的文件名（没有前导路径信息），*还*提供一个`Class`; 在`ClassPathXmlApplicationContext` 将从给定类的路径信息。

一个例子有望清楚地表明这一点。考虑一个如下所示的目录布局：

```bash
com/
  foo/
    services.xml
    daos.xml
    MessengerService.class
```

一个`ClassPathXmlApplicationContext`在规定的豆组成的实例 `'services.xml'`，并`'daos.xml'`可以被实例化是这样的...

```java
ApplicationContext ctx = new ClassPathXmlApplicationContext(
    new String[] {"services.xml", "daos.xml"}, MessengerService.class);
```

### 8.7.2应用程序上下文构造函数资源路径中的通配符

应用程序上下文构造函数值中的资源路径可以是一个简单的路径（如上所示），它具有与目标资源的一对一映射，或者可以包含特殊的“classpath *：”前缀和/或内部Ant-样式正则表达式（使用Spring的`PathMatcher`实用程序匹配）。后者都是有效的通配符

此机制的一个用途是进行组件样式的应用程序组装。所有组件都可以将上下文定义片段“发布”到一个众所周知的位置路径，并且当使用前缀为via的相同路径创建最终应用程序上下文时 `classpath*:`，将自动拾取所有组件片段。

请注意，此通配符特定于在应用程序上下文构造函数中使用资源路径（或`PathMatcher`直接使用实用程序类层次结构时），并在构造时解析。它与`Resource`类型本身无关。不可能使用`classpath*:`前缀来构造实际`Resource`的资源，因为资源一次只指向一个资源。

#### Ant-style

当路径位置包含Ant样式模式时，例如：

```bash
/WEB-INF/*-context.xml
  com/mycompany/**/applicationContext.xml
  file:C:/some/path/*-context.xml
  classpath:com/mycompany/**/applicationContext.xml
```

解析器遵循更复杂但定义的过程来尝试解析通配符。它为直到最后一个非通配符段的路径生成一个Resource，并从中获取一个URL。如果此URL不是`jar:`URL或特定`zip:`于容器的变体（例如，在WebLogic中，`wsjar`在WebSphere中等），则`java.io.File`从中获取a 并用于通过遍历文件系统来解析通配符。对于jar URL，解析器要么从中获取`java.net.JarURLConnection`，要么手动解析jar URL，然后遍历jar文件的内容以解析通配符。

##### 对可移植性的影响

如果指定的路径已经是文件URL（显式或隐式，因为基础`ResourceLoader`是文件系统），那么通配符保证以完全可移植的方式工作。

如果指定的路径是类路径位置，则解析程序必须通过`Classloader.getResource()`调用获取最后一个非通配符路径段URL 。由于这只是路径的一个节点（不是最后的文件），因此`ClassLoader`在这种情况下，实际上未定义（在 javadocs中）确切地返回了什么类型的URL。实际上，它始终`java.io.File`表示目录，类路径资源解析为文件系统位置，或某种类型的jar URL，其中类路径资源解析为jar位置。尽管如此，这项行动仍存在可移植性问题。

如果为最后一个非通配符段获取了jar URL，则解析器必须能够从中获取`java.net.JarURLConnection`，或者手动解析jar URL，以便能够遍历jar的内容，并解析通配符。这适用于大多数环境，但在其他环境中会失败，强烈建议在依赖它之前，在特定环境中对来自jar的资源的通配符解析进行全面测试。

#### classpath *：前缀

构建基于XML的应用程序上下文时，位置字符串可以使用特殊`classpath*:`前缀：

```java
ApplicationContext ctx =
    new ClassPathXmlApplicationContext("classpath*:conf/appContext.xml");
```

此特殊前缀指定必须获取与给定名称匹配的所有类路径资源（内部，这通常通过`ClassLoader.getResources(…)`调用发生 ），然后合并以形成最终的应用程序上下文定义。

通配符类路径依赖于`getResources()`底层类加载器的方法。由于现在大多数应用程序服务器都提供了自己的类加载器实现，因此行为可能会有所不同，尤其是在处理jar文件时。检查是否`classpath*`有效的简单测试是使用类加载器从类路径中的jar中加载文件：`getClass().getClassLoader().getResources("<someFileInsideTheJar>")`。尝试使用具有相同名称但放在两个不同位置的文件进行此测试。如果返回了不适当的结果，请检查应用程序服务器文档以获取可能影响类加载器行为的设置。

例如，`classpath*:`前缀也可以与`PathMatcher`位置路径的其余部分中的模式组合`classpath*:META-INF/*-beans.xml`。在这种情况下，解析策略非常简单：`ClassLoader.getResources()`在最后一个非通配符路径段上使用调用来获取类加载器层次结构中的所有匹配资源，然后关闭每个资源，使用上述相同的PathMatcher解析策略通配符子路径。

#### 与通配符有关的其他说明

请注意`classpath*:`，除非实际目标文件驻留在文件系统中，否则与Ant样式组合使用时，只能在模式启动前与至少一个根目录可靠地工作。这意味着类似的模式`classpath*:*.xml`可能无法从jar文件的根目录中检索文件，而只能从扩展目录的根目录中检索文件。

Spring检索类路径条目的能力来自JDK的 `ClassLoader.getResources()`方法，该方法仅返回传入的空字符串的文件系统位置（指示搜索的潜在根）。Spring评估`URLClassLoader`运行时配置和jar文件中的“java.class.path”清单，但不保证这会导致可移植行为。

扫描类路径包需要在类路径中存在相应的目录条目。使用Ant构建JAR时，请确保*不要* 激活JAR任务的仅文件开关。此外，在某些环境中，类路径目录可能不会基于安全策略公开，例如JDK 1.7.0_45及更高版本上的独立应用程序

`classpath:`如果要搜索的根包在多个类路径位置中可用，则不保证具有资源的Ant样式模式可以找到匹配的资源。这是因为资源如

```java
com/mycompany/package1/service-context.xml
```

可能只在一个位置，但是当一个路径如

```java
classpath:com/mycompany/**/service-context.xml
```

用于尝试解决它，解析器将处理由`getResource("com/mycompany")`; 返回的（第一个）URL 。如果此基本包节点存在于多个类加载器位置中，则实际的最终资源可能不在下面。因此，最好在这种情况下使用具有相同Ant样式模式的“`classpath *：`”，它将搜索包含根包的所有类路径位置。

### 8.7.3 FileSystemResource警告

一个`FileSystemResource`未连接到`FileSystemApplicationContext`（即`FileSystemApplicationContext`不实际的`ResourceLoader`）将把绝对和相对路径如你所愿。相对路径相对于当前工作目录，而绝对路径相对于文件系统的根目录。

为了向后兼容（历史）的原因然而，这改变时 `FileSystemApplicationContext`是`ResourceLoader`。在 `FileSystemApplicationContext`简单地让所有绑定的`FileSystemResource`情况下，把所有的位置路径为相对的，他们是否开始与斜线与否。实际上，这意味着以下内容是等效的：

```java
ApplicationContext ctx =
    new FileSystemXmlApplicationContext("conf/context.xml");
```

```java
ApplicationContext ctx =
    new FileSystemXmlApplicationContext("/conf/context.xml");
```

如下所示:(即使它们是不同的，因为一个案例是相对的而另一个是绝对的。）

```java
FileSystemXmlApplicationContext ctx = ...;
ctx.getResource("some/resource/path/myTemplate.txt");
```

```java
FileSystemXmlApplicationContext ctx = ...;
ctx.getResource("/some/resource/path/myTemplate.txt");
```

实际上，如果需要真正的绝对文件系统路径，最好放弃使用`FileSystemResource`/ 的绝对路径`FileSystemXmlApplicationContext`，并且只`UrlResource`使用`file:`URL前缀强制使用a 。

```java
// actual context type doesn't matter, the Resource will always be UrlResource
ctx.getResource("file:///some/resource/path/myTemplate.txt");
```

```java
// force this FileSystemXmlApplicationContext to load its definition via a UrlResource
ApplicationContext ctx =
    new FileSystemXmlApplicationContext("file:///conf/context.xml");
```