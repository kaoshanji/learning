# 19.dukeetf示例应用程序

`dukeetf`位于tut-install / examples / web / dukeetf /目录中的示例应用程序演示了如何在Servlet中使用异步处理向Web客户端提供数据更新。该示例类似于提供有关电子交易基金（ETF）的价格和交易量的定期更新的服务。

在此解决以下主题：

- [dukeetf示例应用程序的体系结构](https://javaee.github.io/tutorial/servlets017.html#CHDBBEDA)
- [运行dukeetf示例应用程序](https://javaee.github.io/tutorial/servlets017.html#CHDHBBBI)



### dukeetf示例应用程序的体系结构

该`dukeetf`示例应用程序由一个servlet，一个企业bean和一个HTML页面组成。

- Servlet将请求放入异步模式，将其存储在队列中，并在价格和交易量的新数据可用时写入响应。
- 企业bean每秒更新一次价格和数量信息。
- HTML页面使用JavaScript代码向Servlet发出新数据请求，解析Servlet的响应，并在不重新加载页面的情况下更新价格和数量信息。

该`dukeetf`示例应用程序使用一种称为长轮询的编程模型。在传统的HTTP请求和响应模型中，用户必须发出显式请求（例如单击链接或提交表单），才能从服务器获取任何新信息，并且必须重新加载页面。长轮询为Web应用程序提供了一种机制，使Web应用程序可以使用HTTP将更新推送到客户端，而无需用户发出明确的请求。服务器异步处理连接，客户端使用JavaScript建立新连接。在此模型中，客户端在接收到新数据后立即发出新请求，并且服务器保持连接打开直到新数据可用。



#### Servlet

的`DukeETFServlet`类使用异步处理：

```java
@WebServlet(urlPatterns={"/dukeetf"}, asyncSupported=true)
public class DukeETFServlet extends HttpServlet {
...
}
```

在下面的代码中，该`init`方法初始化一个队列以容纳客户端请求，并向提供价格和数量更新的企业bean注册servlet。该`send`方法每秒被调用一次，`PriceVolumeBean`以发送更新并关闭连接：

```java
@Override
public void init(ServletConfig config) {
   /* Queue for requests */
   requestQueue = new ConcurrentLinkedQueue<>();
   /* Register with the enterprise bean that provides price/volume updates */
   pvbean.registerServlet(this);
}

/* PriceVolumeBean calls this method every second to send updates */
public void send(double price, int volume) {
   /* Send update to all connected clients */
   for (AsyncContext acontext : requestQueue) {
      try {
         String msg = String.format("%.2f / %d", price, volume);
         PrintWriter writer = acontext.getResponse().getWriter();
         writer.write(msg);
         logger.log(Level.INFO, "Sent: {0}", msg);
         /* Close the connection
          * The client (JavaScript) makes a new one instantly */
         acontext.complete();
      } catch (IOException ex) {
         logger.log(Level.INFO, ex.toString());
      }
   }
}
```

service方法将客户端请求置于异步模式，并为每个请求添加一个侦听器。侦听器以匿名类的形式实现，当servlet完成写响应或发生错误时，该匿名类将从队列中删除请求。最后，服务方法将请求添加到`init` 方法中创建的请求队列中。服务方法如下：

```java
@Override
public void doGet(HttpServletRequest request,
                  HttpServletResponse response) {
   response.setContentType("text/html");
   /* Put request in async mode */
   final AsyncContext acontext = request.startAsync();
   /* Remove from the queue when done */
   acontext.addListener(new AsyncListener() {
      public void onComplete(AsyncEvent ae) throws IOException {
         requestQueue.remove(acontext);
      }
      public void onTimeout(AsyncEvent ae) throws IOException {
         requestQueue.remove(acontext);
      }
      public void onError(AsyncEvent ae) throws IOException {
         requestQueue.remove(acontext);
      }
      public void onStartAsync(AsyncEvent ae) throws IOException {}
   });
   /* Add to the queue */
   requestQueue.add(acontext);
}
```



#### 企业Bean

该`PriceVolumeBean`班是一个企业Bean使用定时服务从容器更新价格和交易量信息，并调用servlet的`send`方法每秒一次：

```java
@Startup
@Singleton
public class PriceVolumeBean {
    /* Use the container's timer service */
    @Resource TimerService tservice;
    private DukeETFServlet servlet;
    ...

    @PostConstruct
    public void init() {
        /* Initialize the EJB and create a timer */
        random = new Random();
        servlet = null;
        tservice.createIntervalTimer(1000, 1000, new TimerConfig());
    }

    public void registerServlet(DukeETFServlet servlet) {
        /* Associate a servlet to send updates to */
        this.servlet = servlet;
    }

    @Timeout
    public void timeout() {
        /* Adjust price and volume and send updates */
        price += 1.0*(random.nextInt(100)-50)/100.0;
        volume += random.nextInt(5000) - 2500;
        if (servlet != null)
            servlet.send(price, volume);
    }
}
```

请参阅[使用计时器服务](https://javaee.github.io/tutorial/ejb-basicexamples005.html#BNBOY)在 [第37章，“运行Enterprise bean的实例”](https://javaee.github.io/tutorial/ejb-basicexamples.html#GIJRB)的计时器服务的更多信息。



#### HTML页面

HTML页面由一个表和一些JavaScript代码组成。该表包含两个从JavaScript代码引用的字段：

```html
<html xmlns="http://www.w3.org/1999/xhtml">
<head>...</head>
<body onload="makeAjaxRequest();">
  ...
  <table>
    ...
    <td id="price">--.--</td>
    ...
    <td id="volume">--</td>
    ...
  </table>
</body>
</html>
```

JavaScript代码使用`XMLHttpRequest`API，该API提供了用于在客户端和服务器之间传输数据的功能。该脚本向Servlet发出异步请求，并指定一个回调方法。当服务器提供的响应，所述回调方法在更新表中的字段，使一个新的请求。JavaScript代码如下：

```javascript
var ajaxRequest;
function updatePage() {
   if (ajaxRequest.readyState === 4) {
      var arraypv = ajaxRequest.responseText.split("/");
      document.getElementById("price").innerHTML = arraypv[0];
      document.getElementById("volume").innerHTML = arraypv[1];
      makeAjaxRequest();
   }
}
function makeAjaxRequest() {
   ajaxRequest = new XMLHttpRequest();
   ajaxRequest.onreadystatechange = updatePage;
   ajaxRequest.open("GET", "http://localhost:8080/dukeetf/dukeetf",
                    true);
   ajaxRequest.send(null);
}
```

`XMLHttpRequest`大多数现代浏览器都支持该API，并且已广泛用于Ajax Web客户端开发（异步JavaScript和XML）中。

看到[该实施例dukeetf2应用](https://javaee.github.io/tutorial/websocket011.html#BABGCEHE)在 [第19章“用于WebSocket的Java API的”](https://javaee.github.io/tutorial/websocket.html#GKJIQ5)使用的WebSocket端点实现本例的等效版本。



### 运行dukeetf示例应用程序

本节描述如何`dukeetf`使用NetBeans IDE并从命令行运行示例应用程序。

在此解决以下主题：

- [使用NetBeans IDE运行dukeetf示例应用程序](https://javaee.github.io/tutorial/servlets017.html#CHDCGCJD)
- [使用Maven运行dukeetf示例应用程序](https://javaee.github.io/tutorial/servlets017.html#CHDHHAFG)



#### 使用NetBeans IDE运行dukeetf示例应用程序

1. 确保已启动GlassFish Server（请参阅“ [启动和停止GlassFish Server”](https://javaee.github.io/tutorial/usingexamples002.html#BNADI)）。

2. 在文件菜单上，选择打开项目。

3. 在“打开项目”对话框中，导航到：

   ```oac_no_warn
   tut-install/examples/web/servlet
   ```

4. 选择`dukeetf`文件夹。

5. 单击打开项目。

6. 在“项目”选项卡中，右键单击该`dukeetf`项目，然后选择“运行”。

   此命令将应用程序生成并打包到目录中的WAR文件（`dukeetf.war`）中`target`，将其部署到服务器中，并使用以下URL启动Web浏览器窗口：

   ```oac_no_warn
   http://localhost:8080/dukeetf/
   ```

   在不同的Web浏览器中打开相同的URL，以查看两个页面如何同时获取价格和数量更新。



#### 使用Maven运行dukeetf示例应用程序

1. 确保已启动GlassFish Server（请参阅“ [启动和停止GlassFish Server”](https://javaee.github.io/tutorial/usingexamples002.html#BNADI)）。

2. 在终端窗口中，转到：

   ```oac_no_warn
   tut-install/examples/web/servlet/dukeetf/
   ```

3. 输入以下命令以部署应用程序：

   ```oac_no_warn
   mvn install
   ```

4. 打开Web浏览器窗口，然后输入以下地址：

   ```oac_no_warn
   http://localhost:8080/dukeetf/
   ```

   在不同的Web浏览器中打开相同的URL，以查看两个页面如何同时获取价格和数量更新。