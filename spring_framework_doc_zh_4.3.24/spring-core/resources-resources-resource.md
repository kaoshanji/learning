# 8.2 Resource接口

Spring的`Resource`接口是一个更强大的接口，用于抽象对低级资源的访问。

```java
public interface Resource extends InputStreamSource {

    boolean exists();

    boolean isOpen();

    URL getURL() throws IOException;

    File getFile() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    String getFilename();

    String getDescription();

}
```

```java
public interface InputStreamSource {

    InputStream getInputStream() throws IOException;

}
```

`Resource`界面中一些最重要的方法是：

- `getInputStream()`：找到并打开资源，返回`InputStream`从资源中读取的内容。预计每次调用都会返回一个新的 `InputStream`。呼叫者有责任关闭流。
- `exists()`：返回`boolean`指示此资源是否实际存在于物理形式的a。
- `isOpen()`：返回一个`boolean`指示此资源是否表示具有打开流的句柄的a。如果`true`，`InputStream`不能多次读取，必须只读一次然后关闭以避免资源泄漏。将`false`用于所有常规资源实现，但不包括`InputStreamResource`。
- `getDescription()`：返回此资源的描述，用于处理资源时的错误输出。这通常是完全限定的文件名或资源的实际URL。

其他方法允许您获取表示资源的实际`URL`或`File`对象（如果底层实现兼容，并支持该功能）。

该`Resource`抽象需要资源时Spring自身广泛使用，在许多方法签名的参数类型。某些Spring API中的其他方法（例如各种`ApplicationContext`实现的构造函数）采用以`String`简单或简单的形式创建`Resource`适合于该上下文实现的方法，或者通过`String`路径上的特殊前缀，允许调用者指定`Resource`必须创建和使用特定的实现。

虽然`Resource`Spring和Spring都使用了很多接口，但是在你自己的代码中使用它作为通用实用程序类非常有用，用于访问资源，即使你的代码不知道或不关心任何其他部分春天 虽然这会将您的代码耦合到Spring，但它实际上只将它耦合到这一小组实用程序类，这些实用程序类作为更有能力的替代品`URL`，并且可以被认为与您为此目的使用的任何其他库等效。

重要的是要注意`Resource`抽象不会取代功能：它尽可能地包装它。例如，a `UrlResource`包装一个URL，并使用wrapped `URL`来完成它的工作。