# 从命令行创建链接

Windows用户可以使用mklink从命令行创建符号链接和硬链接命令。 

该命令获得一组选项，具体取决于您需要创建哪种类型的链接。

其中一些选项如下：

```base
/D Creates a directory symbolic link. Default is a file symbolic link.
/H Creates a hard link instead of a symbolic link.
/J Creates a Directory Junction.
Link specifies the new symbolic link name.
Target specifies the path (relative or absolute) that the new link refers to.
```

例如，如果要使文件夹 C:\rafaelnadal\photos 可从 C:\rafaelnadal也可以使用以下命令：

```base
mklink /D  C:\rafaelnadal  C:\rafaelnadal\photos
```

现在，如果您查看C：\ rafaelnadal目录，您还将看到 `C:\rafaelnadal\photos` 目录。

Unix（Linux）用户可以使用名为ln的命令来实现与Windows之前的示例（请注意，目标文件位于第一位，链接名称位于第二位这个案例）：

```base
ln –s / home / rafaelnadal /照片/ home / rafaelnadal
```

另外，在Unix（Linux）中，您可以使用rm命令删除链接：`rm /home/rafaelnadal`

----
