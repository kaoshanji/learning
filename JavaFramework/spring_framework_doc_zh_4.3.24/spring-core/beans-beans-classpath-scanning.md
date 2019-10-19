# 7.10 类扫描和组件管理

本章中的大多数示例都使用XML来指定`BeanDefinition`在Spring容器中生成每个元素的配置元数据。上一节（第7.9节“基于注解的容器配置”）演示了如何通过源级注解提供大量配置元数据。但是，即使在这些示例中，“基本”bean定义也在XML文件中明确定义，而注解仅驱动依赖项注入。本节描述了隐式检测*候选组件*的选项 通过扫描类路径。候选组件是与筛选条件匹配的类，并且具有向容器注册的相应bean定义。这消除了使用XML执行bean注册的需要; 相反，您可以使用注解（例如`@Component`），AspectJ类型表达式或您自己的自定义筛选条件来选择哪些类将使用容器注册bean定义。

从Spring 3.0开始，Spring JavaConfig项目提供的许多功能都是核心Spring Framework的一部分。这允许您使用Java而不是使用传统的XML文件来定义bean。看看的`@Configuration`，`@Bean`， `@Import`，和`@DependsOn`注解有关如何使用这些新功能的例子。

### 7.10.1 @Component和进一步的构造型注解

`@Repository`注解是用于满足所述角色或任何类的标记 *构造型*的存储库（也被称为数据访问对象或DAO）的。该标记的用途包括自动翻译异常，如 第20.2.2节“异常翻译”中所述。

Spring提供进一步典型化注解：`@Component`，`@Service`，和 `@Controller`。`@Component`是任何Spring管理组件的通用构造型。 `@Repository`，`@Service`和，并且`@Controller`是`@Component`更具体的用例的特化，例如，分别在持久性，服务和表示层中。因此，你可以用你的注解组件类 `@Component`，但如果用注解它们`@Repository`，`@Service`或者`@Controller` ，你的类能更好地被工具处理，或与切面进行关联。例如，这些刻板印象注解成为切入点的理想目标。这也有可能是`@Repository`，`@Service`和`@Controller`可能会在Spring Framework的未来版本中携带其他语义。因此，如果您在使用`@Component`或`@Service`服务层之间进行选择，`@Service`显然是更好的选择。同样，如上所述，`@Repository`已经支持作为持久层中自动异常转换的标记。

### 7.10.2元注解

Spring提供的许多注解都可以在您自己的代码中用作*元注解*。元注解只是一个可以应用于另一个注解的注解。例如，`@Service`上面提到的注解是元注解的`@Component`：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component // Spring will see this and treat @Service in the same way as @Component
public @interface Service {

    // ....
}
```

元注解也可以组合以创建*组合注解*。例如，`@RestController`从Spring MVC的注解*组成*的`@Controller`和 `@ResponseBody`。

此外，组合注解可以选择性地从元注解重新声明属性以允许用户定制。当您只想公开元注解属性的子集时，这可能特别有用。例如，Spring的 `@SessionScope`注解将范围名称硬编码为`session`但仍允许自定义`proxyMode`。

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(WebApplicationContext.SCOPE_SESSION)
public @interface SessionScope {

    /**
     * Alias for {@link Scope#proxyMode}.
     * <p>Defaults to {@link ScopedProxyMode#TARGET_CLASS}.
     */
    @AliasFor(annotation = Scope.class)
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;

}
```

`@SessionScope`然后可以使用而不声明`proxyMode`如下：

```java
@Service
@SessionScope
public class SessionScopedService {
    // ...
}
```

或者具有以下重写值`proxyMode`：

```java
@Service
@SessionScope(proxyMode = ScopedProxyMode.INTERFACES)
public class SessionScopedUserService implements UserService {
    // ...
}
```

### 7.10.3自动检测类并注册bean定义

Spring可以自动检测定型类并注册相应的 `BeanDefinition`s `ApplicationContext`。例如，以下两个类符合此类自动检测的条件：

```java
@Service
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Autowired
    public SimpleMovieLister(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
}
```

```java
@Repository
public class JpaMovieFinder implements MovieFinder {
    // implementation elided for clarity
}
```

要自动检测这些类并注册相应的bean，您需要添加 `@ComponentScan`到您的`@Configuration`类，其中该`basePackages`属性是两个类的公共父包。（或者，您可以指定包含每个类的父包的逗号/分号/空格分隔列表。）

