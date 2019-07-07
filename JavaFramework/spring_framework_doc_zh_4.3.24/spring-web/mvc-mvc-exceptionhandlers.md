# 22.11 处理异常

### 22.11.1 HandlerExceptionResolver

Spring `HandlerExceptionResolver`实现处理控制器执行期间发生的意外异常。一个`HandlerExceptionResolver`有点象异常映射的，你可以在Web应用程序描述符定义`web.xml`。但是，它们提供了一种更灵活的方法。例如，它们提供有关在抛出异常时正在执行哪个处理程序的信息。此外，处理异常的编程方式为您提供了更多选项，可以在请求转发到另一个URL之前进行适当的响应（与使用Servlet特定异常映射时的最终结果相同）。

除了实现`HandlerExceptionResolver`接口，这只是实现`resolveException(Exception, Handler)`方法和返回a的问题 `ModelAndView`，您还可以使用提供的`SimpleMappingExceptionResolver`或创建 `@ExceptionHandler`方法。将`SimpleMappingExceptionResolver`让您采取可能被抛出的异常的类名，并将它映射到视图名。这在功能上等同于Servlet API的异常映射功能，但也可以实现来自不同处理程序的更细粒度的异常映射。`@ExceptionHandler`另一方面，注释可用于应调用以处理异常的方法。这样的方法可以在一个本地内定义，`@Controller`或者可以`@Controller`在一个`@ControllerAdvice`类中定义时 应用于许多类。以下部分更详细地解释了这一点。

### 22.11.2 @ExceptionHandler

在`HandlerExceptionResolver`接口和`SimpleMappingExceptionResolver` 实现允许你转发到这些观点之前声明以及一些可选的Java逻辑异常对应到具体的意见。但是，在某些情况下，尤其是在依赖`@ResponseBody`方法而不是视图分辨率时，直接设置响应的状态并可选地将错误内容写入响应主体可能更方便。

你可以用`@ExceptionHandler`方法做到这一点。在控制器中声明时，此类方法适用于由`@RequestMapping`该控制器（或其任何子类）的方法引发的异常。您还可以`@ExceptionHandler`在`@ControllerAdvice`类中声明一个方法，在这种情况下，它处理`@RequestMapping` 来自许多控制器的方法的异常。下面是一个控制器本地`@ExceptionHandler`方法的示例 ：

```java
@Controller
public class SimpleController {

    // ...

    @ExceptionHandler
    public ResponseEntity<String> handle(IOException ex) {
        // ...
    }
}
```

异常可能与传播的顶级异常（即直接`IOException`抛出）相匹配，或者与顶级包装异常（例如，`IOException`包裹在内部`IllegalStateException`）中的直接原因相匹配 。

对于匹配的异常类型，最好将目标异常声明为方法参数，如上所示。当多个异常方法匹配时，根异常匹配通常优先于原因异常匹配。更具体地说，`ExceptionDepthComparator`它用于根据抛出的异常类型的深度对异常进行排序。

或者，`@ExceptionHandler`可以将值设置为异常类型数组。如果抛出与列表中的某个类型匹配的异常，则将`@ExceptionHandler`调用使用匹配进行批注的方法。如果未设置注释值，则将使用声明的方法参数类型进行匹配。

对于`@ExceptionHandler`方法，根据特定控制器或通知bean的处理程序方法中的当前异常的原因匹配根首异常匹配。但是，较高优先级的原因匹配`@ControllerAdvice` 仍然优先于较低优先级的通知bean上的任何匹配（无论是根目录还是原因级别）。因此，在使用多建议安排时，请在具有相应顺序的优先级建议bean上声明主根异常映射！

与使用`@RequestMapping`注释注释的标准控制器方法非常相似，方法的方法参数和返回值`@ExceptionHandler`可以是灵活的。例如，`HttpServletRequest`可以在Servlet环境和 `PortletRequest`Portlet环境中访问。返回类型可以是a `String`，它被解释为视图名称，`ModelAndView`对象，a `ResponseEntity`，或者您还可以添加`@ResponseBody`要使用消息转换器转换的方法返回值并将其写入响应流。

最后但并非最不重要的是，`@ExceptionHandler`方法实现可以选择通过以原始形式重新抛出它来退出处理给定的异常实例。这在您只对根级别匹配或在特定上下文中无法静态确定的匹配中感兴趣的情况下非常有用。重新抛出的异常将通过剩余的分辨率链传播，就像给定`@ExceptionHandler`方法首先不匹配一样。

