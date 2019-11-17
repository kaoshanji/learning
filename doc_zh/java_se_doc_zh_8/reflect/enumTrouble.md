# 故障排除

以下示例显示使用枚举类型时可能遇到的问题。

## 尝试实例化枚举类型时出现IllegalArgumentException

如前所述，禁止实例化枚举类型。这个 [`EnumTrouble`](example/EnumTrouble.java)例子试图这样做。

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static java.lang.System.out;

enum Charge {
    POSITIVE, NEGATIVE, NEUTRAL;
    Charge() {
	out.format("under construction%n");
    }
}

public class EnumTrouble {

    public static void main(String... args) {
	try {
	    Class<?> c = Charge.class;

 	    Constructor[] ctors = c.getDeclaredConstructors();
 	    for (Constructor ctor : ctors) {
		out.format("Constructor: %s%n",  ctor.toGenericString());
 		ctor.setAccessible(true);
 		ctor.newInstance();
 	    }

        // production code should handle these exceptions more gracefully
	} catch (InstantiationException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	} catch (InvocationTargetException x) {
	    x.printStackTrace();
	}
    }
}
$ java EnumTrouble
Constructor: private Charge()
Exception in thread "main" java.lang.IllegalArgumentException: Cannot
  reflectively create enum objects
        at java.lang.reflect.Constructor.newInstance(Constructor.java:511)
        at EnumTrouble.main(EnumTrouble.java:22)
```

------

**提示：**  尝试显式实例化枚举是一个编译时错误，因为这会阻止定义的枚举常量是唯一的。这种限制也在反射代码中强制执行。尝试使用其默认构造函数实例化类的代码应首先调用 以确定该类是否为枚举。

## 设置具有不兼容枚举类型的字段时出现IllegalArgumentException

存储枚举的字段使用适当的枚举类型设置。（实际上，必须使用兼容类型设置*任何*类型的字段。）该 [`EnumTroubleToo`](example/EnumTroubleToo.java)示例产生预期的错误。

```java
import java.lang.reflect.Field;

enum E0 { A, B }
enum E1 { A, B }

class ETest {
    private E0 fld = E0.A;
}

public class EnumTroubleToo {
    public static void main(String... args) {
	try {
	    ETest test = new ETest();
	    Field f = test.getClass().getDeclaredField("fld");
	    f.setAccessible(true);
 	    f.set(test, E1.A);  // IllegalArgumentException

        // production code should handle these exceptions more gracefully
	} catch (NoSuchFieldException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	}
    }
}
$ java EnumTroubleToo
Exception in thread "main" java.lang.IllegalArgumentException: Can not set E0
  field ETest.fld to E1
        at sun.reflect.UnsafeFieldAccessorImpl.throwSetIllegalArgumentException
          (UnsafeFieldAccessorImpl.java:146)
        at sun.reflect.UnsafeFieldAccessorImpl.throwSetIllegalArgumentException
          (UnsafeFieldAccessorImpl.java:150)
        at sun.reflect.UnsafeObjectFieldAccessorImpl.set
          (UnsafeObjectFieldAccessorImpl.java:63)
        at java.lang.reflect.Field.set(Field.java:657)
        at EnumTroubleToo.main(EnumTroubleToo.java:16)
```

**提示：**  严格地说，任何将类型字段设置为类型`X`值的尝试`Y`只有在以下语句成立时才能成功：

```java
X.class.isAssignableFrom(Y.class) == true
```

可以修改代码以执行以下测试以验证类型是否兼容：

```java
if (f.getType().isAssignableFrom(E0.class))
    // compatible
else
    // expect IllegalArgumentException
```

