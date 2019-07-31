# 类

每种类型都是引用或基元。类，枚举和数组（都继承自 [`java.lang.Object`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)）以及接口都是引用类型。引用类型的示例包括 [`java.lang.String`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)基本类型的所有包装类，例如 [`java.lang.Double`](https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html)接口 [`java.io.Serializable`](https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html)和枚举[`javax.swing.SortOrder`](https://docs.oracle.com/javase/8/docs/api/javax/swing/SortOrder.html)。有一组固定的原始类型：`boolean`，`byte`，`short`，`int`，`long`，`char`，`float`，和`double`。

对于每种类型的对象，Java虚拟机都实例化一个不可变实例，[`java.lang.Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)该实例 提供了检查对象的运行时属性的方法，包括其成员和类型信息。 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)还提供了创建新类和对象的功能。最重要的是，它是所有Reflection API的入口点。本课程介绍了最常用的涉及类的反射操作：

- [检索类对象](classNew.md)描述了获取a的方法 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)
- [检查类修饰符和类型](classModifiers.md)显示了如何访问类声明信息
- [发现类成员](classMembers.md)说明了如何列出[类](classMembers.html)中的构造函数，字段，方法和嵌套类
- [故障排除](classTrouble.md)描述了使用时遇到的常见错 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)