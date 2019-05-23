# 7.13 环境抽象

## 7.13环境抽象

它[`Environment`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/core/env/Environment.html) 是集成在容器中的抽象，它模拟了应用程序环境的两个关键方面：*配置文件* 和*属性*。

一个*轮廓*是bean定义一个命名的逻辑组，只有当指定的配置文件是活动的容器进行登记。可以将Bean分配给配置文件，无论是以XML还是通过注释定义。`Environment`与配置文件相关的对象的作用是确定哪些配置文件（如果有）当前处于活动状态，以及默认情况下哪些配置文件（如果有）应处于活动状态。

属性在几乎所有应用程序中都发挥着重要作用，可能源自各种源：属性文件，JVM系统属性，系统环境变量，JNDI，servlet上下文参数，ad-hoc属性对象，映射等。`Environment`与属性相关的对象的作用是为用户提供方便的服务接口，用于配置属性源并从中解析属性。

### 7.13.1 Bean定义配置文件

Bean定义配置文件是核心容器中的一种机制，允许在不同环境中注册不同的bean。单词*环境* 对不同的用户来说意味着不同的东西，这个功能可以帮助许多用例，包括：

- 在开发中使用内存中的数据源，在QA或生产环境中查找来自JNDI的相同数据源
- 仅在将应用程序部署到性能环境时注册监视基础结构
- 为客户A和客户B部署注册bean的自定义实现

让我们考虑一个需要a的实际应用中的第一个用例 `DataSource`。在测试环境中，配置可能如下所示：

```java
@Bean
public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.HSQL)
        .addScript("my-schema.sql")
        .addScript("my-test-data.sql")
        .build();
}
```

现在让我们考虑如何将此应用程序部署到QA或生产环境中，假设应用程序的数据源将在生产应用程序服务器的JNDI目录中注册。我们的`dataSource`bean现在看起来像这样：

```Java
@Bean(destroyMethod="")
public DataSource dataSource() throws Exception {
    Context ctx = new InitialContext();
    return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
}
```

问题是如何根据当前环境在使用这两种变体之间切换。随着时间的推移，Spring用户已经设计了许多方法来完成这项工作，通常依赖于系统环境变量和`<import/>`包含`${placeholder}`令牌的XML 语句的组合，这些令牌根据环境变量的值解析为正确的配置文件路径。Bean定义配置文件是核心容器功能，可为此问题提供解决方案。

如果我们概括上面特定于环境的bean定义的示例用例，我们最终需要在某些上下文中注册某些bean定义，而不是在其他上下文中。你可以说你想在情况A中注册一个bean定义的特定配置文件，在情况B中注册一个不同的配置文件。让我们首先看看如何更新我们的配置以反映这种需求。

#### @Profile

该[`@Profile`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/annotation/Profile.html) 注释允许你表明组件有资格登记时的一个或多个指定的简档是活动的。使用上面的示例，我们可以`dataSource`按如下方式重写配置：

```java
@Configuration
@Profile("development")
public class StandaloneDataConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .addScript("classpath:com/bank/config/sql/test-data.sql")
            .build();
    }
}
```

```java
@Configuration
@Profile("production")
public class JndiDataConfig {

    @Bean(destroyMethod="")
    public DataSource dataSource() throws Exception {
        Context ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
    }
}
```

如前所述，使用`@Bean`方法，您通常会选择使用编程JNDI查找：使用Spring的`JndiTemplate`/ `JndiLocatorDelegate`helper或`InitialContext`上面显示的直接JNDI 用法，但不会`JndiObjectFactoryBean` 强制您将返回类型声明为`FactoryBean`类型。

