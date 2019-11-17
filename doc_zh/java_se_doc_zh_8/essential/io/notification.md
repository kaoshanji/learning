# 查看目录以进行更改

您是否曾经发现自己使用IDE或其他编辑器编辑文件，并出现一个对话框，通知您文件系统中的某个打开文件已更改并需要重新加载？或者，与NetBeans IDE一样，应用程序只是在不通知您的情况下安静地更新文件。以下示例对话框显示了此通知在免费编辑器[jEdit](http://sourceforge.net/projects/jedit/)中的显示方式 ：

![示例jEdit对话框说明：另一个程序在磁盘上更改了以下文件。](images/io-jEditDialog.png)





jEdit对话框显示检测到修改过的文件



要实现此功能（称为*文件更改通知）*，程序必须能够检测文件系统上相关目录的内容。一种方法是轮询文件系统以查找更改，但这种方法效率低下。它不能扩展到具有数百个要监视的打开文件或目录的应用程序。

该`java.nio.file`软件包提供了一个名为Watch Service API的文件更改通知API。此API使您可以使用监视服务注册目录（或多个目录）。注册时，您告诉服务您感兴趣的事件类型：文件创建，文件删除或文件修改。当服务检测到感兴趣的事件时，它将被转发到注册的进程。已注册的进程有一个线程（或一个线程池），专门用于监视它已注册的任何事件。当一个事件进入时，它会根据需要进行处理。

本节包括以下内容：

- [观看服务概述](#overview)
- [试试看](#try)
- [创建Watch Service并注册事件](#register)
- [处理事件](#process)
- [检索文件名](#name)
- [何时使用和不使用此API](#concerns)

## 观看服务概述

该`WatchService`API是相当低的水平，使您可以自定义。您可以按原样使用它，也可以选择在此机制之上创建高级API，以便它适合您的特定需求。

以下是实施监视服务所需的基本步骤：

- `WatchService`为文件系统创建“观察者”。
- 对于要监视的每个目录，请将其注册到观察程序。注册目录时，您可以指定要通知的事件类型。您收到`WatchKey`您注册的每个目录的实例。
- 实现无限循环以等待传入事件。当事件发生时，密钥将发出信号并放入观察者的队列中。
- 从观察者的队列中检索密钥。您可以从密钥中获取文件名。
- 检索密钥的每个待处理事件（可能有多个事件）并根据需要进行处理。
- 重置密钥，然后继续等待事件。
- 关闭服务：当线程退出或关闭时（通过调用其`closed`方法），监视服务退出。

`WatchKeys`是线程安全的，可以与`java.nio.concurrent`包一起使用。您可以将[线程池](../concurrency/pools.html)专用 于此工作。

## 试试看

因为此API更高级，所以在继续之前尝试一下。将[`WatchDir`](examples/WatchDir.java)示例保存 到您的计算机，并进行编译。创建`test`将传递给示例的目录。`WatchDir`使用单个线程来处理所有事件，因此它在等待事件时阻止键盘输入。在单独的窗口中或在后台运行程序，如下所示：

```java
java WatchDir test &
```

在`test`目录中播放创建，删除和编辑文件。发生任何这些事件时，会向控制台输出一条消息。完成后，删除`test`目录并`WatchDir`退出。或者，如果您愿意，可以手动终止该过程。

您还可以通过指定`-r`选项来查看整个文件树。指定时`-r`，`WatchDir` [遍历文件树](walk.html)，使用监视服务注册每个目录。

## 创建Watch Service并注册事件

第一步是[`WatchService`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchService.html)使用类中的 [`newWatchService`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#newWatchService--)方法创建一个new `FileSystem`，如下所示：

```
WatchService watcher = FileSystems.getDefault（）。newWatchService（）;
```

接下来，使用监视服务注册一个或多个对象。[`Watchable`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Watchable.html)可以注册实现该接口的任何对象 。的`Path`类实现了`Watchable`接口，所以要被监视的每个目录登记为`Path`对象。

与任何一样`Watchable`，`Path`该类实现两个`register`方法。此页面使用双参数版本 [`register(WatchService, WatchEvent.Kind...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#register-java.nio.file.WatchService-java.nio.file.WatchEvent.Kind...-)。（三参数版本采用a `WatchEvent.Modifier`，目前尚未实现。）

使用监视服务注册对象时，可以指定要监视的事件类型。支持的 [`StandardWatchEventKinds`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/StandardWatchEventKinds.html)事件类型如下：

- `ENTRY_CREATE` - 创建目录条目。
- `ENTRY_DELETE` - 删除目录条目。
- `ENTRY_MODIFY` - 修改目录条目。
- `OVERFLOW` - 表示事件可能已丢失或丢弃。您无需注册`OVERFLOW`活动即可接收该活动。

以下代码段显示了如何`Path`为所有三种事件类型注册实例：

```java
import static java.nio.file.StandardWatchEventKinds.*;

Path dir = ...;
try {
    WatchKey key = dir.register(watcher,
                           ENTRY_CREATE,
                           ENTRY_DELETE,
                           ENTRY_MODIFY);
} catch (IOException x) {
    System.err.println(x);
}
```

## 处理事件

事件处理循环中的事件顺序如下：

1. 获取手表钥匙。提供了三种方法：
   - [`poll`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchService.html#poll--) - 返回排队的密钥（如果可用）。`null`如果不可用，立即返回一个值。
   - [`poll(long, TimeUnit)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchService.html#poll-long-java.util.concurrent.TimeUnit-) - 返回排队的密钥（如果有）。如果排队的密钥不能立即可用，程序将等待指定的时间。所述`TimeUnit`参数确定在指定的时间是否是纳秒，毫秒，或一些其他时间单元。
   - [`take`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchService.html#take--) - 返回排队的密钥。如果没有可用的排队密钥，则此方法将等待。
2. 处理密钥的挂起事件。你取`List`的 [`WatchEvents`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchEvent.html)从 [`pollEvents`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchKey.html#pollEvents--)方法。
3. 使用该[`kind`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchEvent.html#kind--)方法检索事件类型 。无论密钥注册的是什么事件，都可以接收`OVERFLOW`事件。您可以选择处理溢出或忽略它，但您应该测试它。
4. 检索与事件关联的文件名。文件名存储为事件的上下文，因此该 [`context`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchEvent.html#context--)方法用于检索它。
5. 处理完密钥事件后，需要`ready`通过调用将密钥置回状态 [`reset`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchEvent.html#reset--)。如果此方法返回`false`，则该键不再有效，并且循环可以退出。这一步非常**重要**。如果您未能调用`reset`，此密钥将不会再收到任何事件。

表键具有状态。在任何给定时间，其状态可能是以下之一：

- `Ready`表示密钥已准备好接受事件。首次创建时，密钥处于就绪状态。

- `Signaled`表示一个或多个事件已排队。一旦密钥被发出信号，它就不再处于就绪状态，直到[`reset`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchKey.html#reset--)调用该 方法。

- `Invalid` 表示密钥不再有效。发生以下事件之一时会发生此状态：
  - 该过程通过使用该[`cancel`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchKey.html#cancel--)方法明确取消密钥 。
  - 该目录无法访问。
  - 手表服务已 [关闭](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchService.html#close--)。

以下是事件处理循环的示例。它取自 [`Email`](examples/Email.java)示例，该示例监视目录，等待出现新文件。当新文件可用时，将检查它`text/plain`以使用该[`probeContentType(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#probeContentType-java.nio.file.Path-)方法确定它是否是文件 。目的是将`text/plain`文件通过电子邮件发送到别名，但该实现细节留给读者。

Watch服务API特有的方法以粗体显示：

```java
for (;;) {

    // wait for key to be signaled
    WatchKey key;
    try {
        key = watcher.take();
    } catch (InterruptedException x) {
        return;
    }

    for (WatchEvent<?> event: key.pollEvents()) {
        WatchEvent.Kind<?> kind = event.kind();

        // This key is registered only
        // for ENTRY_CREATE events,
        // but an OVERFLOW event can
        // occur regardless if events
        // are lost or discarded.
        if (kind == OVERFLOW) {
            continue;
        }

        // The filename is the
        // context of the event.
        WatchEvent<Path> ev = (WatchEvent<Path>)event;
        Path filename = ev.context();

        // Verify that the new
        //  file is a text file.
        try {
            // Resolve the filename against the directory.
            // If the filename is "test" and the directory is "foo",
            // the resolved name is "test/foo".
            Path child = dir.resolve(filename);
            if (!Files.probeContentType(child).equals("text/plain")) {
                System.err.format("New file '%s'" +
                    " is not a plain text file.%n", filename);
                continue;
            }
        } catch (IOException x) {
            System.err.println(x);
            continue;
        }

        // Email the file to the
        //  specified email alias.
        System.out.format("Emailing file %s%n", filename);
        //Details left to reader....
    }

    // Reset the key -- this step is critical if you want to
    // receive further watch events.  If the key is no longer valid,
    // the directory is inaccessible so exit the loop.
    boolean valid = key.reset();
    if (!valid) {
        break;
    }
}
```

## 检索文件名

从事件上下文中检索文件名。该 [`Email`](examples/Email.java)示例使用以下代码检索文件名：

```java
WatchEvent<Path> ev = (WatchEvent<Path>)event;
Path filename = ev.context();
```

编译`Email`示例时，它会生成以下错误：

```bash
Note: Email.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
```

此错误是转换`WatchEvent<T>`为a 的代码行的结果`WatchEvent<Path>`。该 [`WatchDir`](examples/WatchDir.java)示例通过创建`cast`抑制未检查警告的实用程序方法来避免此错误，如下所示：

```java
@SuppressWarnings("unchecked")
static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<Path>)event;
}
```

如果您不熟悉`@SuppressWarnings`语法，请参阅 [注释](../../java/annotations/index.html)。

## 何时使用和不使用此API

Watch Service API专为需要通知文件更改事件的应用程序而设计。它非常适合任何应用程序，如编辑器或IDE，可能有许多打开的文件，需要确保文件与文件系统同步。它也非常适合于监视目录的应用程序服务器，可能等待`.jsp`或`.jar`丢弃文件，以便部署它们。

此API *不适*用于索引硬盘驱动器。大多数文件系统实现都具有文件更改通知的本机支持。Watch Service API在可用的情况下利用此支持。但是，当文件系统不支持此机制时，Watch Service将轮询文件系统，等待事件。