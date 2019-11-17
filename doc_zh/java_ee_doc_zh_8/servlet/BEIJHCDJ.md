# 14.协议升级处理

在HTTP / 1.1中，客户端可以使用`Upgrade`标头字段请求在当前连接上切换到其他协议。如果服务器接受切换到客户端指示的协议的请求，它将生成状态为101（切换协议）的HTTP响应。交换之后，客户端和服务器使用新协议进行通信。

例如，客户端可以发出HTTP请求以切换到XYZP协议，如下所示：

```
GET /xyzpresource HTTP/1.1
Host: localhost:8080
Accept: text/html
Upgrade: XYZP
Connection: Upgrade
OtherHeaderA: Value
```

客户端可以使用HTTP标头为新协议指定参数。服务器可以接受请求并生成如下响应：

```oac_no_warn
HTTP/1.1 101 Switching Protocols
Upgrade: XYZP
Connection: Upgrade
OtherHeaderB: Value

(XYZP data)
```

Java EE在servlet中支持HTTP协议升级功能，如[表18-7中所述](https://javaee.github.io/tutorial/servlets014.html#BEIBDHAG)。



**表18-7协议升级支持**

| **类或接口**         | **方法**                                                     |
| -------------------- | ------------------------------------------------------------ |
| `HttpServletRequest` | `HttpUpgradeHandler upgrade(Class handler)`升级方法开始协议升级处理。此方法实例化一个实现`HttpUpgradeHandler`接口并委托连接的类。`upgrade`当您接受来自客户端的切换协议请求时，您可以在服务方法内部调用该方法。 |
| `HttpUpgradeHandler` | `void init(WebConnection wc)``init`当servlet接受切换协议的请求时，将调用该方法。您可以实现此方法，并从`WebConnection`对象获取输入和输出流以实现新协议。 |
| `HttpUpgradeHandler` | `void destroy()``destroy`客户端断开连接时调用该方法。您实现此方法并释放与处理新协议相关的任何资源。 |
| `WebConnection`      | `ServletInputStream getInputStream()`该`getInputStream`方法提供对连接的输入流的访问。您可以 对返回的流使用[非阻塞I / O](https://javaee.github.io/tutorial/servlets013.html#BEIHICDH)来实现新协议。 |
| `WebConnection`      | `ServletOutputStream getOutputStream()`该`getOutputStream`方法提供对连接输出流的访问。您可以 对返回的流使用[非阻塞I / O](https://javaee.github.io/tutorial/servlets013.html#BEIHICDH)来实现新协议。 |

以下代码演示了如何接受来自客户端的HTTP协议升级请求：

```java
@WebServlet(urlPatterns={"/xyzpresource"})
public class XYZPUpgradeServlet extends HttpServlet {
   @Override
   public void doGet(HttpServletRequest request,
                     HttpServletResponse response) {
      if ("XYZP".equals(request.getHeader("Upgrade"))) {
         /* Accept upgrade request */
         response.setStatus(101);
         response.setHeader("Upgrade", "XYZP");
         response.setHeader("Connection", "Upgrade");
         response.setHeader("OtherHeaderB", "Value");
         /* Delegate the connection to the upgrade handler */
         XYZPUpgradeHandler = request.upgrade(XYZPUpgradeHandler.class);
         /* (the service method returns immedately) */
      } else {
         /* ... write error response ... */
      }
   }
}
```

本`XYZPUpgradeHandler`类处理的连接：

```java
public class XYZPUpgradeHandler implements HttpUpgradeHandler {
   @Override
   public void init(WebConnection wc) {
      ServletInputStream input = wc.getInputStream();
      ServletOutputStream output = wc.getOutputStream();
      /* ... implement XYZP using these streams (protocol-specific) ... */
   }
   @Override
   public void destroy() { ... }
}
```

实现的类`HttpUpgradeHandler`使用当前协议中的流使用新协议与客户端进行通信。有关`http://jcp.org/en/jsr/detail?id=369`HTTP协议升级支持的详细信息，请参见Servlet 4.0规范 。