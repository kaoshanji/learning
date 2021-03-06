#   Java核心技术 卷1 基础知识 原书第10版

>   第8章 泛型程序设计

致使Java SE 5.0 中增加泛型机制的主要原因是为了满足在 1999 年制定的最早的Java规范需求之一(JSR 14)，专家组花费了5年左右的时间用来定义规范和测试实现。

使用泛型机制编写的程序代码要比那些杂乱无章的使用Object变量，然后再进行强制类型转换的代码具有更好的安全性和可读性。

泛型对于集合类尤其有用，例如，ArrayList 就是一个无处不在的集合类。

##  目录
-   [为什么要使用泛型程序设计](10x.md)
    -   类型参数的好处
    -   谁想成为泛型程序员
-   [定义简单泛型类](11x.md)
-   [泛型方法](12x.md)
-   [类型变量的限定](13x.md)
-   [泛型代码和虚拟机](14x.md)
    -   类型擦除
    -   翻译泛型表达式
    -   翻译泛型方法
    -   调用遗留代码
-   [约束与局限性](15x.md)
    -   不能用基本类型实例化类型参数
    -   运行时类型查询只适用于原始类型
    -   不能创建参数化类型的数组
    -   Varargs 警告
    -   不能实例化类型变量
    -   不能构造泛型数组
    -   泛型类的静态上下文中类型变量无效
    -   不能抛出或捕获泛型类的实例
    -   可以消除对受查异常的检查
    -   注意擦除后的冲突
-   [泛型类型的继承规则](16x.md)
-   [通配符类型](17x.md)
    -   通配符概念
    -   通配符的超类型限定
    -   无限定通配符
    -   通配符捕获
-   [反射和泛型](18x.md)
    -   泛型Class类
    -   使用Class参数进行类型匹配
    -   虚拟机中的泛型类型信息

----