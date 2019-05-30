# 22.16 Spring MVC配置

[第22.2.1节“WebApplicationContext中的特殊Bean类型”](mvc.html#mvc-servlet-special-bean-types)和[第22.2.2节“Default DispatcherServlet配置”](mvc.html#mvc-servlet-config)解释了Spring MVC的特殊bean以及使用的默认实现`DispatcherServlet`。在本节中，您将了解配置Spring MVC的另外两种方法。即MVC Java配置和MVC XML命名空间。

MVC Java配置和MVC命名空间提供了类似的默认配置，可以覆盖默认配置`DispatcherServlet`。目标是使大多数应用程序不必创建相同的配置，并提供更高级别的构造，用于配置Spring MVC，作为一个简单的起点，并且需要很少或根本不需要基础配置的知识。

您可以根据自己的喜好选择MVC Java配置或MVC命名空间。另外，正如您将在下面看到的，使用MVC Java配置，更容易看到底层配置，以及直接对创建的Spring MVC bean进行细粒度自定义。但是，让我们从头开始。

### 22.16.1启用MVC Java Config或MVC XML命名空间

要启用MVC Java配置，请将注释添加`@EnableWebMvc`到其中一个 `@Configuration`类：

```java
@Configuration
@EnableWebMvc
public class WebConfig {
}
```

要在XML中实现相同的功能，请使用`mvc:annotation-driven`DispatcherServlet上下文中的元素（如果未定义DispatcherServlet上下文，则使用根上下文中的元素）：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <mvc:annotation-driven/>

</beans>
```

在上述寄存器一`RequestMappingHandlerMapping`，一个`RequestMappingHandlerAdapter`，和`ExceptionHandlerExceptionResolver`在支持带注释控制器方法的处理请求（等等）使用注释诸如`@RequestMapping`， `@ExceptionHandler`和其他。

它还支持以下功能：

1. 除了用于数据绑定的JavaBeans PropertyEditors之外，还 通过[ConversionService](validation.html#core-convert)实例进行Spring 3样式类型转换。

2. 支持使用注释[格式化](validation.html#format)数字字段。 `@NumberFormat``ConversionService`

3. 支持[的格式](validation.html#format) `Date`，`Calendar`，`Long`使用和乔达，时间字段 `@DateTimeFormat`的注释。

4. 如果类路径中存在JSR-303提供程序，则 支持[验证](mvc.html#mvc-config-validation) `@Controller`输入`@Valid`。

5. `HttpMessageConverter`支持`@RequestBody`方法参数和`@ResponseBody` 方法返回值`@RequestMapping`或`@ExceptionHandler`方法。

   这是由mvc：annotation-driven设置的HttpMessageConverters的完整列表：

   1. `ByteArrayHttpMessageConverter` 转换字节数组。
   2. `StringHttpMessageConverter` 转换字符串。
   3. `ResourceHttpMessageConverter`转换为/来自 `org.springframework.core.io.Resource`所有媒体类型。
   4. `SourceHttpMessageConverter`转换为/从`javax.xml.transform.Source`。
   5. `FormHttpMessageConverter`将表单数据转换为/从`MultiValueMap<String, String>`。
   6. `Jaxb2RootElementHttpMessageConverter` 将Java对象转换为XML或从XML转换 - 如果存在JAXB2且类路径中不存在Jackson 2 XML扩展，则添加Java对象。
   7. `MappingJackson2HttpMessageConverter` 转换为/从JSON转换 - 如果类别路径中存在Jackson 2，则添加。
   8. `MappingJackson2XmlHttpMessageConverter`转换为XML或从XML转换 - 如果类路径中存在[Jackson 2 XML扩展，](https://github.com/FasterXML/jackson-dataformat-xml)则添加 。
   9. `AtomFeedHttpMessageConverter` 转换Atom提要 - 如果类路径中存在Rome，则添加。
   10. `RssChannelHttpMessageConverter` 转换RSS提要 - 如果类路径中存在罗马则添加。

有关如何自定义这些默认转换器的更多信息[，](mvc.html#mvc-config-message-converters)请参见[第22.16.12节“消息转换器”](mvc.html#mvc-config-message-converters)。

Jackson JSON和XML转换器是使用`ObjectMapper`创建的实例创建的 [`Jackson2ObjectMapperBuilder`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/http/converter/json/Jackson2ObjectMapperBuilder.html) ，以便提供更好的默认配置。

此构建器使用以下内容自定义Jackson的默认属性：

1. [`DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES`](https://fasterxml.github.io/jackson-databind/javadoc/2.6/com/fasterxml/jackson/databind/DeserializationFeature.html#FAIL_ON_UNKNOWN_PROPERTIES) 被禁用。
2. [`MapperFeature.DEFAULT_VIEW_INCLUSION`](https://fasterxml.github.io/jackson-databind/javadoc/2.6/com/fasterxml/jackson/databind/MapperFeature.html#DEFAULT_VIEW_INCLUSION) 被禁用。

如果在类路径中检测到它们，它还会自动注册以下众所周知的模块：

1. [jackson-datatype-jdk7](https://github.com/FasterXML/jackson-datatype-jdk7)：支持Java 7类型`java.nio.file.Path`。
2. [jackson-datatype-joda](https://github.com/FasterXML/jackson-datatype-joda)：支持Joda-Time类型。
3. [jackson-datatype-jsr310](https://github.com/FasterXML/jackson-datatype-jsr310)：支持Java 8 Date＆Time API类型。
4. [jackson-datatype-jdk8](https://github.com/FasterXML/jackson-datatype-jdk8)：支持其他Java 8类型`Optional`。

### 22.16.2自定义提供的配置

要在Java中自定义默认配置，您只需实现该 `WebMvcConfigurer`接口，或者更可能扩展该类`WebMvcConfigurerAdapter` 并覆盖您需要的方法：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    // Override configuration methods...
}
```

要自定义默认配置，`<mvc:annotation-driven/>`请检查它支持的属性和子元素。您可以查看 [Spring MVC XML模式](https://schema.spring.io/mvc/spring-mvc.xsd)或使用IDE的代码完成功能来发现可用的属性和子元素。

### 22.16.3转换和格式化

默认情况下`Number`，`Date`会安装格式化程序和类型，包括对`@NumberFormat`和`@DateTimeFormat`注释的支持。如果类路径中存在Joda-Time，则还会安装对Joda-Time格式库的完全支持。要注册自定义格式化程序和转换器，请覆盖以下`addFormatters`方法：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // ...
    }
}
```

在MVC命名空间中，`<mvc:annotation-driven>`添加时应用相同的默认值。要注册自定义格式化程序和转换器，只需提供`ConversionService`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <mvc:annotation-driven conversion-service="conversionService"/>

    <bean id="conversionService"
            class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
        <property name="converters">
            <set>
                <bean class="org.example.MyConverter"/>
            </set>
        </property>
        <property name="formatters">
            <set>
                <bean class="org.example.MyFormatter"/>
                <bean class="org.example.MyAnnotationFormatterFactory"/>
            </set>
        </property>
        <property name="formatterRegistrars">
            <set>
                <bean class="org.example.MyFormatterRegistrar"/>
            </set>
        </property>
    </bean>

</beans>
```

