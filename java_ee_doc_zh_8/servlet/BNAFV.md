# 5.服务方法

由servlet提供的服务是在实施`service`的方法`GenericServlet`，在`do`Method methods (where Method can take the value `Get`，`Delete`，`Options`，`Post`，`Put`，或`Trace`）一个的 `HttpServlet`对象，或在由实现一个类定义的任何其它特定于协议的方法`Servlet`接口。服务方法一词用于Servlet类中向客户端提供服务的任何方法。

服务方法的一般模式是从请求中提取信息，访问外部资源，然后基于该信息填充响应。对于HTTP servlet，填充响应的正确过程是执行以下操作：

1. 从响应中检索输出流。
2. 填写响应头。
3. 将任何正文内容写入输出流。

在提交响应之前，必须始终设置响应头。提交响应后，Web容器将忽略任何设置或添加标头的尝试。接下来的两个部分描述了如何从请求中获取信息并生成响应。



### 从请求中获取信息

请求包含在客户端和Servlet之间传递的数据。所有请求均实现该`ServletRequest`接口。该接口定义了用于访问以下信息的方法：

- 参数，通常用于在客户端和Servlet之间传递信息
- 对象值属性，通常用于在Web容器和Servlet之间或协作Servlet之间传递信息
- 有关用于传达请求的协议以及有关请求中涉及的客户端和服务器的信息
- 与本地化有关的信息

您还可以从请求中检索输入流并手动解析数据。要读取字符数据，请使用`BufferedReader`请求`getReader`方法返回的对象。要读取二进制数据，请使用`ServletInputStream`返回的`getInputStream`。

HTTP Servlet传递了一个HTTP请求对象，`HttpServletRequest`该对象包含请求URL，HTTP标头，查询字符串等。HTTP请求URL包含以下部分：

```oac_no_warn
http://[host]:[port][request-path]?[query-string]
```

请求路径进一步由以下元素组成。

- 上下文路径：正斜杠（`/`）与Servlet Web应用程序的上下文根的串联。
- Servlet路径：与激活该请求的组件别名相对应的路径部分。此路径以正斜杠（`/`）开头。
- 路径信息：请求路径中不属于上下文路径或servlet路径的一部分。

您可以使用`getContextPath`，`getServletPath`以及`getPathInfo` 该方法的`HttpServletRequest`接口来访问这些信息。除了请求URI和路径部分之间的URL编码差异之外，请求URI始终由上下文路径加上Servlet路径加上路径信息组成。

查询字符串由一组参数和值。使用该`getParameter` 方法从请求中检索各个参数。有两种生成查询字符串的方法。

- 查询字符串可以明确地显示在网页上。
- `GET`提交带有HTTP方法的表单时，查询字符串将附加到URL 。



### 构建响应

响应包含在服务器和客户端之间传递的数据。所有响应均实现该`ServletResponse`接口。该接口定义了允许您执行以下操作的方法。

- 检索用于将数据发送到客户端的输出流。要发送字符数据，请使用`PrintWriter`响应`getWriter`方法返回的值 。要在多用途Internet邮件扩展（MIME）正文响应中发送二进制数据，请使用`ServletOutputStream`返回的`getOutputStream`。要混合二进制和文本数据（如多部分响应一样），请使用`ServletOutputStream`并手动管理字符部分。
- 使用方法指示`text/html`响应所返回的内容类型（例如）`setContentType(String)`。提交响应之前，必须先调用此方法。互联网号码分配机构（IANA）保留内容类型名称的注册表 `http://www.iana.org/assignments/media-types/`。
- 指示是否使用该`setBufferSize(int)` 方法缓冲输出。默认情况下，任何写入输出流的内容都会立即发送到客户端。缓冲允许在将任何内容发送回客户端之前先编写内容，从而为Servlet提供更多时间来设置适当的状态代码和标头或转发到另一个Web资源。必须在编写任何内容之前或在提交响应之前调用该方法。
- 设置本地化信息，例如语言环境和字符编码。有关详细信息[，](https://javaee.github.io/tutorial/webi18n.html#BNAXU)请参见[第22章，“对Web应用程序](https://javaee.github.io/tutorial/webi18n.html#BNAXU)进行[国际化和本地化”](https://javaee.github.io/tutorial/webi18n.html#BNAXU)。

HTTP响应对象`javax.servlet.http.HttpServletResponse`具有具有表示HTTP标头的字段，如下所示。

- 状态码，用于指示不满足请求或请求已被重定向的原因。
- Cookies，用于在客户端存储特定于应用程序的信息。有时，cookie用于维护用于跟踪用户会话的标识符（请参阅[Session Tracking](https://javaee.github.io/tutorial/servlets009.html#BNAGR)）。