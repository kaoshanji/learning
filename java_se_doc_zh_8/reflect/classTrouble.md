# 故障排除

以下示例显示了在反映类时可能遇到的典型错误。

## 编译器警告：“注意：...使用未经检查或不安全的操作”

调用方法时，将检查参数值的类型并进行转换。 [`ClassWarning`](example/ClassWarning.java)调用 [`getMethod()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getMethod-java.lang.String-java.lang.Class...-)以导致典型的未经检查的转换警告：

```java
import java.lang.reflect.Method;

public class ClassWarning {
    void m() {
	try {
	    Class c = ClassWarning.class;
	    Method m = c.getMethod("m");  // warning

        // production code should handle this exception more gracefully
	} catch (NoSuchMethodException x) {
    	    x.printStackTrace();
    	}
    }
}
$ javac ClassWarning.java
Note: ClassWarning.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
$ javac -Xlint:unchecked ClassWarning.java
ClassWarning.java:6: warning: [unchecked] unchecked call to getMethod
  (String,Class<?>...) as a member of the raw type Class
Method m = c.getMethod("m");  // warning
                      ^
1 warning
```

许多图书馆方法都使用通用声明进行了改进，其中包括几个 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)。由于`c`声明为*原始*类型（没有类型参数）且相应的参数 [`getMethod()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getMethod-java.lang.String-java.lang.Class...-)是参数化类型，因此会发生未经检查的转换。编译器需要生成警告。（请参阅[*Java语言规范，Java SE 7 Edition*](https://docs.oracle.com/javase/specs/jls/se7/html/index.html)，[未经检查的转换](https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.9)和[方法调用转换部分](https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.3)。）

有两种可能的解决方案。修改声明`c`以包含适当的泛型类型更为可取。在这种情况下，声明应该是：

```java
Class<?> c = warn.getClass();
```

或者，可以使用[`@SuppressWarnings`](https://docs.oracle.com/javase/8/docs/api/java/lang/SuppressWarnings.html)有问题的语句之前的预定义注释明确地抑制警告 。

```java
Class c = ClassWarning.class;
@SuppressWarnings("unchecked")
Method m = c.getMethod("m");  
// warning gone
```

**提示：**  作为一般原则，警告不应该被忽略，因为它们可能表明存在错误。应适当使用参数化声明。如果这不可能（可能是因为应用程序必须与库供应商的代码交互），请使用注释违规行 [`@SuppressWarnings`](https://docs.oracle.com/javase/8/docs/api/java/lang/SuppressWarnings.html)。

## 构造函数不可访问时的InstantiationException

[`Class.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#newInstance--)[`InstantiationException`](https://docs.oracle.com/javase/8/docs/api/java/lang/InstantiationException.html)如果尝试创建类的新实例并且零参数构造函数不可见，则将抛出一个 。该 [`ClassTrouble`](example/ClassTrouble.java)示例说明了生成的堆栈跟踪。

```java
class Cls {
    private Cls() {}
}

public class ClassTrouble {
    public static void main(String... args) {
	try {
	    Class<?> c = Class.forName("Cls");
	    c.newInstance();  // InstantiationException

        // production code should handle these exceptions more gracefully
	} catch (InstantiationException x) {
	    x.printStackTrace();
	} catch (IllegalAccessException x) {
	    x.printStackTrace();
	} catch (ClassNotFoundException x) {
	    x.printStackTrace();
	}
    }
}
$ java ClassTrouble
java.lang.IllegalAccessException: Class ClassTrouble can not access a member of
  class Cls with modifiers "private"
        at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:65)
        at java.lang.Class.newInstance0(Class.java:349)
        at java.lang.Class.newInstance(Class.java:308)
        at ClassTrouble.main(ClassTrouble.java:9)
```

[`Class.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#newInstance--)表现与`new`关键字非常相似，但由于同样的原因`new`会失败。反思的典型解决方案是利用[`java.lang.reflect.AccessibleObject`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AccessibleObject.html)提供抑制访问控制检查能力的 类; 但是，这种方法不起作用，因为[`java.lang.Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)没有扩展 [`AccessibleObject`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AccessibleObject.html)。唯一的解决方案是修改要使用的[`Constructor.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#newInstance-java.lang.Object...-)扩展 代码 [`AccessibleObject`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AccessibleObject.html)。

------

**提示：**  通常，最好使用 “ [成员”](../member/index.html)课程中“ 部分中描述的原因。

[`Constructor.newInstance()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Constructor.html#newInstance-java.lang.Object...-)可以在“ [成员”](../member/index.html)课程的“ [构造函数疑难解答”](../member/ctorTrouble.html)部分中找到使用潜在问题的其他示例 。