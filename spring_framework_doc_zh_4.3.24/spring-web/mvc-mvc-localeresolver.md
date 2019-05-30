# 22.8 本地化

Spring的体系结构的大多数部分都支持国际化，就像Spring Web MVC框架一样。`DispatcherServlet`使您能够使用客户端的区域设置自动解决消息。这是通过`LocaleResolver`对象完成的。

当请求进入时，`DispatcherServlet`查找区域设置解析程序，如果找到它，则尝试使用它来设置区域设置。使用此`RequestContext.getLocale()` 方法，您始终可以检索由区域设置解析程序解析的区域设置。

除了自动语言环境解析之外，您还可以将拦截器附加到处理程序映射（有关处理程序映射拦截器的更多信息，请参见[第22.4.1节“使用HandlerInterceptor拦截请求”](mvc.html#mvc-handlermapping-interceptor)）以在特定情况下更改语言环境，例如，基于请求中的参数。

区域设置解析器和拦截器在`org.springframework.web.servlet.i18n`包中定义， 并以正常方式在应用程序上下文中进行配置。以下是Spring中包含的语言环境解析器的选择。

### 22.8.1获取时区信息

除了获取客户端的区域设置之外，了解其时区通常也很有用。所述`LocaleContextResolver`接口提供了一个扩展`LocaleResolver`，它允许解析器提供更丰富的`LocaleContext`，其可以包括时区信息。

可用时，`TimeZone`可以使用该`RequestContext.getTimeZone()`方法获得 用户。时区信息将自动由日期/时间`Converter`和`Formatter`使用Spring注册的对象使用`ConversionService`。

### 22.8.2 AcceptHeaderLocaleResolver

此区域设置解析程序检查`accept-language`客户端（例如，Web浏览器）发送的请求中的标头。通常，此标头字段包含客户端操作系统的区域设置。*请注意，此解析程序不支持时区信息。*

### 22.8.3 CookieLocaleResolver

这个本地化解析器检查一个`Cookie`可能的客户端中，看是否有 `Locale`或`TimeZone`指定。如果是，则使用指定的详细信息。使用此区域设置解析程序的属性，您可以指定cookie的名称以及最大年龄。在下面找到定义a的示例`CookieLocaleResolver`。

```xml
<bean id="localeResolver" class="org.springframework.web.servlet.i18n.CookieLocaleResolver">

    <property name="cookieName" value="clientlanguage"/>

    <!-- in seconds. If set to -1, the cookie is not persisted (deleted when browser shuts down) -->
    <property name="cookieMaxAge" value="100000"/>

</bean>
```

**表22.4。CookieLocaleResolver属性**

| 属性         | 默认               | 描述                                                         |
| ------------ | ------------------ | ------------------------------------------------------------ |
| cookieName   | classname + LOCALE | Cookie的名称                                                 |
| cookieMaxAge | Servlet容器默认    | Cookie在客户端上保持持久的最长时间。如果指定-1，则不会保留cookie; 它只有在客户端关闭浏览器之后才可用。 |
| cookiePath   | /                  | 限制cookie对您网站某个部分的可见性。指定cookiePath后，cookie只对该路径及其下方的路径可见。 |

### 22.8.4 SessionLocaleResolver

在`SessionLocaleResolver`可以检索`Locale`并`TimeZone`从可能与用户的请求相关的会话。与此相反 `CookieLocaleResolver`，此策略将本地选择的区域设置存储在Servlet容器中`HttpSession`。因此，这些设置对于每个会话来说都是临时的，因此在每个会话终止时都会丢失。

请注意，与外部会话管理机制（如Spring Session项目）没有直接关系。这`SessionLocaleResolver`将简单地`HttpSession`针对当前评估和修改相应的属性`HttpServletRequest`。

### 22.8.5 LocaleChangeInterceptor

您可以通过添加`LocaleChangeInterceptor`到其中一个处理程序映射来启用语言环境的更改（请参见[第22.4节“处理程序映射”](mvc.html#mvc-handlermapping)）。它将检测请求中的参数并更改区域设置。它调用`setLocale()`的`LocaleResolver`是也存在于上下文。以下示例显示对`*.view`包含命名参数的所有资源的调用`siteLanguage`现在将更改区域设置。因此，例如，对以下URL的请求`https://www.sf.net/home.view?siteLanguage=nl`会将站点语言更改为荷兰语。

```xml
<bean id="localeChangeInterceptor"
        class="org.springframework.web.servlet.i18n.LocaleChangeInterceptor">
    <property name="paramName" value="siteLanguage"/>
</bean>

<bean id="localeResolver"
        class="org.springframework.web.servlet.i18n.CookieLocaleResolver"/>

<bean id="urlMapping"
        class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
    <property name="interceptors">
        <list>
            <ref bean="localeChangeInterceptor"/>
        </list>
    </property>
    <property name="mappings">
        <value>/**/*.view=someController</value>
    </property>
</bean>
```

