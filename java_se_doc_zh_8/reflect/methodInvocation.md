# 调用方法

Reflection提供了一种在类上调用方法的方法。通常，只有在无法在非反射代码中将类的实例强制转换为所需类型时才需要这样做。调用方法 [`java.lang.reflect.Method.invoke()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#invoke-java.lang.Object-java.lang.Object...-)。第一个参数是要在其上调用此特定方法的对象实例。（如果方法是`static`，则第一个参数应该是`null`。）后续参数是方法的参数。如果底层方法抛出异常，它将被一个包装 [`java.lang.reflect.InvocationTargetException`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/InvocationTargetException.html)。可以使用异常链接机制的[`InvocationTargetException.getCause()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/InvocationTargetException.html#getCause--)方法检索方法的原始异常 。

## 查找并调用具有特定声明的方法

考虑一个测试套件，它使用反射来调用给定类中的私有测试方法。该 [`Deet`](example/Deet.java)示例搜索以`public`字符串“ `test`” 开头的类中的方法，具有布尔返回类型和单个 [`Locale`](https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html)参数。然后它调用每个匹配方法。

```java
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Locale;
import static java.lang.System.out;
import static java.lang.System.err;

public class Deet<T> {
    private boolean testDeet(Locale l) {
	// getISO3Language() may throw a MissingResourceException
	out.format("Locale = %s, ISO Language Code = %s%n", l.getDisplayName(), l.getISO3Language());
	return true;
    }

    private int testFoo(Locale l) { return 0; }
    private boolean testBar() { return true; }

    public static void main(String... args) {
	if (args.length != 4) {
	    err.format("Usage: java Deet <classname> <langauge> <country> <variant>%n");
	    return;
	}

	try {
	    Class<?> c = Class.forName(args[0]);
	    Object t = c.newInstance();

	    Method[] allMethods = c.getDeclaredMethods();
	    for (Method m : allMethods) {
		String mname = m.getName();
		if (!mname.startsWith("test")
		    || (m.getGenericReturnType() != boolean.class)) {
		    continue;
		}
 		Type[] pType = m.getGenericParameterTypes();
 		if ((pType.length != 1)
		    || Locale.class.isAssignableFrom(pType[0].getClass())) {
 		    continue;
 		}

		out.format("invoking %s()%n", mname);
		try {
		    m.setAccessible(true);
		    Object o = m.invoke(t, new Locale(args[1], args[2], args[3]));
		    out.format("%s() returned %b%n", mname, (Boolean) o);

		// Handle any exceptions thrown by method to be invoked.
		} catch (InvocationTargetException x) {
		    Throwable cause = x.getCause();
		    err.format("invocation of %s failed: %s%n",
			       mname, cause.getMessage());
		}
	    }

        // production code should handle these exceptions more gracefully
	} catch (ClassNotFoundException x) {
	    x.printStackTrace();
	} catch (InstantiationException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	}
    }
}
```

`Deet`invokes [`getDeclaredMethods()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getDeclaredMethods--)将返回在类中显式声明的所有方法。此外， [`Class.isAssignableFrom()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#isAssignableFrom-java.lang.Class-)还用于确定定位方法的参数是否与所需的调用兼容。从技术上讲，代码可能已经测试了下面的语句是`true`因为 [`Locale`](https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html)是`final`：

```java
Locale.class == pType[0].getClass()
```

但是， [`Class.isAssignableFrom()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#isAssignableFrom-java.lang.Class-)更一般。

```bash
$ java Deet Deet ja JP JP
invoking testDeet()
Locale = Japanese (Japan,JP), 
ISO Language Code = jpn
testDeet() returned true
$ java Deet Deet xx XX XX
invoking testDeet()
invocation of testDeet failed: 
Couldn't find 3-letter language code for xx
```

首先，请注意，只`testDeet()`满足代码强制执行的声明限制。接下来，当`testDeet()`传递一个无效参数时，它会抛出一个未选中的参数 [`java.util.MissingResourceException`](https://docs.oracle.com/javase/8/docs/api/java/util/MissingResourceException.html)。反思中，在处理已检查和未检查的异常方面没有区别。他们都被包裹着 [`InvocationTargetException`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/InvocationTargetException.html)

## 调用具有可变数量的参数的方法

[`Method.invoke()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#invoke-java.lang.Object-java.lang.Object...-)可用于将可变数量的参数传递给方法。要理解的关键概念是实现变量arity的方法，就好像变量参数被打包在一个数组中一样。

该 [`InvokeMain`](example/InvokeMain.java)示例说明了如何`main()`在任何类中调用入口点并传递在运行时确定的一组参数。

```java
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class InvokeMain {
    public static void main(String... args) {
	try {
	    Class<?> c = Class.forName(args[0]);
	    Class[] argTypes = new Class[] { String[].class };
	    Method main = c.getDeclaredMethod("main", argTypes);
  	    String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
	    System.out.format("invoking %s.main()%n", c.getName());
	    main.invoke(null, (Object)mainArgs);

        // production code should handle these exceptions more gracefully
	} catch (ClassNotFoundException x) {
	    x.printStackTrace();
	} catch (NoSuchMethodException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	} catch (InvocationTargetException x) {
	    x.printStackTrace();
	}
    }
}
```

首先，找到`main()`方法为具有名称“主”与是阵列的单个参数的一类的代码的搜索 [`String`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)。由于`main()`IS `static`，`null`是的第一个参数 [`Method.invoke()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#invoke-java.lang.Object-java.lang.Object...-)。第二个参数是要传递的参数数组。

```bash
$ java InvokeMain Deet Deet ja JP JP
invoking Deet.main()
invoking testDeet()
Locale = Japanese (Japan,JP), 
ISO Language Code = jpn
testDeet() returned true
```

