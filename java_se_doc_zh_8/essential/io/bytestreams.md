# Byte Streams

程序使用*字节流*来执行8位字节的输入和输出。所有字节流类都来自 [`InputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html)和 [`OutputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/OutputStream.html)。

有许多字节流类。为了演示字节流的工作原理，我们将重点关注文件I / O字节流， [`FileInputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/FileInputStream.html)以及 [`FileOutputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/FileOutputStream.html)。其他种类的字节流的使用方式大致相同; 它们的不同之处主要在于它们的构造方式。

## 使用字节流

我们将探索`FileInputStream`并`FileOutputStream`检查一个名为的示例程序[`CopyBytes`](examples/CopyBytes.java)，该程序 使用字节流进行复制`xanadu.txt`，一次一个字节。

```java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyBytes {
    public static void main(String[] args) throws IOException {

        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream("xanadu.txt");
            out = new FileOutputStream("outagain.txt");
            int c;

            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
```

`CopyBytes`大部分时间都花在一个简单的循环中，该循环读取输入流并一次写入一个字节的输出流， 如下图所示。

![简单的字节流输入和输出。](images/byteStream.gif)



简单的字节流输入和输出。

## 始终关闭流

在不再需要流时关闭流是非常重要的 - `CopyBytes`使用`finally`块来保证即使发生错误也将关闭两个流非常重要。这种做法有助于避免严重的资源泄漏。

一个可能的错误是`CopyBytes`无法打开一个或两个文件。发生这种情况时，对应于该文件的流变量永远不会从其初始`null`值更改。这就是为什么`CopyBytes`在调用之前确保每个流变量都包含一个对象引用`close`。

## 何时不使用字节流

`CopyBytes`看起来像一个普通的程序，但它实际上代表了一种你应该避免的低级I / O. 由于`xanadu.txt`包含字符数据，最好的方法是使用[字符流](charstreams.html)，如下一节中所述。还有用于更复杂数据类型的流。字节流应仅用于最原始的I / O.

那么为什么要谈论字节流呢？因为所有其他流类型都是基于字节流构建的。