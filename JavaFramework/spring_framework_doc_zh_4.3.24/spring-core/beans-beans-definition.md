# 7.3 Bean概述

Spring IoC容器管理一个或多个*bean*。这些bean是使用您提供给容器的配置元数据创建的，例如，以XML `<bean/>`定义的形式 。

在容器本身内，这些bean定义表示为`BeanDefinition` 对象，其中包含（以及其他信息）以下元数据：

- *包限定的类名：*通常是正在定义的bean的实际实现类。
- Bean行为配置元素，说明bean在容器中的行为方式（范围，生命周期回调等）。
- 引用bean执行其工作所需的其他bean; 这些引用也称为*协作者*或*依赖项*。
- 要在新创建的对象中设置的其他配置设置，例如，在管理连接池的Bean中使用的连接数，或池的大小限制。

此元数据转换为构成每个bean定义的一组属性。

**Table 7.1. The bean definition**

| Property                 | Explained in…                                                |
| ------------------------ | ------------------------------------------------------------ |
| class                    | Section 7.3.2, “Instantiating beans” |
| name                     | Section 7.3.1, “Naming beans”  |
| scope                    | Section 7.5, “Bean scopes” |
| constructor arguments    | Section 7.4.1, “Dependency Injection” |
| properties               | Section 7.4.1, “Dependency Injection”|
| autowiring mode          | Section 7.4.5, “Autowiring collaborators” |
| lazy-initialization mode | Section 7.4.4, “Lazy-initialized beans” |
| initialization method    | the section called “Initialization callbacks” |
| destruction method       | the section called “Destruction callbacks” |

了包含有关如何创建特定bean的信息的bean定义之外，这些`ApplicationContext`实现还允许用户注册在容器外部创建的现有对象。这是通过`getBeanFactory()`返回BeanFactory实现的方法访问ApplicationContext的BeanFactory来完成的`DefaultListableBeanFactory`。`DefaultListableBeanFactory` 通过方法`registerSingleton(..)`和 支持这种注册`registerBeanDefinition(..)`。但是，典型应用程序仅适用于通过元数据bean定义定义的bean。

每个bean都有一个或多个标识符。这些标识符在托管bean的容器中必须是唯一的。bean通常只有一个标识符，但如果它需要多个标识符，则额外的标识符可以被视为别名。

在基于XML的配置元数据中，使用`id`和/或`name`属性指定bean标识符。该`id`属性允许您指定一个id。通常，这些名称是字母数字（'myBean'，'fooService'等），但也可能包含特殊字符。如果要向bean引入其他别名，还可以在`name` 属性中指定它们，用逗号（`,`），分号（`;`）或空格分隔。作为历史记录，在Spring 3.1之前的版本中，该`id`属性被定义为一种`xsd:ID`类型，它约束了可能的字符。从3.1开始，它被定义为一种`xsd:string`类型。请注意，`id`容器仍然强制实施bean 唯一性，但不再是XML解析器。

您不需要为bean提供名称或ID。如果没有显式提供名称或标识，则容器会为该bean生成唯一的名称。但是，如果要通过名称引用该bean，则必须通过使用`ref`元素或 Service Locator样式查找来提供名称。

**Bean命名约定**

惯例是在命名bean时使用标准Java约定作为实例字段名称。也就是说，bean名称以小写字母开头，从那时起就是驼峰式的。这种名称的例子将是（不带引号）`'accountManager'`， `'accountService'`，`'userDao'`，`'loginController'`，等等。

命名bean一直使您的配置更易于阅读和理解，如果您使用的是Spring AOP，那么在将建议应用于与名称相关的一组bean时，它会有很大帮助。

#### 在bean定义之外别名bean

在bean定义本身中，您可以为bean提供多个名称，方法是使用`id`属性指定的最多一个名称和属性中的任意数量的其他名称`name`。这些名称可以是同一个bean的等效别名，并且在某些情况下很有用，例如允许应用程序中的每个组件通过使用特定于该组件本身的bean名称来引用公共依赖项。

但是，指定实际定义bean的所有别名并不总是足够的。有时需要为其他地方定义的bean引入别名。在大型系统中通常就是这种情况，其中配置在每个子系统之间分配，每个子系统具有其自己的一组对象定义。在基于XML的配置元数据中，您可以使用该`<alias/>`元素来完成此任务。

```
<alias  name = “fromName”  alias = “toName” />
```

在这种情况下，`fromName`在使用此别名定义之后，命名的bean（在同一容器中）也可以称为`toName`。

例如，子系统A的配置元数据可以通过名称引用DataSource `subsystemA-dataSource`。子系统B的配置元数据可以通过名称引用DataSource `subsystemB-dataSource`。在编写使用这两个子系统的主应用程序时，主应用程序通过名称引用DataSource `myApp-dataSource`。要使所有三个名称引用同一对象，可以将以下别名定义添加到配置元数据中：

