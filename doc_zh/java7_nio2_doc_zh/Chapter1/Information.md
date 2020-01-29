# 从Path获取信息

定义Path对象后，您可以访问提供有用信息的一组方法关于路径元素。 

这些方法基于以下事实：NIO.2将路径字符串分成一组元素（元素是代表目录或文件的子路径），并将索引0分配给最高元素，索引n – 1到最低元素，其中n是路径元素的数量； 

通常，最高的元素是根文件夹，最低的元素是文件。 

本节提供的示例将这些信息获取方法应用于路径 C:\rafaelnadal\tournaments\2009\BNP.txt：

```Java
Path path = Paths.get("C:", "rafaelnadal/tournaments/2009", "BNP.txt");
```

##  获取路径文件/目录名称

路径指示的文件/目录由最远的getFileName（）方法返回。目录层次结构根目录中的元素：

```Java
//output: BNP.txt
System.out.println("The file/directory indicated by path: " + path.getFileName());
```

##  Path 根目录

路径的根可以使用getRoot（）方法获得（如果路径没有根，则它返回null）：

```Java
//output: C:\
System.out.println("Root of this path: " + path.getRoot());
```

##  路径父级

此路径的父级（路径的根组件）由getParent（）方法返回（如果路径为没有父母，则返回null）：

```Java
//output: C:\rafaelnadal\tournaments\2009
System.out.println("Parent: " + path.getParent());
```

##  获取路径名元素

您可以使用getNameCount（）方法获取路径中元素的数量，并获取每个元素的名称使用getName（）方法的元素：

```Java
//output: 4
System.out.println("Number of name elements in path: " + path.getNameCount());
//output: rafaelnadal tournaments 2009 BNP.txt
for (int i = 0; i < path.getNameCount(); i++) {
    System.out.println("Name element " + i + " is: " + path.getName(i));
}
```

##  获取路径子路径

您可以使用subpath（）方法提取相对路径，该方法获取两个参数，即起始索引和结束索引，代表元素的子序列：

```Java
//output: rafaelnadal\tournaments\2009
System.out.println("Subpath (0,3): " + path.subpath(0, 3));
```

----