# 8.3 Resource实现

`Resource`Spring中提供了许多直接提供的实现：

### 8.3.1 UrlResource

所述`UrlResource`包裹一个`java.net.URL`，并且可以被用于访问任何对象，该对象是通过URL正常访问，如文件，一个HTTP靶，FTP对象等的所有URL具有标准化的`String`表示，以使得适当的标准化的前缀被用来指示一个URL类型来自另一个。这包括`file:`访问文件系统路径，`http:`通过HTTP协议 `ftp:`访问资源，通过FTP访问资源等。

A `UrlResource`由Java代码使用`UrlResource`构造函数显式创建，但通常在调用API方法时隐式创建，该方法接受`String` 表示路径的参数。对于后一种情况，JavaBeans `PropertyEditor`最终将决定`Resource`要创建哪种类型。如果路径字符串包含一些众所周知的（对它，那就是）前缀，例如`classpath:`，它将`Resource`为该前缀创建一个合适的专用。但是，如果它不识别前缀，它将假设这只是一个标准的URL字符串，并将创建一个`UrlResource`。

### 8.3.2 ClassPathResource

此类表示应从类路径获取的资源。这使用线程上下文类加载器，给定的类加载器或给定的类来加载资源。

此`Resource`实现支持解析，`java.io.File`就像类路径资源驻留在文件系统中一样，但不支持驻留在jar中且尚未（通过servlet引擎或任何环境）扩展到文件系统的类路径资源。为了解决这个问题，各种`Resource`实现总是支持解决方案`java.net.URL`。

A `ClassPathResource`由Java代码使用`ClassPathResource` 构造函数显式创建，但通常在调用API方法时隐式创建，该方法接受`String`表示路径的参数。对于后一种情况，JavaBeans `PropertyEditor`将识别`classpath:`字符串路径上的特殊前缀，并`ClassPathResource`在该情况下创建一个。

### 8.3.3 FileSystemResource

这是句柄的`Resource`实现`java.io.File`。它显然支持作为一个`File`和一个的决议`URL`。

### 8.3.4 ServletContextResource

这是资源的`Resource`实现`ServletContext`，解释相关Web应用程序根目录中的相对路径。

这始终支持流访问和URL访问，但仅允许`java.io.File`在扩展Web应用程序归档并且资源实际位于文件系统上时进行访问。它是否在这样的文件系统上展开，或直接从JAR或其他地方（如DB）（可以想象）访问，实际上是依赖于Servlet容器。

### 8.3.5 InputStreamResource

`Resource`给定的实现`InputStream`。只有在没有`Resource`适用的具体实施时才应使用此选项。特别地，在可能`ByteArrayResource`的`Resource`情况下，优选 或任何基于文件的实现。

相对于其他`Resource`的实现，这是一个描述符*已经* 打开资源-因此返回`true`的`isOpen()`。如果需要将资源描述符保留在某处，或者需要多次读取流，请不要使用它。

### 8.3.6 ByteArrayResource

这是`Resource`给定字节数组的实现。它`ByteArrayInputStream`为给定的字节数组创建一个 。

它对于从任何给定的字节数组加载内容非常有用，而不必求助于单次使用`InputStreamResource`。