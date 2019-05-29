# 22.5 适配视图

Web应用程序的所有MVC框架都提供了一种处理视图的方法。Spring提供了视图解析器，使您可以在浏览器中呈现模型，而无需将您与特定的视图技术联系起来。开箱即用，Spring允许您使用JSP，Velocity模板和XSLT视图。请参见[第23章，*查看技术*](view.html)对于如何整合并使用不同的视图技术的讨论。

对Spring处理视图的方式很重要的两个接口是`ViewResolver` 和`View`。所述`ViewResolver`提供视图名称和实际视图之间的映射。该`View`接口处理请求的准备并将请求交给其中一种视图技术。

### 22.5.1使用ViewResolver接口解析视图

正如在讨论[第22.3节，“实施控制器”](mvc.html#mvc-controller)，在Spring Web MVC框架控制器的所有处理方法必须解析为一个逻辑视图名称，明确地（例如，通过返回 `String`，`View`或`ModelAndView`）或隐式（即基于惯例）。Spring中的视图由逻辑视图名称处理，并由视图解析器解析。Spring带有相当多的视图解析器。该表列出了其中大部分内容; 下面是几个例子。



**表22.3。查看解析器**

| 视图解析器                                        | 描述                                                         |
| ------------------------------------------------- | ------------------------------------------------------------ |
| `AbstractCachingViewResolver`                     | 缓存视图的抽象视图解析器。视图通常需要准备才能使用; 扩展此视图解析器提供缓存。 |
| `XmlViewResolver`                                 | 其实现`ViewResolver`接受使用与Spring的XML bean工厂相同的DTD以XML编写的配置文件。默认配置文件是`/WEB-INF/views.xml`。 |
| `ResourceBundleViewResolver`                      | 它的实现`ViewResolver`使用bean中的bean定义`ResourceBundle`，由bundle base name指定。通常，您在属性文件中定义捆绑包，该文件位于类路径中。默认文件名是`views.properties`。 |
| `UrlBasedViewResolver`                            | 简单实现`ViewResolver`可以直接将逻辑视图名称解析为URL的接口，而无需显式映射定义。如果您的逻辑名称以直接的方式与视图资源的名称匹配，则这是合适的，而不需要任意映射。 |
| `InternalResourceViewResolver`                    | 方便的子类`UrlBasedViewResolver`支持`InternalResourceView`（实际上，Servlet和JSP）和子类，如`JstlView`和`TilesView`。您可以使用指定此解析程序生成的所有视图的视图类 `setViewClass(..)`。有关`UrlBasedViewResolver`详细信息，请参阅javadocs。 |
| `VelocityViewResolver` / `FreeMarkerViewResolver` | 方便的子类`UrlBasedViewResolver`支持`VelocityView`（实际上，Velocity模板）或`FreeMarkerView`它们的自定义子类。 |
| `ContentNegotiatingViewResolver`                  | 实现`ViewResolver`基于请求文件名或`Accept`标头解析视图的接口。请参见[第22.5.4节“ContentNegotiatingViewResolver”](mvc.html#mvc-multiple-representations)。 |

例如，使用JSP作为视图技术，您可以使用`UrlBasedViewResolver`。此视图解析程序将视图名称转换为URL，并将请求移交给RequestDispatcher以呈现视图。

```xml
<bean id="viewResolver"
        class="org.springframework.web.servlet.view.UrlBasedViewResolver">
    <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
    <property name="prefix" value="/WEB-INF/jsp/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```

`test`作为逻辑视图名称返回时，此视图解析程序将请求转发给`RequestDispatcher`将发送请求的请求`/WEB-INF/jsp/test.jsp`。

在Web应用程序中组合不同的视图技术时，可以使用 `ResourceBundleViewResolver`：

```xml
<bean id="viewResolver"
        class="org.springframework.web.servlet.view.ResourceBundleViewResolver">
    <property name="basename" value="views"/>
    <property name="defaultParentView" value="parentView"/>
</bean>
```

在`ResourceBundleViewResolver`考察`ResourceBundle`确定了基本名字和它应该解决每个视图，它使用属性的值 `[viewname].(class)`作为视图类和属性的值`[viewname].url`作为视图的URL。示例可以在下一章中找到，其中包括视图技术。如您所见，您可以标识父视图，属性文件中的所有视图都从该视图“扩展”。这样，您可以指定默认视图类。

`AbstractCachingViewResolver`它们解析的缓存视图实例的子类。缓存可提高某些视图技术的性能。可以通过将`cache`属性设置为关闭缓存`false`。此外，如果必须在运行时刷新某个视图（例如，修改Velocity模板时），则可以使用该`removeFromCache(String viewName, Locale loc)`方法。

### 22.5.2 Chaining ViewResolvers

Spring支持多个视图解析器。因此，您可以链接解析器，例如，在某些情况下覆盖特定视图。您可以通过向应用程序上下文添加多个解析程序来链接视图解析程序，并在必要时通过设置 `order`属性来指定排序。请记住，order属性越高，视图解析器在链中的位置越晚。

在下面的示例中，视图解析器链包含两个解析器，一个 `InternalResourceViewResolver`始终自动定位为链中的最后一个解析器，以及一个`XmlViewResolver`用于指定Excel视图的解析器。不支持Excel视图`InternalResourceViewResolver`。

```xml
<bean id="jspViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
    <property name="prefix" value="/WEB-INF/jsp/"/>
    <property name="suffix" value=".jsp"/>
</bean>

<bean id="excelViewResolver" class="org.springframework.web.servlet.view.XmlViewResolver">
    <property name="order" value="1"/>
    <property name="location" value="/WEB-INF/views.xml"/>
</bean>

<!-- in views.xml -->

<beans>
    <bean name="report" class="org.springframework.example.ReportExcelView"/>
</beans>
```

如果特定视图解析器不会导致视图，则Spring会检查其他视图解析器的上下文。如果存在其他视图解析器，Spring将继续检查它们，直到视图得到解决。如果没有视图解析器返回视图，Spring会抛出一个 `ServletException`。

视图解析器的约定指定视图解析器*可以*返回null以指示无法找到视图。但是，并非所有视图解析器都这样做，因为在某些情况下，解析器根本无法检测视图是否存在。例如，内部`InternalResourceViewResolver`使用`RequestDispatcher`，并且调度是确定JSP是否存在的唯一方法，但此操作只能执行一次。这同样适用于`VelocityViewResolver`其他人。检查特定视图解析程序的javadoc，以查看它是否报告不存在的视图。因此，将`InternalResourceViewResolver`链条置于最后一个位置导致链条未被完全检查，因为 `InternalResourceViewResolver`它将*始终*返回视图！

### 22.5.3重定向到视图

如前所述，控制器通常返回逻辑视图名称，视图解析器将其解析为特定的视图技术。对于通过Servlet或JSP引擎处理的JSP等视图技术，此解决方案通常通过组合来处理，`InternalResourceViewResolver`并 `InternalResourceView`通过Servlet API的`RequestDispatcher.forward(..)`方法或`RequestDispatcher.include()`方法发出内部转发或包含。对于其他视图技术，例如Velocity，XSLT等，视图本身将内容直接写入响应流。

有时需要在呈现视图之前向客户端发出HTTP重定向。例如，当使用`POST`数据调用一个控制器时，这是可取的， 并且响应实际上是对另一个控制器的委托（例如，在成功的表单提交上）。在这种情况下，正常的内部转发将意味着另一个控制器也将看到相同的`POST`数据，如果它可以将其与其他预期数据混淆，则可能存在问题。在显示结果之前执行重定向的另一个原因是消除用户多次提交表单数据的可能性。在这种情况下，浏览器将首先发送一个初始值`POST`; 然后它会收到重定向到不同URL的响应; 最后浏览器将执行后续操作`GET`对于重定向响应中指定的URL。因此，从浏览器的角度来看，当前页面不反映a的结果，`POST`而是反映a的结果`GET`。最终结果是用户无法`POST`通过执行刷新意外地重新生成相同的数据。刷新强制`GET`结果页面的a，而不是重新发送初始`POST`数据。

#### RedirectView的

作为控制器响应的结果，强制重定向的一种方法是控制器创建并返回Spring的实例`RedirectView`。在这种情况下， `DispatcherServlet`不使用普通的视图解析机制。而是因为它已经被赋予了（重定向）视图，它`DispatcherServlet`只是简单地指示视图完成它的工作。将`RedirectView`依次调用`HttpServletResponse.sendRedirect()` 发送一个HTTP重定向到客户端浏览器。

如果您使用`RedirectView`并且视图是由控制器本身创建的，则建议您将重定向URL配置为注入控制器，以便它不会被烘焙到控制器中，而是在上下文中与视图名称一起配置。在[一节“重定向：前缀”](mvc.html#mvc-redirecting-redirect-prefix)有利于这种脱钩。

##### 将数据传递给重定向目标

默认情况下，所有模型属性都被视为在重定向URL中公开为URI模板变量。在其余属性中，原始类型或原始类型的集合/数组将自动附加为查询参数。

如果专门为重定向准备了模型实例，则将原始类型属性作为查询参数附加可能是期望的结果。但是，在带注释的控制器中，模型可能包含为渲染目的而添加的其他属性（例如，下拉字段值）。为了避免在URL中出现此类属性的可能性，`@RequestMapping`方法可以声明类型的参数`RedirectAttributes`并使用它来指定要使用的确切属性`RedirectView`。如果方法重定向，`RedirectAttributes`则使用内容。否则，使用模型的内容。

在`RequestMappingHandlerAdapter`提供了一个名为标志 `"ignoreDefaultModelOnRedirect"`，可以用来表示默认的内容 `Model`，如果一个控制器方法重定向不应该被使用。相反，控制器方法应声明类型的属性，`RedirectAttributes`或者如果不这样做，则不应传递任何属性`RedirectView`。MVC命名空间和MVC Java配置都将此标志设置为`false`以保持向后兼容性。但是，对于新应用程序，我们建议将其设置为`true`

请注意，扩展重定向URL时，当前请求中的URI模板变量会自动变为可用，并且不需要通过`Model`nor 显式添加`RedirectAttributes`。例如：

```java
@PostMapping("/files/{path}")
public String upload(...) {
    // ...
    return "redirect:files/{path}";
}
```

将数据传递到重定向目标的另一种方法是通过*Flash属性*。与其他重定向属性不同，Flash属性保存在HTTP会话中（因此不会出现在URL中）。有关更多信息[，](mvc.html#mvc-flash-attributes)请参见[第22.6节“使用闪存属性”](mvc.html#mvc-flash-attributes)。

#### 重定向：前缀

虽然使用`RedirectView`工作正常，但如果控制器本身创建了 `RedirectView`，则无法避免控制器意识到重定向正在发生的事实。这实际上并不是最理想的，而是将事情过于紧密。控制器不应该真正关心如何处理响应。通常，它应该仅根据已注入其中的视图名称进行操作。

特殊`redirect:`前缀允许您完成此操作。如果返回具有前缀的视图名称`redirect:`，则`UrlBasedViewResolver`（和所有子类）将此识别为需要重定向的特殊指示。视图名称的其余部分将被视为重定向URL。

净效果与控制器返回a的效果相同`RedirectView`，但现在控制器本身可以简单地按逻辑视图名称操作。逻辑视图名称，例如`redirect:/myapp/some/resource`将相对于当前Servlet上下文重定向，而名称`redirect:https://myhost.com/some/arbitrary/path` 将重定向到绝对URL。

请注意，控制器处理程序使用注释`@ResponseStatus`，注释值优先于设置的响应状态`RedirectView`。

#### 转发：前缀

也可以为`forward:`最终由`UrlBasedViewResolver`子类和子类解析的视图名称使用特殊前缀。这会在视图名称的其余部分（称为URL）周围创建`InternalResourceView`（最终执行a `RequestDispatcher.forward()`）。因此，该前缀是不与有用`InternalResourceViewResolver`和`InternalResourceView`（对JSP例如）。但是，当您主要使用其他视图技术时，前缀可能会有所帮助，但仍希望强制Servlet / JSP引擎处理资源的转发。（请注意，您也可以链接多个视图解析器。）

与`redirect:`前缀一样，如果带有前缀的视图名称`forward:`被注入控制器，则控制器不会检测到在处理响应方面发生了任何特殊情况。

### 22.5.4 ContentNegotiatingViewResolver

该`ContentNegotiatingViewResolver`不能解决的观点本身，而是委托给其他视图解析器，选择类似于客户端请求的代表性的观点。客户端从服务器请求表示存在两种策略：

- 通常通过在URI中使用不同的文件扩展名为每个资源使用不同的URI。例如，URI `https://www.example.com/users/fred.pdf`请求用户fred的PDF表示，并`https://www.example.com/users/fred.xml`请求XML表示。
- 使用相同的URI为客户端定位资源，但设置`Accept`HTTP请求标头以列出它理解的[媒体类型](https://en.wikipedia.org/wiki/Internet_media_type)。例如，对于HTTP请求 `https://www.example.com/users/fred`与`Accept`报头设置为`application/pdf` 请求用户fred的PDF表示，而 `https://www.example.com/users/fred`用`Accept`设置为报头`text/xml`的请求的XML表示。此策略称为 [内容协商](https://en.wikipedia.org/wiki/Content_negotiation)。

`Accept`标题的一个问题是无法在HTML中的Web浏览器中设置它。例如，在Firefox中，它固定为：

```bash
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
```

出于这个原因，在开发基于浏览器的Web应用程序时，通常会看到为每个表示使用不同的URI。

为了支持资源的多个表示，Spring提供了 `ContentNegotiatingViewResolver`基于`Accept`HTTP请求的文件扩展名或标头来解析视图的方法。`ContentNegotiatingViewResolver`不执行视图解析本身，而是委托给您通过bean属性指定的视图解析器列表`ViewResolvers`。

在`ContentNegotiatingViewResolver`选择一个合适的`View`通过比较与所述媒体类型（也被称为媒体请求类型（一个或多个），以处理该请求 `Content-Type`由支持）的`View`与每个其相关联`ViewResolvers`。`View`列表中具有兼容性的第一个`Content-Type`将表示返回给客户端。如果`ViewResolver`链不能提供兼容视图，则将`DefaultViews`查阅通过该属性指定的视图列表。后一个选项适用于`Views`可以呈现当前资源的适当表示的单例，而不管逻辑视图名称如何。的`Accept` 报头可以包括通配符，例如`text/*`，在这种情况下`View`，其内容类型是`text/xml`为相容的匹配。

要支持基于文件扩展名的视图的自定义分辨率，请使用 `ContentNegotiationManager`：请参见[第22.16.6节“内容协商”](mvc.html#mvc-config-content-negotiation)。

以下是一个示例配置`ContentNegotiatingViewResolver`：

```xml
<bean class="org.springframework.web.servlet.view.ContentNegotiatingViewResolver">
    <property name="viewResolvers">
        <list>
            <bean class="org.springframework.web.servlet.view.BeanNameViewResolver"/>
            <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
                <property name="prefix" value="/WEB-INF/jsp/"/>
                <property name="suffix" value=".jsp"/>
            </bean>
        </list>
    </property>
    <property name="defaultViews">
        <list>
            <bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView"/>
        </list>
    </property>
</bean>

<bean id="content" class="com.foo.samples.rest.SampleContentAtomView"/>
```

该`InternalResourceViewResolver`手柄视图名称和JSP页面的翻译，而`BeanNameViewResolver`返回基于bean的名称的视图。（有关[Spring](mvc.html#mvc-viewresolver-resolver)如何查找和实例化视图的更多详细信息，请参阅“ [使用ViewResolver接口解析视图](mvc.html#mvc-viewresolver-resolver) ”。）在此示例中，`content` bean是一个继承自的类`AbstractAtomFeedView`，它返回一个Atom RSS提要。有关创建Atom Feed表示的更多信息，请参阅Atom Views部分。

在上面的配置中，如果使用`.html`扩展名发出请求，则视图解析程序将查找与`text/html`媒体类型匹配的视图。在 `InternalResourceViewResolver`提供了用于匹配视图`text/html`。如果使用文件扩展名发出请求`.atom`，则视图解析程序将查找与`application/atom+xml`媒体类型匹配的视图。该视图由`BeanNameViewResolver`映射到`SampleContentAtomView`返回的视图名称的映射提供 `content`。如果使用文件扩展名进行请求，则无论视图名称如何，都将选择列表中`.json`的`MappingJackson2JsonView`实例`DefaultViews`。或者，可以在没有文件扩展名的情况下进行客户端请求，但将`Accept`标头设置为首选媒体类型，并且将发生对视图的相同请求分辨率。

如果没有显式配置`ContentNegotiatingViewResolver的ViewResolvers列表，它会自动使用应用程序上下文中定义的任何ViewResolvers。

返回表单的URI `http://localhost/content.atom`或`http://localhost/content`带有`Accept`application / atom + xml标头的Atom RSS提要的相应控制器代码 如下所示。

```java
@Controller
public class ContentController {

    private List<SampleContent> contentList = new ArrayList<SampleContent>();

    @GetMapping("/content")
    public ModelAndView getContent() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("content");
        mav.addObject("sampleContentList", contentList);
        return mav;
    }

}
```

