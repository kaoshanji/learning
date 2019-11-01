# 16.代理

### 介绍

使用Tomcat的标准配置，Web应用程序可以要求将请求定向到的服务器名称和端口号进行处理。当Tomcat通过[HTTP / 1.1连接器](http://tomcat.apache.org/tomcat-9.0-doc/config/http.html)独立运行时 ，通常会报告请求中指定的服务器名称以及**连接器**正在侦听的端口号。为此，感兴趣的Servlet API调用是：

- `ServletRequest.getServerName()`：返回请求发送到的服务器的主机名。
- `ServletRequest.getServerPort()`：返回请求发送到的服务器的端口号。
- `ServletRequest.getLocalName()`：返回在其上接收到请求的Internet协议（IP）接口的主机名。
- `ServletRequest.getLocalPort()`：返回接收请求的接口的Internet协议（IP）端口号。

当您在代理服务器（或配置为行为类似于代理服务器的Web服务器）后面运行时，有时您会更喜欢管理这些调用返回的值。特别是，您通常希望端口号反映原始请求中指定的端口号，而不是**连接器**本身正在侦听的端口号。您可以 在元素上使用`proxyName`和`proxyPort`属性`<Connector>`来配置这些值。

代理支持可以采取多种形式。以下各节描述了几种常见情况的代理配置。

### Apache httpd代理支持

Apache httpd 1.3和更高版本支持一个可选模块（`mod_proxy`），该模块将Web服务器配置为充当代理服务器。这可用于将对特定Web应用程序的请求转发到Tomcat实例，而无需配置Web连接器（例如） `mod_jk`。为此，您需要执行以下任务：

1. 配置您的Apache副本，使其包含该 `mod_proxy`模块。如果您是从源代码构建的，则最简单的方法是`--enable-module=proxy`在`./configure`命令行中包含 指令 。

2. 如果尚未为您添加文件`mod_proxy`，请通过在`httpd.conf`文件中使用以下指令来确保在Apache启动时加载 模块：

   ```
   LoadModule proxy_module  {path-to-modules}/mod_proxy.so
   ```

3. `httpd.conf`对于要转发到Tomcat的每个Web应用程序，在文件中包括两个指令。例如，要在上下文路径下转发应用程序`/myapp`：

   ```bash
   ProxyPass         /myapp  http://localhost:8081/myapp
   ProxyPassReverse  /myapp  http://localhost:8081/myapp
   ```

   它告诉Apache将表单的URL转发 `http://localhost/myapp/*`到侦听端口8081的Tomcat连接器。

4. 将Tomcat副本配置为包含特殊 `<Connector>`元素以及适当的代理设置，例如：

   ```xml
   <Connector port="8081" ...
                 proxyName="www.mycompany.com"
                 proxyPort="80"/>
   ```

   这将导致此Web应用程序中的servlet认为所有代理请求都定向到`www.mycompany.com` 端口80。

5. `proxyName`从`<Connector>`元素中省略属性是合法的 。如果这样做，则由`request.getServerName()`运行Tomcat的主机名返回的值will。在上面的示例中，它将为 `localhost`。

6. 如果您还`<Connector>`监听8080端口（位于同一[Service](http://tomcat.apache.org/tomcat-9.0-doc/config/service.html) 元素内），则对任一端口的请求将共享同一组虚拟主机和Web应用程序。

7. 您可能希望使用操作系统的IP筛选功能来限制到端口8081的连接（在本示例中）**仅**允许运行Apache的服务器允许。

8. 另外，您可以设置一系列只能通过代理使用的Web应用程序，如下所示：

   - 配置另一个`<Service>`仅包含一个`<Connector>`用于代理端口的端口。
   - 为可通过代理访问的虚拟主机和Web应用程序配置适当的[Engine](http://tomcat.apache.org/tomcat-9.0-doc/config/engine.html)， [Host](http://tomcat.apache.org/tomcat-9.0-doc/config/host.html)和 [Context](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)元素。
   - （可选）使用IP过滤器保护端口8081，如前所述。

9. 当请求由Apache代理时，Web服务器会将这些请求记录在其访问日志中。因此，通常将需要禁用Tomcat本身执行的所有访问日志记录。

当以这种方式代理请求时，Tomcat将处理对已配置Web应用程序的**所有**请求（包括对静态内容的请求）。您可以使用`mod_jk`Web连接器代替来提高性能 `mod_proxy`。 `mod_jk`可以进行配置，以使Web服务器提供静态内容，而该静态内容未被Web应用程序的部署描述符（`/WEB-INF/web.xml`）中定义的过滤器或安全性约束处理。