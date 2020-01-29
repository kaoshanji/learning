# Paths比较

可以出于不同目的以不同方式测试两个路径的相等性。 

你可以测试一下通过调用Path.equals（）方法，两个路径相等。 

此方法尊重Object.equals（）规格。 它不访问文件系统，因此不需要比较的路径，并且它不检查路径是否为同一文件。 

在某些OS实现中，通过以下方式比较路径忽略大小写，而在其他实现中，比较则区分大小写-实现将指定是否考虑大小写。 

这里我显示了相对于当前文件的路径store和一个绝对路径，它们都表示相同的文件，但不等于：

```Java
Path path01 = Paths.get("/rafaelnadal/tournaments/2009/BNP.txt");
Path path02 = Paths.get("C:/rafaelnadal/tournaments/2009/BNP.txt");
if(path01.equals(path02)){
System.out.println("The paths are equal!");
} else {
System.out.println("The paths are not equal!"); //true
}
```

有时，您需要检查两个路径是否是相同的文件/文件夹。 

您可以轻松完成此操作，通过调用java.nio.File.Files.isSameFile（）方法（如以下示例所示），返回一个布尔值。

在后台，此方法使用Path.equals（）方法。 

如果Path.equals（）返回true，路径相等，因此不需要进一步的比较。 

如果它返回false，然后isSameFile（）方法开始执行操作以进行仔细检查。

请注意，方法要求比较的文件存在于文件系统上； 否则，将引发IOException。

```Java
try {
    boolean check = Files.isSameFile(path01, path02);
    if(check){
        System.out.println("The paths locate the same file!"); //true
    } else {
        System.out.println("The paths does not locate the same file!");
    }
} catch (IOException e) {
    System.out.println(e.getMessage());
}
```

由于Path类实现了Comparable接口，因此您可以使用compareTo（）方法，按字典顺序比较两个抽象路径。 

这可能对排序。 如果参数等于此路径，则该方法返回零；如果该路径为0，则返回小于零的值。

在字典上小于该参数，如果该路径在字典上小于，则该值大于零大于论点。

以下是使用compareTo（）方法的示例：

```Java
//output: 24
int compare = path01.compareTo(path02);
System.out.println(compare);
```

可以通过使用startsWith（）和endsWith（）方法来完成部分比较，如以下示例所示。 

使用这些方法，您可以测试当前路径是开始还是结束，分别使用给定的路径。两种方法都返回布尔值。

```Java
boolean sw = path01.startsWith("/rafaelnadal/tournaments");
boolean ew = path01.endsWith("BNP.txt");
System.out.println(sw); //output: true
System.out.println(ew); //output: true
```

----