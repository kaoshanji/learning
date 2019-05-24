# 9.4 Bean 操作和 BeanWrapper

该`org.springframework.beans`软件包遵循Oracle提供的JavaBeans标准。JavaBean只是一个具有默认无参数构造函数的类，它遵循命名约定，其中（作为示例）一个名为的属性`bingoMadness`将具有setter方法`setBingoMadness(..)`和getter方法`getBingoMadness()`。有关JavaBeans和规范的更多信息，请参阅Oracle的网站（[javabeans](https://docs.oracle.com/javase/6/docs/api/java/beans/package-summary.html)）。

beans包中一个非常重要的类是`BeanWrapper`接口及其相应的实现（`BeanWrapperImpl`）。引自javadocs， `BeanWrapper`提供了设置和获取属性值（单独或批量），获取属性描述符以及查询属性以确定它们是可读还是可写的功能。此外，这些商品`BeanWrapper`支持嵌套属性，可以将子属性的属性设置为无限深度。然后，`BeanWrapper`支持标准JavaBean的能力`PropertyChangeListeners` 和`VetoableChangeListeners`，而不需要在辅助代码。最后但并非最不重要的是，`BeanWrapper`它为索引属性的设置提供了支持。在`BeanWrapper`通常不使用应用程序代码直接的，而是由`DataBinder`和`BeanFactory`。

工作的方式`BeanWrapper`部分由其名称表示：*它包装bean*以对该*bean*执行操作，如设置和检索属性。

### 9.4.1设置和获取基本和嵌套属性

设置和获取属性是使用`setPropertyValue(s)`和 `getPropertyValue(s)`两个带有几个重载变体的方法完成的。它们都在Spring附带的javadocs中有更详细的描述。重要的是要知道有一些用于指示对象属性的约定。几个例子：



**表9.1。属性的例子**

| 表达                   | 说明                                                         |
| ---------------------- | ------------------------------------------------------------ |
| `name`                 | 表示与`name`方法`getName()`或`isName()` 和相对应的属性`setName(..)` |
| `account.name`         | 指示属性的嵌套属性`name`，`account`例如对应于方法`getAccount().setName()`或`getAccount().getName()` |
| `account[2]`           | 指示索引属性的*第三个*元素`account`。索引属性可能是类型的`array`，`list`或其它*天然有序的*集合 |
| `account[COMPANYNAME]` | 指示由Map属性的*KEYNAME*键索引的映射条目的值`account`        |

下面你会找到一些使用`BeanWrapper`to get和set属性的例子。

*（下一节如果你不打算与合作是不是对你非常重要的BeanWrapper。如果您只是使用了DataBinder和BeanFactory 他们外的扩展实现，你可以跳过约一节PropertyEditors。）*

考虑以下两个类：

```java
public class Company {

    private String name;
    private Employee managingDirector;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee getManagingDirector() {
        return this.managingDirector;
    }

    public void setManagingDirector(Employee managingDirector) {
        this.managingDirector = managingDirector;
    }
}
```

```java
public class Employee {

    private String name;

    private float salary;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }
}
```

下面的代码片断展示了如何检索和操作的一些实例化属性的一些例子`Companies`和`Employees`：

```java
BeanWrapper company = new BeanWrapperImpl(new Company());
// setting the company name..
company.setPropertyValue("name", "Some Company Inc.");
// ... can also be done like this:
PropertyValue value = new PropertyValue("name", "Some Company Inc.");
company.setPropertyValue(value);

// ok, let's create the director and tie it to the company:
BeanWrapper jim = new BeanWrapperImpl(new Employee());
jim.setPropertyValue("name", "Jim Stravinsky");
company.setPropertyValue("managingDirector", jim.getWrappedInstance());

// retrieving the salary of the managingDirector through the company
Float salary = (Float) company.getPropertyValue("managingDirector.salary");
```

### 9.4.2内置PropertyEditor实现

Spring使用概念`PropertyEditors`来实现`Object`a和a 之间的转换 `String`。如果您考虑一下，有时可能会以与对象本身不同的方式表示属性。例如，a `Date` 可以用人类可读的方式表示（如`String` `'2007-14-09'`），而我们仍然能够将人类可读的表单转换回原始日期（或者甚至更好：转换以人类可读形式输入的任何日期，返回到`Date`对象）。可以通过*注册*类型的 *自定义编辑器*来实现此行为`java.beans.PropertyEditor`。`BeanWrapper`如上一章所述，在特定的IoC容器中或在一个特定的IoC容器中注册自定义编辑器，使其了解如何将属性转换为所需的类型。了解更多 `PropertyEditors`在`java.beans`Oracle提供的包的javadoc中。

在Spring中使用属性编辑的几个示例：

- *在bean上设置属性*是使用`PropertyEditors`。当提到 `java.lang.String`你在XML文件中声明的某个bean的属性的值时，Spring将（如果相应属性的setter具有`Class`-parameter）使用它`ClassEditor`来尝试将参数解析为`Class` 对象。
- 在Spring的MVC框架中*解析HTTP请求参数*是使用`PropertyEditors`您可以在所有子类中手动绑定的各种类型完成的 `CommandController`。

Spring有许多内置功能，`PropertyEditors`可以让生活变得轻松。下面列出了每一个，它们都位于`org.springframework.beans.propertyeditors` 包装中。大多数（但不是全部）（如下所示）默认注册 `BeanWrapperImpl`。如果属性编辑器可以某种方式配置，您当然可以注册自己的变体来覆盖默认变量：



**表9.2。内置PropertyEditors**

| 类                        | 说明                                                         |
| ------------------------- | ------------------------------------------------------------ |
| `ByteArrayPropertyEditor` | 字节数组的编辑器。字符串将简单地转换为其对应的字节表示。默认注册`BeanWrapperImpl`。 |
| `ClassEditor`             | 解析表示类到实际类的字符串，反之亦然。当找不到某个类时，`IllegalArgumentException`会抛出一个类。默认注册`BeanWrapperImpl`。 |
| `CustomBooleanEditor`     | 属性的可自定义属性编辑器`Boolean`。默认情况下注册 `BeanWrapperImpl`，但是，可以通过将自定义实例注册为自定义编辑器来覆盖。 |
| `CustomCollectionEditor`  | 集合的属性编辑器，将任何源`Collection`转换为给定的目标 `Collection`类型。 |
| `CustomDateEditor`        | java.util.Date的可自定义属性编辑器，支持自定义DateFormat。没有默认注册。必须根据需要以适当的格式注册用户。 |
| `CustomNumberEditor`      | Number的子类定制的属性编辑器一样`Integer`，`Long`，`Float`， `Double`。默认情况下已注册`BeanWrapperImpl`，但可以通过将自定义实例注册为自定义编辑器来覆盖。 |
| `FileEditor`              | 能够将字符串解析为`java.io.File`对象。默认注册 `BeanWrapperImpl`。 |
| `InputStreamEditor`       | 单向属性编辑器，能够获取文本字符串并生成（通过中间`ResourceEditor`和`Resource`）`InputStream`，所以`InputStream`属性可以直接设置为字符串。请注意，默认用法不会`InputStream`为您关闭！默认注册`BeanWrapperImpl`。 |
| `LocaleEditor`            | 能够将字符串解析为`Locale`对象，反之亦然（字符串格式为 *[country]* [variant]，这与Locale提供的toString（）方法相同）。默认注册`BeanWrapperImpl`。 |
| `PatternEditor`           | 能够将字符串解析为`java.util.regex.Pattern`对象，反之亦然。  |
| `PropertiesEditor`        | 能够将字符串（使用`java.util.Properties`类的javadoc中定义的格式进行格式化）转换为`Properties`对象。默认注册`BeanWrapperImpl`。 |
| `StringTrimmerEditor`     | 修剪字符串的属性编辑器。（可选）允许将空字符串转换为`null`值。未默认注册; 必须由用户注册。 |
| `URLEditor`               | 能够将URL的String表示形式解析为实际`URL`对象。默认注册`BeanWrapperImpl`。 |

Spring使用它`java.beans.PropertyEditorManager`来设置可能需要的属性编辑器的搜索路径。搜索路径还包括`sun.bean.editors`，其包括`PropertyEditor`实现为类型，例如`Font`，`Color`和最原始类型。另请注意，标准JavaBeans基础结构将自动发现`PropertyEditor`类（无需显式注册它们），如果它们与它们处理的类位于同一个包中，并且与该类具有相同的名称，并`'Editor'`附加; 例如，可以使用以下类和包结构，这足以使`FooEditor`类被识别并用作`PropertyEditor`for `Foo`-typed属性。

```bash
com
  chank
    pop
      Foo
      FooEditor // the PropertyEditor for the Foo class
```

请注意，您也可以在`BeanInfo`此处使用标准JavaBeans机制（ [在此处未详细描述](https://docs.oracle.com/javase/tutorial/javabeans/advanced/customization.html)）。下面是一个使用该`BeanInfo`机制的示例，用于使用`PropertyEditor`关联类的属性显式注册一个或多个实例。

```bash
com
  chank
    pop
      Foo
      FooBeanInfo // the BeanInfo for the Foo class
```

这是引用`FooBeanInfo`类的Java源代码。这会将a `CustomNumberEditor`与类的`age`属性相关联`Foo`。

```java
public class FooBeanInfo extends SimpleBeanInfo {

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            final PropertyEditor numberPE = new CustomNumberEditor(Integer.class, true);
            PropertyDescriptor ageDescriptor = new PropertyDescriptor("age", Foo.class) {
                public PropertyEditor createPropertyEditor(Object bean) {
                    return numberPE;
                };
            };
            return new PropertyDescriptor[] { ageDescriptor };
        }
        catch (IntrospectionException ex) {
            throw new Error(ex.toString());
        }
    }
}
```

#### 注册其他自定义PropertyEditors

将bean属性设置为字符串值时，Spring IoC容器最终使用标准JavaBeans `PropertyEditors`将这些字符串转换为属性的复杂类型。Spring预先注册了许多自定义`PropertyEditors`（例如，将表示为字符串的类名转换为实际`Class`对象）。此外，Java的标准JavaBeans `PropertyEditor`查找机制允许`PropertyEditor` 简单地将类简单地命名，并将其放置在与其提供支持的类相同的包中，以便自动找到。

如果需要注册其他自定义`PropertyEditors`，可以使用多种机制。最通常不方便或不推荐的手动方法是简单地使用界面的`registerCustomEditor()`方法`ConfigurableBeanFactory`，假设您有`BeanFactory`参考。另一个稍微更方便的机制是使用一个特殊的bean工厂后处理器调用`CustomEditorConfigurer`。尽管bean工厂后处理器可以与`BeanFactory`实现一起使用，但`CustomEditorConfigurer`它具有嵌套属性设置，因此强烈建议将其与 `ApplicationContext`可以以与任何其他bean类似方式部署的方式一起使用，并自动检测和应用。

请注意，所有bean工厂和应用程序上下文都会自动使用许多内置属性编辑器，通过使用称为a `BeanWrapper`来处理属性转换的东西。`BeanWrapper` 寄存器在[上一节](#beans-beans-conversion)中列出的标准属性编辑器。此外， `ApplicationContexts`还可以覆盖或添加其他数量的编辑器，以适合特定应用程序上下文类型的方式处理资源查找。

标准JavaBeans `PropertyEditor`实例用于将表示为字符串的属性值转换为属性的实际复杂类型。 `CustomEditorConfigurer`，bean工厂后处理器，可用于方便地添加对其他`PropertyEditor`实例的支持`ApplicationContext`。

考虑一个用户类`ExoticType`，以及另一个`DependsOnExoticType`需要 `ExoticType`设置为属性的类：

```java
package example;

public class ExoticType {

    private String name;

    public ExoticType(String name) {
        this.name = name;
    }
}

public class DependsOnExoticType {

    private ExoticType type;

    public void setType(ExoticType type) {
        this.type = type;
    }
}
```

正确设置内容后，我们希望能够将type属性指定为字符串，`PropertyEditor`后台会将其转换为实际 `ExoticType`实例：

```xml
<bean id="sample" class="example.DependsOnExoticType">
    <property name="type" value="aNameForExoticType"/>
</bean>
```

`PropertyEditor`实现看上去就像这样：

```java
// converts string representation to ExoticType object
package example;

public class ExoticTypeEditor extends PropertyEditorSupport {

    public void setAsText(String text) {
        setValue(new ExoticType(text.toUpperCase()));
    }
}
```

最后，我们使用`CustomEditorConfigurer`注册new `PropertyEditor`， `ApplicationContext`然后根据需要使用它：

```xml
<bean class="org.springframework.beans.factory.config.CustomEditorConfigurer">
    <property name="customEditors">
        <map>
            <entry key="example.ExoticType" value="example.ExoticTypeEditor"/>
        </map>
    </property>
</bean>
```

##### 使用PropertyEditorRegistrars

使用Spring容器注册属性编辑器的另一种机制是创建和使用a `PropertyEditorRegistrar`。当您需要在几种不同的情况下使用同一组属性编辑器时，此接口特别有用：编写相应的注册器并在每种情况下重用它。`PropertyEditorRegistrars`与一个名为`PropertyEditorRegistry`的接口一起工作，一个由Spring `BeanWrapper`（和`DataBinder`）实现的接口。`PropertyEditorRegistrars` 当与`CustomEditorConfigurer` （[在此](#beans-beans-conversion-customeditor-registration)介绍）一起使用时特别方便，它暴露了一个名为的属性`setPropertyEditorRegistrars(..)`：`PropertyEditorRegistrars`以`CustomEditorConfigurer`这种方式添加到一个 可以很容易地与`DataBinder`Spring MVC 共享`Controllers`。此外，它避免了在自定义编辑器上进行同步的需要：`PropertyEditorRegistrar`期望`PropertyEditor` 为每个bean创建尝试创建新的实例。

使用a `PropertyEditorRegistrar`可能最好用一个例子说明。首先，您需要创建自己的`PropertyEditorRegistrar`实现：

```java
package com.foo.editors.spring;

public final class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {

    public void registerCustomEditors(PropertyEditorRegistry registry) {

        // it is expected that new PropertyEditor instances are created
        registry.registerCustomEditor(ExoticType.class, new ExoticTypeEditor());

        // you could register as many custom property editors as are required here...
    }
}
```

另请参阅`org.springframework.beans.support.ResourceEditorRegistrar`示例 `PropertyEditorRegistrar`实现。请注意，在它的`registerCustomEditors(..)`方法实现中， 它如何创建每个属性编辑器的新实例。

接下来我们配置一个`CustomEditorConfigurer`并将一个实例 `CustomPropertyEditorRegistrar`注入其中：

```xml
<bean class="org.springframework.beans.factory.config.CustomEditorConfigurer">
    <property name="propertyEditorRegistrars">
        <list>
            <ref bean="customPropertyEditorRegistrar"/>
        </list>
    </property>
</bean>

<bean id="customPropertyEditorRegistrar"
    class="com.foo.editors.spring.CustomPropertyEditorRegistrar"/>
```

最后，与本章的重点有所不同，对于那些使用[Spring的MVC Web框架的人来说](#mvc)，`PropertyEditorRegistrars`结合使用数据绑定`Controllers`（如`SimpleFormController`）可以非常方便。下面是一个`PropertyEditorRegistrar`在`initBinder(..)`方法实现中使用a的示例：

```java
public final class RegisterUserController extends SimpleFormController {

    private final PropertyEditorRegistrar customPropertyEditorRegistrar;

    public RegisterUserController(PropertyEditorRegistrar propertyEditorRegistrar) {
        this.customPropertyEditorRegistrar = propertyEditorRegistrar;
    }

    protected void initBinder(HttpServletRequest request,
            ServletRequestDataBinder binder) throws Exception {
        this.customPropertyEditorRegistrar.registerCustomEditors(binder);
    }

    // other methods to do with registering a User
}
```

这种`PropertyEditor`注册方式可以导致简洁的代码（实现`initBinder(..)`只需一行！），并允许将通用`PropertyEditor` 注册代码封装在一个类中，然后`Controllers`根据需要共享 。