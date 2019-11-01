# 2.Servlet生命周期

Servlet的生命周期由已部署Servlet的容器控制。当请求映射到servlet时，容器执行以下步骤。

1. 如果servlet实例不存在，则Web容器：
2. 加载servlet类
3. 创建servlet类的实例
4. 通过调用`init`方法来初始化Servlet实例（[创建和初始化Servlet中](BNAFU.md)涉及了[初始化](BNAFU.md)）
5. 容器调用`service`方法，传递请求和响应对象。服务方法在[编写服务方法中](BNAFV.md)进行了讨论 。

如果需要删除该servlet，则该容器通过调用servlet的`destroy`方法来最终确定该servlet 。有关更多信息，请参见 [终结Servlet](BNAGS.md)。



### 处理Servlet生命周期事件

您可以通过定义侦听器对象来监视和响应servlet生命周期中的事件，这些对象的方法在发生生命周期事件时将被调用。要使用这些侦听器对象，必须定义并指定侦听器类。



#### 定义监听器类

您将侦听器类定义为侦听器接口的实现。[表18-1](https://javaee.github.io/tutorial/servlets002.html#BNAFL)列出了可以监视的事件以及必须实现的相应接口。调用侦听器方法时，将为其传递一个事件，该事件包含适合该事件的信息。例如，`HttpSessionListener`接口中的方法 传递了`HttpSessionEvent`，其中包含`HttpSession`。



**表18-1 Servlet生命周期事件**

| **Object**  | **事件**                     | **侦听器接口和事件类**                                       |
| ----------- | ---------------------------- | ------------------------------------------------------------ |
| Web context | 初始化和销毁                 | `javax.servlet.ServletContextListener` 和 `ServletContextEvent` |
| Web context | 添加，删除或替换属性         | `javax.servlet.ServletContextAttributeListener` 和 `ServletContextAttributeEvent` |
| Session     | 创建，失效，激活，钝化和超时 | `javax.servlet.http.HttpSessionListener`， `javax.servlet.http.HttpSessionActivationListener`和 `HttpSessionEvent` |
| Session     | 添加，删除或替换属性         | `javax.servlet.http.HttpSessionAttributeListener` 和 `HttpSessionBindingEvent` |
| 请求        | Web组件已开始处理servlet请求 | `javax.servlet.ServletRequestListener` 和 `ServletRequestEvent` |
| 请求        | 添加，删除或替换属性         | `javax.servlet.ServletRequestAttributeListener` 和 `ServletRequestAttributeEvent` |

使用`@WebListener`批注定义一个侦听器，以获取特定Web应用程序上下文上各种操作的事件。带有注释的类`@WebListener`必须实现以下接口之一：

```java
javax.servlet.ServletContextListener
javax.servlet.ServletContextAttributeListener
javax.servlet.ServletRequestListener
javax.servlet.ServletRequestAttributeListener
javax.servlet..http.HttpSessionListener
javax.servlet..http.HttpSessionAttributeListener
```

例如，以下代码段定义了一个实现以下两个接口的侦听器：

```java
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener()
public class SimpleServletListener implements ServletContextListener,
        ServletContextAttributeListener {
    ...
```



### 处理Servlet错误

执行servlet时，可以发生任何数量的异常。发生异常时，Web容器将生成一个默认页面，其中包含以下消息：

```oac_no_warn
A Servlet Exception Has Occurred
```