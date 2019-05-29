# 22.4 映射

在以前的Spring版本中，用户需要`HandlerMapping`在Web应用程序上下文中定义一个或多个 bean，以将传入的Web请求映射到适当的处理程序。通过引入带注释的控制器，您通常不需要这样做，因为它会`RequestMappingHandlerMapping`自动查找`@RequestMapping`所有`@Controller`bean 上的 注释。但是，请记住，所有`HandlerMapping`扩展的类`AbstractHandlerMapping`都具有以下可用于自定义其行为的属性：

- `interceptors`要使用的拦截器列表。[第22.4.1节“使用HandlerInterceptor拦截请求”](mvc.html#mvc-handlermapping-interceptor)`HandlerInterceptor`中讨论了s 。
- `defaultHandler` 当此处理程序映射未导致匹配处理程序时要使用的默认处理程序。
- `order`基于order属性的值（参见 `org.springframework.core.Ordered`接口），Spring对上下文中可用的所有处理程序映射进行排序，并应用第一个匹配的处理程序。
- `alwaysUseFullPath`如果`true`，Spring使用当前Servlet上下文中的完整路径来查找适当的处理程序。如果`false`（默认值），则使用当前Servlet映射中的路径。例如，如果使用Servlet映射 `/testing/*`并且`alwaysUseFullPath`属性设置为true， `/testing/viewPage.html`则使用，而如果属性设置为false，`/viewPage.html`则使用。
- `urlDecode`默认为`true`，从Spring 2.5开始。如果您希望比较编码路径，请将此标志设置为`false`。但是，`HttpServletRequest`始终以解码形式公开Servlet路径。请注意，与编码路径相比，Servlet路径不匹配。

以下示例显示如何配置拦截器：

```xml
<beans>
    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping">
        <property name="interceptors">
            <bean class="example.MyInterceptor"/>
        </property>
    </bean>
<beans>
```

### 22.4.1使用HandlerInterceptor拦截请求

Spring的处理程序映射机制包括处理程序拦截器，当您要将特定功能应用于某些请求（例如，检查主体）时，它们很有用。

位于处理程序映射中的拦截器必须`HandlerInterceptor`从 `org.springframework.web.servlet`包中实现。该接口定义了三个方法： *在*执行实际处理程序*之前*`preHandle(..)`调用; *在*执行处理程序*后*调用; 并*在完成请求完成后*调用。这三种方法应该提供足够的灵活性来进行各种预处理和后处理。`postHandle(..)``afterCompletion(..)`

该`preHandle(..)`方法返回一个布尔值。您可以使用此方法来中断或继续执行链的处理。当此方法返回时`true`，处理程序执行链将继续; 当它返回false时，`DispatcherServlet` 假定拦截器本身已经处理了请求（例如，呈现了适当的视图），并且不继续执行执行链中的其他拦截器和实际处理程序。

可以使用`interceptors`属性来配置拦截器，该属性存在于`HandlerMapping`从中扩展的所有类中`AbstractHandlerMapping`。这显示在下面的示例中：

```xml
<beans>
    <bean id="handlerMapping"
            class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping">
        <property name="interceptors">
            <list>
                <ref bean="officeHoursInterceptor"/>
            </list>
        </property>
    </bean>

    <bean id="officeHoursInterceptor"
            class="samples.TimeBasedAccessInterceptor">
        <property name="openingTime" value="9"/>
        <property name="closingTime" value="18"/>
    </bean>
</beans>
```

```java
package samples;

public class TimeBasedAccessInterceptor extends HandlerInterceptorAdapter {

    private int openingTime;
    private int closingTime;

    public void setOpeningTime(int openingTime) {
        this.openingTime = openingTime;
    }

    public void setClosingTime(int closingTime) {
        this.closingTime = closingTime;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(HOUR_OF_DAY);
        if (openingTime <= hour && hour < closingTime) {
            return true;
        }
        response.sendRedirect("https://host.com/outsideOfficeHours.html");
        return false;
    }
}
```

此映射处理的任何请求都被截获`TimeBasedAccessInterceptor`。如果当前时间不在办公时间，则会将用户重定向到静态HTML文件，例如，您只能在办公时间访问该网站。

使用`RequestMappingHandlerMapping`实际处理程序时，其实例 `HandlerMethod`标识将调用的特定控制器方法。

如您所见，Spring适配器类`HandlerInterceptorAdapter`可以更容易地扩展`HandlerInterceptor`接口。

在上面的示例中，配置的拦截器将应用于使用带注释的控制器方法处理的所有请求。如果要缩小拦截器应用的URL路径，可以使用MVC命名空间或MVC Java配置，或声明类型的bean实例`MappedInterceptor`来执行此操作。请参见[第22.16.1节“启用MVC Java配置或MVC XML命名空间”](mvc.html#mvc-config-enable)。

请注意，该`postHandle`方法`HandlerInterceptor`并不总是非常适合`@ResponseBody`与`ResponseEntity`方法一起使用。在这种情况下，调用`HttpMessageConverter` 之前写入和提交响应，`postHandle`这使得无法更改响应，例如添加标头。相反，应用程序可以实现 `ResponseBodyAdvice`并将其声明为`@ControllerAdvice`bean或直接对其进行配置`RequestMappingHandlerAdapter`。