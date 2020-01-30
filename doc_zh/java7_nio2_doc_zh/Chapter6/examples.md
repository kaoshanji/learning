# 另一个使用Watch的示例

在本节中，我们将“玩弄”前面的应用程序，以编码一些旨在探索的场景。

手表服务的可能性。 

我们将基于此构建新的应用程序以完成任务涉及监视服务的更复杂的任务。 

如上一节所述，在每个步骤的描述中，提供了支持该步骤的代码块。 

在步骤中描述之后完整，我们会将所有内容整合到完整的应用程序中。

为了使代码尽可能整洁，我们将跳过变量的声明（名称与上一个应用程序中的名称相同）和应重复的代码。

##  观看目录树

首先，我们将开发一个应用程序，以扩展前面的示例以观看整个C：\ rafaelnadal目录树。 

此外，如果CREATE事件在此位置的某个位置创建了新目录树，它将立即被注册，就好像它从一开始就在那里。

首先，创建监视服务：

```Java
private WatchService watchService = FileSystems.getDefault().newWatchService();
```

接下来，我们需要注册目录树以创建，删除和修改事件。 

这比它在原始应用程序中，因为我们需要注册C：\ rafaelnadal的每个子目录，而不是仅此目录。 

因此，我们需要一个遍历（请参阅第5章）以遍历每个子目录并注册在手表服务中单独显示。 

这种情况非常适合通过扩展SimpleFileVisitor类，因为我们只需要在预定义目录时参与（此外，您可能需要重写visitFileFailed（）方法以显式处理意外的遍历错误）。 

为此，我们将创建一个名为registerTree（）的方法，如下所示：

```Java
private void registerTree(Path start) throws IOException {

    Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            System.out.println("Registering:" + dir);
            registerPath(dir);
            return FileVisitResult.CONTINUE;
        }
    });
}
```

如您所见，这里没有注册。 

对于每个遍历的目录，此代码调用另一个名为registerPath（）的方法，它将向监视服务注册接收的路径，如下所示：

```Java
private void registerPath(Path path) throws IOException {
    //register the received path
    WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
}
```

此时，初始的C：\ rafaelnadal目录和所有子目录已注册用于创建，删除和修改事件。

接下来，我们将集中于将“捕获”这些事件的无限循环。 

当事件发生时，我们对于是否为CREATE事件特别感兴趣，因为它可能表示新的子目录具有已创建，在这种情况下，我们有责任将此子目录添加到监视服务过程中通过调用具有相应路径的registerTree（）方法。 

我们需要在这里解决的问题是我们不知道哪个密钥已经排队，所以我们不知道应该为哪个路径传递注册。 

解决方案可能是将键和相应的路径保留在已更新的HashMap中每次在registerPath（）方法中进行每次注册时，如下所示，之后，当事件发生时，我们可以从哈希图中提取关联的密钥：

```Java
private final Map<WatchKey, Path> directories = new HashMap<>();
…
private void registerPath(Path path) throws IOException {
    //register the received path
    WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
    //store the key and path
    directories.put(key, path);
}
```

现在，在无限循环中，我们可以按以下方式注册任何新的子目录：

```Java
…
while (true) {
…
    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
        final Path directory_path = directories.get(key);
        final Path child = directory_path.resolve(filename);

        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
            registerTree(child);
        }
    }
…
}
…
```

当没有更多有效键可用时，HashMap也可以用于停止无限循环。 

至为此，当密钥无效时，会将其从HashMap中删除，并且当HashMap为空时，循环中断：

```Java
while (true) {
…
    //reset the key
    boolean valid = key.reset();
    //remove the key if it is not valid
    if (!valid) {
        directories.remove(key);
        if (directories.isEmpty()) {
            break;
        }
    }
}
```

而已！ 现在，让我们将所有内容放在一起：

