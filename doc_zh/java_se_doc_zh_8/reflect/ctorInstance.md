# 创建新类实例

创建类的实例有两种反射方法： [`java.lang.reflect.Constructor.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#newInstance-java.lang.Object...-)和 [`Class.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#newInstance--)。前者是首选，因此在这些示例中使用，因为：

- [`Class.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#newInstance--)[`Constructor.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#newInstance-java.lang.Object...-)无论参数的数量多少，都可以调用零参数构造函数，同时 可以调用任何构造函数。
- [`Class.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#newInstance--)抛出构造函数抛出的任何异常，无论是否已选中或未选中。 [`Constructor.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#newInstance-java.lang.Object...-)总是用一个包装抛出的异常 [`InvocationTargetException`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/InvocationTargetException.html)。
- [`Class.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#newInstance--)要求构造函数可见; 在某些情况下[`Constructor.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#newInstance-java.lang.Object...-)可能会调用`private`构造函数。

有时可能需要从仅在构造之后设置的对象检索内部状态。考虑需要获取所使用的内部字符集的场景 [`java.io.Console`](https://docs.oracle.com/javase/8/docs/api/java/io/Console.html)。（`Console`字符集存储在私有字段中，不一定与返回的Java虚拟机缺省字符集相同[`java.nio.charset.Charset.defaultCharset()`](https://docs.oracle.com/javase/8/docs/api/java/nio/charset/Charset.html#defaultCharset--)）。该 [`ConsoleCharset`](example/ConsoleCharset.java)示例显示了如何实现这一目标：

```java
import java.io.Console;
import java.nio.charset.Charset;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import static java.lang.System.out;

public class ConsoleCharset {
    public static void main(String... args) {
	Constructor[] ctors = Console.class.getDeclaredConstructors();
	Constructor ctor = null;
	for (int i = 0; i < ctors.length; i++) {
	    ctor = ctors[i];
	    if (ctor.getGenericParameterTypes().length == 0)
		break;
	}

	try {
	    ctor.setAccessible(true);
 	    Console c = (Console)ctor.newInstance();
	    Field f = c.getClass().getDeclaredField("cs");
	    f.setAccessible(true);
	    out.format("Console charset         :  %s%n", f.get(c));
	    out.format("Charset.defaultCharset():  %s%n",
		       Charset.defaultCharset());

        // production code should handle these exceptions more gracefully
	} catch (InstantiationException x) {
	    x.printStackTrace();
 	} catch (InvocationTargetException x) {
 	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	} catch (NoSuchFieldException x) {
	    x.printStackTrace();
	}
    }
}
```

------

**注意：**

[`Class.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#newInstance--)只有在构造函数的参数为零且已经可访问时才会成功。否则，必须 [`Constructor.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#newInstance-java.lang.Object...-)如上例所示使用。

------

UNIX系统的示例输出：

```bash
$ java ConsoleCharset
Console charset          :  ISO-8859-1
Charset.defaultCharset() :  ISO-8859-1
```

Windows系统的示例输出：

```bash
C:\> java ConsoleCharset
Console charset          :  IBM437
Charset.defaultCharset() :  windows-1252
```

另一个常见的应用 [`Constructor.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#newInstance-java.lang.Object...-)是调用带参数的构造函数。该 [`RestoreAliases`](example/RestoreAliases.java)示例查找特定的单参数构造函数并调用它：

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static java.lang.System.out;

class EmailAliases {
    private Set<String> aliases;
    private EmailAliases(HashMap<String, String> h) {
	aliases = h.keySet();
    }

    public void printKeys() {
	out.format("Mail keys:%n");
	for (String k : aliases)
	    out.format("  %s%n", k);
    }
}

public class RestoreAliases {

    private static Map<String, String> defaultAliases = new HashMap<String, String>();
    static {
	defaultAliases.put("Duke", "duke@i-love-java");
	defaultAliases.put("Fang", "fang@evil-jealous-twin");
    }

    public static void main(String... args) {
	try {
	    Constructor ctor = EmailAliases.class.getDeclaredConstructor(HashMap.class);
	    ctor.setAccessible(true);
	    EmailAliases email = (EmailAliases)ctor.newInstance(defaultAliases);
	    email.printKeys();

        // production code should handle these exceptions more gracefully
	} catch (InstantiationException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	} catch (InvocationTargetException x) {
	    x.printStackTrace();
	} catch (NoSuchMethodException x) {
	    x.printStackTrace();
	}
    }
}
```

此示例用于 [`Class.getDeclaredConstructor()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getDeclaredConstructor-java.lang.Class...-)查找具有单个参数类型的构造函数 [`java.util.HashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html)。请注意，传递就足够了，`HashMap.class`因为参数到任何`get*Constructor()`方法只需要类用于类型目的。由于 [类型擦除](https://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.6)，以下表达式评估为`true`：

```java
HashMap.class == defaultAliases.getClass()
```

然后，该示例使用此构造函数创建类的新实例 [`Constructor.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#newInstance-java.lang.Object...-)。

```bash
$ java RestoreAliases
Mail keys:
  Duke
  Fang
```

