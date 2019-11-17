# 10、初始化 Spring Boot

Spring Boot可以与“经典” Java开发工具一起使用，也可以作为命令行工具安装。无论哪种方式，都需要[Java SDK v1.8](https://www.java.com/)或更高版本。在开始之前，您应该使用以下命令检查当前的Java安装：

```
$ java -version
```

如果您不熟悉Java开发，或者想尝试使用Spring Boot，则可能要先尝试使用[Spring Boot CLI](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/getting-started-installing-spring-boot.html#getting-started-installing-the-cli)（命令行界面）。否则，请继续阅读“经典”安装说明。



## 10.1 Java开发人员的安装说明

您可以像使用任何标准Java库一样使用Spring Boot。为此，请`spring-boot-*.jar`在类路径中包含适当的文件。Spring Boot不需要任何特殊的工具集成，因此您可以使用任何IDE或文本编辑器。而且，Spring Boot应用程序没有什么特别之处，因此您可以像运行其他Java程序一样运行和调试Spring Boot应用程序。

尽管您*可以*复制Spring Boot jar，但是我们通常建议您使用支持依赖关系管理的构建工具（例如Maven或Gradle）。

### 10.1.1 Maven安装

Spring Boot与Apache Maven 3.3或更高版本兼容。如果尚未安装Maven，则可以按照[maven.apache.org上](https://maven.apache.org/)的说明进行操作。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 在许多操作系统上，Maven可以与程序包管理器一起安装。如果您使用OSX Homebrew，请尝试`brew install maven`。Ubuntu用户可以运行`sudo apt-get install maven`。具有[Chocolatey的](https://chocolatey.org/) Windows用户可以`choco install maven`从提升的（管理员）提示符下运行。 |

Spring Boot依赖项使用`org.springframework.boot` `groupId`。通常，您的Maven POM文件从`spring-boot-starter-parent`项目继承，并声明对一个或多个[“启动器”的](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-build-systems.html#using-boot-starter)依赖关系。Spring Boot还提供了一个可选的[Maven插件](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/build-tool-plugins-maven-plugin.html)来创建可执行jar。

以下清单显示了一个典型的`pom.xml`文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.example</groupId>
	<artifactId>myproject</artifactId>
	<version>0.0.1-SNAPSHOT</version>

	<!-- Inherit defaults from Spring Boot -->
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.9.RELEASE</version>
	</parent>

	<!-- Add typical dependencies for a web application -->
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
	</dependencies>

	<!-- Package as an executable jar -->
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 这`spring-boot-starter-parent`是使用Spring Boot的一种很好的方法，但是可能并不总是适合。有时您可能需要从其他父POM继承，或者您可能不喜欢我们的默认设置。在这种情况下，请参见[第13.2.2节“在没有父POM的情况下使用Spring Boot”](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-build-systems.html#using-boot-maven-without-a-parent)以获取使用`import`范围的替代解决方案。 |

## 10.2安装Spring Boot CLI

Spring Boot CLI（命令行界面）是一个命令行工具，可用于快速使用Spring进行原型设计。它使您可以运行[Groovy](http://groovy-lang.org/)脚本，这意味着您具有类似Java的熟悉语法，而没有太多样板代码。

您无需使用CLI即可与Spring Boot一起使用，但这绝对是使Spring应用程序启动的最快方法。