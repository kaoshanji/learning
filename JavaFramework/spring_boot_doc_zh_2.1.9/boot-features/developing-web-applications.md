# 29、开发Web应用

Spring Boot非常适合于Web应用程序开发。您可以使用嵌入式Tomcat，Jetty，Undertow或Netty创建独立的HTTP服务器。大多数Web应用程序都使用该`spring-boot-starter-web`模块来快速启动和运行。您还可以选择使用该`spring-boot-starter-webflux`模块来构建反应式Web应用程序。

如果尚未开发Spring Boot Web应用程序，则可以遵循“ Hello World！”。*入门*部分中的示例。

## 29.1“ Spring Web MVC框架”

在[Spring Web MVC框架](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc)（通常简称为“Spring MVC”）是一个丰富的“模型视图控制器” Web框架。Spring MVC允许您创建特殊的`@Controller`或`@RestController`bean来处理传入的HTTP请求。控制器中的方法通过使用`@RequestMapping`注释映射到HTTP 。

以下代码显示了`@RestController`提供JSON数据的典型代码：

```java
@RestController
@RequestMapping(value="/users")
public class MyRestController {

	@RequestMapping(value="/\{user}", method=RequestMethod.GET)
	public User getUser(@PathVariable Long user) {
		// ...
	}

	@RequestMapping(value="/\{user}/customers", method=RequestMethod.GET)
	List<Customer> getUserCustomers(@PathVariable Long user) {
		// ...
	}

	@RequestMapping(value="/\{user}", method=RequestMethod.DELETE)
	public User deleteUser(@PathVariable Long user) {
		// ...
	}

}
```

