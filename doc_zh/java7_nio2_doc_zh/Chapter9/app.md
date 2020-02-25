# 开发异步应用

有太多的示例需要开发，并且要执行的测试也很多，以实现最佳的可扩展性。

异步通道API，则需要一本专门的书籍来涵盖所有详细信息。

由于我们在单个章节中涵盖了该主题，因此我们将直接介绍存根应用程序，其中应该为您提供发展他人的灵感来源。

我们从异步文件通道开始进行这种开发，以进行读取，写入和处理文件。 

您将了解如何基于两个Future对文件执行这些I / O操作。和CompletionHander表单。 

然后，我们将转到面向流的异步通道侦听套接字和面向流的连接套接字的异步通道。

##  异步文件通道示例

涉及异步文件通道的任何应用程序的第一步都是创建一个新的通过调用两个open（）方法之一来为文件提供AsynchronousFileChannel实例。 

最容易使用将会收到要打开或创建的文件的路径，还可以选择一组选项，指定文件的操作方式打开，如下所示。 

此open（）方法会将通道与系统相关的默认值相关联可以与其他通道（默认组）共享的线程池。

```Java
public static AsynchronousFileChannel open(Path file, OpenOption... options) throws IOException
```

前面代码中调用的选项集是之前的StandardOpenOption枚举常量在第4章和第7章中都有介绍，因此您应该已经熟悉这些选项。

### File Read and Future

以下代码段创建了一个新的异步文件通道，用于读取文件story.txt。位于C：\ rafaelnadal \ grandslam \ RolandGaross目录中（该文件必须存在）：

```Java
Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ) ;
```

该文件已准备好阅读，因此我们可以开始阅读。

该任务由read（）完成方法（其中有两种）。 

由于我们对使用未来模式感兴趣，因此我们将使用以下read（）方法：

```Java
public abstract Future<Integer> read(ByteBuffer dst, long position)
```

此方法从给定的缓冲区开始，从该通道读取字节序列到给定的缓冲区文件位置，并返回代表待处理结果的对象。 

由于我们处于异步状态在环境中，此方法仅启动读取操作，不会阻止应用程序。

以下代码向您展示如何使用它来读取前100个字节：

```Java
ByteBuffer buffer = ByteBuffer.allocate(100);
Future<Integer> result = asynchronousFileChannel.read(buffer, 0);
```

待处理的结果使我们可以通过Future.isDone（）跟踪读取过程的状态。

在读取操作完成之前，该方法将返回false。 

将此呼叫置于循环中可以使我们完成其他任务，直到读取完成：

```Java
while (!result.isDone()) {
    System.out.println("Do something else while reading ...");
}
```

读取操作完成后，应用程序流程退出循环，结果可能是通过调用get（）方法进行检索，该方法在必要时等待操作完成。 

结果是一个整数，代表读取的字节数，而这些字节位于目标缓冲区中：

```Java
System.out.println("Read done: " + result.isDone());
System.out.println("Bytes read: " + result.get());
```

将所有内容粘合在一起将产生以下应用程序：

```Java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        String encoding = System.getProperty("file.encoding");
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");

        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,StandardOpenOption.READ)) {
            Future<Integer> result = asynchronousFileChannel.read(buffer, 0);
           
            while (!result.isDone()) {
                System.out.println("Do something else while reading ...");
            }

            System.out.println("Read done: " + result.isDone());
            System.out.println("Bytes read: " + result.get());
        } catch (Exception ex) {
            System.err.println(ex);
        }
        buffer.flip();
        System.out.print(Charset.forName(encoding).decode(buffer));
        buffer.clear();
    }
}
```

以下是此应用程序的可能输出：

```base
Do something else while reading ...

Do something else while reading ...

Do something else while reading ...

Do something else while reading ...

Read done: true

Bytes read: 100

Rafa Nadal produced another masterclass of clay-court tennis to win his fifth French Open title ...
```

### File Write and Future

以下代码段创建了一个新的异步文件通道，用于将更多字节写入文件位于C：\ rafaelnadal \ grandslam \ RolandGaross中的story.txt（文件必须存在）：

```Java
Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE) ;
```

该文件已准备好写入，因此我们可以开始写入了。 

该任务由write（）完成方法（其中有两种）。 

由于我们对使用“未来”模式很感兴趣，因此我们将使用以下write（）方法：

```Java
public abstract Future<Integer> write(ByteBuffer src, long position)
```

从给定的缓冲区开始，此方法从给定的缓冲区向该通道写入字节序列文件位置，并返回代表待处理结果的对象。 

由于我们处于异步状态在环境中，此方法仅启动写入操作，而不会阻止应用程序。 

以下代码向您展示如何使用它从位置100开始写入一些字节：

```Java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.wrap("The win keeps Nadal at the top of the heap in men's
        tennis, at least for a few more weeks. The world No2, Novak Djokovic, dumped out here in the
        semi-finals by a resurgent Federer, will come hard at them again at Wimbledon but there is
        much to come from two rivals who, for seven years, have held all pretenders at
        bay.".getBytes());
        
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");

        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,StandardOpenOption.WRITE)) {
            Future<Integer> result = asynchronousFileChannel.write(buffer, 100);
            while (!result.isDone()) {
                System.out.println("Do something else while writing ...");
            }
                System.out.println("Written done: " + result.isDone());
                System.out.println("Bytes written: " + result.get());
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
```

这次，get（）方法返回写入的字节数。 

字节从在文件中的位置100。 应用程序输出如下：

```base
Do something else while writing ...

Do something else while writing ...

Do something else while writing ...

Written done: true

Bytes written: 319
```

作为练习，请尝试将两个应用程序合并为一个应用程序以进行读写异步地。

### File Read and Future Timeout

如前所述，get（）方法在必要时等待操作完成，然后再等待检索结果。 

此方法还有一个超时版本，在其中可以精确指定多长时间我们负担得起。 

为此，我们将超时和单位时间传递给get（）方法。 

如果时间到了这个方法会抛出一个TimeoutException异常，我们可以通过调用参数为true的cancel（）方法。 

以下应用程序使用以下命令读取story.txt的内容：超时时间很短：