参见[第9.6.4，“FormatterRegistrar SPI”](validation.html#format-FormatterRegistrar-SPI)和`FormattingConversionServiceFactoryBean` 关于何时使用FormatterRegistrars更多的信息。

### 22.16.4验证

Spring提供了一个[Validator接口](validation.html#validator)，可用于在应用程序的所有层中进行验证。在Spring MVC中，您可以将其配置为用作全局`Validator`实例，在遇到`@Valid`或者`@Validated`控制器方法参数时使用，和/或`Validator`通过`@InitBinder`方法在控制器中作为本地 实例使用。可以组合全局和本地验证器实例以提供复合验证。

Spring还[支持JSR-303 / JSR-349](validation.html#validation-beanvalidation-overview) Bean Validation，通过`LocalValidatorFactoryBean`它可以使Spring `org.springframework.validation.Validator` 接口适应Bean Validation `javax.validation.Validator`合同。如下所述，此类可以作为全局验证器插入Spring MVC。

默认情况下，在类路径中检测到Bean验证提供程序（如Hibernate Validator）时，使用`@EnableWebMvc`或`<mvc:annotation-driven>`自动在Spring MVC中注册Bean Validation支持`LocalValidatorFactoryBean`。

有时`LocalValidatorFactoryBean`注入控制器或其他类很方便。最简单的方法是声明自己的`@Bean`并标记它，`@Primary`以避免与MVC Java配置提供的冲突。

如果您更喜欢使用MVC Java配置中的那个，则需要覆盖该 `mvcValidator`方法`WebMvcConfigurationSupport`并声明该方法以显式返回`LocalValidatorFactory`而不是`Validator`。有关 如何切换以扩展所提供配置的信息，请参见[第22.16.13节“使用MVC Java Config](mvc.html#mvc-config-advanced-java)进行[高级自定义”](mvc.html#mvc-config-advanced-java)。

或者，您可以配置自己的全局`Validator`实例：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public Validator getValidator(); {
        // return "global" validator
    }
}
```

and in XML:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <mvc:annotation-driven validator="globalValidator"/>

</beans>
```

要将全局验证与本地验证相结合，只需添加一个或多个本地验证器：

```java
@Controller
public class MyController {

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(new FooValidator());
    }

}
```

使用此最小配置，只要遇到某个`@Valid`或`@Validated`方法参数，它就会被配置的验证器验证。任何验证违规都将自动`BindingResult`作为方法参数中的可访问错误公开，并且在Spring MVC HTML视图中也可以呈现。

### 22.16.5拦截器

您可以配置`HandlerInterceptors`或`WebRequestInterceptors`应用于所有传入请求或限制为特定URL路径模式。

在Java中注册拦截器的示例：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleInterceptor());
        registry.addInterceptor(new ThemeInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin/**");
        registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/secure/*");
    }

}
```

And in XML use the `<mvc:interceptors>` element:

```xml
<mvc:interceptors>
    <bean class="org.springframework.web.servlet.i18n.LocaleChangeInterceptor"/>
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <mvc:exclude-mapping path="/admin/**"/>
        <bean class="org.springframework.web.servlet.theme.ThemeChangeInterceptor"/>
    </mvc:interceptor>
    <mvc:interceptor>
        <mvc:mapping path="/secure/*"/>
        <bean class="org.example.SecurityInterceptor"/>
    </mvc:interceptor>
</mvc:interceptors>
```

### 22.16.6内容协商

您可以配置Spring MVC如何根据请求确定所请求的媒体类型。可用选项是检查文件扩展名的URL路径，检查“接受”标头，特定查询参数，或在没有请求任何内容时回退到默认内容类型。默认情况下，首先检查请求URI中的路径扩展，然后选中“Accept”标头。

在MVC的Java配置和MVC命名寄存器`json`，`xml`，`rss`，`atom`在默认情况下，如果相应的依赖都在类路径中。其他路径扩展到媒体类型映射也可以明确注册，并且还具有将它们列为白名单的效果，以便进行RFD攻击检测（有关更多详细信息，请参阅[“后缀模式匹配和RFD”一节](mvc.html#mvc-ann-requestmapping-rfd)）。

下面是通过MVC Java配置自定义内容协商选项的示例：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
    }
}
```

在MVC命名空间中，该`<mvc:annotation-driven>`元素具有一个`content-negotiation-manager`属性，该属性需要`ContentNegotiationManager` 依次创建一个 属性`ContentNegotiationManagerFactoryBean`：

```xml
<mvc:annotation-driven content-negotiation-manager="contentNegotiationManager"/>

<bean id="contentNegotiationManager" class="org.springframework.web.accept.ContentNegotiationManagerFactoryBean">
    <property name="mediaTypes">
        <value>
            json=application/json
            xml=application/xml
        </value>
    </property>
</bean>
```

如果不使用MVC Java配置或MVC命名空间，则需要创建实例`ContentNegotiationManager`并使用它来配置`RequestMappingHandlerMapping` 请求映射目的，`RequestMappingHandlerAdapter`以及 `ExceptionHandlerExceptionResolver`用于内容协商目的。

注意，`ContentNegotiatingViewResolver`现在也可以使用a配置 `ContentNegotiationManager`，因此您可以在Spring MVC中使用一个共享实例。

在更高级的情况下，配置多个`ContentNegotiationManager`实例可能会很有用，而这些 实例又可能包含自定义 `ContentNegotiationStrategy`实现。例如，你可以配置 `ExceptionHandlerExceptionResolver`一个`ContentNegotiationManager`总是解析请求的媒体类型`"application/json"`。或者，如果没有请求内容类型，您可能希望插入具有某种逻辑的自定义策略来选择默认内容类型（例如XML或JSON）。

### 22.16.7视图控制器

这是一个快捷方式，用于定义`ParameterizableViewController`在调用时立即转发到视图的快捷方式。在视图生成响应之前，如果没有要执行的Java控制器逻辑，请在静态情况下使用它。

将请求转发`"/"`到`"home"`Java中调用的视图的示例：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
}
```

在XML中使用相同的`<mvc:view-controller>`元素：

```xml
<mvc:view-controller path="/" view-name="home"/>
```

### 22.16.8 View Resolvers

MVC配置简化了视图解析器的注册。

以下是使用FreeMarker HTML模板配置内容协商视图解析的Java配置示例，以及Jackson作为`View`JSON呈现的默认设置：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.enableContentNegotiation(new MappingJackson2JsonView());
        registry.jsp();
    }
}
```

And the same in XML:

```xml
<mvc:view-resolvers>
    <mvc:content-negotiation>
        <mvc:default-views>
            <bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView"/>
        </mvc:default-views>
    </mvc:content-negotiation>
    <mvc:jsp/>
</mvc:view-resolvers>
```

但请注意，FreeMarker，Velocity，Tiles，Groovy Markup和脚本模板也需要配置底层视图技术。

MVC名称空间提供专用元素。例如使用FreeMarker：

```xml
<mvc:view-resolvers>
    <mvc:content-negotiation>
        <mvc:default-views>
            <bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView"/>
        </mvc:default-views>
    </mvc:content-negotiation>
    <mvc:freemarker cache="false"/>
</mvc:view-resolvers>

<mvc:freemarker-configurer>
    <mvc:template-loader-path location="/freemarker"/>
</mvc:freemarker-configurer>
```

在Java配置中，只需添加相应的“Configurer”bean：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.enableContentNegotiation(new MappingJackson2JsonView());
        registry.freeMarker().cache(false);
    }

    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("/WEB-INF/");
        return configurer;
    }
}
```

### 22.16.9资源服务

此选项允许`ResourceHttpRequestHandler`来自任何`Resource`位置列表的特定URL模式后的静态资源请求。这提供了一种方便的方法来从Web应用程序根目录以外的位置提供静态资源，包括类路径上的位置。该`cache-period`属性可用于设置远期未来的到期标头（1年是优化工具的建议，如Page Speed和YSlow），以便客户端更有效地使用它们。处理程序还正确地评估`Last-Modified`标头（如果存在），以便`304`适当地返回状态代码，从而避免客户端已缓存的资源的不必要开销。例如，使用`/resources/**`来自a 的URL模式提供资源请求`public-resources` 您将使用的Web应用程序根目录中的目录：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/public-resources/");
    }

}
```

在XML中也一样：

```xml
<mvc:resources mapping="/resources/**" location="/public-resources/"/>
```

要在未来1年期限内提供这些资源，以确保最大限度地使用浏览器缓存并减少浏览器发出的HTTP请求：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/public-resources/").setCachePeriod(31556926);
    }

}
```

And in XML:

```xml
<mvc:resources mapping="/resources/**" location="/public-resources/" cache-period="31556926"/>
```

有关更多详细信息，请参阅[静态资源的HTTP缓存支持](mvc.html#mvc-caching-static-resources)。

该`mapping`属性必须是可由其使用的Ant模式 `SimpleUrlHandlerMapping`，并且该`location`属性必须指定一个或多个有效的资源目录位置。可以使用逗号分隔的值列表指定多个资源位置。将按指定的顺序检查指定的位置是否存在任何给定请求的资源。例如，要从Web应用程序根目录和`/META-INF/public-web-resources/`类路径上任何jar中的已知路径启用资源，请 使用：

```java
@EnableWebMvc
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/", "classpath:/META-INF/public-web-resources/");
    }

}
```

And in XML:

```xml
<mvc:resources mapping="/resources/**" location="/, classpath:/META-INF/public-web-resources/"/>
```

在提供可能在部署新版本的应用程序时更改的资源时，建议您将版本字符串合并到用于请求资源的映射模式中，以便您可以强制客户端请求新部署的应用程序资源版本。对版本化URL的支持内置于框架中，可以通过在资源处理程序上配置资源链来启用。该链由另外一个`ResourceResolver` 实例组成，后跟一个或多个`ResourceTransformer`实例。它们可以一起提供任意分辨率和资源转换。

内置`VersionResourceResolver`可以配置不同的策略。例如，a `FixedVersionStrategy`可以使用属性，日期或其他作为版本。A `ContentVersionStrategy`使用根据资源内容计算的MD5哈希（称为“指纹识别”URL）。请注意，`VersionResourceResolver`在提供资源时，将自动将已解析的版本字符串用作HTTP ETag标头值。

`ContentVersionStrategy`是一个很好的默认选择，除非它不能使用（例如使用JavaScript模块加载器）。您可以针对不同的模式配置不同的版本策略，如下所示。请记住，计算基于内容的计算版本很昂贵，因此应在生产中启用资源链缓存。

Java配置示例;

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public-resources/")
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
    }
}
```

XML example:

```xml
<mvc:resources mapping="/resources/**" location="/public-resources/">
	<mvc:resource-chain>
		<mvc:resource-cache/>
		<mvc:resolvers>
			<mvc:version-resolver>
				<mvc:content-version-strategy patterns="/**"/>
			</mvc:version-resolver>
		</mvc:resolvers>
	</mvc:resource-chain>
</mvc:resources>
```

为了使上述工作正常，应用程序还必须使用版本呈现URL。最简单的方法是配置 `ResourceUrlEncodingFilter`包装响应并覆盖其`encodeURL`方法。这将适用于JSP，FreeMarker，Velocity以及调用响应`encodeURL`方法的任何其他视图技术。或者，应用程序也可以直接注入和使用`ResourceUrlProvider`bean，它使用MVC Java配置和MVC命名空间自动声明。

Webjars也受支持`WebJarsResourceResolver`，当`"org.webjars:webjars-locator"`库在类路径上时会自动注册。此解析器允许资源链从HTTP GET请求中解析与版本无关的库 `"GET /jquery/jquery.min.js"`将返回资源`"/jquery/1.2.0/jquery.min.js"`。它还可以通过重写模板中的资源URL来实现`<script src="/jquery/jquery.min.js"/> → <script src="/jquery/1.2.0/jquery.min.js"/>`。

### 22.16.10默认Servlet

这允许映射`DispatcherServlet`到“/”（从而覆盖容器的默认Servlet的映射），同时仍然允许容器的默认Servlet处理静态资源请求。它`DefaultServletHttpRequestHandler`使用URL映射“/ **”和相对于其他URL映射的最低优先级进行配置。

此处理程序将所有请求转发到默认Servlet。因此，重要的是它按照所有其他URL的顺序保持最后`HandlerMappings`。这会是这样的，如果你使用`<mvc:annotation-driven>`或者相反，如果你设置你自己定制的`HandlerMapping`情况下，一定要设置其`order`属性为比的下一个值`DefaultServletHttpRequestHandler`，这是`Integer.MAX_VALUE`。

要使用默认设置启用该功能，请使用：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
```

Or in XML:

```xml
<mvc:default-servlet-handler/>
```

覆盖“/”Servlet映射的警告是，`RequestDispatcher`必须通过名称而不是路径来检索默认Servlet。该 `DefaultServletHttpRequestHandler`会尝试自动检测在启动时容器中的默认的Servlet，使用大多数主要的Servlet容器（包括软件Tomcat，Jetty的GlassFish，JBoss和树脂中，WebLogic和WebSphere）已知名称的列表。如果使用不同的名称自定义配置了默认Servlet，或者在默认Servlet名称未知的情况下使用了不同的Servlet容器，则必须显式提供默认的Servlet名称，如下例所示：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable("myCustomDefaultServlet");
    }

}
```

Or in XML:

```xml
<mvc:default-servlet-handler default-servlet-name="myCustomDefaultServlet"/>
```

### 22.16.11路径匹配

这允许自定义与URL映射和路径匹配相关的各种设置。有关各个选项的详细信息，请查看 [PathMatchConfigurer](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/web/servlet/config/annotation/PathMatchConfigurer.html) API。

以下是Java配置中的示例：

```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer
            .setUseSuffixPatternMatch(true)
            .setUseTrailingSlashMatch(false)
            .setUseRegisteredSuffixPatternMatch(true)
            .setPathMatcher(antPathMatcher())
            .setUrlPathHelper(urlPathHelper());
    }

    @Bean
    public UrlPathHelper urlPathHelper() {
        //...
    }

    @Bean
    public PathMatcher antPathMatcher() {
        //...
    }

}
```

和XML一样，使用`<mvc:path-matching>`元素：

```xml
<mvc:annotation-driven>
    <mvc:path-matching
        suffix-pattern="true"
        trailing-slash="false"
        registered-suffixes-only="true"
        path-helper="pathHelper"
        path-matcher="pathMatcher"/>
</mvc:annotation-driven>

<bean id="pathHelper" class="org.example.app.MyPathHelper"/>
<bean id="pathMatcher" class="org.example.app.MyPathMatcher"/>
```

### 22.16.12消息转换器

如果您想要替换Spring MVC创建的默认转换器，或者 如果您只想自定义它们或将其他转换器添加到默认转换器 ，`HttpMessageConverter`则可以通过覆盖来实现Java配置中的 自定义。[`configureMessageConverters()`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurerAdapter.html#configureMessageConverters-java.util.List-)[`extendMessageConverters()`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurerAdapter.html#extendMessageConverters-java.util.List-)

下面是一个将Jackson JSON和XML转换器添加为自定义`ObjectMapper`而非默认转换的示例 ：

```java
@Configuration
@EnableWebMvc
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .modulesToInstall(new ParameterNamesModule());
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.xml().build()));
    }

}
```

在此示例中，`Jackson2ObjectMapperBuilder`用于为两者创建公共配置，`MappingJackson2HttpMessageConverter`并`MappingJackson2XmlHttpMessageConverter`启用缩进，自定义日期格式和[jackson-module-parameter-names](https://github.com/FasterXML/jackson-module-parameter-names)的注册， 这增加了对访问参数名称的支持（Java 8中添加的功能）。

使用Jackson XML支持启用缩进[`woodstox-core-asl`](https://search.maven.org/#search|gav|1|g%3A"org.codehaus.woodstox" AND a%3A"woodstox-core-asl") 除了[`jackson-dataformat-xml`](https://search.maven.org/#search|ga|1|a%3A"jackson-dataformat-xml")一个之外还需要 依赖。

其他有趣的杰克逊模块可用：

1. [jackson-datatype-money](https://github.com/zalando/jackson-datatype-money)：支持`javax.money`类型（非官方模块）
2. [jackson-datatype-hibernate](https://github.com/FasterXML/jackson-datatype-hibernate)：支持Hibernate特定的类型和属性（包括延迟加载方面）

也可以在XML中执行相同的操作：

```xml
<mvc:annotation-driven>
    <mvc:message-converters>
        <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
            <property name="objectMapper" ref="objectMapper"/>
        </bean>
        <bean class="org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter">
            <property name="objectMapper" ref="xmlMapper"/>
        </bean>
    </mvc:message-converters>
</mvc:annotation-driven>

<bean id="objectMapper" class="org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean"
      p:indentOutput="true"
      p:simpleDateFormat="yyyy-MM-dd"
      p:modulesToInstall="com.fasterxml.jackson.module.paramnames.ParameterNamesModule"/>

<bean id="xmlMapper" parent="objectMapper" p:createXmlMapper="true"/>
```

### 22.16.13使用MVC Java配置进行高级自定义

从上面的示例中可以看出，MVC Java配置和MVC命名空间提供了更高级别的构造，这些构造不需要深入了解为您创建的基础bean。相反，它可以帮助您专注于您的应用程序需求。但是，在某些时候，您可能需要更细粒度的控制，或者您可能只是希望了解底层配置。

实现更细粒度控制的第一步是查看为您创建的基础bean。在MVC Java配置中，您可以看到javadocs和`@Bean`方法 `WebMvcConfigurationSupport`。此类中的配置将通过`@EnableWebMvc`注释自动导入。事实上，如果你打开`@EnableWebMvc`你可以看到`@Import`声明。

更细粒度控制的下一步是在其中一个bean上自定义属性，`WebMvcConfigurationSupport`或者可能提供自己的实例。这需要两件事 - 删除`@EnableWebMvc`注释以防止导入然后从`DelegatingWebMvcConfiguration`子类 扩展`WebMvcConfigurationSupport`。这是一个例子：

```java
@Configuration
public class WebConfig extends DelegatingWebMvcConfiguration {

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        // ...
    }

    @Override
    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        // Create or let "super" create the adapter
        // Then customize one of its properties
    }

}
```

应用程序应该只有一个配置扩展`DelegatingWebMvcConfiguration` 或一个带`@EnableWebMvc`注释的类，因为它们都注册相同的底层bean。

以这种方式修改bean不会阻止您使用本节前面所示的任何更高级别的构造。`WebMvcConfigurerAdapter`子类和 `WebMvcConfigurer`实现仍在使用中。

### 22.16.14使用MVC命名空间进行高级自定义

使用MVC命名空间对为您创建的配置进行细粒度控制会更加困难。

如果确实需要这样做，而不是复制它提供的配置，请考虑配置一个`BeanPostProcessor`检测要按类型自定义的bean，然后根据需要修改其属性。例如：

```java
@Component
public class MyPostProcessor implements BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            // Modify properties of the adapter
        }
    }

}
```

请注意，`MyPostProcessor`需要将其包含在一个`<component scan/>`以便检测它，或者如果您愿意，可以使用XML bean声明显式声明它。