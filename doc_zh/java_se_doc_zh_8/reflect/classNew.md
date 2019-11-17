# 检索类对象

所有反射操作的入口点是 [`java.lang.Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)。除了之外 [`java.lang.reflect.ReflectPermission`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/ReflectPermission.html)，没有一个类 [`java.lang.reflect`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/package-summary.html)具有公共构造函数。要获得这些类，有必要调用适当的方法 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)。有几种方法可以获取， [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)具体取决于代码是否可以访问对象，类的名称，类型还是现有的 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)。

## Object.getClass（）

如果对象的实例可用，则获取它的最简单方法 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)是调用 [`Object.getClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#getClass--)。当然，这仅适用于所有继承自的引用类型 [`Object`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)。一些例子如下。

```java
Class c = "foo".getClass();
```

返回 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)for [`String`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)

```java
Class c = System.console().getClass();
```

有一个与该`static`方法 返回的虚拟机关联的唯一控制台[`System.console()`](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#console--)。返回的值 [`getClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#getClass--)是 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)对应的 [`java.io.Console`](https://docs.oracle.com/javase/8/docs/api/java/io/Console.html)。

```java
enum E { A, B }
Class c = A.getClass();
```

`A`是枚举的一个实例`E`; 因此 [`getClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#getClass--)返回 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)对应的枚举类型`E`。

```java
byte[] bytes = new byte[1024];
Class c = bytes.getClass();
```

由于数组是 [`Objects`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)，因此也可以[`getClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#getClass--)在数组的实例上调用 。返回 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)对应于具有组件类型的数组`byte`。

```java
import java.util.HashSet;
import java.util.Set;

Set<String> s = new HashSet<String>();
Class c = s.getClass();
```

在这种情况下， [`java.util.Set`](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)是类型对象的接口 [`java.util.HashSet`](https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html)。返回的值 [`getClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#getClass--)是对应的类 [`java.util.HashSet`](https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html)。

## .class语法

如果类型可用但没有实例，那么可以[`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)通过附加`".class"`类型的名称来获得a 。这也是获取[`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)原始类型的最简单方法 。

```java
boolean b;
Class c = b.getClass();   // compile-time error

Class c = boolean.class;  // correct
```

请注意，该语句`boolean.getClass()`将产生编译时错误，因为a `boolean`是基本类型，无法解除引用。该`.class`语法返回 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)对应于该类型`boolean`。

```java
Class c = java.io.PrintStream.class;
```

变量`c`将 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)与类型对应 [`java.io.PrintStream`](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html)。

```java
Class c = int[][][].class;
```

该`.class`语法可用于检索[`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)对应于给定类型的多维阵列的语法 。

## 的Class.forName()

如果类的完全限定名称可用，则可以[`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)使用静态方法获取相应 的名称 [`Class.forName()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#forName-java.lang.String-)。这不能用于原始类型。数组类名称的语法描述如下 [`Class.getName()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getName--)。此语法适用于引用和基元类型。

```java
Class c = Class.forName("com.duke.MyLocaleServiceProvider");
```

此语句将根据给定的完全限定名称创建一个类。

```java
Class cDoubleArray = Class.forName("[D");

Class cStringArray = Class.forName("[[Ljava.lang.String;");
```

该变量`cDoubleArray`将包含 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)对应于基本类型的数组`double`（即相同`double[].class`）。该`cStringArray`变量将包含 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)对应于二维数组 [`String`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)（即相同`String[][].class`）。

## 原始类型包装的TYPE字段

该`.class`语法是一种更方便，以获得优选的方式 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)为一个基本类型; 然而，有另一种方式来获得 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)。每个基本类型`void`都有一个包装类 [`java.lang`](https://docs.oracle.com/javase/8/docs/api/java/lang/package-summary.html)，用于将基元类型装箱到引用类型。每个包装类都包含一个名为的字段`TYPE`，该字段等于 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)被包装的基本类型。

```java
Class c = Double.TYPE;
```

还有一类 [`java.lang.Double`](https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html)是用来包裹基本类型`double`每当 [`Object`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)是必需的。价值 [`Double.TYPE`](https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html#TYPE)与`double.class`。相同。

```java
Class c = Void.TYPE;
```

[`Void.TYPE`](https://docs.oracle.com/javase/8/docs/api/java/lang/Void.html#TYPE)是完全相同的`void.class`。

## 返回类的方法

有几个Reflection API可以返回类，但只有在[`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)已经直接或间接获得的情况下才能访问这些类 。

- [`Class.getSuperclass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getSuperclass--)

  返回给定类的超类。

```java
Class c = javax.swing.JButton.class.getSuperclass();
```

- [`Class.getClasses()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getClasses--)

  返回作为类成员的所有公共类，接口和枚举，包括继承的成员。

  ```java
  Class<?>[] c = Character.class.getClasses();
  ```

- [`Class.getDeclaringClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getDeclaringClass--) [`java.lang.reflect.Field.getDeclaringClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html#getDeclaringClass--) [`java.lang.reflect.Method.getDeclaringClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#getDeclaringClass--) [`java.lang.reflect.Constructor.getDeclaringClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#getDeclaringClass--)

  返回 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)声明这些成员的位置。 [匿名类声明](https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.9.5)不会有声明类，但会有一个封闭类。

```java
import java.lang.reflect.Field;

Field f = System.class.getField("out");
Class c = f.getDeclaringClass();
```

该字段 [`out`](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#out)在中声明 [`System`](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html)。

```java
public class MyClass {
    static Object o = new Object() {
        public void m() {} 
    };
    static Class<c> = o.getClass().getEnclosingClass();
}
```

- [`Class.getEnclosingClass()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getEnclosingClass--)

  返回类的直接封闭类。

```java
Class c = Thread.State.class().getEnclosingClass();
```

枚举的封闭类 [`Thread.State`](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html)是 [`Thread`](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html)。

```java
public class MyClass {
    static Object o = new Object() { 
        public void m() {} 
    };
    static Class<c> = o.getClass().getEnclosingClass();
}
```

定义的匿名类`o`包含在`MyClass`。