```Java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(100);
        int bytesRead = 0;
        Future<Integer> result = null;
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");

        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,StandardOpenOption.READ)) {
            result = asynchronousFileChannel.read(buffer, 0);
            bytesRead = result.get(1, TimeUnit.NANOSECONDS);
            if (result.isDone()) {
                System.out.println("The result is available!");
                System.out.println("Read bytes: " + bytesRead);
            }
        } catch (Exception ex) {
            if (ex instanceof TimeoutException) {
                if (result != null) {
                    result.cancel(true);
                }

                System.out.println("The result is not available!");
                System.out.println("The read task was cancelled ? " + result.isCancelled());
                System.out.println("Read bytes: " + bytesRead);

            } else {
                System.err.println(ex);
            }
        }
    }
}
```

该应用程序有两个可能的输出。 

首先，如果时间到期并且I / O操作没有完成后，输出将如下所示：

```base
The result is not available!

The read task was cancelled ? true //(or, false)

Read bytes: 0
```

如果I / O操作在时间到期之前完成，则输出如下：

```base
The result is available!

Read bytes: 100
```

### File Read and CompletionHandler

现在，您已经看到了一些有关Future表单如何工作的示例，现在该看看如何可以编写CompletionHandler来读取story.txt内容。 

创建异步文件后读取story.txt文件内容的渠道，我们调用了第二个read（）方法AsynchronousFileChannnel类：

```Java
public abstract <A> void read(ByteBuffer dst, long position, A attachment,CompletionHandler<Integer,? super A> handler)
```

此方法从给定的缓冲区开始，从该通道读取字节序列到给定的缓冲区文件位置。 

除了目标缓冲区和文件位置，此方法还可以获取要附加到的对象I / O操作（可以为null）和完成处理程序以使用结果。

因为我们在在异步环境中，此方法仅启动读取操作，不会阻塞应用程序。

以下代码显示了如何使用它读取前100个字节-您可以找到CompletionHandler作为匿名内部类：

```Java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {
    static Thread current;

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");

        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,StandardOpenOption.READ)) {
            current = Thread.currentThread();
            asynchronousFileChannel.read(buffer, 0, "Read operation status ...", new CompletionHandler<Integer, Object>() {

            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println(attachment);
                System.out.print("Read bytes: " + result);
                current.interrupt();
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println(attachment);
                System.out.println("Error:" + exc);
                current.interrupt();
                }
            });

            System.out.println("\nWaiting for reading operation to end ...\n");
            try {
                current.join();
            } catch (InterruptedException e) {
                }
            //now the buffer contains the read bytes
            System.out.println("\n\nClose everything and leave! Bye, bye ...");
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
```

当前线程仅用于发现何时应停止应用程序。 

在某些情况下，该流程可能在完成处理程序使用结果之前结束应用程序。 

你可以选择而是使用Thread.sleep（）方法，System.in.read（）方法或任何其他方便的方法。

可能的输出如下：

```base
Waiting for reading operation to end ...

Read operation status ...

Read bytes: 100

Closing everything and leave! Bye, bye ...
```

在其他情况下，您可能会在CompletionHandler输出之后看到等待消息，具体取决于它消耗I / O操作结果的速度。

目标ByteBuffer可能会作为附加到对象的对象“到达” CompletionHandler。

I / O操作（当您没有任何附件时，只需传递null）。 

以下应用解码并显示目标ByteBuffer的内容，并将其显示为CompletionHandler：

```Java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {
    
    static Thread current;
    static final Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");

    public static void main(String[] args) {

        CompletionHandler<Integer, ByteBuffer> handler = new CompletionHandler<Integer, ByteBuffer>() {
            String encoding = System.getProperty("file.encoding");

            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                System.out.println("Read bytes: " + result);
                attachment.flip();
                System.out.print(Charset.forName(encoding).decode(attachment));
                attachment.clear();
                current.interrupt();
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println(attachment);
                System.out.println("Error:" + exc);
                current.interrupt();
            }

        };
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,StandardOpenOption.READ)) {
            
            current = Thread.currentThread();
            ByteBuffer buffer = ByteBuffer.allocate(100);
            asynchronousFileChannel.read(buffer, 0, buffer, handler);
            System.out.println("Waiting for reading operation to end ...\n");

            try {
                current.join();
            } catch (InterruptedException e) {
            }
            //the buffer was passed as attachment
            System.out.println("\n\nClosing everything and leave! Bye, bye ...");
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
```

可能的输出如下：

```base
Waiting for reading operation to end ...

Read bytes: 100

Rafa Nadal produced another masterclass of clay-court tennis to win his fifth French Open title ...
```

### File Lock

有时，您需要在执行其他I / O之前获得频道文件的排他锁操作，例如阅读或写作。 

AsynchronousFileChannel为Future提供了一个lock（）方法形式和CompletionHandler的lock（）方法（都具有用于锁定文件区域的签名，您可以在以下官方文档中找到更多详细信息：http://download.oracle.com/javase/7/docs/api/）：

```Java
public final Future<FileLock> lock()
public final <A> void lock(A attachment, CompletionHandler<FileLock,? super A> handler)
```

以下应用程序使用带有Future表单的lock（）方法来锁定文件。 

我们会等待通过调用Future.get（）方法获取锁，然后，我们将写入一些字节到我们的文件中。 

同样，我们调用get（）方法，该方法将等到写入新字节后，最后，释放锁。 

使用的文件是CopaClaro.txt，位于C：\ rafaelnadal \ tournaments \ 2009（该文件必须存在）。

```Java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        
        ByteBuffer buffer = ByteBuffer.wrap("Argentines At Home In Buenos Aires Cathedral\n The
        Copa Claro is the third stop of the four-tournament Latin American swing, and is contested on
        clay at the Buenos Aires Lawn Tennis Club, known as the Cathedral of Argentinean tennis. An
        Argentine has reached the final in nine of the 11 editions of the ATP World Tour 250
        tournament, with champions including Guillermo Coria, Gaston Gaudio, Juan Monaco and David
        Nalbandian.".getBytes());

        Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "CopaClaro.txt");
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,StandardOpenOption.WRITE)) {
            
            Future<FileLock> featureLock = asynchronousFileChannel.lock();
            System.out.println("Waiting for the file to be locked ...");
            FileLock lock = featureLock.get();
            //or, use shortcut
            //FileLock lock = asynchronousFileChannel.lock().get();
            if (lock.isValid()) {
                Future<Integer> featureWrite = asynchronousFileChannel.write(buffer, 0);
                System.out.println("Waiting for the bytes to be written ...");
                int written = featureWrite.get();
                //or, use shortcut
                //int written = asynchronousFileChannel.write(buffer,0).get();
                System.out.println("I’ve written " + written + " bytes into " +
                path.getFileName() + " locked file!");
                lock.release();
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
```

