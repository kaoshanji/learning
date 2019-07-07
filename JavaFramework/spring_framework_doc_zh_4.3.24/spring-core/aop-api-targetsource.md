# 12.10 TargetSources

Spring提供了*TargetSource*的概念，在`org.springframework.aop.TargetSource`界面中表示 。该接口负责返回实现连接点的“目标对象”。在`TargetSource` 每一个AOP代理处理一个方法调用时实现请求一个目标实例。

使用Spring AOP的开发人员通常不需要直接使用TargetSources，但这提供了支持池，热插拔和其他复杂目标的强大方法。例如，池化TargetSource可以为每次调用返回不同的目标实例，使用池来管理实例。

如果未指定TargetSource，则使用包装本地对象的默认实现。每次调用都会返回相同的目标（正如您所期望的那样）。

让我们看一下Spring提供的标准目标源，以及如何使用它们。

使用自定义目标源时，目标通常需要是原型而不是单例bean定义。这允许Spring在需要时创建新的目标实例。

### 12.10.1热插拔目标源

的`org.springframework.aop.target.HotSwappableTargetSource`存在允许同时允许调用者保持引用它，切换一个AOP代理的目标。

更改目标源的目标会立即生效。这 `HotSwappableTargetSource`是线程安全的。

您可以通过`swap()`HotSwappableTargetSource 上的方法更改目标，如下所示：

```java
HotSwappableTargetSource swapper = (HotSwappableTargetSource) beanFactory.getBean("swapper");
Object oldTarget = swapper.swap(newTarget);
```

所需的XML定义如下所示：

```xml
<bean id="initialTarget" class="mycompany.OldTarget"/>

<bean id="swapper" class="org.springframework.aop.target.HotSwappableTargetSource">
    <constructor-arg ref="initialTarget"/>
</bean>

<bean id="swappable" class="org.springframework.aop.framework.ProxyFactoryBean">
    <property name="targetSource" ref="swapper"/>
</bean>
```

上面的`swap()`调用更改了可交换bean的目标。持有对该bean的引用的客户端将不知道该更改，但会立即开始命中新目标。

虽然这个例子没有添加任何建议 - 并且没有必要添加建议来使用`TargetSource`- 当然任何建议`TargetSource`都可以与任意建议一起使用。

### 12.10.2汇集目标来源

使用池化目标源为无状态会话EJB提供了类似的编程模型，其中维护了相同实例的池，方法调用将释放池中的空闲对象。

Spring池和SLSB池之间的一个重要区别是Spring池可以应用于任何POJO。与Spring一样，此服务可以以非侵入方式应用。

Spring为Commons Pool 2.2提供了开箱即用的支持，它提供了一个相当有效的池实现。您需要在应用程序的类路径上使用commons-pool Jar才能使用此功能。也可以子类化 `org.springframework.aop.target.AbstractPoolingTargetSource`以支持任何其他池化API。

Commons Pool 1.5+也受支持，但在Spring Framework 4.2中已弃用。

示例配置如下所示：

```xml
<bean id="businessObjectTarget" class="com.mycompany.MyBusinessObject"
        scope="prototype">
    ... properties omitted
</bean>

<bean id="poolTargetSource" class="org.springframework.aop.target.CommonsPool2TargetSource">
    <property name="targetBeanName" value="businessObjectTarget"/>
    <property name="maxSize" value="25"/>
</bean>

<bean id="businessObject" class="org.springframework.aop.framework.ProxyFactoryBean">
    <property name="targetSource" ref="poolTargetSource"/>
    <property name="interceptorNames" value="myInterceptor"/>
</bean>
```

请注意，目标对象 - 示例中的“businessObjectTarget” - *必须*是原型。这允许`PoolingTargetSource`实现创建目标的新实例以根据需要增长池。`AbstractPoolingTargetSource`有关其属性的信息，请参阅您希望用于的javadocs 和具体子类：“maxSize”是最基本的，并且始终保证存在。

在这种情况下，“myInterceptor”是需要在同一IoC上下文中定义的拦截器的名称。但是，没有必要指定拦截器来使用池。如果您只想要池化，而没有其他建议，请不要设置interceptorNames属性。

可以配置Spring以便能够将任何池化对象转换为 `org.springframework.aop.target.PoolingConfig`接口，从而通过介绍公开有关池的配置和当前大小的信息。你需要定义一个像这样的顾问：

```xml
<bean id="poolConfigAdvisor" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
    <property name="targetObject" ref="poolTargetSource"/>
    <property name="targetMethod" value="getPoolingConfigMixin"/>
</bean>
```

通过在`AbstractPoolingTargetSource`类上调用便捷方法获得此顾问程序 ，因此使用MethodInvokingFactoryBean。此顾问程序的名称（此处为“poolConfigAdvisor”）必须位于ProxyFactoryBean中的拦截器名称列表中，以显示池化对象。

演员表如下：

```java
PoolingConfig conf = (PoolingConfig) beanFactory.getBean("businessObject");
System.out.println("Max pool size is " + conf.getMaxSize());
```

通常不需要池化无状态服务对象。我们不相信它应该是默认选择，因为大多数无状态对象自然是线程安全的，并且如果缓存资源，实例池是有问题的。

使用自动代理可以实现更简单的池化。可以设置任何自动代理创建者使用的TargetSource。

### 12.10.3原型目标来源

设置“原型”目标源类似于池化TargetSource。在这种情况下，将在每个方法调用上创建目标的新实例。虽然在现代JVM中创建新对象的成本并不高，但连接新对象（满足其IoC依赖性）的成本可能更高。因此，如果没有充分理由，就不应该使用这种方法。

为此，您可以`poolTargetSource`按如下方式修改上面显示的定义。（为了清楚起见，我也更改了名称。）

```xml
<bean id="prototypeTargetSource" class="org.springframework.aop.target.PrototypeTargetSource">
    <property name="targetBeanName" ref="businessObjectTarget"/>
</bean>
```

只有一个属性：目标bean的名称。TargetSource实现中使用继承来确保一致的命名。与池化目标源一样，目标bean必须是原型bean定义。

### 12.10.4 ThreadLocal目标源

`ThreadLocal`如果需要为每个传入请求创建一个对象（每个线程），目标源很有用。a的概念`ThreadLocal`提供了一个JDK范围的工具，可以在线程旁边透明地存储资源。设置a `ThreadLocalTargetSource`与其他类型的目标源所解释的几乎相同：

```xml
<bean id="threadlocalTargetSource" class="org.springframework.aop.target.ThreadLocalTargetSource">
    <property name="targetBeanName" value="businessObjectTarget"/>
</bean>
```

ThreadLocals在多线程和多类加载器环境中错误地使用它们时会遇到严重问题（可能导致内存泄漏）。应该总是考虑将threadlocal包装在其他类中，而不是直接使用它`ThreadLocal`本身（当然在包装类中除外）。此外，应始终记住正确设置和取消设置（后者仅涉及调用 `ThreadLocal.set(null)`）线程本地的资源。在任何情况下都应该进行取消设置，因为不取消设置可能会导致有问题的行为。Spring的ThreadLocal支持为您执行此操作，应始终考虑使用ThreadLocals而不使用其他正确的处理代码。