```java
@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig  {
    ...
}
```

以下是使用XML的替代方法

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="org.example"/>

</beans>
```

使用`<context:component-scan>`隐式启用功能 `<context:annotation-config>`。`<context:annotation-config>`使用时通常不需要包含 元素`<context:component-scan>`

此外，当您使用component-scan元素时，隐式包含`AutowiredAnnotationBeanPostProcessor`和 `CommonAnnotationBeanPostProcessor`。这意味着这两个组件是自动检测*并* 连接在一起的 - 所有这些都没有在XML中提供任何bean配置元数据。

可以禁用的登记`AutowiredAnnotationBeanPostProcessor`和 `CommonAnnotationBeanPostProcessor`通过包括*注解的配置*具有值属性`false`

### 7.10.4使用过滤器自定义扫描

默认情况下，类注有`@Component`，`@Repository`，`@Service`， `@Controller`，或者本身都标注有一个自定义的注解`@Component`是唯一检测到的候选组件。但是，您可以通过应用自定义筛选器来修改和扩展此行为。将它们添加为注解的*includeFilters*或*excludeFilters* 参数`@ComponentScan`（或作为 元素的*include-filter*或*exclude-filter*子`component-scan`元素）。每个过滤器元素都需要`type` 和`expression`属性。下表介绍了筛选选项。

**表7.5。过滤类型**

| 过滤器类型         | 示例表达                     | 描述                                                         |
| ------------------ | ---------------------------- | ------------------------------------------------------------ |
| annotation（默认） | `org.example.SomeAnnotation` | 要在目标组件中的类型级别出现的注解。                         |
| assignable         | `org.example.SomeClass`      | 目标组件可分配给（扩展/实现）的类（或接口）。                |
| aspectj            | `org.example..*Service+`     | 要由目标组件匹配的AspectJ类型表达式。                        |
| regex              | `org\.example\.Default.*`    | 要由目标组件类名匹配的正则表达式。                           |
| custom             | `org.example.MyTypeFilter`   | `org.springframework.core.type .TypeFilter`接口的自定义实现。 |

以下示例显示忽略所有`@Repository`注解并使用“存根”存储库的配置。

```java
@Configuration
@ComponentScan(basePackages = "org.example",
        includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
        excludeFilters = @Filter(Repository.class))
public class AppConfig {
    ...
}
```

以及使用XML的等价物

```xml
<beans>
    <context:component-scan base-package="org.example">
        <context:include-filter type="regex"
                expression=".*Stub.*Repository"/>
        <context:exclude-filter type="annotation"
                expression="org.springframework.stereotype.Repository"/>
    </context:component-scan>
</beans>
```

您还可以通过设置`useDefaultFilters=false`注解或提供元素`use-default-filters="false"`属性来禁用默认过滤器`<component-scan/>`。这将在关闭对使用注解的类自动检测`@Component`，`@Repository`， `@Service`，`@Controller`，或`@Configuration`。

### 7.10.5在组件中定义bean元数据

Spring组件还可以向容器提供bean定义元数据。您可以使用与`@Bean`用于在带`@Configuration` 注解的类中定义bean元数据的相同注解来执行此操作。这是一个简单的例子：

```java
@Component
public class FactoryMethodComponent {

    @Bean
    @Qualifier("public")
    public TestBean publicInstance() {
        return new TestBean("publicInstance");
    }

    public void doWork() {
        // Component method implementation omitted
    }
}
```

此类是一个Spring组件，其`doWork()`方法中包含特定于应用程序的代码 。但是，它还提供了一个bean定义，该定义具有引用该方法的工厂方法`publicInstance()`。该`@Bean`注解标识工厂方法和其它bean定义特性，如通过一个限定值`@Qualifier`注解。可以指定其他方法级别的注解是 `@Scope`，`@Lazy`和自定义限定器注解。

除了它对组件初始化的作用外，`@Lazy`注解还可以放在标有`@Autowired`或的注入点上`@Inject`。在这种情况下，它会导致注入惰性解析代理。

如前所述，支持自动装配的字段和方法，并支持自动装配`@Bean`方法：

```java
@Component
public class FactoryMethodComponent {

    private static int i;

    @Bean
    @Qualifier("public")
    public TestBean publicInstance() {
        return new TestBean("publicInstance");
    }

