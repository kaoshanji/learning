# 获取方法参数的名称

您可以使用该方法获取任何方法或构造函数的形式参数的名称 [`java.lang.reflect.Executable.getParameters`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Executable.html#getParameters--)。（类 [`Method`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html)和 [`Constructor`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Executable.html)扩展类 [`Executable`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Executable.html)，因此继承该方法`Executable.getParameters`。）但是，`.class`默认情况下，文件不存储形式参数名称。这是因为许多生成和使用类文件的工具可能不会期望`.class`包含参数名称的文件具有更大的静态和动态占用空间。特别是，这些工具必须处理更大的`.class`文件，而Java虚拟机（JVM）将使用更多的内存。此外，某些参数名称（如`secret`or `password`）可能会公开有关安全敏感方法的信息。

要将正式参数名称存储在特定`.class`文件中，从而使Reflection API能够检索形式参数名称，请使用编译器`-parameters`选项编译源文件`javac`。

该 [`MethodParameterSpy`](example/MethodParameterSpy.java)示例说明了如何检索给定类的所有构造函数和方法的形式参数的名称。该示例还打印有关每个参数的其他信息。

以下命令打印类的构造函数和方法的形式参数名称 [`ExampleMethods`](example/ExampleMethods.java)。**注**：请记住，编译示例`ExampleMethods`与`-parameters`编译器选项：

```bash
java MethodParameterSpy ExampleMethods
```

此命令打印以下内容：

```bash
Number of constructors: 1

Constructor #1
public ExampleMethods()

Number of declared constructors: 1

Declared constructor #1
public ExampleMethods()

Number of methods: 4

Method #1
public boolean ExampleMethods.simpleMethod(java.lang.String,int)
             Return type: boolean
     Generic return type: boolean
         Parameter class: class java.lang.String
          Parameter name: stringParam
               Modifiers: 0
            Is implicit?: false
        Is name present?: true
           Is synthetic?: false
         Parameter class: int
          Parameter name: intParam
               Modifiers: 0
            Is implicit?: false
        Is name present?: true
           Is synthetic?: false

Method #2
public int ExampleMethods.varArgsMethod(java.lang.String...)
             Return type: int
     Generic return type: int
         Parameter class: class [Ljava.lang.String;
          Parameter name: manyStrings
               Modifiers: 0
            Is implicit?: false
        Is name present?: true
           Is synthetic?: false

Method #3
public boolean ExampleMethods.methodWithList(java.util.List<java.lang.String>)
             Return type: boolean
     Generic return type: boolean
         Parameter class: interface java.util.List
          Parameter name: listParam
               Modifiers: 0
            Is implicit?: false
        Is name present?: true
           Is synthetic?: false

Method #4
public <T> void ExampleMethods.genericMethod(T[],java.util.Collection<T>)
             Return type: void
     Generic return type: void
         Parameter class: class [Ljava.lang.Object;
          Parameter name: a
               Modifiers: 0
            Is implicit?: false
        Is name present?: true
           Is synthetic?: false
         Parameter class: interface java.util.Collection
          Parameter name: c
               Modifiers: 0
            Is implicit?: false
        Is name present?: true
           Is synthetic?: false
```

该`MethodParameterSpy`示例使用以下[`Parameter`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Parameter.html)类中的方法 ：

- [`getType`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Parameter.html#getType--)：返回[`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)标识参数的声明类型的 对象。

- [`getName`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Parameter.html#getName--)：返回参数的名称。如果参数的名称存在，则此方法返回`.class`文件提供的名称。否则，此方法合成表单的名称，其中是声明参数的方法的描述符中的参数的索引。`arg*N*``*N*`

  例如，假设您编译了该类`ExampleMethods`而未指定`-parameters`编译器选项。该示例`MethodParameterSpy`将为该方法打印以下内容`ExampleMethods.simpleMethod`：

  ```bash
  public boolean ExampleMethods.simpleMethod(java.lang.String,int)
               Return type: boolean
       Generic return type: boolean
           Parameter class: class java.lang.String
            Parameter name: arg0
                 Modifiers: 0
              Is implicit?: false
          Is name present?: false
             Is synthetic?: false
           Parameter class: int
            Parameter name: arg1
                 Modifiers: 0
              Is implicit?: false
          Is name present?: false
             Is synthetic?: false
  ```

[`getModifiers`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Parameter.html#getModifiers--) ：返回一个整数，表示形式参数拥有的各种特征。如果适用于形式参数，则此值是以下值的总和：

| Value (in decimal) | Value (in hexadecimal | Description                                                  |
| ------------------ | --------------------- | ------------------------------------------------------------ |
| 16                 | 0x0010                | The formal parameter is declared `final`                     |
| 4096               | 0x1000                | The formal parameter is synthetic. Alternatively, you can invoke the method `isSynthetic`. |
| 32768              | 0x8000                | The parameter is implicitly declared in source code. Alternatively, you can invoke the method `isImplicit` |

- [`isImplicit`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Parameter.html#isImplicit--)：`true`如果在源代码中隐式声明此参数，则返回。有关详细信息，请参阅[隐式和合成参数](#implcit_and_synthetic)部分。
- [`isNamePresent`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Parameter.html#isNamePresent--)：`true`如果参数根据`.class`文件具有名称，则返回。
- [`isSynthetic`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Parameter.html#isSynthetic--)：`true`如果在源代码中既未隐式声明也未显式声明此参数，则返回。有关详细信息，请参阅[隐式和合成参数](#implcit_and_synthetic)部分。

## 隐式和合成参数

如果未明确编写某些构造，则会在源代码中隐式声明这些构造。例如，该 [`ExampleMethods`](example/ExampleMethods.java)示例不包含构造函数。默认构造函数是为它隐式声明的。该`MethodParameterSpy`示例打印有关隐式声明的构造函数的信息`ExampleMethods`：

```java
Number of declared constructors: 1
public ExampleMethods()
```

请考虑以下摘录 [`MethodParameterExamples`](example/MethodParameterExamples.java)：

```java
public class MethodParameterExamples {
    public class InnerClass { }
}
```

该类`InnerClass`是非静态 [嵌套类](../../java/javaOO/nested.html)或内部类。内部类的构造函数也是隐式声明的。但是，此构造函数将包含一个参数。当Java编译器编译时`InnerClass`，它会创建一个`.class`代表类似于以下代码的文件：

```java
public class MethodParameterExamples {
    public class InnerClass {
        final MethodParameterExamples parent;
        InnerClass(final MethodParameterExamples this$0) {
            parent = this$0; 
        }
    }
}
```

该`InnerClass`构造包含其类型为包围类的参数`InnerClass`，这是`MethodParameterExamples`。因此，该示例`MethodParameterExamples`打印以下内容：

```bash
public MethodParameterExamples$InnerClass(MethodParameterExamples)
         Parameter class: class MethodParameterExamples
          Parameter name: this$0
               Modifiers: 32784
            Is implicit?: true
        Is name present?: true
           Is synthetic?: false
```

因为`InnerClass`隐式声明了类的构造函数，所以它的参数也是隐式的。

**注意**：

- Java编译器为内部类的构造函数创建形式参数，以使编译器能够将创建表达式中的引用（表示直接封闭的实例）传递给成员类的构造函数。
- 值32784表示`InnerClass`构造函数的参数既是final（16），也是隐式（32768）。
- Java编程语言允许使用美元符号（`$`）的变量名称; 但是，按照惯例，美元符号不会用于变量名称。

如果Java编译器发出的构造与源代码中显式或隐式声明的构造不对应，则将其标记为*合成*构造，除非它们是类初始化方法。合成构造是由编译器生成的工件，这些工件在不同的实现之间变化。请考虑以下摘录 [`MethodParameterExamples`](example/MethodParameterExamples.java)：

```java
public class MethodParameterExamples {
    enum Colors {
        RED, WHITE;
    }
}
```

当Java编译器遇到`enum`构造时，它会创建几个与`.class`文件结构兼容的方法，并提供构造的预期功能`enum`。例如，Java编译器将为构造创建一个`.class`文件，该文件表示类似于以下内容的代码：`enum``Colors`

```java
final class Colors extends java.lang.Enum<Colors> {
    public final static Colors RED = new Colors("RED", 0);
    public final static Colors BLUE = new Colors("WHITE", 1);
 
    private final static values = new Colors[]{ RED, BLUE };
 
    private Colors(String name, int ordinal) {
        super(name, ordinal);
    }
 
    public static Colors[] values(){
        return values;
    }
 
    public static Colors valueOf(String name){
        return (Colors)java.lang.Enum.valueOf(Colors.class, name);
    }
}
```

Java编译器创建这三个构造函数和方法`enum`的结构：`Colors(String name, int ordinal)`，`Colors[] values()`，和`Colors valueOf(String name)`。方法`values`和`valueOf`隐式声明。因此，它们的形式参数名也被隐式声明。

该`enum`构造`Colors(String name, int ordinal)`是一个默认的构造函数，它是隐式声明。然而，此构造（的形式参数`name`和`ordinal`）都*不会*隐式声明。由于这些形式参数既未明确声明也未隐式声明，因此它们是合成的。（构造的默认构造函数的形式参数`enum`不是隐式声明的，因为不同的编译器不需要在这个构造函数的形式上达成一致;另一个Java编译器可能为它指定不同的形式参数。当编译器编译使用`enum`常量的表达式时，它们仅依赖于在`enum`构造的公共静态字段上，它们是隐式声明的，而不是它们的构造函数或者这些常量是如何初始化的。）

因此，该示例`MethodParameterExample`打印有关`enum`构造的以下内容`Colors`：

```bash
enum Colors:

Number of constructors: 0

Number of declared constructors: 1

Declared constructor #1
private MethodParameterExamples$Colors()
         Parameter class: class java.lang.String
          Parameter name: $enum$name
               Modifiers: 4096
            Is implicit?: false
        Is name present?: true
           Is synthetic?: true
         Parameter class: int
          Parameter name: $enum$ordinal
               Modifiers: 4096
            Is implicit?: false
        Is name present?: true
           Is synthetic?: true

Number of methods: 2

Method #1
public static MethodParameterExamples$Colors[]
    MethodParameterExamples$Colors.values()
             Return type: class [LMethodParameterExamples$Colors;
     Generic return type: class [LMethodParameterExamples$Colors;

Method #2
public static MethodParameterExamples$Colors
    MethodParameterExamples$Colors.valueOf(java.lang.String)
             Return type: class MethodParameterExamples$Colors
     Generic return type: class MethodParameterExamples$Colors
         Parameter class: class java.lang.String
          Parameter name: name
               Modifiers: 32768
            Is implicit?: true
        Is name present?: true
           Is synthetic?: false
```

有关隐式声明的构造的更多信息，请参阅[Java语言规范](https://docs.oracle.com/javase/specs/)，包括在Reflection API中显示为隐式的参数。