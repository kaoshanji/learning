# 转换为Path

在本节中，您将看到如何将Path对象转换为字符串，URI，绝对路径，实数。

路径和一个File对象。 Path类为每个这些转换包含一个专用方法，如下所示在以下小节中显示。 以下是我们要使用的路径：

```Java
Path path = Paths.get("/rafaelnadal/tournaments/2009", "BNP.txt");
```

##  Path --> String

路径的字符串转换可以通过toString（）方法实现：

```Java
//output: \rafaelnadal\tournaments\2009\BNP.txt
String path_to_string = path.toString();
System.out.println("Path to String: " + path_to_string);
```

##  Path --> URI

您可以通过应用toURI（）方法将Path转换为Web浏览器格式的字符串，如下面的例子。 

结果是一个URI对象，该对象封装了可以输入到Web浏览器的地址栏。

```Java
//output: file:///C:/rafaelnadal/tournaments/2009/BNP.txt
URI path_to_uri = path.toUri();
System.out.println("Path to URI: " + path_to_uri);
```

##  相对 Path --> 绝对 Path

从相对 Path 那里获得一条绝对的道路是非常普遍的任务。 NIO.2可以通过toAbsolutePath（）方法（请注意，如果将此方法应用于已经是绝对路径的路径，则返回相同的路径）：

```Java
//output: C:\rafaelnadal\tournaments\2009\BNP.txt
Path path_to_absolute_path = path.toAbsolutePath();
System.out.println("Path to absolute path: " + path_to_absolute_path.toString());
```

##  Path --> 真实 Path

toRealPath（）方法返回现有文件的真实路径-这意味着该文件必须存在，如果使用toAbsolutePath（）方法，则没有必要。\

如果没有参数传递给此方法并且文件系统支持符号链接，此方法可解析路径中的所有符号链接。 

如果你想要忽略符号链接，然后将LinkOption.NOFOLLOW_LINKS枚举常量传递给该方法。

此外，如果Path是相对的，则返回绝对路径，并且Path包含任何冗余元素，它返回删除了那些元素的路径。

如果文件，此方法将引发IOException不存在或无法访问。

下面的代码片段通过不遵循符号链接来返回文件的真实路径：

```Java
import java.io.IOException;
…
//output: C:\rafaelnadal\tournaments\2009\BNP.txt
try {
Path real_path = path.toRealPath(LinkOption.NOFOLLOW_LINKS);
System.out.println("Path to real path: " + real_path);
} catch (NoSuchFileException e) {
System.err.println(e);
} catch (IOException e) {
System.err.println(e);
}
```

##  Path --> File

也可以使用toFile（）方法将Path转换为File对象，如下所示。 

这是一座伟大的桥梁因为File类还包含一个名为toPath（）的方法用于重新转换，所以请在Path和File之间进行选择。

```Java
//output: BNP.txt
File path_to_file = path.toFile();
//output: \rafaelnadal\tournaments\2009\BNP.txt
Path file_to_path = path_to_file.toPath();
System.out.println("Path to file name: " + path_to_file.getName());
System.out.println("File to path: " + file_to_path.toString());
```

----