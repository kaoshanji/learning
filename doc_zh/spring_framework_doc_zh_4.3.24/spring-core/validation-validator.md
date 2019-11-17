# 9.2 spring 验证接口

Spring提供了一个`Validator`可用于验证对象的接口。该 `Validator`接口的工作方式使用`Errors`对象，以便在验证，验证器可以报告验证失败的`Errors`对象。

让我们考虑一个小数据对象：

```java
public class Person {

    private String name;
    private int age;

    // the usual getters and setters...
}
```

我们将`Person`通过实现`org.springframework.validation.Validator`接口的以下两种方法来为类提供验证行为：

- `supports(Class)`- 这可以`Validator`验证提供的实例`Class`吗？
- `validate(Object, org.springframework.validation.Errors)`- 验证给定对象，如果验证错误，则注册具有给定`Errors`对象的对象

实现a `Validator`非常简单，特别是当您知道`ValidationUtils`Spring Framework提供的 帮助程序类时。

```java
public class PersonValidator implements Validator {

    /**
     * This Validator validates *just* Person instances
     */
    public boolean supports(Class clazz) {
        return Person.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {
        ValidationUtils.rejectIfEmpty(e, "name", "name.empty");
        Person p = (Person) obj;
        if (p.getAge() < 0) {
            e.rejectValue("age", "negativevalue");
        } else if (p.getAge() > 110) {
            e.rejectValue("age", "too.darn.old");
        }
    }
}
```

如您所见，类的`static` `rejectIfEmpty(..)`方法`ValidationUtils`用于拒绝`'name'`属性，如果它是`null`空字符串。看看`ValidationUtils`javadocs，除了前面的例子之外，看看它提供了什么功能。

虽然可以实现单个`Validator`类来验证富对象中的每个嵌套对象，但最好将每个嵌套对象类的验证逻辑封装在自己的`Validator`实现中。*“富”*对象的一个简单示例`Customer`是由两个`String` 属性（第一个和第二个名称）和一个复杂`Address`对象组成。`Address`对象可以独立于`Customer`对象使用，因此实现了不同的对象`AddressValidator` 。如果您希望`CustomerValidator`重用`AddressValidator`类中包含的逻辑而不采用复制和粘贴，则可以`AddressValidator`在您的内部依赖注入或实例化`CustomerValidator`，并像这样使用它：

```java
public class CustomerValidator implements Validator {

    private final Validator addressValidator;

    public CustomerValidator(Validator addressValidator) {
        if (addressValidator == null) {
            throw new IllegalArgumentException("The supplied [Validator] is " +
                "required and must not be null.");
        }
        if (!addressValidator.supports(Address.class)) {
            throw new IllegalArgumentException("The supplied [Validator] must " +
                "support the validation of [Address] instances.");
        }
        this.addressValidator = addressValidator;
    }

    /**
     * This Validator validates Customer instances, and any subclasses of Customer too
     */
    public boolean supports(Class clazz) {
        return Customer.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", "field.required");
        Customer customer = (Customer) target;
        try {
            errors.pushNestedPath("address");
            ValidationUtils.invokeValidator(this.addressValidator, customer.getAddress(), errors);
        } finally {
            errors.popNestedPath();
        }
    }
}
```

验证错误将报告给`Errors`传递给验证程序的对象。对于Spring Web MVC，您可以使用`<spring:bind/>`tag来检查错误消息，当然您也可以自己检查错误对象。有关它提供的方法的更多信息可以在javadocs中找到。