# 7.8 容器扩展点

通常，应用程序开发人员不需要子类化`ApplicationContext` 实现类。相反，可以通过插入特殊集成接口的实现来扩展Spring IoC容器。接下来的几节将介绍这些集成接口。

### 7.8.1使用BeanPostProcessor自定义bean

该`BeanPostProcessor`接口定义了您可以实现的*回调方法*，以提供您自己的（或覆盖容器的默认）实例化逻辑，依赖关系解析逻辑等。如果要在Spring容器完成实例化，配置和初始化bean之后实现某些自定义逻辑，则可以插入一个或多个自定义`BeanPostProcessor`实现。

您可以配置多个`BeanPostProcessor`实例，并可以`BeanPostProcessor`通过设置`order`属性来控制这些实例的执行顺序。只有在`BeanPostProcessor`实现`Ordered`接口时才能设置此属性; 如果你自己编写，你`BeanPostProcessor`也应该考虑实现这个`Ordered` 接口。有关更多详细信息，请参阅`BeanPostProcessor`和 `Ordered`接口的javadoc 。

`BeanPostProcessor`s对bean（或对象）*实例进行操作* ; 也就是说，Spring IoC容器实例化一个bean实例，*然后* `BeanPostProcessor`执行它们的工作。

`BeanPostProcessor`s是*每个容器的*范围。这仅在您使用容器层次结构时才有意义。如果`BeanPostProcessor`在一个容器中定义一个容器，它将*只*对该容器中的bean进行后处理。换句话说，`BeanPostProcessor`即使两个容器都是同一层次结构的一部分，在一个容器中定义的bean也不会被另一个容器中定义的bean进行后处理。

要改变实际的bean定义（即*蓝图*定义豆），而不是你需要使用`BeanFactoryPostProcessor`在描述。

该`org.springframework.beans.factory.config.BeanPostProcessor`接口由两个回调方法组成。当这样的类被注册为带容器的后处理器时，对于容器创建的每个bean实例，后处理器*在*容器初始化方法*之前*从容器中获取回调（例如InitializingBean的*afterPropertiesSet（）*或任何*在*任何bean初始化回调*之后，*都会调用声明的init方法。后处理器可以对bean实例执行任何操作，包括完全忽略回调。bean后处理器通常会检查回调接口，或者可以使用代理包装bean。一些Spring AOP基础结构类实现为bean后处理器，以便提供代理包装逻辑。

的`ApplicationContext` *自动检测*，其中实施所述配置元数据中定义的任何豆`BeanPostProcessor`接口。将 `ApplicationContext`这些bean注册为后处理器，以便稍后在创建bean时调用它们。Bean后处理器可以像任何其他bean一样部署在容器中。

请注意，在配置类上声明`BeanPostProcessor`使用`@Bean`工厂方法时，工厂方法的返回类型应该是实现类本身或至少是`org.springframework.beans.factory.config.BeanPostProcessor` 接口，清楚地表明该bean的后处理器性质。否则，`ApplicationContext`在完全创建之前， 将无法按类型自动检测它。由于`BeanPostProcessor`需要尽早实例化以便应用于上下文中其他bean的初始化，因此这种早期类型检测至关重要。

而对于建议的方法`BeanPostProcessor`注册是通过 `ApplicationContext`自动检测（如上文所述），但也可以注册它们*编程*对一个`ConfigurableBeanFactory`使用 `addBeanPostProcessor`方法。当需要在注册之前评估条件逻辑，或者甚至在层次结构中跨上下文复制bean后处理器时，这非常有用。但请注意，`BeanPostProcessor`以编程方式添加的s *不尊重Ordered接口*。这是*注册*的*顺序，*它决定了执行的顺序。另请注意`BeanPostProcessor`，无论是否有任何显式排序，以编程方式注册的s始终在通过自动检测注册之前处理。

实现`BeanPostProcessor`接口的类是*特殊*的，容器会对它们进行不同的处理。*他们直接引用的*所有`BeanPostProcessor`s *和bean都会*在启动时实例化，作为特殊启动阶段的一部分 `ApplicationContext`。接下来，所有`BeanPostProcessor`s都以排序的方式注册并应用于容器中的所有其他bean。因为AOP自动代理是作为一个`BeanPostProcessor`自身实现的，所以`BeanPostProcessor`它们和它们直接引用的bean都没有资格进行自动代理，因此没有将方面编织到它们中。

