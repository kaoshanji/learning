# 检查类修饰符和类型

可以使用一个或多个影响其运行时行为的修饰符声明一个类：

- 访问修饰符：`public`，`protected`，和`private`
- 需要覆盖的修饰符： `abstract`
- 修饰符限制为一个实例： `static`
- 修饰符禁止修改值： `final`
- 修饰符强制严格的浮点行为： `strictfp`
- 注释

并非所有类都允许使用所有修饰符，例如接口不能`final`和枚举不能`abstract`。 [`java.lang.reflect.Modifier`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Modifier.html)包含所有可能修饰符的声明。它还包含可用于解码返回的修改器集的方法 [`Class.getModifiers()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getModifiers--)。

该 [`ClassDeclarationSpy`](example/ClassDeclarationSpy.java)示例显示如何获取类的声明组件，包括修饰符，泛型类型参数，实现的接口和继承路径。由于 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)实现了 [`java.lang.reflect.AnnotatedElement`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AnnotatedElement.html)接口，因此还可以查询运行时注释。

```java
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.out;

public class ClassDeclarationSpy {
    public static void main(String... args) {
	try {
	    Class<?> c = Class.forName(args[0]);
	    out.format("Class:%n  %s%n%n", c.getCanonicalName());
	    out.format("Modifiers:%n  %s%n%n",
		       Modifier.toString(c.getModifiers()));

	    out.format("Type Parameters:%n");
	    TypeVariable[] tv = c.getTypeParameters();
	    if (tv.length != 0) {
		out.format("  ");
		for (TypeVariable t : tv)
		    out.format("%s ", t.getName());
		out.format("%n%n");
	    } else {
		out.format("  -- No Type Parameters --%n%n");
	    }

	    out.format("Implemented Interfaces:%n");
	    Type[] intfs = c.getGenericInterfaces();
	    if (intfs.length != 0) {
		for (Type intf : intfs)
		    out.format("  %s%n", intf.toString());
		out.format("%n");
	    } else {
		out.format("  -- No Implemented Interfaces --%n%n");
	    }

	    out.format("Inheritance Path:%n");
	    List<Class> l = new ArrayList<Class>();
	    printAncestor(c, l);
	    if (l.size() != 0) {
		for (Class<?> cl : l)
		    out.format("  %s%n", cl.getCanonicalName());
		out.format("%n");
	    } else {
		out.format("  -- No Super Classes --%n%n");
	    }

	    out.format("Annotations:%n");
	    Annotation[] ann = c.getAnnotations();
	    if (ann.length != 0) {
		for (Annotation a : ann)
		    out.format("  %s%n", a.toString());
		out.format("%n");
	    } else {
		out.format("  -- No Annotations --%n%n");
	    }

        // production code should handle this exception more gracefully
	} catch (ClassNotFoundException x) {
	    x.printStackTrace();
	}
    }

    private static void printAncestor(Class<?> c, List<Class> l) {
	Class<?> ancestor = c.getSuperclass();
 	if (ancestor != null) {
	    l.add(ancestor);
	    printAncestor(ancestor, l);
 	}
    }
}
```

接下来是一些输出样本。用户输入以斜体显示。

```bash
$ java ClassDeclarationSpy java.util.concurrent.ConcurrentNavigableMap
Class:
  java.util.concurrent.ConcurrentNavigableMap

Modifiers:
  public abstract interface

Type Parameters:
  K V

Implemented Interfaces:
  java.util.concurrent.ConcurrentMap<K, V>
  java.util.NavigableMap<K, V>

Inheritance Path:
  -- No Super Classes --

Annotations:
  -- No Annotations --
```

这是[`java.util.concurrent.ConcurrentNavigableMap`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentNavigableMap.html)源代码中的实际声明 ：

```java
public interface ConcurrentNavigableMap<K,V>
    extends ConcurrentMap<K,V>, NavigableMap<K,V>
```

请注意，由于这是一个接口，因此它是隐式的`abstract`。编译器为每个接口添加此修饰符。此外，此声明包含两个泛型类型参数，`K`和`V`。示例代码只是打印这些参数的名称，但是可以使用方法检索有关它们的其他信息[`java.lang.reflect.TypeVariable`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/TypeVariable.html)。接口还可以实现如上所示的其他接口。

```bash
$ java ClassDeclarationSpy "[Ljava.lang.String;"
Class:
  java.lang.String[]

Modifiers:
  public abstract final

Type Parameters:
  -- No Type Parameters --

Implemented Interfaces:
  interface java.lang.Cloneable
  interface java.io.Serializable

Inheritance Path:
  java.lang.Object

Annotations:
  -- No Annotations --
```

由于数组是运行时对象，因此所有类型信息都由Java虚拟机定义。特别是，数组实现 [`Cloneable`](https://docs.oracle.com/javase/8/docs/api/java/lang/Cloneable.html)并且 [`java.io.Serializable`](https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html)它们的直接超类总是如此 [`Object`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)。

```bash
$ java ClassDeclarationSpy java.io.InterruptedIOException
Class:
  java.io.InterruptedIOException

Modifiers:
  public

Type Parameters:
  -- No Type Parameters --

Implemented Interfaces:
  -- No Implemented Interfaces --

Inheritance Path:
  java.io.IOException
  java.lang.Exception
  java.lang.Throwable
  java.lang.Object

Annotations:
  -- No Annotations --
```

从继承路径可以推断出，这 [`java.io.InterruptedIOException`](https://docs.oracle.com/javase/8/docs/api/java/io/InterruptedIOException.html)是一个已检查的异常，因为 [`RuntimeException`](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html)它不存在。

```bash
$ java ClassDeclarationSpy java.security.Identity
Class:
  java.security.Identity

Modifiers:
  public abstract

Type Parameters:
  -- No Type Parameters --

Implemented Interfaces:
  interface java.security.Principal
  interface java.io.Serializable

Inheritance Path:
  java.lang.Object

Annotations:
  @java.lang.Deprecated()
```

此输出显示 [`java.security.Identity`](https://docs.oracle.com/javase/8/docs/api/java/security/Identity.html)，已弃用的API具有注释 [`java.lang.Deprecated`](https://docs.oracle.com/javase/8/docs/api/java/lang/Deprecated.html)。反射代码可以使用它来检测已弃用的API。

------

**注意：**  并非所有注释都可通过反射获得。只有那些拥有 的 都可以访问。三个注释的语言预先定义的 ， 以及 只在运行时可用。