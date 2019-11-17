# 12.5 ProxyFactoryBean创建AOP代理

如果您正在为业务对象使用Spring IoC容器（ApplicationContext或BeanFactory） - 您应该这样做！ - 您将需要使用Spring的AOP FactoryBeans之一。（请记住，工厂bean引入了一个间接层，使其能够创建不同类型的对象。）

Spring AOP支持还使用了工厂bean。

在Spring中创建AOP代理的基本方法是使用 *org.springframework.aop.framework.ProxyFactoryBean*。这样可以完全控制将要应用的切入点和建议及其排序。但是，如果您不需要此类控件，则可以使用更简单的选项。

### 12.5.1基础知识

的`ProxyFactoryBean`，像其它的`FactoryBean`实现中，引入了一个间接的水平。如果`ProxyFactoryBean`使用名称定义`foo`，引用的对象`foo`不是`ProxyFactoryBean`实例本身，而是由方法`ProxyFactoryBean`实现创建的对象`getObject()`。此方法将创建包装目标对象的AOP代理。

使用一个`ProxyFactoryBean`或另一个IoC感知类来创建AOP代理的最重要的好处之一是，它意味着IoC也可以管理建议和切入点。这是一个强大的功能，可以实现其他AOP框架难以实现的某些方法。例如，一个建议本身可以引用应用程序对象（除了目标，它应该在任何AOP框架中可用），受益于依赖注入提供的所有可插入性。

### 12.5.2 JavaBean属性

`FactoryBean`与Spring提供的大多数实现一样， `ProxyFactoryBean`该类本身就是一个JavaBean。其属性用于：