可能的输出如下：

```base
Waiting for the file to be locked ...

Waiting for the bytes to be written ...

I’ve written 423 bytes into CopaClaro.txt locked file!
```

此外，使用CompletionHandler实现lock（）方法的过程可能类似于以下：

```Java
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {
    static Thread current;

    public static void main(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "CopaClaro.txt");

        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            current = Thread.currentThread();
            asynchronousFileChannel.lock("Lock operation status:", new CompletionHandler<FileLock, Object>() {
                @Override
                public void completed(FileLock result, Object attachment) {
                    System.out.println(attachment + " " + result.isValid());
                    if (result.isValid()) {
                        //... processing ...
                        System.out.println("Processing the locked file ...");
                        //...
                        try {
                            result.release();
                        } catch (IOException ex) {
                            System.err.println(ex);
                        }
                    }
                    current.interrupt();
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println(attachment);
                    System.out.println("Error:" + exc);
                    current.interrupt();
                }
            });

            System.out.println("Waiting for file to be locked and process ... \n");
            try {
                current.join();
            } catch (InterruptedException e) {
            }
            System.out.println("\n\nClosing everything and leave! Bye, bye ...");
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
```

以下是可能的输出：

```base
Waiting for file to be locked and process ...

Lock operation status: true

Processing the locked file ...
```

AsynchronousFileChannel还提供了著名的tryLock（）方法，但它们没有与Future或CompletionHandler表单相关联。

### AsynchronousFileChannel and ExecutorService


到目前为止，您仅在工作中看到了第一个AsynchronousFileChannel.open（）方法，该方法使用默认池线程。 

现在该看看第二个open（）方法在起作用了，它使我们可以指定一个通过ExecutorService对象的自定义线程池。 

此方法的语法如下：

```Java
public static AsynchronousFileChannel open(Path file, Set<? extends OpenOption> options,ExecutorService executor, FileAttribute<?>... attrs) throws IOException
```

如您所见，此open（）方法获取要打开或创建的文件的路径，以及一组选项指定如何打开文件（可选），以及作为ExecutorService的线程池（或null）（请参见上面的“ ExecutorService API简介”，以及在创建时自动设置的文件属性列表文件（可选）。

在我们的场景中，我们想开发一个应用程序，该应用程序异步填充50个ByteBufferstory.txt文件中随机位置的字节数。 

字节缓冲区的容量也将是随机的。

此外，我们要使用具有五个线程的固定线程池的自定义组。

我们首先通过ExecutorService创建线程池：

```Java
final int THREADS = 5;
ExecutorService taskExecutor = Executors.newFixedThreadPool(THREADS);
```

我们继续将线程池传递到文件路径和选项旁边的open（）方法：

```Java
private static Set withOptions() {
    final Set options = new TreeSet<>();
    options.add(StandardOpenOption.READ);
    return options;
}
AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, withOptions(), taskExecutor);
```

接下来，在一个循环中，我们创建50个Callable worker（返回值的任务）并覆盖call（）创建随机容量字节缓冲区并用来自随机位置的字节填充它们的方法在文件中-这是我们的计算。 

我们将每个“工人”提交给执行者，并将其“未来”存储到ArrayList。 

稍后，我们将循环此列表并调用get（）方法以从每个字节中检索结果缓冲。

```Java
List<Future<ByteBuffer>> list = new ArrayList<>();
…
for (int i = 0; i < 50; i++) {

    Callable<ByteBuffer> worker = new Callable<ByteBuffer>() {

        @Override
        public ByteBuffer call() throws Exception {
            ByteBuffer buffer=ByteBuffer.allocateDirect(ThreadLocalRandom.current().nextInt(100, 200));
            asynchronousFileChannel.read(buffer, ThreadLocalRandom.current().nextInt(0, 100));
            return buffer;
        }
    };
    Future<ByteBuffer> future = taskExecutor.submit(worker);
    list.add(future);
}
```

由于我们将所有必要的任务交给了执行者，因此我们可以将其关闭，以使其不接受新任务。 

它完成了队列中所有现有线程的处理并终止了–同时，我们可以计算一些羊：

```Java
taskExecutor.shutdown();

while (!taskExecutor.isTerminated()) {
    //do something else while the buffers are prepared
    System.out.println("Counting sheep while filling up some buffers! So far I counted: " + (sheeps += 1));
}
```

数完一段时间后，isTerminate（）方法返回true，结果只是“从烤箱中取出。”遍历Future列表并调用get（）方法以检索每个结果：

```Java
for (Future<ByteBuffer> future : list) {
    ByteBuffer buffer = future.get();
    …
}
```

做完了！ 将所有内容粘合在一起并添加样板代码和导入将产生以下内容：

```Java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    private static Set withOptions() {
        final Set options = new TreeSet<>();
        options.add(StandardOpenOption.READ);
        return options;
    }

    public static void main(String[] args) {
        final int THREADS = 5;
        ExecutorService taskExecutor = Executors.newFixedThreadPool(THREADS);
        String encoding = System.getProperty("file.encoding");
        List<Future<ByteBuffer>> list = new ArrayList<>();
        int sheeps = 0;
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");

        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,withOptions(), taskExecutor)) {

            for (int i = 0; i < 50; i++) {
                Callable<ByteBuffer> worker = new Callable<ByteBuffer>() {

                    @Override
                    public ByteBuffer call() throws Exception {

                        ByteBuffer buffer = ByteBuffer.allocateDirect(ThreadLocalRandom.current().nextInt(100, 200));
                        asynchronousFileChannel.read(buffer, ThreadLocalRandom.current().nextInt(0,100));
                        return buffer;
                    }
                };
                Future<ByteBuffer> future = taskExecutor.submit(worker);
                list.add(future);
            }
            //this will make the executor accept no new threads
            // and finish all existing threads in the queue
            taskExecutor.shutdown();
            //wait until all threads are finished
            while (!taskExecutor.isTerminated()) {
                //do something else while the buffers are prepared
                System.out.println("Counting sheep while filling up some buffers! So far I counted: " + (sheeps += 1));
            }

            System.out.println("\nDone! Here are the buffers:\n");
            for (Future<ByteBuffer> future : list) {
                ByteBuffer buffer = future.get();
                System.out.println("\n\n"+ buffer);
                System.out.println("______________________________________________________");
                buffer.flip();
                System.out.print(Charset.forName(encoding).decode(buffer));
                buffer.clear();
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

```