`@Profile`可以用作[元注释](beans.html#beans-meta-annotations)，以创建自定义*组合注释*。以下示例定义了一个自定义`@Production`注释，可用作以下内容的 替代品 `@Profile("production")`：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Profile("production")
public @interface Production {
}
```

如果`@Configuration`标记了类，则除非一个或多个指定的配置文件处于活动状态，否则将绕过与该类关联的`@Profile`所有`@Bean`方法和 `@Import`注释。如果a `@Component`或`@Configuration`class被标记`@Profile({"p1", "p2"})`，则除非已激活配置文件'p1'和/或'p2'，否则不会注册/处理该类。如果给定的配置文件以NOT运算符（`!`）作为前缀，则如果配置文件**未** 处于活动状态，则将注册带注释的元素。例如，`@Profile({"p1", "!p2"})`如果配置文件“p1”处于活动状态或配置文件“p2”未激活，则会发生注册。

`@Profile` 也可以在方法级别声明只包含配置类的一个特定bean，例如，对于特定bean的替代变体：

```java
@Configuration
public class AppConfig {

    @Bean("dataSource")
    @Profile("development")
    public DataSource standaloneDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .addScript("classpath:com/bank/config/sql/test-data.sql")
            .build();
    }

    @Bean("dataSource")
    @Profile("production")
    public DataSource jndiDataSource() throws Exception {
        Context ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
    }
}
```

使用`@Profile`on `@Bean`方法，可能会应用特殊方案：对于`@Bean`相同Java方法名称的重载方法（类似于构造函数重载），`@Profile`需要在所有重载方法上一致地声明条件。如果条件不一致，则只有重载方法中第一个声明的条件才重要。`@Profile`因此，不能用于选择具有特定参数签名的重载方法而不是另一个; 同一个bean的所有工厂方法之间的分辨率遵循Spring的构造函数解析算法在创建时。

如果要定义具有不同配置文件条件的备用bean，请使用通过`@Bean`name属性指向同一bean名称的不同Java方法名称，如上例所示。如果参数签名都是相同的（例如，所有变体都具有no-arg工厂方法），这是首先在有效Java类中表示这种排列的唯一方法（因为只有一种方法可以特定的名称和参数签名）。

#### XML bean定义配置文件

XML对应物是元素的`profile`属性`<beans>`。我们上面的示例配置可以在两个XML文件中重写，如下所示：

```xml
<beans profile="development"
    xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:jdbc="http://www.springframework.org/schema/jdbc"
    xsi:schemaLocation="...">

    <jdbc:embedded-database id="dataSource">
        <jdbc:script location="classpath:com/bank/config/sql/schema.sql"/>
        <jdbc:script location="classpath:com/bank/config/sql/test-data.sql"/>
    </jdbc:embedded-database>
</beans>
```

```xml
<beans profile="production"
    xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:jee="http://www.springframework.org/schema/jee"
    xsi:schemaLocation="...">

    <jee:jndi-lookup id="dataSource" jndi-name="java:comp/env/jdbc/datasource"/>
</beans>
```

也可以避免`<beans/>`在同一文件中使用split和nest 元素：

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:jdbc="http://www.springframework.org/schema/jdbc"
    xmlns:jee="http://www.springframework.org/schema/jee"
    xsi:schemaLocation="...">

    <!-- other bean definitions -->

    <beans profile="development">
        <jdbc:embedded-database id="dataSource">
            <jdbc:script location="classpath:com/bank/config/sql/schema.sql"/>
            <jdbc:script location="classpath:com/bank/config/sql/test-data.sql"/>
        </jdbc:embedded-database>
    </beans>

    <beans profile="production">
        <jee:jndi-lookup id="dataSource" jndi-name="java:comp/env/jdbc/datasource"/>
    </beans>
</beans>
```

在`spring-bean.xsd`受到了制约，使这些元素只能作为文件中的最后一个人。这应该有助于提供灵活性，而不会在XML文件中引起混乱。

#### 激活profile

现在我们已经更新了配置，我们仍然需要指示Spring哪个配置文件处于活动状态。如果我们现在开始我们的示例应用程序，我们会看到`NoSuchBeanDefinitionException`抛出，因为容器找不到名为的Spring bean `dataSource`。

激活配置文件可以通过多种方式完成，但最直接的方法是通过以下方式对`Environment`API进行 编程`ApplicationContext`：

