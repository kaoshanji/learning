# 22.14 HTTP缓存

良好的HTTP缓存策略可以显着提高Web应用程序的性能和客户端的体验。所述`'Cache-Control'`HTTP响应报头主要是为这个负责，使用条件报头，例如沿`'Last-Modified'`和`'ETag'`。

该`'Cache-Control'`HTTP响应头劝告私有的高速缓存（如浏览器），以及他们如何缓存进一步重用HTTP响应的公共高速缓存（例如代理）。

一个[的ETag](https://en.wikipedia.org/wiki/HTTP_ETag)（实体标签）是由HTTP / 1.1兼容的Web用于在给定的URL来确定内容改变服务器返回的HTTP响应报头中。它可以被认为是`Last-Modified`标题的更复杂的后继者 。当服务器返回带有ETag标头的表示时，客户端可以在标头中的后续GET中使用此标`If-None-Match`头。如果内容未更改，则服务器返回`304: Not Modified`。

本节介绍在Spring Web MVC应用程序中配置HTTP缓存的不同选择。

### 22.14.1 Cache-Control HTTP头

Spring Web MVC支持许多用例和方法来为应用程序配置“Cache-Control”头。虽然[RFC 7234第5.2.2节](https://tools.ietf.org/html/rfc7234#section-5.2.2) 完整地描述了该标头及其可能的指令，但有几种方法可以解决最常见的情况。

Spring Web MVC在其几个API中使用配置约定 `setCachePeriod(int seconds)`：

- 甲`-1`值将不生成`'Cache-Control'`响应头。
- 一个`0`值，将使用防止缓存`'Cache-Control: no-store'`指令。
- 一个`n > 0`值将缓存对于给定的响应`n`使用秒 `'Cache-Control: max-age=n'`指令。

该[`CacheControl`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/http/CacheControl.html)生成器类简单地描述了可用的“缓存控制”指令，并使其更容易建立自己的HTTP缓存策略。构建完成后，`CacheControl`可以在几个Spring Web MVC API中接受一个实例作为参数。

```java
// Cache for an hour - "Cache-Control: max-age=3600"
CacheControl ccCacheOneHour = CacheControl.maxAge(1, TimeUnit.HOURS);

// Prevent caching - "Cache-Control: no-store"
CacheControl ccNoStore = CacheControl.noStore();

// Cache for ten days in public and private caches,
// public caches should not transform the response
// "Cache-Control: max-age=864000, public, no-transform"
CacheControl ccCustom = CacheControl.maxAge(10, TimeUnit.DAYS)
                                    .noTransform().cachePublic();
```

### 22.14.2对静态资源的HTTP缓存支持

应使用适当的`'Cache-Control'`条件标头提供静态资源，以获得最佳性能。 [配置`ResourceHttpRequestHandler`](mvc.html#mvc-config-static-resources)服务静态资源不仅可以`'Last-Modified'`通过读取文件的元数据本地写入标头，还`'Cache-Control'`可以通过正确配置来标记标头。

您可以`cachePeriod`在a上设置属性`ResourceHttpRequestHandler`或使用`CacheControl`支持更具体指令的实例：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public-resources/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
    }

}
```

And in XML:

```xml
<mvc:resources mapping="/resources/**" location="/public-resources/">
    <mvc:cache-control max-age="3600" cache-public="true"/>
</mvc:resources>
```

### 22.14.3支持控制器中的Cache-Control，ETag和Last-Modified响应头

控制器可以支持`'Cache-Control'`，`'ETag'`和/或`'If-Modified-Since'`HTTP请求; 如果`'Cache-Control'`要在响应上设置标头，确实建议这样做。这涉及计算`long`给定请求的lastModified 和/或Etag值，将其与`'If-Modified-Since'`请求标头值进行比较，并可能返回状态代码为304（未修改）的响应。

如[“使用HttpEntity”一节](mvc.html#mvc-ann-httpentity)所述，控制器可以使用`HttpEntity`类型与请求/响应进行交互 。返回的控制器`ResponseEntity`可以在响应中包含HTTP缓存信息，如下所示：

```java
@GetMapping("/book/{id}")
public ResponseEntity<Book> showBook(@PathVariable Long id) {

    Book book = findBook(id);
    String version = book.getVersion();

    return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .eTag(version) // lastModified is also available
                .body(book);
}
```

这样做不仅会在响应中包含`'ETag'`和`'Cache-Control'`标题，如果客户端发送的条件标头与Controller设置的缓存信息相匹配，它也会**将响应转换为HTTP 304 Not Modified空主体的响应**。

一种`@RequestMapping`方法也不妨支持相同的行为。这可以通过以下方式实现：

```java
@RequestMapping
public String myHandleMethod(WebRequest webRequest, Model model) {

    long lastModified = // 1. application-specific calculation

    if (request.checkNotModified(lastModified)) {
        // 2. shortcut exit - no further processing necessary
        return null;
    }

    // 3. or otherwise further request processing, actually preparing content
    model.addAttribute(...);
    return "myViewName";
}
```

这里有两个关键元素：调用`request.checkNotModified(lastModified)`和返回`null`。前者在返回之前设置适当的响应状态和标头`true`。后者与前者相结合，导致Spring MVC不再对请求进行进一步处理。

请注意，有3种变体：

- `request.checkNotModified(lastModified)`将lastModified与`'If-Modified-Since'`或`'If-Unmodified-Since'`请求标头 进行比较
- `request.checkNotModified(eTag)`将eTag与`'If-None-Match'`请求标头 进行比较
- `request.checkNotModified(eTag, lastModified)` 两者兼顾，意味着两个条件都应该有效

当接收条件`'GET'`/ `'HEAD'`请求时，`checkNotModified`将检查资源是否未被修改，如果是，则将导致`HTTP 304 Not Modified` 响应。在条件`'POST'`/ `'PUT'`/ `'DELETE'`请求的情况下，`checkNotModified` 将检查资源是否已被修改，如果已经修改，则将导致 `HTTP 409 Precondition Failed`响应以防止并发修改。

### 22.14.4浅ETag支持

Servlet过滤器提供对ETag的支持`ShallowEtagHeaderFilter`。它是一个普通的Servlet过滤器，因此可以与任何Web框架结合使用。该 `ShallowEtagHeaderFilter`滤波器产生所谓的浅ETag的（相对于深的ETag，稍后详细说明）。该过滤器缓存呈现JSP（或其他内容）的内容，产生超过其MD5哈希，并返回作为ETag头在回应中。客户端下次发送对同一资源的请求时，会使用该哈希作为`If-None-Match`值。过滤器检测到此情况，再次呈现视图，并比较两个哈希值。如果它们相等，`304`则返回a。

请注意，此策略可以节省网络带宽，但不能节省CPU，因为必须为每个请求计算完整响应。控制器级别的其他策略（如上所述）可以节省网络带宽并避免计算。

此过滤器有一个`writeWeakETag`参数，用于配置过滤器以编写弱ETag，如下所示：`W/"02a2d595e6ed9a0b24f027f2b63b134d6"`，如[RFC 7232第2.3节中](https://tools.ietf.org/html/rfc7232#section-2.3)所定义 。

您配置`ShallowEtagHeaderFilter`in `web.xml`：

```xml
<filter>
    <filter-name>etagFilter</filter-name>
    <filter-class>org.springframework.web.filter.ShallowEtagHeaderFilter</filter-class>
    <!-- Optional parameter that configures the filter to write weak ETags
    <init-param>
        <param-name>writeWeakETag</param-name>
        <param-value>true</param-value>
    </init-param>
    -->
</filter>

<filter-mapping>
    <filter-name>etagFilter</filter-name>
    <servlet-name>petclinic</servlet-name>
</filter-mapping>
```

Or in Servlet 3.0+ environments,

```java
public class MyWebAppInitializer extends AbstractDispatcherServletInitializer {

    // ...

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] { new ShallowEtagHeaderFilter() };
    }

}
```

