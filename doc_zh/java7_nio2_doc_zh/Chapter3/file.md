# 检查链接和目标是否是同一文件

有时，您可能需要检查链接和目标是否指向同一文件（位置）。 

你可以得到这个信息以不同的方式提供，但是一个简单的解决方案是使用Files.isSameFile（）方法。 

这个方法从用户处接收要比较的两个路径，并返回一个布尔值。 

以下代码段创建目标和该目标的符号链接，然后应用 isSameFile（）方法。 

它为文件创建一个名为rafael.nadal.7的符号链接，`C:\rafaelnadal\photos\rafa_winner.jpg`（文件必须存在，文件系统必须具有以下权限：创建符号链接）。

```Java
…
Path link = FileSystems.getDefault().getPath("rafael.nadal.7");
Path target = FileSystems.getDefault().getPath("C:/rafaelnadal/photos", "rafa_winner.jpg");
try {
    Files.createSymbolicLink(link, target);
} catch (IOException | UnsupportedOperationException | SecurityException e) {

}

try {
    Path linkedpath = Files.readSymbolicLink(link);
    System.out.println(linkedpath.toString());
} catch (IOException e) {
    System.err.println(e);
}
```

输出如下：

```base
rafael.nadal.7 and C:\rafaelnadal\photos\rafa_winner.jpg point to the same location
```