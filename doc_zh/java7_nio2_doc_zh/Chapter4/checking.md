# 检查文件和目录

Files类提供了一组isSomething方法，可用于执行各种类型的操作。

在实际操作文件或目录之前进行检查。 

其中一些方法已在前几章，其余的都在这里介绍。 

利用这些方法是推荐使用，因为它们在帮助您避免出现异常或其他奇怪情况方面非常有用应用程序中的行为。 

例如，最好在移动之前检查文件是否存在。

它到另一个位置。 

同样，在尝试执行以下操作之前，最好检查一下是否可以读取文件：从中读取。 

您还可以通过元数据属性来执行其中一些检查见第二章。

##  检查文件或目录的存在

从前面的章节中可以知道，即使映射文件或目录实际上不存在。 

而且，句法路径方法可以成功地应用于这种情况是因为它们不能对文件或目录本身进行操作。 

但是在某个时候，知道文件或目录是否存在非常重要，这就是为什么Files类提供了可以通过以下两种方法进行这种检查：

-   exists()：检查文件是否存在
-   notExists()：检查文件是否不存在

两种方法都接收两个参数，分别代表要测试的文件的路径和指示如何处理符号链接。 

如果文件存在，则exist（）方法返回true，否则返回false（该文件不存在或无法执行检查）。

以下代码段检查文件AEGON.txt是否存在于 `C:\rafaelnadal\tournaments\2009` 目录（在我们假设的目录结构中，此文件存在）：

```Java
Path path = FileSystems.getDefault().getPath("C:/rafaelnadal/tournaments/2009","AEGON.txt");
…
boolean path_exists = Files.exists(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
```

如果仅在文件不存在时才需要采取措施，请调用notExists（）方法，该方法如果文件不存在，则返回true，否则返回false（文件存在或无法进行检查执行）：

```Java
Path path = FileSystems.getDefault().getPath("C:/rafaelnadal/tournaments/2009","AEGON.txt");
…
boolean path_notexists = Files.notExists(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
```

如果两个方法都应用于相同的Path并且都返回false，则不能进行检查执行。 

例如，如果应用程序无权访问该文件，则状态为未知，并且两者都方法返回false。 

从这里，很容易得出结论，文件/目录的存在状态可以是：存在，不存在或未知。 

立即检查此状态后，结果已过时，因为存在一个文件可以在检查后立即删除，因此结果必须立即“过期”。

如果此方法指示文件存在，则无法保证子序列访问将成功。 

此外，SecurityException可能如果这些方法之一没有读取文件的权限，则抛出该错误。

！Files.exists（...）不等同于Files.notExists（...），notExists（）方法是而不是exist（）方法的补充。

##  检查文件可访问性

在访问文件之前，另一个好的做法是使用isReadable（）检查文件的可访问性级别，isWritable（）和isExecutable（）方法。

传递要验证的路径后，这些方法将分别检查它是否是可读路径（文件存在，并且JVM有权打开该路径以进行验证）读取），可写路径（文件存在，并且JVM有权打开该文件进行写入），并且可执行文件路径（文件存在，并且JVM有权执行该文件）。

另外，您可以通过调用isRegularFile（）方法来检查Path是否指向常规文件。

常规文件是没有特殊特征的文件（它们不是符号链接，目录等）,并包含真实数据，例如文本或二进制文件。 

isReadable（），isWritable（），isExecutable（）和isRegularFile（）都返回布尔值：如果文件存在并且是可读，可写，可执行的，则返回true

常规；如果文件不存在，则为false；读取，写入，执行和常规访问将被拒绝，因为JVM权限不足，或者无法确定访问权限。

将这些方法放入代码段中，以检查AEGON.txt文件在 `C:\rafaelnadal\tournaments\2009` 目录（该文件必须存在）如下所示：

```Java
Path path = FileSystems.getDefault().getPath("C:/rafaelnadal/tournaments/2009","AEGON.txt");

boolean is_readable = Files.isReadable(path);
boolean is_writable = Files.isWritable(path);
boolean is_executable = Files.isExecutable(path);
boolean is_regular = Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS);

if ((is_readable) && (is_writable) && (is_executable) && (is_regular)) {
    System.out.println("The checked file is accessible!");
} else {
    System.out.println("The checked file is not accessible!");
}
```

或者，您可以使用以下较短的版本：

```Java
boolean is_accessible = Files.isRegularFile(path) & Files.isReadable(path) &Files.isExecutable(path) & Files.isWritable(path);

if (is_accessible) {
    System.out.println("The checked file is accessible!");
} else {
    System.out.println("The checked file is not accessible!");
}
```

前面的示例通过将所有四个方法应用于路径来检查可访问性，但是您可以根据您需要获得的可访问性级别，以不同方式将这四种方法结合起来。 

对于例如，您可能不在乎路径是否可写，在这种情况下，您可以排除此检查。

即使这些方法确认了可访问性，也无法保证文件可以被访问。

解释存在于一个著名的软件错误中，该错误称为“使用时间检查”（TOCTTOU，发音为“ TOCK too”），这意味着在检查和使用检查结果之间的时间内，系统可能会遭受各种变化。 

Unix爱好者可能熟悉此概念，但是它适用以及其他任何系统。

##  检查两个路径是否指向同一个文件

在上一章中，您了解了如何检查符号链接和目标是否指向同一文件。

您可以使用isSameFile（）方法执行的另一项常见测试是检查是否有两个路径表示不同的指向同一文件。 

例如，相对路径和绝对路径可能指向到同一文件，即使它不是很明显。 

调用isSameFile（）方法将在以下代码段，以三种不同方式表示MutuaMadridOpen.txt文件的路径（该文件必须存在于 `C:\rafaelnadal\tournaments\2009`目录中）：

```Java
Path path_1 = FileSystems.getDefault().getPath("C:/rafaelnadal/tournaments/2009","MutuaMadridOpen.txt");
Path path_2 = FileSystems.getDefault().getPath("/rafaelnadal/tournaments/2009","MutuaMadridOpen.txt");
Path path_3 = FileSystems.getDefault().getPath("/rafaelnadal/tournaments/dummy/../2009","MutuaMadridOpen.txt");
try {
    boolean is_same_file_12 = Files.isSameFile(path_1, path_2);
    boolean is_same_file_13 = Files.isSameFile(path_1, path_3);
    boolean is_same_file_23 = Files.isSameFile(path_2, path_3);

    System.out.println("is same file 1&2 ? " + is_same_file_12);
    System.out.println("is same file 1&3 ? " + is_same_file_13);
    System.out.println("is same file 2&3 ? " + is_same_file_23);
} catch (IOException e) {
    System.err.println(e);
}
```

输出如下：

```base
is same file 1&2 ? true

is same file 1&3 ? true

is same file 2&3 ? true
```

##  检查文件可见性

如果需要确定文件是否被隐藏，可以调用Files.isHidden（）方法。 

牢记“隐藏”的概念取决于平台/提供者，您只需要传递要检查的路径即可并得到正确或错误的回应。

以下代码段检查MutuaMadridOpen.txt文件是否为隐藏文件（该文件必须存在于 `C:\rafaelnadal\tournaments\2009` 目录中）：

```Java
Path path = FileSystems.getDefault().getPath("C:/rafaelnadal/tournaments/2009","MutuaMadridOpen.txt");
…
try {
    boolean is_hidden = Files.isHidden(path);
    System.out.println("Is hidden ? " + is_hidden);
} catch (IOException e) {
    System.err.println(e);
}
```

----