```java
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
ctx.getEnvironment().setActiveProfiles("development");
ctx.register(SomeConfig.class, StandaloneDataConfig.class, JndiDataConfig.class);
ctx.refresh();
```

此外，还可以通过`spring.profiles.active`属性以声明方式激活配置文件，该 属性可以通过系统环境变量，JVM系统属性，servlet上下文参数`web.xml`或甚至作为JNDI中的条目来指定

在集成测试中，可以通过模块中的`@ActiveProfiles`注释声明活动配置文件`spring-test`

请注意，配置文件不是“任何 - 或”命题; 可以一次激活多个配置文件。以编程方式，只需为`setActiveProfiles()`方法提供多个配置文件名称，该 方法接受`String…`varargs：

```java
ctx.getEnvironment().setActiveProfiles("profile1", "profile2");
```

声明性地，`spring.profiles.active`可以接受以逗号分隔的配置文件名称列表：

```bash
-Dspring.profiles.active="profile1,profile2"
```

#### 默认配置文件

在*默认的*配置文件表示默认启用的配置文件。考虑以下：

```java
@Configuration
@Profile("default")
public class DefaultDataConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .build();
    }
}
```

如果没有激活配置文件，`dataSource`将创建上述配置文件; 这可以看作是为一个或多个bean 提供*默认*定义的一种方法。如果启用了任何配置文件，则*默认*配置文件将不适用。

默认的配置文件的名称可以使用改变`setDefaultProfiles()`的`Environment`或声明使用的`spring.profiles.default`属性。

### 7.13.2 PropertySource抽象

Spring的`Environment`抽象提供了对可配置的属性源层次结构的搜索操作。要完整解释，请考虑以下事项：

```Java
ApplicationContext ctx = new GenericApplicationContext();
Environment env = ctx.getEnvironment();
boolean containsFoo = env.containsProperty("foo");
System.out.println("Does my environment contain the 'foo' property? " + containsFoo);
```

在上面的代码片段中，我们看到了一种向Spring询问是否`foo`为当前环境定义属性的高级方法。要回答这个问题，`Environment`对象会执行一组搜索[`PropertySource`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/core/env/PropertySource.html) 。A `PropertySource`是对任何键值对源的简单抽象，Spring [`StandardEnvironment`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/core/env/StandardEnvironment.html) 配置有两个PropertySource对象 - 一个表示JVM系统属性集（*a la* `System.getProperties()`），另一个表示系统环境变量集（*a la* `System.getenv()`）。

这些默认属性源`StandardEnvironment`适用于独立应用程序。[`StandardServletEnvironment`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/web/context/support/StandardServletEnvironment.html) 填充了其他默认属性源，包括servlet配置和servlet上下文参数。[`StandardPortletEnvironment`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/web/portlet/context/StandardPortletEnvironment.html) 同样可以访问portlet配置和portlet上下文参数作为属性源。两者都可以选择启用a [`JndiPropertySource`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/jndi/JndiPropertySource.html)。

具体来说，在使用时`StandardEnvironment`，`env.containsProperty("foo")` 如果在运行时存在`foo`系统属性或`foo`环境变量，则调用将返回true 。

具体来说，在使用时`StandardEnvironment`，`env.containsProperty("foo")` 如果在运行时存在`foo`系统属性或`foo`环境变量，则调用将返回true 。

|                                                              |
| ------------------------------------------------------------ |
| 执行的搜索是分层的。默认情况下，系统属性优先于环境变量，因此如果`foo`在调用期间恰好在两个位置都设置了属性`env.getProperty("foo")`，则系统属性值将“获胜”并优先于环境变量返回。请注意，属性值不会被合并，而是被前面的条目完全覆盖。对于公共`StandardServletEnvironment`层次结构，完整层次结构如下所示，顶部的最高优先级条目：ServletConfig参数（如果适用，例如在`DispatcherServlet`上下文的情况下）ServletContext参数（web.xml context-param条目）JNDI环境变量（“java：comp / env /”条目）JVM系统属性（“-D”命令行参数）JVM系统环境（操作系统环境变量） |