```Java
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

class WatchRecursiveRafaelNadal {
    private WatchService watchService;
    private final Map<WatchKey, Path> directories = new HashMap<>();

    private void registerPath(Path path) throws IOException {
        //register the received path
        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        //store the key and path
        directories.put(key, path);
    }

    private void registerTree(Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            System.out.println("Registering:" + dir);
            registerPath(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void watchRNDir(Path start) throws IOException, InterruptedException {

        watchService = FileSystems.getDefault().newWatchService();
        registerTree(start);
        //start an infinite loop
        while (true) {
            //retrieve and remove the next watch key
            final WatchKey key = watchService.take();
            //get list of events for the watch key
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                //get the kind of event (create, modify, delete)
                final Kind<?> kind = watchEvent.kind();
                //get the filename for the event
                final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                final Path filename = watchEventPath.context();
                //handle OVERFLOW event
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                //handle CREATE event
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    final Path directory_path = directories.get(key);
                    final Path child = directory_path.resolve(filename);
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerTree(child);
                    }
                }
                //print it out
                System.out.println(kind + " -> " + filename);
            }
            //reset the key
            boolean valid = key.reset();
            //remove the key if it is not valid
            if (!valid) {
                directories.remove(key);
                //there are no more keys registered
                if (directories.isEmpty()) {
                    break;
                }
            }
        }
        watchService.close();
    }
}


public class Main {
    public static void main(String[] args) {
        final Path path = Paths.get("C:/rafaelnadal");
        WatchRecursiveRafaelNadal watch = new WatchRecursiveRafaelNadal();

        try {
            watch.watchRNDir(path);
        } catch (IOException | InterruptedException ex) {
            System.err.println(ex);
        }
    }
}

```

为了进行测试，请尝试创建新的子目录和文件，对其进行修改，然后将其删除。

同时，请注意控制台输出，以了解如何报告事件。 

以下是将名为rafa_champ.jpg的新图片添加到C：\ rafaelnadal \ photos的示例输出 目录并在几秒钟后将其删除：

```base
Registering:C:\rafaelnadal

Registering:C:\rafaelnadal\equipment

Registering:C:\rafaelnadal\grandslam

Registering:C:\rafaelnadal\grandslam\AustralianOpen

Registering:C:\rafaelnadal\grandslam\RolandGarros

Registering:C:\rafaelnadal\grandslam\USOpen
…
Registering:C:\rafaelnadal\wiki

ENTRY_CREATE -> rafa_champ.jpg

ENTRY_MODIFY -> rafa_champ.jpg

ENTRY_MODIFY -> photos

ENTRY_MODIFY -> rafa_champ.jpg

ENTRY_DELETE -> rafa_champ.jpg

ENTRY_MODIFY -> photos
```

##  监视摄像机

对于这种情况，假设我们有一个可捕获至少一个图像的监控摄像机每10秒将其以JPG格式发送到计算机目录。 

在幕后，控制器是负责检查相机是否按时以正确的JPG格式发送图像捕获。

如果相机无法正常工作，它将显示警告消息。

借助Watch Service API，可以在代码行中轻松重现此方案。 

我们是对编写观看摄像机的控制器特别感兴趣。 

自摄像机将捕获的数据发送到目录，我们的控制器可以在该目录中监视CREATE事件。 

在此示例中，目录为C：\ security（您应手动创建），并将其映射为路径通过path变量：

```Java
final Path path = Paths.get("C:/security");
…
WatchService watchService = FileSystems.getDefault().newWatchService();
path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
```

接下来，我们知道摄像机每10秒发送一次图像，这意味着poll（long，TimeUnit）方法应该是监视此事件的完美方法（请记住，如果事件发生在指定的时间段内，此方法将退出，并返回相关的WatchKey）。 

我们将其设置为等待恰好11秒，如果此时没有创建新的捕获，那么我们会通过一条消息进行报告并停止系统：

```Java
while (true) {
    final WatchKey key = watchService.poll(11, TimeUnit.SECONDS);
    if (key == null) {
        System.out.println("The video camera is jammed - security watch system is canceled!");
        break;
        } else {
    …
    }
}
…
```

最后，如果有可用的新捕获，那么我们要做的就是检查它是否在JPG中图像格式。 

为此，我们可以使用Files类中的助手方法，名为probeContentType（），探测文件的内容类型。 

我们传递文件，它返回null或内容类型作为哑剧。 

对于JPG图像，此方法应返回image / jpeg。

```Java
OUTERMOST:
while (true) {
    …
    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
        //get the filename for the event
        final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
        final Path filename = watchEventPath.context();
        final Path child = path.resolve(filename);

        if (Files.probeContentType(child).equals("image/jpeg")) {
            //print out the video capture time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            System.out.println("Video capture successfully at: " + dateFormat.format(new Date()));
            } else {
                System.out.println("The video camera capture format failed! This could be a virus!");
                break OUTERMOST;
        }
    }
}
```

我们已经完成了编写控制器的主要任务，所以现在我们要做的就是填写缺少代码（导入，声明，主函数等）可以为我们提供完整的应用程序，如下所示：

