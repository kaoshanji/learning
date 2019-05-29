# 22.1 概述

## 22.1 Spring Web MVC框架简介

Spring Web模型 - 视图 - 控制器（MVC）框架围绕一个 `DispatcherServlet`调度请求处理程序而设计，具有可配置的处理程序映射，视图分辨率，区域设置，时区和主题解析以及对上载文件的支持。默认处理程序基于`@Controller`和`@RequestMapping` 注释，提供各种灵活的处理方法。随着Spring 3.0的推出，该`@Controller`机制还允许您通过`@PathVariable`注释和其他功能创建RESTful Web站点和应用程序。

“Open for extension ...”Spring Web MVC和Spring中的一个关键设计原则是“ *Open for extension，closed for modification* ”原则。

Spring Web MVC的核心类中的一些方法被标记`final`。作为开发人员，您无法覆盖这些方法来提供自己的行为。这不是任意做的，而是特别考虑到这一原则。

有关此原理的说明，请参阅Seth Ladd等人的*Expert Spring Web MVC和Web Flow* ; 请参阅第一版第117页的“设计外观”一节。或者，请参阅

- [鲍勃马丁，开放封闭原则（PDF）](https://www.cs.duke.edu/courses/fall07/cps108/papers/ocp.pdf)

使用Spring MVC时，无法向最终方法添加建议。例如，您无法向该`AbstractController.setSynchronizeOnSession()`方法添加建议。有关AOP代理的更多信息以及无法向最终方法添加建议的原因[，](aop.html#aop-understanding-aop-proxies)请参见 [第11.6.1节“了解AOP代理”](aop.html#aop-understanding-aop-proxies)。

在Spring Web MVC中，您可以将任何对象用作命令或表单支持对象; 您不需要实现特定于框架的接口或基类。Spring的数据绑定非常灵活：例如，它将类型不匹配视为可由应用程序评估的验证错误，而不是系统错误。因此，您不需要将业务对象的属性复制为表单对象中的简单无类型字符串，只需处理无效提交或正确转换字符串。相反，通常最好直接绑定到业务对象。

Spring的视图分辨率非常灵活。A `Controller`通常负责准备`Map`包含数据的模型并选择视图名称，但它也可以直接写入响应流并完成请求。通过文件扩展名或Accept头内容类型协商，通过bean名称，属性文件甚至自定义`ViewResolver`实现，可以高度配置视图名称解析。模型（MVC中的M）是一个`Map`接口，它允许完全抽象视图技术。您可以直接与基于模板的渲染技术（如JSP，Velocity和Freemarker）集成，也可以直接生成XML，JSON，Atom和许多其他类型的内容。该模型`Map` 简单地转换为适当的格式，例如JSP请求属性，Velocity模板模型。

### 22.1.1 Spring Web MVC的特性

**Spring Web Flow**

Spring Web Flow（SWF）旨在成为Web应用程序页面流管理的最佳解决方案。

SWF在Servlet和Portlet环境中与Spring MVC和JSF等现有框架集成。如果您的业务流程（或流程）可以从会话模型中受益而不是纯粹的请求模型，那么SWF可能就是解决方案。

SWF允许您将逻辑页面流捕获为可在不同情况下重用的自包含模块，因此非常适合构建Web应用程序模块，以引导用户完成驱动业务流程的受控导航。

有关SWF的更多信息，请参阅 [Spring Web Flow网站](https://projects.spring.io/spring-webflow/)。

Spring的Web模块包含许多独特的Web支持功能：

- *明确分离角色*。每个角色 - 控制器，验证器，命令对象，表单对象，模型对象`DispatcherServlet`，处理程序映射，视图解析器等 - 都可以由专用对象来完成。
- *像JavaBeans一样强大而直接地配置框架和应用程序类*。此配置功能包括跨上下文轻松引用，例如从Web控制器到业务对象和验证器。
- *适应性，非侵入性和灵活性。*定义您需要的任何控制器方法签名，可能使用给定方案的参数注释之一（例如@RequestParam，@ RequestHeader，@ PathVariable等）。
- *可重复使用的业务代码，无需重复*。将现有业务对象用作命令或表单对象，而不是镜像它们以扩展特定的框架基类。
- *可定制的绑定和验证*。键入不匹配作为应用程序级验证错误，这些错误会保留违规值，本地化日期和数字绑定等，而不是通过手动解析和转换为业务对象的仅限字符串的表单对象。
- *可定制的处理程序映射和视图解析*。处理程序映射和视图解析策略的范围从简单的基于URL的配置到复杂的，专用的解析策略。Spring比授权特定技术的Web MVC框架更灵活。
- *灵活的模型转移*。具有名称/值的模型传输`Map`支持与任何视图技术的轻松集成。
- *可自定义的区域设置，时区和主题解析，支持带或不带Spring标记库的JSP，支持JSTL，支持Velocity而无需额外的桥接，等等。*
- *一个简单但功能强大的JSP标记库，称为Spring标记库，为数据绑定和主题等功能提供支持*。自定义标记在标记代码方面具有最大的灵活性。有关标记库描述符的信息，请参阅[第43章](spring-tld.html)[*spring spring Tag Library*](spring-tld.html)附录
- *Spring 2.0中引入的JSP表单标记库，使得在JSP页面中编写表单变得更加容易。*有关标记库描述符的信息，请参阅标题为[第44章的](spring-form-tld.html)[*弹簧形式JSP标记库*](spring-form-tld.html)的附录
- *生命周期范围限定为当前HTTP请求或HTTP的Bean Session。* 这不是Spring MVC本身的特定功能，而是`WebApplicationContext`Spring MVC使用的 容器。[第7.5.4节“请求，会话，全局会话，应用程序和WebSocket范围”](beans.html#beans-factory-scopes-other)中介绍了这些Bean作用域。

### 22.1.2其他MVC实现的可插拔性

对于某些项目，非Spring MVC实现是更可取的。许多团队希望利用他们在技能和工具方面的现有投资，例如使用JSF。

如果您不想使用Spring的Web MVC，但打算利用Spring提供的其他解决方案，您可以轻松地将您选择的Web MVC框架与Spring集成。只需通过它启动Spring根应用程序上下文 `ContextLoaderListener`，并通过其`ServletContext`属性（或Spring的各自帮助方法）从任何操作对象中访问它。不涉及“插件”，因此不需要专门的集成。从Web层的角度来看，您只需将Spring用作库，将根应用程序上下文实例作为入口点。

即使没有Spring的Web MVC，您的注册bean和Spring的服务也可以触手可及。在这种情况下，Spring不会与其他Web框架竞争。它简单地解决了纯web MVC框架没有的许多方面，从bean配置到数据访问和事务处理。因此，您可以使用Spring中间层和/或数据访问层来丰富您的应用程序，即使您只是想使用JDBC或Hibernate的事务抽象。
