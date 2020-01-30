# SimpleFileVisitor类

实现FileVisitor接口这可能是需要实现其所有方法，如果只需要实现这些方法中的一种或几种，则是不可取的。 

那样的话扩展SimpleFileVisitor类更加简单，该类实现了FileVisitor接口。 

这个方法只需要覆盖所需的方法。

例如，您可能想遍历文件树并列出所有目录的名称。 

去完成这样，仅使用postVisitDirectory（）和visitFileFailed（）方法就足够了，如以下代码片段（下一部分介绍了起始文件树）：

```Java
class ListTree extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        System.out.println("Visited directory: " + dir.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.out.println(exc);
    return FileVisitResult.CONTINUE;
    }
}

```

如您所见，preVisitDirectory（）和visitFile（）方法被跳过。

----