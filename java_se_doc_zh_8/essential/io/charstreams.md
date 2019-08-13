# 字符流

Java平台使用Unicode约定存储字符值。字符流I / O自动将此内部格式转换为本地字符集。在Western语言环境中，本地字符集通常是ASCII的8位超集。

对于大多数应用程序，具有字符流的I / O并不比具有字节流的I / O复杂。使用流类完成的输入和输出会自动转换为本地字符集和从本地字符集转换。使用字符流代替字节流的程序会自动适应本地字符集，并且可以进行国际化 - 所有这些都不需要程序员的额外努力。

如果国际化不是优先考虑事项，您可以简单地使用字符流类而不必过多关注字符集问题。之后，如果国际化成为优先事项，您的程序可以进行调整而无需进行大量重新编码。有关更多信息，请参阅 [国际化](../../i18n/index.html)路径。

## 使用字符流

所有字符流类都来自 [`Reader`](https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html)和 [`Writer`](https://docs.oracle.com/javase/8/docs/api/java/io/Writer.html)。与字节流一样，有一些专门用于文件I / O的字符流类： [`FileReader`](https://docs.oracle.com/javase/8/docs/api/java/io/FileReader.html)和 [`FileWriter`](https://docs.oracle.com/javase/8/docs/api/java/io/FileWriter.html)。该 [`CopyCharacters`](examples/CopyCharacters.java)示例说明了这些类。

```java
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CopyCharacters {
    public static void main(String[] args) throws IOException {

        FileReader inputStream = null;
        FileWriter outputStream = null;

        try {
            inputStream = new FileReader("xanadu.txt");
            outputStream = new FileWriter("characteroutput.txt");

            int c;
            while ((c = inputStream.read()) != -1) {
                outputStream.write(c);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
```

`CopyCharacters`非常相似`CopyBytes`。最重要的区别是`CopyCharacters`使用`FileReader`和`FileWriter`输入和输出代替`FileInputStream`和`FileOutputStream`。请注意，两个`CopyBytes`和`CopyCharacters`使用一个`int`变量来读取和写入从。但是，在变量中`CopyCharacters`，`int`变量在最后16位中保存一个字符值; in `CopyBytes`，`int`变量保存`byte`最后8位的值。

### 使用字节流的字符流

字符流通常是字节流的“包装器”。字符流使用字节流来执行物理I / O，而字符流处理字符和字节之间的转换。`FileReader`例如，在使用`FileInputStream`时`FileWriter`使用`FileOutputStream`。

有两个通用的字节到字符“桥”流： [`InputStreamReader`](https://docs.oracle.com/javase/8/docs/api/java/io/InputStreamReader.html)和 [`OutputStreamWriter`](https://docs.oracle.com/javase/8/docs/api/java/io/OutputStreamWriter.html)。当没有符合您需求的预打包字符流类时，使用它们来创建字符流。该 [插座的教训](../../networking/sockets/readingWriting.html)在 [网络跟踪](../../networking/index.html)显示了如何创建从套接字类提供的字节流的字符流。

## 面向行的I / O.

字符I / O通常以比单个字符更大的单位出现。一个常见的单位是行：一串字符，末尾有一个行终止符。行终止符可以是回车符/换行序列（`"\r\n"`），单个回车符（`"\r"`）或单个换行符（`"\n"`）。支持所有可能的行终止符允许程序读取在任何广泛使用的操作系统上创建的文本文件。

让我们修改`CopyCharacters`示例以使用面向行的I / O. 要做到这一点，我们必须使用我们以前从未见过的两个类， [`BufferedReader`](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html)并且 [`PrintWriter`](https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html)。我们将在[缓冲I / O](buffers.html)和[格式化](formatting.html)中更深入地探索这些类。现在，我们只对他们对面向行的I / O的支持感兴趣。

该 [`CopyLines`](examples/CopyLines.java)示例调用`BufferedReader.readLine`并`PrintWriter.println`一次输入和输出一行。

```java
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

public class CopyLines {
    public static void main(String[] args) throws IOException {

        BufferedReader inputStream = null;
        PrintWriter outputStream = null;

        try {
            inputStream = new BufferedReader(new FileReader("xanadu.txt"));
            outputStream = new PrintWriter(new FileWriter("characteroutput.txt"));

            String l;
            while ((l = inputStream.readLine()) != null) {
                outputStream.println(l);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
```

调用`readLine`返回带有该行的文本行。`CopyLines`使用输出每一行`println`，它附加当前操作系统的行终止符。这可能与输入文件中使用的行终止符不同。

构造文本输入和输出的方法有很多种，可以超出字符和行。有关更多信息，请参阅[扫描和格式](scanfor.html)。