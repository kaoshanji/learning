# 获取方法类型信息

方法声明包括名称，修饰符，参数，返回类型和可抛出异常列表。本 [`java.lang.reflect.Method`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html)类提供了一种方法来获取此信息。

该 [`MethodSpy`](example/MethodSpy.java)示例说明了如何枚举给定类中的所有声明的方法，并检索给定名称的所有方法的返回，参数和异常类型。

```java
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import static java.lang.System.out;

public class MethodSpy {
    private static final String  fmt = "%24s: %s%n";

    // for the morbidly curious
    <E extends RuntimeException> void genericThrow() throws E {}

    public static void main(String... args) {
	try {
	    Class<?> c = Class.forName(args[0]);
	    Method[] allMethods = c.getDeclaredMethods();
	    for (Method m : allMethods) {
		if (!m.getName().equals(args[1])) {
		    continue;
		}
		out.format("%s%n", m.toGenericString());

		out.format(fmt, "ReturnType", m.getReturnType());
		out.format(fmt, "GenericReturnType", m.getGenericReturnType());

		Class<?>[] pType  = m.getParameterTypes();
		Type[] gpType = m.getGenericParameterTypes();
		for (int i = 0; i < pType.length; i++) {
		    out.format(fmt,"ParameterType", pType[i]);
		    out.format(fmt,"GenericParameterType", gpType[i]);
		}

		Class<?>[] xType  = m.getExceptionTypes();
		Type[] gxType = m.getGenericExceptionTypes();
		for (int i = 0; i < xType.length; i++) {
		    out.format(fmt,"ExceptionType", xType[i]);
		    out.format(fmt,"GenericExceptionType", gxType[i]);
		}
	    }

        // production code should handle these exceptions more gracefully
	} catch (ClassNotFoundException x) {
	    x.printStackTrace();
	}
    }
}
```

这里的输出 [`Class.getConstructor()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getConstructor-java.lang.Class...-)是具有参数化类型和可变数量参数的方法的示例。

```bash
$ java MethodSpy java.lang.Class getConstructor
public java.lang.reflect.Constructor<T> java.lang.Class.getConstructor
  (java.lang.Class<?>[]) throws java.lang.NoSuchMethodException,
  java.lang.SecurityException
              ReturnType: class java.lang.reflect.Constructor
       GenericReturnType: java.lang.reflect.Constructor<T>
           ParameterType: class [Ljava.lang.Class;
    GenericParameterType: java.lang.Class<?>[]
           ExceptionType: class java.lang.NoSuchMethodException
    GenericExceptionType: class java.lang.NoSuchMethodException
           ExceptionType: class java.lang.SecurityException
    GenericExceptionType: class java.lang.SecurityException
```

这是源代码中方法的实际声明：

```java
public Constructor<T> getConstructor(Class<?>... parameterTypes)
```

首先请注意，返回和参数类型是通用的。 [`Method.getGenericReturnType()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#getGenericReturnType--)将查询 类文件中的签名属性（如果存在）。如果该属性不可用，则它会回退， [`Method.getReturnType()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#getReturnType--)因为引入了泛型。反射中具有某些*Foo*值的名称的其他方法也是类似地实现的。`getGeneric*Foo*()`

接下来，请注意最后一个（也是唯一的）参数`parameterType`是类型的变量arity（具有可变数量的参数）`java.lang.Class`。它表示为类型的单维数组`java.lang.Class`。这可以`java.lang.Class`通过调用 明确表示的数组来区分[`Method.isVarArgs()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#isVarArgs--)。返回值的语法在`Method.get*Types()`中描述 [`Class.getName()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getName--)。

以下示例说明了具有泛型返回类型的方法。

```java
$ java MethodSpy java.lang.Class cast
public T java.lang.Class.cast(java.lang.Object)
              ReturnType: class java.lang.Object
       GenericReturnType: T
           ParameterType: class java.lang.Object
    GenericParameterType: class java.lang.Object
```

[`Class.cast()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#cast-java.lang.Object-)报告方法的泛型返回类型 是`java.lang.Object`因为泛型是通过*类型擦除*实现的，它在编译期间删除有关泛型类型的所有信息。擦除`T`由以下声明定义 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)：

```java
public final class Class<T> implements ...
```

因此`T`，在这种情况下，由类型变量的上限代替`java.lang.Object`。

最后一个示例说明了具有多个重载的方法的输出。

```java
$ java MethodSpy java.io.PrintStream format
public java.io.PrintStream java.io.PrintStream.format
  (java.util.Locale,java.lang.String,java.lang.Object[])
              ReturnType: class java.io.PrintStream
       GenericReturnType: class java.io.PrintStream
           ParameterType: class java.util.Locale
    GenericParameterType: class java.util.Locale
           ParameterType: class java.lang.String
    GenericParameterType: class java.lang.String
           ParameterType: class [Ljava.lang.Object;
    GenericParameterType: class [Ljava.lang.Object;
public java.io.PrintStream java.io.PrintStream.format
  (java.lang.String,java.lang.Object[])
              ReturnType: class java.io.PrintStream
       GenericReturnType: class java.io.PrintStream
           ParameterType: class java.lang.String
    GenericParameterType: class java.lang.String
           ParameterType: class [Ljava.lang.Object;
    GenericParameterType: class [Ljava.lang.Object;
```

如果发现了相同方法名称的多个重载，则它们都将返回 [`Class.getDeclaredMethods()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getDeclaredMethods--)。由于`format()`有两个重载（有一个 [`Locale`](https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html)和一个没有），两者都显示`MethodSpy`。

------

**注意：** 存在是因为实际上可以声明具有通用异常类型的方法。但是这很少使用，因为无法捕获通用异常类型。