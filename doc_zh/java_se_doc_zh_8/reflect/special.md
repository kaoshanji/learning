# 数组和枚举类型

从Java虚拟机的角度来看，数组和枚举类型（或枚举）只是类。[`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)可以在它们上使用许多方法 。Reflection为数组和枚举提供了一些特定的API。本课程使用一系列代码示例来描述如何将这些对象与其他类区分开来并对其进行操作。还要检查各种错误。

## 数组

数组具有组件类型和长度（不属于该类型）。阵列可以全部或逐个组件进行人工操作。反思为[`java.lang.reflect.Array`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Array.html)后一目的提供了 课程。

- [标识数组类型](arrayComponents.md)描述了如何确定类成员是否是数组类型的字段
- [创建新数组](arrayInstance.md)说明了如何使用简单和复杂的组件类型创建新的数组实例
- [获取和设置数组及其组件](arraySetGet.md)显示了如何访问类型数组的字段并单独访问数组元素
- [故障排除](arrayTrouble.md)包括常见错误和编程误解

## 枚举类型

枚举的处理方式与反射代码中的普通类非常相似。 [`Class.isEnum()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#isEnum--)告诉我是否 [`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)代表和`enum`。 [`Class.getEnumConstants()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getEnumConstants--)检索枚举中定义的枚举常量。 [`java.lang.reflect.Field.isEnumConstant()`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html#isEnumConstant--)指示字段是否为枚举类型。

- [检查枚举](enumMembers.md)说明了如何检索枚举的常量以及任何其他字段，构造函数和方法
- [使用枚举类型获取和设置字段](enumSetGet.md)显示如何使用枚举常量值设置和获取字段
- [故障排除](enumTrouble.md)描述了与枚举相关的常见错误