对于任何此类bean，您应该看到一条信息性日志消息：“ *Bean foo不适合所有BeanPostProcessor接口处理（例如：不符合自动代理条件）* ”。

请注意，如果您将bean连接到`BeanPostProcessor`使用自动装配或 `@Resource`（可能会回退到自动装配），Spring可能会在搜索类型匹配依赖项候选项时访问意外的bean，从而使它们不符合自动代理或其他类型的bean post -处理。例如，如果您有一个依赖项注释，`@Resource`其中字段/ setter名称不直接对应于bean的声明名称，并且没有使用name属性，那么Spring将访问其他bean以按类型匹配它们。

以下示例显示如何在a中编写，注册和使用`BeanPostProcessor`s `ApplicationContext`。

#### 示例：Hello World，BeanPostProcessor-style

第一个例子说明了基本用法。该示例显示了一个自定义 `BeanPostProcessor`实现，该实现调用`toString()`容器创建的每个bean 的方法，并将生成的字符串输出到系统控制台。

在下面找到自定义`BeanPostProcessor`实现类定义：

```java
package scripting;

import org.springframework.beans.factory.config.BeanPostProcessor;

public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {

    // simply return the instantiated bean as-is
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean; // we could potentially return any object reference here...
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("Bean '" + beanName + "' created : " + bean.toString());
        return bean;
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:lang="http://www.springframework.org/schema/lang"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/lang
        https://www.springframework.org/schema/lang/spring-lang.xsd">

    <lang:groovy id="messenger"
            script-source="classpath:org/springframework/scripting/groovy/Messenger.groovy">
        <lang:property name="message" value="Fiona Apple Is Just So Dreamy."/>
    </lang:groovy>

    <!--
    when the above bean (messenger) is instantiated, this custom
    BeanPostProcessor implementation will output the fact to the system console
    -->
    <bean class="scripting.InstantiationTracingBeanPostProcessor"/>

</beans>
```

注意如何`InstantiationTracingBeanPostProcessor`简单定义。它甚至没有名称，因为它是一个bean，它可以像任何其他bean一样依赖注入。（前面的配置也定义了一个由Groovy脚本支持的bean。

以下简单Java应用程序执行上述代码和配置：

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scripting.Messenger;

public final class Boot {

