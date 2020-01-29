# 检查符号链接

Path的不同实例可以指向文件或链接，因此您可以检测Path实例是否指向文件或链接。

通过调用Files.isSymbolicLink（）方法进行符号链接。 

它接收一个参数，代表要测试的路径，并返回一个布尔值。 

以下代码段是一个简单的示例测试符号链接的路径。 

它为文件创建一个名为rafael.nadal.5的符号链接 `C:\rafaelnadal\photos\rafa_winner.jpg`（文件必须存在，文件系统必须具有以下权限：创建符号链接）。

```Java
…
Path link = FileSystems.getDefault().getPath("rafael.nadal.5");
Path target = FileSystems.getDefault().getPath("C:/rafaelnadal/photos", "rafa_winner.jpg");

try {
    Files.createSymbolicLink(link, target);
} catch (IOException | UnsupportedOperationException | SecurityException e) {
...
}
//check if a path is a symbolic link - solution 1
boolean link_isSymbolicLink_1 = Files.isSymbolicLink(link);
boolean target_isSymbolicLink_1 = Files.isSymbolicLink(target);

System.out.println(link.toString() + " is a symbolic link ? " + link_isSymbolicLink_1);
System.out.println(target.toString() + " is a symbolic link ? " + target_isSymbolicLink_1);
…

```

此代码输出以下结果：

```base
rafael.nadal.5 is a symbolic link ? true

C:\rafaelnadal\photos\rafa_winner.jpg is a symbolic link ? false
```

如第2章所述，您可以使用属性视图来测试Path的符号链接。 

基础的视图提供了名为isSymbolicLink的属性，如果指定的Path找到文件，则该属性返回true这是一个符号链接。 

您可以通过readAttributes（）方法查看isSymbolicLink属性。（在这种情况下不建议使用，因为它会返回大量属性列表），或者更容易地通过getAttribute（）方法，可以按如下方式使用：

```Java
…
try {
    
    boolean link_isSymbolicLink_2 = (boolean) Files.getAttribute(link,"basic:isSymbolicLink");
    boolean target_isSymbolicLink_2 = (boolean) Files.getAttribute(target,"basic:isSymbolicLink");
    
    System.out.println(link.toString() + " is a symbolic link ? " + link_isSymbolicLink_2);
    System.out.println(target.toString() + " is a symbolic link ? "+ target_isSymbolicLink_2);

} catch (IOException | UnsupportedOperationException e) {
    System.err.println(e);
}
…
```

同样，输出是

```base
rafael.nadal.5 is a symbolic link ? true

C:\rafaelnadal\photos\rafa_winner.jpg is a symbolic link ? false
```

----
