# 使用枚举类型获取和设置字段

存储枚举的字段使用[`Field.set()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html#set-java.lang.Object-java.lang.Object-)和 设置和检索为任何其他引用类型 [`Field.get()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html#get-java.lang.Object-)。有关访问字段的详细信息，请参阅此跟踪的“ [字段”](../member/field.html)部分。

考虑需要动态修改服务器应用程序中跟踪级别的应用程序，该应用程序通常在运行时不允许此更改。假设服务器对象的实例可用。该 [`SetTrace`](example/SetTrace.java)示例显示了代码如何[`String`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)将枚举的 表示转换为枚举类型，并检索和设置存储枚举的字段的值。

```java
import java.lang.reflect.Field;
import static java.lang.System.out;

enum TraceLevel { OFF, LOW, MEDIUM, HIGH, DEBUG }

class MyServer {
    private TraceLevel level = TraceLevel.OFF;
}

public class SetTrace {
    public static void main(String... args) {
	TraceLevel newLevel = TraceLevel.valueOf(args[0]);

	try {
	    MyServer svr = new MyServer();
	    Class<?> c = svr.getClass();
	    Field f = c.getDeclaredField("level");
	    f.setAccessible(true);
	    TraceLevel oldLevel = (TraceLevel)f.get(svr);
	    out.format("Original trace level:  %s%n", oldLevel);

	    if (oldLevel != newLevel) {
 		f.set(svr, newLevel);
		out.format("    New  trace level:  %s%n", f.get(svr));
	    }

        // production code should handle these exceptions more gracefully
	} catch (IllegalArgumentException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	} catch (NoSuchFieldException x) {
	    x.printStackTrace();
	}
    }
}
```

由于枚举常量是单例，因此可以使用`==`和`!=`运算符来比较相同类型的枚举常量。

```bash
$ java SetTrace OFF
Original trace level:  OFF
$ java SetTrace DEBUG
Original trace level:  OFF
    New  trace level:  DEBUG
```

