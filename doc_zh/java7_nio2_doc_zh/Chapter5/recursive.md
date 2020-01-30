# 开始递归

一旦创建了递归机制（通过实现FileVisitor接口或扩展SimpleFileVisitor类），则可以通过调用两者之一来启动该过程
Files.walkFileTree（）方法。

最简单的walkFileTree（）方法获取起始文件（这是通常是文件树的根）和为每个文件调用的文件访问者（这是递归的一个实例）
机制类）。 

例如，您可以通过调用 walkFileTree（）方法如下（传递的文件树为 C:\rafaelnadal）：

```Java
Path listDir = Paths.get("C:/rafaelnadal"); //define the starting file tree
ListTree walk = new ListTree(); //instantiate the walk

try{
    Files.walkFileTree(listDir, walk); //start the walk
} catch(IOException e){
    System.err.println(e);
}
```

第二个walkFileTree（）方法获取起始文件，用于自定义步行的选项，要访问的最大目录级别数（为确保遍历所有级别，您可以指定
Integer.MAX_VALUE（用于最大深度参数），以及walk实例。 

可接受的选项是FileVisitOption枚举的常量。 

实际上，该枚举包含单个常量，名为FOLLOW_LINKS，表示在步行中遵循符号链接（默认情况下，不然后）。

在前面的遍历中调用此方法可能如下所示：

```Java
Path listDir = Paths.get("C:/rafaelnadal"); //define the starting file
ListTree walk = new ListTree(); //instantiate the walk
EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS); //follow links

try{
    Files.walkFileTree(listDir, opts, Integer.MAX_VALUE, walk); //start the walk
} catch(IOException e){
    System.err.println(e);
}
```

调用walkFileTree（start，visitor）与调用walkFileTree（start，EnumSet.noneOf（FileVisitOption.class），Integer.MAX_VALUE，访问者）。

以下几行是上述示例的输出：

```base
Visited directory: C:\rafaelnadal\equipment

Visited directory: C:\rafaelnadal\grandslam\AustralianOpen

Visited directory: C:\rafaelnadal\grandslam\RolandGarros

Visited directory: C:\rafaelnadal\grandslam\USOpen

Visited directory: C:\rafaelnadal\grandslam\Wimbledon

Visited directory: C:\rafaelnadal\grandslam
…
Visited directory: C:\rafaelnadal
```
