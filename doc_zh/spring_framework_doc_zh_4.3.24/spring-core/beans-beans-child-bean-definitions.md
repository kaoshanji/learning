# 7.7 Bean继承定义


bean定义可以包含许多配置信息，包括构造函数参数，属性值和特定于容器的信息，例如初始化方法，静态工厂方法名称等。子bean定义从父定义继承配置数据。子定义可以根据需要覆盖某些值或添加其他值。使用父bean和子bean定义可以节省大量的输入。实际上，这是一种模板形式。

如果以`ApplicationContext`编程方式使用接口，则子bean定义由`ChildBeanDefinition`类表示。大多数用户不在这个级别上使用它们，而是以类似的方式以声明方式配置bean定义`ClassPathXmlApplicationContext`。使用基于XML的配置元数据时，可以使用该`parent`属性指定子bean定义，并将父bean指定为此属性的值。

```xml
<bean id="inheritedTestBean" abstract="true"
        class="org.springframework.beans.TestBean">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithDifferentClass"
        class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBean" init-method="initialize">
    <property name="name" value="override"/>
    <!-- the age property value of 1 will be inherited from parent -->
</bean>
```

如果没有指定，则子 bean定义使用父定义中的bean类，但也可以覆盖它。在后一种情况下，子bean类必须与父类兼容，也就是说，它必须接受父类的属性值。

子bean定义从父级继承范围，构造函数参数值，属性值和方法覆盖，并带有添加新值的选项。`static`您指定的任何范围，初始化方法，销毁方法和/或工厂方法设置都将覆盖相应的父设置。

其余设置*始终*取自子定义：*取决于*， *autowire模式*，*依赖性检查*，*singleton*，*lazy init*。

前面的示例通过使用该`abstract`属性将父bean定义显式标记为abstract 。如果父定义未指定类，`abstract`则根据需要显式标记父bean定义，如下所示：

```xml
<bean id="inheritedTestBeanWithoutClass" abstract="true">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithClass" class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBeanWithoutClass" init-method="initialize">
    <property name="name" value="override"/>
    <!-- age will inherit the value of 1 from the parent bean definition-->
</bean>
```

父bean不能单独实例化，因为它不完整，并且也明确标记为`abstract`。当定义`abstract`如下所示时，它仅可用作纯模板bean定义，用作子定义的父定义。尝试自己使用这样的`abstract`父bean，通过将其称为另一个bean的ref属性或`getBean()`使用父bean id 进行显式调用，会返回错误。类似地，容器的内部`preInstantiateSingletons()`方法忽略定义为abstract的bean定义。

`ApplicationContext`默认情况下预先实例化所有单例。因此，重要的是（至少对于单例bean），如果你有一个（父）bean定义，你只打算用作模板，并且这个定义指定了一个类，你必须确保将*abstract*属性设置为*true*否则应用程序上下文将实际（尝试）预先实例化`abstract`bean。
