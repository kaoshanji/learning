# 故障排除

以下示例显示了在阵列上操作时可能出现的典型错误。

## 由于Inconvertible类型导致的IllegalArgumentException

该 [`ArrayTroubleAgain`](example/ArrayTroubleAgain.java)示例将生成一个 [`IllegalArgumentException`](https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalArgumentException.html)。 [`Array.setInt()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html#setInt-java.lang.Object-int-int-)调用以设置具有`Integer`基本类型值的引用类型的组件`int`。在非反射等价物中`ary[0] = 1`，编译器会将值转换（或*框*）`1`为引用类型，`new Integer(1)`以便其类型检查将接受该语句。使用反射时，类型检查仅在运行时进行，因此无法对值进行装箱。

```java
import java.lang.reflect.Array;
import static java.lang.System.err;

public class ArrayTroubleAgain {
    public static void main(String... args) {
	Integer[] ary = new Integer[2];
	try {
	    Array.setInt(ary, 0, 1);  // IllegalArgumentException

        // production code should handle these exceptions more gracefully
	} catch (IllegalArgumentException x) {
	    err.format("Unable to box%n");
	} catch (ArrayIndexOutOfBoundsException x) {
	    x.printStackTrace();
	}
    }
}
$ java ArrayTroubleAgain
Unable to box
```

要消除此异常，有问题的行应替换为以下调用 [`Array.set(Object array, int index, Object value)`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html#set-java.lang.Object-int-java.lang.Object-)：

```java
Array.set(ary, 0, new Integer(1));
```

------

**提示：**  使用反射设置或获取数组组件时，编译器没有机会执行装箱。它只能转换规范所描述的相关类型 。该示例预计会失败，因为将在此测试中返回，可以通过编程方式验证是否可以进行特定转换：

```java
Integer.class.isAssignableFrom(int.class) == false 
```

类似地，在反射中也不可能从原始类型到引用类型的自动转换。

```java
int.class.isAssignableFrom（Integer.class）== false
```

------

## 空数组的ArrayIndexOutOfBoundsException

该 [`ArrayTrouble`](example/ArrayTrouble.java)示例说明了在尝试访问零长度数组的元素时将发生的错误：

```java
import java.lang.reflect.Array;
import static java.lang.System.out;

public class ArrayTrouble {
    public static void main(String... args) {
        Object o = Array.newInstance(int.class, 0);
        int[] i = (int[])o;
        int[] j = new int[0];
        out.format("i.length = %d, j.length = %d, args.length = %d%n",
                   i.length, j.length, args.length);
        Array.getInt(o, 0);  // ArrayIndexOutOfBoundsException
    }
}
$ java ArrayTrouble
i.length = 0, j.length = 0, args.length = 0
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException
        at java.lang.reflect.Array.getInt(Native Method)
        at ArrayTrouble.main(ArrayTrouble.java:11)
```

------

**提示：**  可以使数组没有元素（空数组）。在常见代码中只有少数情况可见，但它们可能会在无意中发生反射。当然，不可能设置/获取空数组的值，因为 将抛出。

## 如果尝试缩小，则为IllegalArgumentException

该 [`ArrayTroubleToo`](example/ArrayTroubleToo.java)示例包含失败的代码，因为它尝试执行可能会丢失数据的操作：

```java
import java.lang.reflect.Array;
import static java.lang.System.out;

public class ArrayTroubleToo {
    public static void main(String... args) {
        Object o = new int[2];
        Array.setShort(o, 0, (short)2);  // widening, succeeds
        Array.setLong(o, 1, 2L);         // narrowing, fails
    }
}
$ java ArrayTroubleToo
Exception in thread "main" java.lang.IllegalArgumentException: argument type
  mismatch
        at java.lang.reflect.Array.setLong(Native Method)
        at ArrayTroubleToo.main(ArrayTroubleToo.java:9)
```

**提示：**  在`Array.set*()`和`Array.get*()`方法将执行自动的宽化转换，但会抛出 [`IllegalArgumentException`](https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalArgumentException.html)，如果收缩转换尝试。有关扩展和缩小转换的完整讨论，请参阅[*Java语言规范，Java SE 7 Edition*](https://docs.oracle.com/javase/specs/jls/se7/html/index.html)，分别扩展[原始转换](https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2)和[缩小原始转换](https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3)。