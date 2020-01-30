# FileVisitor 接口

如前所述，FileVisitor接口提供了对递归遍历文件树的支持。 

该接口的方法代表遍历过程中的关键点，使您能够控制何时访问文件，访问目录之前，访问目录之后以及何时发生故障；

换句话说，该接口还具有在访问文件之前，期间和之后的挂钩。至于何时发生故障。 

一旦掌握了控制权（在任何这些关键点上），您都可以选择如何处理访问的文件并通过指示访问结果来决定下一步该如何处理
FileVisitResult 枚举，其中包含四个枚举常量：

-   FileVisitResult.CONTINUE

此访问结果表明遍历过程应该继续。 可以将其转换为不同的动作，具体取决于哪个返回FileVisitor方法。 

例如，遍历过程可能会继续通过访问下一个文件，访问目录的条目或跳过故障。

-   FileVisitResult.SKIP_SIBLINGS

此访问结果表明遍历该过程应继续进行，而无需访问此文件或目录的兄弟姐妹。

-   FileVisitResult.SKIP_SUBTREE

此访问结果表明遍历该过程应继续进行，而无需访问此目录中的其余条目

-   FileVisitResult.TERMINATE

此访问结果表明遍历过程应该终止。


可以按以下方式迭代此枚举类型的常量：

```Java
for (FileVisitResult constant : FileVisitResult.values())
    System.out.println(constant);
```

以下小节讨论了如何通过实现以下内容来控制遍历过程各种FileVisitor方法。

##  FileVisitor.visitFile() 方法

对目录中的文件调用visitFile（）方法。 

通常，此方法返回 CONTINUE 结果或 TERMINATE 结果。 

例如，当搜索文件时，此方法应返回CONTINUE直到找到文件（或完全遍历树），并在找到文件后终止。

调用此方法后，它将收到对该文件及其基本属性的引用。 

如果发生 I/O 错误，然后引发IOException异常。 以下是此方法的签名：

```Java
FileVisitResult visitFile(T file, BasicFileAttributes attrs) throws IOException
```

##  FileVisitor.preVisitDirectory() 方法

在访问目录条目之前，将为目录调用preVisitDirectory（）方法。 

如果是条目该方法将返回 CONTINUE，则访问；

如果返回SKIP_SUBTREE，则该方法将不被访问（仅当从该方法返回时，后一个访问结果才有意义）。 

另外，您可以跳过访问的兄弟姐妹

通过返回SKIP_SIBLINGS结果，此文件或目录（以及所有后代）。调用此方法时，它将获得对目录和目录的基本属性的引用。

如果发生 I/O 错误，则会引发IOException异常。 该方法的签名是

```Java
FileVisitResult preVisitDirectory(T dir, BasicFileAttributes attrs) throws IOException
```

##  FileVisitor.postVisitDirectory() 方法

在目录中的所有条目（以及所有后代）之后调用postVisitDirectory（）方法。

已被访问或访问突然结束（即，发生了I / O错误或访问已以编程方式中止）。 

调用此方法时，它将获得对目录的引用，并且IOException对象-如果在访问期间未发生错误，则为null，否则它将返回对应的如果发生一个错误。 

如果发生I / O错误，则会引发IOException异常。 以下是此方法的签名

```Java
FileVisitResult postVisitDirectory(T dir, IOException exc) throws IOException
```

##  FileVisitor.visitFileFailed() 方法

当无法通过几种不同方式访问文件时，调用visitFileFailed（）方法

例如无法读取文件的属性或无法打开目录。 

当这种方法调用后，它将获得对该文件的引用以及尝试访问该文件时发生的异常。 

如果发生I / O错误，然后引发IOException异常。 以下是此方法的签名：

```Java
FileVisitResult visitFileFailed(T file, IOException exc) throws IOException
```

----
