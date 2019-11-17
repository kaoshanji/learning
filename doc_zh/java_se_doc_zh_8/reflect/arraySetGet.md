# 获取和设置数组及其组件

正如在非反射代码中一样，可以整体地或逐个组件地设置或检索阵列字段。要立即设置整个数组，请使用 [`java.lang.reflect.Field.set(Object obj, Object value)`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html#set-java.lang.Object-java.lang.Object-)。要检索整个数组，请使用 [`Field.get(Object)`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html#get-java.lang.Object-)。可以使用方法设置或检索单个组件 [`java.lang.reflect.Array`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html)。

[`Array`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html)提供表单的方法以及设置和获取任何基本类型的组件。例如，阵列的组件可以设置为 并且可以用其检索 。`set*Foo*()``get*Foo*()``int`[`Array.setInt(Object array, int index, int value)`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html#setInt-java.lang.Objectint-int-)[`Array.getInt(Object array, int index)`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html#getInt-java.lang.Object-int-)

这些方法支持自动*扩展*数据类型。因此， [`Array.getShort()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html#getShort-java.lang.Object-int-)可以用于设置`int`数组的值，因为16位`short`可以加宽到32位`int`而不会丢失数据; 另一方面，调用 [`Array.setLong()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html#setLong-java.lang.Object-int-long-)一个数组`int`将导致 [`IllegalArgumentException`](https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalArgumentException.html)抛出，因为64位`long`不能缩小到32位存储`int`而没有信息丢失。无论传递的实际值是否可以在目标数据类型中准确表示，都是如此。[*Java语言规范，Java SE 7版*](https://docs.oracle.com/javase/specs/jls/se7/html/index.html)，部分[扩展原始转换](https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2)和[缩小原始转换](https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3) 包含有关扩大和缩小转化的完整讨论。

使用[`Array.set(Object array, int index, int value)`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html#set-java.lang.Object-int-int-)和 设置和检索引用类型数组（包括数组数组）的组件 [`Array.get(Object array, int index)`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html#get-java.lang.Object-int-)。

## 设置类型数组的字段

该 [`GrowBufferedReader`](example/GrowBufferedReader.java)示例说明了如何替换数组类型字段的值。在这种情况下，代码替换[`java.io.BufferedReader`](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html)较大的后备阵列 。（这假设原始的创建`BufferedReader`是在不可修改的代码中;否则，简单地使用[`BufferedReader(java.io.Reader in, int size)`](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html#BufferedReader-java.io.Reader-int-)接受输入缓冲区大小的备用构造函数将是微不足道的 。）

```java
import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import static java.lang.System.out;

public class GrowBufferedReader {
    private static final int srcBufSize = 10 * 1024;
    private static char[] src = new char[srcBufSize];
    static {
	src[srcBufSize - 1] = 'x';
    }
    private static CharArrayReader car = new CharArrayReader(src);

    public static void main(String... args) {
	try {
	    BufferedReader br = new BufferedReader(car);

	    Class<?> c = br.getClass();
	    Field f = c.getDeclaredField("cb");

	    // cb is a private field
	    f.setAccessible(true);
	    char[] cbVal = char[].class.cast(f.get(br));

	    char[] newVal = Arrays.copyOf(cbVal, cbVal.length * 2);
	    if (args.length > 0 && args[0].equals("grow"))
		f.set(br, newVal);

	    for (int i = 0; i < srcBufSize; i++)
		br.read();

	    // see if the new backing array is being used
	    if (newVal[srcBufSize - 1] == src[srcBufSize - 1])
		out.format("Using new backing array, size=%d%n", newVal.length);
	    else
		out.format("Using original backing array, size=%d%n", cbVal.length);

        // production code should handle these exceptions more gracefully
	} catch (FileNotFoundException x) {
	    x.printStackTrace();
	} catch (NoSuchFieldException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	} catch (IOException x) {
	    x.printStackTrace();
	}
    }
}
$ java GrowBufferedReader grow
Using new backing array, size=16384
$ java GrowBufferedReader
Using original backing array, size=8192
```

请注意，上面的示例使用了数组实用程序方法 [`java.util.Arrays.copyOf)`](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html#copyOf-char:A-int-)。 [`java.util.Arrays`](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html)包含许多在数组上操作时很方便的方法。

## 访问多维数组的元素

多维数组只是嵌套数组。二维数组是一个数组数组。三维数组是二维数组的数组，依此类推。该 [`CreateMatrix`](example/CreateMatrix.java)示例说明了如何使用反射创建和初始化多维数组。

```java
import java.lang.reflect.Array;
import static java.lang.System.out;

public class CreateMatrix {
    public static void main(String... args) {
        Object matrix = Array.newInstance(int.class, 2, 2);
        Object row0 = Array.get(matrix, 0);
        Object row1 = Array.get(matrix, 1);

        Array.setInt(row0, 0, 1);
        Array.setInt(row0, 1, 2);
        Array.setInt(row1, 0, 3);
        Array.setInt(row1, 1, 4);

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                out.format("matrix[%d][%d] = %d%n", i, j, ((int[][])matrix)[i][j]);
    }
}
$ java CreateMatrix
matrix[0][0] = 1
matrix[0][1] = 2
matrix[1][0] = 3
matrix[1][1] = 4
```

使用以下代码片段可以获得相同的结果：

```java
Object matrix = Array.newInstance(int.class, 2);
Object row0 = Array.newInstance(int.class, 2);
Object row1 = Array.newInstance(int.class, 2);

Array.setInt(row0, 0, 1);
Array.setInt(row0, 1, 2);
Array.setInt(row1, 0, 3);
Array.setInt(row1, 1, 4);

Array.set(matrix, 0, row0);
Array.set(matrix, 1, row1);
```

变量参数 [`Array.newInstance(Class componentType, int... dimensions)`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html#newInstance-java.lang.Class-int...-)提供了一种创建多维数组的便捷方法，但仍需要使用多维数组是嵌套数组的原则来初始化组件。（Reflection不为此目的提供多个索引`get`/ `set`方法。）