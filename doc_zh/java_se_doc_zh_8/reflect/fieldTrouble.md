# 故障排除

以下是开发人员遇到的一些常见问题，解释了为什么会发生以及如何解决这些问题。

## 由于Inconvertible类型导致的IllegalArgumentException

该 [`FieldTrouble`](example/FieldTrouble.java)示例将生成一个 [`IllegalArgumentException`](https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalArgumentException.html)。 [`Field.setInt()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html#setInt-java.lang.Object-int-)调用以设置具有`Integer`基本类型值的引用类型的字段。在非反射等价物中`Integer val = 42`，编译器将原始类型转换（或*框*）`42`为引用类型，`new Integer(42)`以便其类型检查将接受该语句。使用反射时，类型检查仅在运行时进行，因此无法对值进行装箱。

```java
import java.lang.reflect.Field;

public class FieldTrouble {
    public Integer val;

    public static void main(String... args) {
	FieldTrouble ft = new FieldTrouble();
	try {
	    Class<?> c = ft.getClass();
	    Field f = c.getDeclaredField("val");
  	    f.setInt(ft, 42);               // IllegalArgumentException

        // production code should handle these exceptions more gracefully
	} catch (NoSuchFieldException x) {
	    x.printStackTrace();
 	} catch (IllegalAccessException x) {
 	    x.printStackTrace();
	}
    }
}
$ java FieldTrouble
Exception in thread "main" java.lang.IllegalArgumentException: Can not set
  java.lang.Object field FieldTrouble.val to (long)42
        at sun.reflect.UnsafeFieldAccessorImpl.throwSetIllegalArgumentException
          (UnsafeFieldAccessorImpl.java:146)
        at sun.reflect.UnsafeFieldAccessorImpl.throwSetIllegalArgumentException
          (UnsafeFieldAccessorImpl.java:174)
        at sun.reflect.UnsafeObjectFieldAccessorImpl.setLong
          (UnsafeObjectFieldAccessorImpl.java:102)
        at java.lang.reflect.Field.setLong(Field.java:831)
        at FieldTrouble.main(FieldTrouble.java:11)
```

要消除此异常，有问题的行应替换为以下调用 [`Field.set(Object obj, Object value)`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html#set-java.lang.Object-java.lang.Object-)：

```java
f.set(ft, new Integer(43));
```

------

**提示：**  使用反射设置或获取字段时，编译器没有机会执行装箱。它只能转换规范所描述的相关类型 。该示例预计会失败，因为将在此测试中返回，可以通过编程方式验证是否可以进行特定转换：

类似地，在反射中也不可能从原始类型到引用类型的自动转换。

```java
int.class.isAssignableFrom（Integer.class）== false
```

------

## 非公共字段的NoSuchFieldException

精明的读者可能会注意到，如果[`FieldSpy`](example/FieldSpy.java)前面显示的 示例用于获取非公共字段的信息，它将失败：

```java
$ java FieldSpy java.lang.String count
java.lang.NoSuchFieldException：count
        在java.lang.Class.getField（Class.java:1519）
        在FieldSpy.main（FieldSpy.java:12）
```

**提示：**  该 和 方法返回类，枚举或由所表示的接口的构件字段（一个或多个）对象。要检索声明（但未继承）的所有字段，请使用该 方法。

## 修改最终字段时出现IllegalAccessException

一个 [`IllegalAccessException`](https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalAccessException.html)如果试图获取或设置的值可以被抛出`private`或以其他方式无法进入现场或者设置的值`final`字段（无论其访问修饰符）。

该 [`FieldTroubleToo`](example/FieldTroubleToo.java)示例说明了尝试设置最终字段时产生的堆栈跟踪类型。

```java
import java.lang.reflect.Field;

public class FieldTroubleToo {
    public final boolean b = true;

    public static void main(String... args) {
	FieldTroubleToo ft = new FieldTroubleToo();
	try {
	    Class<?> c = ft.getClass();
	    Field f = c.getDeclaredField("b");
// 	    f.setAccessible(true);  // solution
	    f.setBoolean(ft, Boolean.FALSE);   // IllegalAccessException

        // production code should handle these exceptions more gracefully
	} catch (NoSuchFieldException x) {
	    x.printStackTrace();
	} catch (IllegalArgumentException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	}
    }
}
$ java FieldTroubleToo
java.lang.IllegalAccessException: Can not set final boolean field
  FieldTroubleToo.b to (boolean)false
        at sun.reflect.UnsafeFieldAccessorImpl.
          throwFinalFieldIllegalAccessException(UnsafeFieldAccessorImpl.java:55)
        at sun.reflect.UnsafeFieldAccessorImpl.
          throwFinalFieldIllegalAccessException(UnsafeFieldAccessorImpl.java:63)
        at sun.reflect.UnsafeQualifiedBooleanFieldAccessorImpl.setBoolean
          (UnsafeQualifiedBooleanFieldAccessorImpl.java:78)
        at java.lang.reflect.Field.setBoolean(Field.java:686)
        at FieldTroubleToo.main(FieldTroubleToo.java:12)
```

------

**提示：**  存在访问限制，可防止`final`在初始化类之后设置字段。但是，`Field`声明为extend [`AccessibleObject`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AccessibleObject.html)，它提供了抑制此检查的能力。https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AccessibleObject.html#setAccessible-boolean-)

如果 [`AccessibleObject.setAccessible()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AccessibleObject.html#setAccessible-boolean-)成功，那么对该字段值的后续操作将不会对此问题造成影响。这可能会产生意想不到的副作用; 例如，有时原始值将继续被应用程序的某些部分使用，即使该值已被修改。[`AccessibleObject.setAccessible()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AccessibleObject.html#setAccessible-boolean-)只有在安全上下文允许操作时才会成功。

