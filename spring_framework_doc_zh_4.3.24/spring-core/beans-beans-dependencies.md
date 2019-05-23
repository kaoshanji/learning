# 7.4 依赖

典型的企业应用程序不包含单个对象（或Spring用法中的bean）。即使是最简单的应用程序也有一些对象可以协同工作，以呈现最终用户所看到的连贯应用程序。下一节将介绍如何定义多个独立的bean定义，以及对象协作实现目标的完全实现的应用程序。

### 7.4.1依赖注入

*依赖注入*（DI）是一个过程，通过这个过程，对象定义它们的依赖关系，即它们使用的其他对象，只能通过构造函数参数，工厂方法的参数或在构造或返回对象实例后在对象实例上设置的属性。从工厂方法。然后容器在创建bean时*注入*这些依赖项。这个过程基本上是相反的，因此名称 *Inversion of Control*（IoC），bean本身通过使用类的直接构造或*服务定位器*模式来控制其依赖项的实例化或位置。

使用DI原理的代码更清晰，当对象提供其依赖项时，解耦更有效。该对象不查找其依赖项，也不知道依赖项的位置或类。因此，您的类变得更容易测试，特别是当依赖关系在接口或抽象基类上时，这允许在单元测试中使用存根或模拟实现。

DI存在两个主要变体，基于构造函数的依赖注入和基于Setter的依赖注入。

#### 基于构造函数的依赖注入

*基于构造函数的* DI由容器调用具有多个参数的构造函数来完成，每个参数表示一个依赖项。调用`static`具有特定参数的工厂方法来构造bean几乎是等效的，本讨论同样处理构造函数和`static`工厂方法的参数。以下示例显示了一个只能通过构造函数注入进行依赖注入的类。请注意，此类没有什么*特别之处*，它是一个POJO，它不依赖于容器特定的接口，基类或注释。

```java
public class SimpleMovieLister {

    // the SimpleMovieLister has a dependency on a MovieFinder
    private MovieFinder movieFinder;

    // a constructor so that the Spring container can inject a MovieFinder
    public SimpleMovieLister(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // business logic that actually uses the injected MovieFinder is omitted...
}
```

##### 构造函数参数解析

使用参数的类型进行构造函数参数解析匹配。如果bean定义的构造函数参数中不存在潜在的歧义，那么在bean定义中定义构造函数参数的顺序是在实例化bean时将这些参数提供给适当的构造函数的顺序。考虑以下类：

```java
package x.y;

public class Foo {

    public Foo(Bar bar, Baz baz) {
        // ...
    }
}
```

假设`Bar`并且`Baz`类与继承无关，则不存在潜在的歧义。因此，以下配置工作正常，您无需在`<constructor-arg/>` 元素中显式指定构造函数参数索引和/或类型。

```xml
<beans>
    <bean id="foo" class="x.y.Foo">
        <constructor-arg ref="bar"/>
        <constructor-arg ref="baz"/>
    </bean>

    <bean id="bar" class="x.y.Bar"/>

    <bean id="baz" class="x.y.Baz"/>
</beans>
```

当引用另一个bean时，类型是已知的，并且可以发生匹配（与前面的示例一样）。当使用简单类型时，例如 `<value>true</value>`，Spring无法确定值的类型，因此无法在没有帮助的情况下按类型进行匹配。考虑以下类：

```java
package examples;

public class ExampleBean {

    // Number of years to calculate the Ultimate Answer
    private int years;

    // The Answer to Life, the Universe, and Everything
    private String ultimateAnswer;

    public ExampleBean(int years, String ultimateAnswer) {
        this.years = years;
        this.ultimateAnswer = ultimateAnswer;
    }
}
```

在前面的场景中，如果使用属性显式指定构造函数参数的类型，则容器*可以*使用与简单类型匹配的类型`type`。例如：

```xml
<bean  id = “exampleBean”  class = “examples.ExampleBean” > 
    <constructor-arg  type = “int”  value = “7500000” /> 
    <constructor-arg  type = “java.lang.String”  value = “42” / > 
</ bean>
```

使用该`index`属性显式指定构造函数参数的索引。例如：

```xml
<bean  id = “exampleBean”  class = “examples.ExampleBean” > 
    <constructor-arg  index = “0”  value = “7500000” /> 
    <constructor-arg  index = “1”  value = “42” /> 
</ bean >
```

除了解决多个简单值的歧义之外，指定索引还可以解决构造函数具有相同类型的两个参数的歧义。请注意， *索引是基于0的*。

您还可以使用构造函数参数名称进行值消歧：

