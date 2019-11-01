# 13.非阻塞I / O

应用程序服务器中的Web容器通常根据每个客户端请求使用服务器线程。要开发可伸缩的Web应用程序，必须确保与客户端请求关联的线程永远不会闲置等待阻塞操作完成。 [异步处理](https://javaee.github.io/tutorial/servlets012.html#BEIGCFDF)提供了一种机制在一个新的线程来执行特定应用的阻挡操作，返回与立即请求到容器相关联的线程。即使您对服务方法中的所有特定于应用程序的阻塞操作使用异步处理，由于输入/输出的考虑，与客户端请求关联的线程也可能暂时处于空闲状态。

例如，如果客户端通过缓慢的网络连接提交大型HTTP POST请求，则服务器读取请求的速度比客户端提供请求的速度快。使用传统的I / O，与此请求关联的容器线程有时会处于空闲状态，以等待请求的其余部分。

在异步模式下处理请求时，Java EE为Servlet和过滤器提供了非阻塞I / O支持。以下步骤总结了如何使用非阻塞I / O处理请求并在服务方法内写入响应。

1. 如[异步处理中](https://javaee.github.io/tutorial/servlets012.html#BEIGCFDF)所述，将请求置于异步模式 。
2. 在服务方法中从请求和响应对象获取输入流和/或输出流。
3. 将读取侦听器分配给输入流，和/或将写入侦听器分配给输出流。
4. 在侦听器的回调方法中处理请求和响应。

[表18-4](https://javaee.github.io/tutorial/servlets013.html#BEIFDICJ)和[表18-5](https://javaee.github.io/tutorial/servlets013.html#BEIFIIIH)描述了servlet输入和输出流中可用于无阻塞I / O支持的方法。[表18-6](https://javaee.github.io/tutorial/servlets013.html#BEIFGJCG)说明了读取侦听器和写入侦听器的接口。



**表18-4 javax.servlet.ServletInputStream中的非阻塞I / O支持**

| **方法**                                | **描述**                                                     |
| --------------------------------------- | ------------------------------------------------------------ |
| `void setReadListener(ReadListener rl)` | 将此输入流与包含用于异步读取数据的回调方法的侦听器对象相关联。您可以将侦听器对象作为匿名类提供，或使用其他机制将输入流传递给读取的侦听器对象。 |
| `boolean isReady()`                     | 如果可以不阻塞地读取数据，则返回true。                       |
| `boolean isFinished()`                  | 读取所有数据后，返回true。                                   |



**表18-5 javax.servlet.ServletOutputStream中的非阻塞I / O支持**

| **方法**                                  | **描述**                                                     |
| ----------------------------------------- | ------------------------------------------------------------ |
| `void setWriteListener(WriteListener wl)` | 将此输出流与包含用于异步写入数据的回调方法的侦听器对象相关联。您可以将写侦听器对象作为匿名类提供，或使用其他机制将输出流传递给写侦听器对象。 |
| `boolean isReady()`                       | 如果可以不阻塞地写入数据，则返回true。                       |



**表18-6用于无阻塞I / O支持的侦听器接口**

| **接口**        | **方法**                                                     | **描述**                                                     |
| --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `ReadListener`  | `void onDataAvailable()``void onAllDataRead()``void onError(Throwable t)` | 一`ServletInputStream`当有可用来读取数据，当所有的数据已被读取时，或者当有错误实例调用它的监听这些方法。 |
| `WriteListener` | `void onWritePossible()``void onError(Throwable t)`          | 一个`ServletOutputStream`实例调用它的监听器，这些方法时，可以在不阻断或当有错误写入数据。 |



### 使用非阻塞I / O读取大型HTTP POST请求

本节中的代码显示如何通过将请求置于异步模式（如“ [异步处理”中所述](https://javaee.github.io/tutorial/servlets012.html#BEIGCFDF)）并使用[表18-4](https://javaee.github.io/tutorial/servlets013.html#BEIFDICJ) 和[表18-6中](https://javaee.github.io/tutorial/servlets013.html#BEIFGJCG)的非阻塞I / O功能，来读取Servlet中的大型HTTP POST请求。

```oac_no_warn
@WebServlet(urlPatterns={"/asyncioservlet"}, asyncSupported=true)
public class AsyncIOServlet extends HttpServlet {
   @Override
   public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
                      throws IOException {
      final AsyncContext acontext = request.startAsync();
      final ServletInputStream input = request.getInputStream();

      input.setReadListener(new ReadListener() {
         byte buffer[] = new byte[4*1024];
         StringBuilder sbuilder = new StringBuilder();
         @Override
         public void onDataAvailable() {
            try {
               do {
                  int length = input.read(buffer);
                  sbuilder.append(new String(buffer, 0, length));
               } while(input.isReady());
            } catch (IOException ex) { ... }
         }
         @Override
         public void onAllDataRead() {
            try {
               acontext.getResponse().getWriter()
                                     .write("...the response...");
            } catch (IOException ex) { ... }
            acontext.complete();
         }
         @Override
         public void onError(Throwable t) { ... }
      });
   }
}
```

本示例使用`@WebServlet`注释参数声明具有异步支持的Web servlet `asyncSupported=true`。服务方法首先通过调用`startAsync()`请求对象的方法将请求置于异步模式，这是使用非阻塞I / O所必需的。然后，服务方法获得与请求关联的输入流，并分配一个定义为内部类的读取侦听器。侦听器在请求的各个部分可用时读取它们，然后在完成读取请求后向客户端写入一些响应。