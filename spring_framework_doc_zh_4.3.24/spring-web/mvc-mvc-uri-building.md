# 22.7 构建URIs

Spring MVC提供了一种使用`UriComponentsBuilder`和构建和编码URI的机制 `UriComponents`。

例如，您可以展开和编码URI模板字符串：

```java
UriComponents uriComponents = UriComponentsBuilder.fromUriString(
        "https://example.com/hotels/{hotel}/bookings/{booking}").build();

URI uri = uriComponents.expand("42", "21").encode().toUri();
```

请注意，这`UriComponents`是不可变的，`expand()`并且`encode()`操作会在必要时返回新实例。

您还可以使用单个URI组件进行扩展和编码：

```java
UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("http").host("example.com").path("/hotels/{hotel}/bookings/{booking}").build()
        .expand("42", "21")
        .encode();
```

在Servlet环境中，`ServletUriComponentsBuilder`子类提供静态工厂方法来从Servlet请求中复制可用的URL信息：

```java
HttpServletRequest request = ...

// Re-use host, scheme, port, path and query string
// Replace the "accountId" query param

ServletUriComponentsBuilder ucb = ServletUriComponentsBuilder.fromRequest(request)
        .replaceQueryParam("accountId", "{id}").build()
        .expand("123")
        .encode();
```

或者，您可以选择复制可用信息的子集，包括上下文路径：

```java
// Re-use host, port and context path
// Append "/accounts" to the path

ServletUriComponentsBuilder ucb = ServletUriComponentsBuilder.fromContextPath(request)
        .path("/accounts").build()
```

或者在`DispatcherServlet`按名称（例如`/main/*`）映射的情况下，您还可以包含servlet映射的文字部分：

```java
// Re-use host, port, context path
// Append the literal part of the servlet mapping to the path
// Append "/accounts" to the path

ServletUriComponentsBuilder ucb = ServletUriComponentsBuilder.fromServletMapping(request)
        .path("/accounts").build()
```

### 22.7.1为控制器和方法构建URI

Spring MVC提供了一种准备控制器方法链接的机制。例如，以下MVC控制器可以轻松地创建链接：

```java
@Controller
@RequestMapping("/hotels/{hotel}")
public class BookingController {

    @GetMapping("/bookings/{booking}")
    public ModelAndView getBooking(@PathVariable Long booking) {
        // ...
    }
}
```

您可以通过按名称引用方法来准备链接：

```java
UriComponents uriComponents = MvcUriComponentsBuilder
    .fromMethodName(BookingController.class, "getBooking", 21).buildAndExpand(42);

URI uri = uriComponents.encode().toUri();
```

在上面的例子中，我们提供了实际的方法参数值，在这种情况下是长值21，用作路径变量并插入到URL中。此外，我们提供值42以填充任何剩余的URI变量，例如从类型级请求映射继承的“hotel”变量。如果方法有更多参数，则可以为URL不需要的参数提供null。一般而言`@PathVariable`，`@RequestParam`参数与构造URL相关。

还有其他使用方法`MvcUriComponentsBuilder`。例如，您可以使用类似于通过代理进行模拟测试的技术，以避免按名称引用控制器方法（该示例假定为静态导入`MvcUriComponentsBuilder.on`）：

```java
UriComponents uriComponents = MvcUriComponentsBuilder
    .fromMethodCall(on(BookingController.class).getBooking(21)).buildAndExpand(42);

URI uri = uriComponents.encode().toUri();
```

当控制器方法签名被认为可用于创建链接时，它们的设计受到限制`fromMethodCall`。除了需要适当的参数签名之外，返回类型还存在技术限制：即为链接构建器调用生成运行时代理，因此返回类型不能是`final`。特别是，`String`视图名称的常见返回类型在此处不起作用; 使用`ModelAndView` 甚至普通`Object`（带有`String`返回值）代替。

