# 12.异步处理

应用程序服务器中的Web容器通常根据每个客户端请求使用服务器线程。在高负载条件下，容器需要大量线程来满足所有客户端请求。可伸缩性限制包括内存不足或容器线程池用尽。要创建可伸缩的Web应用程序，必须确保没有与请求关联的线程处于空闲状态，以便容器可以使用它们来处理新请求。

在两种常见情况下，与请求关联的线程可能处于空闲状态。

- 线程需要等待资源变得可用或处理数据，然后再构建响应。例如，应用程序可能需要在产生响应之前查询从远程Web服务数据库或访问数据。
- 线程需要等待一个事件，然后才能生成响应。例如，在生成响应之前，应用程序可能必须等待JMS消息，来自另一个客户端的新信息或队列中可用的新数据。

这些方案代表了限制Web应用程序可伸缩性的阻塞操作。异步处理是指将这些阻塞操作分配给新线程，并将与请求关联的线程立即重新调整到容器。



### Servlet中的异步处理

Java EE为Servlet和过滤器提供了异步处理支持。如果Servlet或过滤器在处理请求时达到潜在的阻塞操作，则可以将操作分配给异步执行上下文，并将与请求关联的线程立即返回到容器，而不会生成响应。阻塞操作在异步执行上下文中的另一个线程中完成，该线程可以生成响应或将请求分派到另一个servlet。

要在一个servlet启用异步处理时，参数设定 `asyncSupported`到`true`所述`@WebServlet`注释如下：

```java
@WebServlet(urlPatterns={"/asyncservlet"}, asyncSupported=true)
public class AsyncServlet extends HttpServlet { ... }
```

本`javax.servlet.AsyncContext`类提供了你需要的内部服务方法进行异步处理功能。要获取的实例`AsyncContext`，请在`startAsync()`服务方法的请求对象上调用方法；例如：

```java
public void doGet(HttpServletRequest req, HttpServletResponse resp) {
   ...
   AsyncContext acontext = req.startAsync();
   ...
}
```

此呼叫并将请求放入异步模式，并确保反应不是退出服务方法之后提交。阻塞操作完成后，您必须在异步上下文中生成响应，或者将请求分派到另一个Servlet。

[表18-3](https://javaee.github.io/tutorial/servlets012.html#BEICFIEC)描述了`AsyncContext`该类提供的基本功能。



**表18-3 AsyncContext类提供的功能**

| **方法签名**                    | **描述**                                                     |
| ------------------------------- | ------------------------------------------------------------ |
| `void start(Runnable run)`      | 容器提供了一个不同的线程，可以在其中处理阻塞操作。您可以将阻塞操作的代码作为实现该`Runnable`接口的类来提供。您可以在调用`start`方法时将该类作为内部类提供，或使用其他机制将`AsyncContext`实例传递 给您的类。 |
| `ServletRequest getRequest()`   | 返回用于初始化此异步上下文的请求。在上面的示例中，请求与服务方法中的请求相同。您可以在异步上下文中使用此方法从请求中获取参数。 |
| `ServletResponse getResponse()` | 返回用于初始化此异步上下文的响应。在上面的示例中，响应与服务方法中的响应相同。可以使用异步上下文内此方法写入与阻挡操作的结果的响应。 |
| `void complete()`               | 完成异步操作并关闭与此异步上下文关联的响应。在写入异步上下文中的响应对象后，可以调用此方法。 |
| `void dispatch(String path)`    | 将请求和响应对象分派到给定路径。您可以使用此方法在阻塞操作完成后让另一个servlet写入响应。 |



### 等待资源

本节演示如何`AsyncContext`在以下用例中使用该类提供的功能 ：

1. Servlet从GET请求接收参数。
2. Servlet使用诸如数据库或Web服务之类的资源来基于参数的值检索信息。资源有时可能很慢，因此这可能是阻塞操作。
3. Servlet使用来自资源的结果生成响应。

以下代码显示了不使用异步处理的基本servlet：

```java
@WebServlet(urlPatterns={"/syncservlet"})
public class SyncServlet extends HttpServlet {
   private MyRemoteResource resource;
   @Override
   public void init(ServletConfig config) {
      resource = MyRemoteResource.create("config1=x,config2=y");
   }

   @Override
   public void doGet(HttpServletRequest request,
                     HttpServletResponse response) {
      response.setContentType("text/html;charset=UTF-8");
      String param = request.getParameter("param");
      String result = resource.process(param);
      /* ... print to the response ... */
   }
}
```

以下代码显示了使用异步处理的同一servlet：

```java
@WebServlet(urlPatterns={"/asyncservlet"}, asyncSupported=true)
public class AsyncServlet extends HttpServlet {
   /* ... Same variables and init method as in SyncServlet ... */

   @Override
   public void doGet(HttpServletRequest request,
                     HttpServletResponse response) {
      response.setContentType("text/html;charset=UTF-8");
      final AsyncContext acontext = request.startAsync();
      acontext.start(new Runnable() {
         public void run() {
            String param = acontext.getRequest().getParameter("param");
            String result = resource.process(param);
            HttpServletResponse response = acontext.getResponse();
            /* ... print to the response ... */
            acontext.complete();
   }
}
```

`AsyncServlet`添加`asyncSupported=true`到`@WebServlet` 注释中。其余的差异在服务方法内部。

- `request.startAsync()`使请求被异步处理；服务方法结束时不会将响应发送给客户端。
- `acontext.start(new Runnable() {…})` 从容器中获取一个新线程。
- `run()`内部类方法内部的代码在新线程中执行。内部类可以访问异步上下文，以从请求中读取参数并写入响应。调用`complete()`异步上下文的 方法将提交响应并将其发送到客户端。

服务的`AsyncServlet`return 方法立即返回，并在异步上下文中处理请求。