以下是可能的输出片段：

```base
Counting sheep while filling up some buffers! So far I counted: 352

Counting sheep while filling up some buffers! So far I counted: 353

Counting sheep while filling up some buffers! So far I counted: 354

Done! Here are the buffers:

java.nio.HeapByteBuffer[pos=100 lim=100 cap=100]
______________________________________________________

d another masterclass of clay-court tennis to win his fifth French Open title ...

java.nio.HeapByteBuffer[pos=189 lim=189 cap=189]

______________________________________________________

nother masterclass of clay-court tennis to win his fifth French Open title ...

java.nio.HeapByteBuffer[pos=112 lim=112 cap=112]
______________________________________________________
y-court tennis to win his fifth French Open title ...
```

##  异步 Sockets 通道

异步通道套接字是NIO.2的瑰宝。 

开发异步客户端/服务器对于任何专注于网络的Java开发人员而言，该应用程序都是一个有趣的项目应用领域。 

更好地了解如何完成此任务的最简单方法是遵循一系列简单的步骤，并伴随将在讨论结束。 

我们将从基于Future表单的异步服务器开始。

### Writing an Asynchronous Server (Based on Future)

我们想开发一个异步服务器，它将从客户端获得的一切回显到客户端。

在执行期间，未来模式将负责跟踪任务状态，例如接受连接，从客户端读取字节并将字节写入客户端。

-   创建 AsynchronousServerSocketChannel

第一步涉及为面向流的侦听套接字创建一个异步通道，即使用java.nio.channels.AsynchronousServerSocketChannel完成。 

更确切地说，这个任务是如下面所示，由AsynchronousServerSocketChannel.open（）方法完成，其中异步服务器套接字通道绑定到默认组：

```Java
AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
```

请记住，新创建的异步服务器套接字通道未绑定到本地地址。 

这将在以下步骤中完成。

您可以通过以下方法检查异步服务器套接字是否已打开或已成功打开：调用AsynchronousServerSocketChannel.isOpen（）方法，该方法返回相应的布尔值：

```Java
if (asynchronousServerSocketChannel.isOpen()) {
…
}
```

-   设置 AsynchronousServerSocketChannel 选项

这是一个可选步骤。 

没有必需的选项（您可以使用默认值），但是我们将明确设置一些选项以向您展示如何完成此操作。 

更确切地说，异步服务器套接字通道支持两个选项：SO_RCVBUF和SO_REUSEADDR。 

我们将分别设置它们，如下所示：

```Java
asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
```

您可以通过调用来找出异步服务器套接字通道支持哪些选项继承的方法supportedOptions（）：

```Java
Set<SocketOption<?>> options = asynchronousServerSocketChannel.supportedOptions();
for(SocketOption<?> option : options) System.out.println(option);
```

-   绑定 AsynchronousServerSocketChannel

此时，我们可以将异步服务器套接字通道绑定到本地地址，并配置套接字以监听连接。 

为此，我们调用AsynchronousServerSocketChannel.bind（）方法。

我们的服务器将等待本地主机（127.0.0.1）的端口5555（任意选择）上的传入连接：

```Java
final int DEFAULT_PORT = 5555;
final String IP = "127.0.0.1";
asynchronousServerSocketChannel.bind(new InetSocketAddress(IP, DEFAULT_PORT));
```

另一种常见的方法是在不指定IP的情况下创建InetSocketAddress对象地址，只有端口（有一个构造函数）。 

在这种情况下，IP地址是通配符地址，端口号是指定值。 

通配符地址是一个特殊的本地IP地址，可以是仅用于绑定操作。

```Java
asynchronousServerSocketChannel.bind(new InetSocketAddress(DEFAULT_PORT));
```

此外，除了绑定套接字的地址之外，还有一个bind（）方法可以获取到，最大挂起连接数：

```Java
public abstract AsynchronousServerSocketChannel bind(SocketAddress local,int pc) throws IOException
```

如果我们将null传递给bind（）方法，则也可以自动分配本地地址。 

您可以还可以通过调用以下命令找出绑定的本地地址 AsynchronousServerSocketChannel.getLocalAddress（）方法，该方法继承自NetworkChannel接口。 如

果异步服务器套接字通道尚未建立，则返回null绑定了。

```Java
System.out.println(asynchronousServerSocketChannel.getLocalAddress());
```

-   接入连接

打开并绑定后，我们终于达到了接受的里程碑。 

我们表示我们急于接受通过调用AsynchronousServerSocketChannel.accept（）方法发起新的连接，异步操作，以接受与此通道的套接字建立的连接并返回Future对象以跟踪操作状态。 

我们调用Future.get（）方法，该方法返回新连接成功完成。 

另外，您可能需要使用isDone（）方法定期检查操作完成状态。 

返回的连接是AsynchronousSocketChannel类的实例，该类表示面向流的异步通道连接插座。

```Java
Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = asynchronousServerSocketChannel.accept();
AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get();
```

尝试为未绑定的服务器套接字通道调用accept（）方法将抛出一个NotYetBoundException异常。

接受新的连接后，我们可以通过调用 AsynchronousSocketChannel.getRemoteAddress（）方法：

```Java
System.out.println("Incoming connection from: " + asynchronousSocketChannel.getRemoteAddress());
```

-   通过连接传输数据

此时，服务器和客户端可以通过连接传输数据。 

他们可以发送和接收映射为字节数组的各种数据包。 

实现传输（发送/接收）是一个灵活而具体的过程，因为它涉及许多选项。 

例如，对于我们的服务器，我们将使用 ByteBuffers，请记住，这是一个回显服务器-它从客户端读取的内容是它编写的内容背部。 