以上示例使用静态方法`MvcUriComponentsBuilder`。在内部，它们依赖于`ServletUriComponentsBuilder`从当前请求的方案，主机，端口，上下文路径和servlet路径准备基本URL。这在大多数情况下效果很好，但有时可能不够。例如，您可能在请求的上下文之外（例如，准备链接的批处理）或者您可能需要插入路径前缀（例如，从请求路径中删除并需要重新插入链接的区域设置前缀）。

对于这种情况，您可以使用静态“fromXxx”重载方法来接受 `UriComponentsBuilder`使用基本URL。或者，您可以`MvcUriComponentsBuilder` 使用基本URL 创建实例，然后使用基于实例的“withXxx”方法。例如：

```java
UriComponentsBuilder base = ServletUriComponentsBuilder.fromCurrentContextPath().path("/en");
MvcUriComponentsBuilder builder = MvcUriComponentsBuilder.relativeTo(base);
builder.withMethodCall(on(BookingController.class).getBooking(21)).buildAndExpand(42);

URI uri = uriComponents.encode().toUri();
```

### 22.7.2使用“转发”和“X-Forwarded- *”标题

当请求通过负载平衡器等代理时，主机，端口和方案可能会发生变化，这对需要创建资源链接的应用程序提出了挑战，因为链接应该反映原始请求的主机，端口和方案，如下所示。客户的观点。

[RFC 7239](https://tools.ietf.org/html/rfc7239)为代理定义了“Forwarded”HTTP标头，用于提供有关原始请求的信息。还有其他非标准标题正在使用中，例如“X-Forwarded-Host”，“X-Forwarded-Port”和“X-Forwarded-Proto”。

既`ServletUriComponentsBuilder`和`MvcUriComponentsBuilder`检测，提取，并从“转发”头，或从“X -转发，主机”使用信息，“X-转发端口”和“X-转发，原”如果“转发”不存在，以便生成的链接反映原始请求。

在`ForwardedHeaderFilter`提供了一个替代方案为整个应用程序做同样的曾经和全球。过滤器包装请求以覆盖主机，端口和方案信息，并“隐藏”任何转发的标头以供后续处理。

请注意，使用转发标头时需要考虑安全性，如RFC 7239的第8节所述。在应用程序级别，很难确定转发的标头是否可信。这就是为什么应该正确配置网络上游以从外部过滤掉不受信任的转发报头。

没有代理但不需要使用转发标头的应用程序可以配置`ForwardedHeaderFilter`删除和忽略此类标头。

### 22.7.3从视图构建控制器和方法的URI

您还可以从JSP，Thymeleaf，FreeMarker等视图构建带注释控制器的链接。这可以使用 通过名称引用映射的`fromMappingName`方法来完成`MvcUriComponentsBuilder`。

每个`@RequestMapping`都根据类的大写字母和完整的方法名称分配默认名称。例如，`getFoo`类中的方法`FooController` 被赋予名称“FC＃getFoo”。可以通过创建一个实例`HandlerMethodMappingNamingStrategy`并将其插入您的 策略来替换或自定义此策略`RequestMappingHandlerMapping`。默认策略实现还会查看name属性`@RequestMapping`并使用该属性（如果存在）。这意味着如果分配的默认映射名称与另一个（例如重载方法）冲突，您可以在其上明确指定名称`@RequestMapping`。

分配的请求映射名称在启动时记录在TRACE级别。

Spring JSP标记库提供了一个调用的函数`mvcUrl`，可用于根据此机制准备指向控制器方法的链接。

例如给出：

```java
@RequestMapping("/people/{id}/addresses")
public class PersonAddressController {

    @RequestMapping("/{country}")
    public HttpEntity getAddress(@PathVariable String country) { ... }
}
```

您可以按如下方式从JSP准备链接：

```html
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
...
<a href="${s:mvcUrl('PAC#getAddress').arg(0,'US').buildAndExpand('123')}">Get Address</a>
```

上面的例子依赖于`mvcUrl`Spring标记库中声明的JSP函数（即META-INF / spring.tld）。对于更高级的情况（例如，如上一节中所述的自定义基本URL），可以轻松定义自己的函数或使用自定义标记文件，以便使用`MvcUriComponentsBuilder`自定义基本URL 的特定实例。