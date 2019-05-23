# 7.9 注解容器配置

**注释是否比配置Spring的XML更好？**

基于注释的配置的引入引发了这种方法是否比XML更“好”的问题。简短的答案*取决于它*。答案很长，每种方法都有其优点和缺点，通常由开发人员决定哪种策略更适合他们。由于它们的定义方式，注释在其声明中提供了大量上下文，从而导致更短更简洁的配置。但是，XML擅长在不触及源代码或重新编译它们的情况下连接组件。一些开发人员更喜欢将布线靠近源，而另一些开发人员则认为注释类不再是POJO，而且配置变得分散且难以控制。

基于注释的配置提供了XML设置的替代方案，该配置依赖于字节码元数据来连接组件而不是角括号声明。开发人员不是使用XML来描述bean连接，而是通过在相关的类，方法或字段声明上使用注释将配置移动到组件类本身。正如在“示例：RequiredAnnotationBeanPostProcessor一节中所提到的`BeanPostProcessor`，结合注释是扩展Spring IoC容器的常用方法。例如，Spring 2.0引入了使用@Required强制执行所需属性的可能性注解。Spring 2.5使得有可能采用相同的通用方法来驱动Spring的依赖注入。本质上，`@Autowired`注释提供的功能与第7.4.5节“自动装配协作者”中所述的功能相同[，](beans.html#beans-factory-autowire)但具有更细粒度的控制和更广泛的适用性。Spring 2.5还增加了对JSR-250注释的支持，例如 `@PostConstruct`和`@PreDestroy`。Spring 3.0增加了对javax.inject包中包含的JSR-330（Java的依赖注入）注释的支持，例如`@Inject`和`@Named`。

注解注入*在* XML注入*之前*执行，因此后一种配置将覆盖通过两种方法连接的属性的前者。

与往常一样，您可以将它们注册为单独的bean定义，但也可以通过在基于XML的Spring配置中包含以下标记来隐式注册它们（请注意包含`context`命名空间）：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

</beans>
```

该隐式注册的后处理器包括 [`AutowiredAnnotationBeanPostProcessor`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor.html)， [`CommonAnnotationBeanPostProcessor`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/annotation/CommonAnnotationBeanPostProcessor.html)，[`PersistenceAnnotationBeanPostProcessor`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/orm/jpa/support/PersistenceAnnotationBeanPostProcessor.html)，以及前述 [`RequiredAnnotationBeanPostProcessor`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/beans/factory/annotation/RequiredAnnotationBeanPostProcessor.html)）。

`<context:annotation-config/>`仅查找在定义它的同一应用程序上下文中的bean上的注释。这意味着，如果你`<context:annotation-config/>`输入一个`WebApplicationContext`for `DispatcherServlet`，它只会检查`@Autowired`你的控制器中的bean，而不是你的服务。

### 7.9.1 @Required

该`@Required`注释适用于bean属性setter方法，如下面的例子：

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Required
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

此注释仅表示受影响的bean属性必须在配置时填充，通过bean定义中的显式属性值或通过自动装配填充。如果尚未填充受影响的bean属性，容器将引发异常; 这允许急切和明确的失败，以后避免`NullPointerException`s等。仍然建议您将断言放入bean类本身，例如，放入init方法。即使您在容器外部使用类，这样做也会强制执行那些必需的引用和值。

### 7.9.2 @Autowired

在下面的示例中，`@Inject`可以使用JSR 330的注释代替Spring的`@Autowired`注解。

您可以将`@Autowired`注释应用于构造函数：

```java
public class MovieRecommender {

    private final CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

从Spring Framework 4.3开始，`@Autowired`如果目标bean只定义了一个开头的构造函数，则不再需要对这样的构造函数进行注释。但是，如果有几个构造器可用，则必须注释至少一个构造器以教导容器使用哪一个。

正如所料，您还可以将`@Autowired`注释应用于“传统”setter方法：

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Autowired
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

您还可以将注解应用于具有任意名称和/或多个参数的方法：

```java
public class MovieRecommender {

    private MovieCatalog movieCatalog;

    private CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public void prepare(MovieCatalog movieCatalog,
            CustomerPreferenceDao customerPreferenceDao) {
        this.movieCatalog = movieCatalog;
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

您也可以应用于`@Autowired`字段，甚至可以将它与构造函数混合使用：

```java
public class MovieRecommender {

    private final CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    private MovieCatalog movieCatalog;

    @Autowired
    public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

确保你的目标组件（例如`MovieCatalog`，`CustomerPreferenceDao`）始终得到您正在使用您的类型声明`@Autowired`-annotated注入点。否则，由于在运行时未找到类型匹配，注入可能会失败。

对于通过类路径扫描找到的XML定义的bean或组件类，容器通常预先知道具体类型。但是，对于`@Bean`工厂方法，您需要确保声明的返回类型具有足够的表现力。对于实现多个接口的组件或可能由其实现类型引用的组件，请考虑在工厂方法上声明最具体的返回类型（至少与引用bean的注入点所要求的具体相同）

通过将注释添加到需要该类型数组的字段或方法，也可以提供特定类型的*所有* bean `ApplicationContext`：

```java
public class MovieRecommender {

    @Autowired
    private MovieCatalog[] movieCatalogs;

    // ...
}
```

这同样适用于类型集合：

```java
public class MovieRecommender {

    private Set<MovieCatalog> movieCatalogs;

    @Autowired
    public void setMovieCatalogs(Set<MovieCatalog> movieCatalogs) {
        this.movieCatalogs = movieCatalogs;
    }

    // ...
}
```

如果希望按特定顺序对数组或列表中的项进行排序，则目标bean可以实现`org.springframework.core.Ordered`接口或使用`@Order`或标准`@Priority`注释。否则，它们的顺序将遵循容器中相应目标bean定义的注册顺序。

所述`@Order`注释可以在目标类水平，而且也对被声明`@Bean`（在的多个定义的情况下用相同的bean类）的方法，有可能为每bean定义非常个体。`@Order`值可能会影响注入点的优先级，但请注意它们不会影响单例启动顺序，这是由依赖关系和`@DependsOn`声明确定的正交关注点。

请注意，标准`javax.annotation.Priority`注释在`@Bean`级别上不可用， 因为它无法在方法上声明。它的语义可以通过`@Order`值与`@Primary`每种类型的单个bean 相结合来建模。

即使键入的地图也可以自动装配，只要预期的密钥类型是`String`。Map值将包含所需类型的所有bean，并且键将包含相应的bean名称：

```java
public class MovieRecommender {

    private Map<String, MovieCatalog> movieCatalogs;

    @Autowired
    public void setMovieCatalogs(Map<String, MovieCatalog> movieCatalogs) {
        this.movieCatalogs = movieCatalogs;
    }

    // ...
}
```

默认情况下，只要*零*候选bean可用，自动装配就会失败; 默认行为是将带注释的方法，构造函数和字段视为指示*所需的*依赖项。可以更改此行为，如下所示。

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Autowired(required = false)
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

只有*一个每类注释构造函数*可以作为标记*要求*，但多个非必需的构造函数可以被注解。在这种情况下，每个都被认为是候选者之一，Spring使用*最贪婪的*构造函数，其依赖性可以得到满足，即具有最多参数的构造函数。

建议使用*必需*属性而`@Autowired`不是`@Required`注释。将*所需的*属性表示该属性不需要自动装配的目的，如果它不能自动装配的属性被忽略。`@Required`另一方面，它更强大，因为它强制执行由容器支持的任何方式设置的属性。如果未注入任何值，则会引发相应的异常。

或者，您可以通过Java 8表达特定依赖关系的非必需特性`java.util.Optional`：

```java
public class SimpleMovieLister {

    @Autowired
    public void setMovieFinder(Optional<MovieFinder> movieFinder) {
        ...
    }
}
```

您还可以使用`@Autowired`对于那些众所周知的解析依赖接口：`BeanFactory`，`ApplicationContext`，`Environment`，`ResourceLoader`，`ApplicationEventPublisher`，和`MessageSource`。这些接口及其扩展接口（如`ConfigurableApplicationContext`或`ResourcePatternResolver`）会自动解析，无需特殊设置。

```java
public class MovieRecommender {

    @Autowired
    private ApplicationContext context;

    public MovieRecommender() {
    }

    // ...
}
```

`@Autowired`，`@Inject`，`@Resource`，和`@Value`注释由Spring处理 `BeanPostProcessor`实现这反过来又意味着你*不能*在您自己的应用这些注释`BeanPostProcessor`或`BeanFactoryPostProcessor`类型（如果有的话）。这些类型必须通过XML或使用Spring `@Bean`方法显式“连接” 。

### 7.9.3使用@Primary微调基于注释的自动装配

由于按类型自动装配可能会导致多个候选人，因此通常需要对选择过程进行更多控制。实现这一目标的一种方法是使用Spring的 `@Primary`注释。`@Primary`表示当多个bean可以自动装配到单值依赖项时，应该优先选择特定的bean。如果候选者中只存在一个“主”bean，则它将是自动装配的值。

假设我们有以下配置定义`firstMovieCatalog`为 *主要* 配置`MovieCatalog`。

```xml
@Configuration
public class MovieConfiguration {

    @Bean
    @Primary
    public MovieCatalog firstMovieCatalog() { ... }

    @Bean
    public MovieCatalog secondMovieCatalog() { ... }

    // ...
}
```

通过这样的配置，以下`MovieRecommender`将自动装配 `firstMovieCatalog`。

```java
public class MovieRecommender {

    @Autowired
    private MovieCatalog movieCatalog;

    // ...
}
```

相应的bean定义如下所示。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean class="example.SimpleMovieCatalog" primary="true">
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean id="movieRecommender" class="example.MovieRecommender"/>

</beans>
```

### 7.9.4使用限定符微调基于注释的自动装配

`@Primary`当可以确定一个主要候选者时，是通过具有多个实例的类型使用自动装配的有效方式。当需要更多地控制选择过程时，`@Qualifier`可以使用Spring的注释。您可以将限定符值与特定参数相关联，缩小类型匹配集，以便为每个参数选择特定的bean。在最简单的情况下，这可以是一个简单的描述性值：

```java
public class MovieRecommender {

    @Autowired
    @Qualifier("main")
    private MovieCatalog movieCatalog;

    // ...
}
```

`@Qualifier`注解也可以在单独的构造器参数或方法参数指定：

```java
public class MovieRecommender {

    private MovieCatalog movieCatalog;

    private CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public void prepare(@Qualifier("main")MovieCatalog movieCatalog,
            CustomerPreferenceDao customerPreferenceDao) {
        this.movieCatalog = movieCatalog;
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

相应的bean定义如下所示。具有限定符值“main”的bean与使用相同值限定的构造函数参数连接。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean class="example.SimpleMovieCatalog">
        <qualifier value="main"/>

        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <qualifier value="action"/>

        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean id="movieRecommender" class="example.MovieRecommender"/>

</beans>
```

对于回退匹配，bean名称被视为默认限定符值。因此，您可以使用id“main”而不是嵌套的限定符元素来定义bean，从而得到相同的匹配结果。但是，尽管您可以使用此约定来按名称引用特定bean，但`@Autowired`基本上是关于具有可选语义限定符的类型驱动注入。这意味着即使使用bean名称回退，限定符值在类型匹配集中也总是具有缩小的语义; 它们在语义上不表示对唯一bean id的引用。良好的限定符值是“主”或“EMEA”或“持久”，表示独立于bean的特定组件的特征`id`，

限定符也适用于类型集合，如上所述，例如， `Set<MovieCatalog>`。在这种情况下，根据声明的限定符的所有匹配bean都作为集合注入。这意味着限定符不必是唯一的; 它们只是简单地构成过滤标准。例如，您可以`MovieCatalog`使用相同的限定符值“action” 定义多个bean，所有这些bean都将注入到带`Set<MovieCatalog>`注释的注释中`@Qualifier("action")`。

在类型匹配候选项中，允许根据目标bean名称选择限定符值，甚至不需要`@Qualifier`注入点处的注释。如果没有其他分辨率指示符（例如限定符或主要标记），对于非唯一依赖性情况，Spring将使注入点名称（即字段名称或参数名称）与目标bean名称匹配，并选择相同的 - 如果有的候选人。

也就是说，如果您打算按名称表达注释驱动的注入，请不要主要使用`@Autowired`，即使能够在类型匹配候选项中通过bean名称进行选择。相反，使用JSR-250 `@Resource`注释，该注释在语义上定义为通过其唯一名称标识特定目标组件，声明的类型与匹配过程无关。`@Autowired`具有相当不同的语义：按类型选择候选bean后，指定的字符串限定符值将仅在这些类型选择的候选项中被考虑，例如，将“帐户”限定符与标记有相同限定符标签的bean匹配。

对于本身定义为集合/映射或数组类型的`@Resource` bean ，是一个很好的解决方案，通过唯一名称引用特定集合或数组bean。也就是说，从4.3开始，`@Autowired`只要元素类型信息保留在`@Bean`返回类型签名或集合继承层次结构中，集合/映射和数组类型也可以通过Spring的类型匹配算法进行匹配。在这种情况下，限定符值可用于在相同类型的集合中进行选择，如上一段所述。

从4.3开始，`@Autowired`还考虑用于注入的自引用，即引用回到当前注入的bean。请注意，自我注射是一种后备; 对其他组件的常规依赖性始终具有优先权。从这个意义上讲，自我引用并不参与常规的候选人选择，因此尤其不是主要的; 相反，它们总是最低优先级。在实践中，仅使用自引用作为最后的手段，例如，通过bean的事务代理调用同一实例上的其他方法：在这种情况下，考虑将受影响的方法分解为单独的委托bean。或者，使用`@Resource`它可以通过其唯一名称获得代理回到当前bean。

`@Autowired`适用于字段，构造函数和多参数方法，允许在参数级别通过限定符注释缩小范围。相比之下，`@Resource` 仅支持具有单个参数的字段和bean属性setter方法。因此，如果您的注射目标是构造函数或多参数方法，请坚持使用限定符。

您可以创建自己的自定义限定符注释。只需定义注释并`@Qualifier`在定义中提供注解：

```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Genre {

    String value();
}
```

然后，您可以在自动装配的字段和参数上提供自定义限定符：

```java
public class MovieRecommender {

    @Autowired
    @Genre("Action")
    private MovieCatalog actionCatalog;

    private MovieCatalog comedyCatalog;

    @Autowired
    public void setComedyCatalog(@Genre("Comedy") MovieCatalog comedyCatalog) {
        this.comedyCatalog = comedyCatalog;
    }

    // ...
}
```

接下来，提供候选bean定义的信息。您可以将`<qualifier/>`标记添加为 标记的子元素，`<bean/>`然后指定`type`和 `value`匹配自定义限定符注释。类型与注释的完全限定类名匹配。或者，为方便起见，如果不存在冲突名称的风险，您可以使用短类名称。以下示例演示了这两种方法。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean class="example.SimpleMovieCatalog">
        <qualifier type="Genre" value="Action"/>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <qualifier type="example.Genre" value="Comedy"/>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean id="movieRecommender" class="example.MovieRecommender"/>

</beans>
```

在第7.10节“类路径扫描和托管组件”中，您将看到基于注释的替代方法，用于在XML中提供限定符元数据。具体来说，请参见第7.10.8节“使用注释提供限定符元数据”。

在某些情况下，使用没有值的注释可能就足够了。当注释用于更通用的目的并且可以跨多种不同类型的依赖项应用时，这可能很有用。例如，您可以提供 在没有Internet连接时将搜索的*脱机*目录。首先定义简单注释：

```Java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Offline {

}
```

然后将注解添加到要自动装配的字段或属性中：

```java
public class MovieRecommender {

    @Autowired
    @Offline
    private MovieCatalog offlineCatalog;

    // ...
}
```

现在bean定义只需要一个限定符`type`：

```xml
<bean class="example.SimpleMovieCatalog">
    <qualifier type="Offline"/>
    <!-- inject any dependencies required by this bean -->
</bean>
```

您还可以定义除简单`value`属性之外或代替简单属性接受命名属性的自定义限定符注释。如果随后在要自动装配的字段或参数上指定了多个属性值，则bean定义必须匹配*所有*此类属性值才能被视为自动装配候选。例如，请考虑以下注释定义：

```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface MovieQualifier {

    String genre();

    Format format();
}
```

在这种情况下`Format`是一个枚举：

```java
public enum Format {
    VHS, DVD, BLURAY
}
```

要自动装配的字段使用自定义限定符进行注释，并包含两个属性的值：`genre`和`format`。

```xml
public class MovieRecommender {

    @Autowired
    @MovieQualifier(format=Format.VHS, genre="Action")
    private MovieCatalog actionVhsCatalog;

    @Autowired
    @MovieQualifier(format=Format.VHS, genre="Comedy")
    private MovieCatalog comedyVhsCatalog;

    @Autowired
    @MovieQualifier(format=Format.DVD, genre="Action")
    private MovieCatalog actionDvdCatalog;

    @Autowired
    @MovieQualifier(format=Format.BLURAY, genre="Comedy")
    private MovieCatalog comedyBluRayCatalog;

    // ...
}
```

最后，bean定义应包含匹配的限定符值。此示例还演示可以使用bean *元*属性而不是 `<qualifier/>`子元素。如果可用，则`<qualifier/>`其属性优先，但`<meta/>`如果不存在此限定符，则自动装配机制将回退到标记内提供的值 ，如以下示例中的最后两个bean定义。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean class="example.SimpleMovieCatalog">
        <qualifier type="MovieQualifier">
            <attribute key="format" value="VHS"/>
            <attribute key="genre" value="Action"/>
        </qualifier>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <qualifier type="MovieQualifier">
            <attribute key="format" value="VHS"/>
            <attribute key="genre" value="Comedy"/>
        </qualifier>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <meta key="format" value="DVD"/>
        <meta key="genre" value="Action"/>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <meta key="format" value="BLURAY"/>
        <meta key="genre" value="Comedy"/>
        <!-- inject any dependencies required by this bean -->
    </bean>

</beans>
```

### 7.9.5使用泛型作为自动装配限定符

除了`@Qualifier`注释之外，还可以使用Java泛型类型作为隐式的限定形式。例如，假设您具有以下配置：

```java
@Configuration
public class MyConfiguration {

    @Bean
    public StringStore stringStore() {
        return new StringStore();
    }

    @Bean
    public IntegerStore integerStore() {
        return new IntegerStore();
    }
}
```

假设上述bean实现一个通用接口，即`Store<String>`和 `Store<Integer>`，你可以`@Autowire`在`Store`界面和*通用*将作为一个限定：

```java
@Autowired
private Store<String> s1; // <String> qualifier, injects the stringStore bean

@Autowired
private Store<Integer> s2; // <Integer> qualifier, injects the integerStore bean
```

通用限定符也适用于自动装配列表，map和数组：

```java
// Inject all Store beans as long as they have an <Integer> generic
// Store<String> beans will not appear in this list
@Autowired
private List<Store<Integer>> s;
```

### 7.9.6 CustomAutowireConfigurer

这 [`CustomAutowireConfigurer`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/beans/factory/annotation/CustomAutowireConfigurer.html) 是一个`BeanFactoryPostProcessor`使您能够注册自己的自定义限定符注释类型，即使它们没有使用Spring的`@Qualifier`注释注释。

```xml
<bean id="customAutowireConfigurer"
        class="org.springframework.beans.factory.annotation.CustomAutowireConfigurer">
    <property name="customQualifierTypes">
        <set>
            <value>example.CustomQualifier</value>
        </set>
    </property>
</bean>
```

通过以下方式`AutowireCandidateResolver`确定autowire候选人：

- `autowire-candidate`每个bean定义 的值
- 元素上 `default-autowire-candidates`可用的 任何模式`<beans/>`
- `@Qualifier`注释 的存在以及注册的任何自定义注释`CustomAutowireConfigurer`

当多个bean有资格作为autowire候选者时，“primary”的确定如下：如果候选者中只有一个bean定义具有`primary` 设置为的属性`true`，则将选择它。

### 7.9.7 @Resource

Spring还支持`@Resource`在字段或bean属性setter方法上使用JSR-250 注释进行注入。这是Java EE 5和6中的常见模式，例如在JSF 1.2托管bean或JAX-WS 2.0端点中。Spring也支持Spring管理对象的这种模式。

`@Resource`采用name属性，默认情况下，Spring将该值解释为要注入的bean名称。换句话说，它遵循*按名称*语义，如本例所示：

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Resource(name="myMovieFinder")
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
}
```

如果未明确指定名称，则默认名称是从字段名称或setter方法派生的。如果是字段，则采用字段名称; 在setter方法的情况下，它采用bean属性名称。所以下面的例子将把名为“movieFinder”的bean注入其setter方法：

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Resource
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
}
```

提供注解的名称解析由一个bean的名称 `ApplicationContext`，其中的`CommonAnnotationBeanPostProcessor`知道。如果您[`SimpleJndiBeanFactory`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/jndi/support/SimpleJndiBeanFactory.html) 明确配置Spring，则可以通过JNDI解析名称 。但是，建议您依赖于默认行为，只需使用Spring的JNDI查找功能来保持间接级别。

在专属情况下，`@Resource`不指定明确的名称，以及类似的使用`@Autowired`，`@Resource`发现的主要类型的比赛，而不是一个具体的bean并解决众所周知的解析依存关系：`BeanFactory`， `ApplicationContext`，`ResourceLoader`，`ApplicationEventPublisher`，和`MessageSource` 接口。

因此，在以下示例中，`customerPreferenceDao`字段首先查找名为customerPreferenceDao的bean，然后返回到该类型的主类型匹配 `CustomerPreferenceDao`。基于已知的可解析依赖性类型注入“上下文”字段`ApplicationContext`。

```java
public class MovieRecommender {

    @Resource
    private CustomerPreferenceDao customerPreferenceDao;

    @Resource
    private ApplicationContext context;

    public MovieRecommender() {
    }

    // ...
}
```

### 7.9.8 @PostConstruct和@PreDestroy

将`CommonAnnotationBeanPostProcessor`不仅承认了`@Resource`注解也是JSR-250 *的生命周期*注解。在Spring 2.5中引入，对这些注释的支持提供了 初始化回调 和 销毁回调中描述的另一种替代 方法。如果 `CommonAnnotationBeanPostProcessor`在Spring中注册 `ApplicationContext`，则在生命周期的同一点调用带有这些注释之一的方法，作为相应的Spring生命周期接口方法或显式声明的回调方法。在下面的示例中，缓存将在初始化时预先填充，并在销毁时清除。

```java
public class CachingMovieLister {

    @PostConstruct
    public void populateMovieCache() {
        // populates the movie cache upon initialization...
    }

    @PreDestroy
    public void clearMovieCache() {
        // clears the movie cache upon destruction...
    }
}
```