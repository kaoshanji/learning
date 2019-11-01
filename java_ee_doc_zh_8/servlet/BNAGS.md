# 10.完成Servlet

Web容器可以确定应从服务中删除Servlet（例如，当容器要回收内存资源时或在其关闭时）。在这种情况下，容器将调用接口的 `destroy`方法`Servlet`。在这种方法中，您释放了servlet正在使用的所有资源并保存了所有持久状态。该`destroy`方法释放该 方法中创建的数据库对象`init` 。

删除servlet时，servlet的服务方法都应该完整。服务器尝试通过`destroy`仅在所有服务请求都返回之后或在特定于服务器的宽限期之后（以先到者为准）来调用方法来确保这一点。如果您的Servlet所执行的操作的运行时间可能长于服务器的宽限期，`destroy`则调用时这些操作仍可以运行。您必须确保所有仍在处理客户端请求的线程都已完成。

本节的其余部分说明了如何执行以下操作。

- 跟踪当前有多少线程正在运行该`service` 方法。
- 通过使`destroy`方法通知长时间运行的线程关闭并等待它们完成来提供干净的关闭。
- 让长时间运行的方法定期轮询以检查是否关闭，并在必要时停止工作，清理并返回。



### 跟踪服务请求

跟踪服务请求：

1. 在Servlet类中包含一个字段，用于计算正在运行的服务方法的数量。

   该字段应具有同步的访问方法，以递增，递减和返回其值：

   ```java
   public class ShutdownExample extends HttpServlet {
       private int serviceCounter = 0;
       ...
       // Access methods for serviceCounter
       protected synchronized void enteringServiceMethod() {
           serviceCounter++;
       }
       protected synchronized void leavingServiceMethod() {
           serviceCounter--;
       }
       protected synchronized int numServices() {
           return serviceCounter;
       }
   }
   ```

   `service`每次输入方法时，该方法应增加服务计数器，而每次方法返回时，应减少计数器。这是您的`HttpServlet`子类应重写该`service`方法的几次。应该调用新方法 `super.service`来保留原始`service` 方法的功能：

   ```java
   protected void service(HttpServletRequest req,
                          HttpServletResponse resp)
                          throws ServletException,IOException {
       enteringServiceMethod();
       try {
           super.service(req, resp);
       } finally {
           leavingServiceMethod();
       }
   }
   ```



### 通知关机方法

为确保彻底关闭，在`destroy`所有服务请求完成之前，您的方法不应释放任何共享资源：

1. 检查服务柜台。

2. 通知长时间运行的方法该关闭了。

   对于此通知，需要另一个字段。该字段应具有通常的访问方法：

   ```java
   public class ShutdownExample extends HttpServlet {
       private boolean shuttingDown;
       ...
       //Access methods for shuttingDown
       protected synchronized void setShuttingDown(boolean flag) {
           shuttingDown = flag;
       }
       protected synchronized boolean isShuttingDown() {
           return shuttingDown;
       }
   }
   ```

   这是`destroy`使用这些字段提供干净关闭的方法的示例：

   ```java
   public void destroy() {
       /* Check to see whether there are still service methods /*
       /* running, and if there are, tell them to stop. */
       if (numServices()> 0) {
           setShuttingDown(true);
       }
   
       /* Wait for the service methods to stop. */
       while (numServices()> 0) {
           try {
               Thread.sleep(interval);
           } catch (InterruptedException e) {
           }
       }
   }
   ```



### 创建礼貌的长期运行方法

提供干净关闭的最后一步是使所有长时间运行的方法都表现得礼貌。可能长时间运行的方法应检查通知关闭的字段的值，并在必要时中断其工作：

```java
public void doPost(...) {
    ...
    for(i = 0; ((i < lotsOfStuffToDo) &&
         !isShuttingDown()); i++) {
        try {
            partOfLongRunningOperation(i);
        } catch (InterruptedException e) {
            ...
        }
    }
}
```