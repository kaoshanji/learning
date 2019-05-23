# 7.15 ApplicationContext

正如章节介绍中所讨论的，该`org.springframework.beans.factory` 包提供了管理和操作bean的基本功能，包括以编程方式。除了扩展其他接口以提供更多*面向应用程序框架的样式的*附加功能外 ，该`org.springframework.context`软件包还添加了 [`ApplicationContext`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/ApplicationContext.html)扩展`BeanFactory`接口的接口。许多人以完全声明的方式使用它，甚至不以编程方式创建它，而是依赖于支持类，例如自动实例化 作为Java EE Web应用程序的正常启动过程的一部分。`ApplicationContext``ContextLoader``ApplicationContext`

为了`BeanFactory`以更加面向框架的样式增强功能，上下文包还提供以下功能：

- 通过`MessageSource`界面*访问i18n风格的消息*。
- 通过`ResourceLoader`界面*访问* URL和文件等*资源*。
- *事件发布*即`ApplicationListener`通过使用接口实现接口的bean`ApplicationEventPublisher`。
- *加载多个（分层）上下文*，允许每个*上下文*通过`HierarchicalBeanFactory`接口聚焦在一个特定层上，例如应用程序的Web层 。

### 7.15.1使用MessageSource进行国际化

该`ApplicationContext`接口扩展了一个名为的接口`MessageSource`，因此提供了国际化（i18n）功能。Spring还提供了接口`HierarchicalMessageSource`，可以分层次地解析消息。这些接口共同构成了Spring影响消息解析的基础。这些接口上定义的方法包括：

- `String getMessage(String code, Object[] args, String default, Locale loc)`：用于从中检索消息的基本方法`MessageSource`。如果未找到指定区域设置的消息，则使用默认消息。传入的任何参数都使用`MessageFormat`标准库提供的功能成为替换值。
- `String getMessage(String code, Object[] args, Locale loc)`：基本上与前一个方法相同，但有一点不同：不能指定默认消息; 如果无法找到消息，`NoSuchMessageException`则抛出a。
- `String getMessage(MessageSourceResolvable resolvable, Locale locale)`：前面方法中使用的所有属性也包装在一个名为的类中`MessageSourceResolvable`，您可以使用此方法。

当`ApplicationContext`被加载时，它自动搜索`MessageSource` 在上下文中定义的bean。bean必须具有名称`messageSource`。如果找到这样的bean，则对前面方法的所有调用都被委托给消息源。如果未找到任何消息源，则`ApplicationContext`尝试查找包含具有相同名称的bean的父级。如果是，它使用该bean作为`MessageSource`。如果 `ApplicationContext`找不到任何消息源，`DelegatingMessageSource`则实例化为空 以便能够接受对上面定义的方法的调用。

春天提供了两种`MessageSource`实现方式，`ResourceBundleMessageSource`和 `StaticMessageSource`。两者都是`HierarchicalMessageSource`为了进行嵌套消息传递而实现的。在`StaticMessageSource`很少使用，但提供了编程的方式向消息源添加消息。在`ResourceBundleMessageSource`被示出在下面的例子：

```xml
<beans>
    <bean id="messageSource"
            class="org.springframework.context.support.ResourceBundleMessageSource">
        <property name="basenames">
            <list>
                <value>format</value>
                <value>exceptions</value>
                <value>windows</value>
            </list>
        </property>
    </bean>
</beans>
```

在这个例子中，假设你在你的类路径称为定义了三种资源包`format`，`exceptions`和`windows`。任何解决消息的请求都将以JDK标准的方式处理，通过ResourceBundles解析消息。出于示例的目的，假设上述两个资源包文件的内容是......

```bash
# in format.properties
message=Alligators rock!
```

```bash
# in format.properties
message=Alligators rock!
# in exceptions.properties
argument.required=The {0} argument is required.
```

`MessageSource`下一个示例中显示了执行该功能的程序。请记住，所有`ApplicationContext`实现都是`MessageSource` 实现，因此可以强制转换为`MessageSource`接口。

```java
public static void main(String[] args) {
    MessageSource resources = new ClassPathXmlApplicationContext("beans.xml");
    String message = resources.getMessage("message", null, "Default", null);
    System.out.println(message);
}
```

上述程序产生的结果将是......

```bash
Alligators rock!
```

总而言之，它`MessageSource`是在一个名为的文件中定义的，该文件`beans.xml`存在于类路径的根目录中。该`messageSource`bean定义是指通过它的一些资源包的`basenames`属性。这是在列表中传递的三个文件`basenames`属性存在于你的classpath根目录的文件，被称为`format.properties`，`exceptions.properties`和 `windows.properties`分别。

