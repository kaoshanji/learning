# 9.8 验证

Spring 3为其验证支持引入了一些增强功能。首先，现在完全支持JSR-303 Bean Validation API。其次，当以编程方式使用时，Spring的DataBinder现在可以验证对象以及绑定它们。第三，Spring MVC现在支持声明性地验证`@Controller`输入。

### 9.8.1 JSR-303 Bean Validation API概述

JSR-303标准化了Java平台的验证约束声明和元数据。使用此API，您可以使用声明性验证约束来注释域模型属性，并且运行时会强制执行它们。您可以利用许多内置约束。您还可以定义自己的自定义约束。

为了说明，请考虑一个具有两个属性的简单PersonForm模型：

```java
public class PersonForm {
    private String name;
    private int age;
}
```

JSR-303允许您为这些属性定义声明性验证约束：

```java
public class PersonForm {

    @NotNull
    @Size(max=64)
    private String name;

    @Min(0)
    private int age;
}
```

当JSR-303 Validator验证此类的实例时，将强制执行这些约束。

### 9.8.2配置Bean验证提供程序

Spring提供对Bean Validation API的完全支持。这包括方便地支持将JSR-303 / JSR-349 Bean Validation提供程序作为Spring bean引导。这允许在您的应用程序中需要验证的地方注入`javax.validation.ValidatorFactory`或`javax.validation.Validator`注入。

使用将`LocalValidatorFactoryBean`默认Validator配置为Spring bean：

```xml
<bean id="validator"
    class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean"/>
```

上面的基本配置将触发Bean Validation使用其默认引导机制进行初始化。JSR-303 / JSR-349提供程序（如Hibernate Validator）预计会出现在类路径中并自动检测。

#### 注入验证器

`LocalValidatorFactoryBean`同时实现了`javax.validation.ValidatorFactory`和 `javax.validation.Validator`，以及Spring的`org.springframework.validation.Validator`。您可以将这些接口中的任何一个引用注入到需要调用验证逻辑的bean中。

`javax.validation.Validator`如果您希望直接使用Bean Validation API，请引用一个引用：

```java
import javax.validation.Validator;

@Service
public class MyService {

    @Autowired
    private Validator validator;
```

`org.springframework.validation.Validator`如果您的bean需要Spring Validation API，请引用一个引用：

```java
import org.springframework.validation.Validator;

@Service
public class MyService {

    @Autowired
    private Validator validator;
}
```

#### 配置自定义约束

每个Bean Validation约束由两部分组成。首先，`@Constraint`声明约束及其可配置属性的注释。第二，实现`javax.validation.ConstraintValidator`约束行为的接口的实现。要将声明与实现相关联，每个`@Constraint`注释都引用相应的`ConstraintValidator`实现类。在运行时，`ConstraintValidatorFactory`在域模型中遇到约束注释时， 实例化引用的实现。

默认情况下，`LocalValidatorFactoryBean`配置`SpringConstraintValidatorFactory` 使用Spring创建ConstraintValidator实例的a。这允许您的自定义ConstraintValidators像其他任何Spring bean一样受益于依赖注入。

下面显示的是自定义`@Constraint`声明的示例，后跟一个`ConstraintValidator`使用Spring进行依赖项注入的关联 实现：

```java
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=MyConstraintValidator.class)
public @interface MyConstraint {
}
```

```java
import javax.validation.ConstraintValidator;

public class MyConstraintValidator implements ConstraintValidator {

    @Autowired;
    private Foo aDependency;

    ...
}
```

正如您所看到的，ConstraintValidator实现可能与其他任何Spring bean一样具有@Autowired的依赖关系。

#### Spring-driven 方法验证

Bean Validation 1.1支持的方法验证功能，以及Hibernate Validator 4.3的自定义扩展，可以通过`MethodValidationPostProcessor`bean定义集成到Spring上下文中：

```xml
<bean class="org.springframework.validation.beanvalidation.MethodValidationPostProcessor"/>
```

为了有资格进行Spring驱动的方法验证，所有目标类都需要使用Spring的`@Validated`注释进行注释，可以选择声明要使用的验证组。`MethodValidationPostProcessor`使用Hibernate Validator和Bean Validation 1.1提供程序查看javadocs的设置详细信息。

#### 其他配置选项

`LocalValidatorFactoryBean`对于大多数情况，默认配置应该足够了。从消息插值到遍历解析，各种Bean Validation构造有许多配置选项。有关`LocalValidatorFactoryBean`这些选项的更多信息，请参阅 javadocs。

### 9.8.3配置DataBinder

从Spring 3开始，可以使用Validator配置DataBinder实例。配置完成后，可以通过调用来调用Validator `binder.validate()`。任何验证错误都会自动添加到活页夹的BindingResult中。

以编程方式使用DataBinder时，可以在绑定到目标对象后使用它来调用验证逻辑：

```java
Foo target = new Foo();
DataBinder binder = new DataBinder(target);
binder.setValidator(new FooValidator());

// bind to the target object
binder.bind(propertyValues);

// validate the target object
binder.validate();

// get BindingResult that includes any validation errors
BindingResult results = binder.getBindingResult();
```

DataBinder也可以`Validator`通过`dataBinder.addValidators`和配置多个实例 `dataBinder.replaceValidators`。将全局配置的Bean验证与`Validator`在DataBinder实例上本地配置的Spring组合时，这非常有用。

