# 7.6 自定义Bean


### 7.6.1生命周期回调

要与容器的bean生命周期管理进行交互，可以实现Spring `InitializingBean`和`DisposableBean`接口。容器调用 `afterPropertiesSet()`前者，`destroy()`后者允许bean在初始化和销毁bean时执行某些操作。

JSR-250 `@PostConstruct`和`@PreDestroy`注释通常被认为是在现代Spring应用程序中接收生命周期回调的最佳实践。使用这些注释意味着您的bean不会耦合到Spring特定的接口。

在内部，Spring Framework使用`BeanPostProcessor`实现来处理它可以找到的任何回调接口并调用适当的方法。如果您需要自定义功能或其他生命周期行为Spring不提供开箱即用的功能，您可以`BeanPostProcessor`自己实现。

除了初始化和销毁回调之外，Spring管理的对象还可以实现`Lifecycle`接口，以便这些对象可以参与由容器自身生命周期驱动的启动和关闭过程。

本节描述了生命周期回调接口。

#### 初始化回调

该`org.springframework.beans.factory.InitializingBean`接口允许后对bean的所有必要属性容器设置一个bean来执行初始化的工作。的`InitializingBean`接口规定了一个方法：

```java
void afterPropertiesSet() throws Exception;
```

建议您不要使用该`InitializingBean`接口，因为它会不必要地将代码耦合到Spring。或者，使用[`@PostConstruct`](beans.html#beans-postconstruct-and-predestroy-annotations)注释或指定POJO初始化方法。对于基于XML的配置元数据，可以使用该`init-method`属性指定具有void无参数签名的方法的名称。使用Java配置，您可以使用`initMethod`属性`@Bean`。

```xml
<bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
```

```java
public class ExampleBean {

    public void init() {
        // do some initialization work
    }
}
```

完全一样

```xml
<bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
```

```java
public class AnotherExampleBean implements InitializingBean {

    public void afterPropertiesSet() {
        // do some initialization work
    }
}
```

但不会将代码耦合到Spring。

#### 销毁回调

实现`org.springframework.beans.factory.DisposableBean`接口允许bean在包含它的容器被销毁时获得回调。的 `DisposableBean`接口规定了一个方法：

```java
void destroy() throws Exception;
```

建议您不要使用`DisposableBean`回调接口，因为它会不必要地将代码耦合到Spring。或者，使用[`@PreDestroy`](beans.html#beans-postconstruct-and-predestroy-annotations)注释或指定bean定义支持的泛型方法。使用基于XML的配置元数据，您可以使用该`destroy-method`属性`<bean/>`。使用Java配置，您可以使用`destroyMethod`属性`@Bean`。

```xml
<bean id="exampleInitBean" class="examples.ExampleBean" destroy-method="cleanup"/>
```

```java
public class ExampleBean {

    public void cleanup() {
        // do some destruction work (like releasing pooled connections)
    }
}
```

与以下内容完全相同：

```xml
<bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
```

```java
public class AnotherExampleBean implements DisposableBean {

    public void destroy() {
        // do some destruction work (like releasing pooled connections)
    }
}
```

但不会将代码耦合到Spring。

可以为元素的`destroy-method`属性`<bean>`分配一个特殊 `(inferred)`值，该值指示Spring自动检测特定bean类（任何实现或将匹配的类）的公共`close`或 `shutdown`方法 。也可以在元素的属性上设置此特殊 值， 以将此行为应用于整组bean。请注意，这是Java配置的默认行为。`java.lang.AutoCloseable``java.io.Closeable``(inferred)``default-destroy-method``<beans>。`

#### 默认初始化和销毁方法

当你写的初始化和销毁不使用Spring的具体方法回调`InitializingBean`和`DisposableBean`回调接口，你通常写有名字，如方法`init()`，`initialize()`，`dispose()`，等等。理想情况下，此类生命周期回调方法的名称在项目中是标准化的，以便所有开发人员使用相同的方法名称并确保一致性。

您可以将Spring容器配置`look`为命名初始化并销毁*每个* bean 上的回调方法名称。这意味着，作为应用程序开发人员，您可以编写应用程序类并使用调用的初始化回调`init()`，而无需为`init-method="init"`每个bean定义配置属性。Spring IoC容器在创建bean时调用该方法（并且符合前面描述的标准生命周期回调协定）。此功能还强制执行初始化和销毁方法回调的一致命名约定。

假设您的初始化回调方法已命名，`init()`并且命名了destroy回调方法`destroy()`。您的类将类似于以下示例中的类。

```Java
public class DefaultBlogService implements BlogService {

    private BlogDao blogDao;

    public void setBlogDao(BlogDao blogDao) {
        this.blogDao = blogDao;
    }

    // this is (unsurprisingly) the initialization callback method
    public void init() {
        if (this.blogDao == null) {
            throw new IllegalStateException("The [blogDao] property must be set.");
        }
    }
}
```

```xml
<beans default-init-method="init">

    <bean id="blogService" class="com.foo.DefaultBlogService">
        <property name="blogDao" ref="blogDao" />
    </bean>

</beans>
```

`default-init-method`顶级`<beans/>`元素属性上存在属性会导致Spring IoC容器将`init`bean上的方法识别为初始化方法回调。当bean被创建和组装时，如果bean类具有这样的方法，则在适当的时候调用它。

您可以通过使用`default-destroy-method`顶级`<beans/>`元素上的属性来类似地（在XML中）配置destroy方法回调 。

如果现有的bean类已经具有与约定一致的变量命名的回调方法，则可以通过使用 自身的`init-method`和`destroy-method`属性指定（在XML中，即）方法名称来覆盖缺省值`<bean/>`。

Spring容器保证在为bean提供所有依赖项后立即调用已配置的初始化回调。因此，在原始bean引用上调用初始化回调，这意味着AOP拦截器等尚未应用于bean。*首先*完全创建目标bean ， *然后*应用具有其拦截器链的AOP代理（例如）。如果目标bean和代理是分开定义的，那么您的代码甚至可以绕过代理与原始目标bean交互。因此，将拦截器应用于init方法是不一致的，因为这样做会将目标bean的生命周期与其代理/拦截器耦合在一起，并在代码直接与原始目标bean交互时留下奇怪的语义。

#### 合生命周期机制

从Spring 2.5开始，您有三个控制bean生命周期行为的选项：[`InitializingBean`和[`DisposableBean`回调接口; 习俗 `init()`和`destroy()`方法; 和`@PostConstruct`和`@PreDestroy` 注解。您可以组合这些机制来控制给定的bean。

如果为bean配置了多个生命周期机制，并且每个机制配置了不同的方法名称，则每个配置的方法都按下面列出的顺序执行。但是，如果`init()`为多个这些生命周期机制配置了相同的方法名称（例如， 对于初始化方法），则该方法将执行一次，如上一节中所述。

为同一个bean配置的多个生命周期机制具有不同的初始化方法，如下所示：

- 用注释方法注释 `@PostConstruct`
- `afterPropertiesSet()`由`InitializingBean`回调接口 定义
- 自定义配置的`init()`方法

Destroy方法以相同的顺序调用：

- 用注释方法注释 `@PreDestroy`
- `destroy()`由`DisposableBean`回调接口 定义
- 自定义配置的`destroy()`方法

#### 启动和关闭回调

该`Lifecycle`接口为具有自己的生命周期要求的任何对象定义了基本方法（例如，启动和停止某些后台进程）：

```java
public interface Lifecycle {

    void start();

    void stop();

    boolean isRunning();
}
```

任何Spring管理的对象都可以实现该接口。然后，当 `ApplicationContext`自身接收到启动和停止信号时，例如在运行时接收停止/重启场景时，它会将这些调用级联到`Lifecycle`该上下文中定义的所有实现。它通过委托给`LifecycleProcessor`：

```java
public interface LifecycleProcessor extends Lifecycle {

    void onRefresh();

    void onClose();
}
```

请注意，`LifecycleProcessor`它本身是`Lifecycle` 接口的扩展。它还添加了另外两种方法来响应刷新和关闭的上下文。

请注意，常规`org.springframework.context.Lifecycle`接口只是显式启动/停止通知的简单合约，并不意味着在上下文刷新时自动启动。考虑实现`org.springframework.context.SmartLifecycle`对特定bean的自动启动（包括启动阶段）的细粒度控制。此外，请注意，在销毁之前不能保证停止通知：在常规关闭时，所有`Lifecycle`bean将在传播一般销毁回调之前首先收到停止通知; 但是，在上下文生命周期中的热刷新或中止刷新尝试时，只会调用destroy方法。

启动和关闭调用的顺序非常重要。如果任何两个对象之间存在“依赖”关系，则依赖方将*在*其依赖*之后*启动，并且它将*在*其依赖*之前*停止。但是，有时直接依赖性是未知的。您可能只知道某种类型的对象应该在另一种类型的对象之前开始。在这些情况下，`SmartLifecycle`接口定义了另一个选项，即`getPhase()`在其超级接口上定义的方法 `Phased`。

```java
public interface Phased {

    int getPhase();
}
```

```java
public interface SmartLifecycle extends Lifecycle, Phased {

    boolean isAutoStartup();

    void stop(Runnable callback);
}
```

启动时，具有最低相位的对象首先开始，停止时，遵循相反的顺序。因此，实现`SmartLifecycle`和`getPhase()`返回其方法的对象`Integer.MIN_VALUE`将是第一个开始和最后一个停止的对象。在频谱的另一端，相位值 `Integer.MAX_VALUE`将指示对象应该最后启动并首先停止（可能因为它依赖于正在运行的其他进程）。在考虑相位值时，同样重要的是要知道任何`Lifecycle`未实现的“正常” 对象的默认阶段 `SmartLifecycle`都是0.因此，任何负相位值都表示对象应该在那些标准组件之前启动（并在之后停止）对于任何正相值，反之亦然。

正如您所看到的，定义的stop方法`SmartLifecycle`接受回调。任何实现*必须*`run()`在该实现的关闭过程完成后调用该回调的方法。这样就可以在必要时启用异步关闭，因为`LifecycleProcessor`接口 的默认实现`DefaultLifecycleProcessor`将等待每个阶段内的对象组的超时值来调用该回调。默认的每阶段超时为30秒。您可以通过在上下文中定义名为“lifecycleProcessor”的bean来覆盖缺省生命周期处理器实例。如果您只想修改超时，那么定义以下内容就足够了：

```xml
<bean id="lifecycleProcessor" class="org.springframework.context.support.DefaultLifecycleProcessor">
    <!-- timeout value in milliseconds -->
    <property name="timeoutPerShutdownPhase" value="10000"/>
</bean>
```

如前所述，该`LifecycleProcessor`接口还定义了用于刷新和关闭上下文的回调方法。后者将简单地驱动关闭过程，就像`stop()`已经显式调用一样，但是当上下文关闭时它将发生。另一方面，'refresh'回调启用了`SmartLifecycle`bean的另一个功能 。刷新上下文（在所有对象都已实例化并初始化之后），将调用该回调，此时默认生命周期处理器将检查每个`SmartLifecycle`对象的`isAutoStartup()`方法返回的布尔值 。如果为“true”，则该对象将在该点启动，而不是等待显式调用上下文或其自身`start()`方法（与上下文刷新不同，上下文启动不会自动发生在标准上下文实现中）。“阶段”值以及任何“依赖”关系将以与上述相同的方式确定启动顺序。

#### 在非Web应用程序中正常关闭Spring IoC容器

本节仅适用于非Web应用程序。Spring的基于Web的 `ApplicationContext`实现已经具有代码，可以在关闭相关Web应用程序时正常关闭Spring IoC容器。

如果您在非Web应用程序环境中使用Spring的IoC容器; 例如，在富客户端桌面环境中; 您在JVM上注册了一个关闭钩子。这样做可确保正常关闭并在单例bean上调用相关的destroy方法，以便释放所有资源。当然，您仍然必须正确配置和实现这些destroy回调。

要注册关闭挂钩，请调用接口`registerShutdownHook()`上声明的方法`ConfigurableApplicationContext`：

```java
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class Boot {

    public static void main(final String[] args) throws Exception {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();

        // app runs here...

        // main method exits, hook is called prior to the app shutting down...
    }
}
```

### 7.6.2 ApplicationContextAware和BeanNameAware

当`ApplicationContext`创建实现`org.springframework.context.ApplicationContextAware`接口的对象实例时，将 为该实例提供对该实例的引用`ApplicationContext`。

```java
public interface ApplicationContextAware {

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
```

因此，bean可以`ApplicationContext`通过`ApplicationContext`接口以编程方式操作创建它们的方法，或者通过将引用转换为此接口的已知子类（例如`ConfigurableApplicationContext`，公开其他功能）来操作。一种用途是对其他bean进行编程检索。有时这种能力很有用; 但是，通常你应该避免它，因为它将代码耦合到Spring并且不遵循Inversion of Control样式，其中协作者作为属性提供给bean。其他方法 `ApplicationContext`提供对文件资源的访问，发布应用程序事件和访问`MessageSource`

从Spring 2.5开始，自动装配是另一种获取参考的方法 `ApplicationContext`。“传统” `constructor`和`byType`自动装配模式。可以分别为`ApplicationContext`构造函数参数或setter方法参数提供类型依赖性 。为了获得更大的灵活性，包括自动装配字段和多参数方法的能力，请使用基于注释的新自动装配功能。如果这样做，`ApplicationContext`则自动装配到字段，构造函数参数或方法参数中，`ApplicationContext`如果相关字段，构造函数或方法带有`@Autowired`注释，则该参数期望该类型。

当`ApplicationContext`创建实现`org.springframework.beans.factory.BeanNameAware`接口的类时，将为 该类提供对其关联对象定义中定义的名称的引用。

```java
public interface BeanNameAware {

    void setBeanName(String name) throws BeansException;
}
```

在普通bean属性填充之后但在初始化回调之前（例如`InitializingBean` *afterPropertiesSet*或自定义init方法）之前调用回调。

### 7.6.3其他Aware接口

除了`ApplicationContextAware`和`BeanNameAware`上面所讨论的，Spring提供了一系列的`Aware`回调接口，允许bean指示，他们需要一定的集装箱*基础设施*的依赖。最重要的`Aware` 接口总结如下 - 作为一般规则，名称是依赖类型的良好指示：

**表7.4。Aware接口**

| 名称                             | 注入依赖                                                     | 解释在......                                                 |
| -------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `ApplicationContextAware`        | 声明 `ApplicationContext`                                    | 第7.6.2节“ApplicationContextAware和BeanNameAware” |
| `ApplicationEventPublisherAware` | 封闭的事件发布者 `ApplicationContext`                        | 第7.15节“ApplicationContext的附加功能” |
| `BeanClassLoaderAware`           | 用于加载bean类的类加载器。                                   | 第7.3.2节“实例化bean”      |
| `BeanFactoryAware`               | 声明 `BeanFactory`                                           | 第7.6.2节“ApplicationContextAware和BeanNameAware” |
| `BeanNameAware`                  | 声明bean的名称                                               | 第7.6.2节“ApplicationContextAware和BeanNameAware” |
| `BootstrapContextAware`          | 资源适配器`BootstrapContext`的容器中，仅在JCA知道运行。通常可用`ApplicationContext`小号 | 第32章，*JCA CCI*                                |
| `LoadTimeWeaverAware`            | 定义的*weaver*用于在加载时处理类定义                         | 第11.8.4节“在Spring框架中使用AspectJ进行加载时编织” |
| `MessageSourceAware`             | 用于解析消息的已配置策略（支持参数化和国际化）               | 第7.15节“ApplicationContext的附加功能” |
| `NotificationPublisherAware`     | Spring JMX通知发布者                                         | 第31.7节“通知”                 |
| `PortletConfigAware`             | 当前`PortletConfig`容器运行。仅在Web感知弹簧中有效`ApplicationContext` | 第25章，*Portlet MVC框架*                    |
| `PortletContextAware`            | 当前`PortletContext`容器运行。仅在Web感知弹簧中有效`ApplicationContext` | 第25章，*Portlet MVC框架*                    |
| `ResourceLoaderAware`            | 配置的加载程序，用于对资源进行低级访问                       | 第8章，*资源*                             |
| `ServletConfigAware`             | 当前`ServletConfig`容器运行。仅在Web感知弹簧中有效`ApplicationContext` | 第22章，*Web MVC框架*                            |
| `ServletContextAware`            | 当前`ServletContext`容器运行。仅在Web感知弹簧中有效`ApplicationContext` | 第22章，*Web MVC框架*                                                               |

再次注意，这些接口的使用将您的代码与Spring API联系起来，并且不遵循Inversion of Control样式。因此，建议将它们用于需要以编程方式访问容器的基础结构bean。
