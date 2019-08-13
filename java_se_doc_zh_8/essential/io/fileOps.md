# 文件操作

的 [`Files`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html)类是另一主要入口点`java.nio.file`包。该类提供了一组丰富的静态方法，用于读取，写入和操作文件和目录。这些`Files`方法适用于`Path`对象的实例。在继续讨论其余部分之前，您应该熟悉以下常见概念：

- [释放系统资源](#resources)
- [捕捉异常](#exception)
- [可变参数](#varargs)
- [原子操作](#atomic)
- [方法链接](#chaining)
- [什么*是* Glob？](#glob)
- [链接意识](#linkaware)

## 释放系统资源

此API中使用的许多资源（例如流或通道）实现或扩展 [`java.io.Closeable`](https://docs.oracle.com/javase/8/docs/api/java/io/Closeable.html)接口。`Closeable`资源的要求是`close`必须调用该方法以在不再需要时释放资源。忽略关闭资源可能会对应用程序的性能产生负面影响。在`try-`与资源的语句，在下一节中描述的那样，处理这一步你。

## 捕捉异常

对于文件I / O，意外情况是生活中的事实：文件存在（或不存在）预期时，程序无法访问文件系统，默认文件系统实现不支持特定功能， 等等。可能遇到许多错误。

访问文件系统的所有方法都可以抛出`IOException`。最佳实践是通过将这些方法嵌入`try-`Java SE 7发行版中引入的with-resources语句来捕获这些异常。在`try-`与资源语句，编译器会自动生成的代码，关闭时不再需要的资源（S）的优势。以下代码显示了它的外观：

```java
Charset charset = Charset.forName("US-ASCII");
String s = ...;
try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
    writer.write(s, 0, s.length());
} catch (IOException x) {
    System.err.format("IOException: %s%n", x);
}
```

有关更多信息，请参阅 [try-with-resources语句](../../essential/exceptions/tryResourceClose.html)。

或者，您可以将文件I / O方法嵌入`try`块中，然后捕获块中的任何异常`catch`。如果您的代码已打开任何流或通道，则应在`finally`块中关闭它们。前面的示例使用try-catch-finally方法看起来如下所示：

```java
Charset charset = Charset.forName("US-ASCII");
String s = ...;
BufferedWriter writer = null;
try {
    writer = Files.newBufferedWriter(file, charset);
    writer.write(s, 0, s.length());
} catch (IOException x) {
    System.err.format("IOException: %s%n", x);
} finally {
    if (writer != null) writer.close();
}
```

有关更多信息，请参阅 [捕获和处理异常](../../essential/exceptions/handling.html)。

除此之外`IOException`，还有许多具体的例外情况 [`FileSystemException`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystemException.html)。这个类有返回有关文件的一些有用的方法 [（`getFile`）](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystemException.html#getFile--)，详细消息字符串 [（`getMessage`）](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystemException.html#getMessage--)，为什么文件系统操作失败的原因 [（`getReason`）](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystemException.html#getReason--)，以及所涉及的“其他”的文件，如果有 [（`getOtherFile`）](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystemException.html#getOtherFile--)。

以下代码段显示了如何使用该`getFile`方法：

```java
try (...) {
    ...    
} catch (NoSuchFileException x) {
    System.err.format("%s does not exist\n", x.getFile());
}
```

为清楚起见，本课程中的文件I / O示例可能不会显示异常处理，但您的代码应始终包含它。

## 可变参数

`Files`指定标志时，有几种方法接受任意数量的参数。例如，在以下方法签名中，`CopyOption`参数后面的省略号表示该方法接受可变数量的参数或*varargs*，因为它们通常被称为：

```java
Path Files.move(Path, Path, CopyOption...)
```

当方法接受varargs参数时，您可以将逗号分隔的值列表或值的数组（`CopyOption[]`）传递给它。

在该`move`示例中，可以按如下方式调用该方法：

```java
import static java.nio.file.StandardCopyOption.*;

Path source = ...;
Path target = ...;
Files.move(source,
           target,
           REPLACE_EXISTING,
           ATOMIC_MOVE);
```

有关varargs语法的更多信息，请参阅 [任意数量的参数](../../java/javaOO/arguments.html#varargs)。

## 原子操作

有些`Files`方法，例如`move`，可以在某些文件系统中以原子方式执行某些操作。

一个*原子文件操作*是不能被中断或“部分”执行的操作。执行整个操作或操作失败。当您在文件系统的同一区域上运行多个进程时，这很重要，并且您需要保证每个进程都访问一个完整的文件。

## 方法链接

许多文件I / O方法都支持*方法链*的概念。

您首先调用返回对象的方法。然后，您立即在*该*对象上调用一个方法，*该*方法返回另一个对象，依此类推。许多I / O示例使用以下技术：

```java
String value = Charset.defaultCharset().decode(buf).toString();
UserPrincipal group =
    file.getFileSystem().getUserPrincipalLookupService().
         lookupPrincipalByName("me");
```

此技术生成紧凑的代码，使您可以避免声明不需要的临时变量。

## 什么*是* Glob？

`Files`该类中的两个方法接受一个glob参数，但什么是*glob*？

您可以使用glob语法指定模式匹配行为。

glob模式被指定为字符串，并与其他字符串匹配，例如目录或文件名。Glob语法遵循几个简单的规则：

- 星号，`*`匹配任意数量的字符（包括无）。

- 两个星号，`**`工作`*`但跨越目录边界。此语法通常用于匹配完整路径。

- 问号，`?`恰好匹配一个字符。

- 大括号指定子模式的集合。例如：

  - `{sun,moon,stars}` 匹配“太阳”，“月亮”或“星星”。
  - `{temp*,tmp*}` 匹配以“temp”或“tmp”开头的所有字符串。

- 方括号表示一组单个字符，或者在使用连字符（

  ```
  -
  ```

  ）时，表示一系列字符。例如：

  - `[aeiou]` 匹配任何小写元音。
  - `[0-9]` 匹配任何数字。
  - `[A-Z]` 匹配任何大写字母。
  - `[a-z,A-Z]` 匹配任何大写或小写字母。

  在方括号内，

  ```
  *
  ```

  ，

  ```
  ?
  ```

  ，并

  ```
  \
  ```

  与自身匹配。

- 所有其他角色都匹配自己。

- 要匹配`*`，`?`或其他特殊字符，您可以使用反斜杠字符来转义它们`\`。例如：`\\`匹配单个反斜杠，并`\?`匹配问号。

以下是glob语法的一些示例：

- `*.html`- 匹配以*.html*结尾的所有字符串
- `???` - 匹配所有字符串，正好是三个字母或数字
- `*[0-9]*` - 匹配包含数值的所有字符串
- `*.{htm,html,pdf}`- 匹配以*.htm*，*.html*或*.pdf*结尾的任何字符串
- `a?*.java`- 匹配以？开头的任何字符串`a`，后跟至少一个字母或数字，以*.java*结尾
- `{foo*,*[0-9]*}`- 匹配以*foo*开头的任何字符串或包含数字值的任何字符串

------

**注意：**  如果在键盘上键入glob模式并且它包含一个特殊字符，则必须将模式放在quotes（）中，使用反斜杠（），或使用命令行支持的任何转义机制。

glob语法功能强大且易于使用。但是，如果它不足以满足您的需求，您还可以使用正则表达式。有关更多信息，请参阅 [正则表达式](../../essential/regex/index.html)课程。

有关glob sytnax的更多信息，请参阅类中[`getPathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-)方法的API规范 `FileSystem`。

## 链接意识

该`Files`课程是“链接感知”。每个`Files`方法都会检测遇到符号链接时要执行的操作，或者提供一个选项，使您能够在遇到符号链接时配置行为。