这是发送代码段：

```Java
final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
…
while (asynchronousSocketChannel.read(buffer).get() != -1) {
    buffer.flip();
    asynchronousSocketChannel.write(buffer).get();

    if (buffer.hasRemaining()) {
        buffer.compact();
    } else {
        buffer.clear();
    }
}
```

前面的read（）和write（）方法获取目标/源ByteBuffer，启动一个读/写操作，并返回 `Future <Integer>` 对象以跟踪读/写操作状态。

调用get（）方法将强制应用程序等待操作完成，然后返回读/写字节数。

首先，我们等待读取传入的字节（这就是服务器回声）。

其次，我们要等到写操作结束后才能避免出现更多字节回显，并且线程在先前的写操作完成之前启动新的写操作，以WritePendingException异常结束。

由于应用程序被“捕获”在内部与第一个客户端进行读/写操作之前，它不准备接受其他连接，直到有完全为当前客户提供服务，这意味着一次只能为一个客户提供服务。

这是非常基本的，对于服务器来说显然不令人满意，但是对于我们的第一个服务器是可以接受的异步服务器。

-   关闭通道

当通道变得无用时，必须将其关闭。 

为此，您可以调用 AsynchronousSocketChannel.close（）方法（这不会关闭服务器以侦听传入的消息连接，它将仅关闭客户端的渠道）和/或AsynchronousServerSocketChannel.close（）方法（这将关闭服务器以侦听传入的信息连接； 随后的客户将无法再找到服务器）。

```Java
asynchronousServerSocketChannel.close();
asynchronousSocketChannel.close();
```

另外，我们可以通过将代码放入Java 7 try-with-resources中来关闭这些资源。

这是可能的，因为AsynchronousServerSocketChannel和AsynchronousSocketChannel类实现AutoCloseable接口。 

使用此功能将确保资源自动关闭。

-   结合一切 Echo Server

现在，我们拥有创建回显服务器所需的一切。 

将前面的大块放在一起代码并添加必要的导入，意大利面条代码等，将产生以下回显服务器

```Java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {

        final int DEFAULT_PORT = 5555;
        final String IP = "127.0.0.1";

        //create an asynchronous server socket channel bound to the default group
        try (AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()) {
            if (asynchronousServerSocketChannel.isOpen()) {
                //set some options
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                //bind the asynchronous server socket channel to local address
                asynchronousServerSocketChannel.bind(new InetSocketAddress(IP, DEFAULT_PORT));
                //display a waiting message while ... waiting clients
                System.out.println("Waiting for connections ...");

                while (true) {
                    Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = asynchronousServerSocketChannel.accept();
                    try (AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get()) {

                        System.out.println("Incoming connection from: " + asynchronousSocketChannel.getRemoteAddress());
                        final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

                        //transmitting data
                        while (asynchronousSocketChannel.read(buffer).get() != -1) {

                            buffer.flip();
                            asynchronousSocketChannel.write(buffer).get();

                            if (buffer.hasRemaining()) {
                                buffer.compact();
                            } else {
                                buffer.clear();
                            }
                        }
                        
                        System.out.println(asynchronousSocketChannel.getRemoteAddress() + " was successfully served!");
                    } catch (IOException | InterruptedException | ExecutionException ex) {
                        System.err.println(ex);
                    }
                }

            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
```

您可能仍然想知道如何接受多个客户。 

一个简单的解决方案是包装前面的代码放入ExecutorService。 

每次接受新连接并使用get（）方法以AsynchronousSocketChannel通道的形式返回它，我们编写了一个“ worker”来维护或关闭与客户进行“对话”。 

之后，工人被提交给执行者并建立新连接准备接受。 

如果发生意外错误，那么我们将关闭执行器并等待终止。 以下应用程序修改了前面的应用程序，以便它在同时：

```Java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        final int DEFAULT_PORT = 5555;
        final String IP = "127.0.0.1";

        ExecutorService taskExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
        //create asynchronous server socket channel bound to the default group
        try (AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()) {

            if (asynchronousServerSocketChannel.isOpen()) {
                //set some options
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                //bind the server socket channel to local address
                asynchronousServerSocketChannel.bind(new InetSocketAddress(IP, DEFAULT_PORT));
                //display a waiting message while ... waiting clients
                System.out.println("Waiting for connections ...");

                while (true) {
                    Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = asynchronousServerSocketChannel.accept();

                    try {
                        final AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get();

                        Callable<String> worker = new Callable<String>() {

                            @Override
                            public String call() throws Exception {

                                String host = asynchronousSocketChannel.getRemoteAddress().toString();
                                System.out.println("Incoming connection from: " + host);
                                
                                final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                                //transmitting data
                                while (asynchronousSocketChannel.read(buffer).get() != -1) {
                                    buffer.flip();
                                    asynchronousSocketChannel.write(buffer).get();

                                    if (buffer.hasRemaining()) {
                                        buffer.compact();
                                    } else {
                                        buffer.clear();
                                    }
                                }

                                asynchronousSocketChannel.close();
                                System.out.println(host + " was successfully served!");
                                return host;
                            }
                        };

                        taskExecutor.submit(worker);
                    } catch (InterruptedException | ExecutionException ex) {
                        System.err.println(ex);
                        System.err.println("\n Server is shutting down ...");
                        //this will make the executor accept no new threads
                        // and finish all existing threads in the queue
                        taskExecutor.shutdown();
                        //wait until all threads are finished
                        while (!taskExecutor.isTerminated()) {}
                        break;
                    }
                }
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
```

### Writing an Asynchronous Client (Based on Future)

现在，我们为回显服务器开发一个客户端。 

假设我们有以下情形：客户端连接到我们的服务器，发送“ Hello！”消息，然后继续发送0之间的随机数和100，直到生成数字50。 

生成数字50时，客户端停止发送并关闭频道。 

服务器将回显（写回）从客户端读取的所有内容。 的步骤接下来讨论为这种情况实现客户端。

-   创建 AsynchronousSocketChannel

第一步是为绑定到的流导向连接套接字创建一个异步通道默认组。
 
这是通过java.nio.channels.AsynchronousSocketChannel类完成的。