```xml
<bean  id = “exampleBean”  class = “examples.ExampleBean” > 
    <constructor-arg  name = “years”  value = “7500000” /> 
    <constructor-arg  name = “ultimateAnswer”  value = “42” /> 
</ bean >
```

请记住，为了使这项工作开箱即用，必须在启用调试标志的情况下编译代码，以便Spring可以从构造函数中查找参数名称。

#### 基于Setter的依赖注入

*基于setter的* DI是在调用无参数构造函数或无参数`static`工厂方法来实例化bean之后，通过容器调用bean上的setter方法来完成的。

以下示例显示了一个只能使用纯setter注入进行依赖注入的类。这个类是传统的Java。它是一个POJO，它不依赖于容器特定的接口，基类或注释。

```java
public class SimpleMovieLister {

    // the SimpleMovieLister has a dependency on the MovieFinder
    private MovieFinder movieFinder;

    // a setter method so that the Spring container can inject a MovieFinder
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // business logic that actually uses the injected MovieFinder is omitted...
}
```

它`ApplicationContext`支持它管理的bean的基于构造函数和基于setter的DI。在通过构造函数方法注入了一些依赖项之后，它还支持基于setter的DI。您可以以a的形式配置依赖项，并将`BeanDefinition`其与`PropertyEditor`实例结合使用，以将属性从一种格式转换为另一种格式。然而，大多数Spring用户不直接与这些类（即，编程），而是用XML `bean` 定义，注释组件（即与注释类`@Component`， `@Controller`等等），或`@Bean`在基于Java的方法`@Configuration`类。然后，这些源在内部转换为实例`BeanDefinition`并用于加载整个Spring IoC容器实例。

#### 依赖性解决过程

容器执行bean依赖性解析，如下所示：

- 使用`ApplicationContext`描述所有bean的配置元数据创建和初始化。可以通过XML，Java代码或注释指定配置元数据。
- 对于每个bean，如果使用的是依赖于普通构造函数的，那么它的依赖关系将以属性，构造函数参数或static-factory方法的参数的形式表示。*实际创建* bean *时，会将*这些依赖项提供给bean 。
- 每个属性或构造函数参数都是要设置的值的实际定义，或者是对容器中另一个bean的引用。
- 作为值的每个属性或构造函数参数都从其指定格式转换为该属性或构造函数参数的实际类型。默认情况下，Spring能够转换成字符串格式提供给所有的内置类型，比如数值`int`， `long`，`String`，`boolean`，等。

Spring容器在创建容器时验证每个bean的配置。但是，在*实际创建* bean之前，不会设置bean属性本身。创建容器时会创建单例作用域并设置为预先实例化（默认值）的Bean。否则，仅在请求时才创建bean。创建bean可能会导致创建bean的图形，因为bean的依赖关系及其依赖关系（依此类推）被创建和分配。请注意，这些依赖项之间的解决方案不匹配可能会显示较晚，即首次创建受影响的bean时。

**循环依赖**

如果您主要使用构造函数注入，则可以创建无法解析的循环依赖关系场景。

例如：类A通过构造函数注入需要类B的实例，而类B通过构造函数注入需要类A的实例。如果将A类和B类的bean配置为相互注入，则Spring IoC容器会在运行时检测到此循环引用，并抛出a `BeanCurrentlyInCreationException`。

一种可能的解决方案是编辑由setter而不是构造函数配置的某些类的源代码。或者，避免构造函数注入并仅使用setter注入。换句话说，尽管不推荐使用，但您可以使用setter注入配置循环依赖项。

与*典型*情况（没有循环依赖）不同，bean A和bean B之间的循环依赖强制其中一个bean在完全初始化之前被注入另一个bean（一个经典的鸡/蛋场景）。

你通常可以相信Spring做正确的事。它在容器加载时检测配置问题，例如对不存在的bean和循环依赖关系的引用。当实际创建bean时，Spring会尽可能晚地设置属性并解析依赖关系。这意味着，如果在创建该对象或其某个依赖项时出现问题，则在请求对象时，正确加载的Spring容器可以在以后生成异常。例如，bean因缺少属性或无效属性而抛出异常。这可能会延迟一些配置问题的可见性`ApplicationContext`默认情况下实现预实例化单例bean。以实际需要之前创建这些bean的一些前期时间和内存为代价，您`ApplicationContext`会在创建时发现配置问题，而不是更晚。您仍然可以覆盖此默认行为，以便单例bean将延迟初始化，而不是预先实例化。