下一个示例显示传递给消息查找的参数; 这些参数将转换为字符串并插入到查找消息中的占位符中。

```xml
<beans>

    <!-- this MessageSource is being used in a web application -->
    <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
        <property name="basename" value="exceptions"/>
    </bean>

    <!-- lets inject the above MessageSource into this POJO -->
    <bean id="example" class="com.foo.Example">
        <property name="messages" ref="messageSource"/>
    </bean>

</beans>
```

```java
public class Example {

    private MessageSource messages;

    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    public void execute() {
        String message = this.messages.getMessage("argument.required",
            new Object [] {"userDao"}, "Required", null);
        System.out.println(message);
    }
}
```

调用该`execute()`方法得到的结果将是......

```bash
The userDao argument is required.
```

关于国际化（i18n），Spring的各种`MessageSource` 实现遵循与标准JDK相同的区域设置解析和回退规则 `ResourceBundle`。总之，和继续该示例`messageSource`先前定义的，如果你想解析British（消息`en-GB`）语言环境中，您将创建文件名为`format_en_GB.properties`，`exceptions_en_GB.properties`和`windows_en_GB.properties`分别。

通常，区域设置解析由应用程序的周围环境管理。在此示例中，将手动指定将解析（英国）消息的区域设置。

```bash
# in exceptions_en_GB.properties
argument.required=Ebagum lad, the {0} argument is required, I say, required.
```

```java
public static void main(final String[] args) {
    MessageSource resources = new ClassPathXmlApplicationContext("beans.xml");
    String message = resources.getMessage("argument.required",
        new Object [] {"userDao"}, "Required", Locale.UK);
    System.out.println(message);
}
```

运行上述程序产生的结果将是......

```bash
Ebagum lad, the 'userDao' argument is required, I say, required.
```

您还可以使用该`MessageSourceAware`界面获取对`MessageSource`已定义的任何内容的引用 。在创建和配置bean时，将使用应用程序上下文注入`ApplicationContext`实现`MessageSourceAware`接口的任何bean `MessageSource`。

*作为替代ResourceBundleMessageSource，Spring提供了一个 ReloadableResourceBundleMessageSource类。此变体支持相同的捆绑文件格式，但比基于标准JDK的ResourceBundleMessageSource实现更灵活 。*特别是，它允许从任何Spring资源位置（不仅仅是从类路径）读取文件，并支持bundle属性文件的热重新加载（同时有效地在它们之间缓存它们）。查看`ReloadableResourceBundleMessageSource`javadocs了解详细信息。

### 7.15.2标准和自定义事件

`ApplicationContext`通过`ApplicationEvent` 类和`ApplicationListener`接口提供事件处理。如果将实现`ApplicationListener`接口的bean 部署到上下文中，则每次 `ApplicationEvent`将其发布到该`ApplicationContext`bean时，都会通知该bean。从本质上讲，这是标准的*Observer*设计模式。

从Spring 4.2开始，事件基础结构得到了显着改进，并提供了基于注释的模型 以及发布任意事件的能力，这是一个不一定从中扩展的对象`ApplicationEvent`。当发布这样的对象时，我们将它包装在一个事件中。

Spring提供以下标准事件：

**表7.7。内置事件**

| 事件                    | 说明                                                         |
| ----------------------- | ------------------------------------------------------------ |
| `ContextRefreshedEvent` | Published when the `ApplicationContext` is initialized or refreshed, for example, using the `refresh()` method on the `ConfigurableApplicationContext` interface. "Initialized" here means that all beans are loaded, post-processor beans are detected and activated, singletons are pre-instantiated, and the `ApplicationContext` object is ready for use. As long as the context has not been closed, a refresh can be triggered multiple times, provided that the chosen `ApplicationContext`actually supports such "hot" refreshes. For example, `XmlWebApplicationContext` supports hot refreshes, but`GenericApplicationContext` does not. |
| `ContextStartedEvent`   | 在`ApplicationContext`启动时发布，使用界面`start()`上的方法 `ConfigurableApplicationContext`。这里的“已启动”意味着所有`Lifecycle` bean都会收到明确的启动信号。通常，此信号用于在显式停止后重新启动Bean，但它也可用于启动尚未为自动启动配置的组件，例如，尚未在初始化时启动的组件。 |
| `ContextStoppedEvent`   | `ApplicationContext`停止时发布，使用界面`stop()`上的方法 `ConfigurableApplicationContext`。这里的“停止”意味着所有`Lifecycle` bean都会收到明确的停止信号。可以通过`start()`呼叫重新启动已停止的上下文 。 |
| `ContextClosedEvent`    | `ApplicationContext`关闭时发布，使用界面`close()`上的方法 `ConfigurableApplicationContext`。这里的“封闭”意味着所有单身豆都被销毁。封闭的环境达到了生命的终点; 它无法刷新或重新启动。 |
| `RequestHandledEvent`   | 一个特定于Web的事件，告诉所有bean已经为HTTP请求提供服务。请求完成*后*发布此事件。此事件仅适用于使用Spring的Web应用程序`DispatcherServlet`。 |

