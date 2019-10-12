# 19、运行你的应用

将应用程序打包为jar并使用嵌入式HTTP服务器的最大优势之一是，您可以像运行其他应用程序一样运行应用程序。调试Spring Boot应用程序也很容易。您不需要任何特殊的IDE插件或扩展。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 本节仅介绍基于罐子的包装。如果选择将应用程序打包为war文件，则应参考服务器和IDE文档。 |



## 19.1从IDE运行

您可以将IDE中的Spring Boot应用程序作为简单的Java应用程序运行。但是，您首先需要导入您的项目。导入步骤因您的IDE和构建系统而异。大多数IDE可以直接导入Maven项目。例如，Eclipse用户可以从菜单中选择`Import…`→ 。`Existing Maven Projects``File`

如果您不能直接将项目导入IDE，则可以使用构建插件生成IDE元数据。Maven包括[Eclipse](https://maven.apache.org/plugins/maven-eclipse-plugin/)和[IDEA的](https://maven.apache.org/plugins/maven-idea-plugin/)插件。Gradle提供了用于[各种IDE的](https://docs.gradle.org/4.2.1/userguide/userguide.html)插件。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果不小心两次运行Web应用程序，则会看到“端口已在使用中”错误。STS用户可以使用`Relaunch`按钮而不是`Run`按钮来确保关闭任何现有实例。 |

## 19.2作为打包的应用程序运行

如果您使用Spring Boot Maven或Gradle插件来创建可执行jar，则可以使用来运行您的应用程序`java -jar`，如以下示例所示：

```bash
$ java -jar target/myapplication-0.0.1-SNAPSHOT.jar
```

也可以在启用了远程调试支持的情况下运行打包的应用程序。这样做使您可以将调试器附加到打包的应用程序，如以下示例所示：

```bash
$ java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n \
       -jar target/myapplication-0.0.1-SNAPSHOT.jar
```

## 19.3使用Maven插件

Spring Boot Maven插件包含一个`run`目标，可用于快速编译和运行您的应用程序。应用程序以爆炸形式运行，就像在IDE中一样。以下示例显示了运行Spring Boot应用程序的典型Maven命令：

```bash
$ mvn spring-boot:run
```

您可能还想使用`MAVEN_OPTS`操作系统环境变量，如以下示例所示：

```bash
$ export MAVEN_OPTS=-Xmx1024m
```



## 19.5热插拔

由于Spring Boot应用程序只是普通的Java应用程序，因此JVM热交换应该可以立即使用。JVM热插拔在一定程度上受到它可以替换的字节码的限制。对于更完整的解决方案，可以使用[JRebel](https://jrebel.com/software/jrebel/)。

该`spring-boot-devtools`模块还包括对应用程序快速重启的支持。有关详细信息，请参见[本章](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-devtools.html)后面的[第20章“ ](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-devtools.html)[*开发人员工具”*](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-devtools.html)部分和[热插拔“操作方法”](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-hotswapping.html)。