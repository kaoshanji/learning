# 4.创建和初始化Servlet

使用`@WebServlet`注释在Web应用程序中定义Servlet组件。该注释在类中指定，并且包含有关要声明的servlet的元数据。带注释的servlet必须指定至少一个URL模式。这是通过使用 注释上的`urlPatterns`或`value`属性来完成的。所有其他属性都是可选的，具有默认设置。使用`value` 时，对注释的唯一属性是URL pattern属性; 否则，在`urlPatterns`还使用其他属性时使用该属性。

带注释`@WebServlet`的`javax.servlet.http.HttpServlet`类必须扩展 该类。例如，以下代码片段使用URL模式定义了一个servlet `/report`：

```java
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet("/report")
public class MoodServlet extends HttpServlet {
    ...
```

Web容器在加载和实例化Servlet类之后以及在传递来自客户端的请求之前初始化Servlet。要自定义此过程以允许servlet读取持久配置数据，初始化资源以及执行任何其他一次性活动，您可以覆盖 接口的`init`方法`Servlet`或指定 批注的`initParams`属性`@WebServlet`。该`initParams`属性包含一个`@WebInitParam` 注释。如果不能完成其初始化过程，一个servlet抛出`UnavailableException`。

使用初始化参数来提供特定servlet所需的数据。相反，上下文参数提供可用于Web应用程序所有组件的数据。