### 22.11.3处理标准Spring MVC异常

Spring MVC在处理请求时可能会引发许多异常。`SimpleMappingExceptionResolver`可以根据 需要轻松地将任何异常映射到默认错误视图。但是，在与以自动方式解释响应的客户端合作时，您需要在响应上设置特定的状态代码。根据引发的异常，状态代码可能指示客户端错误（4xx）或服务器错误（5xx）。

该`DefaultHandlerExceptionResolver`转换Spring MVC的例外特定的错误状态代码。它默认注册了MVC命名空间，MVC Java配置，以及`DispatcherServlet`（即不使用MVC命名空间或Java配置时）。下面列出了此解析程序处理的一些例外情况以及相应的状态代码：

| 例外                                      | HTTP状态代码            |
| ----------------------------------------- | ----------------------- |
| `BindException`                           | 400（不良请求）         |
| `ConversionNotSupportedException`         | 500内部服务器错误）     |
| `HttpMediaTypeNotAcceptableException`     | 406（不可接受）         |
| `HttpMediaTypeNotSupportedException`      | 415（不支持的媒体类型） |
| `HttpMessageNotReadableException`         | 400（不良请求）         |
| `HttpMessageNotWritableException`         | 500内部服务器错误）     |
| `HttpRequestMethodNotSupportedException`  | 405（不允许的方法）     |
| `MethodArgumentNotValidException`         | 400（不良请求）         |
| `MissingPathVariableException`            | 500内部服务器错误）     |
| `MissingServletRequestParameterException` | 400（不良请求）         |
| `MissingServletRequestPartException`      | 400（不良请求）         |
| `NoHandlerFoundException`                 | 404（未找到）           |
| `NoSuchRequestHandlingMethodException`    | 404（未找到）           |
| `TypeMismatchException`                   | 400（不良请求）         |

将`DefaultHandlerExceptionResolver`通过设置响应的状态透明地工作。但是，如果您的应用程序可能需要在每个错误响应中添加开发人员友好的内容，例如在提供REST API时，它就不会将任何错误内容写入响应主体。你可以准备一个`ModelAndView` 和渲染通过视图解析错误内容-通过配置，即`ContentNegotiatingViewResolver`，`MappingJackson2JsonView`，等等。但是，您可能更喜欢使用`@ExceptionHandler`方法。

如果您希望通过`@ExceptionHandler`方法编写错误内容，则可以扩展 `ResponseEntityExceptionHandler`。对于`@ControllerAdvice`提供`@ExceptionHandler`处理标准Spring MVC异常和返回的方法的类，这是一个方便的基础 `ResponseEntity`。这允许您使用消息转换器自定义响应并写入错误内容。有关`ResponseEntityExceptionHandler`更多详细信息，请参阅 javadocs。

### 22.11.4使用@ResponseStatus注释业务异常

可以使用注释来处理业务异常`@ResponseStatus`。引发异常时，`ResponseStatusExceptionResolver`通过相应地设置响应的状态来处理它。默认情况下，`DispatcherServlet`寄存器 `ResponseStatusExceptionResolver`可以使用。

### 22.11.5自定义默认Servlet容器错误页面

当响应的状态设置为错误状态代码并且响应的主体为空时，Servlet容器通常会呈现HTML格式的错误页面。要自定义容器的默认错误页面，可以在中声明`<error-page>`元素`web.xml`。直到Servlet 3，该元素必须映射到特定的状态代码或异常类型。从Servlet 3开始，不需要映射错误页面，这实际上意味着指定的位置自定义默认的Servlet容器错误页面。

```xml
<error-page>
    <location>/error</location>
</error-page>
```

请注意，错误页面的实际位置可以是JSP页面或容器中的其他URL，包括通过`@Controller`方法处理的URL ：

写入错误信息时，`HttpServletResponse`可以通过控制器中的请求属性访问状态代码和设置的错误消息 ：

```java
@Controller
public class ErrorController {

    @RequestMapping(path = "/error", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Map<String, Object> handle(HttpServletRequest request) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", request.getAttribute("javax.servlet.error.status_code"));
        map.put("reason", request.getAttribute("javax.servlet.error.message"));

        return map;
    }

}
```

or in a JSP:

```jsp
<%@ page contentType="application/json" pageEncoding="UTF-8"%>
{
    status:<%=request.getAttribute("javax.servlet.error.status_code") %>,
    reason:<%=request.getAttribute("javax.servlet.error.message") %>
}
```

