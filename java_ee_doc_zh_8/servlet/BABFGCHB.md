# 11.使用Java Servlet技术上传文件

支持文件上传是许多Web应用程序的非常基本且常见的要求。在Servlet规范的早期版本中，实现文件上载需要使用外部库或复杂的输入处理。现在，Java Servlet规范有助于以通用且可移植的方式为该问题提供可行的解决方案。Java Servlet技术现在支持开箱即用的文件上传，因此，实现该规范的任何Web容器都可以解析多部分请求，并通过该`HttpServletRequest`对象提供mime附件 。

新的注释`javax.servlet.annotation.MultipartConfig`用来指示声明了它的servlet期望使用`multipart/form-data`MIME类型进行请求。带注解的Servlet `@MultipartConfig`可以通过调用or 方法来检索`Part`给定`multipart/form-data`请求 的组件。`request.getPart(String name)``request.getParts()`



### @MultipartConfig批注

该`@MultipartConfig`注释支持以下可选属性。

- `location`：文件系统上目录的绝对路径。该 `location`属性不支持相对于应用程序上下文的路径。在处理零件或文件大小超过指定`fileSizeThreshold`设置时，此位置用于临时存储文件 。默认位置是`""`。
- `fileSizeThreshold`：文件大小（以字节为单位），之后该文件将被临时存储在磁盘上。默认大小为0字节。
- `MaxFileSize`：上传的文件允许的最大大小（以字节为单位）。如果任何上传的文件的大小大于该大小，则Web容器将引发异常（`IllegalStateException`）。默认大小是无限的。
- `maxRequestSize`：`multipart/form-data` 请求允许的最大大小，以字节为单位。如果所有上载文件的总大小超过此阈值，则Web容器将引发异常。默认大小是无限的。

例如，`@MultipartConfig`注释可以按以下方式构造：

```java
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024,
    maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
```

`@MultipartConfig`可以使用以下内容作为`web.xml` 文件中servlet配置元素的子元素，而不是使用注释在文件上传servlet中对这些属性进行硬编码：

```xml
<multipart-config>
    <location>/tmp</location>
    <max-file-size>20848820</max-file-size>
    <max-request-size>418018841</max-request-size>
    <file-size-threshold>1048576</file-size-threshold>
</multipart-config>
```



### getParts和getPart方法

Servlet规范支持两种其他`HttpServletRequest` 方法：

- `Collection<Part> getParts()`
- `Part getPart(String name)`

该`request.getParts()`方法返回所有`Part` 对象的集合。如果类型文件的输入不止一个，`Part` 则返回多个对象。因为`Part`对象是命名的，所以该 `getPart(String name)`方法可用于访问特定对象`Part`。或者，可以使用`getParts()`返回的方法 `Iterable<Part>`来获取`Iterator`所有`Part` 对象的。

该`javax.servlet.http.Part`接口很简单，提供了允许对每个接口进行自省的方法`Part`。该方法执行以下操作：

- 检索名称，大小和内容类型 `Part`
- 查询提交的标头 `Part`
- 删除一个 `Part`
- 写出`Part`到磁盘

例如，该`Part`接口提供了`write(String filename)` 使用指定名称写入文件的方法。然后，可以将文件保存在使用批注`location`属性指定的目录中，`@MultipartConfig`或者在`fileupload` 示例的情况下，保存在表单中“目标”字段指定的位置中。