- 指定要代理的目标。
- 指定是否使用CGLIB（参见下文以及[第12.5.3节“基于JDK和CGLIB的代理”](aop-api.html#aop-pfb-proxy-types)）。

一些关键属性继承自`org.springframework.aop.framework.ProxyConfig` （Spring中所有AOP代理工厂的超类）。这些关键属性包括：

- `proxyTargetClass`：`true`如果要代理目标类，而不是目标类的接口。如果此属性值设置为`true`，则将创建CGLIB代理（但另请参见[第12.5.3节“基于JDK和CGLIB的代理”](aop-api.html#aop-pfb-proxy-types)）。
- `optimize`：控制是否将积极优化应用于*通过CGLIB创建的*代理 。除非完全理解相关AOP代理如何处理优化，否则不应轻易使用此设置。目前仅用于CGLIB代理; 它对JDK动态代理没有影响。
- `frozen`：如果是代理配置`frozen`，则不再允许更改配置。这既可以作为轻微优化，也可以用于`Advised` 在创建代理后不希望调用者能够操作代理（通过接口）的情况。此属性的默认值为 `false`，因此允许添加其他建议等更改。
- `exposeProxy`：确定是否应将当前代理公开在一个 `ThreadLocal`目标中，以便目标可以访问它。如果目标需要获取代理并且`exposeProxy`属性设置为`true`，则目标可以使用该 `AopContext.currentProxy()`方法。

其他特定属性`ProxyFactoryBean`包括：

- `proxyInterfaces`：String接口名称数组。如果未提供，则将使用目标类的CGLIB代理（但另请参见[第12.5.3节“基于JDK和CGLIB的代理”](aop-api.html#aop-pfb-proxy-types)）。
- `interceptorNames`：要应用的字符串数组`Advisor`，拦截器或其他建议名称。以先到先得的方式订购非常重要。也就是说，列表中的第一个拦截器将是第一个能够拦截调用的拦截器。

名称是当前工厂中的bean名称，包括来自祖先工厂的bean名称。你不能在这里提到bean引用，因为这样做会导致 `ProxyFactoryBean`忽略通知的单例设置。

您可以使用星号（`*`）附加拦截器名称。这将导致应用所有顾问bean，其名称以要应用星号之前的部分开头。有关使用此功能的示例，请参见[第12.5.6节“使用'全局'顾问程序”](aop-api.html#aop-global-advisors)。

- singleton：无论`getObject()`方法调用的频率如何，工厂是否应该返回单个对象。一些`FactoryBean`实现提供了这样的方法。默认值为`true`。如果您想使用有状态建议 - 例如，对于有状态的mixins - 使用原型建议以及单例值 `false`。

### 12.5.3基于JDK和CGLIB的代理

本节作为关于如何`ProxyFactoryBean` 选择为特定目标对象（即要代理）创建基于JDK和CGLIB的代理之一的权威文档。

`ProxyFactoryBean`关于创建基于JDK或CGLIB的代理的行为在Spring的1.2.x和2.0版本之间发生了变化。在`ProxyFactoryBean`现在表现关于与上述的自动检测接口相似的语义 `TransactionProxyFactoryBean`类。

如果要代理的目标对象的类（以下简称为目标类）未实现任何接口，则将创建基于CGLIB的代理。这是最简单的方案，因为JDK代理是基于接口的，没有接口意味着甚至不可能进行JDK代理。只需插入目标bean，并通过`interceptorNames`属性指定拦截器列表。请注意，即使已将`proxyTargetClass`属性 `ProxyFactoryBean`设置为，也将创建基于CGLIB的代理`false`。（显然这没有任何意义，最好从bean定义中删除，因为它最多是冗余的，最糟糕的是混淆。）

如果目标类实现一个（或多个）接口，则创建的代理类型取决于该配置`ProxyFactoryBean`。

如果已将`proxyTargetClass`属性`ProxyFactoryBean`设置为`true`，则将创建基于CGLIB的代理。这是有道理的，并且符合最少惊喜的原则。即使已将`proxyInterfaces`属性 `ProxyFactoryBean`设置为一个或多个完全限定的接口名称，该`proxyTargetClass`属性设置为`true` *将*导致基于CGLIB的代理生效。

如果已将`proxyInterfaces`属性`ProxyFactoryBean`设置为一个或多个完全限定的接口名称，则将创建基于JDK的代理。创建的代理将实现`proxyInterfaces` 属性中指定的所有接口; 如果目标类碰巧实现了比`proxyInterfaces`属性中指定的接口多得多的接口，那么这一切都很好，但返回的代理不会实现这些额外的接口。

如果`proxyInterfaces`财产`ProxyFactoryBean`已*不*被设置，但是目标类*的确实现了一个（或多个）*接口，那么 `ProxyFactoryBean`会自动检测到这个目标类已经实现了至少一个接口，一个基于JDK的代理将被创造。实际代理的接口将是目标类实现的*所有*接口; 实际上，这与仅提供目标类为`proxyInterfaces`属性实现的每个接口的列表相同。但是，它的工作量明显减少，并且不太容易出现错别字。

### 12.5.4代理接口

让我们看一个简单的实例`ProxyFactoryBean`。这个例子涉及：

- 一个*目标bean*将被代理。这是下面示例中的“personTarget”bean定义。
- 用于提供建议的顾问和拦截器。
- AOP代理bean定义，指定目标对象（personTarget bean）和要代理的接口，以及要应用的建议。

```xml
<bean id="personTarget" class="com.mycompany.PersonImpl">
    <property name="name" value="Tony"/>
    <property name="age" value="51"/>
</bean>

<bean id="myAdvisor" class="com.mycompany.MyAdvisor">
    <property name="someProperty" value="Custom string property value"/>
</bean>

<bean id="debugInterceptor" class="org.springframework.aop.interceptor.DebugInterceptor">
</bean>

<bean id="person"
    class="org.springframework.aop.framework.ProxyFactoryBean">
    <property name="proxyInterfaces" value="com.mycompany.Person"/>

    <property name="target" ref="personTarget"/>
    <property name="interceptorNames">
        <list>
            <value>myAdvisor</value>
            <value>debugInterceptor</value>
        </list>
    </property>
</bean>
```

请注意，该`interceptorNames`属性采用String列表：当前工厂中拦截器或顾问程序的bean名称。可以使用顾问，拦截器，返回之前，投掷建议对象。顾问的排序很重要。

您可能想知道为什么列表不包含bean引用。这样做的原因是，如果ProxyFactoryBean的singleton属性设置为false，则它必须能够返回独立的代理实例。如果任何顾问本身就是原型，则需要返回一个独立的实例，因此必须能够从工厂获得原型的实例; 持有参考是不够的。

上面的“person”bean定义可以用来代替Person实现，如下所示：

```java
Person person = (Person) factory.getBean("person");
```

与普通Java对象一样，同一IoC上下文中的其他bean可以表达对它的强类型依赖关系：

```xml
<bean id="personUser" class="com.mycompany.PersonUser">
    <property name="person"><ref bean="person"/></property>
</bean>
```

`PersonUser`此示例中的类将公开Person类型的属性。就其而言，可以透明地使用AOP代理来代替“真实”的人实现。但是，它的类将是一个动态代理类。可以将其转换为`Advised`界面（下面讨论）。

可以使用匿名*内部bean*隐藏目标和代理之间的区别 ，如下所示。只有`ProxyFactoryBean`定义不同; 仅包含完整性的建议：

```xml
<bean id="myAdvisor" class="com.mycompany.MyAdvisor">
    <property name="someProperty" value="Custom string property value"/>
</bean>

<bean id="debugInterceptor" class="org.springframework.aop.interceptor.DebugInterceptor"/>

<bean id="person" class="org.springframework.aop.framework.ProxyFactoryBean">
    <property name="proxyInterfaces" value="com.mycompany.Person"/>
    <!-- Use inner bean, not local reference to target -->
    <property name="target">
        <bean class="com.mycompany.PersonImpl">
            <property name="name" value="Tony"/>
            <property name="age" value="51"/>
        </bean>
    </property>
    <property name="interceptorNames">
        <list>
            <value>myAdvisor</value>
            <value>debugInterceptor</value>
        </list>
    </property>
</bean>
```

### 12.5.5代理类

如果您需要代理一个类而不是一个或多个接口，该怎么办？

想象一下，在上面的例子中，没有`Person`接口：我们需要建议一个`Person`没有实现任何业务接口的类。在这种情况下，您可以将Spring配置为使用CGLIB代理，而不是动态代理。只需将`proxyTargetClass`上面的ProxyFactoryBean属性设置 为true即可。虽然最好是编程接口而不是类，但在使用遗留代码时，建议不实现接口的类的能力会很有用。（一般来说，Spring不是规定性的。虽然它可以很容易地应用好的实践，但它避免强制使用特定的方法。）

如果您愿意，即使您有接口，也可以在任何情况下强制使用CGLIB。

CGLIB代理通过在运行时生成目标类的子类来工作。Spring将这个生成的子类配置为委托对原始目标的方法调用：子类用于实现*Decorator*模式，在通知中编织。

CGLIB代理通常应对用户透明。但是，有一些问题需要考虑：

- `Final` 方法无法建议，因为它们无法被覆盖。
- 无需将CGLIB添加到类路径中。从Spring 3.2开始，CGLIB被重新打包并包含在spring-core JAR中。换句话说，基于CGLIB的AOP将像JDK动态代理一样“开箱即用”。

CGLIB代理和动态代理之间的性能差异很小。从Spring 1.0开始，动态代理略快一些。但是，这可能会在未来发生变化。在这种情况下，绩效不应该是决定性的考虑因素。

### 12.5.6使用“global”顾问

通过在拦截器名称后附加星号，所有具有与星号前面部分匹配的bean名称的顾问程序将添加到顾问程序链中。如果您需要添加一组标准的“全局”顾问程序，这可以派上用场：

```xml
<bean id="proxy" class="org.springframework.aop.framework.ProxyFactoryBean">
    <property name="target" ref="service"/>
    <property name="interceptorNames">
        <list>
            <value>global*</value>
        </list>
    </property>
</bean>

<bean id="global_debug" class="org.springframework.aop.interceptor.DebugInterceptor"/>
<bean id="global_performance" class="org.springframework.aop.interceptor.PerformanceMonitorInterceptor"/>
```