Spring MVC是核心Spring Framework的一部分，有关详细信息，请参阅[参考文档](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc)。在[spring.io/guides](https://spring.io/guides)上还有一些涵盖Spring MVC的指南。

### 29.1.1 Spring MVC自动配置

Spring Boot为Spring MVC提供了自动配置，可与大多数应用程序完美配合。

自动配置在Spring的默认值之上添加了以下功能：

- 包含`ContentNegotiatingViewResolver`和`BeanNameViewResolver`。
- 支持服务静态资源，包括对WebJars的支持（[在本文档的后面部分中有介绍](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-static-content)）。
- 自动注册`Converter`，`GenericConverter`和`Formatter`豆类。
- 支持`HttpMessageConverters`（[在本文档后面介绍](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-message-converters)）。
- 自动注册`MessageCodesResolver`（[在本文档后面介绍](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-message-codes)）。
- 静态`index.html`支持。
- 定制`Favicon`支持（[在本文档后面部分中介绍](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-favicon)）。
- 自动使用`ConfigurableWebBindingInitializer`bean（[在本文档后面部分中介绍](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-web-binding-initializer)）。

如果您想保留Spring Boot MVC功能，并且想要添加其他[MVC配置](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc)（拦截器，格式化程序，视图控制器和其他功能），则可以添加自己`@Configuration`的type类，`WebMvcConfigurer`但**不添加** `@EnableWebMvc`。如果您希望提供，或的自定义实例`RequestMappingHandlerMapping`，则可以声明一个实例来提供此类组件。`RequestMappingHandlerAdapter``ExceptionHandlerExceptionResolver``WebMvcRegistrationsAdapter`

如果您想完全控制Spring MVC，可以使用添加自己的`@Configuration`注释`@EnableWebMvc`。

### 29.1.2 HttpMessageConverters

Spring MVC使用该`HttpMessageConverter`接口来转换HTTP请求和响应。开箱即用中包含明智的默认设置。例如，可以将对象自动转换为JSON（通过使用Jackson库）或XML（通过使用Jackson XML扩展（如果可用）或通过使用JAXB（如果Jackson XML扩展不可用））。默认情况下，字符串编码为`UTF-8`。

如果您需要添加或自定义转换器，则可以使用Spring Boot的`HttpMessageConverters`类，如以下清单所示：

```java
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.*;

@Configuration
public class MyConfiguration {

	@Bean
	public HttpMessageConverters customConverters() {
		HttpMessageConverter<?> additional = ...
		HttpMessageConverter<?> another = ...
		return new HttpMessageConverters(additional, another);
	}

}
```

`HttpMessageConverter`上下文中存在的任何Bean都将添加到转换器列表中。您也可以用相同的方法覆盖默认转换器。

### 29.1.3自定义JSON序列化器和反序列化器

如果使用Jackson来序列化和反序列化JSON数据，则可能需要编写自己的`JsonSerializer`和`JsonDeserializer`类。自定义序列化程序通常是[通过模块向Jackson进行注册的](https://github.com/FasterXML/jackson-docs/wiki/JacksonHowToCustomSerializers)，但是Spring Boot提供了另一种`@JsonComponent`注释，使直接注册Spring Bean更加容易。

您可以`@JsonComponent`直接在`JsonSerializer`或`JsonDeserializer`实现上使用注释。您还可以在包含序列化器/反序列化器作为内部类的类上使用它，如以下示例所示：

```java
import java.io.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import org.springframework.boot.jackson.*;

@JsonComponent
public class Example {

	public static class Serializer extends JsonSerializer<SomeObject> {
		// ...
	}

	public static class Deserializer extends JsonDeserializer<SomeObject> {
		// ...
	}

}
```

中的所有`@JsonComponent`bean都会`ApplicationContext`自动向Jackson注册。因为使用`@JsonComponent`进行了元注释`@Component`，所以适用通常的组件扫描规则。

Spring Boot还提供了[`JsonObjectSerializer`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/jackson/JsonObjectSerializer.java)和[`JsonObjectDeserializer`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/jackson/JsonObjectDeserializer.java)基类，它们在序列化对象时为标准Jackson版本提供了有用的替代方法。见[`JsonObjectSerializer`](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/jackson/JsonObjectSerializer.html)和[`JsonObjectDeserializer`](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/jackson/JsonObjectDeserializer.html)在Javadoc了解详情。

### 29.1.4 MessageCodesResolver

Spring MVC的具有产生错误代码从绑定错误的渲染错误消息的策略：`MessageCodesResolver`。如果您设置`spring.mvc.message-codes-resolver.format`属性`PREFIX_ERROR_CODE`或`POSTFIX_ERROR_CODE`，Spring Boot会为您创建一个（请参阅中的枚举[`DefaultMessageCodesResolver.Format`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/javadoc-api/org/springframework/validation/DefaultMessageCodesResolver.Format.html)）。

### 29.1.5静态内容

默认情况下，Spring Boot从类路径中名为`/static`（`/public`或`/resources`或`/META-INF/resources`）的目录或根目录提供静态内容`ServletContext`。它使用`ResourceHttpRequestHandler`Spring MVC中的from，因此您可以通过添加自己`WebMvcConfigurer`的`addResourceHandlers`方法并覆盖该方法来修改该行为。

在独立的Web应用程序中，还启用了容器中的默认servlet，并将其用作后备，从`ServletContext`Spring决定不处理它的根开始提供内容。在大多数情况下，这不会发生（除非您修改默认的MVC配置），因为Spring始终可以通过处理请求`DispatcherServlet`。

默认情况下，资源映射到`/**`，但是您可以使用`spring.mvc.static-path-pattern`属性对其进行调整。例如，将所有资源重新定位`/resources/**`可以通过以下方式实现：

```bash
spring.mvc.static-path-pattern=/resources/**
```

您还可以通过使用`spring.resources.static-locations`属性来自定义静态资源位置（用目录位置列表替换默认值）。根Servlet上下文路径，`"/"`也会自动添加为一个位置。

除了前面提到的“标准”静态资源位置，[Webjars内容也](https://www.webjars.org/)有特殊情况。`/webjars/**`如果jar文件以Webjars格式打包，则从jar文件提供带有路径的所有资源。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| `src/main/webapp`如果您的应用程序打包为jar，则不要使用该目录。尽管此目录是一个通用标准，但它**仅**与war打包一起使用，并且如果生成jar，大多数构建工具都将其忽略。 |

Spring Boot还支持Spring MVC提供的高级资源处理功能，允许使用案例，例如缓存清除静态资源或对Webjars使用版本无关的URL。

要对Webjar使用版本无关的URL，请添加`webjars-locator-core`依赖项。然后声明您的Webjar。以jQuery为例，将`"/webjars/jquery/jquery.min.js"`结果添加到Webjar版本`"/webjars/jquery/x.y.z/jquery.min.js"`所在`x.y.z`的位置。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果使用JBoss，则需要声明`webjars-locator-jboss-vfs`依赖关系而不是`webjars-locator-core`。否则，所有Webjar都将解析为`404`。 |

要使用缓存清除，以下配置为所有静态资源配置了缓存清除解决方案，从而有效地`<link href="/css/spring-2a2d595e6ed9a0b24f027f2b63b134d6.css"/>`在URL中添加了内容哈希，例如，：

```bash
spring.resources.chain.strategy.content.enabled=true
spring.resources.chain.strategy.content.paths=/**
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 由于`ResourceUrlEncodingFilter`为Thymeleaf和FreeMarker自动配置了，因此在运行时可以在模板中重写资源链接。使用JSP时，您应该手动声明此过滤器。目前尚不自动支持其他模板引擎，但可以与自定义模板宏/帮助程序一起使用，也可以使用[`ResourceUrlProvider`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/javadoc-api/org/springframework/web/servlet/resource/ResourceUrlProvider.html)。 |

例如，当使用JavaScript模块加载器动态加载资源时，不能重命名文件。这就是为什么其他策略也受支持并且可以组合的原因。“固定”策略在URL中添加静态版本字符串，而不更改文件名，如以下示例所示：

```bash
spring.resources.chain.strategy.content.enabled=true
spring.resources.chain.strategy.content.paths=/**
spring.resources.chain.strategy.fixed.enabled=true
spring.resources.chain.strategy.fixed.paths=/js/lib/
spring.resources.chain.strategy.fixed.version=v12
```

通过这种配置，位于下面的JavaScript模块`"/js/lib/"`使用固定的版本控制策略（`"/v12/js/lib/mymodule.js"`），而其他资源仍使用内容版本（`<link href="/css/spring-2a2d595e6ed9a0b24f027f2b63b134d6.css"/>`）。

请参阅[`ResourceProperties`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/ResourceProperties.java)以获取更多受支持的选项。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 专门的[博客文章](https://spring.io/blog/2014/07/24/spring-framework-4-1-handling-static-web-resources)和Spring Framework的[参考文档中](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc-config-static-resources)已经对该功能进行了全面的描述。 |

### 29.1.6欢迎页面

Spring Boot支持静态和模板欢迎页面。它首先`index.html`在配置的静态内容位置中查找文件。如果未找到，则寻找`index`模板。如果找到任何一个，它将自动用作应用程序的欢迎页面。

### 29.1.7自定义图标

Spring Boot `favicon.ico`在已配置的静态内容位置和类路径的根目录（按此顺序）中查找a 。如果存在这样的文件，它将自动用作应用程序的收藏夹图标。

### 29.1.8路径匹配和内容协商

Spring MVC可以通过查看请求路径并将其匹配到应用程序中定义的映射（例如，`@GetMapping`Controller方法上的注释）来将传入的HTTP请求映射到处理程序。

Spring Boot默认选择禁用后缀模式匹配，这意味着`"GET /projects/spring-boot.json"`类似的请求将不会与`@GetMapping("/projects/spring-boot")`映射匹配。这被认为是[Spring MVC应用程序](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc-ann-requestmapping-suffix-pattern-match)的[最佳实践](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc-ann-requestmapping-suffix-pattern-match)。过去，此功能主要用于未发送正确的“ Accept”请求标头的HTTP客户端。我们需要确保将正确的内容类型发送给客户端。如今，内容协商已变得更加可靠。

还有其他处理HTTP客户端的方法，这些客户端不能始终发送正确的“ Accept”请求标头。除了使用后缀匹配，我们还可以使用查询参数来确保将诸如这样的请求`"GET /projects/spring-boot?format=json"`映射到`@GetMapping("/projects/spring-boot")`：

```bash
spring.mvc.contentnegotiation.favor-parameter=true

＃我们可以更改参数名称，默认情况下为“格式”：
＃spring.mvc.contentnegotiation.parameter-name = myparam

＃我们还可以通过以下方式注册其他文件扩展名/媒体类型：
spring.mvc.contentnegotiation.media-types.markdown=text/markdown
```

如果您了解了注意事项，但仍希望您的应用程序使用后缀模式匹配，则需要以下配置：

```bash
spring.mvc.contentnegotiation.favor-path-extension=true
spring.mvc.pathmatch.use-suffix-pattern=true
```

另外，与其打开所有后缀模式，不如仅支持已注册的后缀模式，这会更安全：

```bash
spring.mvc.contentnegotiation.favor-path-extension=true
spring.mvc.pathmatch.use-registered-suffix-pattern=true

＃您还可以通过以下方式注册其他文件扩展名/媒体类型：
＃spring.mvc.contentnegotiation.media-types.adoc = text / asciidoc
```

### 29.1.9 ConfigurableWebBindingInitializer

Spring MVC使用a `WebBindingInitializer`初始化`WebDataBinder`特定请求。如果创建自己的`ConfigurableWebBindingInitializer` `@Bean`，Spring Boot会自动将Spring MVC配置为使用它。

### 29.1.10模板引擎

除了REST Web服务之外，您还可以使用Spring MVC来提供动态HTML内容。Spring MVC支持各种模板技术，包括Thymeleaf，FreeMarker和JSP。同样，许多其他模板引擎包括他们自己的Spring MVC集成。

Spring Boot包含对以下模板引擎的自动配置支持：

- [FreeMarker](https://freemarker.apache.org/docs/)
- [Groovy](http://docs.groovy-lang.org/docs/next/html/documentation/template-engines.html#_the_markuptemplateengine)
- [Thymeleaf](https://www.thymeleaf.org/)
- [Mustache](https://mustache.github.io/)

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果可能，应避免使用JSP。将它们与嵌入式servlet容器一起使用时，存在几个[已知的限制](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-jsp-limitations)。 |

在默认配置下使用这些模板引擎之一时，将从中自动提取模板`src/main/resources/templates`。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 根据您运行应用程序的方式，IntelliJ IDEA对类路径的排序不同。与使用Maven或Gradle或从其打包的jar运行应用程序时相比，从IDE的主要方法运行应用程序的顺序会有所不同。这可能导致Spring Boot无法在类路径上找到模板。如果遇到此问题，可以在IDE中重新排序类路径，以首先放置模块的类和资源。或者，您可以配置模板前缀来搜索`templates`类路径上的每个目录，如下所示：`classpath*:/templates/`。 |

### 29.1.11错误处理

默认情况下，Spring Boot提供了`/error`一种可明智地处理所有错误的映射，并且已在servlet容器中注册为“全局”错误页面。对于机器客户端，它将生成一个JSON响应，其中包含错误，HTTP状态和异常消息的详细信息。对于浏览器客户端，存在一个“ whitelabel”错误视图，该视图以HTML格式呈现相同的数据（要对其进行自定义，请添加`View`解析为的`error`）。要完全替换默认行为，可以实现`ErrorController`并注册该类型的Bean定义，或添加类型的Bean `ErrorAttributes`以使用现有机制但替换其内容。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 在`BasicErrorController`可以用作自定义基类`ErrorController`。如果您要为新的内容类型添加处理程序（默认是`text/html`专门处理并为其他所有内容提供后备功能），则此功能特别有用。为此，请扩展`BasicErrorController`，添加一个`@RequestMapping`具有`produces`属性的公共方法，并创建一个新类型的Bean。 |

您还可以定义带有注释的类，`@ControllerAdvice`以自定义JSON文档以针对特定的控制器和/或异常类型返回，如以下示例所示：

```java
@ControllerAdvice(basePackageClasses = AcmeController.class)
public class AcmeControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(YourException.class)
	@ResponseBody
	ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable ex) {
		HttpStatus status = getStatus(request);
		return new ResponseEntity<>(new CustomErrorType(status.value(), ex.getMessage()), status);
	}

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return HttpStatus.valueOf(statusCode);
	}

}
```

在前面的示例中，如果if `YourException`由与包在同一包中定义的控制器抛出，则使用POJO `AcmeController`的JSON表示`CustomErrorType`代替该`ErrorAttributes`表示。

#### 自定义错误页面

如果要显示给定状态代码的自定义HTML错误页面，可以将文件添加到文件`/error`夹。错误页面可以是静态HTML（即添加到任何静态资源文件夹下），也可以使用模板来构建。文件名应为确切的状态代码或系列掩码。

例如，要映射`404`到静态HTML文件，您的文件夹结构如下：

```bash
src/
 +- main/
     +- java/
     |   + <source code>
     +- resources/
         +- public/
             +- error/
             |   +- 404.html
             +- <other public assets>
```

要`5xx`使用FreeMarker模板映射所有错误，您的文件夹结构如下：

```bash
src/
 +- main/
     +- java/
     |   + <source code>
     +- resources/
         +- templates/
             +- error/
             |   +- 5xx.ftl
             +- <other templates>
```

对于更复杂的映射，还可以添加实现该`ErrorViewResolver`接口的bean ，如以下示例所示：

```java
public class MyErrorViewResolver implements ErrorViewResolver {

	@Override
	public ModelAndView resolveErrorView(HttpServletRequest request,
			HttpStatus status, Map<String, Object> model) {
		// Use the request or status to optionally return a ModelAndView
		return ...
	}

}
```

您还可以使用常规的Spring MVC功能，例如[`@ExceptionHandler`方法](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc-exceptionhandlers)和[`@ControllerAdvice`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc-ann-controller-advice)。在`ErrorController`随后拿起任何未处理的异常。

#### 在Spring MVC之外映射错误页面

对于不使用Spring MVC的应用程序，您可以使用`ErrorPageRegistrar`接口直接注册`ErrorPages`。此抽象直接与基础嵌入式servlet容器一起使用，即使您没有Spring MVC也可以使用`DispatcherServlet`。

```java
@Bean
public ErrorPageRegistrar errorPageRegistrar(){
	return new MyErrorPageRegistrar();
}

// ...

private static class MyErrorPageRegistrar implements ErrorPageRegistrar {

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		registry.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/400"));
	}

}
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您`ErrorPage`使用最终由a处理的路径注册`Filter`（在某些非Spring Web框架，例如Jersey和Wicket中很常见），则`Filter`必须将其显式注册为`ERROR`调度程序，如以下示例所示： |

```java
@Bean
public FilterRegistrationBean myFilter() {
	FilterRegistrationBean registration = new FilterRegistrationBean();
	registration.setFilter(new MyFilter());
	...
	registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
	return registration;
}
```

请注意，默认值`FilterRegistrationBean`不包括`ERROR`调度程序类型。

注意：当Spring Boot部署到servlet容器时，将使用其错误页面过滤器将具有错误状态的请求转发到适当的错误页面。如果尚未提交响应，则只能将请求转发到正确的错误页面。缺省情况下，WebSphere Application Server 8.0及更高版本在成功完成servlet的服务方法后提交响应。您应该通过设置`com.ibm.ws.webcontainer.invokeFlushAfterService`为来禁用此行为`false`。

### 12.1.29 Spring HATEOAS

如果您开发使用超媒体的RESTful API，Spring Boot将为Spring HATEOAS提供自动配置，该配置可与大多数应用程序很好地兼容。自动配置取代了使用`@EnableHypermediaSupport`和注册大量Bean的需求，以简化构建基于超媒体的应用程序，包括`LinkDiscoverers`（用于客户端支持）和`ObjectMapper`配置为正确地将响应编组为所需表示形式的Bean 。的`ObjectMapper`是通过设置各种定制的`spring.jackson.*`属性，或者，如果存在的话，通过一个`Jackson2ObjectMapperBuilder`豆。

您可以使用来控制Spring HATEOAS的配置`@EnableHypermediaSupport`。请注意，这样做会禁用`ObjectMapper`前面所述的自定义。

### 29.1.13 CORS支持

[跨域资源共享](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing)（CORS）是由[大多数浏览器](https://caniuse.com/#feat=cors)实施的[W3C规范](https://www.w3.org/TR/cors/)，可让您灵活地指定授权哪种类型的跨域请求，而不是使用诸如IFRAME或JSONP之类的安全性较低，功能较弱的方法。 。

从4.2版本开始，Spring MVC [支持CORS](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#cors)。在Spring Boot应用程序中使用带有注释的[控制器方法CORS配置](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#controller-method-cors-configuration)[`@CrossOrigin`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/javadoc-api/org/springframework/web/bind/annotation/CrossOrigin.html)不需要任何特定的配置。 可以通过使用自定义方法注册bean 来定义[全局CORS配置](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#global-cors-configuration)，如以下示例所示：`WebMvcConfigurer``addCorsMappings(CorsRegistry)`

```java
@Configuration
public class MyConfiguration {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**");
			}
		};
	}
}
```

## 

## 29.3 JAX-RS和Jersey

如果您更喜欢REST端点的JAX-RS编程模型，则可以使用可用的实现之一来代替Spring MVC。 [Jersey](https://jersey.github.io/)和[Apache CXF](https://cxf.apache.org/)开箱即用。CXF需要您注册其`Servlet`或`Filter`为`@Bean`您的应用程序上下文。Jersey提供了一些本机的Spring支持，因此我们在Spring Boot中还与启动程序一起为其提供了自动配置支持。

要开始使用Jersey，请将包含`spring-boot-starter-jersey`为依赖项，然后需要使用一种`@Bean`类型`ResourceConfig`注册所有端点，如以下示例所示：

```java
@Component
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(Endpoint.class);
	}

}
```

| ![[警告]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/warning.png) |
| ------------------------------------------------------------ |
| 泽西岛对扫描可执行归档文件的支持非常有限。例如，它无法扫描在[完全可执行的jar文件中](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/deployment-install.html)或在`WEB-INF/classes`运行可执行的war文件时在包中找到的端点。为避免此限制，`packages`不应该使用该方法，并且应该使用该`register`方法分别注册端点，如前面的示例所示。 |

对于更高级的自定义，您还可以注册任意数量的实现的Bean `ResourceConfigCustomizer`。

所有注册的端点都应`@Components`带有HTTP资源注释（`@GET`及其他注释），如以下示例所示：

```java
@Component
@Path("/hello")
public class Endpoint {

	@GET
	public String message() {
		return "Hello";
	}

}
```

由于`Endpoint`是Spring `@Component`，因此其生命周期由Spring管理，您可以使用`@Autowired`批注注入依赖项，并使用`@Value`批注注入外部配置。默认情况下，Jersey servlet已注册并映射到`/*`。您可以通过添加改变映射`@ApplicationPath`到你`ResourceConfig`。

默认情况下，泽西岛被设置为在一个Servlet `@Bean`类型的`ServletRegistrationBean`命名`jerseyServletRegistration`。默认情况下，该Servlet延迟初始化，但是您可以通过设置来自定义该行为`spring.jersey.servlet.load-on-startup`。您可以通过创建自己的同名豆之一来禁用或覆盖该bean。您还可以通过设置使用过滤器而不是Servlet `spring.jersey.type=filter`（在这种情况下，`@Bean`要替换或覆盖的是`jerseyFilterRegistration`）。过滤器具有`@Order`，您可以使用设置`spring.jersey.filter.order`。可以通过`spring.jersey.init.*`指定属性映射来为servlet和过滤器注册都赋予init参数。

有一个[Jersey示例，](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-samples/spring-boot-sample-jersey)以便您可以了解如何进行设置。

## 29.4嵌入式Servlet容器支持

Spring Boot包含对嵌入式[Tomcat](https://tomcat.apache.org/)，[Jetty](https://www.eclipse.org/jetty/)和[Undertow](https://github.com/undertow-io/undertow)服务器的支持。大多数开发人员使用适当的“入门”来获取完全配置的实例。默认情况下，嵌入式服务器在port上侦听HTTP请求`8080`。

### 29.4.1 Servlet，过滤器和侦听器

使用嵌入式Servlet容器时，您可以`HttpSessionListener`使用Spring Bean或扫描Servlet组件来注册Servlet规范中的Servlet，过滤器和所有侦听器（例如）。

#### 将Servlet，过滤器和侦听器注册为Spring Bean

作为Spring bean的任何`Servlet`，`Filter`或servlet `*Listener`实例都向嵌入式容器注册。如果要`application.properties`在配置过程中引用一个值，这将特别方便。

默认情况下，如果上下文仅包含单个Servlet，则将其映射到`/`。对于多个servlet bean，bean名称用作路径前缀。过滤器映射到`/*`。

如果以公约为基础测绘不够灵活，你可以使用`ServletRegistrationBean`，`FilterRegistrationBean`以及`ServletListenerRegistrationBean`类的完全控制。

Spring Boot附带了许多可能定义Filter bean的自动配置。以下是过滤器及其相应顺序的一些示例（较低的顺序值表示较高的优先级）：

| Servlet过滤器                    | 订购                             |
| -------------------------------- | -------------------------------- |
| `OrderedCharacterEncodingFilter` | `Ordered.HIGHEST_PRECEDENCE`     |
| `WebMvcMetricsFilter`            | `Ordered.HIGHEST_PRECEDENCE + 1` |
| `ErrorPageFilter`                | `Ordered.HIGHEST_PRECEDENCE + 1` |
| `HttpTraceFilter`                | `Ordered.LOWEST_PRECEDENCE - 10` |

通常可以使无序滤豆处于无序状态。

如果需要特定的顺序，则应避免配置一个在读取请求正文的过滤器`Ordered.HIGHEST_PRECEDENCE`，因为它可能与应用程序的字符编码配置不符。如果Servlet过滤器包装了请求，则应以小于或等于的顺序对其进行配置`OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER`。

| ![[警告]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/warning.png) |
| ------------------------------------------------------------ |
| 注册`Filter`bean 时要小心，因为它们是在应用程序生命周期中很早就初始化的。如果您需要注册一个`Filter`与其他bean交互的，请考虑使用a [`DelegatingFilterProxyRegistrationBean`](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/web/servlet/DelegatingFilterProxyRegistrationBean.html)。 |

### 29.4.2 Servlet上下文初始化

嵌入式Servlet容器不会直接执行Servlet 3.0+ `javax.servlet.ServletContainerInitializer`接口或Spring的`org.springframework.web.WebApplicationInitializer`接口。这是一个有意的设计决定，旨在降低旨在在战争中运行的第三方库可能破坏Spring Boot应用程序的风险。

如果您需要在Spring Boot应用程序中执行servlet上下文初始化，则应该注册一个实现该`org.springframework.boot.web.servlet.ServletContextInitializer`接口的bean 。单一`onStartup`方法提供对的访问，`ServletContext`并且在必要时可以轻松用作现有的适配器`WebApplicationInitializer`。

#### 扫描Servlet，过滤器和侦听器

当使用嵌入式容器中，类自动登记注释有`@WebServlet`，`@WebFilter`和`@WebListener`可以通过使用被使能`@ServletComponentScan`。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| `@ServletComponentScan` 在独立的容器中无效，而是使用容器的内置发现机制。 |

### 29.4.3 ServletWebServerApplicationContext

在后台，Spring Boot `ApplicationContext`对嵌入式servlet容器使用了不同类型的支持。该`ServletWebServerApplicationContext`是一种特殊类型的`WebApplicationContext`通过搜索单说引导自身`ServletWebServerFactory`豆。通常是`TomcatServletWebServerFactory`，`JettyServletWebServerFactory`或`UndertowServletWebServerFactory`已被自动配置。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 通常，您不需要了解这些实现类。大多数应用程序都自动配置，并适当的`ApplicationContext`和`ServletWebServerFactory`以您的名义创建。 |

### 29.4.4自定义嵌入式Servlet容器

可以使用Spring `Environment`属性来配置常见的servlet容器设置。通常，您将在`application.properties`文件中定义属性。

常用服务器设置包括：

- 网络设置：侦听传入HTTP请求的端口（`server.port`），要绑定到的接口地址`server.address`，等等。
- 会话设置：会话是否为持久（`server.servlet.session.persistent`），会话超时（`server.servlet.session.timeout`），会话数据位置（`server.servlet.session.store-dir`）和会话Cookie配置（`server.servlet.session.cookie.*`）。
- 错误管理：错误页面的位置（`server.error.path`）等。
- [SSL协议](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-embedded-web-servers.html#howto-configure-ssl)
- [HTTP压缩](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-embedded-web-servers.html#how-to-enable-http-response-compression)

Spring Boot尝试尽可能多地公开通用设置，但这并不总是可能的。在这种情况下，专用名称空间提供服务器特定的自定义（请参阅`server.tomcat`和`server.undertow`）。例如，可以使用嵌入式servlet容器的特定功能配置[访问日志](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-embedded-web-servers.html#howto-configure-accesslogs)。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 有关[`ServerProperties`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/ServerProperties.java)完整列表，请参见课程。 |

#### 程序定制

如果需要以编程方式配置嵌入式servlet容器，则可以注册一个实现该`WebServerFactoryCustomizer`接口的Spring bean 。 `WebServerFactoryCustomizer`提供对的访问`ConfigurableServletWebServerFactory`，其中包括许多自定义设置方法。以下示例显示以编程方式设置端口：

```java
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomizationBean implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

	@Override
	public void customize(ConfigurableServletWebServerFactory server) {
		server.setPort(9000);
	}

}
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| `TomcatServletWebServerFactory`，`JettyServletWebServerFactory`和`UndertowServletWebServerFactory`是的专用变体`ConfigurableServletWebServerFactory`分别具有为Tomcat，码头和暗流额外定制setter方法。 |

#### 直接自定义ConfigurableServletWebServerFactory

如果前面的定制技术太有限，你可以注册`TomcatServletWebServerFactory`，`JettyServletWebServerFactory`或`UndertowServletWebServerFactory`豆你自己。

```java
@Bean
public ConfigurableServletWebServerFactory webServerFactory() {
	TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
	factory.setPort(9000);
	factory.setSessionTimeout(10, TimeUnit.MINUTES);
	factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notfound.html"));
	return factory;
}
```

提供了许多配置选项的设置器。如果您需要做一些更奇特的操作，还提供了几种受保护的方法“挂钩”。有关详细信息，请参见[源代码文档](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/web/servlet/server/ConfigurableServletWebServerFactory.html)。

### 29.4.5 JSP限制

运行使用嵌入式servlet容器（并打包为可执行档案）的Spring Boot应用程序时，JSP支持存在一些限制。

- 对于Jetty和Tomcat，如果使用战争包装，它应该可以工作。与一起启动时`java -jar`，可执行的War将起作用，并且也可部署到任何标准容器中。使用可执行jar时，不支持JSP。
- Undertow不支持JSP。
- 创建自定义`error.jsp`页面不会覆盖默认视图以进行[错误处理](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-error-handling)。 应改用[自定义错误页面](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-error-handling-custom-error-pages)。

有一个[JSP示例，](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-samples/spring-boot-sample-web-jsp)以便您可以了解如何进行设置。