# 11、第一个Spring Boot 应用程序

本节描述如何开发一个简单的“ Hello World！” Web应用程序，该应用程序重点介绍了Spring Boot的一些关键功能。我们使用Maven来构建该项目，因为大多数IDE都支持它。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 该[spring.io](https://spring.io/)网站包含了许多“入门” [指南](https://spring.io/guides)使用Spring的引导。如果您需要解决特定的问题，请首先检查。通过转到[start.spring.io](https://start.spring.io/)并从依赖项搜索器中选择“ Web”启动器，可以简化以下步骤。这样做会生成一个新的项目结构，以便您可以[立即开始编码](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/getting-started-first-application.html#getting-started-first-application-code)。查看[Spring Initializr文档](https://docs.spring.io/initializr/docs/current/reference/html//#user-guide)以获取更多详细信息。 |

在开始之前，请打开终端并运行以下命令，以确保安装了有效的Java和Maven版本：

```
$ java -version
java version "1.8.0_102"
Java(TM) SE Runtime Environment (build 1.8.0_102-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14, mixed mode)

$ mvn -v
Apache Maven 3.5.4 (1edded0938998edf8bf061f1ceb3cfdeccf443fe; 2018-06-17T14:33:14-04:00)
Maven home: /usr/local/Cellar/maven/3.3.9/libexec
Java version: 1.8.0_102, vendor: Oracle Corporation
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 该示例需要在其自己的文件夹中创建。随后的说明假定您已经创建了一个合适的文件夹，并且它是当前目录。 |

## 11.1创建POM

我们需要先创建一个Maven `pom.xml`文件。本`pom.xml`是用来构建项目的配方。打开您喜欢的文本编辑器并添加以下内容：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.example</groupId>
	<artifactId>myproject</artifactId>
	<version>0.0.1-SNAPSHOT</version>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.9.RELEASE</version>
	</parent>

	<!-- Additional lines to be added here... -->

</project>
```

上面的清单应为您提供有效的构建。您可以通过运行`mvn package`进行测试（目前，您可以忽略“ jar将为空-没有内容标记为包含！”警告）。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 此时，您可以将项目导入IDE（大多数现代Java IDE包括对Maven的内置支持）。为简单起见，我们在此示例中继续使用纯文本编辑器。 |

## 11.2添加类路径依赖

Spring Boot提供了许多“启动器”，使您可以将jar添加到类路径中。我们的示例应用程序已在POM `spring-boot-starter-parent`的`parent`部分中使用。本`spring-boot-starter-parent`是一个特殊的启动提供有用的Maven的默认值。它还提供了一个[`dependency-management`](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-build-systems.html#using-boot-dependency-management)部分，以便您可以省略`version`“祝福”依赖项的标签。

其他“入门”提供了在开发特定类型的应用程序时可能需要的依赖项。由于我们正在开发Web应用程序，因此我们添加了`spring-boot-starter-web`依赖项。在此之前，我们可以通过运行以下命令来查看当前的状态：

```bash
$ mvn dependency:tree

[INFO] com.example:myproject:jar:0.0.1-SNAPSHOT
```

该`mvn dependency:tree`命令显示项目依赖关系的树形表示。您可以看到它`spring-boot-starter-parent`本身不提供任何依赖关系。要添加必要的依赖项，请编辑您的依赖项，`pom.xml`并`spring-boot-starter-web`在该`parent`部分的正下方添加依赖项：

```xml
<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
</dependencies>
```

如果`mvn dependency:tree`再次运行，您会发现现在还有许多其他依赖项，包括Tomcat Web服务器和Spring Boot本身。

## 11.3编写代码

要完成我们的应用程序，我们需要创建一个Java文件。默认情况下，Maven从编译源`src/main/java`，因此您需要创建该文件夹结构，然后添加一个名为的文件`src/main/java/Example.java`，其中包含以下代码：

```java
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
public class Example {

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	public static void main(String[] args) {
		SpringApplication.run(Example.class, args);
	}

}
```

尽管这里没有太多代码，但是正在发生很多事情。我们将在接下来的几节中逐步介绍重要部分。

### 11.3.1 @RestController和@RequestMapping注释

我们`Example`课程的第一个注释是`@RestController`。这被称为*构造型*注释。它为人们阅读代码提供了提示，对于Spring来说，类扮演了特定角色。在这种情况下，我们的类是web `@Controller`，因此Spring在处理传入的Web请求时会考虑使用它。

该`@RequestMapping`注释提供“路由”的信息。它告诉Spring任何具有`/`路径的HTTP请求都应映射到该`home`方法。该`@RestController`注解告诉Spring使得到的字符串直接返回给调用者。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 在`@RestController`与`@RequestMapping`注解是Spring MVC的注解（他们并不是专门针对春季启动）。有关更多详细信息，请参见Spring参考文档中的[MVC部分](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc)。 |

### 11.3.2 @EnableAutoConfiguration批注

第二个类级别的注解是`@EnableAutoConfiguration`。这个注解告诉Spring Boot根据所添加的jar依赖关系“猜测”您如何配置Spring。由于`spring-boot-starter-web`添加了Tomcat和Spring MVC，因此自动配置假定您正在开发Web应用程序并相应地设置Spring。

**启动器和自动配置**

自动配置旨在与“启动器”配合使用，但是这两个概念并没有直接联系在一起。您可以在启动程序之外自由选择jar依赖项。Spring Boot仍会尽其所能自动配置您的应用程序。

### 11.3.3“主要”方法

我们应用程序的最后一部分是`main`方法。这只是遵循Java约定的应用程序入口点的标准方法。我们的main方法`SpringApplication`通过调用委托给Spring Boot的类`run`。 `SpringApplication`引导我们的应用程序，启动Spring，这反过来又启动自动配置的Tomcat Web服务器。我们需要将`Example.class`一个参数传递给该`run`方法，以判断`SpringApplication`哪个是主要的Spring组件。该`args`数组也通过传递以公开任何命令行参数。

## 11.4运行示例

此时，您的应用程序应该可以工作了。由于使用了`spring-boot-starter-parent`POM，因此有一个有用的`run`目标，可以用来启动应用程序。`mvn spring-boot:run`从项目根目录中键入以启动应用程序。您应该看到类似于以下内容的输出：

```bash
$ mvn spring-boot:run

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::  (v2.1.9.RELEASE)
....... . . .
....... . . . (log output here)
....... . . .
........ Started Example in 2.222 seconds (JVM running for 6.514)
```

如果您打开Web浏览器到`localhost:8080`，则应该看到以下输出：

```bash
Hello World!
```

要正常退出该应用程序，请按`ctrl-c`。

## 11.5 创建一个可执行的jar

通过创建可以在生产环境中运行的完全独立的可执行jar文件来结束示例。可执行jar（有时称为“胖jar”）是包含您的已编译类以及代码需要运行的所有jar依赖项的归档文件。

**可执行jar和Java**

Java没有提供加载嵌套jar文件（jar中本身包含的jar文件）的标准方法。如果您要分发独立的应用程序，则可能会出现问题。

为了解决这个问题，许多开发人员使用“超级”罐子。uber jar将来自应用程序所有依赖项的所有类打包到单个存档中。这种方法的问题在于，很难查看应用程序中包含哪些库。如果在多个jar中使用相同的文件名（但具有不同的内容），也可能会产生问题。

Spring Boot采用了[另一种方法](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/executable-jar.html)，实际上允许您直接嵌套jar。

要创建可执行jar，我们需要将添加`spring-boot-maven-plugin`到`pom.xml`。为此，请在该`dependencies`部分下方插入以下行：

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
		</plugin>
	</plugins>
</build>
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 所述`spring-boot-starter-parent`POM包括`<executions>`配置以结合`repackage`目标。如果不使用父POM，则需要自己声明此配置。有关详细信息，请参见[插件文档](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/maven-plugin/usage.html)。 |

保存`pom.xml`并从命令行运行`mvn package`，如下所示：

```bash
$ mvn package

[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building myproject 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] .... ..
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ myproject ---
[INFO] Building jar: /Users/developer/example/spring-boot-example/target/myproject-0.0.1-SNAPSHOT.jar
[INFO]
[INFO] --- spring-boot-maven-plugin:2.1.9.RELEASE:repackage (default) @ myproject ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

如果查看`target`目录，则应该看到`myproject-0.0.1-SNAPSHOT.jar`。该文件的大小应为10 MB左右。如果您想窥视内部，可以使用`jar tvf`，如下所示：

```bash
$ jar tvf target/myproject-0.0.1-SNAPSHOT.jar
```

您还应该`myproject-0.0.1-SNAPSHOT.jar.original`在`target`目录中看到一个更小的文件。这是Maven在Spring Boot重新打包之前创建的原始jar文件。

要运行该应用程序，请使用以下`java -jar`命令：

```bash
$ java -jar target/myproject-0.0.1-SNAPSHOT.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::  (v2.1.9.RELEASE)
....... . . .
....... . . . (log output here)
....... . . .
........ Started Example in 2.536 seconds (JVM running for 2.864)
```

和以前一样，要退出该应用程序，请按`ctrl-c`。