    public static void main(final String[] args) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("scripting/beans.xml");
        Messenger messenger = (Messenger) ctx.getBean("messenger");
        System.out.println(messenger);
    }

}
```

上述应用程序的输出类似于以下内容：

```bash
Bean 'messenger' created : org.springframework.scripting.groovy.GroovyMessenger@272961
org.springframework.scripting.groovy.GroovyMessenger@272961
```

#### 示例：RequiredAnnotationBeanPostProcessor

将回调接口或注释与自定义`BeanPostProcessor`实现结合使用 是扩展Spring IoC容器的常用方法。一个例子是Spring `RequiredAnnotationBeanPostProcessor`- 一个 `BeanPostProcessor`随Spring发行版一起提供的实现，它确保用（任意）注释标记的bean上的JavaBean属性实际上（配置为）依赖注入值。

### 7.8.2使用BeanFactoryPostProcessor自定义配置元数据

我们将看到的下一个扩展点是 `org.springframework.beans.factory.config.BeanFactoryPostProcessor`。这个接口的语义类似于那个接口的语义，`BeanPostProcessor`主要区别在于：`BeanFactoryPostProcessor`操作*bean配置元数据* ; 也就是说，Spring IoC容器允许`BeanFactoryPostProcessor`读取配置元数据，并可能在容器实例化除s 之外的任何bean *之前*更改它`BeanFactoryPostProcessor`。

您可以配置多个`BeanFactoryPostProcessor`s，并且可以`BeanFactoryPostProcessor`通过设置`order`属性来控制这些s的执行顺序。但是，如果`BeanFactoryPostProcessor`实现 `Ordered`接口，则只能设置此属性。如果你自己编写`BeanFactoryPostProcessor`，你也应该考虑实现这个`Ordered`接口。有关更多详细信息，请参阅`BeanFactoryPostProcessor`和`Ordered`接口的javadoc 。

如果要更改实际的bean *实例*（即，从配置元数据创建的对象），则需要使用a `BeanPostProcessor` 。虽然技术上可以在一个`BeanFactoryPostProcessor`（例如，使用`BeanFactory.getBean()`）中使用 bean实例，但这样做会导致过早的bean实例化，从而违反标准的容器生命周期。这可能会导致负面影响，例如绕过bean后处理。

此外，`BeanFactoryPostProcessor`s是*每个容器的*范围。这仅在您使用容器层次结构时才有意义。如果`BeanFactoryPostProcessor`在一个容器中定义一个容器，它将*只*应用于该容器中的bean定义。`BeanFactoryPostProcessor`即使两个容器都是同一层次结构的一部分，一个容器中的Bean定义也不会被另一个容器中的s 进行后处理。

bean工厂后处理器在其内部声明时自动执行 `ApplicationContext`，以便将更改应用于定义容器的配置元数据。Spring包含许多预定义的bean工厂后处理器，例如`PropertyOverrideConfigurer`和 `PropertyPlaceholderConfigurer`。自定义`BeanFactoryPostProcessor`也可使用，例如，以注册自定义属性编辑器。

一个`ApplicationContext`自动检测部署在它实现了任何豆`BeanFactoryPostProcessor`接口。它在适当的时候使用这些bean作为bean工厂后处理器。您可以像处理任何其他bean一样部署这些后处理器bean。

与`BeanPostProcessor`s一样，您通常不希望`BeanFactoryPostProcessor`为延迟初始化配置 s。如果没有其他bean引用a`Bean(Factory)PostProcessor`，则该后处理器根本不会被实例化。因此，将其标记为延迟初始化将被忽略， `Bean(Factory)PostProcessor`会急切地实例化，即使你设定的 `default-lazy-init`属性`true`对你的声明`<beans />`元素。

#### 示例：类名替换PropertyPlaceholderConfigurer

您可以`PropertyPlaceholderConfigurer`使用标准Java `Properties`格式在单独的文件中使用bean定义中的外部化属性值。这样做可以使部署应用程序的人员自定义特定于环境的属性（如数据库URL和密码），而不会出现修改主XML定义文件或容器文件的复杂性或风险。

请考虑以下基于XML的配置元数据片段，其中`DataSource` 定义了占位符值。该示例显示了从外部`Properties`文件配置的属性。在运行时，a `PropertyPlaceholderConfigurer`将应用于将替换DataSource的某些属性的元数据。要替换的值被指定为遵循Ant / log4j / JSP EL样式的表单的*占位符*`${property-name}`。

```xml
<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
    <property name="locations" value="classpath:com/foo/jdbc.properties"/>
</bean>

<bean id="dataSource" destroy-method="close"
        class="org.apache.commons.dbcp.BasicDataSource">
    <property name="driverClassName" value="${jdbc.driverClassName}"/>
    <property name="url" value="${jdbc.url}"/>
    <property name="username" value="${jdbc.username}"/>
    <property name="password" value="${jdbc.password}"/>
</bean>
```

实际值来自标准Java `Properties`格式的另一个文件：

```bash
jdbc.driverClassName=org.hsqldb.jdbcDriver
jdbc.url=jdbc:hsqldb:hsql://production:9002
jdbc.username=sa
jdbc.password=root
```

因此，`${jdbc.username}`在运行时将字符串替换为值“sa”，这同样适用于与属性文件中的键匹配的其他占位符值。在`PropertyPlaceholderConfigurer`为大多数属性和bean定义的属性占位符检查。此外，可以自定义占位符前缀和后缀。

使用`context`Spring 2.5中引入的命名空间，可以使用专用配置元素配置属性占位符。可以在`location`属性中提供一个或多个位置作为逗号分隔列表。

```xml
<context:property-placeholder location="classpath:com/foo/jdbc.properties"/>
```

在`PropertyPlaceholderConfigurer`不仅将查找在属性`Properties` 指定的文件。默认情况下，`System`如果它无法在指定的属性文件中找到属性，它还会检查Java 属性。您可以通过`systemPropertiesMode`使用以下三个受支持的整数值之一设置configurer 的属性来自定义此行为：

- *never*（0）：从不检查系统属性
- *fallback*（1）：如果在指定的属性文件中无法解析，则检查系统属性。这是默认值。
- *override*（2）：在尝试指定的属性文件之前，首先检查系统属性。这允许系统属性覆盖任何其他属性源。

有关`PropertyPlaceholderConfigurer`更多信息，请参阅javadocs。

在`PropertyPlaceholderConfigurer`不仅将查找在属性`Properties` 指定的文件。默认情况下，`System`如果它无法在指定的属性文件中找到属性，它还会检查Java 属性。您可以通过`systemPropertiesMode`使用以下三个受支持的整数值之一设置configurer 的属性来自定义此行为：

- *never*（0）：从不检查系统属性
- *fallback*（1）：如果在指定的属性文件中无法解析，则检查系统属性。这是默认值。
- *override*（2）：在尝试指定的属性文件之前，首先检查系统属性。这允许系统属性覆盖任何其他属性源。

有关`PropertyPlaceholderConfigurer`更多信息，请参阅javadocs。

|                                                              |
| ------------------------------------------------------------ |
| 您可以使用`PropertyPlaceholderConfigurer`替换类名称，这在您必须在运行时选择特定实现类时有时很有用。例如： |

```xml
<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
    <property name="locations">
        <value>classpath:com/foo/strategy.properties</value>
    </property>
    <property name="properties">
        <value>custom.strategy.class=com.foo.DefaultStrategy</value>
    </property>