如果不存在循环依赖关系，当一个或多个协作bean被注入依赖bean时，每个协作bean 在被注入依赖bean之前*完全*配置。这意味着如果bean A依赖于bean B，Spring IoC容器在调用bean A上的setter方法之前完全配置bean B.换句话说，bean被实例化（如果不是预先实例化的单例），设置依赖项，并调用相关的生命周期方法（如配置的init方法 或InitializingBean回调方法）。

#### 依赖注入的例子

以下示例将基于XML的配置元数据用于基于setter的DI。Spring XML配置文件的一小部分指定了一些bean定义：

```xml
<bean id="exampleBean" class="examples.ExampleBean">
    <!-- setter injection using the nested ref element -->
    <property name="beanOne">
        <ref bean="anotherExampleBean"/>
    </property>

    <!-- setter injection using the neater ref attribute -->
    <property name="beanTwo" ref="yetAnotherBean"/>
    <property name="integerProperty" value="1"/>
</bean>

<bean id="anotherExampleBean" class="examples.AnotherBean"/>
<bean id="yetAnotherBean" class="examples.YetAnotherBean"/>
```

```java
public class ExampleBean {

    private AnotherBean beanOne;

    private YetAnotherBean beanTwo;

    private int i;

    public void setBeanOne(AnotherBean beanOne) {
        this.beanOne = beanOne;
    }

    public void setBeanTwo(YetAnotherBean beanTwo) {
        this.beanTwo = beanTwo;
    }

    public void setIntegerProperty(int i) {
        this.i = i;
    }
}
```

在前面的示例中，声明setter与XML文件中指定的属性匹配。以下示例使用基于构造函数的DI：

```xml
<bean id="exampleBean" class="examples.ExampleBean">
    <!-- constructor injection using the nested ref element -->
    <constructor-arg>
        <ref bean="anotherExampleBean"/>
    </constructor-arg>

    <!-- constructor injection using the neater ref attribute -->
    <constructor-arg ref="yetAnotherBean"/>

    <constructor-arg type="int" value="1"/>
</bean>

<bean id="anotherExampleBean" class="examples.AnotherBean"/>
<bean id="yetAnotherBean" class="examples.YetAnotherBean"/>
```

```java
public class ExampleBean {

    private AnotherBean beanOne;

    private YetAnotherBean beanTwo;

    private int i;

    public ExampleBean(
        AnotherBean anotherBean, YetAnotherBean yetAnotherBean, int i) {
        this.beanOne = anotherBean;
        this.beanTwo = yetAnotherBean;
        this.i = i;
    }
}
```

bean定义中指定的构造函数参数将用作构造函数的参数`ExampleBean`。

现在考虑这个示例的变体，其中不使用构造函数，而是告诉Spring调用`static`工厂方法来返回对象的实例：

```xml
<bean id="exampleBean" class="examples.ExampleBean" factory-method="createInstance">
    <constructor-arg ref="anotherExampleBean"/>
    <constructor-arg ref="yetAnotherBean"/>
    <constructor-arg value="1"/>
</bean>

<bean id="anotherExampleBean" class="examples.AnotherBean"/>
<bean id="yetAnotherBean" class="examples.YetAnotherBean"/>
```

```java
public class ExampleBean {

    // a private constructor
    private ExampleBean(...) {
        ...
    }

    // a static factory method; the arguments to this method can be
    // considered the dependencies of the bean that is returned,
    // regardless of how those arguments are actually used.
    public static ExampleBean createInstance (
        AnotherBean anotherBean, YetAnotherBean yetAnotherBean, int i) {

        ExampleBean eb = new ExampleBean (...);
        // some other operations...
        return eb;
    }
}
```

`static`工厂方法的参数是通过`<constructor-arg/>`元素提供的，与实际使用的构造函数完全相同。工厂方法返回的类的类型不必与包含`static`工厂方法的类的类型相同，尽管在此示例中它是。实例（非静态）工厂方法将以基本相同的方式使用（除了使用`factory-bean`属性而不是`class`属性），因此这里不再讨论细节。



### 7.4.2详细信息的依赖关系和配置

如上一节所述，您可以将bean属性和构造函数参数定义为对其他托管bean（协作者）的引用，或者作为内联定义的值。Spring的基于XML的配置元数据为此目的支持其元素`<property/>`和`<constructor-arg/>`元素中的子元素类型 。

#### 直值（基元，字符串等）

在`value`所述的属性`<property/>`元素指定属性或构造器参数的人类可读的字符串表示。Spring的 转换服务 用于将这些值从a转换`String`为属性或参数的实际类型。

