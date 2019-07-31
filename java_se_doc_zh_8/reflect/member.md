# 属性

反射定义了一个接口 [`java.lang.reflect.Member`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Member.html)，其是由执行 [`java.lang.reflect.Field`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html)， [`java.lang.reflect.Method`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html)和 [`java.lang.reflect.Constructor`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html)。这一对象将在本课程中讨论。对于每个成员，本课程将描述用于检索声明和类型信息的关联API，成员特有的任何操作（例如，设置字段的值或调用方法）以及常见的错误。将使用代码样本和相关输出来说明每个概念，其近似于一些预期的反射用途。

------

**注意：**  根据，类的是类主体的继承组件，包括字段，方法，嵌套类，接口和枚举类型。由于构造函数不是继承的，因此它们不是成员。这与实施类不同 。

## 字段

字段具有类型和值。的 [`java.lang.reflect.Field`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html)类提供用于访问类型信息和设置和获取一个字段的值的给定对象的方法。

- [获取字段类型](fieldTypes.md)描述了如何[获取字段](fieldTypes.html)的声明类型和泛型类型
- [检索和解析字段修饰符](fieldModifiers.md)显示如何获取字段声明的部分，如`public`或`transient`
- [获取和设置字段值](fieldValues.md)说明了如何访问字段值
- [故障排除](fieldTrouble.md)描述了一些可能导致混淆的常见编码错误

## 方法

方法具有返回值，参数，并可能抛出异常。的 [`java.lang.reflect.Method`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html)类提供用于获得所述参数和返回值的类型的信息的方法。它也可以用于调用给定对象的方法。

- [获取方法类型信息](methodType.md)显示如何枚举类中声明的方法并获取类型信息
- [获取方法参数的名称](methodparameterreflection.md)显示了如何检索方法或构造函数参数的名称和其他信息
- [检索和解析方法修饰符](methodModifiers.md)描述了如何访问和解码修饰符以及与该方法相关的其他信息
- [调用方法](methodInvocation.md)说明了如何执行方法并获取其返回值
- [故障排除](methodTrouble.md)包括查找或调用方法时遇到的常见错误

## 构造函数

构造函数的Reflection API在方法中定义 [`java.lang.reflect.Constructor`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html)并且与方法类似，有两个主要的例外：第一，构造函数没有返回值; 第二，调用构造函数为给定的类创建一个对象的新实例。

- [Finding Constructors](ctorLocation.md)说明了如何检索具有特定参数的构造函数
- [检索和解析构造函数修饰符](ctorModifiers.md)显示如何获取构造函数声明的修饰符以及有关构造函数的其他信息
- [创建新类实例](ctorInstance.md)显示如何通过调用其构造函数来实例化对象的实例
- [故障排除](ctorTrouble.md)描述了在查找或调用构造函数时可能遇到的常见错误