```Java
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class SecurityWatch {
    WatchService watchService;
    
    private void register(Path path, Kind<Path> kind) throws IOException {
        //register the directory with the watchService for Kind<Path> event
        path.register(watchService, kind);
    }

    public void watchVideoCamera(Path path) throws IOException, InterruptedException {
        watchService = FileSystems.getDefault().newWatchService();
        register(path, StandardWatchEventKinds.ENTRY_CREATE);
        //start an infinite loop
        OUTERMOST:

        while (true) {
            //retrieve and remove the next watch key
            final WatchKey key = watchService.poll(11, TimeUnit.SECONDS);
            if (key == null) {
                System.out.println("The video camera is jammed - security watch system is canceled!");
                break;
            } else {
                //get list of events for the watch key
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    //get the kind of event (create, modify, delete)
                    final Kind<?> kind = watchEvent.kind();
                    //handle OVERFLOW event
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        //get the filename for the event
                        final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                        final Path filename = watchEventPath.context();
                        final Path child = path.resolve(filename);
                        if (Files.probeContentType(child).equals("image/jpeg")) {
                            //print out the video capture time
                            SimpleDateFormat dateFormat = new
                            SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
                            System.out.println("Video capture successfully at: " +
                            dateFormat.format(new Date()));
                        } else {
                            System.out.println("The video camera capture format failed!
                            This could be a virus!");
                            break OUTERMOST;
                        }
                    }
                }

                //reset the key
                boolean valid = key.reset();
                //exit loop if the key is not valid
                if (!valid) {
                    break;
                }
            }
        }
        watchService.close();
    }
}


public class Main {
    public static void main(String[] args) {

        final Path path = Paths.get("C:/security");
        SecurityWatch watch = new SecurityWatch();

        try {
            watch.watchVideoCamera(path);
        } catch (IOException | InterruptedException ex) {
            System.err.println(ex);
        }
    }
}
```

为了进行测试，您可能需要编写一个测试人员类，或者，更轻松地扮演视频的角色相机。 

只需启动应用程序，然后在关键时间之前将JPG图像复制并粘贴到C：\ security中即可通过。 

请尝试其他情况，例如使用错误的文件格式，然后等待11秒钟以上复制另一个图像，依此类推。

##  监视打印机系统

在本节中，我们将开发一个监视大型打印机托盘的应用程序。 

假设我们有一个多线程基类，该基类接收要打印的文档，并根据旨在优化打印机使用的算法将这些文档分发给一组网络打印机线程在相应文档打印后终止。 

该类实现为如下：

```Java
import java.nio.file.Path;
import java.util.Random;

class Print implements Runnable {
    private Path doc;
    Print(Path doc) {
        this.doc = doc;
    }

    @Override
    public void run() {
        try {
            //sleep a random number of seconds for simulating dispatching and printing
            Thread.sleep(20000 + new Random().nextInt(30000));
            System.out.println("Printing: " + doc);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
}
```

Java 7建议使用新的ThreadLocalRandom类在中生成随机数。

多线程情况。,但是我更喜欢旧的Random类，因为新的类似乎有一个错误。 

它产生在多个线程上使用相同的数字。 

如果您在阅读本书时该错误已得到解决，那么您可能要改用此行：ThreadLocalRandom.current（）。nextInt（20000，50000）;

现在，通过目录（C：\ printertray，您需要手动创建）。 

我们的工作是实施监视服务来管理该托盘。 

什么时候新文档到达进纸匣，我们必须将其传递给Print类，然后已被打印，我们必须将其从纸盘中删除。

我们首先通过经典方法获得手表服务，然后注册C：\ printertray CREATE和DELETE事件的目录：

```Java
final Path path = Paths.get("C:/printertray");
…
WatchService watchService = FileSystems.getDefault().newWatchService();
path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE);
```

接下来，当新文档到达进纸匣时，我们必须创建一个新的打印线程并存储线程和文档路径，以进一步跟踪线程状态。 

这将有助于我们了解何时文档已打印，因此应从纸盘中删除并取出以进行存储（我们使用HashMap）。 

以下代码段包含一个新代码块时执行的代码块文档到达进纸匣（CREATE事件已排队）：

