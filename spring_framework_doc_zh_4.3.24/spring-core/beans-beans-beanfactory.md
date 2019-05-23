# 7.16 BeanFactory

该`BeanFactory`API提供了春天的IoC功能的基本依据。其特定合同主要用于与Spring的其他部分和相关的第三方框架集成，其`DefaultListableBeanFactory`实现是更高级别`GenericApplicationContext`容器中的关键委托。

`BeanFactory`以及相关的接口，如`BeanFactoryAware`，`InitializingBean`， `DisposableBean`对于其他框架组件的重要结合点：不需要任何注解，甚至反映，他们允许容器及其组件之间的非常有效的互动。应用程序级bean可以使用相同的回调接口，但通常更喜欢通过注释或通过编程配置进行声明性依赖注入。

请注意，核心`BeanFactory`API级别及其`DefaultListableBeanFactory` 实现不会对配置格式或要使用的任何组件注释做出假设。所有这些口味的通过扩展，例如进来`XmlBeanDefinitionReader`和`AutowiredAnnotationBeanPostProcessor`，共享操作`BeanDefinition`对象作为核心元数据表示。这是使Spring的容器如此灵活和可扩展的本质。

以下部分解释了之间的差异`BeanFactory`和 `ApplicationContext`容器级别和引导的意义。

### 7.16.1 BeanFactory或ApplicationContext？

`ApplicationContext`除非你有充分的理由不使用，否则使用an ， `GenericApplicationContext`并将其子类`AnnotationConfigApplicationContext` 作为自定义引导的常用实现。这些是Spring用于所有常见目的的核心容器的主要入口点：加载配置文件，触发类路径扫描，以编程方式注册bean定义和带注释的类。

因为`ApplicationContext`包含a的所有功能`BeanFactory`，所以`BeanFactory`除了需要完全控制bean处理的场景之外，通常建议使用它。在`ApplicationContext`诸如`GenericApplicationContext`实现之类的 过程中，将按照约定（即通过bean名称或bean类型）检测几种bean，特别是后处理器，而普通`DefaultListableBeanFactory`对于任何特殊bean都是不可知的。

对于许多扩展容器功能，例如注释处理和AOP代理，`BeanPostProcessor`扩展点 是必不可少的。如果仅使用普通`DefaultListableBeanFactory`处理器，则默认情况下不会检测到并激活此类后处理器。这种情况可能令人困惑，因为您的bean配置实际上没有任何问题; 它更像是在这种情况下需要通过附加设置完全自举的容器。

下表列出了提供的功能`BeanFactory`和 `ApplicationContext`接口和实现。

**表7.9。特征矩阵**

| 特征                               | `BeanFactory` | `ApplicationContext` |
| ---------------------------------- | ------------- | -------------------- |
| Bean实例化/布线                    | 是            | 是                   |
| 集成的生命周期管理                 | 没有          | 是                   |
| 自动`BeanPostProcessor`注册        | 没有          | 是                   |
| 自动`BeanFactoryPostProcessor`注册 | 没有          | 是                   |
| 方便`MessageSource`访问（内化）    | 没有          | 是                   |
| 内置`ApplicationEvent`发布机制     | 没有          | 是                   |

要使用a显式注册bean后处理器`DefaultListableBeanFactory`，您需要以编程方式调用`addBeanPostProcessor`：

```java
DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
// populate the factory with bean definitions

// now register any needed BeanPostProcessor instances
factory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
factory.addBeanPostProcessor(new MyBeanPostProcessor());

// now start using the factory
```

要应用于`BeanFactoryPostProcessor`plain `DefaultListableBeanFactory`，您需要调用其`postProcessBeanFactory`方法：

```java
DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
reader.loadBeanDefinitions(new FileSystemResource("beans.xml"));

// bring in some property values from a Properties file
PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
cfg.setLocation(new FileSystemResource("jdbc.properties"));

// now actually do the replacement
cfg.postProcessBeanFactory(factory);
```

在这两种情况下，显式注册步骤都不方便，这就是为什么各种`ApplicationContext`变体优于 `DefaultListableBeanFactory`Spring支持的应用程序中的简单，特别是在典型的企业设置中依赖`BeanFactoryPostProcessor`s和`BeanPostProcessor`s来扩展容器功能时。

An `AnnotationConfigApplicationContext`具有开箱即用的所有通用注释后处理器，并且可以通过配置注释（例如）在封面下方引入额外的处理器`@EnableTransactionManagement`。在Spring的基于注释的配置模型的抽象级别，bean后处理器的概念变成仅仅是内部容器细节。

### 7.16.2 连接单例

最好以依赖注入（DI）方式编写大多数应用程序代码，其中该代码由Spring IoC容器提供，在创建容器时由容器提供自己的依赖关系，并且完全不知道容器。但是，对于有时需要将其他代码绑定在一起的小型胶代码层，有时需要对Spring IoC容器进行单例（或准单例）样式访问。例如，第三方代码可能会尝试直接构造新对象（`Class.forName()`如果由第三方代码构造的对象是一个小的存根或代理，然后使用单一样式访问Spring IoC容器来获得真实的要委托的对象，然后仍然为大多数代码（来自容器的对象）实现了控制的反转。因此，大多数代码仍然没有意识到容器或它是如何被访问的，并且仍然与其他代码分离，并带来所有后续的好处。EJB也可以使用这种存根/代理方法委托从Spring IoC容器中检索的普通Java实现对象。虽然Spring IoC容器本身理想情况下不必是单例，`SessionFactory`）为每个bean使用自己的非单例Spring IoC容器。

