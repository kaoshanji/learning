# 22.15 Servlet 容器初始化

在Servlet 3.0+环境中，您可以选择以编程方式配置Servlet容器作为替代方案或与`web.xml`文件组合。以下是注册的示例`DispatcherServlet`：

```java
import org.springframework.web.WebApplicationInitializer;

public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");

        ServletRegistration.Dynamic registration = container.addServlet("dispatcher", new DispatcherServlet(appContext));
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }

}
```

`WebApplicationInitializer`是Spring MVC提供的接口，可确保检测到您的实现并自动用于初始化任何Servlet 3容器。`WebApplicationInitializer`named 的抽象基类实现通过简单地重写方法来指定servlet映射和配置的位置，从而`AbstractDispatcherServletInitializer`更容易注册。`DispatcherServlet``DispatcherServlet`

对于使用基于Java的Spring配置的应用程序，建议使用此方法：

```java
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { MyWebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

}
```

如果使用基于XML的Spring配置，则应直接从`AbstractDispatcherServletInitializer`以下位置进行扩展 ：

```java
public class MyWebAppInitializer extends AbstractDispatcherServletInitializer {

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        return null;
    }

    @Override
    protected WebApplicationContext createServletApplicationContext() {
        XmlWebApplicationContext cxt = new XmlWebApplicationContext();
        cxt.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
        return cxt;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

}
```

`AbstractDispatcherServletInitializer`还提供了一种方便的方法来添加`Filter` 实例并将它们自动映射到`DispatcherServlet`：

```java
public class MyWebAppInitializer extends AbstractDispatcherServletInitializer {

    // ...

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] { new HiddenHttpMethodFilter(), new CharacterEncodingFilter() };
    }

}
```

每个过滤器都会根据其具体类型添加一个默认名称，并自动映射到`DispatcherServlet`。

该`isAsyncSupported`保护的方法`AbstractDispatcherServletInitializer` 提供了一个单一的地方，以使在异步支持`DispatcherServlet`并映射到它的所有过滤器。默认情况下，此标志设置为`true`。

最后，如果您需要进一步自定义`DispatcherServlet`自身，则可以覆盖该`createDispatcherServlet`方法。