</bean>

<bean id="serviceStrategy" class="${custom.strategy.class}"/>
```

如果类不能在运行时被解析为一个有效的类，bean的分辨率，当它即将被创造，这是在失败`preInstantiateSingletons()` 的阶段`ApplicationContext`对非延迟实例化的bean。

#### 示例：PropertyOverrideConfigurer

在`PropertyOverrideConfigurer`另一个bean工厂后置处理器，类似 `PropertyPlaceholderConfigurer`，但不同的是后者，原来的定义可以有缺省值或者根本没有值的bean属性。如果覆盖 `Properties`文件没有某个bean属性的条目，则使用默认上下文定义。

请注意，bean定义*不*知道被覆盖，因此从XML定义文件中可以立即看出正在使用覆盖配置器。如果多个`PropertyOverrideConfigurer`实例为同一个bean属性定义了不同的值，则由于覆盖机制，最后一个实例会获胜。

属性文件配置行采用以下格式：

```bash
beanName.property=value
```

For example:

```bash
dataSource.driverClassName=com.mysql.jdbc.Driver
dataSource.url=jdbc:mysql:mydb
```

此示例文件可以与包含名为*dataSource*的bean的容器定义一起使用，该bean 具有*driver*和*url*属性。

也支持复合属性名称，只要路径的每个组件（重写的最终属性除外）都已经非空（可能由构造函数初始化）。在这个例子中

```bash
foo.fred.bob.sammy=123
```

在`sammy`该财产`bob`的财产`fred`的财产`foo`bean被设置为标量值`123`。

指定的覆盖值始终是*字面值* ; 它们不会被翻译成bean引用。当XML bean定义中的原始值指定bean引用时，此约定也适用。

使用`context`Spring 2.5中引入的命名空间，可以使用专用配置元素配置属性覆盖：

```xml
<context:property-override location="classpath:override.properties"/>
```

### 7.8.3使用FactoryBean自定义实例化逻辑

`org.springframework.beans.factory.FactoryBean`为*自己工厂的*对象实现接口 。

该`FactoryBean`接口是Spring IoC容器实例化逻辑的可插拔点。如果你有一个复杂的初始化代码，用Java表示，而不是（可能）冗长的XML，你可以创建自己的`FactoryBean`，在该类中编写复杂的初始化，然后将自定义`FactoryBean`插入容器。

该`FactoryBean`接口提供了三种方法：

- `Object getObject()`：返回此工厂创建的对象的实例。可以共享实例，具体取决于此工厂是返回单例还是原型。
- `boolean isSingleton()`：`true`如果`FactoryBean`返回单例，`false`则返回，否则返回 。
- `Class getObjectType()`：返回`getObject()`方法返回的对象类型，或者`null`如果事先不知道类型。

该`FactoryBean`概念和接口被一些Spring框架内的地方使用; 超过50个`FactoryBean`接口的实现与Spring本身一起提供。

当你需要向一个容器询问一个实际的`FactoryBean`实例而不是它生成的bean `&`时，在调用the的`getBean()`方法时，用strersand符号（）来表示bean的id `ApplicationContext`。因此对于`FactoryBean` 具有id的给定`myBean`，`getBean("myBean")`在容器上调用返回的产品`FactoryBean`; 而，调用`getBean("&myBean")`返回 `FactoryBean`实例本身。