```Java
private final Map<Thread, Path> threads = new HashMap<>();
…
    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
        System.out.println("Sending the document to print -> " + filename);
        Runnable task = new Print(path.resolve(filename));
        Thread worker = new Thread(task);
        //we can set the name of the thread
        worker.setName(path.resolve(filename).toString());
        //store the thread and the path
        threads.put(worker, path.resolve(filename));
        //start the thread, never call method run() direct
        worker.start();
}
```

从纸盘中删除文档后（一个DELETE事件已排队），我们只打印一条消息：

```Java
if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
    System.out.println("Document " + filename + " was successfully printed!");
}
```

但是何时删除文档？ 为了解决此任务，我们使用了一些技巧。 

而不是使用take（）方法用于等待键排队，我们使用poll（long，TimeUnit）方法，我们可以在指定的时间间隔内在无限循环中进行控制-只要我们有控制权（无论是否或没有任何键排队），我们可以循环线程的HashMap来查看是否有任何打印作业已终止（关联的线程状态为TERMINATED）。 

每个TERMINATED状态都将被删除关联的路径并删除HashMap条目。 

删除路径后，将发生DELETE事件排队。 

以下代码显示了如何完成此操作：

```Java
if (!threads.isEmpty()) {
    for (Iterator<Map.Entry<Thread, Path>> it = threads.entrySet().iterator(); it.hasNext();)
        Map.Entry<Thread, Path> entry = it.next();
        if (entry.getKey().getState() == Thread.State.TERMINATED) {
            Files.deleteIfExists(entry.getValue());
            it.remove();
        }
    }
}
```

现在，将所有内容放在一起以获得完整的应用程序：

```Java
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class Print implements Runnable {
    private Path doc;

    Print(Path doc) {
        this.doc = doc;
    }

    @Override
    public void run() {
        try {
            //sleep a random number of seconds for simulating dispatching and printing
            Thread.sleep(20000 + new Random().nextInt(30000));
            System.out.println("Printing: " + doc);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
}

class WatchPrinterTray {
    private final Map<Thread, Path> threads = new HashMap<>();

    public void watchTray(Path path) throws IOException, InterruptedException {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE);
            //start an infinite loop
            while (true) {
                //retrieve and remove the next watch key
                final WatchKey key = watchService.poll(10, TimeUnit.SECONDS);
                //get list of events for the watch key
                if (key != null) {
                    for (WatchEvent<?> watchEvent : key.pollEvents()) {
                        //get the filename for the event
                        final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                        final Path filename = watchEventPath.context();
                        //get the kind of event (create, modify, delete)
                        final Kind<?> kind = watchEvent.kind();
                        //handle OVERFLOW event
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            System.out.println("Sending the document to print ->" + filename);
                            Runnable task = new Print(path.resolve(filename));
                            Thread worker = new Thread(task);
                            //we can set the name of the thread
                            worker.setName(path.resolve(filename).toString());
                            //store the thread and the path
                            threads.put(worker, path.resolve(filename));
                            //start the thread, never call method run() direct
                            worker.start();
                        }

                        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            System.out.println(filename + " was successfully printed!");
                        }
                    }
                    //reset the key
                    boolean valid = key.reset();
                    //exit loop if the key is not valid
                    if (!valid) {
                        threads.clear();
                        break;
                    }
                }

                if (!threads.isEmpty()) {
                    for (Iterator<Map.Entry<Thread, Path>> it = threads.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Thread, Path> entry = it.next();
                        if (entry.getKey().getState() == Thread.State.TERMINATED) {
                            Files.deleteIfExists(entry.getValue());
                            it.remove();
                        }
                    }
                }
            }
        }
    }
}


public class Main {
    public static void main(String[] args) {
        final Path path = Paths.get("C:/printertray");
        WatchPrinterTray watch = new WatchPrinterTray();
        try {
            watch.watchTray(path);
        } catch (IOException | InterruptedException ex) {
            System.err.println(ex);
        }
    }
}

```

为了进行测试，请启动该应用程序并将一组文件复制到C：\ printertray目录中。

例如，以下是一组文件的测试输出：

```base
Sending the document to print -> rafa_1.jpg

Sending the document to print -> AEGON.txt

Sending the document to print -> BNP.txt

Printing: C:\printertray\rafa_1.jpg

Printing: C:\printertray\AEGON.txt

rafa_1.jpg was successfully printed!

AEGON.txt was successfully printed!

Printing: C:\printertray\BNP.txt

Sending the document to print -> rafa_winner.jpg

BNP.txt was successfully printed!

Printing: C:\printertray\rafa_winner.jpg

rafa_winner.jpg was successfully printed
```

----
