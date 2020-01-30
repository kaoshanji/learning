# Watch实现

实施监视服务是一项需要完成一组步骤的任务。 

在本节中，您将请参阅开发监视服务以监视给定目录的三个通知的主要步骤事件：删除，创建和修改。 

每个步骤都由一段代码演示，该代码演示了如何实际上完成了该步骤。 

最后，我们将这些块粘合在一起，形成一个完整的功能监视服务示例。

##  创建 WatchService

我们通过创建用于监视文件系统的WatchService开始我们的旅程。 

为此，我们称 FileSystem.newWatchService（）方法：

```Java
WatchService watchService = FileSystems.getDefault().newWatchService();
```

现在，我们可以提供监视服务。

##  注册 WatchService

必须监视的每个对象必须在监视服务中显式注册。 

我们可以注册实现Watchable接口的任何对象。 

对于我们的示例，我们将注册以下目录Path类的实例。 

除了受监视的对象之外，注册过程还需要标识服务应监视和通知的事件。 

支持的事件类型已映射在StandardWatchEventKinds类下作为`Kind <Path>`类型的常量：

-   StandardWatchEventKinds.ENTRY_CREATE

目录条目已创建。 

一个重命名文件或将文件移入该文件时，也会触发ENTRY_CREATE事件目录。

-   StandardWatchEventKinds.ENTRY_DELETE

目录条目被删除。 

一个重命名文件或从中移出文件时也会触发ENTRY_DELETE事件目录。

-   StandardWatchEventKinds.ENTRY_MODIFY

目录条目已修改。 

哪一个事件构成的修改在某种程度上是特定于平台的，但实际上修改文件的内容始终会触发修改事件。 

一些平台，更改文件的属性也会触发此事件。

-   StandardWatchEventKinds.OVERFLOW

表示事件可能已经丢失或丢弃。 

您不必注册OVERFLOW事件就可以接收它。


由于Path类实现了Watchable接口，因此它提供了Watchable.register（）方法。

有两种专门用于向监视服务注册对象的方法。 

之一它们接收两个表示要向其注册该对象的监视服务的参数，并且该对象应注册的事件。 

第二种注册方法接收这两个参数，还有第三个参数，用于指定修饰符，这些修饰符限定目录的方式注册。 

在撰写本文时，NIO.2没有提供任何标准修饰符。

以下代码段向Watch服务注册了路径C：\ rafaelnadal（受监视事件将被创建，删除和修改）：

```Java
import static java.nio.file.StandardWatchEventKinds.*;
…
final Path path = Paths.get("C:/rafaelnadal");
WatchService watchService = FileSystems.getDefault().newWatchService();
…
path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
…
watchService.close();
```

您会为您注册的每个目录收到一个WatchKey实例； 这是代表使用WatchService注册可观察对象。 

是否挂在这里是您的选择参考，因为WatchService在触发事件时会向您返回相关的WatchKey。

下节提供了有关监视键的更多详细信息。

##  等待即将到来的事件

等待传入事件需要无限循环。 

发生事件时，监视服务为负责发信号通知相应的监视键并将其放入监视者的队列，从在哪里可以找到它-我们说监视键已排队。 

因此，我们的无限循环可能是以下类型：

```Java
while(true){
//retrieve and process the incoming events
…
}
// Or it may be of the following type:
for(;;){
//retrieve and process the incoming events
…
}
```

##  获取 Watch Key

可以通过调用以下三种方法之一来获取排队的密钥：WatchService类。 

这三种方法都检索下一个键并将其从队列中删除。 

他们不同如果没有可用的密钥，他们如何响应，如下所示：

-   poll()：如果没有可用的键，则立即返回空值
-   poll(long, TimeUnit)：如果没有可用的密钥，它将等待指定的时间并尝试再次。 如果仍然没有密钥可用，则返回null。 指示时间段作为一个长数字，而TimeUnit参数确定是否指定时间是分钟，秒，毫秒或其他时间单位。
-   take()：如果没有可用的密钥，它将等待直到密钥排队或无限循环结束。由于几种不同的原因而停止

以下三个代码段向您展示了在无限循环内调用的每种方法：

```Java
//poll method, without arguments
while (true) {
//retrieve and remove the next watch key
final WatchKey key = watchService.poll();
//the thread flow gets here immediately with an available key or a null value
…
}

//poll method, with arguments
while (true) {
//retrieve and remove the next watch key
final WatchKey key = watchService.poll(10, TimeUnit.SECONDS);
//the thread flow gets here immediately if a key is available, or after 10 seconds
//with an available key or null value
…
}

//take method
while (true) {
//retrieve and remove the next watch key
final WatchKey key = watchService.take();
//the thread flow gets here immediately if a key is available, or it will wait until a
//key is available, or the loop breaks
…
}
```

请记住，键始终具有状态，该状态可以是就绪状态，有信号状态或无效状态：

-   Ready

首次创建时，密钥处于就绪状态，这意味着它处于准备接受事件。

-   Signaled

当按键处于信号状态时，表示至少有一个事件发生并且密钥已排队，因此可以通过poll（）检索或take（）方法。

类似于钓鱼：关键是浮标，事件是鱼。 当您将鱼钩上时，浮子（钥匙）会提示您拉动鱼钩。

发出信号后，密钥保持在此状态，直到其reset（）调用方法将密钥返回到就绪状态。 

如果在此期间发生其他事件发出了密钥的信号，他们排队而没有重新排队密钥本身（这永远不会钓鱼时发生）。

-   Invalid

当密钥处于无效状态时，这意味着它不再处于活动状态。 

