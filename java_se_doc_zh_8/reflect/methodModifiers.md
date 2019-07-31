# 检索和解析方法修饰符

有几个修饰符可能是方法声明的一部分：

- 访问修饰符：`public`，`protected`，和`private`
- 修饰符限制为一个实例： `static`
- 修饰符禁止修改值： `final`
- 需要覆盖的修饰符： `abstract`
- 防止重入的修饰符： `synchronized`
- 修饰符指示另一种编程语言的实现： `native`
- 修饰符强制严格的浮点行为： `strictfp`
- 注释

该 [`MethodModifierSpy`](example/MethodModifierSpy.java)示例列出了具有给定名称的方法的修饰符。它还显示方法是合成的（编译器生成的），可变的arity还是桥接方法（编译器生成的以支持通用接口）。

```java
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static java.lang.System.out;

public class MethodModifierSpy {

    private static int count;
    private static synchronized void inc() { count++; }
    private static synchronized int cnt() { return count; }

    public static void main(String... args) {
	try {
	    Class<?> c = Class.forName(args[0]);
	    Method[] allMethods = c.getDeclaredMethods();
	    for (Method m : allMethods) {
		if (!m.getName().equals(args[1])) {
		    continue;
		}
		out.format("%s%n", m.toGenericString());
		out.format("  Modifiers:  %s%n",
			   Modifier.toString(m.getModifiers()));
		out.format("  [ synthetic=%-5b var_args=%-5b bridge=%-5b ]%n",
			   m.isSynthetic(), m.isVarArgs(), m.isBridge());
		inc();
	    }
	    out.format("%d matching overload%s found%n", cnt(),
		       (cnt() == 1 ? "" : "s"));

        // production code should handle this exception more gracefully
	} catch (ClassNotFoundException x) {
	    x.printStackTrace();
	}
    }
}
```

输出的几个例子 [`MethodModifierSpy`](example/MethodModifierSpy.java)产生如下。

```bash
$ java MethodModifierSpy java.lang.Object wait
public final void java.lang.Object.wait() throws java.lang.InterruptedException
  Modifiers:  public final
  [ synthetic=false var_args=false bridge=false ]
public final void java.lang.Object.wait(long,int)
  throws java.lang.InterruptedException
  Modifiers:  public final
  [ synthetic=false var_args=false bridge=false ]
public final native void java.lang.Object.wait(long)
  throws java.lang.InterruptedException
  Modifiers:  public final native
  [ synthetic=false var_args=false bridge=false ]
3 matching overloads found
$ java MethodModifierSpy java.lang.StrictMath toRadians
public static double java.lang.StrictMath.toRadians(double)
  Modifiers:  public static strictfp
  [ synthetic=false var_args=false bridge=false ]
1 matching overload found
$ java MethodModifierSpy MethodModifierSpy inc
private synchronized void MethodModifierSpy.inc()
  Modifiers: private synchronized
  [ synthetic=false var_args=false bridge=false ]
1 matching overload found
$ java MethodModifierSpy java.lang.Class getConstructor
public java.lang.reflect.Constructor<T> java.lang.Class.getConstructor
  (java.lang.Class<T>[]) throws java.lang.NoSuchMethodException,
  java.lang.SecurityException
  Modifiers: public transient
  [ synthetic=false var_args=true bridge=false ]
1 matching overload found
$ java MethodModifierSpy java.lang.String compareTo
public int java.lang.String.compareTo(java.lang.String)
  Modifiers: public
  [ synthetic=false var_args=false bridge=false ]
public int java.lang.String.compareTo(java.lang.Object)
  Modifiers: public volatile
  [ synthetic=true  var_args=false bridge=true  ]
2 matching overloads found
```

需要注意的是 [`Method.isVarArgs()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#isVarArgs--)返回`true`的 [`Class.getConstructor()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getConstructor-java.lang.Class...-)。这表明方法声明如下所示：

```java
public Constructor<T> getConstructor(Class<?>... parameterTypes)
```

不是这样的：

```java
public Constructor<T> getConstructor(Class<?> [] parameterTypes)
```

请注意，输出 [`String.compareTo()`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#compareTo-java.lang.String-)包含两个方法。声明的方法`String.java`：

```java
public int compareTo(String anotherString);
```

以及第二种*合成*或编译器生成的*桥接*方法。发生这种情况是因为 [`String`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)实现了参数化接口 [`Comparable`](https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html)。在类型擦除期间，继承方法的参数类型 [`Comparable.compareTo()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html#compareTo-T-)从更改`java.lang.Object`为`java.lang.String`。由于参数类型的`compareTo`的方法`Comparable`和`String`擦除之后不再匹配，则无法进行覆盖。在所有其他情况下，这将产生编译时错误，因为未实现接口。增加桥接方法可以避免这个问题。

[`Method`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html)实施 [`java.lang.reflect.AnnotatedElement`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AnnotatedElement.html)。因此，[`java.lang.annotation.RetentionPolicy.RUNTIME`](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/RetentionPolicy.html#RUNTIME)可以检索任何运行时注释 。有关获取注释的示例，请参阅“ [检查类修饰符和类型](../class/classModifiers.html) ”一节。