# 7.12 Java代码容器配置

## 7.12基于Java的容器配置

### 7.12.1基本概念：@Bean和@Configuration

Spring的新Java配置支持中的中心工件是 `@Configuration`注释类和`@Bean`注释方法。

该`@Bean`注释被用于指示一个方法实例，配置和初始化为通过Spring IoC容器进行管理的新对象。对于那些熟悉Spring的`<beans/>`XML配置的人来说，`@Bean`注释与`<bean/>`元素扮演的角色相同。您可以`@Bean`对任何Spring 使用带注释的方法 `@Component`，但是，它们最常用于`@Configuration`bean。

对类进行注释`@Configuration`表明其主要目的是作为bean定义的来源。此外，`@Configuration`类允许通过简单地调用`@Bean`同一类中的其他方法来定义bean间依赖关系。最简单的`@Configuration`类如下：

```java
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
```

`AppConfig`上面的类将等效于以下Spring `<beans/>`XML：

```xml
<beans>
    <bean id="myService" class="com.acme.services.MyServiceImpl"/>
</beans>
```

**完整@Configuration vs'lite'@Bean模式？**

当`@Bean`在*未*注释的类中声明方法时， `@Configuration`它们被称为以“精简”模式处理。`@Component`在一个*普通的旧类中*或甚至在一个*普通的旧类中*声明的Bean方法将被视为“lite”，具有包含类的不同主要目的，并且`@Bean`方法只是那里的一种奖励。例如，服务组件可以通过`@Bean`每个适用组件类的附加方法将管理视图公开给容器。在这种情况下，`@Bean`方法是一种简单的通用工厂方法机制。

与full不同`@Configuration`，lite `@Bean`方法不能声明bean间依赖关系。相反，它们对其包含组件的内部状态以及它们可能声明的参数进行操作。`@Bean`因此，这种方法不应援引其他 `@Bean`方法; 每个这样的方法实际上只是一个特定bean引用的工厂方法，没有任何特殊的运行时语义。这里的积极副作用是不必在运行时应用CGLIB子类，因此在类设计方面没有限制（即包含类可能仍然是`final`等等）。

在常见的场景中，`@Bean`方法将在`@Configuration`类中声明，确保始终使用“完整”模式，因此交叉方法引用将被重定向到容器的生命周期管理。这将防止`@Bean`通过常规Java调用意外地调用相同的 方法，这有助于减少在“精简”模式下操作时难以跟踪的细微错误。

这些`@Bean`和`@Configuration`注释将在下面的部分中进行深入讨论。首先，我们将介绍使用基于Java的配置创建弹簧容器的各种方法。

### 7.12.2使用AnnotationConfigApplicationContext实例化Spring容器

下面的部分记录了Spring的`AnnotationConfigApplicationContext`新版本，Spring 3.0中的新版本。这种通用`ApplicationContext`实现不仅能够接受`@Configuration`类作为输入，还能接受 `@Component`使用JSR-330元数据注释的普通类和类。

当`@Configuration`提供类作为输入时，`@Configuration`类本身被注册为bean定义，并且`@Bean`类中的所有声明的方法也被注册为bean定义。

当`@Component`提供JSR-330类时，它们被注册为bean定义，并且假定DI元数据例如`@Autowired`或`@Inject`在必要时在这些类中使用。

#### 结构简单

与实例化a时Spring XML文件用作输入的方式大致相同，在实例化时 `ClassPathXmlApplicationContext`，`@Configuration`类可用作输入`AnnotationConfigApplicationContext`。这允许Spring容器的完全无XML使用：

```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```

如上所述，`AnnotationConfigApplicationContext`不仅限于与`@Configuration`班级一起工作。`@Component`可以将任何或JSR-330带注释的类作为输入提供给构造函数。例如：

```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(MyServiceImpl.class, Dependency1.class, Dependency2.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```

以上假设`MyServiceImpl`，`Dependency1`并`Dependency2`使用Spring依赖注入注释等`@Autowired`。

#### 使用register（Class <？> ...）以编程方式构建容器