关键保持有效，直到通过显式调用cancel（）方法将其取消为止，目录变得不可访问，或者监视服务关闭。 

你可以测试通过调用WatchKey.isValid（）方法，密钥是否有效，该方法将返回相应的布尔值。

监视键可安全用于多个并发线程。

##  检索密钥的待处理事件

发出钥匙信号时，我们有一个或多个待处理事件等待我们采取行动。 

我们可以通过调用WatchKey.pollEvents（）检索并删除特定监视键的所有未决事件。

它没有参数，并返回一个包含检索到的未决事件的列表。 

我们可以迭代此列表可分别提取和处理每个未决事件。 

列表类型为`WatchEvent <T>`，其中代表已向WatchService注册的对象的事件（或重复事件）：

```Java
public List<WatchEvent<?>> pollEvents()
```

如果没有待处理的事件，则pollEvents（）方法不会等待，这有时可能会导致一个空列表。

以下代码片段迭代了密钥的未决事件：

```Java
while (true) {
    //retrieve and remove the next watch key
    final WatchKey key = watchService.take();
    //get list of pending events for the watch key
    for (WatchEvent<?> watchEvent : key.pollEvents()) {
    …
    }
…
}
```

监视事件是不可变的并且是线程安全的。

##  检索事件类型和计数

`WatchEvent <T>`接口映射事件属性，例如类型和计数。 

事件的类型可以是通过调用`WatchEvent.kind（）`方法获得，该方法将事件类型作为Kind <T>对象返回

如果忽略注册的事件类型，则可能会收到OVERFLOW事件。 

这种事件可以忽略或处理，具体取决于您。

以下代码段将列出pollEvents（）方法提供的每个事件的类型：

```Java
//get list of pending events for the watch key
for (WatchEvent<?> watchEvent : key.pollEvents()) {
    //get the kind of event (create, modify, delete)
    final Kind<?> kind = watchEvent.kind();
    //handle OVERFLOW event
    if (kind == StandardWatchEventKinds.OVERFLOW) {
        continue;
    }
    System.out.println(kind);
}
```

除了事件类型，我们还可以获取观察到事件的次数（重复事件）。 

如果我们调用WatchEvent.count（）方法，该方法返回一个int，则有可能实现：

```Java
System.out.println(watchEvent.count());
```

##  检索与事件相关的文件名

当文件上发生删除，创建或修改事件时，我们可以通过获取事件来查找其名称上下文（文件名存储为事件的上下文）。 

此任务可以通过调用 WatchEvent.context（）方法：

```Java
…
final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
final Path filename = watchEventPath.context();

System.out.println(filename);
```

##  将钥匙放回就绪状态

发出信号后，密钥将保持此状态，直到调用其reset（）方法以将密钥返回给就绪状态。 

然后，它恢复等待事件。 如果监视键有效，则reset（）方法将返回true。并且已被重置，如果由于无法使用而无法重置监视键，则返回false。 

在某些情况下，如果密钥不再有效，则应该打破无限循环； 

例如，如果我们有一个单键，没有理由停留在无限循环中。

以下是如果密钥不再有效时用于中断循环的代码：

```Java
while(true){
    …
    //reset the key
    boolean valid = key.reset();
    //exit loop if the key is not valid (if the directory was deleted, for example)
        if (!valid) {
            break;
    }
}
…
```

如果您忘记或未能调用reset（）方法，该键将不会再接收任何事件！

##  关闭

当线程退出或服务关闭时，监视服务退出。 

它应该被关闭显式调用WatchService.close（）方法，或将创建代码放在try-withresources中块，如下所示：

```Java
try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
…
}
```

当监视服务关闭时，任何当前操作都将被取消并无效。 

看完之后服务已关闭，对其进行任何其他调用操作的尝试都将引发ClosedWatchServiceException。 

如果此监视服务已关闭，则调用此方法没有影响。

##  粘合在一起

在本节中，我们将前面的所有代码块（包括导入和意大利面条代码）粘合在一起，一个监视应用程序创建，删除和修改路径C：\ rafaelnadal和报告事件的类型以及发生事件的文件。 

为了进行测试，请尝试手动添加，删除或修改此路径下的文件或目录。 

请记住，仅监视下一级（仅C：\ rafaelnadal目录），而不是C：\ rafaelnadal目录下的整个目录树。

应用程序代码如下：

```Java
package watch_01;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

class WatchRafaelNadal {

    public void watchRNDir(Path path) throws IOException, InterruptedException {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            //start an infinite loop
            while (true) {
                //retrieve and remove the next watch key
                final WatchKey key = watchService.take();
                //get list of pending events for the watch key
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    //get the kind of event (create, modify, delete)
                    final Kind<?> kind = watchEvent.kind();
                    //handle OVERFLOW event
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    //get the filename for the event
                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                    final Path filename = watchEventPath.context();
                    //print it out
                    System.out.println(kind + " -> " + filename);
                }
                //reset the key
                boolean valid = key.reset();
                //exit loop if the key is not valid (if the directory was deleted, for example)
                if (!valid) {
                    break;
                }
            }
        }
    }
}


public class Main {
    public static void main(String[] args) {
    
        final Path path = Paths.get("C:/rafaelnadal");
        WatchRafaelNadal watch = new WatchRafaelNadal();
        try {
            watch.watchRNDir(path);
        } catch (IOException | InterruptedException ex) {
            System.err.println(ex);
        }
    }
}
```

由于此应用程序包含无限循环，因此请小心手动停止该应用程序，或者实施停止机制。 

该应用程序作为NetBeans项目提供，因此您可以轻松停止在“输出”窗口中没有补充代码。

----