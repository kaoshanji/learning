# 7.调用其他Web资源

Web组件可以间接或直接调用其他Web资源。Web组件通过在返回给客户端的内容中嵌入指向另一个Web组件的URL来间接调用另一个Web资源。Web组件在执行时，通过包括另一个资源的内容或将请求转发到另一个资源来直接调用另一个资源。

若要调用运行Web组件的服务器上可用的资源，必须首先`RequestDispatcher`使用`getRequestDispatcher("URL")`方法获得一个对象。您可以`RequestDispatcher`从请求或Web上下文中获取 对象。但是，这两种方法的行为略有不同。该方法将请求资源的路径作为参数。请求可以采用相对路径（即，不是以开头`/`的路径），但是Web上下文需要绝对路径。如果资源不可用，或者服务器尚未`RequestDispatcher` 为该类型的资源实现对象，`getRequestDispatcher`则将返回null。您的servlet应该准备好应对这种情况。



### 在响应中包括其他资源

在从Web组件返回的响应中包含其他Web资源（例如横幅内容或版权信息）通常很有用。要包含另一个资源，请调用对象的`include`方法 `RequestDispatcher`：

```java
include(request, response);
```

如果资源是静态的，该`include`方法使编程服务器端包含。如果资源是Web组件，则该方法的作用是将请求发送到包含的Web组件，执行Web组件，然后将执行结果包含在包含servlet的响应中。包含的Web组件可以访问请求对象，但是在响应对象中可以执行的操作受到限制。

- 它可以写入响应的主体并提交响应。
- 它不能设置标头或调用任何`setCookie`会影响响应标头的方法，例如。



### 将控制权转移到另一个Web组件

在某些应用程序中，您可能希望让一个Web组件对请求进行初步处理，并让另一个组件生成响应。例如，您可能要部分处理一个请求，然后再将其转移到另一个组件，具体取决于请求的性质。

要将控件转移到另一个Web组件，请调用的`forward` 方法`RequestDispatcher`。转发请求时，请求URL设置为转发页面的路径。原始URI及其组成部分保存为以下请求属性：

```java
javax.servlet.forward.request_uri
javax.servlet.forward.context_path
javax.servlet.forward.servlet_path
javax.servlet.forward.path_info
javax.servlet.forward.query_string
```

该`forward`方法应用于赋予其他资源回复用户的责任。如果您已经访问了servlet中的 `ServletOutputStream`或`PrintWriter`对象，则不能使用此方法。这样做会抛出一个错误`IllegalStateException`。