一个`AnnotationConfigApplicationContext`可以使用一个无参数的构造被实例化，然后使用配置的`register()`方法。这种方法在以编程方式构建时特别有用`AnnotationConfigApplicationContext`。

```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(AppConfig.class, OtherConfig.class);
    ctx.register(AdditionalConfig.class);
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```

#### 使用扫描启用组件扫描（String ...）

要启用组件扫描，只需`@Configuration`按如下方式注释您的类：

```java
@Configuration
@ComponentScan(basePackages = "com.acme")
public class AppConfig  {
    ...
}
```

有经验的Spring用户将熟悉与Spring的`context:`命名空间等效的XML声明

```xml
<beans> 
    <context：component-scan  base-package = “com.acme” /> 
</ beans>
```

在上面的示例中，`com.acme`将扫描包，查找任何已 `@Component`注释的类，并且这些类将在容器中注册为Spring bean定义。`AnnotationConfigApplicationContext`公开 `scan(String…)`方法以允许相同的组件扫描功能：

```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.scan("com.acme");
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
}
```

请记住，`@Configuration`类是[元注释](beans.html#beans-meta-annotations) 的`@Component`，因此它们是组件扫描的候选者！在上面的示例中，假设`AppConfig`在`com.acme`包（或下面的任何包）中声明它，它将在调用期间被拾取`scan()`，并且在`refresh()`其所有`@Bean`方法将被处理并在容器内注册为bean定义。

#### 使用AnnotationConfigWebApplicationContext支持Web应用程序

可用的`WebApplicationContext`变体。配置Spring servlet侦听器，Spring MVC 等时可以使用此实现。以下是配置典型Spring MVC Web应用程序的代码段。注意context-param和init-param的使用：`AnnotationConfigApplicationContext``AnnotationConfigWebApplicationContext``ContextLoaderListener``DispatcherServlet``web.xml``contextClass`

```xml
<web-app>
    <!-- Configure ContextLoaderListener to use AnnotationConfigWebApplicationContext
        instead of the default XmlWebApplicationContext -->
    <context-param>
        <param-name>contextClass</param-name>
        <param-value>
            org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </context-param>

    <!-- Configuration locations must consist of one or more comma- or space-delimited
        fully-qualified @Configuration classes. Fully-qualified packages may also be
        specified for component-scanning -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>com.acme.AppConfig</param-value>
    </context-param>

    <!-- Bootstrap the root application context as usual using ContextLoaderListener -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- Declare a Spring MVC DispatcherServlet as usual -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- Configure DispatcherServlet to use AnnotationConfigWebApplicationContext
            instead of the default XmlWebApplicationContext -->
        <init-param>
            <param-name>contextClass</param-name>
            <param-value>
                org.springframework.web.context.support.AnnotationConfigWebApplicationContext
            </param-value>
        </init-param>
        <!-- Again, config locations must consist of one or more comma- or space-delimited
            and fully-qualified @Configuration classes -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>com.acme.web.MvcConfig</param-value>
        </init-param>
    </servlet>

    <!-- map all requests for /app/* to the dispatcher servlet -->
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>
</web-app>
```

### 7.12.3使用@Bean批注

`@Bean`是方法级注释和XML `<bean/>`元素的直接模拟。注释支持一些提供的属性`<bean/>`，例如： [init-method](beans.html#beans-factory-lifecycle-initializingbean)， [destroy-method](beans.html#beans-factory-lifecycle-disposablebean)， [autowiring](beans.html#beans-factory-autowire)和`name`。

您可以在带`@Bean`注释的类`@Configuration`或带 注释的类中使用注释`@Component`。

#### 声明一个bean

要声明bean，只需使用注释注释方法即可`@Bean`。您可以使用此方法在`ApplicationContext`指定为方法的返回值的类型中注册bean定义。默认情况下，bean名称将与方法名称相同。以下是`@Bean`方法声明的简单示例：

```java
@Configuration
public class AppConfig {

    @Bean
    public TransferServiceImpl transferService() {
        return new TransferServiceImpl();
    }
}
```

上述配置与以下Spring XML完全等效：

```xml
<beans> 
    <bean  id = “transferService”  class = “com.acme.TransferServiceImpl” /> 
</ beans>
```

这两个声明都将一个名为`transferService`available 的bean命名为`ApplicationContext`绑定到类型的对象实例`TransferServiceImpl`：

```java
transferService  - > com.acme.TransferServiceImpl
```

您还可以`@Bean`使用接口（或基类）返回类型声明您的方法：

```java
@Configuration
 public  class AppConfig {

    @Bean
     public TransferService transferService（）{
         return  new TransferServiceImpl（）;
    }
}
```

但是，这会将高级类型预测的可见性限制为指定的接口类型（`TransferService`），然后`TransferServiceImpl`一旦受影响的单例bean被实例化，只有容器知道完整类型（）。非延迟单例bean根据它们的声明顺序进行实例化，因此您可能会看到不同的类型匹配结果，具体取决于另一个组件何时尝试通过非声明类型进行匹配（例如`@Autowired TransferServiceImpl` ，只有在“transferService”bean被解析后才会解析实例化）。

如果您始终通过声明的服务接口引用您的类型，则您的 `@Bean`返回类型可以安全地加入该设计决策。但是，对于实现多个接口的组件或可能由其实现类型引用的组件，更安全地声明可能的最具体的返回类型（至少与引用您的bean的注入点所需的具体相同）。

#### Bean依赖项

带`@Bean`注释的方法可以有任意数量的参数来描述构建该bean所需的依赖关系。例如，如果我们`TransferService` 需要，`AccountRepository`我们可以通过方法参数实现该依赖：

```java
@Configuration
public class AppConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }
}
```

#### 接收生命周期回调

与定义的任何类`@Bean`注释支持定时生命周期回调，可以使用`@PostConstruct`并`@PreDestroy`注解从JSR-250，见 JSR-250注解进一步的细节。

完全支持常规的Spring 生命周期回调。如果bean实现`InitializingBean`，`DisposableBean`或者`Lifecycle`它们各自的方法由容器调用。

还完全支持BeanFactoryAware，BeanNameAware，MessageSourceAware， ApplicationContextAware*Aware`等标准接口集。

该`@Bean`注释支持指定任意初始化和销毁回调方法，就像春天XML的`init-method`，并`destroy-method`在属性上的`bean`元素：

```java
public class Foo {

    public void init() {
        // initialization logic
    }
}

public class Bar {

    public void cleanup() {
        // destruction logic
    }
}

@Configuration
public class AppConfig {

    @Bean(initMethod = "init")
    public Foo foo() {
        return new Foo();
    }

    @Bean(destroyMethod = "cleanup")
    public Bar bar() {
        return new Bar();
    }
}
```

默认情况下，使用具有public `close`或`shutdown` method的Java配置定义的bean 会自动使用销毁回调登记。如果您有一个公共 `close`或`shutdown`方法，并且您不希望在容器关闭时调用它，只需添加`@Bean(destroyMethod="")`到您的bean定义以禁用默认`(inferred)`模式。

对于通过JNDI获取的资源，您可能希望默认执行此操作，因为其生命周期在应用程序外部进行管理。特别是，请确保始终执行此操作，`DataSource`因为已知Java EE应用程序服务器上存在问题。

```java
@Bean(destroyMethod="")
public DataSource dataSource() throws NamingException {
    return (DataSource) jndiTemplate.lookup("MyDS");
}
```

此外，使用`@Bean`方法，您通常会选择使用编程JNDI查找：使用Spring `JndiTemplate`/ `JndiLocatorDelegate`helper或直接JNDI`InitialContext`用法，但不会`JndiObjectFactoryBean`强制您将返回类型声明为`FactoryBean`类型而不是实际目标类型的变体，使其成为可能在其他`@Bean`打算在这里引用提供的资源的方法中更难用于交叉引用调用。

当然，在上述情况下， 在构造期间直接`Foo`调用该`init()`方法同样有效：

```java
@Configuration
public class AppConfig {

    @Bean
    public Foo foo() {
        Foo foo = new Foo();
        foo.init();
        return foo;
    }

    // ...
}
```

当您直接使用Java工作时，您可以使用对象执行任何您喜欢的操作，并且不必总是依赖于容器生命周期！

#### 指定bean范围

##### 使用@Scope注释

您可以指定使用`@Bean`注释定义的bean 应具有特定范围。您可以使用Bean Scopes部分中指定的任何标准作用域 。

默认范围是`singleton`，但您可以使用`@Scope`注释覆盖它：

```java
@Configuration
public class MyConfiguration {

    @Bean
    @Scope("prototype")
    public Encryptor encryptor() {
        // ...
    }
}
```

##### @Scope和scoped-proxy

Spring提供了一种通过 作用域代理 处理作用域依赖项的便捷方法 。使用XML配置时创建此类代理的最简单方法是`<aop:scoped-proxy/>`元素。使用@Scope批注在Java中配置bean提供了与proxyMode属性的等效支持。默认值为no proxy（`ScopedProxyMode.NO`），但您可以指定`ScopedProxyMode.TARGET_CLASS`或`ScopedProxyMode.INTERFACES`。

如果将scoped代理示例从XML参考文档（参见前面的链接）移植到我们`@Bean`使用的Java，它将如下所示：

```java
// an HTTP Session-scoped bean exposed as a proxy
@Bean
@SessionScope
public UserPreferences userPreferences() {
    return new UserPreferences();
}

@Bean
public Service userService() {
    UserService service = new SimpleUserService();
    // a reference to the proxied userPreferences bean
    service.setUserPreferences(userPreferences());
    return service;
}
```

#### 自定义bean命名

默认情况下，配置类使用`@Bean`方法的名称作为结果bean的名称。但是，可以使用该`name`属性覆盖此功能。

```java
@Configuration
public class AppConfig {

    @Bean(name = "myFoo")
    public Foo foo() {
        return new Foo();
    }
}
```

#### Bean别名

正如 第7.3.1节“命名bean”中 所讨论的，有时需要为单个bean提供多个名称，也称为*bean别名*。 为此目的`name`，`@Bean`注释的属性接受String数组。

```java
@Configuration
public class AppConfig {

    @Bean({"dataSource", "subsystemA-dataSource", "subsystemB-dataSource"})
    public DataSource dataSource() {
        // instantiate, configure and return DataSource bean...
    }
}
```

#### Bean的描述

有时提供bean的更详细的文本描述是有帮助的。当bean暴露（可能通过JMX）用于监视目的时，这可能特别有用。

要添加`@Bean`对 [`@Description`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/annotation/Description.html) 注释的描述，可以使用：

```java
@Configuration
public class AppConfig {

    @Bean
    @Description("Provides a basic example of a bean")
    public Foo foo() {
        return new Foo();
    }
}
```

### 7.12.4使用@Configuration批注

`@Configuration`是一个类级别的注释，指示对象是bean定义的来源。`@Configuration`类通过公共`@Bean`注释方法声明bean 。`@Bean`对`@Configuration`类上的方法的调用也可用于定义bean间依赖项。有关一般介绍[，](beans.html#beans-java-basic-concepts)请参见第7.12.1节“基本概念：@Bean和@Configuration”。

#### 注入bean间依赖关系

当`@Bean`s彼此依赖时，表达该依赖就像让一个bean方法调用另一个一样简单：

```java
@Configuration
public class AppConfig {

    @Bean
    public Foo foo() {
        return new Foo(bar());
    }

    @Bean
    public Bar bar() {
        return new Bar();
    }
}
```

在上面的示例中，`foo`bean接收对`bar`via构造函数注入的引用。

种声明bean间依赖关系的`@Bean`方法仅在方法在`@Configuration`类中声明时才有效。您不能使用普通`@Component`类声明bean间依赖项。

#### 查找方法注入

如前所述，查找方法注入 是一项很少使用的高级功能。在单例范围的bean依赖于原型范围的bean的情况下，它很有用。将Java用于此类配置提供了实现此模式的自然方法。

```java
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

使用Java配置支持，您可以创建一个子类，`CommandManager`其中抽象`createCommand()`方法被覆盖，以便查找新的（原型）命令对象：

```java
@Bean
@Scope("prototype")
public AsyncCommand asyncCommand() {
    AsyncCommand command = new AsyncCommand();
    // inject dependencies here as required
    return command;
}

@Bean
public CommandManager commandManager() {
    // return new anonymous implementation of CommandManager with command() overridden
    // to return a new prototype Command object
    return new CommandManager() {
        protected Command createCommand() {
            return asyncCommand();
        }
    }
}
```

#### 有关基于Java的配置如何在内部工作的更多信息

以下示例显示了`@Bean`两次调用的带注释的方法：

```java
@Configuration
public class AppConfig {

    @Bean
    public ClientService clientService1() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientService clientService2() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientDao clientDao() {
        return new ClientDaoImpl();
    }
}
```

`clientDao()`被称为一次进入`clientService1()`和进入一次`clientService2()`。由于此方法创建了一个新实例`ClientDaoImpl`并将其返回，因此通常需要2个实例（每个服务一个）。这肯定会有问题：在Spring中，实例化的bean `singleton`默认具有范围。这就是魔术的用武之地：所有`@Configuration`类都在启动时被子类化`CGLIB`。在子类中，子方法在调用父方法并创建新实例之前，首先检查容器是否有任何缓存（作用域）bean。请注意，从Spring 3.2开始，不再需要将CGLIB添加到类路径中，因为CGLIB类已经重新打包`org.springframework.cglib`并直接包含在spring-core JAR中。

由于CGLIB在启动时动态添加功能，因此存在一些限制，特别是配置类不能是最终的。但是，从4.3开始，配置类允许使用任何构造函数，包括使用`@Autowired`默认注入的单个非默认构造函数声明。

如果您希望避免任何CGLIB强加的限制，请考虑`@Bean` 在非`@Configuration`类上声明您的方法，例如在普通`@Component`类上。因此，方法之间的跨方法调用`@Bean`不会被截获，因此您必须完全依赖于构造函数或方法级别的依赖注入。

### 7.12.5编写基于Java的配置

#### 使用@Import注释

就像`<import/>`在Spring XML文件中使用元素来帮助模块化配置一样，`@Import`注释允许`@Bean`从另一个配置类加载定义：

```java
@Configuration
public class ConfigA {

    @Bean
    public A a() {
        return new A();
    }
}

@Configuration
@Import(ConfigA.class)
public class ConfigB {

    @Bean
    public B b() {
        return new B();
    }
}
```

现在，不需要同时指定`ConfigA.class`和`ConfigB.class`实例化上下文，只`ConfigB`需要显式提供：

```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigB.class);

    // now both beans A and B will be available...
    A a = ctx.getBean(A.class);
    B b = ctx.getBean(B.class);
}
```

这种方法简化了容器实例化，因为只需要处理一个类，而不是要求开发人员`@Configuration`在构造期间记住潜在的大量 类。

从Spring Framework 4.2开始，`@Import`还支持对常规组件类的引用，类似于`AnnotationConfigApplicationContext.register`方法。如果您想避免组件扫描，使用一些配置类作为明确定义所有组件的入口点，这将特别有用。

##### 注入对导入的@Bean定义的依赖关系

上面的例子有效，但很简单。在大多数实际情况中，bean将跨配置类相互依赖。使用XML时，这本身并不是问题，因为不涉及编译器，可以简单地声明`ref="someBean"`并相信Spring会在容器初始化期间解决它。当然，在使用`@Configuration`类时，Java编译器会对配置模型施加约束，因为对其他bean的引用必须是有效的Java语法。

幸运的是，解决这个问题很简单。正如 我们已经讨论过的， `@Bean`方法可以有任意数量的参数来描述bean的依赖关系。让我们考虑一个更真实的场景，其中包含几个`@Configuration` 类，每个类都取决于其他类中声明的bean：

```java
@Configuration
public class ServiceConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }
}

@Configuration
public class RepositoryConfig {

    @Bean
    public AccountRepository accountRepository(DataSource dataSource) {
        return new JdbcAccountRepository(dataSource);
    }
}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }
}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```

还有另一种方法可以达到相同的效果。请记住，`@Configuration`类最终只是容器中的另一个bean：这意味着它们可以像任何其他bean一样利用 `@Autowired`和`@Value`注入等等！

确保以这种方式注入的依赖项只是最简单的类型。`@Configuration` 在上下文初始化期间很早就处理了类，并强制以这种方式注入依赖项可能会导致意外的早期初始化。尽可能采用基于参数的注入，如上例所示。

另外，要特别注意`BeanPostProcessor`和`BeanFactoryPostProcessor`定义via `@Bean`。这些通常应该声明为`static @Bean`方法，而不是触发其包含配置类的实例化。否则，`@Autowired`并且`@Value`不会对配置类本身，因为它是被作为一个bean实例创建太早上班。

```java
@Configuration
public class ServiceConfig {

    @Autowired
    private AccountRepository accountRepository;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(accountRepository);
    }
}

@Configuration
public class RepositoryConfig {

    private final DataSource dataSource;

    @Autowired
    public RepositoryConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }
}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }
}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```

`@Configuration`仅在Spring Framework 4.3中支持类中的构造函数注入。还要注意，不需要指定`@Autowired`目标bean是否只定义了一个构造函数; 在上面的例子中，`@Autowired`在`RepositoryConfig`构造函数上没有必要。

在上面的场景中，使用`@Autowired`效果很好并提供了所需的模块性，但确定声明自动装配的bean定义的确切位置仍然有些模棱两可。例如，作为开发人员`ServiceConfig`，您如何确切地知道`@Autowired AccountRepository`bean的声明位置？它在代码中并不明确，这可能就好了。请记住， [Spring Tool Suite](https://spring.io/tools/sts)提供的工具可以呈现图形，显示所有内容的连接方式 - 这可能就是您所需要的。此外，您的Java IDE可以轻松找到该`AccountRepository`类型的所有声明和用法，并将快速显示`@Bean`返回该类型的方法的位置。

如果这种歧义是不可接受的，并且您希望从IDE中从一个`@Configuration`类直接导航到另一个类，请考虑自行装配配置类：

```java
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        // navigate 'through' the config class to the @Bean method!
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }
}
```

在上面的情况中，它是完全明确`AccountRepository`定义的位置。但是，`ServiceConfig`现在紧紧地联系在一起`RepositoryConfig`; 这是权衡。通过使用基于接口的或基于`@Configuration`类的抽象类，可以在某种程度上减轻这种紧密耦合。考虑以下：

```java
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }
}

@Configuration
public interface RepositoryConfig {

    @Bean
    AccountRepository accountRepository();
}

@Configuration
public class DefaultRepositoryConfig implements RepositoryConfig {

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(...);
    }
}

@Configuration
@Import({ServiceConfig.class, DefaultRepositoryConfig.class})  // import the concrete config!
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return DataSource
    }

}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```

现在`ServiceConfig`与具体内容松散耦合 `DefaultRepositoryConfig`，内置的IDE工具仍然很有用：开发人员很容易获得实现的类型层次结构`RepositoryConfig`。通过这种方式，导航`@Configuration`类及其依赖关系与导航基于接口的代码的常规过程没有什么不同。

如果您想影响某些bean的启动创建顺序，可以考虑将它们中的一些声明为`@Lazy`（用于在首次访问时创建而不是在启动时）或`@DependsOn`在某些其他bean上（确保在当前之前创建特定的其他bean） bean，超出后者的直接依赖性所暗示的）。

#### 有条件地包括@Configuration类或@Bean方法

有条件地启用或禁用完整的`@Configuration`类甚至是个人通常很有用`@Bean`基于某些任意系统状态方法。一个常见的例子是`@Profile`只有在Spring中启用了特定的配置文件时才使用注释来激活bean `Environment`（ 有关详细信息[，](beans.html#beans-definition-profiles)请参见[第7.13.1节“Bean定义配置文件”](beans.html#beans-definition-profiles)）。

该`@Profile`注释是使用所谓的更灵活的注释实际执行[`@Conditional`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/annotation/Conditional.html)。该`@Conditional`注释指示特定`org.springframework.context.annotation.Condition`前应谘询的实施`@Bean`是注册。

`Condition`接口的实现只是提供一个`matches(…)` 返回`true`或的方法`false`。例如，以下是`Condition`用于的实际 实现`@Profile`：

```java
@Override
public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    if (context.getEnvironment() != null) {
        // Read the @Profile annotation attributes
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (attrs != null) {
            for (Object value : attrs.get("value")) {
                if (context.getEnvironment().acceptsProfiles(((String[]) value))) {
                    return true;
                }
            }
            return false;
        }
    }
    return true;
}
```

#### 结合Java和XML配置

Spring的`@Configuration`类支持并非旨在成为Spring XML的100％完全替代品。诸如Spring XML命名空间之类的一些工具仍然是配置容器的理想方式。在XML方便或必要的情况下，您可以选择：例如，以“以XML为中心”的方式实例化容器 `ClassPathXmlApplicationContext`，或者以“以Java为中心”的方式使用`AnnotationConfigApplicationContext`和`@ImportResource`根据需要导入XML 的注释。 。

##### 以XML为中心的@Configuration类的使用

最好从XML引导Spring容器并`@Configuration`以ad-hoc方式包含 类。例如，在使用Spring XML的大型现有代码库中，根据需要创建`@Configuration`类并将其包含在现有XML文件中会更容易。下面你将找到`@Configuration`在这种“以XML为中心”的情况下使用类的选项。

请记住，`@Configuration`类最终只是容器中的bean定义。在这个例子中，我们创建一个`@Configuration`名为的类，`AppConfig`并将其`system-test-config.xml`作为`<bean/>`定义包含在内。因为 `<context:annotation-config/>`已打开，容器将识别 `@Configuration`注释并 正确处理`@Bean`声明的方法`AppConfig`。

```java
@Configuration
public class AppConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }

    @Bean
    public TransferService transferService() {
        return new TransferService(accountRepository());
    }
}
```

**system-test-config.xml**:

```xml
<beans>
    <!-- enable processing of annotations such as @Autowired and @Configuration -->
    <context:annotation-config/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="com.acme.AppConfig"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```

**jdbc.properties**:

```bash
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=sa
jdbc.password=
```

```java
public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/com/acme/system-test-config.xml");
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}
```

在`system-test-config.xml`上面，`AppConfig` `<bean/>`没有声明一个`id` 元素。虽然这样做是可以接受的，但是没有其他bean可以引用它，并且不太可能通过名称从容器中显式获取它。与`DataSource`bean 类似- 它只是由类型自动装配，因此`id`不严格要求显式bean 。

因为`@Configuration`带有元注释`@Component`，注释`@Configuration`类自动成为组件扫描的候选者。使用与上面相同的方案，我们可以重新定义`system-test-config.xml`以利用组件扫描。请注意，在这种情况下，我们不需要显式声明 `<context:annotation-config/>`，因为`<context:component-scan/>`启用相同的功能。

**system-test-config.xml**：

```xml
<beans>
    <!-- picks up and registers AppConfig as a bean definition -->
    <context:component-scan base-package="com.acme"/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```

##### @Configuration以类为中心使用带@ImportResource的XML

在`@Configuration`类是配置容器的主要机制的应用程序中，仍然可能需要使用至少一些XML。在这些场景中，只需使用`@ImportResource`和定义所需的XML。这样做可以实现“以Java为中心”的方法来配置容器并将XML保持在最低限度。

```java
@Configuration
@ImportResource("classpath:/com/acme/properties-config.xml")
public class AppConfig {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(url, username, password);
    }
}
```

```xml
properties-config.xml
<beans>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>
</beans>
```

```bash
jdbc.properties
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=sa
jdbc.password=
```

```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}
```