    // use of a custom qualifier and autowiring of method parameters
    @Bean
    protected TestBean protectedInstance(
            @Qualifier("public") TestBean spouse,
            @Value("#{privateInstance.age}") String country) {
        TestBean tb = new TestBean("protectedInstance", 1);
        tb.setSpouse(spouse);
        tb.setCountry(country);
        return tb;
    }

    @Bean
    private TestBean privateInstance() {
        return new TestBean("privateInstance", i++);
    }

    @Bean
    @RequestScope
    public TestBean requestScopedInstance() {
        return new TestBean("requestScopedInstance", 3);
    }
}
```

该示例将`String`method参数自动装配`country`到`age` 另一个名为的bean 的属性值`privateInstance`。Spring Expression Language元素通过符号定义属性的值`#{ <expression> }`。对于`@Value` 注解，表达式解析器预先配置为在解析表达式文本时查找bean名称。

从Spring Framework 4.3开始，您还可以声明类型的工厂方法参数 `InjectionPoint`（或其更具体的子类`DependencyDescriptor`），以便访问触发创建当前bean的请求注入点。请注意，这仅适用于实例创建bean实例，而不适用于注入现有实例。因此，此功能对原型范围的bean最有意义。对于其他作用域，工厂方法只会看到触发在给定作用域中创建新bean实例的注入点：例如，触发创建惰性单例bean的依赖项。在这种情况下，使用提供的注入点元数据和语义关注。

```java
@Component
public class FactoryMethodComponent {

    @Bean @Scope("prototype")
    public TestBean prototypeInstance(InjectionPoint injectionPoint) {
        return new TestBean("prototypeInstance for " + injectionPoint.getMember());
    }
}
```

将`@Bean`在普通的Spring组件方法比春天里的同行处理方式不同`@Configuration`类。不同之处在于`@Component` ，CGLIB不会增强类来拦截方法和字段的调用。CGLIB代理是一种方法，通过它可以调用类中`@Bean`方法中的方法或字段来`@Configuration`创建对协作对象的bean元数据引用; 这些方法*不是*用普通的Java语义调用的，而是通过容器来提供通常的生命周期管理和Spring bean的代理，即使在通过对`@Bean`方法的编程调用引用其他bean时也是如此。相反，`@Bean`在普通`@Component` 类中的方法中调用方法或字段*具有* 标准Java语义，没有特殊的CGLIB处理或其他约束应用。

您可以将`@Bean`方法声明为`static`，允许在不创建包含配置类作为实例的情况下调用它们。这在定义后处理器bean时特别有意义，例如类型`BeanFactoryPostProcessor`或 `BeanPostProcessor`，因为这样的bean将在容器生命周期的早期初始化，并且应避免在此时触发配置的其他部分。

请注意，对静态`@Bean`方法的调用永远不会被容器拦截，甚至在`@Configuration`类中也不会被拦截（参见上文）。这是由于技术限制：CGLIB子类化只能覆盖非静态方法。因此，直接调用另一个`@Bean`方法将具有标准的Java语义，从而导致直接从工厂方法本身返回一个独立的实例。

方法的Java语言可见性`@Bean`不会立即影响Spring容器中的结果bean定义。您可以自由地声明您认为适合非`@Configuration`类的工厂方法以及任何地方的静态方法。但是，类中的常规`@Bean`方法`@Configuration`需要是可覆盖的，即它们不能声明为`private`或`final`。

`@Bean`还将在给定组件或配置类的基类上以及在由组件或配置类实现的接口中声明的Java 8缺省方法上发现方法。这使得在编写复杂的配置安排时具有很大的灵活性，从Spring 4.2开始，甚至可以通过Java 8默认方法实现多重继承。

最后，请注意，单个类可以`@Bean`为同一个bean 保存多个方法，作为在运行时根据可用依赖项使用的多个工厂方法的排列。这与在其他配置方案中选择“最贪婪”构造函数或工厂方法的算法相同：将在构造时选择具有最多可满足依赖项的变体，类似于容器在多个`@Autowired`构造函数之间进行选择的方式。

### 7.10.6命名自动检测的组件

当组件作为扫描过程的一部分自动检测时，其bean名称由该扫描程序`BeanNameGenerator`已知的策略生成。默认情况下，任何Spring刻板印象注解（`@Component`，`@Repository`，`@Service`，和 `@Controller`包含）*的名字* `value`将因此提供的名字相应的bean定义。

如果此类注解不包含任何*名称* `value`或任何其他检测到的组件（例如自定义过滤器发现的那些组件），则默认的bean名称生成器将返回未大写的非限定类名称。例如，如果检测到以下组件类，则名称将为：`myMovieLister`和`movieFinderImpl`：

