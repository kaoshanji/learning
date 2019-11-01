# 14.SSI

### 介绍

SSI（服务器端包含）是放置在HTML页面中的指令，并在服务页面时在服务器上对其进行评估。它们使您可以将动态生成的内容添加到现有的HTML页面，而不必通过CGI程序或其他动态技术来提供整个页面。

在Tomcat中，将Tomcat用作HTTP服务器并且需要SSI支持时，可以添加SSI支持。通常，当您不想运行Apache之类的Web服务器时，这是在开发期间完成的。

Tomcat SSI支持实现与Apache相同的SSI指令。有关使用SSI指令的信息，请参见 [Apache SSI简介](https://httpd.apache.org/docs/howto/ssi.html#basicssidirectives)。

SSI支持可作为Servlet和过滤器使用。您应该使用一个或另一个来提供SSI支持，但不能同时使用两者。

基于Servlet的SSI支持使用类实现 `org.apache.catalina.ssi.SSIServlet`。传统上，此servlet映射到URL模式“ * .shtml”。

使用类实现基于过滤器的SSI支持 `org.apache.catalina.ssi.SSIFilter`。传统上，此过滤器会映射到URL模式“ * .shtml”，尽管它可以映射到“ *”，因为它将基于mime类型选择性地启用/禁用SSI处理。contentType初始化参数使您可以将SSI处理应用于JSP页面，javascript或所需的任何其他内容。

默认情况下，Tomcat中禁用了SSI支持。

### 安装

**注意** -SSI指令可用于执行Tomcat JVM外部的程序。如果您使用的是Java SecurityManager，它将绕过您的安全策略配置`catalina.policy.`

要使用SSI servlet，请从中删除SSI servlet和servlet映射配置周围的XML注释 `$CATALINA_BASE/conf/web.xml`。

要使用SSI过滤器，请从中删除SSI过滤器和过滤器映射配置周围的XML注释 `$CATALINA_BASE/conf/web.xml`。

只有标记为特权的上下文可以使用SSI功能（请参阅Context元素的特权属性）。

### Servlet配置

有几个servlet初始化参数可用于配置SSI servlet的行为。

- **缓冲** -该servlet的输出是否应该缓冲？（0 =假，1 =真）默认0（假）。
- **debug-**调试此servlet记录的消息的详细信息级别。默认值0。
- **expires-**具有SSI指令的页面将过期的秒数。默认行为是针对每个请求评估所有SSI指令。
- **isVirtualWebappRelative-**是否应该将“虚拟” SSI指令路径解释为相对于上下文根而不是服务器根？默认为false。
- **inputEncoding-**如果无法从资源本身确定SSI资源，则假定为编码。默认是默认平台编码。
- **outputEncoding-**用于SSI处理结果的编码。默认值为UTF-8。
- **allowExec-**启用了exec命令吗？默认为false。

### 过滤器配置

有几个过滤器初始化参数可用于配置SSI过滤器的行为。

- **contentType-**在应用SSI处理之前必须匹配的正则表达式模式。在制作自己的模式时，请不要忘记在mime内容类型之后可能是一个可选字符集，其形式必须为“ mime / type; charset = set”。默认值为“ text / x-server-parsed-html（;。*）？”。
- **debug-**调试此servlet记录的消息的详细信息级别。默认值0。
- **expires-**具有SSI指令的页面将过期的秒数。默认行为是针对每个请求评估所有SSI指令。
- **isVirtualWebappRelative-**是否应该将“虚拟” SSI指令路径解释为相对于上下文根而不是服务器根？默认为false。
- **allowExec-**启用了exec命令吗？默认为false。

### 指令

通过将SSI指令嵌入HTML文档中来调用服务器端包含，其类型将由SSI servlet处理。指令采用HTML注释的形式。在将页面发送到客户端之前，该指令将被解释结果替换。指令的一般形式为：

```html
<!--#directive [parm=value] -->
```

指令是：

- **配置** - `<!--#config errmsg="Error occured" sizefmt="abbrev" timefmt="%B %Y" -->` 用于设置SSI错误信息，通过SSI处理日期和文件大小的格式。
  所有都是可选的，但必须至少使用一个。可用的选项如下：
  **ERRMSG** -用于SSI错误的错误消息
  **sizefmt** -用于大小格式**FSIZE**指令
  **timefmt的**格式在使用时间戳- **flastmod**指令

- **回波** - `<!--#echo var="VARIABLE_NAME" encoding="entity" -->` 将由变量的值来替代。
  可选的**encoding**参数指定要使用的编码类型。有效值是**实体**（默认），**url**或**none**。注意：使用除**实体**之外的编码可能会导致安全问题。

- **EXEC** - `<!--#exec cmd="file-name" -->` 用于运行在主机系统上的命令。

- **EXEC** - `<!--#exec cgi="file-name" -->` 它的作用相同，**包括虚拟**指令，而实际上并不执行任何命令。

- **包括** - `<!--#include file="file-name" -->` 插入内容。该路径是相对于使用此伪指令的文档解释的，而不是相对于上下文根或服务器根的“虚拟”路径。

- **包括** - `<!--#include virtual="file-name" -->` 插入内容。该路径被解释为相对于上下文根或服务器根的“虚拟”路径（取决于**isVirtualWebappRelative** 参数）。

- **flastmod** - `<!--#flastmod file="filename.shtml" -->` 返回时，一个文件的最后修改。该路径是相对于使用此伪指令的文档解释的，而不是相对于上下文根或服务器根的“虚拟”路径。

- **flastmod** - `<!--#flastmod virtual="filename.shtml" -->` 返回时，一个文件的最后修改。该路径被解释为相对于上下文根或服务器根的“虚拟”路径（取决于**isVirtualWebappRelative** 参数）。

- **FSIZE** - `<!--#fsize file="filename.shtml" -->` 返回文件的大小。该路径是相对于使用此伪指令的文档解释的，而不是相对于上下文根或服务器根的“虚拟”路径。

- **FSIZE** - `<!--#fsize virtual="filename.shtml" -->` 返回文件的大小。该路径被解释为相对于上下文根或服务器根的“虚拟”路径（取决于**isVirtualWebappRelative** 参数）。

- **printenv** - `<!--#printenv -->` 返回所有定义的变量列表。

- **组** - `<!--#set var="foo" value="Bar" -->` 被用来指定一个值，以用户定义的变量。

- if elif endif else-

  用于创建条件部分。例如：

  ```html
  <!--#config timefmt="%A" -->
  <!--#if expr="$DATE_LOCAL = /Monday/" -->
  <p>Meeting at 10:00 on Mondays</p>
  <!--#elif expr="$DATE_LOCAL = /Friday/" -->
  <p>Turn in your time card</p>
  <!--#else -->
  <p>Yoga class at noon.</p>
  <!--#endif -->
  ```

有关使用SSI指令的更多信息，请参见 [Apache SSI简介](https://httpd.apache.org/docs/howto/ssi.html#basicssidirectives)。

### 变数

SSI变量是通过**javax.servlet.ServletRequest**对象上的请求属性实现的，并且不仅限于SSI servlet。以名称“ java。”，“ javax。”，“ sun”或“ org.apache.catalina.ssi.SSIMediator”开头的变量。保留，不能使用。

SSI servlet当前实现以下变量：

| 变量名                 | 描述                                                     |
| :--------------------- | :------------------------------------------------------- |
| AUTH_TYPE              | 用于此用户的身份验证类型：BASIC，FORM等。                |
| CONTENT_LENGTH         | 从表单传递的数据长度（字节或字符数）。                   |
| 内容类型               | 查询数据的MIME类型，例如“ text / html”。                 |
| DATE_GMT               | 格林尼治标准时间的当前日期和时间                         |
| DATE_LOCAL             | 当地时区的当前日期和时间                                 |
| DOCUMENT_NAME          | 当前文件                                                 |
| DOCUMENT_URI           | 文件的虚拟路径                                           |
| GATEWAY_INTERFACE      | 如果启用，服务器使用的通用网关接口的修订：“ CGI / 1.1”。 |
| HTTP_ACCEPT            | 客户端可以接受的MIME类型的列表。                         |
| HTTP_ACCEPT_ENCODING   | 客户端可以接受的压缩类型列表。                           |
| HTTP_ACCEPT_LANGUAGE   | 客户可以接受的语言列表。                                 |
| HTTP_CONNECTION        | 管理来自客户端的连接的方式：“关闭”或“保持活动”。         |
| HTTP_HOST              | 客户请求的网站。                                         |
| HTTP_REFERER           | 客户端链接到的文档的URL。                                |
| HTTP_USER_AGENT        | 客户端用于发出请求的浏览器。                             |
| 上一次更改             | 当前文件的上次修改日期和时间                             |
| PATH_INFO              | 传递给servlet的额外路径信息。                            |
| PATH_TRANSLATED        | 变量PATH_INFO给出的路径的翻译版本。                      |
| 请求参数               | “？”之后的查询字符串 在网址中。                          |
| QUERY_STRING_UNESCAPED | 未解码的查询字符串，其中所有的外壳元字符都以“ \”转义     |
| REMOTE_ADDR            | 发出请求的用户的远程IP地址。                             |
| 远程主机               | 发出请求的用户的远程主机名。                             |
| REMOTE_PORT            | 发出请求的用户的远程IP地址上的端口号。                   |
| REMOTE_USER            | 用户的身份验证名称。                                     |
| REQUEST_METHOD         | 发出信息请求的方法：“ GET”，“ POST”等。                  |
| REQUEST_URI            | 客户端最初请求的网页。                                   |
| SCRIPT_FILENAME        | 服务器上当前网页的位置。                                 |
| SCRIPT_NAME            | 网页名称。                                               |
| SERVER_ADDR            | 服务器的IP地址。                                         |
| 服务器名称             | 服务器的主机名或IP地址。                                 |
| 服务器端口             | 服务器接收请求的端口。                                   |
| SERVER_PROTOCOL        | 服务器使用的协议。例如“ HTTP / 1.1”。                    |
| SERVER_SOFTWARE        | 响应客户端请求的服务器软件的名称和版本。                 |
| 唯一身份               | 如果已经建立，则用于标识当前会话的令牌。                 |