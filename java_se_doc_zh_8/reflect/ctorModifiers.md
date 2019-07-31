# 检索和解析构造函数修饰符

由于构造函数在语言中的作用，修饰符比方法更有意义：

- 访问修饰符：`public`，`protected`，和`private`
- 注释

该 [`ConstructorAccess`](example/ConstructorAccess.java)示例使用指定的访问修饰符搜索给定类中的构造函数。它还显示构造函数是合成的（编译器生成的）还是可变的arity。

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import static java.lang.System.out;

public class ConstructorAccess {
    public static void main(String... args) {
	try {
	    Class<?> c = Class.forName(args[0]);
	    Constructor[] allConstructors = c.getDeclaredConstructors();
	    for (Constructor ctor : allConstructors) {
		int searchMod = modifierFromString(args[1]);
		int mods = accessModifiers(ctor.getModifiers());
		if (searchMod == mods) {
		    out.format("%s%n", ctor.toGenericString());
		    out.format("  [ synthetic=%-5b var_args=%-5b ]%n",
			       ctor.isSynthetic(), ctor.isVarArgs());
		}
	    }

        // production code should handle this exception more gracefully
	} catch (ClassNotFoundException x) {
	    x.printStackTrace();
	}
    }

    private static int accessModifiers(int m) {
	return m & (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED);
    }

    private static int modifierFromString(String s) {
	if ("public".equals(s))               return Modifier.PUBLIC;
	else if ("protected".equals(s))       return Modifier.PROTECTED;
	else if ("private".equals(s))         return Modifier.PRIVATE;
	else if ("package-private".equals(s)) return 0;
	else return -1;
    }
}
```

没有明确的 [`Modifier`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Modifier.html)常量对应于“包 - 私有”访问，因此有必要检查是否缺少所有三个访问修饰符以标识包私有构造函数。

此输出显示私有构造函数 [`java.io.File`](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)：

```java
$ java ConstructorAccess java.io.File private
private java.io.File(java.lang.String,int)
  [ synthetic=false var_args=false ]
private java.io.File(java.lang.String,java.io.File)
  [ synthetic=false var_args=false ]
```

合成构造函数很少见; 但是，该 [`SyntheticConstructor`](example/SyntheticConstructor.java)示例说明了可能发生这种情况的典型情况：

```java
public class SyntheticConstructor {
    private SyntheticConstructor() {}
    class Inner {
	// Compiler will generate a synthetic constructor since
	// SyntheticConstructor() is private.
	Inner() { new SyntheticConstructor(); }
    }
}
$ java ConstructorAccess SyntheticConstructor package-private
SyntheticConstructor(SyntheticConstructor$1)
  [ synthetic=true  var_args=false ]
```

由于内部类的构造函数引用了封闭类的私有构造函数，因此编译器必须生成包私有构造函数。参数类型`SyntheticConstructor$1`是任意的，取决于编译器实现。取决于是否存在任何合成或非公共类成员的代码可能无法移植。

构造函数实现 [`java.lang.reflect.AnnotatedElement`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AnnotatedElement.html)，它提供了检索运行时注释的方法 [`java.lang.annotation.RetentionPolicy.RUNTIME`](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/RetentionPolicy.html#RUNTIME)。有关获取注释的示例，请参阅“ [检查类修饰符和类型”](../class/classModifiers.html)部分。