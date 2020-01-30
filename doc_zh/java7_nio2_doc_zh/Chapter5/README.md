# 递归操作：Walks

您可能知道，递归编程是一种有争议的技术，因为它通常需要很多内存，但它简化了一些编程任务。 

基本上，递归编程过程是调用自身，传入一个或多个参数的修改值传递给该过程的当前迭代。 

编程任务，例如计算阶乘，斐波那契数词，字谜和Sierpinski地毯只是可以完成的一些著名任务通过递归编程技术来完成。 

以下代码段使用此代码计算阶乘（n！= 1 * 2 * 3 *…* n）的技术-请注意该过程如何调用自身：

```Java
/**
* Calculate the factorial of n (n! = 1 * 2 * 3 * … * n).
*
* @param n the number to calculate the factorial of.
* @return n! - the factorial of n.
*/
static int fact(int n) {
    // Base Case:
    // If n <= 1 then n! = 1.
    if (n <= 1) {
        return 1;
    }
    // Recursive Case:
    // If n > 1 then n! = n * (n-1)!
    else {
        return n * fact(n-1);
    }
}
```

如果您已经熟悉此编程技术，请继续阅读本章以了解NIO.2如何利用它。

否则，在继续阅读之前，这是一个好主意，专门针对递归编程的教程，例如Jonathan的“精通递归编程”Bartlett，可从www.ibm.com/developerworks/linux/library/l-recurs/index.html获得。

许多涉及处理文件的编程任务都需要访问文件树中的所有文件，是使用递归编程机制的好机会，因为每个文件都应分别“触摸”。 

在执行删除，复制或移动文件树。 

基于这种机制，NIO.2封装了一个对象的遍历过程。

接口java.nio.file包中名为FileVisitor的文件树。

本章首先介绍FileVisitor的范围和方法。 

一旦熟悉 FileVisitor，本章将帮助您开发一组可用于执行任务的应用程序涉及遍历文件树，例如查找，复制，删除和移动文件。

----