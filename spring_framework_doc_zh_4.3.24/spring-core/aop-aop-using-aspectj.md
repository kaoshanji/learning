# 11.8 spring应用里的AspectJ

到目前为止，我们在本章中介绍的所有内容都是纯粹的Spring AOP。在本节中，我们将讨论如何使用AspectJ编译器/编织器代替Spring AOP，或者除了Spring AOP之外，如果您的需求超出Spring AOP提供的功能。

Spring附带了一个小的AspectJ方面库，可以在您的发行版中独立使用`spring-aspects.jar`; 您需要将其添加到类路径中才能使用其中的方面。 第11.8.1节“使用AspectJ依赖于使用Spring注入域对象” 和 第11.8.2节“AspectJ的其他Spring方面” 讨论了该库的内容以及如何使用它。 第11.8.3节“使用Spring IoC配置AspectJ方面” 讨论了如何依赖注入使用AspectJ编译器编织的AspectJ方面。最后， 第11.8.4节“在Spring框架中 使用AspectJ进行加载时编织[”](aop.html#aop-aj-ltw)介绍了使用AspectJ为Spring应用程序加载时编织。

### 11.8.1使用AspectJ依赖注入域对象与Spring

Spring容器实例化和配置在应用程序上下文中定义的bean。在 给定包含要应用的配置的bean定义的名称的情况下，还可以要求bean工厂配置*预先存在的*对象。它`spring-aspects.jar`包含一个注释驱动的方面，利用此功能允许依赖注入*任何对象*。该支持旨在用于*在任何容器控制之外*创建的对象。域对象通常属于此类别，因为它们通常使用`new`运算符以编程方式创建，或者由于数据库查询而由ORM工具创建 。

该`@Configurable`注释标记一个类符合Spring驱动配置的条件。在最简单的情况下，它可以用作标记注释：

```java
package com.xyz.myapp.domain;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class Account {
    // ...
}
```

当以这种方式用作标记接口时，Spring将`Account`使用与完全限定类型名称（`com.xyz.myapp.domain.Account`）具有相同名称的bean定义（通常为prototype-scoped）来配置带注释类型的新实例（在本例中 ）。由于bean的默认名称是其类型的完全限定名称，因此声明原型定义的简便方法是省略该`id`属性：

```xml
<bean class="com.xyz.myapp.domain.Account" scope="prototype">
    <property name="fundsTransferService" ref="fundsTransferService"/>
</bean>
```

如果要显式指定要使用的原型bean定义的名称，可以直接在注释中执行此操作：

```java
package com.xyz.myapp.domain;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable("account")
public class Account {
    // ...
}
```

Spring现在将查找名为“account”的bean定义，并将其用作配置新`Account`实例的定义。

您还可以使用自动装配来避免必须指定专用的bean定义。要让Spring应用自动装配，请使用注释的`autowire`属性 `@Configurable`：分别指定`@Configurable(autowire=Autowire.BY_TYPE)`或 `@Configurable(autowire=Autowire.BY_NAME`按类型或名称指定自动装配。作为替代方案，从Spring 2.5开始，最好`@Configurable`通过使用`@Autowired`或`@Inject`在字段或方法级别为bean 指定显式的，注释驱动的依赖注入（ 有关更多详细信息[，](beans.html#beans-annotation-config)请参见 第7.9节“基于注释的容器配置”）。

最后，您可以使用`dependencyCheck`属性（例如 :)在新创建和配置的对象中为对象引用启用Spring依赖性检查`@Configurable(autowire=Autowire.BY_NAME,dependencyCheck=true)`。如果此属性设置为true，则Spring将在配置后验证是否已设置所有属性（*不是基元或集合*）。

单独使用注释当然不会做任何事情。它是 `AnnotationBeanConfigurerAspect`在`spring-aspects.jar`作用于注释的存在。本质上，方面说“在从注释类型的新对象的初始化返回之后`@Configurable`，根据注释的属性使用Spring配置新创建的对象”。在此上下文中， *初始化*是指新实例化的对象（例如，用`new`操作符实例化的对象）以及`Serializable`正在进行反序列化的对象（例如，通过 [readResolve（）](https://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html)）。

上段中的一个关键短语是“ *实质上* ”。在大多数情况下，确切的语义“ *从一个新对象初始化返回之后* ”将被罚款......在这种情况下，“ *初始化之后* ”意味着依赖将被注入*后*的对象已经构造-这意味着，依赖项将无法在类的构造函数体中使用。如果您希望在构造函数体执行*之前*注入依赖项，从而可以*在*构造函数体中使用，那么您需要在`@Configurable`声明上定义它， 如下所示：

```java
@Configurable(preConstruction=true)
```

为此，必须使用AspectJ编织器编写带注释的类型 - 您可以使用构建时Ant或Maven任务来执行此操作（请参阅 [AspectJ开发环境指南](https://www.eclipse.org/aspectj/doc/released/devguide/antTasks.html)）或加载时编织（请参阅[第11.8节）。 4，“在Spring框架中使用AspectJ进行加载时编织” ）。它 `AnnotationBeanConfigurerAspect`本身需要通过Spring进行配置（以获取对用于配置新对象的bean工厂的引用）。如果您使用的是基于Java的配置，只需添加`@EnableSpringConfigured`到任何 `@Configuration`类。

```java
@Configuration
@EnableSpringConfigured
public class AppConfig {

}
```

如果您更喜欢基于XML的配置，Spring [`context`命名空间](xsd-configuration.html#xsd-config-body-schemas-context)定义了一个方便的`context:spring-configured`元素：

```xml
<context:spring-configured/>
```

在配置方面*之前*`@Configurable`创建的对象实例将导致向调试日志发出消息，并且不会发生对象的配置。一个示例可能是Spring配置中的bean，它在Spring初始化时创建域对象。在这种情况下，您可以使用“depends-on”bean属性手动指定bean依赖于配置方面。

```xml
<bean id="myService"
        class="com.xzy.myapp.service.MyService"
        depends-on="org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect">

    <!-- ... -->

</bean>
```

不要`@Configurable`通过bean配置器方面激活处理，除非你真的想在运行时依赖它的语义。特别是，请确保您不使用`@Configurable`在容器上注册为常规Spring bean的bean类：否则，您将获得双重初始化，一次通过容器，一次通过方面。

#### 单元测试@Configurable对象

`@Configurable`支持的目标之一是实现域对象的独立单元测试，而没有与硬编码查找相关的困难。如果 `@Configurable`类型尚未由AspectJ编织，那么注释在单元测试期间没有任何影响，您只需在被测对象中设置模拟或存根属性引用并继续正常进行。如果`@Configurable`类型*已*通过AspectJ织然后仍然可以在容器为正常的外部单元的测试，但你会看到在每次构建时间的警告消息`@Configurable`，指示它没有被Spring配置对象。

#### 使用多个应用程序上下文

在`AnnotationBeanConfigurerAspect`用于实现`@Configurable`的支持是一个AspectJ singleton切面。单例方面的范围与`static`成员的范围相同 ，也就是说每个类加载器有一个方面实例来定义类型。这意味着如果在同一个类加载器层次结构中定义多个应用程序上下文，则需要考虑在何处定义`@EnableSpringConfigured`bean以及在`spring-aspects.jar`类路径上放置的位置。

考虑一个典型的Spring Web应用程序配置，其中包含共享父应用程序上下文，用于定义公共业务服务和支持它们所需的一切，以及每个servlet包含一个子应用程序上下文，其中包含特定于该servlet 所有这些上下文将在同一个类加载器层次结构中共存，因此 `AnnotationBeanConfigurerAspect`只能包含对其中一个的引用。在这种情况下，我们建议`@EnableSpringConfigured`在共享（父）应用程序上下文中定义bean：这定义了您可能希望注入域对象的服务。结果是您无法使用@Configurable机制（可能不是您想要做的事情！）来配置域对象，并引用在子（特定于servlet）的上下文中定义的bean。

当部署在同一个容器内的多个web的应用程序，确保每个网络的应用程序加载类型`spring-aspects.jar`（例如，通过将使用其自己的类加载器`spring-aspects.jar`中`'WEB-INF/lib'`）。如果`spring-aspects.jar`仅添加到容器范围的类路径（并因此由共享父类加载器加载），则所有Web应用程序将共享相同的方面实例，这可能不是您想要的。

### 11.8.2 AspectJ的其他Spring方面

除了`@Configurable`方面之外，还`spring-aspects.jar`包含一个AspectJ方面，可用于为使用注释注释的类型和方法驱动Spring的事务管理`@Transactional`。这主要适用于希望在Spring容器之外使用Spring Framework的事务支持的用户。

解释`@Transactional`注释的方面是 `AnnotationTransactionAspect`。使用此方面时，必须注释 *实现*类（和/或该类中的方法），*而不是*该类实现的接口（如果有）。AspectJ遵循Java的规则，即接口上的注释*不会被继承*。

一`@Transactional`类上注解指定任何执行默认事务语义*公共*操作的类。

`@Transactional`类中方法的注释会覆盖类注释（如果存在）给出的默认事务语义。可以注释任何可见性的方法，包括私有方法。直接注释非公共方法是获得执行此类方法的事务划分的唯一方法。

从Spring Framework 4.2开始，`spring-aspects`提供了一个类似的方面，为标准`javax.transaction.Transactional`注释提供完全相同的功能。查看 `JtaAnnotationTransactionAspect`更多详细信息。

对于想要使用Spring配置和事务管理支持但不想（或不能）使用注释的AspectJ程序员，`spring-aspects.jar` 还包含`abstract`可以扩展以提供自己的切入点定义的方面。有关更多信息，请参阅`AbstractBeanConfigurerAspect`和 `AbstractTransactionAspect`方面的来源。作为示例，以下摘录显示了如何使用与完全限定类名匹配的原型bean定义编写方面来配置域模型中定义的所有对象实例：

```java
public aspect DomainObjectConfiguration extends AbstractBeanConfigurerAspect {

    public DomainObjectConfiguration() {
        setBeanWiringInfoResolver(new ClassNameBeanWiringInfoResolver());
    }

    // the creation of a new bean (any object in the domain model)
    protected pointcut beanCreation(Object beanInstance) :
        initialization(new(..)) &&
        SystemArchitecture.inDomainModel() &&
        this(beanInstance);

}
```

### 11.8.3使用Spring IoC配置AspectJ方面

在Spring应用程序中使用AspectJ方面时，很自然希望能够使用Spring配置这些方面。AspectJ运行时本身负责方面创建，并且通过Spring配置AspectJ创建方面的方法取决于方面使用的AspectJ实例化模型（`per-xxx`子句）。

AspectJ的大多数方面都是*单例*方面。这些方面的配置非常简单：只需创建一个引用方面类型的bean定义，并包含bean属性`'factory-method="aspectOf"'`。这可以确保Spring通过向AspectJ请求它来获取方面实例，而不是尝试创建实例本身。例如：

```xml
<bean id="profiler" class="com.xyz.profiler.Profiler"
        factory-method="aspectOf">

    <property name="profilingStrategy" ref="jamonProfilingStrategy"/>
</bean>
```

非单例方面更难配置：但是可以通过创建原型bean定义并使用`@Configurable`支持 `spring-aspects.jar`来配置方面实例（一旦它们具有由AspectJ运行时创建的bean）来实现。

如果您想要使用AspectJ编写一些@AspectJ方面（例如，使用域模型类型的加载时编织）和其他要与Spring AOP一起使用的@AspectJ方面，并且这些方面都是使用Spring配置的，那么你需要告诉Spring AOP @AspectJ autoproxying支持配置中定义的@AspectJ方面的确切子集应该用于自动代理。您可以通过`<include/>`在`<aop:aspectj-autoproxy/>` 声明中使用一个或多个元素来完成此操作。每个`<include/>`元素都指定一个名称模式，只有名称与至少一个模式匹配的bean才会用于Spring AOP autoproxy配置：

```xml
<aop:aspectj-autoproxy>
    <aop:include name="thisBean"/>
    <aop:include name="thatBean"/>
</aop:aspectj-autoproxy>
```

不要被`<aop:aspectj-autoproxy/>`元素的名称误导：使用它将导致创建*Spring AOP代理*。这里只使用@AspectJ样式的方面声明，但*不*涉及AspectJ运行时。

### 11.8.4 Spring Framework中使用AspectJ进行加载时编织

加载时编织（LTW）是指在将AspectJ方面加载到Java虚拟机（JVM）中时将其编织到应用程序的类文件中的过程。本节的重点是在Spring Framework的特定上下文中配置和使用LTW：本节不是对LTW的介绍。有关LTW细节的详细信息以及仅使用AspectJ配置LTW（完全不涉及Spring），请参阅[AspectJ开发环境指南](https://www.eclipse.org/aspectj/doc/released/devguide/ltw.html)的 [LTW部分](https://www.eclipse.org/aspectj/doc/released/devguide/ltw.html)。

Spring Framework为AspectJ LTW带来的附加价值在于对编织过程实现更细粒度的控制。'Vanilla'AspectJ LTW使用Java（5+）代理实现，该代理通过在启动JVM时指定VM参数来启用。因此它是一个JVM范围的设置，在某些情况下可能很好，但通常有点太粗糙。支持Spring的LTW使您能够在*每个ClassLoader的*基础上打开LTW ，这显然更精细，并且在“单JVM多应用程序”环境中更有意义（例如在典型的环境中）应用服务器环境）。

此外，[在某些环境中](aop.html#aop-aj-ltw-environments)，此支持可实现加载时编织，*而无需对应用程序服务器的启动脚本进行任何修改，这些修改*将需要添加`-javaagent:path/to/aspectjweaver.jar`或（如本节后面部分所述）`-javaagent:path/to/org.springframework.instrument-{version}.jar`（以前称为 `spring-agent.jar`）。开发人员只需修改构成应用程序上下文的一个或多个文件即可启用加载时编织，而不是依赖通常负责部署配置的管理员（如启动脚本）。

既然销售情况已经结束，那么让我们首先介绍使用Spring的AspectJ LTW的快速示例，然后详细介绍以下示例中介绍的元素。有关完整示例，请参阅 [Petclinic示例应用程序](https://github.com/spring-projects/spring-petclinic)。

#### 第一个例子

让我们假设您是一名应用程序开发人员，负责诊断系统中某些性能问题的原因。我们要做的不是打破分析工具，而是打开一个简单的分析方面，这将使我们能够非常快速地获得一些性能指标，这样我们就可以立即将更精细的分析工具应用于该特定区域。然后。

此处提供的示例使用XML样式配置，也可以使用[Java Configuration](beans.html#beans-java)配置和使用@AspectJ 。具体而言，`@EnableLoadTimeWeaving`注释可以用作替代`<context:load-time-weaver/>`（参见[下面](aop.html#aop-aj-ltw-spring)的详细信息）。

这是剖析方面。没有什么太花哨的，只是一个快速而肮脏的基于时间的探查器，使用@ AspectJ风格的方面声明。

```java
package foo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;
import org.springframework.core.annotation.Order;

@Aspect
public class ProfilingAspect {

    @Around("methodsToBeProfiled()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch(getClass().getSimpleName());
        try {
            sw.start(pjp.getSignature().getName());
            return pjp.proceed();
        } finally {
            sw.stop();
            System.out.println(sw.prettyPrint());
        }
    }

    @Pointcut("execution(public * foo..*.*(..))")
    public void methodsToBeProfiled(){}
}
```

我们还需要创建一个`META-INF/aop.xml`文件，以通知AspectJ weaver我们想要将我们`ProfilingAspect`编入我们的类中。此文件约定，即所调用的Java类路径上的文件（或多个文件）的存在 `META-INF/aop.xml`是标准AspectJ。

```xml
<!DOCTYPE aspectj PUBLIC "-//AspectJ//DTD//EN" "https://www.eclipse.org/aspectj/dtd/aspectj.dtd">
<aspectj>

    <weaver>
        <!-- only weave classes in our application-specific packages -->
        <include within="foo.*"/>
    </weaver>

    <aspects>
        <!-- weave in just this aspect -->
        <aspect name="foo.ProfilingAspect"/>
    </aspects>

</aspectj>
```

现在到Spring特定的配置部分。我们需要配置一个 `LoadTimeWeaver`（稍后解释，现在就把它当作信任）。此加载时weaver是负责将一个或多个`META-INF/aop.xml`文件中的方面配置编织到应用程序的类中的基本组件。好处是它不需要很多配置，如下所示（您可以指定更多选项，但稍后会详细介绍）。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <!-- a service object; we will be profiling its methods -->
    <bean id="entitlementCalculationService"
            class="foo.StubEntitlementCalculationService"/>

    <!-- this switches on the load-time weaving -->
    <context:load-time-weaver/>
</beans>
```

现在所有必需的工件都已到位 - 方面，`META-INF/aop.xml` 文件和Spring配置 - 让我们创建一个简单的驱动程序类，其中包含一个`main(..)`演示LTW 的 方法。

```java
package foo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class Main {

    public static void main(String[] args) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml", Main.class);

        EntitlementCalculationService entitlementCalculationService
            = (EntitlementCalculationService) ctx.getBean("entitlementCalculationService");

        // the profiling aspect is 'woven' around this method execution
        entitlementCalculationService.calculateEntitlement();
    }
}
```

还有最后一件事要做。本节的介绍确实说可以选择在`ClassLoader`Spring 上选择性地打开LTW ，这是事实。但是，仅为此示例，我们将使用Java代理（随Spring提供）来打开LTW。这是我们用来运行上面`Main`类的命令行：

```bash
java -javaagent:C:/projects/foo/lib/global/spring-instrument.jar foo.Main
```

这`-javaagent`是一个标志，用于指定和启用 [代理程序来检测在JVM上运行的程序](https://docs.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html)。Spring Framework附带了一个代理程序，`InstrumentationSavingAgent`它包含 在上面示例`spring-instrument.jar`中作为`-javaagent`参数值提供的代理程序中。

执行`Main`程序的输出结果如下所示。（我已经`Thread.sleep(..)`在`calculateEntitlement()` 实现中引入了一个声明，以便探查器实际捕获0毫秒以外的东西 - `01234`毫秒*不是* AOP引入的开销:)）

```bash
Calculating entitlement

StopWatch 'ProfilingAspect': running time (millis) = 1234
------ ----- ----------------------------
ms     %     Task name
------ ----- ----------------------------
01234  100%  calculateEntitlement
```

由于LTW是使用成熟的AspectJ实现的，因此我们不仅限于为Spring bean提供建议; 程序的以下细微变化`Main`将产生相同的结果。

```java
package foo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class Main {

    public static void main(String[] args) {

        new ClassPathXmlApplicationContext("beans.xml", Main.class);

        EntitlementCalculationService entitlementCalculationService =
            new StubEntitlementCalculationService();

        // the profiling aspect will be 'woven' around this method execution
        entitlementCalculationService.calculateEntitlement();
    }
}
```

注意在上面的程序中我们只是简单地引导Spring容器，然后创建一个`StubEntitlementCalculationService`完全在Spring上下文之外的新实例......分析建议仍然被编织进去。

不可否认，这个例子很简单......但是在上面的例子中已经介绍了Spring中LTW支持的基础知识，本节的其余部分将详细解释每个配置和用法背后的“原因”。

在`ProfilingAspect`本例中使用可能是基本的，但它是非常有用的。这是开发人员在开发期间（当然）可以使用的开发时间方面的一个很好的例子，然后很容易从部署到UAT或生产中的应用程序的构建中排除。

#### 方面

您在LTW中使用的方面必须是AspectJ方面。它们可以用AspectJ语言本身编写，也可以用@ AspectJ风格编写方面。这意味着您的方面都是有效的AspectJ *和* Spring AOP方面。此外，编译的方面类需要在类路径上可用。

#### 'META-INF / aop.xml文件'

AspectJ LTW基础结构使用一个或多个`META-INF/aop.xml` 文件进行配置，这些文件位于Java类路径上（直接或更常见于jar文件中）。

该文件的结构和内容在主要的AspectJ参考文档中有详细介绍，感兴趣的读者可以 [参考该资源](https://www.eclipse.org/aspectj/doc/released/devguide/ltw-configuration.html)。（我很欣赏这一部分是简短的，但`aop.xml`文件是100％AspectJ - 没有适用于它的特定于Spring的信息或语义，因此没有额外的值可以作为结果贡献），所以相反而不是重述AspectJ开发人员所写的相当令人满意的部分，我只是指导你那里。）

#### 必需的库（JARS）

您至少需要以下库才能使用Spring Framework对AspectJ LTW的支持：

- `spring-aop.jar` （版本2.5或更高版本，加上所有必需的依赖项）
- `aspectjweaver.jar` （1.6.8或更高版本）

如果您使用[Spring提供的代理来启用检测](aop.html#aop-aj-ltw-environment-generic)，您还需要：

- `spring-instrument.jar`

#### 弹簧配置

Spring的LTW支持的关键组件是`LoadTimeWeaver`接口（在 `org.springframework.instrument.classloading`包中），以及随Spring发行版一起提供的众多实现。一个`LoadTimeWeaver`是负责添加一个或一个以上`java.lang.instrument.ClassFileTransformers`的`ClassLoader`在运行时，这将打开大门，各种各样有趣的应用方式，其中一个正好是各方面的LTW。

如果您不熟悉运行时类文件转换的想法，建议您`java.lang.instrument`在继续之前阅读该包的javadoc API文档。这不是一项繁重的工作，因为那里有相当令人讨厌的珍贵文档......关键接口和类至少会在您阅读本章时作为参考。

`LoadTimeWeaver`为特定项目配置a `ApplicationContext`可以像添加一行一样简单。（请注意，你几乎肯定需要使用一个 `ApplicationContext`作为你的Spring容器 - 通常`BeanFactory`是不够的，因为LTW支持使用`BeanFactoryPostProcessors`。）

要启用Spring Framework的LTW支持，您需要配置a `LoadTimeWeaver`，这通常使用`@EnableLoadTimeWeaving`注释完成。

```java
@Configuration
@EnableLoadTimeWeaving
public class AppConfig {

}
```

或者，如果您更喜欢基于XML的配置，请使用该 `<context:load-time-weaver/>`元素。请注意，该元素是在`context`命名空间中定义的 。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:load-time-weaver/>

</beans>
```

上面的配置将自动为您定义和注册一些LTW特定的基础架构bean，例如a `LoadTimeWeaver`和an `AspectJWeavingEnabler`。默认`LoadTimeWeaver`是`DefaultContextLoadTimeWeaver`类，它尝试装饰自动检测到的`LoadTimeWeaver`：`LoadTimeWeaver`“自动检测” 的确切类型 取决于您的运行时环境（在下表中总结）。

**表11.1。DefaultContextLoadTimeWeaver LoadTimeWeavers**

| 运行环境                                                     | `LoadTimeWeaver` 履行           |
| ------------------------------------------------------------ | ------------------------------- |
| 在Oracle的[WebLogic中](https://www.oracle.com/technetwork/middleware/weblogic/overview/index-085209.html)运行 | `WebLogicLoadTimeWeaver`        |
| 在Oracle的[GlassFish中](https://glassfish.dev.java.net/)运行 | `GlassFishLoadTimeWeaver`       |
| 在[Apache Tomcat中](https://tomcat.apache.org/)运行          | `TomcatLoadTimeWeaver`          |
| 在Red Hat的[JBoss AS](https://www.jboss.org/jbossas/)或[WildFly中运行](https://www.wildfly.org/) | `JBossLoadTimeWeaver`           |
| 在IBM的[WebSphere中](https://www-01.ibm.com/software/webservers/appserv/was/)运行 | `WebSphereLoadTimeWeaver`       |
| JVM以Spring开头`InstrumentationSavingAgent` *（java -javaagent：path / to / spring-instrument.jar）* | `InstrumentationLoadTimeWeaver` |
| 后备，期望底层的ClassLoader遵循常见的约定（例如适用于`TomcatInstrumentableClassLoader`和[Resin](https://www.caucho.com/)） | `ReflectiveLoadTimeWeaver`      |

请注意，这些只是`LoadTimeWeavers`在使用时自动检测的 `DefaultContextLoadTimeWeaver`：当然可以准确指定 `LoadTimeWeaver`您希望使用的实现。

要`LoadTimeWeaver`使用Java配置指定特定实现 `LoadTimeWeavingConfigurer`接口并覆盖该`getLoadTimeWeaver()`方法：

```java
@Configuration
@EnableLoadTimeWeaving
public class AppConfig implements LoadTimeWeavingConfigurer {

    @Override
    public LoadTimeWeaver getLoadTimeWeaver() {
        return new ReflectiveLoadTimeWeaver();
    }
}
```

如果使用基于XML的配置，则可以将完全限定的类名指定为 元素`weaver-class`上属性的值`<context:load-time-weaver/>`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:load-time-weaver
            weaver-class="org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver"/>

</beans>
```

在`LoadTimeWeaver`由配置定义和注册可以使用公知的名称Spring容器以后提取`loadTimeWeaver`。请记住，`LoadTimeWeaver`存在就像Spring的LTW基础架构添加一个或多个的机制一样`ClassFileTransformers`。`ClassFileTransformer`LTW 的实际 情况是`ClassPreProcessorAgentAdapter`（来自`org.aspectj.weaver.loadtime`包）类。有关`ClassPreProcessorAgentAdapter`更多详细信息，请参阅类的类级别javadoc ，因为编织实际如何实现的细节超出了本节的范围。

剩下要讨论的配置有一个最终属性： `aspectjWeaving`属性（或者`aspectj-weaving`如果您使用的是XML）。这是一个控制LTW是否启用的简单属性; 它是如此简单。它接受以下总结的三个可能值中的一个，`autodetect`如果该属性不存在，则默认值为 。

**表11.2。AspectJ编织属性值**

| 注释值       | XML值        | 说明                                                         |
| ------------ | ------------ | ------------------------------------------------------------ |
| `ENABLED`    | `on`         | AspectJ编织已启用，并且各个方面将在加载时编辑。              |
| `DISABLED`   | `off`        | LTW关闭......在加载时不会编织任何方面。                      |
| `AUTODETECT` | `autodetect` | 如果Spring LTW基础结构可以找到至少一个`META-INF/aop.xml`文件，那么AspectJ编织就会打开，否则就会关闭。这是默认值。 |

#### 特定于环境的配置

最后一节包含在应用程序服务器和Web容器等环境中使用Spring的LTW支持时所需的任何其他设置和配置。

##### Tomcat的

从历史上看，[Apache Tomcat](https://tomcat.apache.org/)的默认类加载器不支持类转换，这就是Spring提供满足此需求的增强实现的原因。命名`TomcatInstrumentableClassLoader`，加载程序适用于Tomcat 6.0及更高版本。

`TomcatInstrumentableClassLoader`不再在Tomcat 8.0及更高版本上定义。相反，让Spring `InstrumentableClassLoader` 通过`TomcatLoadTimeWeaver`策略自动使用Tomcat的新本机设施。

如果您仍然需要使用`TomcatInstrumentableClassLoader`，可以为*每个* Web应用程序单独注册，如下所示：

- 复制`org.springframework.instrument.tomcat.jar`到*$ CATALINA_HOME* / lib，其中 *$ CATALINA_HOME*表示Tomcat安装的根目录）
- 通过编辑Web应用程序上下文文件，指示Tomcat使用自定义类加载器（而不是默认值）：

```xml
<Context path="/myWebApp" docBase="/my/webApp/location">
    <Loader
        loaderClass="org.springframework.instrument.classloading.tomcat.TomcatInstrumentableClassLoader"/>
</Context>
```

Apache Tomcat（6.0+）支持多个上下文位置：

- 服务器配置文件 - *$ CATALINA_HOME / conf / server.xml*
- 默认上下文配置 - *$ CATALINA_HOME / conf / context.xml* - 影响所有已部署的Web应用程序
- 每个Web应用程序配置，可以在服务器端部署在 *$ CATALINA_HOME / conf / [enginename] / [hostname] / [webapp] -context.xml，*也可以嵌入在*META-INF / context*的web-app存档*中.XML*

为了提高效率，建议使用嵌入式per-web-app配置样式，因为它只会影响使用自定义类加载器的应用程序，并且不需要对服务器配置进行任何更改。有关可用上下文位置的更多详细信息，请参阅Tomcat 6.0.x [文档](https://tomcat.apache.org/tomcat-6.0-doc/config/context.html)。

或者，考虑使用Spring提供的通用VM代理，在Tomcat的启动脚本中指定（参见上文）。这将使所有已部署的Web应用程序都可以使用检测，无论它们恰好运行在哪个ClassLoader上。

##### WebLogic，WebSphere，Resin，GlassFish，JBoss

最新版本的WebLogic Server（版本10及更高版本），IBM WebSphere Application Server（版本7及更高版本），Resin（3.1及更高版本）和JBoss（6.x或更高版本）提供了一个能够进行本地检测的ClassLoader。Spring的原生LTW利用这种ClassLoader来实现AspectJ编织。您可以通过简单地激活加载时编织来启用LTW，如前所述。具体来说，你就*不会*需要修改启动脚本来添加`-javaagent:path/to/spring-instrument.jar`。

请注意，支持GlassFish检测的ClassLoader仅在其EAR环境中可用。对于GlassFish Web应用程序，请按照上面概述的Tomcat设置说明进行操作。

请注意，在JBoss 6.x上，需要禁用应用服务器扫描，以防止它在应用程序实际启动之前加载类。一个快速的解决方法是向您的工件添加一个名为`WEB-INF/jboss-scanning.xml`以下内容的文件：

```xml
<scanning xmlns="urn:jboss:scanning:1.0"/>
```

##### 通用Java应用程序

如果在不支持或不支持现有`LoadTimeWeaver`实现的环境中需要类检测，则JDK代理可以是唯一的解决方案。对于这种情况，Spring提供了`InstrumentationLoadTimeWeaver`，它需要一个特定于Spring的（但非常通用的）VM代理 `org.springframework.instrument-{version}.jar`（以前称为`spring-agent.jar`）。

要使用它，必须通过提供以下JVM选项，使用Spring代理启动虚拟机：

```
-javaagent：/path/to/org.springframework.instrument- {}版本的.jar
```

请注意，这需要修改VM启动脚本，这可能会阻止您在应用程序服务器环境中使用它（取决于您的操作策略）。此外，JDK代理将检测*整个* VM，这可能证明是昂贵的。

出于性能原因，仅当目标环境（例如[Jetty](https://www.eclipse.org/jetty/)）没有（或不支持）专用LTW时，才建议使用此配置。