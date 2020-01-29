# 两个路径构建Path

当您需要构造从一个位置到另一位置的路径时，可以调用relativize（）方法，它在此路径和给定路径之间构造相对路径。 

该方法构造路径从原始路径开始，到传入路径指定的位置结束。 新的path是相对于原始路径的。 

为了更好地了解这种强大的功能，请考虑示例。 假设您具有以下两个相对路径：

```Java
Path path01 = Paths.get("BNP.txt");
Path path02 = Paths.get("AEGON.txt");
```

在这种情况下，假定BNP.txt和AEGON.txt是兄弟姐妹，这意味着您可以通过上一级然后下一级从一个导航到另一个。 

应用relativize（）方法输出 ..\AEGON.txt 和 ..\BNP.txt：

```Java
//output: ..\AEGON.txt
Path path01_to_path02 = path01.relativize(path02);
System.out.println(path01_to_path02);
//output: ..\BNP.txt
Path path02_to_path01 = path02.relativize(path01);
System.out.println(path02_to_path01);
```

另一种典型情况是包含根元素的两条路径。 考虑以下路径：

```Java
Path path01 = Paths.get("/tournaments/2009/BNP.txt");
Path path02 = Paths.get("/tournaments/2011");
```

在这种情况下，两个路径都包含相同的根元素/ tournaments。

从path01导航至path02，您将上升两层，而下降一层（.. \ .. \ 2011）。

要从path02导航到path01，您将上一层，下两层（.. \ 2009 \ BNP.txt）。 

这正是relativize（）的方式方法有效：

```Java
//output: ..\..\2011
Path path01_to_path02 = path01.relativize(path02);
System.out.println(path01_to_path02);
//output: ..\2009\BNP.txt
Path path02_to_path01 = path02.relativize(path01);
System.out.println(path02_to_path01);
```

----