您还可以创建和发布自己的自定义事件。这个例子演示了一个扩展Spring的`ApplicationEvent`基类的简单类：

```java
public class BlackListEvent extends ApplicationEvent {

    private final String address;
    private final String content;

    public BlackListEvent(Object source, String address, String content) {
        super(source);
        this.address = address;
        this.content = content;
    }

    // accessor and other methods...
}
```

要发布自定义`ApplicationEvent`，请在`publishEvent()`方法上调用该方法 `ApplicationEventPublisher`。通常，这是通过创建一个实现`ApplicationEventPublisherAware`并将其注册为Spring bean 的类来完成的 。以下示例演示了这样一个类：

```java
public class EmailService implements ApplicationEventPublisherAware {

    private List<String> blackList;
    private ApplicationEventPublisher publisher;

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void sendEmail(String address, String content) {
        if (blackList.contains(address)) {
            publisher.publishEvent(new BlackListEvent(this, address, content));
            return;
        }
        // send email...
    }
}
```

在配置时，Spring容器将检测到该`EmailService`实现 `ApplicationEventPublisherAware`并将自动调用 `setApplicationEventPublisher()`。实际上，传入的参数将是Spring容器本身; 您只需通过其`ApplicationEventPublisher`界面与应用程序上下文进行 交互。

要接收自定义`ApplicationEvent`，请创建一个实现 `ApplicationListener`并将其注册为Spring bean的类。以下示例演示了这样一个类：

```java
public class BlackListNotifier implements ApplicationListener<BlackListEvent> {

    private String notificationAddress;

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }

    public void onApplicationEvent(BlackListEvent event) {
        // notify appropriate parties via notificationAddress...
    }
}
```

请注意，`ApplicationListener`通常使用自定义事件的类型进行参数化`BlackListEvent`。这意味着该`onApplicationEvent()`方法可以保持类型安全，避免任何向下转换的需要。您可以根据需要注册任意数量的事件侦听器，但请注意，默认情况下，事件侦听器会同步接收事件。这意味着该`publishEvent()`方法将阻塞，直到所有侦听器都已完成对事件的处理。这种同步和单线程方法的一个优点是，当侦听器接收到事件时，如果事务上下文可用，它将在发布者的事务上下文内运行。如果需要另一个事件发布策略，请参阅Spring `ApplicationEventMulticaster`接口的javadoc 。

以下示例显示了用于注册和配置上述每个类的bean定义：

```xml
<bean id="emailService" class="example.EmailService">
    <property name="blackList">
        <list>
            <value>known.spammer@example.org</value>
            <value>known.hacker@example.org</value>
            <value>john.doe@example.org</value>
        </list>
    </property>
</bean>

<bean id="blackListNotifier" class="example.BlackListNotifier">
    <property name="notificationAddress" value="blacklist@example.org"/>
</bean>
```

总而言之，当调用bean 的`sendEmail()`方法时`emailService`，如果有任何电子邮件应该被列入黑名单，`BlackListEvent`则会发布类型的自定义事件 。所述`blackListNotifier`豆被注册为一个 `ApplicationListener`，从而接收到`BlackListEvent`，在该点它可以通知适当方。