确切地说，此任务是通过AsynchronousSocketChannel.open（）方法完成的，如下所示：

```Java
AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open();
```

请记住，未连接新创建的异步套接字通道。 

你可以检查一下异步服务器套接字已打开或已通过调用以下命令成功打开 AsynchronousSocketChannel.isOpen（）方法，该方法返回相应的布尔值：

```Java
if (asynchronousSocketChannel.isOpen()) {
…
}
```

-   设置 AsynchronousSocketChannel 选项

异步套接字通道支持以下选项：SO_RCVBUF，SO_REUSEADDR，TCP_NODELAY，SO_KEEPALIVE和SO_SNDBUF。 其中一些显示在这里：

```Java
asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
```

您可以通过调用以下命令来发现异步服务器套接字通道的受支持选项：继承的方法supportedOptions（）：

```Java
Set<SocketOption<?>> options = asynchronousSocketChannel.supportedOptions();
for(SocketOption<?> option : options) System.out.println(option);
```

-   连接异步通道 Socket

打开异步套接字通道（并可选地绑定）后，您应该连接到远程地址（服务器端地址）。 

意图连接是通过调用AsynchronousSocketChannel.connect（）方法，并将远程地址作为其实例传递给它InetSocketAddress，如下所示（请记住，我们的回显服务器在127.0.0.1的端口5555上运行）：

```Java
final int DEFAULT_PORT = 5555;
final String IP = "127.0.0.1";
Void connect = asynchronousSocketChannel.connect(new InetSocketAddress(IP, DEFAULT_PORT)).get();
```

此方法启动一个操作以连接到该通道。 

该方法返回`Future <Void>`表示待决结果的对象。 

Future的get（）方法成功返回null完成。

-   通过连接传输数据

连接已建立，因此我们可以开始传输数据包。 

以下代码发送“ Hello！”消息，然后发送随机数，直到生成数字50。 

以下read（）和write（）方法获取目标/源ByteBuffer，启动读取/写入操作，然后返回`Future <Integer>`对象以跟踪读/写操作状态。 

呼叫get（）方法将等待操作完成，并返回读/写的字节数。

将get（）方法与write（）方法一起使用将避免出现更多字节需要写入，并且线程会在先前的写入操作完成之前发起新的写入操作，以WritePendingException异常结束。

```Java
ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
ByteBuffer helloBuffer = ByteBuffer.wrap("Hello !".getBytes());
ByteBuffer randomBuffer;
CharBuffer charBuffer;
Charset charset = Charset.defaultCharset();
CharsetDecoder decoder = charset.newDecoder();
…
asynchronousSocketChannel.write(helloBuffer).get();

while (asynchronousSocketChannel.read(buffer).get() != -1) {
    buffer.flip();
    charBuffer = decoder.decode(buffer);
    System.out.println(charBuffer.toString());

    if (buffer.hasRemaining()) {
        buffer.compact();
    } else {
        buffer.clear();
    }

    int r = new Random().nextInt(100);
    if (r == 50) {
        System.out.println("50 was generated! Close the asynchronous socket channel!");
        break;
    } else {
        randomBuffer = ByteBuffer.wrap("Random number:".concat(String.valueOf(r)).getBytes());
        asynchronousSocketChannel.write(randomBuffer).get();
    }
}
```

-   关闭 通道

当通道变得无用时，必须将其关闭。 

为此，您可以调用 AsynchronousSocketChannel.close（），客户端将与服务器断开连接：

```Java
asynchronousSocketChannel.close();
```

同样，Java 7 try-with-resources功能可用于自动关闭。

-   综合上述 Client

现在，我们拥有创建客户所需的一切。 

将前面的代码块和添加必要的导入，意大利面条代码等将为我们提供以下客户端：

```Java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {

        final int DEFAULT_PORT = 5555;
        final String IP = "127.0.0.1";
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        ByteBuffer helloBuffer = ByteBuffer.wrap("Hello !".getBytes());
        ByteBuffer randomBuffer;
        CharBuffer charBuffer;
        Charset charset = Charset.defaultCharset();
        CharsetDecoder decoder = charset.newDecoder();
        
        //create an asynchronous socket channel bound to the default group
        try (AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open()) {
            if (asynchronousSocketChannel.isOpen()) {
                //set some options
                asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
                asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
                asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                //connect this channel's socket
                Void connect = asynchronousSocketChannel.connect(new InetSocketAddress(IP, DEFAULT_PORT)).get();

                if (connect == null) {
                    System.out.println("Local address: " + asynchronousSocketChannel.getLocalAddress());
                    //transmitting data
                    asynchronousSocketChannel.write(helloBuffer).get();
                    while (asynchronousSocketChannel.read(buffer).get() != -1) {
                        buffer.flip();
                        charBuffer = decoder.decode(buffer);
                        System.out.println(charBuffer.toString());

                        if (buffer.hasRemaining()) {
                            buffer.compact();
                        } else {
                            buffer.clear();
                        }

                        int r = new Random().nextInt(100);
                        if (r == 50) {
                            System.out.println("50 was generated! Close the asynchronous socket channel!");
                            break;
                        } else {
                            randomBuffer = ByteBuffer.wrap("Random number:".concat(String.valueOf(r)).getBytes());
                            asynchronousSocketChannel.write(randomBuffer).get();
                        }
                    }
                } else {
                    System.out.println("The connection cannot be established!");
                }
            } else {
                System.out.println("The asynchronous socket channel cannot be opened!");
            }
        } catch (IOException | InterruptedException | ExecutionException ex) {
            System.err.println(ex);
        }
    }
}
```

### Testing the Echo Application (Based on Future)

测试应用程序是一个简单的任务。 

首先，启动服务器，等待直到看到消息“等待连接...”。 

通过启动客户端继续并检出输出。 

以下是可能的服务输出：

```base
Waiting for connections ...

Incoming connection from: /127.0.0.1:49578

Incoming connection from: /127.0.0.1:49579

Incoming connection from: /127.0.0.1:49580

/127.0.0.1:49579 was successfully served!

Incoming connection from: /127.0.0.1:49581

/127.0.0.1:49580 was successfully served!

/127.0.0.1:49578 was successfully served!

/127.0.0.1:49581 was successfully served!
```

以下是一些可能的客户端输出：