具体来说，在使用时`StandardEnvironment`，`env.containsProperty("foo")` 如果在运行时存在`foo`系统属性或`foo`环境变量，则调用将返回true 。

| ![[小费]](images/tip.png)                                    |
| ------------------------------------------------------------ |
| 执行的搜索是分层的。默认情况下，系统属性优先于环境变量，因此如果`foo`在调用期间恰好在两个位置都设置了属性`env.getProperty("foo")`，则系统属性值将“获胜”并优先于环境变量返回。请注意，属性值不会被合并，而是被前面的条目完全覆盖。对于公共`StandardServletEnvironment`层次结构，完整层次结构如下所示，顶部的最高优先级条目：ServletConfig参数（如果适用，例如在`DispatcherServlet`上下文的情况下）ServletContext参数（web.xml context-param条目）JNDI环境变量（“java：comp / env /”条目）JVM系统属性（“-D”命令行参数）JVM系统环境（操作系统环境变量） |

最重要的是，整个机制是可配置的。也许您有自定义的属性源，您希望将其集成到此搜索中。没问题 - 只需实现并实例化您自己的`PropertySource`并将其添加到`PropertySources`当前的集合中`Environment`：

```java
ConfigurableApplicationContext ctx = new GenericApplicationContext();
MutablePropertySources sources = ctx.getEnvironment().getPropertySources();
sources.addFirst(new MyPropertySource());
```

在上面的代码中，`MyPropertySource`在搜索中添加了最高优先级。如果它包含一个 `foo`属性，它将被检测并`foo`在任何其他属性之前返回`PropertySource`。所述[`MutablePropertySources`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/core/env/MutablePropertySources.html) API公开了大量的，其允许该组的属性源的精确操作方法。

### 7.13.3 @PropertySource

所述[`@PropertySource`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/annotation/PropertySource.html) 注释提供了一种方便和声明机构，用于将`PropertySource` 到Spring的`Environment`。

给定包含键/值对的文件“app.properties” `testbean.name=myTestBean`，以下`@Configuration`类`@PropertySource`以这样的方式使用，即调用`testBean.getName()`将返回“myTestBean”。

```java
@Configuration
@PropertySource("classpath:/com/myco/app.properties")
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        testBean.setName(env.getProperty("testbean.name"));
        return testBean;
    }
}
```

资源位置中`${…}`存在的任何占位符`@PropertySource`将根据已针对环境注册的属性源集合进行解析。例如：

```java
@Configuration
@PropertySource("classpath:/com/${my.placeholder:default/path}/app.properties")
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        testBean.setName(env.getProperty("testbean.name"));
        return testBean;
    }
}
```

假设“my.placeholder”存在于已注册的其中一个属性源中，例如系统属性或环境变量，则占位符将被解析为相应的值。如果没有，则“default / path”将用作默认值。如果未指定默认值且无法解析属性，`IllegalArgumentException`则将抛出an 。

`@PropertySource`根据Java 8约定，注释是可重复的。但是，所有这些`@PropertySource`注释都需要在同一级别声明：直接在配置类上或在同一自定义注释中的元注释。不建议混合直接注释和元注释，因为直接注释将有效地覆盖元注释。

### 7.13.4占位符决议在陈述中

从历史上看，元素中占位符的值只能针对JVM系统属性或环境变量进行解析。情况不再如此。因为环境抽象集成在整个容器中，所以很容易通过它来解决占位符的分辨率。这意味着您可以以任何您喜欢的方式配置解析过程：更改搜索系统属性和环境变量的优先级，或者完全删除它们; 根据需要将您自己的属性源添加到混合中。

具体而言，以下语句无论`customer` 属性的定义位置如何都可以，只要它在以下位置可用`Environment`：

```xml
<beans>
    <import resource="com/bank/service/${customer}-config.xml"/>
</beans>
```