```xml
<bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <!-- results in a setDriverClassName(String) call -->
    <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/mydb"/>
    <property name="username" value="root"/>
    <property name="password" value="masterkaoli"/>
</bean>
```

以下示例使用 p命名空间 进行更简洁的XML配置。

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource"
        destroy-method="close"
        p:driverClassName="com.mysql.jdbc.Driver"
        p:url="jdbc:mysql://localhost:3306/mydb"
        p:username="root"
        p:password="masterkaoli"/>

</beans>
```

您还可以将`java.util.Properties`实例配置为：

```xml
<bean id="mappings"
    class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">

    <!-- typed as a java.util.Properties -->
    <property name="properties">
        <value>
            jdbc.driver.className=com.mysql.jdbc.Driver
            jdbc.url=jdbc:mysql://localhost:3306/mydb
        </value>
    </property>
</bean>
```

Spring容器通过使用JavaBeans 机制将`<value/>`元素内的文本转换为 `java.util.Properties`实例`PropertyEditor`。这是一个很好的快捷方式，也是Spring团队支持`<value/>`在`value`属性样式上使用嵌套元素的少数几个地方之一。

##### idref元素

该`idref`元素只是一种防错方法，可以将容器中另一个bean 的*id*（字符串值 - 而不是引用）传递给`<constructor-arg/>`or或`<property/>` 节点。

```xml
<bean id="theTargetBean" class="..."/>

<bean id="theClientBean" class="...">
    <property name="targetName">
        <idref bean="theTargetBean"/>
    </property>
</bean>
```

上面的bean定义片段与以下片段*完全*等效（在运行时）：

```xml
<bean id="theTargetBean" class="..." />

<bean id="client" class="...">
    <property name="targetName" value="theTargetBean"/>
</bean>
```

第一种形式优于第二种形式，因为使用`idref`标签允许容器*在部署时*验证引用的命名bean实际存在。在第二个变体中，不对传递给bean 的`targetName`属性的值执行验证`client`。只有在`client`实际实例化bean 时才会发现错别字（很可能是致命的结果）。如果`client` bean是 原型 bean，则只能在部署容器后很长时间才能发现此错误和产生的异常。

其中一个共同的地方（至少在早期比Spring 2.0版本）`<idref/>`元素带来的值在配置 AOP拦截 在 `ProxyFactoryBean`bean定义。`<idref/>`指定拦截器名称时使用元素可以防止拼写错误的拦截器ID。

#### 引用其他bean（协作者）

所述`ref`元件是内部的最终元件`<constructor-arg/>`或`<property/>` 定义元素。在这里，您将bean的指定属性的值设置为对容器管理的另一个bean（协作者）的引用。引用的bean是bean的依赖项，其属性将被设置，并且在设置属性之前根据需要按需初始化。（如果协作者是单例bean，它可能已由容器初始化。）所有引用最终都是对另一个对象的引用。划定范围和有效性取决于是否通过指定其他对象的ID /名称`bean`，`local,`或`parent`属性。

通过标记的`bean`属性指定目标bean `<ref/>`是最常用的形式，并允许创建对同一容器或父容器中的任何bean的引用，而不管它是否在同一XML文件中。`bean`属性的值可以`id`与目标bean 的属性相同，或者作为目标bean的`name`属性中的值之一。

```xml
<ref bean="someBean"/>
```

通过该`parent`属性指定目标bean 会创建对当前容器的父容器中的bean的引用。`parent` 属性的值可以`id`与目标bean 的属性相同，也可以与目标bean 的属性中的一个值相同`name`，并且目标bean必须位于当前bean的父容器中。您主要在拥有容器层次结构并且希望将现有bean包装在父容器中并使用与父bean具有相同名称的代理时使用此bean引用变体。

```xml
<!-- in the parent context -->
<bean id="accountService" class="com.foo.SimpleAccountService">
    <!-- insert dependencies as required as here -->
</bean>
```

```xml
<!-- in the child (descendant) context -->
<bean id="accountService" <!-- bean name is the same as the parent bean -->
    class="org.springframework.aop.framework.ProxyFactoryBean">
    <property name="target">
        <ref parent="accountService"/> <!-- notice how we refer to the parent bean -->
    </property>
    <!-- insert other configuration and dependencies as required here -->
</bean>
```

#### 内部Bean

`<bean/>`内部的元件`<property/>`或`<constructor-arg/>`元件定义了一个所谓的*内部bean*。

```xml
<bean id="outer" class="...">
    <!-- instead of using a reference to a target bean, simply define the target bean inline -->
    <property name="target">
        <bean class="com.example.Person"> <!-- this is the inner bean -->
            <property name="name" value="Fiona Apple"/>
            <property name="age" value="25"/>
        </bean>
    </property>