```base
Hello !

Random number:78

Random number:72

Random number:29

Random number:77

Random number:35

Random number:0
…
50 was generated! Close the asynchronous socket channel!
```

### Writing an Asynchronous Server (Based on CompletionHandler)

接下来，我们想使用CompletionHandler模式开发相同的回显异步服务器未来模式。 

实际上，通过让CompletionHandler模式处理，我们将它们混合在一起连接的接受操作并让“未来”模式处理读/写操作。 

我们打开异步服务器套接字通道，设置其选项，然后将其完全绑定和我们之前一样。 

接下来，我们着重于传达接受连接的愿望。 

为此，我们调用accept（）方法：

```Java
public abstract <A> void accept(A attachment,CompletionHandler<AsynchronousSocketChannel,? super A> handler)
```

此方法获取对象以附加到I / O操作（可以为null）并完成接受连接（或操作失败）时调用的处理程序。 

结果传递给完成处理程序是到新连接的AsynchronousSocketChannel。

我们将CompletionHandler实现为匿名内部类，并覆盖其方法。 

现在，完成处理程序的completed（）方法负责维护和关闭与连接的客户端进行“对话”。 

为此，我们使用与以下相同的read（）和write（）方法较早并使用相同的方法。 

应该调用完成处理程序的failed（）方法仅当接受连接的操作失败时，我们才会抛出异常并准备接受另一个连接。

接受连接后，我们立即通过调用accept（）准备一个新的连接来自completed（）和failed（）方法的方法，如下所示（这是代码的第一行）：

```Java
asynchronousServerSocketChannel.accept(null, this);
```

最后，还有一个方面需要注意。 

由于这是一个异步应用程序，因此流程将“遍历”整个应用程序并快速退出，甚至无法建立单个连接或服务，这是不好的，因为我们希望服务器等待很长时间为客户服务。 

从而，我们必须添加一些代码以使流“挂在空中”，例如通过添加Thread.sleep（）方法或System.in.read（）方法，或通过加入主线程并等待其死亡或其他的东西。 

在此示例中，我们将选择System.in.read（）方法。

这是CompletionHandler异步服务器：

```Java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        final int DEFAULT_PORT = 5555;
        final String IP = "127.0.0.1";
        //create an asynchronous server socket channel bound to the default group
        try (AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()) {

            if (asynchronousServerSocketChannel.isOpen()) {
                //set some options
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF,4 * 1024);
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                //bind the server socket channel to local address
                asynchronousServerSocketChannel.bind(new InetSocketAddress(IP, DEFAULT_PORT));
                //display a waiting message while ... waiting clients
                System.out.println("Waiting for connections ...");

                asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

                    final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                    
                    @Override
                    public void completed(AsynchronousSocketChannel result, Void attachment) {
                        asynchronousServerSocketChannel.accept(null, this);

                        try {
                            System.out.println("Incoming connection from: " + result.getRemoteAddress());
                            //transmitting data
                            while (result.read(buffer).get() != -1) {
                                buffer.flip();
                                result.write(buffer).get();
                                if (buffer.hasRemaining()) {
                                    buffer.compact();
                                } else {
                                    buffer.clear();
                                }
                            }
                        } catch (IOException | InterruptedException | ExecutionException ex) {
                            System.err.println(ex);
                        } finally {

                            try {
                                result.close();
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        asynchronousServerSocketChannel.accept(null, this);
                        throw new UnsupportedOperationException("Cannot accept connections!");
                    }
                });
                // Wait
                System.in.read();
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        } catch (IOException ex) {
        System.err.println(ex);
        }
    }
}
```

### Writing an Asynchronous Client (Based on CompletionHandler)

我们服务器的客户端也可以通过CompletionHandler来实现，以处理连接请求操作。 

为此，我们将调用以下connect（）方法：

```Java
public abstract <A> void connect(SocketAddress remote, A attachment,CompletionHandler<Void,? super A> handler)
```

此方法获取此通道要连接到的远程地址，即要附加的对象到I / O操作（可以为null），以及连接为时调用的完成处理程序成功建立与否。

我们将CompletionHandler实现为匿名内部类，并覆盖其方法。 

现在，完成处理程序的completed（）方法负责维护和关闭与服务器“对话”。 

为此，我们使用与之前相同的read（）和write（）方法，并使用同样的方法。 

仅在以下情况下才应调用完成处理程序的failed（）方法：连接操作失败-在这种情况下，通道已关闭。

这是CompletionHandler异步客户端：

```Java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        final int DEFAULT_PORT = 5555;
        final String IP = "127.0.0.1";

        //create an asynchronous socket channel bound to the default group
        try (AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open()) {
            if (asynchronousSocketChannel.isOpen()) {
                //set some options
                asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
                asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
                asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

                //connect this channel's socket
                asynchronousSocketChannel.connect(new InetSocketAddress(IP, DEFAULT_PORT), null, new CompletionHandler<Void, Void>() {
                    
                    final ByteBuffer helloBuffer = ByteBuffer.wrap("Hello !".getBytes());
                    final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                    CharBuffer charBuffer = null;
                    ByteBuffer randomBuffer;
                    final Charset charset = Charset.defaultCharset();
                    final CharsetDecoder decoder = charset.newDecoder();

                    @Override
                    public void completed(Void result, Void attachment) {
                        try {
                            System.out.println("Successfully connected at: " + asynchronousSocketChannel.getRemoteAddress());
                            //transmitting data
                            asynchronousSocketChannel.write(helloBuffer).get();

                            while (asynchronousSocketChannel.read(buffer).get() != -1) {
                                
                                buffer.flip();
                                charBuffer = decoder.decode(buffer);
                                System.out.println(charBuffer.toString());

                                if (buffer.hasRemaining()) {
                                    buffer.compact();
                                } else {
                                    buffer.clear();
                                }

                                int r = new Random().nextInt(100);
                                if (r == 50) {
                                    System.out.println("50 was generated! Close the asynchronous socket channel!");
                                    break;
                                } else {
                                    randomBuffer = ByteBuffer.wrap("Random
                                    number:".concat(String.valueOf(r)).getBytes());
                                    asynchronousSocketChannel.write(randomBuffer).get();
                                }
                            }

                        } catch (IOException | InterruptedException | ExecutionException ex) {
                            System.err.println(ex);
                        } finally {
                            try {
                                asynchronousSocketChannel.close();
                            } catch (IOException ex) {
                                System.err.println(ex);
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        throw new UnsupportedOperationException("Connection cannot be established!");
                    }

                });
                System.in.read();
            } else {
                System.out.println("The asynchronous socket channel cannot be opened!");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}

```

