# 8.4 ResourceLoader

该`ResourceLoader`接口旨在由可以返回（即加载）`Resource`实例的对象实现。

```java
public interface ResourceLoader {

    Resource getResource(String location);

}
```

所有应用程序上下文都实现了`ResourceLoader`接口，因此可以使用所有应用程序上下文来获取`Resource`实例。

当您调用`getResource()`特定的应用程序上下文，并且指定的位置路径没有特定的前缀时，您将返回一个`Resource`适合该特定应用程序上下文的类型。例如，假设针对`ClassPathXmlApplicationContext`实例执行了以下代码片段：

```java
Resource template = ctx.getResource("some/resource/path/myTemplate.txt");
```

将返回的是一个`ClassPathResource`; 如果对一个`FileSystemXmlApplicationContext`实例执行相同的方法，你会得到一个 `FileSystemResource`。对于a `WebApplicationContext`，你会得到一个 `ServletContextResource`，等等。

因此，您可以以适合特定应用程序上下文的方式加载资源。

另一方面，您也`ClassPathResource`可以通过指定特殊`classpath:`前缀强制使用，而不管应用程序上下文类型如何：

```java
Resource template = ctx.getResource("classpath:some/resource/path/myTemplate.txt");
```

类似地，可以`UrlResource`通过指定任何标准 `java.net.URL`前缀来强制使用a ：

```java
Resource template = ctx.getResource("file:///some/resource/path/myTemplate.txt");
```

```java
Resource template = ctx.getResource("https://myhost.com/resource/path/myTemplate.txt");
```

下表总结了将`String`s 转换为`Resource`s 的策略：



**表8.1。资源字符串**

| 字首         | 例                               | 说明                                        |
| ------------ | -------------------------------- | ------------------------------------------- |
| classpath:： | `classpath:com/myapp/config.xml` | 从类路径加载。                              |
| file：       | `file:///data/config.xml`        | `URL`从文件系统加载为。[[1\]](#ftn.d5e6190) |
| http：       | `https://myserver/logo.png`      | 加载为`URL`。                               |
| (none)       | `/data/config.xml`               | 取决于潜在的`ApplicationContext`。          |

