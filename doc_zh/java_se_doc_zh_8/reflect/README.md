# 反射

反射通常由程序使用，这些程序需要能够检查或修改在Java虚拟机中运行的应用程序的运行时行为。这是一个相对高级的功能，只有那些掌握了语言基础知识的开发人员才能使用。考虑到这一点，反射是一种强大的技术，可以使应用程序执行本来不可能的操作。

- 可扩展性功能

  应用程序可以通过使用完全限定名称创建可扩展性对象的实例来使用外部的用户定义类。

- 类浏览器和可视化开发环境

  类浏览器需要能够枚举类的成员。可视化开发环境可以从利用反射中可用的类型信息中受益，以帮助开发人员编写正确的代码。

- 调试器和测试工具

  调试器需要能够检查类上的私有成员。测试工具可以利用反射系统地调用类上定义的可发现的set API，以确保测试套件中的高级代码覆盖率。

## 反射的缺点

反射是强大的，但不应随意使用。如果可以在不使用反射的情况下执行操作，则优选避免使用它。通过反射访问代码时，应牢记以下问题。

- 性能开销

  由于反射涉及动态解析的类型，因此无法执行某些Java虚拟机优化。因此，反射操作的性能低于非反射操作，并且应避免在性能敏感应用程序中频繁调用的代码段中。

- 安全限制

  Reflection需要运行时权限，在安全管理器下运行时可能不存在。对于必须在受限安全上下文中运行的代码，例如在Applet中，这是一个重要的考虑因素。

- 内部接触

  由于反射允许代码执行在非反射代码中非法的操作，例如访问`private`字段和方法，因此使用反射会导致意外的副作用，这可能导致代码功能失常并可能破坏可移植性。反射代码打破了抽象，因此可能会通过升级平台来改变行为。

## 小道教训

此跟踪包含用于访问和操作类，字段，方法和构造函数的反射的常见用法。每课包含代码示例，提示和疑难解答信息。

- [**类**](class.md)

  本课程介绍了获取[`Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)对象的各种方法， 并使用它来检查类的属性，包括其声明和内容。

- [**属性**](member.md)

  本课程描述如何使用Reflection API查找类的字段，方法和构造函数。提供了用于设置和获取字段值，调用方法以及使用特定构造函数创建对象的新实例的示例。

- [**数组和枚举类型**](special.md)

  本课介绍两种特殊类型的类：在运行时生成的数组和`enum`定义唯一命名对象实例的类型。示例代码显示了如何检索数组的组件类型以及如何设置和获取具有数组或`enum`类型的字段。