### Testing the Echo Application (Based on CompletionHandler)

测试应用程序是一个简单的任务。 

首先，启动服务器，等待直到看到消息“等待连接...。”继续以启动客户端并签出输出。 

下列是可能的服务器输出：

```base
Waiting for connections ...

Incoming connection from: /127.0.0.1:50369

Incoming connection from: /127.0.0.1:50370

Incoming connection from: /127.0.0.1:50371

Incoming connection from: /127.0.0.1:50372
```

下面显示了可能的客户端输出：

```base
Hello !

Random number:19

Random number:54

Random number:28

Random number:59

Random number:34

Random number:60
…
50 was generated! Close the asynchronous socket channel!
```

### Using Read/Write Operations and CompletionHandler

在前面的示例中，我们通过“未来”模式管理了读/写操作。 

如果你想要将CompletionHandler与读/写操作相关联，则可以使用下一个 AsynchronousSocketChannel的read（）和write（）方法：

-   第一个read（）方法启动一个从中读取字节序列的操作该通道进入给定缓冲区的子序列（称为异步分散读取）。 该操作必须在指定的超时后结束：

```Java
public abstract <A> void read(ByteBuffer[] dsts, int offset, int length, long timeout,TimeUnit unit, A attachment, CompletionHandler<Long,? super A> handler)
```

-   此方法启动一个操作，从该操作读取一个字节序列引导到给定的缓冲区：

```Java
public final <A> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer,? super A> handler)
```

-   此方法启动一个操作，从该操作读取一个字节序列通道到给定的缓冲区。 该操作必须在指定的超时后结束：

```Java
public abstract <A> void read(ByteBuffer dst,long timeout, TimeUnit unit, A attachment,CompletionHandler<Integer,? super A> handler)
```

与这些方法类似，但是对于编写操作，我们有一种异步方法收集写：

```Java
public abstract <A> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long,? super A> handler)
```

我们还有另外两种从给定缓冲区向该通道写入字节序列的方法：

```Java
public final <A> void write(ByteBuffer src, A attachment,CompletionHandler<Integer,? super A> handler)

public abstract <A> void write(ByteBuffer src, long timeout, TimeUnit unit,A attachment, CompletionHandler<Integer,? super A> handler)
```

### Writing an Asynchronous Client/Server Based on Custom Group

以前的客户端/服务器应用程序是使用默认组开发的。 

我们可以指定一个自定义组作为AsynchronousChannelGroup对象传递给AsynchronousServerSocketChannel.open（）方法和/或AsynchronousSocketChannel.open（）方法。

首先，我们创建一个自定义组。 本示例创建一个初始大小为1的缓存线程池线：

```Java
AsynchronousChannelGroup threadGroup = null;
…
ExecutorService executorService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
try {
    threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
} catch (IOException ex) {
    System.err.println(ex);
}
```

以下示例创建一个具有五个线程的固定线程池：

```Java
AsynchronousChannelGroup threadGroup = null;
…
try {
    threadGroup = AsynchronousChannelGroup.withFixedThreadPool(5,Executors.defaultThreadFactory());
} catch (IOException ex) {
    System.err.println(ex);
}
```

并且，可以将threadGroup传递到异步通道以进行面向流的侦听套接字-如果该组已关闭并且接受了连接，则该连接将关闭，并且操作完成并带有IOException异常，并导致ShutdownChannelGroupException：

```Java
AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(threadGroup);
```

接受新连接后，生成的AsynchronousSocketChannel将绑定到与此通道相同的AsynchronousChannelGroup。

或者可以将ThreadGroup传递到异步通道以进行面向流的连接套接字-如果该组已关闭且连接处于活动状态，则该连接将关闭，并且操作完成并带有IOException异常，并导致ShutdownChannelGroupException：

```Java
AsynchronousSocketChannel asynchronousSocketChannel =
AsynchronousSocketChannel.open(threadGroup);
```

现在，您可以修改上述应用程序以使用自定义组。

### Tips

本章介绍的应用程序适合用于教育目的，但不适用于生产环境。 

如果您需要为生产环境编写应用程序，那么最好请记住以下提示。

-   Use Byte Buffer Pool and Throttle Read Operations

考虑以下情况：AsynchronousSocketChannel.read（）方法从数千个客户，并创建数千个ByteBuffer。

该方法能够从大量的慢速读取客户一段时间，但最终却被大量客户到达所淹没。 

您可以通过使用技巧来避免这种情况：使用字节缓冲池和限制读取操作。 

另外，可能如果字节缓冲区过大，将有可能耗尽内存，因此您必须注意内存消耗（可能会调整Java堆参数，例如Xms和Xmx）。

-   Use Blocking Only for Short Reading Operations

对于下一种情况，假设正在从中读取AsynchronousSocketChannel.read（）方法。

客户端处于“未来”模式，这意味着get（）方法将等待，直到读取操作完成，因此阻塞了线程。 

在这种情况下，您必须确保不锁定线程池，特别是在使用固定线程池的情况下。 

您可以通过仅对简短的阅读操作。 

使用超时也是一种解决方案。

-   Use FIFO-Q and Allow Blocking for Write Operations

现在着重于写操作，请考虑以下情况：AsynchronousSocketChannel.write（）方法无阻碍地将字节写入其客户端-它启动编写操作并继续执行其他任务。 

但是，继续执行其他任务可能会导致线程再次调用write（）方法，并且上一个尚未调用完成处理程序写电话。 

馊主意！ 将抛出WritePendingException异常。 

您可以通过以下方法解决此问题：确保在启动新的写操作之前调用完成处理程序complete（）方法。

为此，对字节缓冲区使用先进先出队列（FIFO-Q），并且仅在前一个write（）已完成。 

因此，使用FIFO-Q并允许阻塞写操作。

另请参阅本章前面的“ ByteBuffers注意事项”部分。


----

