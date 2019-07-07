# 22.3 控制器

控制器提供对通常通过服务接口定义的应用程序行为的访问。控制器解释用户输入并将其转换为由视图表示给用户的模型。Spring以非常抽象的方式实现控制器，使您可以创建各种控制器。

Spring 2.5中引入了MVC控制器的，基于注解的编程模型，使用注解如`@RequestMapping`，`@RequestParam`，`@ModelAttribute`，等。这个注释支持可用于Servlet MVC和Portlet MVC。以此样式实现的控制器不必扩展特定的基类或实现特定的接口。此外，它们通常不直接依赖于Servlet或Portlet API，尽管您可以轻松配置对Servlet或Portlet工具的访问。

[在Github上](https://github.com/spring-projects/)的[Spring项目Org中](https://github.com/spring-projects/)可用，许多Web应用程序利用本节中描述的注释支持，包括*MvcShowcase*，*MvcAjax*，*MvcBasic*，*PetClinic*，*PetCare*等

```java
@Controller
public class HelloWorldController {

    @RequestMapping("/helloWorld")
    public String helloWorld(Model model) {
        model.addAttribute("message", "Hello World!");
        return "helloWorld";
    }
}
```

如您所见，`@Controller`和`@RequestMapping`注释允许灵活的方法名称和签名。在此特定示例中，该方法接受a `Model`并将视图名称作为a返回`String`，但可以使用各种其他方法参数和返回值，如本节后面所述。`@Controller`和`@RequestMapping`许多其他注释构成了Spring MVC实现的基础。本节介绍了这些注释以及它们在Servlet环境中最常用的方式。

### 22.3.1使用@Controller定义控制器

该`@Controller`注解表明特定类供应的作用*控制器*。Spring不要求您扩展任何控制器基类或引用Servlet API。但是，如果需要，您仍可以引用特定于Servlet的功能。

该`@Controller`注释充当注解类刻板印象，这表明它的作用。调度程序扫描这些带注释的类以查找映射方法并检测 `@RequestMapping`注释（请参阅下一节）。

您可以使用调度程序上下文中的标准Spring bean定义显式定义带注释的控制器bean。但是，`@Controller`构造型还允许自动检测，与Spring一致支持，以检测类路径中的组件类，并为它们自动注册bean定义。

要启用此类带注释控制器的自动检测，请将组件扫描添加到配置中。使用*spring-context*架构，如以下XML片段所示：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="org.springframework.samples.petclinic.web"/>

    <!-- ... -->

</beans>
```

### 22.3.2使用@RequestMapping映射请求

您可以使用`@RequestMapping`注释将URL映射`/appointments`到整个类或特定处理程序方法。通常，类级注释将特定请求路径（或路径模式）映射到表单控制器上，其他方法级注释缩小了特定HTTP方法请求方法（“GET”，“POST”等）的主映射。或HTTP请求参数条件。

*Petcare*示例中的以下示例显示了使用此批注的Spring MVC应用程序中的控制器：

```java
@Controller
@RequestMapping("/appointments")
public class AppointmentsController {

    private final AppointmentBook appointmentBook;

    @Autowired
    public AppointmentsController(AppointmentBook appointmentBook) {
        this.appointmentBook = appointmentBook;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Appointment> get() {
        return appointmentBook.getAppointmentsForToday();
    }

    @RequestMapping(path = "/{day}", method = RequestMethod.GET)
    public Map<String, Appointment> getForDay(@PathVariable @DateTimeFormat(iso=ISO.DATE) Date day, Model model) {
        return appointmentBook.getAppointmentsForDay(day);
    }

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public AppointmentForm getNewForm() {
        return new AppointmentForm();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(@Valid AppointmentForm appointment, BindingResult result) {
        if (result.hasErrors()) {
            return "appointments/new";
        }
        appointmentBook.addAppointment(appointment);
        return "redirect:/appointments";
    }
}
```

在上面的例子中，`@RequestMapping`用于许多地方。第一种用法是在类型（类）级别上，这表示此控制器中的所有处理程序方法都与`/appointments`路径相关。该`get()`方法有一个进一步的 `@RequestMapping`改进：它只接受`GET`请求，这意味着HTTP `GET`用于 `/appointments`调用此方法。它`add()`具有类似的细化，并将`getNewForm()`HTTP方法和路径的定义合并为一个，以便`GET` 请求`appointments/new`由该方法处理。

该`getForDay()`方法显示了另一种用法`@RequestMapping`：URI模板。（请参阅 [“URI模板模式”一节](mvc.html#mvc-ann-requestmapping-uri-templates)）。

一个`@RequestMapping`在类级别不是必需的。没有它，所有路径都是绝对的，而不是相对的。以下来自*PetClinic*示例应用程序的示例显示了一个多动作控制器，它使用`@RequestMapping`：

```java
@Controller
public class ClinicController {

    private final Clinic clinic;

    @Autowired
    public ClinicController(Clinic clinic) {
        this.clinic = clinic;
    }

    @RequestMapping("/")
    public void welcomeHandler() {
    }

    @RequestMapping("/vets")
    public ModelMap vetsHandler() {
        return new ModelMap(this.clinic.getVets());
    }

}
```

上面的示例未指定`GET`vs. `PUT`，`POST`等等，因为 `@RequestMapping`默认情况下映射所有HTTP方法。使用`@RequestMapping(method=GET)`或 `@GetMapping`缩小映射范围。

#### 组成的@RequestMapping变体

Spring Framework 4.3引入了以下方法级*组合*的`@RequestMapping`注释变体，这些变体 有助于简化常见HTTP方法的映射，并更好地表达带注释的处理程序方法的语义。例如，a `@GetMapping`可以读作a `GET` `@RequestMapping`。

- `@GetMapping`
- `@PostMapping`
- `@PutMapping`
- `@DeleteMapping`
- `@PatchMapping`

以下示例显示了上一部分的修改版本，该版本`AppointmentsController`已使用*组合* `@RequestMapping`注释进行了简化。

```java
@Controller
@RequestMapping("/appointments")
public class AppointmentsController {

    private final AppointmentBook appointmentBook;

    @Autowired
    public AppointmentsController(AppointmentBook appointmentBook) {
        this.appointmentBook = appointmentBook;
    }

    @GetMapping
    public Map<String, Appointment> get() {
        return appointmentBook.getAppointmentsForToday();
    }

    @GetMapping("/{day}")
    public Map<String, Appointment> getForDay(@PathVariable @DateTimeFormat(iso=ISO.DATE) Date day, Model model) {
        return appointmentBook.getAppointmentsForDay(day);
    }

    @GetMapping("/new")
    public AppointmentForm getNewForm() {
        return new AppointmentForm();
    }

    @PostMapping
    public String add(@Valid AppointmentForm appointment, BindingResult result) {
        if (result.hasErrors()) {
            return "appointments/new";
        }
        appointmentBook.addAppointment(appointment);
        return "redirect:/appointments";
    }
}
```

#### @Controller和AOP Proxying

在某些情况下，控制器可能需要在运行时使用AOP代理进行修饰。例如，如果您选择`@Transactional`直接在控制器上注释。在这种情况下，对于控制器而言，我们建议使用基于类的代理。这通常是控制器的默认选择。但是，如果一个控制器必须实现一个接口，这不是一个Spring上下文回调（例如`InitializingBean`，`*Aware`等），您可能需要显式配置基于类的代理。例如`<tx:annotation-driven/>`，改为`<tx:annotation-driven proxy-target-class="true"/>`。

#### Spring MVC 3.1中@RequestMapping方法的新支持类

春季3.1中引入了一套新的支持类的`@RequestMapping`调用的方法 `RequestMappingHandlerMapping`和`RequestMappingHandlerAdapter`分别。建议使用它们，甚至需要利用Spring MVC 3.1中的新功能并继续使用。默认情况下，MVC命名空间和MVC Java配置启用新的支持类，但如果两者都不使用，则必须明确配置。本节介绍旧支持类和新支持类之间的一些重要差异。

在Spring 3.1之前，类型和方法级请求映射在两个单独的阶段中进行检查 - 首先由控制器选择控制器， `DefaultAnnotationHandlerMapping`然后调用实际的调用方法`AnnotationMethodHandlerAdapter`。

使用Spring 3.1中的新支持类，`RequestMappingHandlerMapping`唯一可以决定哪个方法应该处理请求的地方。将控制器方法视为一组唯一端点，并为从类型和方法级`@RequestMapping`信息派生的每个方法提供映射。

这实现了一些新的可能性。一旦a `HandlerInterceptor`或a `HandlerExceptionResolver`现在可以期望基于对象的处理程序是a `HandlerMethod`，这允许它们检查确切的方法，其参数和相关的注释。不再需要跨不同的控制器分割URL的处理。

还有一些事情不再可能：

- 首先用选择一个控制器`SimpleUrlHandlerMapping`或 `BeanNameUrlHandlerMapping`，然后缩小基于该方法`@RequestMapping` 的注释。
- 依赖于方法名称作为回退机制来消除两个`@RequestMapping`方法之间的歧义，这两个 方法没有显式路径映射URL路径，但在其他方面相同，例如通过HTTP方法。在新的支持类 `@RequestMapping`中，必须唯一地映射方法。
- 如果没有其他控制器方法更具体地匹配，则使用单个默认方法（没有显式路径映射）处理请求。在新的支持类中，如果找不到匹配的方法，则会引发404错误。

现有支持类仍支持上述功能。但是，要利用新的Spring MVC 3.1功能，您需要使用新的支持类。

#### URI模板模式

*URI模板*可用于方便地访问`@RequestMapping`方法中URL的选定部分 。

URI模板是类似URI的字符串，包含一个或多个变量名称。当您为这些变量替换值时，模板将成为URI。该 [建议的RFC](https://bitworking.org/projects/URI-Templates/)的URI模板定义的URI是如何参数。例如，URI模板`https://www.example.com/users/{userId}`包含变量*userId*。将值*fred*分配给变量会产生`https://www.example.com/users/fred`。

在Spring MVC中，您可以使用`@PathVariable`方法参数上的注释将其绑定到URI模板变量的值：

```java
@GetMapping("/owners/{ownerId}")
public String findOwner(@PathVariable String ownerId, Model model) {
    Owner owner = ownerService.findOwner(ownerId);
    model.addAttribute("owner", owner);
    return "displayOwner";
}
```

URI模板“ `/owners/{ownerId}`" specifies the variable name `ownerId`。当控制器处理该请求，的值`ownerId`被设定为在URI的适当部分中找到的值。例如，当一个请求到达为`/owners/fred`，的值`ownerId`是`fred`。

要处理@PathVariable批注，Spring MVC需要按名称查找匹配的URI模板变量。您可以在注释中指定它：

```java
@GetMapping("/owners/{ownerId}")
public String findOwner(@PathVariable("ownerId") String theOwner, Model model) {
    // implementation omitted
}
```

或者，如果URI模板变量名称与方法参数名称匹配，则可以省略该详细信息。只要您的代码使用调试信息或`-parameters` Java 8上的编译器标志进行编译，Spring MVC就会将方法参数名称与URI模板变量名称匹配：

```java
@GetMapping("/owners/{ownerId}")
public String findOwner(@PathVariable String ownerId, Model model) {
    // implementation omitted
}
```

方法可以包含任意数量的`@PathVariable`注释：

```java
@GetMapping("/owners/{ownerId}/pets/{petId}")
public String findPet(@PathVariable String ownerId, @PathVariable String petId, Model model) {
    Owner owner = ownerService.findOwner(ownerId);
    Pet pet = owner.getPet(petId);
    model.addAttribute("pet", pet);
    return "displayPet";
}
```

在参数`@PathVariable`上使用注释时`Map<String, String>`，将使用所有URI模板变量填充映射。

可以从类型和方法级别*@RequestMapping* 注释组装URI模板。因此，`findPet()`可以使用诸如的URL来调用该方法 `/owners/42/pets/21`。

```java
@Controller
@RequestMapping("/owners/{ownerId}")
public class RelativePathUriTemplateController {

    @RequestMapping("/pets/{petId}")
    public void findPet(@PathVariable String ownerId, @PathVariable String petId, Model model) {
        // implementation omitted
    }

}
```

甲`@PathVariable`参数可以是*任何简单类型*如`int`，`long`，`Date`等Spring自动转换成适当的类型或引发 `TypeMismatchException`，如果它不这样做。您还可以注册支持解析其他数据类型。请参阅[“方法参数和类型转换” ](mvc.html#mvc-ann-typeconversion)[一节](mvc.html#mvc-ann-webdatabinder)以及[“自定义WebDataBinder初始化”一节](mvc.html#mvc-ann-webdatabinder)。

#### 具有正则表达式的URI模板模式

有时您需要更精确地定义URI模板变量。考虑一下URL `"/spring-web/spring-web-3.0.5.jar"`。你如何将其分解为多个部分？

该`@RequestMapping`注解支持使用URI模板变量的正则表达式。语法是`{varName:regex}`第一部分定义变量名称，第二部分定义正则表达式。例如：

```java
@RequestMapping("/spring-web/{symbolicName:[a-z-]+}-{version:\\d\\.\\d\\.\\d}{extension:\\.[a-z]+}")
public void handle(@PathVariable String version, @PathVariable String extension) {
    // ...
}
```

#### 路径模式

除URI模板外，`@RequestMapping`注释和所有*组合* `@RequestMapping`变体还支持Ant样式的路径模式（例如， `/myPath/*.do`）。还支持URI模板变量和Ant样式globs的组合（例如`/owners/*/pets/{petId}`）。

#### 路径模式比较

当URL匹配多个模式时，使用排序来查找最具体的匹配。

具有较低URI变量和通配符计数的模式被认为更具体。例如，`/hotels/{hotel}/*`有1个URI变量和1个通配符，被认为比`/hotels/{hotel}/**`1 URI变量和2个通配符更具体。

如果两个模式具有相同的计数，则更长的模式被认为更具体。例如`/foo/bar*`，更长，被认为更具体`/foo/*`。

当两个模式具有相同的计数和长度时，具有较少通配符的模式被认为更具体。例如`/hotels/{hotel}`比具体更具体`/hotels/*`。

还有一些额外的特殊规则：

- 所述**默认映射模式** `/**`是比任何其它模式更小的特定。例如`/api/{a}/{b}/{c}`更具体。
- 甲**前缀模式**如`/public/**`比不含有双通配符的任何其他图案较不具体。例如`/public/path3/{a}/{b}/{c}`更具体。

有关完整详情，请参阅`AntPatternComparator`在`AntPathMatcher`。请注意，PathMatcher可以自定义（请参阅配置Spring MVC一[节中的第22.16.11节“路径匹配”](mvc.html#mvc-config-path-matching)）。

#### 带占位符的路径模式

`@RequestMapping`注释中的模式支持`${…}`占位符，以支持本地属性和/或系统属性和环境变量。在控制器映射到的路径可能需要通过配置进行自定义的情况下，这可能很有用。有关占位符的更多信息，请参阅`PropertyPlaceholderConfigurer`该类的javadoc 。

#### 后缀模式匹配

默认情况下，Spring MVC执行`".*"`后缀模式匹配，以便映射到的控制器`/person`也隐式映射到`/person.*`。这使得通过URL路径（例如`/person.pdf`，`/person.xml`）请求资源的不同表示变得容易。

可以关闭或限制后缀模式匹配，以便为内容协商目的明确注册一组路径扩展。这是一般建议尽量减少与共同申请的映射，如不确定性 `/person/{id}`，其中一个点可能不代表文件扩展名，例如， `/person/joe@email.com`VS `/person/joe@email.com.json`。此外，如下面的注释中所解释的，后缀模式匹配以及内容协商可能在某些情况下用于尝试恶意攻击，并且有充分的理由对其进行有意义的限制。

有关后缀模式匹配配置，请参见[第22.16.11节“路径匹配”](mvc.html#mvc-config-path-matching)，对于内容协商配置[，](mvc.html#mvc-config-path-matching)请参见[第22.16.6 ](mvc.html#mvc-config-path-matching)[节“内容协商”](mvc.html#mvc-config-content-negotiation)。

#### 后缀模式匹配和RFD

2014年[，Trustwave](https://www.trustwave.com/Resources/SpiderLabs-Blog/Reflected-File-Download---A-New-Web-Attack-Vector/)在[一篇论文中](https://www.trustwave.com/Resources/SpiderLabs-Blog/Reflected-File-Download---A-New-Web-Attack-Vector/)首次描述了反射文件下载（RFD）攻击 。该攻击类似于XSS，因为它依赖于响应中反映的输入（例如查询参数，URI变量）。然而，如果基于文件扩展名（例如.bat，.cmd）双击，则RFD攻击依赖于浏览器切换来执行下载并将响应视为可执行脚本，而不是将JavaScript插入HTML。

在Spring MVC中`@ResponseBody`，`ResponseEntity`方法存在风险，因为它们可以呈现客户端可以请求的不同内容类型，包括通过URL路径扩展。但请注意，既不禁用后缀模式匹配也不禁用路径扩展仅用于内容协商目的，这对于防止RFD攻击都是有效的。

为了全面防范RFD，在呈现响应体之前，Spring MVC添加了一个`Content-Disposition:inline;filename=f.txt`标题来建议一个固定且安全的下载文件文件名。仅当URL路径包含既未列入白名单也未明确注册以用于内容协商目的的文件扩展名时，才会执行此操作。但是，当直接在浏览器中输入URL时，它可能会产生副作用。

默认情况下，许多常见路径扩展名列入白名单。此外，REST API调用通常不能直接在浏览器中用作URL。然而，使用自定义`HttpMessageConverter`实现的应用程序 可以显式注册文件扩展名以进行内容协商，并且不会为此类扩展添加Content-Disposition标头。请参见[第22.16.6节“内容协商”](mvc.html#mvc-config-content-negotiation)。

这最初是作为[CVE-2015-5211](https://pivotal.io/security/cve-2015-5211)工作的一部分而引入的 。以下是报告中的其他建议：

- 编码而不是逃避JSON响应。这也是OWASP XSS的推荐。有关如何使用Spring执行此操作的示例，请参阅[spring-jackson-owasp](https://github.com/rwinch/spring-jackson-owasp)。
- 将后缀模式匹配配置为仅关闭或仅限于显式注册的后缀。
- 配置内容协商，将属性“useJaf”和“ignoreUnknownPathExtensions”设置为false，这将导致对未知扩展名的URL进行406响应。但是请注意，如果URL自然希望在末尾有一个点，则可能无法选择此选项。
- 添加`X-Content-Type-Options: nosniff`标题到响应。Spring Security 4默认执行此操作。

#### 矩阵变量

URI规范[RFC 3986](https://tools.ietf.org/html/rfc3986#section-3.3)定义了在路径段中包含名称 - 值对的可能性。规范中没有使用特定术语。可以应用一般的“URI路径参数”，尽管源自Tim Berners-Lee的旧帖子的更独特的[“矩阵URI”](https://www.w3.org/DesignIssues/MatrixURIs.html)也经常被使用并且是众所周知的。在Spring MVC中，这些被称为矩阵变量。

矩阵变量可以出现在任何路径段中，每个矩阵变量用“;”分隔 （分号）。例如：`"/cars;color=red;year=2012"`。多个值可以是“，”（逗号）分隔`"color=red,green,blue"`，也可以重复变量名`"color=red;color=green;color=blue"`。

如果URL预计包含矩阵变量，则请求映射模式必须使用URI模板表示它们。这确保了请求可以正确匹配，无论是否存在矩阵变量以及它们的提供顺序。

下面是提取矩阵变量“q”的示例：

```java
// GET /pets/42;q=11;r=22

@GetMapping("/pets/{petId}")
public void findPet(@PathVariable String petId, @MatrixVariable int q) {

    // petId == 42
    // q == 11

}
```

由于所有路径段都可能包含矩阵变量，因此在某些情况下，您需要更具体地确定变量的预期位置：

```java
// GET /owners/42;q=11/pets/21;q=22

@GetMapping("/owners/{ownerId}/pets/{petId}")
public void findPet(
        @MatrixVariable(name="q", pathVar="ownerId") int q1,
        @MatrixVariable(name="q", pathVar="petId") int q2) {

    // q1 == 11
    // q2 == 22

}
```

矩阵变量可以定义为可选，并指定默认值：

```java
// GET /pets/42

@GetMapping("/pets/{petId}")
public void findPet(@MatrixVariable(required=false, defaultValue="1") int q) {

    // q == 1

}
```

所有矩阵变量都可以在Map中获得：

```java
// GET /owners/42;q=11;r=12/pets/21;q=22;s=23

@GetMapping("/owners/{ownerId}/pets/{petId}")
public void findPet(
        @MatrixVariable MultiValueMap<String, String> matrixVars,
        @MatrixVariable(pathVar="petId"") MultiValueMap<String, String> petMatrixVars) {

    // matrixVars: ["q" : [11,22], "r" : 12, "s" : 23]
    // petMatrixVars: ["q" : 11, "s" : 23]

}
```

请注意，要启用矩阵变量，必须将`removeSemicolonContent`属性设置 `RequestMappingHandlerMapping`为`false`。默认情况下，它设置为`true`。

MVC Java配置和MVC命名空间都提供了启用矩阵变量使用的选项。

如果您使用的是Java配置，则“ [使用MVC Java配置进行高级自定义”](mvc.html#mvc-config-advanced-java)部分介绍了如何`RequestMappingHandlerMapping`自定义。

在MVC名称空间中，`<mvc:annotation-driven>`元素具有`enable-matrix-variables`应设置为的 属性`true`。默认情况下，它设置为`false`。

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

    <mvc:annotation-driven enable-matrix-variables="true"/>

</beans>
```

#### Consumable Media Types

您可以通过指定可使用的介质类型列表来缩小主映射。仅当`Content-Type`请求标头与指定的媒体类型匹配时，才会匹配请求。例如：

```java
@PostMapping(path = "/pets", consumes = "application/json")
public void addPet(@RequestBody Pet pet, Model model) {
    // implementation omitted
}
```

消费媒体类型表达式也可以被否定，`!text/plain`以匹配除具有`Content-Type`的所有请求之外的所有请求`text/plain`。还要考虑使用和 `MediaType`等中提供的常量。`APPLICATION_JSON_VALUE``APPLICATION_JSON_UTF8_VALUE`

*消耗*状态被支撑在类型和方法的水平。与大多数其他条件不同，在类型级别使用时，方法级消耗类型会覆盖而不是扩展类型级消耗类型。

#### 可生产的媒体类型

您可以通过指定可生成的媒体类型列表来缩小主映射。仅当`Accept`请求标头与这些值中的一个匹配时，才会匹配请求。此外，使用*生成*条件可确保用于生成响应的实际内容类型遵循*生成* 条件中指定的媒体类型。例如：

```java
@GetMapping(path = "/pets/{petId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ResponseBody
public Pet getPet(@PathVariable String petId, Model model) {
    // implementation omitted
}
```

请注意，*生成*条件中指定的介质类型也可以选择指定字符集。例如，在上面的代码片段中，我们指定的媒体类型与配置的默认媒体类型相同`MappingJackson2HttpMessageConverter`，包括`UTF-8`charset。

就像使用*消费一样*，可以否定可生成的媒体类型表达式， `!text/plain`以匹配除`Accept`标头值为的那些请求之外的所有请求`text/plain`。还要考虑使用和`MediaType`等中提供的常量。`APPLICATION_JSON_VALUE``APPLICATION_JSON_UTF8_VALUE`

所述*产生*状态被支撑在类型和方法的水平。与大多数其他条件不同，在类型级别使用时，方法级可生成类型会覆盖而不是扩展类型级可生成类型。

#### 请求参数和标头值

可以通过请求参数条件，如缩小请求匹配 `"myParam"`，`"!myParam"`或`"myParam=myValue"`。前两个测试请求参数存在/不存在，第三个测试特定参数值。以下是请求参数值条件的示例：

```java
@Controller
@RequestMapping("/owners/{ownerId}")
public class RelativePathUriTemplateController {

    @GetMapping(path = "/pets/{petId}", params = "myParam=myValue")
    public void findPet(@PathVariable String ownerId, @PathVariable String petId, Model model) {
        // implementation omitted
    }

}
```

可以执行相同的操作来测试请求标头的存在/不存在，或者根据特定的请求标头值进行匹配：

```java
@Controller
@RequestMapping("/owners/{ownerId}")
public class RelativePathUriTemplateController {

    @GetMapping(path = "/pets", headers = "myHeader=myValue")
    public void findPet(@PathVariable String ownerId, @PathVariable String petId, Model model) {
        // implementation omitted
    }

}
```

虽然您可以使用媒体类型通配符匹配*Content-Type*和*Accept*标头值（例如*“content-type = text / \*”*将匹配*“text / plain”*和 *“text / html”*），但建议使用的*消耗*和*产生*的条件分别代替。它们专门用于此目的。

#### HTTP HEAD和HTTP OPTIONS

`@RequestMapping`映射到“GET”的方法也隐式映射到“HEAD”，即不需要显式声明“HEAD”。处理HTTP HEAD请求就像它是HTTP GET一样，除了不写入主体，只计算字节数并设置“Content-Length”标头。

`@RequestMapping`方法内置了对HTTP OPTIONS的支持。默认情况下，通过将“Allow”响应标头设置为在`@RequestMapping`具有匹配URL模式的所有方法上显式声明的HTTP方法来处理HTTP OPTIONS请求。当没有显式声明HTTP方法时，“Allow”标头设置为“GET，HEAD，POST，PUT，PATCH，DELETE，OPTIONS”。理想情况下，始终声明方法`@RequestMapping`要处理的HTTP方法，或者使用其中一个专用*组合* `@RequestMapping`变体（请参阅 [“Composed @RequestMapping Variants”一节](mvc.html#mvc-ann-requestmapping-composed)）。

虽然没有必要，但`@RequestMapping`可以将方法映射到HTTP HEAD或HTTP OPTIONS，或者同时处理它们。

### 22.3.3定义@RequestMapping处理程序方法

`@RequestMapping`处理程序方法可以具有非常灵活的签名。支持的方法参数和返回值将在下一节中介绍。大多数参数可以按任意顺序使用，唯一的例外是`BindingResult` 参数。这将在下一节中介绍。

春季3.1中引入了一套新的支持类的`@RequestMapping`调用的方法 `RequestMappingHandlerMapping`和`RequestMappingHandlerAdapter`分别。建议使用它们，甚至需要利用Spring MVC 3.1中的新功能并继续使用。默认情况下，从MVC命名空间启用新的支持类，并使用MVC Java配置，但如果不使用，则必须明确配置。

#### 支持的方法参数类型

以下是受支持的方法参数：

- 请求或响应对象（Servlet API）。选择任何特定请求或响应类型，例如`ServletRequest`或`HttpServletRequest`。
- 会话对象（Servlet API）：类型`HttpSession`。此类型的参数强制存在相应的会话。因此，这种论点永远不会 `null`。

会话访问可能不是线程安全的，特别是在Servlet环境中。`RequestMappingHandlerAdapter`如果允许多个请求同时访问会话，请考虑将's“synchronizeOnSession”标志设置为“true”。

- `org.springframework.web.context.request.WebRequest`或 `org.springframework.web.context.request.NativeWebRequest`。允许通用请求参数访问以及请求/会话属性访问，而不与本机Servlet / Portlet API绑定。
- `java.util.Locale`对于当前请求区域设置，由最可用的区域设置解析器确定，实际上是在MVC环境中配置的`LocaleResolver`/ `LocaleContextResolver`。
- `java.util.TimeZone`（Java 6+）/ `java.time.ZoneId`（在Java 8上）与当前请求关联的时区，由a确定`LocaleContextResolver`。
- `java.io.InputStream`/ `java.io.Reader`用于访问请求的内容。此值是Servlet API公开的原始InputStream / Reader。
- `java.io.OutputStream`/ `java.io.Writer`用于生成响应的内容。此值是Servlet API公开的原始OutputStream / Writer。
- `org.springframework.http.HttpMethod` 对于HTTP请求方法。
- `java.security.Principal` 包含当前经过身份验证的用户。
- `@PathVariable`用于访问URI模板变量的带注释的参数。请参阅 [“URI模板模式”一节](mvc.html#mvc-ann-requestmapping-uri-templates)。
- `@MatrixVariable`用于访问位于URI路径段中的名称 - 值对的带注释的参数。请参阅[“矩阵变量”一节](mvc.html#mvc-ann-matrix-variables)。
- `@RequestParam`用于访问特定Servlet请求参数的带注释参数。参数值将转换为声明的方法参数类型。请参阅[“使用@RequestParam将请求参数绑定到方法参数”一节](mvc.html#mvc-ann-requestparam)。
- `@RequestHeader`用于访问特定Servlet请求HTTP标头的带注释参数。参数值将转换为声明的方法参数类型。请参阅[“使用@RequestHeader注释映射请求标头属性”一节](mvc.html#mvc-ann-requestheader)。
- `@RequestBody`用于访问HTTP请求主体的带注释的参数。使用`HttpMessageConverter`s 将参数值转换为声明的方法参数类型 。请参阅[“使用@RequestBody注释映射请求正文”一节](mvc.html#mvc-ann-requestbody)。
- `@RequestPart`用于访问“multipart / form-data”请求部分内容的带注释参数。请参见[第22.10.5节“处理来自程序客户端的文件上载请求”](mvc.html#mvc-multipart-forms-non-browsers)和 [第22.10节“Spring的多部分（文件上载）支持”](mvc.html#mvc-multipart)。
- `@SessionAttribute`用于访问现有的永久会话属性（例如用户认证对象）的注释参数，而不是作为控制器工作流通过的一部分临时存储在会话中的模型属性`@SessionAttributes`。
- `@RequestAttribute` 用于访问请求属性的带注释的参数。
- `HttpEntity<?>`用于访问Servlet请求HTTP标头和内容的参数。请求流将使用`HttpMessageConverter`s 转换为实体主体 。请参阅[“使用HttpEntity”一节](mvc.html#mvc-ann-httpentity)。
- `java.util.Map`/ `org.springframework.ui.Model`/ `org.springframework.ui.ModelMap` 欲得暴露于web视图隐含模型。
- `org.springframework.web.servlet.mvc.support.RedirectAttributes`指定在重定向的情况下要使用的确切属性集，还要添加flash属性（临时存储在服务器端的属性，以使其在重定向后可用于请求）。请参阅[“将数据传递给重定向目标”一](mvc.html#mvc-redirecting-passing-data)[节](mvc.html#mvc-flash-attributes)和 [第22.6节“使用闪存属性”](mvc.html#mvc-flash-attributes)。
- 命令或表单对象将请求参数绑定到bean属性（通过setter）或直接绑定到字段，具有可自定义的类型转换，具体取决于`@InitBinder` 方法和/或HandlerAdapter配置。查看`webBindingInitializer` 酒店`RequestMappingHandlerAdapter`。默认情况下，使用命令类名称（例如，类型为“some.package.OrderAddress”的命令对象的模型属性“orderAddress”），此类命令对象及其验证结果将作为模型属性公开。该`ModelAttribute`注释可以对方法参数被用于定制所使用的模型的属性名称。
- `org.springframework.validation.Errors`/ `org.springframework.validation.BindingResult`前面的命令或表单对象的验证结果（紧接在前的方法参数）。
- `org.springframework.web.bind.support.SessionStatus`用于将表单处理标记为完成的状态句柄，用于触发已`@SessionAttributes`在处理程序类型级别由注释指示的会话属性的清除。
- `org.springframework.web.util.UriComponentsBuilder` 用于准备相对于当前请求的主机，端口，方案，上下文路径和servlet映射的文字部分的URL的构建器。

该`Errors`或`BindingResult`参数必须遵循被立即绑定方法签名可能有不止一个模型对象和Spring将创建一个单独的模型对象`BindingResult`为他们每个人的实例，以下面的示例将无法正常工作：

**BindingResult和@ModelAttribute的排序无效。** 

```java
@PostMapping
public String processSubmit(@ModelAttribute("pet") Pet pet, Model model, BindingResult result) { ... }
```

注意，`Model`在`Pet`和之间有一个参数`BindingResult`。要使其正常工作，您必须按如下方式重新排序参数：

```java
@PostMapping
 public String processSubmit（ @ModelAttribute（“pet”）Pet pet， BindingResult result，Model model）{...}
```

JDK 1.8的`java.util.Optional`被支撑作为方法参数类型与具有注解`required`的属性（例如`@RequestParam`，`@RequestHeader`等。使用的`java.util.Optional`在这些情况下是等价于具有`required=false`。

#### 支持的方法返回类型

以下是支持的返回类型：

- 一个`ModelAndView`对象，模型隐式地丰富了命令对象和带`@ModelAttribute`注释的引用数据访问器方法的结果。
- 一个`Model`对象，其视图名称通过a隐式确定， `RequestToViewNameTranslator`并且模型隐式地丰富了命令对象和带`@ModelAttribute`注释的引用数据访问器方法的结果。
- 甲`Map`用于曝光模式，与通过隐式地确定的视图名对象`RequestToViewNameTranslator`和该模型隐含地命令对象和结果富集`@ModelAttribute`注释的基准数据存取方法。
- 一个`View`对象，模型通过命令对象和带`@ModelAttribute`注释的引用数据访问器方法隐式确定 。处理程序方法还可以通过声明`Model`参数以编程方式丰富模型（参见上文）。
- 一个`String`值，被解释为逻辑视图名称，模型通过命令对象和带`@ModelAttribute`注释的引用数据访问器方法隐式确定。处理程序方法还可以通过声明`Model`参数以编程方式丰富模型（参见上文）。
- `void`如果方法处理响应本身（通过直接写响应内容，为此目的声明类型`ServletResponse`/ 的参数`HttpServletResponse`）或者是否应该通过`RequestToViewNameTranslator`（不在处理程序方法签名中声明响应参数）隐式确定视图名称 。
- 如果方法带有注释`@ResponseBody`，则返回类型将写入响应HTTP正文。返回值将使用`HttpMessageConverter`s 转换为声明的方法参数类型。请参阅[“使用@ResponseBody注释映射响应正文”一节](mvc.html#mvc-ann-responsebody)。
- 一个`HttpEntity<?>`或`ResponseEntity<?>`目的是提供访问的Servlet响应HTTP标头和内容。实体主体将使用`HttpMessageConverter`s 转换为响应流。请参阅[“使用HttpEntity”一节](mvc.html#mvc-ann-httpentity)。
- 一个`HttpHeaders`返回没有正文的响应的对象。
- `Callable<?>`当应用程序想要在Spring MVC管理的线程中异步生成返回值时，可以返回 A.
- `DeferredResult<?>`当应用程序想要从自己选择的线程生成返回值时，可以返回 A.
- 当应用程序想要从线程池提交中生成值时，可以返回 A `ListenableFuture<?>`或`CompletableFuture<?>`/ `CompletionStage<?>`。
- `ResponseBodyEmitter`可以返回 A 以异步方式将多个对象写入响应; 也支持作为一个内部的身体`ResponseEntity`。
- `SseEmitter`可以返回 An 以异步方式将Server-Sent Events写入响应; 也支持作为一个内部的身体`ResponseEntity`。
- `StreamingResponseBody`可以返回 A 以异步写入响应OutputStream; 也支持作为一个内部的身体`ResponseEntity`。
- 任何其他返回类型都被视为要暴露给视图的单个模型属性，使用`@ModelAttribute`在方法级别指定的属性名称（或基于返回类型类名称的默认属性名称）。该模型隐含地丰富了命令对象和带`@ModelAttribute` 注释的参考数据访问器方法的结果。

#### 使用@RequestParam将请求参数绑定到方法参数

使用`@RequestParam`批注将请求参数绑定到控制器中的方法参数。

以下代码段显示了用法：

```java
@Controller
@RequestMapping("/pets")
@SessionAttributes("pet")
public class EditPetForm {

    // ...

    @GetMapping
    public String setupForm(@RequestParam("petId") int petId, ModelMap model) {
        Pet pet = this.clinic.loadPet(petId);
        model.addAttribute("pet", pet);
        return "petForm";
    }

    // ...

}
```

默认情况下，使用此批注的参数是必需的，但您可以通过将`@RequestParam`'s' `required`属性设置为`false` （例如`@RequestParam(name="id", required=false)`）来指定参数是可选的。

如果不是目标方法参数类型，则自动应用类型转换 `String`。请参阅[“方法参数和类型转换”一节](mvc.html#mvc-ann-typeconversion)。

在an 或 argument `@RequestParam`上使用注释时，将使用所有请求参数填充映射。`Map<String, String>``MultiValueMap<String, String>`

#### 使用@RequestBody注释映射请求正文

所述`@RequestBody`方法参数注释指示方法参数应绑定到HTTP请求正文的值。例如：

```java
@PutMapping("/something")
public void handle(@RequestBody String body, Writer writer) throws IOException {
    writer.write(body);
}
```

您可以使用a将请求主体转换为方法参数`HttpMessageConverter`。 `HttpMessageConverter`负责从HTTP请求消息转换为对象并从对象转换为HTTP响应主体。该`RequestMappingHandlerAdapter`支持`@RequestBody`使用以下默认注释`HttpMessageConverters`：

- `ByteArrayHttpMessageConverter` 转换字节数组。
- `StringHttpMessageConverter` 转换字符串。
- `FormHttpMessageConverter` 将表单数据转换为MultiValueMap <String，String>。
- `SourceHttpMessageConverter` 转换为/从javax.xml.transform.Source转换。

有关这些转换器的更多信息，请参阅[消息转换器](remoting.html#rest-message-conversion)。另请注意，如果使用MVC命名空间或MVC Java配置，则默认情况下会注册更多范围的消息转换器。有关更多信息[，](mvc.html#mvc-config-enable)请参见[第22.16.1节“启用MVC Java配置或MVC XML命名空间”](mvc.html#mvc-config-enable)。

如果您打算读写XML，则需要`MarshallingHttpMessageConverter`使用包中的特定`Marshaller`和`Unmarshaller` 实现进行配置 `org.springframework.oxm`。下面的示例显示了如何在配置中直接执行此操作，但如果您的应用程序是通过MVC命名空间或MVC Java配置配置的，请参见[第22.16.1节“启用MVC Java配置或MVC XML命名空间”](mvc.html#mvc-config-enable)。

```xml
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
    <property name="messageConverters">
        <util:list id="beanList">
            <ref bean="stringHttpMessageConverter"/>
            <ref bean="marshallingHttpMessageConverter"/>
        </util:list>
    </property
</bean>

<bean id="stringHttpMessageConverter"
        class="org.springframework.http.converter.StringHttpMessageConverter"/>

<bean id="marshallingHttpMessageConverter"
        class="org.springframework.http.converter.xml.MarshallingHttpMessageConverter">
    <property name="marshaller" ref="castorMarshaller"/>
    <property name="unmarshaller" ref="castorMarshaller"/>
</bean>

<bean id="castorMarshaller" class="org.springframework.oxm.castor.CastorMarshaller"/>
```

一种`@RequestBody`方法参数可以与进行注释`@Valid`，在这种情况下，它将使用配置的验证`Validator`实例。使用MVC命名空间或MVC Java配置时，假设JSR-303实现在类路径上可用，则自动配置JSR-303验证器。

就像`@ModelAttribute`参数一样，`Errors`可以使用参数来检查错误。如果没有宣布这样的论证，`MethodArgumentNotValidException` 将会提出一个论点。该异常在处理中`DefaultHandlerExceptionResolver`，它将`400`错误发送回客户端。

有关通过MVC命名空间或MVC Java配置配置消息转换器和验证器的信息，另请参见[第22.16.1节“启用MVC Java配置或MVC XML命名空间”](mvc.html#mvc-config-enable)。

#### 使用@ResponseBody注释映射响应正文

该`@ResponseBody`注释是类似`@RequestBody`。此注释可以放在方法上，并指示应将返回类型直接写入HTTP响应主体（而不是放在模型中，或解释为视图名称）。例如：

```java
@GetMapping("/something")
@ResponseBody
public String helloWorld() {
    return "Hello World";
}
```

上面的示例将导致文本`Hello World`被写入HTTP响应流。

与使用一样`@RequestBody`，Spring将返回的对象转换为响应主体`HttpMessageConverter`。有关这些转换器的更多信息，请参阅上一节和[消息转换器](remoting.html#rest-message-conversion)。

#### 使用@RestController批注创建REST控制器

控制器实现REST API是一个非常常见的用例，因此只提供JSON，XML或自定义MediaType内容。为方便起见，您可以使用注释控制器类，而不是使用所有`@RequestMapping`方法`@ResponseBody`注释`@RestController`。

[`@RestController`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/web/bind/annotation/RestController.html) 是一个结合了`@ResponseBody`和的刻板印象注释`@Controller`。更重要的是，它为您的Controller提供了更多的意义，并且可能在框架的未来版本中带来额外的语义。

与常规`@Controller`s一样，a `@RestController`可以由bean `@ControllerAdvice`或`@RestControllerAdvice`bean 辅助 。有关 更多详细信息，请参阅[“使用@ControllerAdvice和@RestControllerAdvice建议控制器”](mvc.html#mvc-ann-controller-advice)一节。

#### 使用HttpEntity

在`HttpEntity`类似于`@RequestBody`和`@ResponseBody`。除了访问请求和响应主体之外，`HttpEntity`（以及特定于响应的子类`ResponseEntity`）还允许访问请求和响应头，如下所示：

```java
@RequestMapping("/something")
public ResponseEntity<String> handle(HttpEntity<byte[]> requestEntity) throws UnsupportedEncodingException {
    String requestHeader = requestEntity.getHeaders().getFirst("MyRequestHeader"));
    byte[] requestBody = requestEntity.getBody();

    // do something with request header and body

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("MyResponseHeader", "MyValue");
    return new ResponseEntity<String>("Hello World", responseHeaders, HttpStatus.CREATED);
}
```

上面的示例获取`MyRequestHeader`请求标头的值，并将主体作为字节数组读取。它将`MyResponseHeader`响应添加到`Hello World`响应流中，并将响应状态代码设置为201（已创建）。

与`@RequestBody`和一样`@ResponseBody`，Spring用于`HttpMessageConverter`转换请求和响应流。有关这些转换器的更多信息，请参阅上一节和[消息转换器](remoting.html#rest-message-conversion)。

#### 在方法上使用@ModelAttribute

该`@ModelAttribute`注释可以对方法或方法的参数来使用。本节解释了它在方法上的用法，下一节解释了它在方法参数上的用法。

一个`@ModelAttribute`上的方法指示方法的目的是增加一个或多个模型的属性。此类方法支持与方法相同的参数类型，`@RequestMapping` 但不能直接映射到请求。而是`@ModelAttribute`在`@RequestMapping`方法之前，在同一个控制器中调用控制器中的方法。几个例子：

```java
// Add one attribute
// The return value of the method is added to the model under the name "account"
// You can customize the name via @ModelAttribute("myAccount")

@ModelAttribute
public Account addAccount(@RequestParam String number) {
    return accountManager.findAccount(number);
}

// Add multiple attributes

@ModelAttribute
public void populateModel(@RequestParam String number, Model model) {
    model.addAttribute(accountManager.findAccount(number));
    // add more ...
}
```

`@ModelAttribute`方法用于使用常用属性填充模型，例如用状态或宠物类型填充下拉列表，或者检索像Account这样的命令对象，以便使用它来表示HTML表单上的数据。后一种情况将在下一节进一步讨论。

请注意两种风格的`@ModelAttribute`方法。在第一个中，该方法通过返回隐式添加属性。在第二种方法中，该方法接受`Model`并向其添加任意数量的模型属性。您可以根据需要在两种样式之间进行选择。

控制器可以有任意数量的`@ModelAttribute`方法。`@RequestMapping`在同一控制器的方法之前调用所有这些方法。

`@ModelAttribute`方法也可以在`@ControllerAdvice`注释类中定义，并且此类方法适用于许多控制器。有关更多详细信息，请参阅[“使用@ControllerAdvice和@RestControllerAdvice建议控制器”](mvc.html#mvc-ann-controller-advice)一节。

未明确指定模型属性名称时会发生什么？在这种情况下，会根据模型属性的类型为模型属性指定默认名称。例如，如果方法返回类型的对象`Account`，则使用的默认名称是“account”。您可以通过`@ModelAttribute`注释的值更改它。如果直接向属性添加属性`Model`，请使用适当的重载`addAttribute(..)`方法 - 即使用或不使用属性名称。

该`@ModelAttribute`批注可在使用`@RequestMapping`方法为好。在这种情况下，`@RequestMapping`方法的返回值被解释为模型属性而不是视图名称。然后根据视图名称约定派生视图名称，就像返回的方法一样`void` - 请参见[第22.13.3节“默认视图名称”](mvc.html#mvc-coc-r2vnt)。

#### 在方法参数上使用@ModelAttribute

如前一节所述，`@ModelAttribute`可以在方法或方法参数上使用。本节介绍其在方法参数上的用法。

一个`@ModelAttribute`上的方法参数指示参数应该从模型中检索。如果模型中不存在，则应首先实例化参数，然后将其添加到模型中。一旦出现在模型中，参数的字段应该从具有匹配名称的所有请求参数中填充。这在Spring MVC中称为数据绑定，这是一种非常有用的机制，可以使您不必单独解析每个表单字段。

```java
@PostMapping("/owners/{ownerId}/pets/{petId}/edit")
public String processSubmit(@ModelAttribute Pet pet) { }
```

鉴于以上示例，Pet实例可以来自何处？有几种选择：

- 由于使用它可能已经在模型中`@SessionAttributes` - 请参阅 [“使用@SessionAttributes在请求之间的HTTP会话中存储模型属性”一节](mvc.html#mvc-ann-sessionattrib)。
- 由于`@ModelAttribute`同一控制器中的方法，它可能已经在模型中 - 如上一节中所述。
- 可以基于URI模板变量和类型转换器来检索它（下面更详细地解释）。
- 它可以使用其默认构造函数进行实例化。

一种`@ModelAttribute`方法是从数据库中检索，其可以任选通过使用被存储请求之间的属性的常用方法 `@SessionAttributes`。在某些情况下，使用URI模板变量和类型转换器检索属性可能很方便。这是一个例子：

```java
@PutMapping("/accounts/{account}")
public String save(@ModelAttribute("account") Account account) {
    // ...
}
```

在此示例中，模型属性的名称（即“account”）与URI模板变量的名称匹配。如果您注册`Converter<String, Account>`可以将 `String`帐户值转换为`Account`实例，那么上面的示例将无需`@ModelAttribute`方法即可运行。

下一步是数据绑定。该`WebDataBinder`级比赛要求参数名称-包括查询字符串参数和表单域-以模拟通过名称属性字段。在必要时应用类型转换（从String到目标字段类型）后填充匹配字段。[第9章*验证，数据绑定和类型转换*](validation.html)中介绍了数据绑定和验证 。自定义控制器级别的数据绑定过程在[“自定义WebDataBinder初始化”一节中介绍](mvc.html#mvc-ann-webdatabinder)。

由于数据绑定，可能存在错误，例如缺少必填字段或类型转换错误。要检查此类错误，请在`BindingResult`参数后面添加一个`@ModelAttribute`参数：

```java
@PostMapping("/owners/{ownerId}/pets/{petId}/edit")
public String processSubmit(@ModelAttribute("pet") Pet pet, BindingResult result) {

    if (result.hasErrors()) {
        return "petForm";
    }

    // ...

}
```

使用a，`BindingResult`您可以检查是否发现错误在哪种情况下，通常使用Spring的`<errors>` 表单标记呈现相同的表单，其中可以显示错误。

请注意，在某些情况下，在没有数据绑定的情况下访问模型中的属性可能很有用。对于这种情况，您可以将其`Model`注入控制器或者`binding`在注释上使用标志：

```java
@ModelAttribute
public AccountForm setUpForm() {
    return new AccountForm();
}

@ModelAttribute
public Account findAccount(@PathVariable String accountId) {
    return accountRepository.findOne(accountId);
}

@PostMapping("update")
public String update(@Valid AccountUpdateForm form, BindingResult result,
        @ModelAttribute(binding=false) Account account) {

    // ...
}
```

除了数据绑定之外，您还可以使用自己的自定义验证程序调用验证，该验证程序传递`BindingResult`用于记录数据绑定错误的验证程序。这允许数据绑定和验证错误在一个地方累积，然后报告给用户：

```java
@PostMapping("/owners/{ownerId}/pets/{petId}/edit")
public String processSubmit(@ModelAttribute("pet") Pet pet, BindingResult result) {

    new PetValidator().validate(pet, result);
    if (result.hasErrors()) {
        return "petForm";
    }

    // ...

}
```

或者，您可以通过添加JSR-303 `@Valid` 注释自动调用验证：

```java
@PostMapping("/owners/{ownerId}/pets/{petId}/edit")
public String processSubmit(@Valid @ModelAttribute("pet") Pet pet, BindingResult result) {

    if (result.hasErrors()) {
        return "petForm";
    }

    // ...

}
```

有关如何配置和使用验证的详细信息，请参见[第9.8节“Spring验证”](validation.html#validation-beanvalidation)和[第9章，*验证，数据绑定和类型转换*](validation.html)。

#### 使用@SessionAttributes在请求之间的HTTP会话中存储模型属性

类型级别`@SessionAttributes`注释声明特定处理程序使用的会话属性。这通常会列出模型属性的名称或模型属性的类型，这些属性应该透明地存储在会话或某些会话存储中，作为后续请求之间的表单支持bean。

以下代码段显示了此批注的用法，指定了模型属性名称：

```java
@Controller
@RequestMapping("/editPet.do")
@SessionAttributes("pet")
public class EditPetForm {
    // ...
}
```

#### 使用@SessionAttribute访问预先存在的全局会话属性

如果您需要访问全局管理的预先存在的会话属性，即在控制器外部（例如通过过滤器），并且可能存在或不存在，请`@SessionAttribute`在方法参数上使用注释：

```java
@RequestMapping("/")
public String handle(@SessionAttribute User user) {
    // ...
}
```

对于需要添加或删除会话属性的用例，请考虑注入 `org.springframework.web.context.request.WebRequest`或 `javax.servlet.http.HttpSession`注入控制器方法。

为模型的临时存储在会话属性作为控制器工作流程的一部分可以考虑使用`SessionAttributes`在如所描述的 [被称为“在请求之间的HTTP会话使用@SessionAttributes存储模型属性”的部分](mvc.html#mvc-ann-sessionattrib)。

#### 使用@RequestAttribute访问请求属性

到类似`@SessionAttribute`的`@RequestAttribute`注释可以被用于访问由滤波器或拦截器创建的预先存在的请求属性：

```java
@RequestMapping("/")
public String handle(@RequestAttribute Client client) {
    // ...
}
```

#### 使用“application / x-www-form-urlencoded”数据

前面几节介绍了如何使用`@ModelAttribute`来支持来自浏览器客户端的表单提交请求。建议将相同的注释用于非浏览器客户端的请求。但是，在处理HTTP PUT请求时，有一个显着的区别。浏览器可以通过HTTP GET或HTTP POST提交表单数据。非浏览器客户端也可以通过HTTP PUT提交表单。这提出了一个挑战，因为Servlet规范要求`ServletRequest.getParameter*()`一系列方法仅支持HTTP POST的表单字段访问，而不支持HTTP PUT。

为了支持HTTP PUT和PATCH请求，`spring-web`模块提供了过滤器 `HttpPutFormContentFilter`，可以在以下位置配置`web.xml`：

```xml
<filter>
    <filter-name>httpPutFormFilter</filter-name>
    <filter-class>org.springframework.web.filter.HttpPutFormContentFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>httpPutFormFilter</filter-name>
    <servlet-name>dispatcherServlet</servlet-name>
</filter-mapping>

<servlet>
    <servlet-name>dispatcherServlet</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
</servlet>
```

上面的过滤器截取带有内容类型的HTTP PUT和PATCH请求， `application/x-www-form-urlencoded`从请求的主体读取表单数据，并`ServletRequest`通过`ServletRequest.getParameter*()`方法族包装以使表单数据可用 。

由于`HttpPutFormContentFilter`消耗了请求的主体，因此不应该为依赖于其他转换器的PUT或PATCH URL配置它`application/x-www-form-urlencoded`。这包括`@RequestBody MultiValueMap<String, String>`和`HttpEntity<MultiValueMap<String, String>>`。

#### 使用@CookieValue注释映射cookie值

该`@CookieValue`注释允许的方法参数绑定到一个HTTP cookie的值。

让我们考虑以下cookie已收到http请求：

```bash
JSESSIONID = 415A4AC178C59DACE0B2C9CA727CDD84
```

以下代码示例演示了如何获取`JSESSIONID`cookie 的值：

```java
@RequestMapping("/displayHeaderInfo.do")
public void displayHeaderInfo(@CookieValue("JSESSIONID") String cookie) {
    //...
}
```

如果不是目标方法参数类型，则自动应用类型转换 `String`。请参阅[“方法参数和类型转换”一节](mvc.html#mvc-ann-typeconversion)。

Servlet和Portlet环境中带注释的处理程序方法支持此注释。

#### 使用@RequestHeader注释映射请求标头属性

该`@RequestHeader`注释允许的方法参数绑定到的请求报头。

这是一个示例请求标头：

```bash
Host                    localhost:8080
Accept                  text/html,application/xhtml+xml,application/xml;q=0.9
Accept-Language         fr,en-gb;q=0.7,en;q=0.3
Accept-Encoding         gzip,deflate
Accept-Charset          ISO-8859-1,utf-8;q=0.7,*;q=0.7
Keep-Alive              300
```

以下代码示例演示了如何获取`Accept-Encoding`和 `Keep-Alive`标头的值：

```java
@RequestMapping("/displayHeaderInfo.do")
public void displayHeaderInfo(@RequestHeader("Accept-Encoding") String encoding,
        @RequestHeader("Keep-Alive") long keepAlive) {
    //...
}
```

如果方法参数不是，则自动应用类型转换`String`。请参阅 [“方法参数和类型转换”一节](mvc.html#mvc-ann-typeconversion)。

当`@RequestHeader`注解上的使用`Map<String, String>`， `MultiValueMap<String, String>`或`HttpHeaders`参数，则地图被填充有所有标头值。

内置支持可用于将逗号分隔的字符串转换为字符串数组/集合或类型转换系统已知的其他类型。例如带注释的方法参数`@RequestHeader("Accept")`可以是类型的 `String`，但也`String[]`还是`List<String>`。

Servlet和Portlet环境中带注释的处理程序方法支持此注释。

#### 方法参数和类型转换

从请求中提取的基于字符串的值（包括请求参数，路径变量，请求标头和cookie值）可能需要转换为方法参数或字段的目标类型（例如，将请求参数绑定到参数中的字段`@ModelAttribute`）他们必然会。如果目标类型不是`String`，Spring会自动转换为适当的类型。支持所有简单类型，如int，long，Date等。您可以进一步自定义通过转换过程`WebDataBinder`（见[称为“定制WebDataBinder初始化”一节](mvc.html#mvc-ann-webdatabinder)），或者通过注册`Formatters`与`FormattingConversionService`（见[第9.6节，“春字段格式”](validation.html#format)）。

#### 自定义WebDataBinder初始化

要通过Spring自定义与PropertyEditors的请求参数绑定 `WebDataBinder`，您可以`@InitBinder`在控制器中使用带注释的方法，`@InitBinder`在`@ControllerAdvice`类中使用方法 ，或者提供自定义 `WebBindingInitializer`。有关更多详细信息，请参阅[“使用@ControllerAdvice和@RestControllerAdvice建议控制器”](mvc.html#mvc-ann-controller-advice)一节。

##### 使用@InitBinder自定义数据绑定

注释控制器方法`@InitBinder`允许您直接在控制器类中配置Web数据绑定。`@InitBinder`标识初始化的方法，该方法`WebDataBinder`将用于填充带注释的处理程序方法的命令和表单对象参数。

此类init-binder方法支持方法支持的所有参数`@RequestMapping`，但命令/表单对象和相应的验证结果对象除外。Init-binder方法不能有返回值。因此，它们通常被声明为`void`。典型的参数包括`WebDataBinder`与`WebRequest`或 结合使用`java.util.Locale`，允许代码注册特定于上下文的编辑器。

以下示例演示如何使用`@InitBinder`配置 `CustomDateEditor`所有`java.util.Date`表单属性。

```java
@Controller
public class MyFormController {

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    // ...
}
```

或者，从Spring 4.2开始，考虑使用`addCustomFormatter`指定 `Formatter`实现而不是`PropertyEditor`实例。如果你碰巧`Formatter`在共享`FormattingConversionService`中也有一个基于设置的设置， 这一点特别有用，同样的方法可以重复用于特定于控制器的绑定规则调整。

```java
@Controller
public class MyFormController {

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd"));
    }

    // ...
}
```

##### 配置自定义WebBindingInitializer

要外部化数据绑定初始化，您可以提供`WebBindingInitializer`接口的自定义实现，然后通过为其提供自定义Bean配置来启用该实现`AnnotationMethodHandlerAdapter`，从而覆盖默认配置。

以下来自PetClinic应用程序的示例显示了使用`WebBindingInitializer`接口 的自定义实现的`org.springframework.samples.petclinic.web.ClinicBindingInitializer`配置，该配置配置了几个PetClinic控制器所需的PropertyEditors。

```xml
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
    <property name="cacheSeconds" value="0"/>
    <property name="webBindingInitializer">
        <bean class="org.springframework.samples.petclinic.web.ClinicBindingInitializer"/>
    </property>
</bean>
```

`@InitBinder`方法也可以在`@ControllerAdvice`注释类中定义，在这种情况下，它们适用于匹配的控制器。这提供了使用a的替代方法 `WebBindingInitializer`。有关更多详细信息，请参阅[“使用@ControllerAdvice和@RestControllerAdvice建议控制器”](mvc.html#mvc-ann-controller-advice)一节。

#### 使用@ControllerAdvice和@RestControllerAdvice向控制器提供建议

的`@ControllerAdvice`注释是一个组件注释允许实现类自动检测通过类路径扫描。使用MVC命名空间或MVC Java配置时会自动启用它。

注释的类`@ControllerAdvice`可以包含`@ExceptionHandler`， `@InitBinder`和`@ModelAttribute`注释方法，这些方法将应用于 `@RequestMapping`跨所有控制器层次结构的方法，而不是声明它们的控制器层次结构。

`@RestControllerAdvice`是一种替代`@ExceptionHandler`方法，其中方法`@ResponseBody`默认采用语义。

二者`@ControllerAdvice`并`@RestControllerAdvice`可以针对控制器的一个子集：

```java
// Target all Controllers annotated with @RestController
@ControllerAdvice(annotations = RestController.class)
public class AnnotationAdvice {}

// Target all Controllers within specific packages
@ControllerAdvice("org.example.controllers")
public class BasePackageAdvice {}

// Target all Controllers assignable to specific classes
@ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})
public class AssignableTypesAdvice {}
```

查看 [`@ControllerAdvice` 文档](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html)以获取更多详细信息。

#### Jackson Serialization View Support

有时可以将上下文中将序列化为HTTP响应主体的对象进行过滤。为了提供这样的功能，Spring MVC内置支持使用[Jackson的序列化视图](https://wiki.fasterxml.com/JacksonJsonViews)进行渲染。

要将它与返回的`@ResponseBody`控制器方法或控制器方法一起使用 `ResponseEntity`，只需添加`@JsonView`带有类参数的注释，该类参数指定要使用的视图类或接口：

```java
@RestController
public class UserController {

    @GetMapping("/user")
    @JsonView(User.WithoutPasswordView.class)
    public User getUser() {
        return new User("eric", "7!jd#h23");
    }
}

public class User {

    public interface WithoutPasswordView {};
    public interface WithPasswordView extends WithoutPasswordView {};

    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @JsonView(WithoutPasswordView.class)
    public String getUsername() {
        return this.username;
    }

    @JsonView(WithPasswordView.class)
    public String getPassword() {
        return this.password;
    }
}
```

请注意，尽管`@JsonView`允许指定多个类，但只有一个类参数才支持在控制器方法上使用。如果需要启用多个视图，请考虑使用复合接口。

对于依赖于视图分辨率的控制器，只需将序列化视图类添加到模型中：

```java
@Controller
public class UserController extends AbstractController {

    @GetMapping("/user")
    public String getUser(Model model) {
        model.addAttribute("user", new User("eric", "7!jd#h23"));
        model.addAttribute(JsonView.class.getName(), User.WithoutPasswordView.class);
        return "userView";
    }
}
```

#### Jackson JSONP Support

为了启用[JSONP](https://en.wikipedia.org/wiki/JSONP)支持`@ResponseBody` 和`ResponseEntity`方法，声明一个如下所示`@ControllerAdvice`扩展的bean， `AbstractJsonpResponseBodyAdvice`其中构造函数参数指示JSONP查询参数名称：

```java
@ControllerAdvice
public class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {

    public JsonpAdvice() {
        super("callback");
    }
}
```

对于依赖于视图解析的控制器，当请求具有名为`jsonp`或的查询参数时，将自动启用JSONP `callback`。这些名称可以通过`jsonpParameterNames`财产定制。

从Spring Framework 4.3.18开始，不推荐使用JSONP支持，从Spring Framework 5.1开始，将删除JSONP支持，而应使用[CORS](cors.html)。

### 22.3.4异步请求处理

Spring MVC 3.2引入了基于Servlet 3的异步请求处理。通常，控制器方法可以返回a `java.util.concurrent.Callable`并从Spring MVC托管线程生成返回值，而不是像往常一样 返回值。同时，主要的Servlet容器线程被退出并释放，并允许处理其他请求。Spring MVC `Callable`在a的帮助下调用一个单独的线程， `TaskExecutor`当`Callable`返回时，请求被调度回Servlet容器以使用返回的值恢复处理`Callable`。以下是此类控制器方法的示例：

```java
@PostMapping
public Callable<String> processUpload(final MultipartFile file) {

    return new Callable<String>() {
        public String call() throws Exception {
            // ...
            return "someView";
        }
    };

}
```

另一种选择是控制器方法返回一个实例`DeferredResult`。在这种情况下，返回值也将从任何线程产生，即不由Spring MVC管理的线程。例如，可以响应于诸如JMS消息，计划任务等的一些外部事件来产生结果。以下是此类控制器方法的示例：

```java
@RequestMapping("/quotes")
@ResponseBody
public DeferredResult<String> quotes() {
    DeferredResult<String> deferredResult = new DeferredResult<String>();
    // Save the deferredResult somewhere..
    return deferredResult;
}

// In some other thread...
deferredResult.setResult(data);
```

如果不了解Servlet 3.0异步请求处理功能，这可能很难理解。阅读这一点肯定会有所帮助。以下是有关基础机制的一些基本事实：

- A `ServletRequest`可以通过调用置于异步模式`request.startAsync()`。这样做的主要作用是Servlet以及任何过滤器都可以退出，但响应将保持打开状态以允许稍后完成处理。
- 对`request.startAsync()`返回的调用`AsyncContext`，可用于进一步控制异步处理。例如，它提供的方法`dispatch`类似于Servlet API的转发，但它允许应用程序在Servlet容器线程上恢复请求处理。
- 在`ServletRequest`提供对当前`DispatcherType`可用于处理所述初始请求，一个异步调度，正向，以及其他的调度类型之间进行区分。

考虑到上述情况，以下是异步请求处理的事件序列`Callable`：

- 控制器返回一个`Callable`。
- Spring MVC启动异步处理并将其提交`Callable`到a `TaskExecutor`以在单独的线程中进行处理。
- 将`DispatcherServlet`所有过滤器的退出Servlet容器线程，但反应仍然开放。
- 所述`Callable`产生的结果和Spring MVC分派请求回Servlet容器以恢复处理。
- 在`DispatcherServlet`再次调用和处理与来自所述异步生产结果恢复`Callable`。

序列`DeferredResult`非常相似，除了应用程序从任何线程产生异步结果：

- Controller返回a `DeferredResult`并将其保存在可以访问它的某个内存中队列或列表中。
- Spring MVC启动异步处理。
- 在`DispatcherServlet`所有配置的过滤器的退出请求处理线程，但反应仍然开放。
- 应用程序`DeferredResult`从一些线程设置，Spring MVC将请求调度回Servlet容器。
- 在`DispatcherServlet`再次调用和处理与异步生产结果恢复。

有关异步请求处理的动机和何时或为何使用它的进一步背景，请阅读 [此博客文章系列](https://spring.io/blog/2012/05/07/spring-mvc-3-2-preview-introducing-servlet-3-async-support)。

#### 异步请求的异常处理

如果`Callable`从控制器方法返回的内容在执行时引发异常会发生什么？简短回答与控制器方法引发异常时发生的情况相同。它通过常规异常处理机制。更长的解释是，当`Callable` 引发异常Spring MVC调度到Servlet容器时，`Exception`结果导致使用`Exception`而不是控制器方法返回值来恢复请求处理。使用时，`DeferredResult`您可以选择是呼叫 `setResult`还是`setErrorResult`使用`Exception`实例。

#### 拦截异步请求

甲`HandlerInterceptor`还可以实现`AsyncHandlerInterceptor`以执行`afterConcurrentHandlingStarted`回调，这就是所谓的代替`postHandle`和`afterCompletion`处理开始异步时。

A `HandlerInterceptor`还可以注册a `CallableProcessingInterceptor` 或a `DeferredResultProcessingInterceptor`以便更深入地集成异步请求的生命周期，例如处理超时事件。有关`AsyncHandlerInterceptor` 更多详细信息，请参阅Javadoc 。

该`DeferredResult`类型还提供诸如`onTimeout(Runnable)` 和的方法`onCompletion(Runnable)`。有关`DeferredResult`更多详细信息，请参阅Javadoc 。

使用时，`Callable`您可以使用其实例来包装它，该实例`WebAsyncTask` 还提供超时和完成的注册方法。

#### HTTP流媒体

控制器方法可以异步使用`DeferredResult`和`Callable`生成其返回值，并且可以用于实现诸如[长轮询之类的](https://spring.io/blog/2012/05/08/spring-mvc-3-2-preview-techniques-for-real-time-updates/)技术， 其中服务器可以尽快将事件推送到客户端。

如果您想在单个HTTP响应上推送多个事件，该怎么办？这是与“长轮询”相关的技术，称为“HTTP流”。Spring MVC通过`ResponseBodyEmitter`返回值类型实现了这一点，该类型可用于发送多个对象，而不是通常情况下的一个`@ResponseBody`，其中发送的每个Object都被写入带有的响应`HttpMessageConverter`。

这是一个例子：

```java
@RequestMapping("/events")
public ResponseBodyEmitter handle() {
    ResponseBodyEmitter emitter = new ResponseBodyEmitter();
    // Save the emitter somewhere..
    return emitter;
}

// In some other thread
emitter.send("Hello once");

// and again later on
emitter.send("Hello again");

// and done at some point
emitter.complete();
```

请注意，`ResponseBodyEmitter`也可以将其用作a `ResponseEntity`中的正文 ，以便自定义响应的状态和标题。

#### 使用服务器发送事件的HTTP流式传输

`SseEmitter`是`ResponseBodyEmitter`为[Server-Sent Events](https://www.w3.org/TR/eventsource/)提供支持 的子类。服务器发送的事件只是同一“HTTP Streaming”技术的另一种变体，除了从服务器推送的事件是根据W3C Server-Sent Events规范格式化的。

服务器发送事件可用于其预期目的，即将事件从服务器推送到客户端。在Spring MVC中很容易做到，只需要返回一个类型的值`SseEmitter`。

但请注意，Internet Explorer不支持服务器发送事件，对于更高级的Web应用程序消息传递方案（如在线游戏，协作，财务应用程序等），最好考虑Spring的WebSocket支持，包括SockJS样式的WebSocket仿真，可以追溯到各种各样的浏览器（包括Internet Explorer）以及更高级别的消息传递模式，用于通过更加以消息传递为中心的体系结构中的发布 - 订阅模型与客户端进行交互。有关此问题的进一步背景，请参阅 [以下博客文章](https://blog.pivotal.io/pivotal/products/websocket-architecture-in-spring-4-0)。

#### HTTP流直接到OutputStream

`ResponseBodyEmitter`允许通过将对象写入响应来发送事件`HttpMessageConverter`。这可能是最常见的情况，例如在编写JSON数据时。但是，有时绕过消息转换并直接写入响应`OutputStream` （例如文件下载）很有用。这可以在`StreamingResponseBody`返回值类型的帮助下完成 。

这是一个例子：

```java
@RequestMapping("/download")
public StreamingResponseBody handle() {
    return new StreamingResponseBody() {
        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
            // write...
        }
    };
}
```

请注意，`StreamingResponseBody`也可以将其用作a `ResponseEntity`中的正文 ，以便自定义响应的状态和标题。

#### 配置异步请求处理

##### Servlet容器配置

对于配置`web.xml`为确保更新到3.0版的应用程序：

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            http://java.sun.com/xml/ns/javaee
            https://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
    version="3.0">

    ...

</web-app>
```

必须`DispatcherServlet`通过 `<async-supported>true</async-supported>`子元素启用异步支持`web.xml`。此外，任何`Filter`参与asyncrequest处理的人都必须配置为支持ASYNC调度程序类型。为Spring框架提供的所有过滤器启用ASYNC调度程序类型应该是安全的，因为它们通常会扩展`OncePerRequestFilter`，并且运行时检查过滤器是否需要参与异步调度。

下面是一些web.xml配置示例：

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
            http://java.sun.com/xml/ns/javaee
            https://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
    version="3.0">

    <filter>
        <filter-name>Spring OpenEntityManagerInViewFilter</filter-name>
        <filter-class>org.springframework.~.OpenEntityManagerInViewFilter</filter-class>
        <async-supported>true</async-supported>
    </filter>

    <filter-mapping>
        <filter-name>Spring OpenEntityManagerInViewFilter</filter-name>
        <url-pattern>/*</url-pattern>
        <dispatcher>REQUEST</dispatcher>
        <dispatcher>ASYNC</dispatcher>
    </filter-mapping>

</web-app>
```

如果使用Servlet 3，基于Java的配置，例如via `WebApplicationInitializer`，您还需要设置“asyncSupported”标志以及ASYNC调度程序类型`web.xml`。要简化所有这些配置，请考虑扩展 `AbstractDispatcherServletInitializer`或更好地 `AbstractAnnotationConfigDispatcherServletInitializer`自动设置这些选项并使注册`Filter`实例变得非常容易。

##### Spring MVC配置

MVC Java配置和MVC命名空间提供了配置异步请求处理的选项。`WebMvcConfigurer`具有该方法 `configureAsyncSupport`，同时`<mvc:annotation-driven>`具有`<async-support>`子元素。

这些允许您配置用于异步请求的默认超时值，如果未设置，则取决于底层Servlet容器（例如，Tomcat上的10秒）。您还可以配置`AsyncTaskExecutor`用于执行`Callable`从控制器方法返回的实例。强烈建议配置此属性，因为默认情况下Spring MVC使用`SimpleAsyncTaskExecutor`。MVC Java配置和MVC命名空间还允许您注册`CallableProcessingInterceptor`和 `DeferredResultProcessingInterceptor`实例。

如果需要覆盖特定的默认超时值`DeferredResult`，可以使用适当的类构造函数来实现。类似地，对于a `Callable`，您可以将其包装在一个`WebAsyncTask`并使用适当的类构造函数来自定义超时值。类构造函数`WebAsyncTask`也允许提供 `AsyncTaskExecutor`。

### 22.3.5测试控制器

该`spring-test`模块为测试带注释的控制器提供了一流的支持。请参见[第15.6节“Spring MVC测试框架”](integration-testing.html#spring-mvc-test-framework)。