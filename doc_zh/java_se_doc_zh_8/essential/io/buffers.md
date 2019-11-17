# 缓冲流

到目前为止，我们看到的大多数示例都使用*无缓冲的* I / O. 这意味着每个读取或写入请求都由底层操作系统直接处理。这可以使程序效率低得多，因为每个这样的请求经常触发磁盘访问，网络活动或一些相对昂贵的其他操作。

为了减少这种开销，Java平台实现了*缓冲的* I / O流。缓冲输入流从称为*缓冲区*的存储区读取数据; 仅当缓冲区为空时才调用本机输入API。类似地，缓冲输出流将数据写入缓冲区，并且仅在缓冲区已满时才调用本机输出API。

程序可以使用我们现在多次使用的包装习惯用法将无缓冲的流转换为缓冲流，其中无缓冲的流对象被传递给缓冲流类的构造函数。以下是您可以修改`CopyCharacters`示例中的构造函数调用以使用缓冲I / O的方法：

```java
inputStream = new BufferedReader(new FileReader("xanadu.txt"));
outputStream = new BufferedWriter(new FileWriter("characteroutput.txt"));
```

还有用于包装缓冲流4个缓冲流类： [`BufferedInputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedInputStream.html)与 [`BufferedOutputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedOutputStream.html)创建缓冲字节流，而 [`BufferedReader`](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html)与 [`BufferedWriter`](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedWriter.html)创建缓冲字符流。

## 刷新缓冲流

在关键点写出缓冲区通常是有意义的，而无需等待它填充。这称为*刷新*缓冲区。

一些缓冲的输出类支持*autoflush*，由可选的构造函数参数指定。启用autoflush时，某些键事件会导致刷新缓冲区。例如，autoflush `PrintWriter`对象在每次调用`println`or时刷新缓冲区`format`。有关这些方法的更多信息，请参阅[格式化](formatting.html)

要手动刷新流，请调用其`flush`方法。该`flush`方法在任何输出流上都有效，但除非缓冲流，否则无效。