# 故障排除

本节包含开发人员在使用反射来查找，调用或获取有关方法的信息时可能遇到的问题的示例。

## NoSuchMethodException由于类型擦除

该 [`MethodTrouble`](example/MethodTrouble.java)示例说明了在类中搜索特定方法的代码未考虑类型擦除时会发生什么。

```java
import java.lang.reflect.Method;

public class MethodTrouble<T>  {
    public void lookup(T t) {}
    public void find(Integer i) {}

    public static void main(String... args) {
	try {
	    String mName = args[0];
	    Class cArg = Class.forName(args[1]);
	    Class<?> c = (new MethodTrouble<Integer>()).getClass();
	    Method m = c.getMethod(mName, cArg);
	    System.out.format("Found:%n  %s%n", m.toGenericString());

        // production code should handle these exceptions more gracefully
	} catch (NoSuchMethodException x) {
	    x.printStackTrace();
	} catch (ClassNotFoundException x) {
	    x.printStackTrace();
	}
    }
}
$ java MethodTrouble lookup java.lang.Integer
java.lang.NoSuchMethodException: MethodTrouble.lookup(java.lang.Integer)
        at java.lang.Class.getMethod(Class.java:1605)
        at MethodTrouble.main(MethodTrouble.java:12)
$ java MethodTrouble lookup java.lang.Object
Found:
  public void MethodTrouble.lookup(T)
```

当使用泛型参数类型声明方法时，编译器将使用其上限替换泛型类型，在本例中为`T`is 的上限[`Object`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)。因此，当代码搜索时`lookup(Integer)`，没有找到任何方法，尽管实例`MethodTrouble`创建如下：

```java
Class<?> c = (new MethodTrouble<Integer>()).getClass();
```

`lookup(Object)`按预期搜索成功。

```java
$ java MethodTrouble find java.lang.Integer
Found:
  public void MethodTrouble.find(java.lang.Integer)
$ java MethodTrouble find java.lang.Object
java.lang.NoSuchMethodException: MethodTrouble.find(java.lang.Object)
        at java.lang.Class.getMethod(Class.java:1605)
        at MethodTrouble.main(MethodTrouble.java:12)
```

在这种情况下，`find()`没有通用参数，因此搜索的参数类型 [`getMethod()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getMethod-java.lang.String-java.lang.Class...-)必须完全匹配。

------

**提示：**  搜索方法时，始终传递参数化类型的上限。

## 调用方法时出现IllegalAccessException

[`IllegalAccessException`](https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalAccessException.html)如果尝试调用`private`或以其他方式无法访问的方法，则抛出An 。

该 [`MethodTroubleAgain`](example/MethodTroubleAgain.java)示例显示了典型的堆栈跟踪，这是通过尝试在另一个类中调用私有方法而产生的。

```java
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class AnotherClass {
    private void m() {}
}

public class MethodTroubleAgain {
    public static void main(String... args) {
	AnotherClass ac = new AnotherClass();
	try {
	    Class<?> c = ac.getClass();
 	    Method m = c.getDeclaredMethod("m");
//  	    m.setAccessible(true);      // solution
 	    Object o = m.invoke(ac);    // IllegalAccessException

        // production code should handle these exceptions more gracefully
	} catch (NoSuchMethodException x) {
	    x.printStackTrace();
	} catch (InvocationTargetException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	}
    }
}
```

抛出异常的堆栈跟踪如下。

```java
$ java MethodTroubleAgain
java.lang.IllegalAccessException: Class MethodTroubleAgain can not access a
  member of class AnotherClass with modifiers "private"
        at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:65)
        at java.lang.reflect.Method.invoke(Method.java:588)
        at MethodTroubleAgain.main(MethodTroubleAgain.java:15)
```

**提示：**  存在访问限制，阻止反射调用通常无法通过直接调用访问的方法。（这包括---但不限于--- 单独的类中的方法和单独的私有类中的公共方法。）但是，声明为extend ，它提供了通过抑制此检查的能力 。如果成功，则此方法对象的后续调用不会因此问题而失败。

## Method.invoke（）中的IllegalArgumentException

[`Method.invoke()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#invoke-java.lang.Object-java.lang.Object...-)已被改装为可变方法。这是一个巨大的便利，但它可能导致意外的行为。该 [`MethodTroubleToo`](example/MethodTroubleToo.java)示例显示了[`Method.invoke()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#invoke-java.lang.Object-java.lang.Object...-)可能产生令人困惑的结果的各种方式 。

