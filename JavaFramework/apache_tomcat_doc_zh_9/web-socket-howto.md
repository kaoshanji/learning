# 33.WebSocket支持

### 总览

Tomcat提供对[RFC 6455](https://tools.ietf.org/html/rfc6455)定义的WebSocket的支持 。

### 应用开发

Tomcat实现了[JSR-356](https://www.jcp.org/en/jsr/detail?id=356)定义的Java WebSocket 1.1 API 。

有几个示例应用程序演示了如何使用WebSocket API。您将需要同时查看客户端[ HTML](https://svn.apache.org/viewvc/tomcat/trunk/webapps/examples/websocket/)和服务器端[ 代码](https://svn.apache.org/viewvc/tomcat/trunk/webapps/examples/WEB-INF/classes/websocket/)。

### Tomcat WebSocket特定配置

Tomcat为WebSocket提供了许多Tomcat特定的配置选项。预计随着时间的推移，这些将被纳入WebSocket规范中。

在阻塞模式下发送WebSocket消息时使用的写超时默认为20000毫秒（20秒）。这可以通过`org.apache.tomcat.websocket.BLOCKING_SEND_TIMEOUT` 在WebSocket会话所附的用户属性集合中设置属性来更改。分配给此属性的值应为a，`Long`并表示要使用的超时（以毫秒为单位）。对于无限超时，请使用 `-1`。

如果应用程序没有`MessageHandler.Partial`为传入的二进制消息定义，则必须缓冲所有传入的二进制消息，以便可以在单个调用中将整个消息传递给已注册 `MessageHandler.Whole`的二进制消息。二进制消息的默认缓冲区大小为8192字节。对于Web应用程序，可以通过将Servlet上下文初始化参数`org.apache.tomcat.websocket.binaryBufferSize`设置为所需的字节值来更改此设置 。

如果应用程序没有`MessageHandler.Partial`为传入的文本消息定义，则必须缓冲所有传入的文本消息，以便可以在单个调用中将整个消息传递到已注册 `MessageHandler.Whole`的文本消息。文本消息的默认缓冲区大小为8192字节。对于Web应用程序，可以通过将Servlet上下文初始化参数`org.apache.tomcat.websocket.textBufferSize`设置为所需的字节值来更改此设置 。

在第一个端点启动WebSocket握手之后，Java WebSocket规范1.0不允许以编程方式进行部署。默认情况下，Tomcat继续允许其他程序部署。此行为由 `org.apache.tomcat.websocket.noAddAfterHandshake`Servlet上下文初始化参数控制。可以通过将`org.apache.tomcat.websocket.STRICT_SPEC_COMPLIANCE`system属性设置为来更改默认值 ，`true`但是servlet上下文上的任何显式设置将始终优先。

使用WebSocket客户端连接到服务器端点时，建立连接时IO操作的超时由`userProperties`提供的 所控制 `javax.websocket.ClientEndpointConfig`。该属性是 `org.apache.tomcat.websocket.IO_TIMEOUT_MS`和是超时（`String`以毫秒为单位）。默认值为5000（5秒）。

使用WebSocket客户端连接到安全服务器端点时，客户端SSL配置由`userProperties` 提供的进行控制`javax.websocket.ClientEndpointConfig`。支持以下用户属性：

- `org.apache.tomcat.websocket.SSL_CONTEXT`
- `org.apache.tomcat.websocket.SSL_PROTOCOLS`
- `org.apache.tomcat.websocket.SSL_TRUSTSTORE`
- `org.apache.tomcat.websocket.SSL_TRUSTSTORE_PWD`

默认的信任库密码为`changeit`。

如果`org.apache.tomcat.websocket.SSL_CONTEXT`设置了属性，则`org.apache.tomcat.websocket.SSL_TRUSTSTORE`和 `org.apache.tomcat.websocket.SSL_TRUSTSTORE_PWD`属性将被忽略。

对于安全的服务器端点，默认情况下启用主机名验证。要绕过此验证（不推荐），必须`SSLContext`通过 `org.apache.tomcat.websocket.SSL_CONTEXT`user属性提供自定义。该自定义`SSLContext`必须配置有`TrustManager`extends 的自定义 `javax.net.ssl.X509ExtendedTrustManager`。然后可以通过各个抽象方法的适当实现来控制所需的验证（或缺少验证）。

使用WebSocket客户端连接到服务器端点时，客户端将遵循的HTTP重定向次数由 `userProperties`提供的进行控制 `javax.websocket.ClientEndpointConfig`。该属性是 org.apache.tomcat.websocket.MAX_REDIRECTIONS。默认值为20。可以通过将值配置为零来禁用重定向支持。