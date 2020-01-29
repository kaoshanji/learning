# Paths组合

组合两个路径是一种技术，它允许您定义固定的根路径并将部分路径附加到该路径路径。 

这对于基于公共零件定义路径非常有用。 NIO.2提供了此操作通过resolve（）方法。 以下是其工作方式的示例：

```Java
//define the fixed path
Path base = Paths.get("C:/rafaelnadal/tournaments/2009");
//resolve BNP.txt file
Path path_1 = base.resolve("BNP.txt");
//output: C:\rafaelnadal\tournaments\2009\BNP.txt
System.out.println(path_1.toString());
//resolve AEGON.txt file
Path path_2 = base.resolve("AEGON.txt");
//output: C:\rafaelnadal\tournaments\2009\AEGON.txt
System.out.println(path_2.toString());
```

还有一种专用于同级路径的方法称为resolveSibling（）。 

解析通过相对于当前路径的父路径的路径。 实际上，此方法将替换当前文件的文件名。路径与给定路径的文件名。

下面的示例阐明了这个想法：

```Java
//define the fixed path
Path base = Paths.get("C:/rafaelnadal/tournaments/2009/BNP.txt");
//resolve sibling AEGON.txt file
Path path = base.resolveSibling("AEGON.txt");
//output: C:\rafaelnadal\tournaments\2009\AEGON.txt
System.out.println(path.toString());
```

----