```java
@Service("myMovieLister")
public class SimpleMovieLister {
    // ...
}
```

```java
@Repository
public class MovieFinderImpl implements MovieFinder {
    // ...
}
```

如果您不想依赖默认的bean命名策略，则可以提供自定义bean命名策略。首先，实现 [`BeanNameGenerator`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/beans/factory/support/BeanNameGenerator.html) 接口，并确保包含默认的无参数构造函数。然后，在配置扫描程序时提供完全限定的类名：

```java
@Configuration
@ComponentScan(basePackages = "org.example", nameGenerator = MyNameGenerator.class)
public class AppConfig {
    ...
}
```

```java
<beans>
    <context:component-scan base-package="org.example"
        name-generator="org.example.MyNameGenerator" />
</beans>
```

作为一般规则，考虑在其他组件可能对其进行显式引用时使用注解指定名称。另一方面，只要容器负责接线，自动生成的名称就足够了。

### 7.10.7为自动检测的组件提供范围

与Spring管理的组件一样，自动检测组件的默认和最常见的范围是`singleton`。但是，有时您需要一个可以通过`@Scope`注解指定的不同范围。只需在注解中提供范围的名称：

```java
@Scope("prototype")
@Repository
public class MovieFinderImpl implements MovieFinder {
    // ...
}
```

`@Scope`注解仅在具体bean类（对于带注解的组件）或工厂方法（对于`@Bean`方法）上进行了内省。与XML bean定义相比，没有bean定义继承的概念，类级别的继承层次结构与元数据目的无关。

有关特定于Web的范围（如Spring上下文中的“request”/“session”）的详细信息，请参见第7.5.4节“请求，会话，全局会话，应用程序和WebSocket范围”。与这些范围的预构建注解一样，您也可以使用Spring的元注解方法编写自己的范围注解：例如，使用元注解的自定义注解`@Scope("prototype")`，可能还会声明自定义范围代理模式。

要为范围解析提供自定义策略而不是依赖基于注解的方法，请实现 [`ScopeMetadataResolver`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/annotation/ScopeMetadataResolver.html) 接口，并确保包含默认的无参数构造函数。然后，在配置扫描程序时提供完全限定的类名：

```java
@Configuration
@ComponentScan(basePackages = "org.example", scopeResolver = MyScopeResolver.class)
public class AppConfig {
    ...
}
```

```xml
<beans>
    <context:component-scan base-package="org.example" scope-resolver="org.example.MyScopeResolver"/>
</beans>
```

使用某些非单例作用域时，可能需要为作用域对象生成代理。推理在“Scoped beans as dependencies”一节中描述。为此，component-scan元素上提供了*scoped-proxy*属性。三个可能的值是：no，interfaces和targetClass。例如，以下配置将生成标准JDK动态代理：

```java
@Configuration
@ComponentScan(basePackages = "org.example", scopedProxy = ScopedProxyMode.INTERFACES)
public class AppConfig {
    ...
}
```

```xml
<beans>
    <context:component-scan base-package="org.example" scoped-proxy="interfaces"/>
</beans>
```

### 7.10.8提供带注解的限定符元数据

在`@Qualifier`注解中讨论[第7.9.4，“微调基于注解的自动连接带有各种限制条件”](beans.html#beans-autowired-annotation-qualifiers)。该部分中的示例演示了`@Qualifier`在解析自动线候选时使用注解和自定义限定符注解来提供细粒度控制。因为这些示例基于XML bean定义，所以使用XML中 元素的元素`qualifier`或`meta`子元素在候选bean定义上提供限定符元数据`bean`。当依赖类路径扫描来自动检测组件时，您可以在候选类上为限定符元数据提供类型级注解。以下三个示例演示了此技术：

```Java
@Component
@Qualifier("Action")
public class ActionMovieCatalog implements MovieCatalog {
    // ...
}
```

```Java
@Component
@Genre("Action")
public class ActionMovieCatalog implements MovieCatalog {
    // ...
}
```

```java
@Component
@Offline
public class CachingMovieCatalog implements MovieCatalog {
    // ...
}
```

与大多数基于注解的备选方案一样，请记住注解元数据绑定到类定义本身，而XML的使用允许多个*相同类型的* bean 在其限定符元数据中提供变体，因为每个元数据都是按照 - 实例而不是每班。