</bean>
```

内部bean定义不需要定义的id或名称; 如果指定，则容器不使用此类值作为标识符。容器还会`scope`在创建时忽略标志：内部bean *始终是*匿名的，并且*始终*使用外部bean创建。这是*不是*可以内部bean注入到协作bean以外进入封闭豆或独立访问它们。

作为一个极端情况，可以从自定义范围接收销毁回调，例如，对于包含在单例bean中的请求范围的内部bean：内部bean实例的创建将绑定到其包含的bean，但是销毁回调允许它参与请求范围的生命周期。这不是常见的情况; 内部bean通常只是共享其包含bean的范围。

#### 集合

在`<list/>`，`<set/>`，`<map/>`，和`<props/>`元素，你将Java的性能和参数`Collection`类型`List`，`Set`，`Map`，和`Properties`分别。

```xml
<bean id="moreComplexObject" class="example.ComplexObject">
    <!-- results in a setAdminEmails(java.util.Properties) call -->
    <property name="adminEmails">
        <props>
            <prop key="administrator">administrator@example.org</prop>
            <prop key="support">support@example.org</prop>
            <prop key="development">development@example.org</prop>
        </props>
    </property>
    <!-- results in a setSomeList(java.util.List) call -->
    <property name="someList">
        <list>
            <value>a list element followed by a reference</value>
            <ref bean="myDataSource" />
        </list>
    </property>
    <!-- results in a setSomeMap(java.util.Map) call -->
    <property name="someMap">
        <map>
            <entry key="an entry" value="just some string"/>
            <entry key ="a ref" value-ref="myDataSource"/>
        </map>
    </property>
    <!-- results in a setSomeSet(java.util.Set) call -->
    <property name="someSet">
        <set>
            <value>just some string</value>
            <ref bean="myDataSource" />
        </set>
    </property>
</bean>
```

*映射键或值的值或设置值也可以是以下任何元素：*

```
bean | ref | idref | list | set | map | props | value | null
```

##### 集合

Spring容器还支持集合的合并。应用程序开发人员可以定义一个父风格<list/>，<map/>，<set/>或<props/>元素，并有孩子式的<list/>，<map/>，<set/>或<props/>元素继承和父集合覆盖值。也就是说，子集合的值是合并父集合和子集合的元素的结果，子集合的元素覆盖父集合中指定的值。

以下示例演示了集合：

```xml
<beans>
    <bean id="parent" abstract="true" class="example.ComplexObject">
        <property name="adminEmails">
            <props>
                <prop key="administrator">administrator@example.com</prop>
                <prop key="support">support@example.com</prop>
            </props>
        </property>
    </bean>
    <bean id="child" parent="parent">
        <property name="adminEmails">
            <!-- the merge is specified on the child collection definition -->
            <props merge="true">
                <prop key="sales">sales@example.com</prop>
                <prop key="support">support@example.co.uk</prop>
            </props>
        </property>
    </bean>
<beans>
```

注意使用的`merge=true`上属性`<props/>`的元素 `adminEmails`的财产`child`bean定义。当`child`容器解析并实例化bean时，生成的实例有一个`adminEmails``Properties`集合，其中包含将子集合`adminEmails`与父`adminEmails`集合合并的结果 。

```
administrator=administrator@example.com 
sales=sales@example.com 
support=support@example.co.uk
```

孩子`Properties`集合的值设置继承父所有属性元素`<props/>`，和孩子的为值`support`值将覆盖父集合的价值。

这一集合行为同样适用于`<list/>`，`<map/>`和`<set/>` 集合类型。在`<list/>`元素的特定情况下，保持与`List`集合类型相关联的语义，即`ordered` 值集合的概念; 父级的值位于所有子级列表的值之前。在的情况下`Map`，`Set`和`Properties`集合类型，没有顺序存在。因此，没有排序的语义在背后的关联的集合类型的效果`Map`，`Set`以及`Properties`该容器内部使用实现类型。

#### 带有p命名空间的XML快捷方式

p-namespace使您可以使用`bean`元素的属性而不是嵌套 `<property/>`元素来描述属性值和/或协作bean。

Spring支持[具有命名空间的](xsd-configuration.html)可扩展配置格式，这些[命名空间](xsd-configuration.html)基于XML Schema定义。`beans`本章中讨论的配置格式在XML Schema文档中定义。但是，p-namespace未在XSD文件中定义，仅存在于Spring的核心中。

以下示例显示了两个解析为相同结果的XML片段：第一个使用标准XML格式，第二个使用p命名空间。

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean name="classic" class="com.example.ExampleBean">
        <property name="email" value="foo@bar.com"/>
    </bean>

    <bean name="p-namespace" class="com.example.ExampleBean"
        p:email="foo@bar.com"/>
</beans>
```

