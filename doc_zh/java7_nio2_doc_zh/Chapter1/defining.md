# 定义一个Path

一旦确定了文件系统以及文件或目录的位置，就可以为其创建Path对象。绝对路径，相对路径，用符号“.”定义的路径（表示当前目录）或“ ..”（表示父目录），并且仅包含文件/目录名称的路径由路径类。 

定义路径的最简单解决方案是调用路径的 get() 方法。

 以下小节介绍几种定义同一文件路径的不同方法（在Windows上）-C:\rafaelnadal\tournaments\2009\BNP.txt。


##  定义绝对 Path

绝对路径（也称为完整路径或文件路径）是包含根目录和所有包含文件或文件夹的其他子目录。

在NIO.2中定义绝对路径是一行代码，如您在以下示例中看到的那样，该任务指向文件中的名为BNP.txt的文件：C:\rafaelnadal\tournaments\200 目录（该文件可能不存在，无法测试此代码）：

```Java
Path path = Paths.get("C:/rafaelnadal/tournaments/2009/BNP.txt");
```

get()还允许您将路径拆分为一组块。 NIO将为您重建路径，不管有多少块。 

请注意，如果您为路径的每个组件定义一个块，则可以省略文件分隔符分隔符。 前面的绝对路径可以分块为“跟随”：

```Java
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
Path path = Paths.get("C:", "rafaelnadal/tournaments/2009", "BNP.txt");
Path path = Paths.get("C:", "rafaelnadal", "tournaments", "2009", "BNP.txt");
```

##  定义相对于文件存储根的 Path

相对路径（也称为非绝对路径或部分路径）仅是完整路径的一部分。 

一种相对路径通常用于创建网页。 相对路径的使用比绝对路径。 定义相对于当前文件存储根的路径应从文件定界符开始。

在以下示例中，如果当前文件存储根为 C:，则绝对路径为 C:\rafaelnadal\tournaments\2009\BNP.txt：

```Java
Path path = Paths.get("/rafaelnadal/tournaments/2009/BNP.txt");
Path path = Paths.get("/rafaelnadal","tournaments/2009/BNP.txt");
```

##  定义相对于工作文件夹的 Path

当您定义相对于当前工作文件夹的路径时，该路径不应以文件开头定界符。 如果当前文件夹是 C:根目录下的 /ATP，则以下命令返回的绝对路径代码段是 C:\ATP\rafaelnadal\tournaments\2009\BNP.txt：

```Java
Path path = Paths.get("rafaelnadal/tournaments/2009/BNP.txt");
Path path = Paths.get("rafaelnadal","tournaments/2009/BNP.txt");
```

##  使用快捷方式定义 Path

使用符号 “.”（指示当前目录）或“..”（指示父目录）定义路径目录）是一种常见的做法。 

NIO.2可以处理这些类型的路径，以消除可能的情况，如果您调用Path.normalize（）方法（该方法将删除所有冗余元素，包括出现的任何“。”或“ directory / ..”）：

```Java
Path path = Paths.get("C:/rafaelnadal/tournaments/2009/dummy/../BNP.txt").normalize();
Path path = Paths.get("C:/rafaelnadal/tournaments/./2009/dummy/../BNP.txt").normalize();
```

如果要查看normalize（）方法的效果，请尝试使用和不使用定义相同的Path#normalize（），如下所示，并将结果打印到控制台：

```Java
Path noNormalize = Paths.get("C:/rafaelnadal/tournaments/./2009/dummy/../BNP.txt");
Path normalize = Paths.get("C:/rafaelnadal/tournaments/./2009/dummy/../BNP.txt").normalize();
```

如果使用System.out.println（）打印前面的路径，则会在以下位置看到以下结果：其中normalize（）删除了多余的元素：

```bash
C:\rafaelnadal\tournaments\.\2009\dummy\..\BNP.txt
C:\rafaelnadal\tournaments\2009\BNP.txt
```

##  使用 URI 定义 Path

在某些情况下，您可能需要根据统一资源标识符（URI）创建路径。 

你可以这样做使用URI.create（）方法从给定的字符串并使用Paths.get（）方法创建URI，它以URI对象作为参数。 

如果您需要封装一个可以在网络浏览器的地址栏中输入：

```Java
import java.net.URI;
…
Path path = Paths.get(URI.create("file:///rafaelnadal/tournaments/2009/BNP.txt"));
Path path = Paths.get(URI.create("file:///C:/rafaelnadal/tournaments/2009/BNP.txt"));
```

##  FileSystems.getDefault().getPath() 定义 Path

创建路径的另一个常见解决方案是使用FileSystems类。 

首先，通过 getDefault（）方法获取默认文件系统-NIO.2将提供一个通用对象，该对象是能够访问默认文件系统。 

然后，您可以按以下方式调用getPath（）方法（前面示例中的Paths.get（）方法只是此解决方案的简写）：

```Java
import java.nio.file.FileSystems;
…
Path path = FileSystems.getDefault().getPath("/rafaelnadal/tournaments/2009", "BNP.txt");
Path path = FileSystems.getDefault().getPath("/rafaelnadal/tournaments/2009/BNP.txt");
Path path = FileSystems.getDefault().getPath("rafaelnadal/tournaments/2009", "BNP.txt");
Path path = FileSystems.getDefault().getPath("/rafaelnadal/tournaments/./2009","BNP.txt").normalize();
```

##  获取主目录的 Path

当您需要指向主目录的路径时，可以如下所示进行操作示例（返回的主目录取决于每台计算机和每个操作系统）：

```Java
Path path = Paths.get(System.getProperty("user.home"), "downloads", "game.exe");
```

在我的Windows 7计算机上，这将返回 C:\Users\Leo\downloads\game.exe，而在我朋友的计算机上CentOS系统（Linux），返回 /home/simpa/downloads/game.exe。

----
