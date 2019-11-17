# 22.13 约定优于配置

对于许多项目而言，坚持已建立的约定并具有合理的默认值正是它们（项目）所需要的，而Spring Web MVC现在明确支持*约定优于配置*。这意味着如果你建立了一组命名约定等，你可以*大大*减少设置处理程序映射，查看解析器，`ModelAndView`实例等所需的配置量 。这对于快速原型设计，如果您选择将其推向生产阶段，还可以在代码库中提供一定程度的（始终是良好的）一致性。

Convention-over-configuration支持解决了MVC的三个核心领域：模型，视图和控制器。

### 22.13.1 Controller ControllerClassNameHandlerMapping

该`ControllerClassNameHandlerMapping`班是一个`HandlerMapping`使用惯例来确定请求的URL和之间的映射实现`Controller` 是要处理这些请求的情况。

考虑以下简单`Controller`实现。特别注意 班级*名称*。

```java
public class ViewShoppingCartController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
        // the implementation is not hugely important for this example...
    }

}
```

以下是相应的Spring Web MVC配置文件的片段：

```xml
<bean class="org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping"/>

<bean id="viewShoppingCart" class="x.y.z.ViewShoppingCartController">
    <!-- inject dependencies as required... -->
</bean>
```

在`ControllerClassNameHandlerMapping`找出所有的处理程序（或 `Controller`在其应用上下文定义的）豆和剥离`Controller`掉，以限定其处理程序映射的名称。因此，`ViewShoppingCartController`映射到 `/viewshoppingcart*`请求URL。

让我们看一些更多的例子，让中心思想立刻变得熟悉。（注意URL中的所有小写，与驼峰`Controller`类名称相反。）

- `WelcomeController`映射到`/welcome*`请求URL
- `HomeController`映射到`/home*`请求URL
- `IndexController`映射到`/index*`请求URL
- `RegisterController`映射到`/register*`请求URL

对于`MultiActionController`处理程序类，生成的映射稍微复杂一些。`Controller`以下示例中的名称假定为`MultiActionController`实现：

- `AdminController`映射到`/admin/*`请求URL
- `CatalogController`映射到`/catalog/*`请求URL

如果您遵循将`Controller`实现命名为 的惯例`xxxController`，则*可以省去*`ControllerClassNameHandlerMapping`定义和维护潜在*looooong*`SimpleUrlHandlerMapping`（或类似）的*繁琐*。

本`ControllerClassNameHandlerMapping`类扩展`AbstractHandlerMapping`基类，所以你可以定义`HandlerInterceptor`实例和一切，就像你与许多其他`HandlerMapping`的实现。

### 22.13.2模型ModelMap（ModelAndView）

该`ModelMap`班本质上是一种荣耀`Map`，可以使补充说，是要显示（或上）一个对象`View`坚持一个共同的命名约定。考虑以下`Controller`实现; 请注意，对象被添加到`ModelAndView`没有指定任何关联名称的对象中 。

```java
public class DisplayShoppingCartController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {

        List cartItems = // get a List of CartItem objects
        User user = // get the User doing the shopping

        ModelAndView mav = new ModelAndView("displayShoppingCart"); <-- the logical view name

        mav.addObject(cartItems); <-- look ma, no name, just the object
        mav.addObject(user); <-- and again ma!

        return mav;
    }
}
```

的`ModelAndView`类使用一个`ModelMap`类，它是一个自定义的`Map`，可自动生成用于当对象被添加到它的对象的键实现。确定添加对象的名称的策略是，在标量对象的情况下，例如`User`，使用对象类的短类名。以下示例是为放入`ModelMap`实例的标量对象生成的名称。

- `x.y.User`添加 的实例将`user`生成名称。
- `x.y.Registration`添加 的实例将`registration`生成名称。
- `x.y.Foo`添加 的实例将`foo`生成名称。
- `java.util.HashMap`添加 的实例将`hashMap`生成名称。在这种情况下，您可能希望明确说明名称，因为`hashMap`它不够直观。
- 添加`null`将导致`IllegalArgumentException`被抛出。如果您要添加的对象（或多个对象）可能是`null`，那么您还需要明确该名称。

**什么，没有自动复数？**

Spring Web MVC的配置约定支持不支持自动复数。也就是说，你不能添加`List`的`Person`对象的`ModelAndView` ，并有生成的名字会`people`。

这个决定是经过一番辩论后做出的，最后的“最小惊喜原则”获胜。

添加a `Set`或a 后生成名称的策略`List`是查看集合，获取集合中第一个对象的短类名，并使用`List`附加到名称的名称。这同样适用于数组，但是对于数组，没有必要查看数组内容。一些示例将使集合的名称生成的语义更清晰：

- 添加`x.y.User[]`了零个或多个`x.y.User`元素 的数组将`userList`生成名称 。
- 添加`x.y.Foo[]`了零个或多个`x.y.User`元素 的数组将`fooList`生成名称 。
- 添加`java.util.ArrayList`了一个或多个`x.y.User`元素的 A 将`userList`生成名称 。
- 添加`java.util.HashSet`了一个或多个`x.y.Foo`元素的 A 将`fooList`生成名称 。
- 一个*空* `java.util.ArrayList`不会在所有被添加（实际上，该 `addObject(..)`电话将基本上是一个无操作）。

### 22.13.3默认视图名称

当没有显式提供此类逻辑视图名称时，`RequestToViewNameTranslator`接口确定逻辑`View`名称。它只有一个实现，即 `DefaultRequestToViewNameTranslator`类。

该`DefaultRequestToViewNameTranslator`请求的URL映射到逻辑视图的名称，与该实施例中：

```java
public class RegistrationController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
        // process the request...
        ModelAndView mav = new ModelAndView();
        // add data as necessary to the model...
        return mav;
        // notice that no View or logical view name has been set
    }

}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- this bean with the well known name generates view names for us -->
    <bean id="viewNameTranslator"
            class="org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator"/>

    <bean class="x.y.RegistrationController">
        <!-- inject dependencies as necessary -->
    </bean>

    <!-- maps request URLs to Controller names -->
    <bean class="org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping"/>

    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>
```

请注意，在`handleRequest(..)`方法的实现中，如何在返回的内容`View`上设置no 或逻辑视图名称`ModelAndView`。其 `DefaultRequestToViewNameTranslator`任务是 从请求的URL 生成*逻辑视图名称*。在上面`RegistrationController`结合使用的情况下`ControllerClassNameHandlerMapping`，请求URL`http://localhost/registration.html`结果`registration` 是由生成的逻辑视图名称`DefaultRequestToViewNameTranslator`。然后，该逻辑视图名称`/WEB-INF/jsp/registration.jsp`由`InternalResourceViewResolver`bean 解析为视图 。

您不需要`DefaultRequestToViewNameTranslator`显式定义bean。如果您喜欢默认设置，则`DefaultRequestToViewNameTranslator`可以依赖Spring Web MVC `DispatcherServlet`来实例化此类的实例（如果未明确配置）。

当然，如果您需要更改默认设置，那么您需要`DefaultRequestToViewNameTranslator`明确配置自己的bean。有关`DefaultRequestToViewNameTranslator`可配置的各种属性的详细信息，请参阅综合 javadoc。