该示例显示了bean定义中名为email的p命名空间中的属性。这告诉Spring包含一个属性声明。如前所述，p命名空间没有架构定义，因此您可以将属性的名称设置为属性名称。

下一个示例包括另外两个bean定义，它们都引用了另一个bean：

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean name="john-classic" class="com.example.Person">
        <property name="name" value="John Doe"/>
        <property name="spouse" ref="jane"/>
    </bean>

    <bean name="john-modern"
        class="com.example.Person"
        p:name="John Doe"
        p:spouse-ref="jane"/>

    <bean name="jane" class="com.example.Person">
        <property name="name" value="Jane Doe"/>
    </bean>
</beans>
```

如您所见，此示例不仅包含使用p命名空间的属性值，还使用特殊格式来声明属性引用。第一个bean定义用于`<property name="spouse" ref="jane"/>`创建从bean `john`到bean 的引用 `jane`，而第二个bean定义`p:spouse-ref="jane"`用作属性来执行完全相同的操作。在这种情况下`spouse`是属性名称，而该`-ref`部分表示这不是直接值，而是对另一个bean的引用。

#### 带有c-namespace的XML快捷方式

类似于“带有p-namespace的XML快捷方式”一节，Spring 3.1中新引入的*c-namespace*允许使用内联属性来配置构造函数参数，而不是嵌套`constructor-arg`元素。

让我们回顾一下名为“基于构造函数的依赖注入”一节中的示例，其中包含`c:`命名空间：

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:c="http://www.springframework.org/schema/c"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="bar" class="x.y.Bar"/>
    <bean id="baz" class="x.y.Baz"/>

    <!-- traditional declaration -->
    <bean id="foo" class="x.y.Foo">
        <constructor-arg ref="bar"/>
        <constructor-arg ref="baz"/>
        <constructor-arg value="foo@bar.com"/>
    </bean>

    <!-- c-namespace declaration -->
    <bean id="foo" class="x.y.Foo" c:bar-ref="bar" c:baz-ref="baz" c:email="foo@bar.com"/>

</beans>
```

该`c:`命名空间使用相同的约定作为`p:`一个（后`-ref`为bean引用），供他们的名字设置构造函数的参数。同样，它需要声明，即使它没有在XSD架构中定义（但它存在于Spring核心内）。

对于构造函数参数名称不可用的罕见情况（通常如果字节码是在没有调试信息的情况下编译的话），可以使用回退到参数索引：

```xml
<!-- c-namespace index declaration -->
<bean id="foo" class="x.y.Foo" c:_0-ref="bar" c:_1-ref="baz"/>
```

#### 复合属性名称

设置bean属性时，可以使用复合或嵌套属性名称，只要除最终属性名称之外的路径的所有组件都不是`null`。请考虑以下bean定义。

```xml
<bean id="foo" class="foo.Bar">
    <property name="fred.bob.sammy" value="123" />
</bean>
```

所述`foo`豆具有`fred`属性，该属性具有`bob`属性，其具有`sammy` 特性，并且最终`sammy`属性被设置为值`123`。为了使这一工作，`fred`财产`foo`和`bob`财产`fred`绝不能 `null`豆后构造，或`NullPointerException`抛出。

### 7.4.3使用依赖

如果bean是另一个bean的依赖项，通常意味着将一个bean设置为另一个bean的属性。通常，您可以使用基于XML的配置元数据中的 元素 来完成此操作。但是，有时bean之间的依赖关系不那么直接; 例如，需要触发类中的静态初始化程序，例如数据库驱动程序注册。`depends-on`在初始化使用此元素的bean之前，该属性可以显式强制初始化一个或多个bean。以下示例使用该`depends-on`属性表示对单个bean的依赖关系：

```xml
<bean id="beanOne" class="ExampleBean" depends-on="manager"/>
<bean id="manager" class="ManagerBean" />
```

要表示对多个bean的依赖关系，请提供bean名称列表作为`depends-on`属性的值，使用逗号，空格和分号作为有效分隔符：

```xml
<bean id="beanOne" class="ExampleBean" depends-on="manager,accountDao">
    <property name="manager" ref="manager" />
</bean>

<bean id="manager" class="ManagerBean" />
<bean id="accountDao" class="x.y.jdbc.JdbcAccountDao" />
```