Spring的事件机制是为在同一应用程序上下文中的Spring bean之间的简单通信而设计的。但是，对于更复杂的企业集成需求，单独维护的 [Spring Integration](https://projects.spring.io/spring-integration/)项目为构建基于众所周知的Spring编程模型的轻量级，[面向模式](https://www.enterpriseintegrationpatterns.com)，事件驱动的体系结构提供了完整的支持 。

#### 基于注解的事件侦听器

从Spring 4.2开始，可以通过`EventListener`注释在托管bean的任何公共方法上注册事件侦听器。该`BlackListNotifier`可改写如下：

```java
public class BlackListNotifier {

    private String notificationAddress;

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }

    @EventListener
    public void processBlackListEvent(BlackListEvent event) {
        // notify appropriate parties via notificationAddress...
    }
}
```

如上所示，方法签名再次声明它侦听的事件类型，但这次使用灵活的名称并且没有实现特定的侦听器接口。只要实际事件类型在其实现层次结构中解析通用参数，也可以通过泛型缩小事件类型。

如果您的方法应该监听多个事件，或者您想要根据任何参数进行定义，那么也可以在注释本身上指定事件类型：

```java
@EventListener（{ContextStartedEvent.class，ContextRefreshedEvent.class}）
 public  void handleContextStart（）{
    ...
}
```

还可以通过`condition`注释的属性添加额外的运行时过滤，该注释定义应该匹配的[`SpEL`表达式](expressions.html)以实际调用特定事件的方法。

例如，如果`content`事件的属性等于`foo`：我们的通知程序可以被重写为仅被调用：

```java
@EventListener(condition = "#blEvent.content == 'foo'")
public void processBlackListEvent(BlackListEvent blEvent) {
    // notify appropriate parties via notificationAddress...
}
```

每个`SpEL`表达式都针对专用上下文进行评估。下一个表列出了可用于上下文的项目，因此可以将它们用于条件事件处理：

**表7.8。事件SpEL可用元数据**

| 名称       | 地点     | 描述                                                         | 例                                                           |
| ---------- | -------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 事件       | 根对象   | 实际上 `ApplicationEvent`                                    | `#root.event`                                                |
| 参数数组   | 根对象   | 用于调用目标的参数（作为数组）                               | `#root.args[0]`                                              |
| *参数名称* | 评估背景 | 任何方法参数的名称。如果由于某种原因名称是不可用（例如，没有调试信息），参数名称也是在现有的`#a<#arg>` 地方*#arg*代表的说法指数（从0开始）。 | `#blEvent`或者`#a0`（也可以使用`#p0`或`#p<#arg>`表示为别名）。 |

请注意`#root.event`，即使您的方法签名实际引用已发布的任意对象，也允许您访问基础事件。

如果您需要发布一个事件作为处理另一个事件的结果，只需更改方法签名以返回应该发布的事件，例如：

```java
@EventListener
public ListUpdateEvent handleBlackListEvent(BlackListEvent event) {
    // notify appropriate parties via notificationAddress and
    // then publish a ListUpdateEvent...
}
```

异步侦听器 不支持此功能。

这个新方法将为上述方法处理的`ListUpdateEvent`每个方法发布一个新`BlackListEvent`的方法。如果您需要发布多个事件，请返回一个`Collection`事件。

#### 异步监听器

如果您希望特定侦听器异步处理事件，只需重用 常规`@Async`支持：

```java
@EventListener
@Async
public void processBlackListEvent(BlackListEvent event) {
    // BlackListEvent is processed in a separate thread
}
```

使用异步事件时请注意以下限制：

1. 如果事件侦听器抛出`Exception`它将不会传播给调用者，请检查`AsyncUncaughtExceptionHandler`更多详细信息。
2. 此类事件监听器无法发送回复。如果您需要作为处理结果发送另一个事件，请注入`ApplicationEventPublisher`以手动发送事件。

#### 订购听众

如果需要在另一个侦听器之前调用侦听器，只需将`@Order` 注释添加到方法声明中：

```java
@EventListener
@Order(42)
public void processBlackListEvent(BlackListEvent event) {
    // notify appropriate parties via notificationAddress...
}
```

#### 通用事件

您还可以使用泛型来进一步定义事件的结构。考虑 `EntityCreatedEvent<T>`where `T`是创建的实际实体的类型。您可以创建以下侦听器定义只接收`EntityCreatedEvent`了 `Person`：

```java
@EventListener
public void onPersonCreated(EntityCreatedEvent<Person> event) {
    ...
}
```

由于类型擦除，这只有在被触发的事件解析事件侦听器在其上过滤的泛型参数（类似`class PersonCreatedEvent extends EntityCreatedEvent<Person> { … }`）时才有效 。

在某些情况下，如果所有事件都遵循相同的结构（这应该是上述事件的情况），这可能会变得相当繁琐。在这种情况下，可以实现`ResolvableTypeProvider`对*引导*超出原来的运行环境提供了框架：

```java
public class EntityCreatedEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {

    public EntityCreatedEvent(T entity) {
        super(entity);
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(getSource()));
    }
}
```

这不仅适用于`ApplicationEvent`您作为事件发送的任何对象。

### 7.15.3方便地访问低级资源

为了最佳地使用和理解应用程序上下文，用户通常应该熟悉Spring的`Resource`抽象，如[第8章“ *资源*](resources.html) ”一[章](resources.html)所述 。

应用程序上下文是a `ResourceLoader`，可用于加载`Resource`s。A `Resource`本质上是一个功能更丰富的JDK类版本`java.net.URL`，实际上，在适当`Resource`的`java.net.URL`地方包装一个实例的实现。A `Resource`可以透明的方式从几乎任何位置获取低级资源，包括从类路径，文件系统位置，任何可用标准URL描述的位置，以及一些其他变体。如果资源位置字符串是没有任何特殊前缀的简单路径，那么这些资源来自特定且适合于实际应用程序上下文类型。

您可以配置部署到应用程序上下文中的bean来实现特殊的回调接口，`ResourceLoaderAware`在初始化时自动回调，应用程序上下文本身作为传入`ResourceLoader`。您还可以公开`Resource`用于访问静态资源的类型属性; 它们将像任何其他属性一样注入其中。您可以将这些`Resource`属性指定为简单的String路径，并依赖于`PropertyEditor`上下文自动注册的特殊JavaBean ，以便`Resource`在部署Bean时将这些文本字符串转换为实际对象。

提供给`ApplicationContext`构造函数的位置路径实际上是资源字符串，并且以简单的形式适当地处理特定的上下文实现。`ClassPathXmlApplicationContext`将简单的位置路径视为类路径位置。您还可以使用具有特殊前缀的位置路径（资源字符串）来强制从类路径或URL加载定义，而不管实际的上下文类型如何。

### 7.15.4 Web应用程序的便捷ApplicationContext实例化

您可以`ApplicationContext`使用例如a以声明方式创建实例 `ContextLoader`。当然，您也可以`ApplicationContext`使用其中一个`ApplicationContext`实现以编程方式创建实例。

您可以`ApplicationContext`使用`ContextLoaderListener`以下注册：

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/daoContext.xml /WEB-INF/applicationContext.xml</param-value>
</context-param>

<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

监听器检查`contextConfigLocation`参数。如果参数不存在，则侦听器将`/WEB-INF/applicationContext.xml`默认使用。当参数*确实*存在时，侦听器使用预定义的分隔符（逗号，分号和空格）分隔String，并将值用作将搜索应用程序上下文的位置。还支持Ant样式的路径模式。示例`/WEB-INF/*Context.xml`适用于名称以“Context.xml”结尾的所有文件，驻留在“WEB-INF”目录中，以及`/WEB-INF/**/*Context.xml`“WEB-INF”的任何子目录中的所有此类文件。

### 7.15.5将Spring ApplicationContext部署为Java EE RAR文件

可以将Spring ApplicationContext部署为RAR文件，将上下文及其所有必需的bean类和库JAR封装在Java EE RAR部署单元中。这相当于引导一个独立的ApplicationContext，它只是托管在Java EE环境中，能够访问Java EE服务器设施。RAR部署是部署无头WAR文件的场景的更自然的替代方案，实际上是没有任何HTTP入口点的WAR文件，仅用于在Java EE环境中引导Spring ApplicationContext。

RAR部署非常适用于不需要HTTP入口点但仅包含消息端点和预定作业的应用程序上下文。在这样的上下文中的Bean可以使用应用程序服务器资源，例如JTA事务管理器和JNDI绑定的JDBC DataSources和JMS ConnectionFactory实例，也可以使用Spring的标准事务管理以及JNDI和JMX支持工具向平台的JMX服务器注册。应用程序组件还可以通过Spring的`TaskExecutor`抽象与应用程序服务器的JCA WorkManager交互。

查看[`SpringContextResourceAdapter`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/jca/context/SpringContextResourceAdapter.html) 类的javadoc以 获取RAR部署中涉及的配置详细信息。

*对于将Spring ApplicationContext简单部署为Java EE RAR文件：*将所有应用程序类打包到RAR文件中，该文件是具有不同文件扩展名的标准JAR文件。将所有必需的库JAR添加到RAR存档的根目录中。添加“META-INF / ra.xml”部署描述符（如`SpringContextResourceAdapter`s javadoc所示）和相应的Spring XML bean定义文件（通常为“META-INF / applicationContext.xml”），并删除生成的RAR文件进入应用程序服务器的部署目录。