```
<alias  name = “myApp-dataSource”  alias = “subsystemA-dataSource” /> 
<alias  name = “myApp-dataSource”  alias = “subsystemB-dataSource” />
```

现在，每个组件和主应用程序都可以通过一个唯一的名称引用dataSource，并保证不与任何其他定义冲突（有效地创建命名空间），但它们引用相同的bean。

### 7.3.2实例化bean

bean定义本质上是用于创建一个或多个对象的配方。容器在被询问时查看命名bean的配方，并使用由该bean定义封装的配置元数据来创建（或获取）实际对象。

如果使用基于XML的配置元数据，则指定要在元素的`class`属性中实例化的对象的类型（或类）`<bean/>`。此 `class`属性在内部是 实例`Class`上的属性`BeanDefinition`，通常是必需的。您可以通过`Class`以下两种方式之一使用该属性：

- 通常，在容器本身通过反向调用其构造函数直接创建bean的情况下指定要构造的bean类，稍微等同于使用`new`运算符的Java代码。
- 要指定包含`static`将被调用以创建对象的工厂方法的实际类，在不太常见的情况下，容器在类上调用 `static` *工厂*方法来创建bean。从调用`static`工厂方法返回的对象类型可以完全是同一个类或另一个类。



#### 使用构造函数实例化

当您通过构造方法创建bean时，所有普通类都可以使用并与Spring兼容。也就是说，正在开发的类不需要实现任何特定接口或以特定方式编码。简单地指定bean类就足够了。但是，根据您为该特定bean使用的IoC类型，您可能需要一个默认（空）构造函数。

Spring IoC容器几乎可以管理您希望它管理的*任何*类; 它不仅限于管理真正的JavaBeans。大多数Spring用户更喜欢实际的JavaBeans，只有一个默认（无参数）构造函数，并且在容器中的属性之后建模了适当的setter和getter。您还可以在容器中拥有更多异国情调的非bean样式类。例如，如果您需要使用绝对不符合JavaBean规范的旧连接池，那么Spring也可以对其进行管理。

使用基于XML的配置元数据，您可以按如下方式指定bean类：

```xml
<bean  id = “exampleBean”  class = “examples.ExampleBean” /> 

<bean  name = “anotherExample”  class = “examples.ExampleBeanTwo” />
```

#### 使用静态工厂方法实例化

定义使用静态工厂方法创建的bean时，可以使用该`class` 属性指定包含`static`工厂方法的类，并使用`factory-method`名称的属性指定工厂方法本身的名称。您应该能够调用此方法（使用后面描述的可选参数）并返回一个活动对象，随后将其视为通过构造函数创建的对象。这种bean定义的一个用途是`static`在遗留代码中调用工厂。

以下bean定义指定通过调用factory-method创建bean。该定义未指定返回对象的类型（类），仅指定包含工厂方法的类。在此示例中，该`createInstance()` 方法必须是*静态*方法。

```xml
<bean id="clientService"
    class="examples.ClientService"
    factory-method="createInstance"/>

```

```java
public class ClientService {
    private static ClientService clientService = new ClientService();
    private ClientService() {}

    public static ClientService createInstance() {
        return clientService;
    }
}
```

#### 使用实例工厂方法实例化

与通过静态工厂方法实例化类似，使用实例工厂方法进行实例化会从容器调用现有bean的非静态方法来创建新bean。要使用此机制，请将该`class`属性保留为空，并在`factory-bean`属性中指定当前（或父/祖先）容器中bean的名称，该容器包含要调用以创建对象的实例方法。使用`factory-method`属性设置工厂方法本身的名称

```xml
<!-- the factory bean, which contains a method called createInstance() -->
<bean id="serviceLocator" class="examples.DefaultServiceLocator">
    <!-- inject any dependencies required by this locator bean -->
</bean>

<!-- the bean to be created via the factory bean -->
<bean id="clientService"
    factory-bean="serviceLocator"
    factory-method="createClientServiceInstance"/>
```

```java
public class DefaultServiceLocator {

    private static ClientService clientService = new ClientServiceImpl();

    public ClientService createClientServiceInstance() {
        return clientService;
    }
}
```

一个工厂类也可以包含多个工厂方法，如下所示：

```xml
<bean id="serviceLocator" class="examples.DefaultServiceLocator">
    <!-- inject any dependencies required by this locator bean -->
</bean>

<bean id="clientService"
    factory-bean="serviceLocator"
    factory-method="createClientServiceInstance"/>

<bean id="accountService"
    factory-bean="serviceLocator"
    factory-method="createAccountServiceInstance"/>
```

```java
public class DefaultServiceLocator {

    private static ClientService clientService = new ClientServiceImpl();

    private static AccountService accountService = new AccountServiceImpl();

    public ClientService createClientServiceInstance() {
        return clientService;
    }

    public AccountService createAccountServiceInstance() {
        return accountService;
    }
}
```