`depends-on`bean定义中的属性既可以指定初始化时间依赖关系，也可以指定仅限[单例](beans.html#beans-factory-scopes-singleton) bean的相应销毁时间依赖关系。`depends-on` 在给定的bean本身被销毁之前，首先销毁定义与给定bean 的关系的从属bean 。因此`depends-on`也可以控制关机顺序。

### 7.4.4延迟初始化的bean

默认情况下，`ApplicationContext`实现会急切地创建和配置所有 [单例](beans.html#beans-factory-scopes-singleton) bean，作为初始化过程的一部分。通常，这种预先实例化是可取的，因为配置或周围环境中的错误是立即发现的，而不是几小时甚至几天后。如果*不*希望出现这种情况，可以通过将bean定义标记为延迟初始化来阻止单例bean的预实例化。延迟初始化的bean告诉IoC容器在第一次请求时创建bean实例，而不是在启动时。

在XML中，此行为由 元素`lazy-init`上的属性控制`<bean/>`; 例如：

```xml
<bean id="lazy" class="com.foo.ExpensiveToCreateBean" lazy-init="true"/>
<bean name="not.lazy" class="com.foo.AnotherBean"/>
```

当前面的配置被a使用时`ApplicationContext`，命名的bean `lazy`在`ApplicationContext`启动时不会急切地预先实例化，而`not.lazy`bean被急切地预先实例化。

但是，当延迟初始化的bean是*未进行*延迟初始化的单例bean的依赖项时 ，`ApplicationContext`会在启动时创建延迟初始化的bean，因为它必须满足单例的依赖关系。惰性初始化的bean被注入到其他地方的单独的bean中，而这个bean并不是惰性初始化的。

您还可以通过使用元素`default-lazy-init`上的属性来控制容器级别的延迟初始化 `<beans/>`; 例如：

```xml
<beans default-lazy-init="true">
    <!-- no beans will be pre-instantiated... -->
</beans>
```

### 7.4.5 自动装配



### 7.4.6方法注入

在大多数应用程序场景中，容器中的大多数bean都是 `单例`。当单例bean需要与另一个单例bean协作，或者非单例bean需要与另一个非单例bean协作时，通常通过将一个bean定义为另一个bean的属性来处理依赖关系。当bean生命周期不同时会出现问题。假设单例bean A需要使用非单例（原型）bean B，可能是在A上的每个方法调用上。容器只创建一次单例bean A，因此只有一次机会来设置属性。每次需要时，容器都不能为bean A提供bean B的新实例。

解决方案是放弃一些控制反转。您可以通过实现接口 使bean A了解容器 `ApplicationContextAware`，并通过 对容器 进行getBean（“B”）调用，每次bean A需要时都要求（通常是新的）bean B实例。以下是此方法的示例：

```Java
// a class that uses a stateful Command-style class to perform some processing
package fiona.apple;

// Spring-API imports
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CommandManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Object process(Map commandState) {
        // grab a new instance of the appropriate Command
        Command command = createCommand();
        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
        return command.execute();
    }

    protected Command createCommand() {
        // notice the Spring API dependency!
        return this.applicationContext.getBean("command", Command.class);
    }

    public void setApplicationContext(
            ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```

前面的内容是不可取的，因为业务代码知道并耦合到Spring Framework。Method Injection是Spring IoC容器的一个先进功能，它允许以干净的方式处理这个用例。

您可以在[此博客条目中](https://spring.io/blog/2004/08/06/method-injection/)阅读有关Method Injection的动机的更多信息 。

#### 查找方法注入

Lookup方法注入是容器覆盖*容器托管bean*上的方法的能力 ，以返回*容器*中另一个命名bean的查找结果。查找通常涉及原型bean，如上一节中描述的场景。Spring Framework通过使用CGLIB库中的字节码生成来实现此方法注入，以动态生成覆盖该方法的子类。

- 要使这个动态子类工作，Spring bean容器将子类化的类不能`final`，并且要重写的方法也不能`final`。
- 对具有`abstract`方法的类进行单元测试需要您自己对类进行子类化并提供该`abstract`方法的存根实现。
- 组件扫描也需要具体的方法，这需要具体的类别来获取。
- 另一个关键限制是查找方法不适用于工厂方法，特别是`@Bean`配置类中的方法，因为容器在这种情况下不负责创建实例，因此无法在上面创建运行时生成的子类。飞。

查看`CommandManager`前面代码片段中的类，您会看到Spring容器将动态覆盖该`createCommand()` 方法的实现。您的`CommandManager`类将不具有任何Spring依赖项，如在重新编写的示例中可以看到的：

```java
package fiona.apple;

// no more Spring imports!

public abstract class CommandManager {

    public Object process(Object commandState) {
        // grab a new instance of the appropriate Command interface
        Command command = createCommand();
        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
        return command.execute();
    }

    // okay... but where is the implementation of this method?
    protected abstract Command createCommand();
}
```

在包含要注入的方法的客户端类中（`CommandManager`在本例中），要注入的方法需要以下形式的签名：

```java
<public|protected> [abstract] <return-type> theMethodName(no-arguments);
```

如果方法是`abstract`，则动态生成的子类实现该方法。否则，动态生成的子类将覆盖原始类中定义的具体方法。例如：

```java
<!-- a stateful bean deployed as a prototype (non-singleton) -->
<bean id="myCommand" class="fiona.apple.AsyncCommand" scope="prototype">
    <!-- inject dependencies here as required -->
</bean>

<!-- commandProcessor uses statefulCommandHelper -->
<bean id="commandManager" class="fiona.apple.CommandManager">
    <lookup-method name="createCommand" bean="myCommand"/>
</bean>
```

标识为*commandManager*的bean `createCommand()` 在需要*myCommand* bean 的新实例时调用自己的方法。您必须小心将`myCommand`bean 部署为原型，如果这实际上是需要的话。如果它是一个单例，`myCommand` 则每次都返回相同的bean 实例。

或者，在基于注释的组件模型中，您可以通过`@Lookup`注释声明查找方法：

```java
public abstract class CommandManager {

    public Object process(Object commandState) {
        Command command = createCommand();
        command.setState(commandState);
        return command.execute();
    }

    @Lookup("myCommand")
    protected abstract Command createCommand();
}
```

或者，更具惯用性，您可以依赖于针对查找方法的声明返回类型解析目标bean：

```Java
public abstract class CommandManager {

    public Object process(Object commandState) {
        MyCommand command = createCommand();
        command.setState(commandState);
        return command.execute();
    }

    @Lookup
    protected abstract MyCommand createCommand();
}
```

请注意，您通常会使用具体的存根实现声明这种带注释的查找方法，以使它们与Spring的组件扫描规则兼容，默认情况下抽象类被忽略。此限制不适用于显式注册或显式导入的bean类。

访问不同范围的目标bean的另一种方法是`ObjectFactory`/ `Provider`注入点。

感兴趣的读者也可以找到`ServiceLocatorFactoryBean`（在 `org.springframework.beans.factory.config`包中）有用。

#### 任意方法更换

与查找方法注入相比，一种不太有用的方法注入形式是能够使用另一个方法实现替换托管bean中的任意方法。用户可以安全地跳过本节的其余部分，直到实际需要该功能。

使用基于XML的配置元数据，您可以使用该`replaced-method`元素将已存在的方法实现替换为已部署的bean。考虑以下类，使用方法computeValue，我们要覆盖它：

```Java
public class MyValueCalculator {

    public String computeValue(String input) {
        // some real code...
    }

    // some other methods...
}
```

实现`org.springframework.beans.factory.support.MethodReplacer` 接口的类提供新的方法定义。

```java
/**
 * meant to be used to override the existing computeValue(String)
 * implementation in MyValueCalculator
 */
public class ReplacementComputeValue implements MethodReplacer {

    public Object reimplement(Object o, Method m, Object[] args) throws Throwable {
        // get the input value, work with it, and return a computed result
        String input = (String) args[0];
        ...
        return ...;
    }
}
```

部署原始类并指定方法覆盖的bean定义如下所示：

```xml
<bean id="myValueCalculator" class="x.y.z.MyValueCalculator">
    <!-- arbitrary method replacement -->
    <replaced-method name="computeValue" replacer="replacementComputeValue">
        <arg-type>String</arg-type>
    </replaced-method>
</bean>

<bean id="replacementComputeValue" class="a.b.c.ReplacementComputeValue"/>
```

您可以`<arg-type/>`在`<replaced-method/>` 元素中使用一个或多个包含的元素来指示被覆盖的方法的方法签名。仅当方法重载且类中存在多个变体时，才需要参数的签名。为方便起见，参数的类型字符串可以是完全限定类型名称的子字符串。例如，以下所有匹配 `java.lang.String`：

```
java.lang.String
String
Str
```

因为参数的数量通常足以区分每个可能的选择，所以通过允许您只键入与参数类型匹配的最短字符串，此快捷方式可以节省大量的输入。