```java
import java.lang.reflect.Method;

public class MethodTroubleToo {
    public void ping() { System.out.format("PONG!%n"); }

    public static void main(String... args) {
	try {
	    MethodTroubleToo mtt = new MethodTroubleToo();
	    Method m = MethodTroubleToo.class.getMethod("ping");

 	    switch(Integer.parseInt(args[0])) {
	    case 0:
  		m.invoke(mtt);                 // works
		break;
	    case 1:
 		m.invoke(mtt, null);           // works (expect compiler warning)
		break;
	    case 2:
		Object arg2 = null;
		m.invoke(mtt, arg2);           // IllegalArgumentException
		break;
	    case 3:
		m.invoke(mtt, new Object[0]);  // works
		break;
	    case 4:
		Object arg4 = new Object[0];
		m.invoke(mtt, arg4);           // IllegalArgumentException
		break;
	    default:
		System.out.format("Test not found%n");
	    }

        // production code should handle these exceptions more gracefully
	} catch (Exception x) {
	    x.printStackTrace();
	}
    }
}
$ java MethodTroubleToo 0
PONG!
```

由于[`Method.invoke()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#invoke-java.lang.Object-java.lang.Object...-)除了第一个参数之外的所有参数 都是可选的，因此当要调用的方法没有参数时，可以省略它们。

```java
$ java MethodTroubleToo 1
PONG!
```

在这种情况下，代码生成此编译器警告，因为它`null`是不明确的。

```java
$ javac MethodTroubleToo.java
MethodTroubleToo.java:16: warning: non-varargs call of varargs method with
  inexact argument type for last parameter;
 		m.invoke(mtt, null);           // works (expect compiler warning)
 		              ^
  cast to Object for a varargs call
  cast to Object[] for a non-varargs call and to suppress this warning
1 warning
```

无法确定是`null`表示空数组的参数还是第一个参数`null`。

```java
$ java MethodTroubleToo 2
java.lang.IllegalArgumentException: wrong number of arguments
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke
          (NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke
          (DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at MethodTroubleToo.main(MethodTroubleToo.java:21)
```

尽管参数是这样的，但是失败了`null`，因为类型是a [`Object`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)并且`ping()`根本不需要参数。

```java
$ java MethodTroubleToo 3
PONG!
```

这是因为`new Object[0]`创建一个空数组，而对于varargs方法，这相当于不传递任何可选参数。

```java
$ java MethodTroubleToo 4
java.lang.IllegalArgumentException: wrong number of arguments
        at sun.reflect.NativeMethodAccessorImpl.invoke0
          (Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke
          (NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke
          (DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at MethodTroubleToo.main(MethodTroubleToo.java:28)
```

与前面的示例不同，如果空数组存储在a中 [`Object`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)，则将其视为 [`Object`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)。这失败的原因与案例2失败的原因相同，`ping()`并不期望参数。

------

**提示：**  当声明方法时，编译器会将传递给的所有参数放入类型数组中 。执行与声明它的相同。理解这可能有助于避免上述问题的类型。

## 调用方法失败时的InvocationTargetException

一个 [`InvocationTargetException`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/InvocationTargetException.html)涡卷被调用的方法对象时所有的异常（选中和未选中）制作。该 [`MethodTroubleReturns`](example/MethodTroubleReturns.java)示例显示如何检索调用方法抛出的原始异常。

```java
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodTroubleReturns {
    private void drinkMe(int liters) {
	if (liters < 0)
	    throw new IllegalArgumentException("I can't drink a negative amount of liquid");
    }

    public static void main(String... args) {
	try {
	    MethodTroubleReturns mtr  = new MethodTroubleReturns();
 	    Class<?> c = mtr.getClass();
   	    Method m = c.getDeclaredMethod("drinkMe", int.class);
	    m.invoke(mtr, -1);

        // production code should handle these exceptions more gracefully
	} catch (InvocationTargetException x) {
	    Throwable cause = x.getCause();
	    System.err.format("drinkMe() failed: %s%n", cause.getMessage());
	} catch (Exception x) {
	    x.printStackTrace();
	}
    }
}
$ java MethodTroubleReturns
drinkMe() failed: I can't drink a negative amount of liquid
```

------

**提示：**  如果 抛出a，则调用该方法。问题的诊断与直接调用该方法并抛出由其检索的异常相同 。此异常并不表示反射包或其用法存在问题。