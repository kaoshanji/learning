# 定位链接目标


从链接开始，可以通过调用readSymbolicLink（）来找到其目标（可能不存在）。

此方法从用户接收链接作为路径，并返回代表链接目标的Path对象。 

如果传递的路径不是链接，则将引发NotLinkException异常。

以下代码段使用此方法为文件创建名为rafael.nadal.6的符号链接 `C:\rafaelnadal\photos\rafa_winner.jpg`（文件必须存在，文件系统必须具有以下权限：创建符号链接）：

```Java
Path link = FileSystems.getDefault().getPath("rafael.nadal.6");
Path target = FileSystems.getDefault().getPath("C:/rafaelnadal/photos", "rafa_winner.jpg");
…
try {
    Path linkedpath = Files.readSymbolicLink(link);
    System.out.println(linkedpath.toString());
} catch (IOException e) {
    System.err.println(e);
}
```

----