# 异步IO

在谈论Java中的异步I / O时，我们正在谈论异步通道。 

一个异步通道是一种连接，它通过以下方式并行支持多个I / O操作：分开的线程（例如，连接，读取和写入），并提供了用于启动操作后对其进行控制。

本节讨论所有异步通道共有的一些重要方面。

首先，请注意，所有异步通道都会启动I / O操作（不会阻止应用程序执行其他任务），并在I / O完成时提供通知。

这条规则是异步通道，并从中派生整个异步通道API。

在开始讨论异步I / O全局图之前，我们将看一下表单。 全部异步 I / O操作具有以下两种形式之一：

-   Pending(将来式)
-   Complete(回调式)

##  Pending 与 Future

第一种形式返回`java.util.concurrent.Future <V>`对象，并表示异步I / O操作。

通过Future的方法，我们可以检查操作是否完成，等待完成操作（如果尚未完成），然后检索操作结果。

例如，您可以通过Future.isXXX（）方法执行布尔检查：通过调用Future.isDone（）方法确定操作是否完成，或者可以检查是否通过调用Future.isCancelled（）方法取消了该操作。

您可以明确取消通过调用Future.cancel（）方法进行操作，该方法将返回一个表示成功的布尔值取消—如果执行此任务的线程应被中断，则将true传递给此对象方法;否则，将允许正在进行的任务完成。

如果任务有已完成，已被取消或由于其他原因而无法取消。

如果成功，并且在调用cancel（）时此任务尚未开始，则该任务永远不应运行。

取消异步I / O操作时，所有等待结果的线程都将抛出CancellationException。 

无法保证底层的I / O操作会立即被取消，但是可以确保进一步尝试启动与之前的操作“相同”的I / O操作。

取消将是不允许的（即，通道被置于实现特定的错误状态）。 

另外，继续请注意，如果将cancel（）方法参数设置为true，则I / O操作可能会因关闭而中断通道-等待I / O操作结果的所有线程都将引发CancellationException，并且任何通道上其他未完成的其他I / O操作均已完成，但具有AsynchronousCloseException异常。

确保取消读取/写入操作中涉及的I / O缓冲区通道保持打开状态。

只能使用Future.get（）和操作完成后的 Future.get(long timeout, TimeUnit unit)，如有必要，请等待直到准备就绪或指定的超时时间已到期。 在这种情况下，将抛出TimeoutException。 

V 表示此Future的get（）方法返回的结果类型，这意味着这是结果操作类型。

##  Complete 结果 和 CompletionHandler 接口

第二种形式，即完整结果，让人联想到众所周知的回调机制（例如AJAX回调）。

这是“将来”表单的替代机制。

我们注册一个回调到异步I / O操作（例如，读或写），当操作完成或失败时，调用处理程序（CompletionHandler）消耗操作结果。

完成处理程序的格式为`CompletionHandler <V，A>`，其中V是结果值的类型A是附加到I / O操作的对象的类型。

处理程序应重写两个方法：complete（）方法，当I / O操作成功完成时调用，而failed（）I / O操作失败时调用的方法。

如果操作成功完成，则结果作为参数传递给completed（）方法，如果操作失败，则将Throwable传递给failed（）方法。

忽略操作状态，两种方法均会收到附件表示传递给异步操作的对象的参数。

它可以用来追踪如果同一CompletionHandler对象用于多个操作，则哪个操作首先完成，

当然，您可能会发现它在其他情况下很有用。

这些方法的语法如下所示：

```Java
void completed(V result, A attachment)
void failed(Throwable exc, A attachment)
```

根据CompletionHandler的官方Java Platform SE 7文档，“这些方法应及时完成，以避免使调用线程无法调度到其他部分将说明原因。

##  异步 Channels 类型

在撰写本文时，Java 7带有以下三种类型的异步通道。 

下列小节依次简要介绍了每个小节。

-   AsynchronousFileChannel
-   AsynchronousServerSocketChannel
-   AsynchronousSocketChannel

### AsynchronousFileChannel

顾名思义，AsynchronousFileChannel类代表用于读取，写入和处理文件。 

此类提供读取和写入文件的方法基于ByteBuffers。 

此外，它提供了锁定文件，截断文件和获取文件的方法。

但请记住，与同步FileChannel通道不同，此类型的通道不会保持全局文件位置（当前位置）或偏移量。 

即使没有全局位置或偏移量，每个读取或写入操作应指定文件在其中读取或写入的位置。 

这个允许同时访问文件的不同部分。

使用AsynchronousFileChannel通道时，必须小心考虑以下几个方面：

-   通过显式调用继承的close（）来关闭异步文件通道方法（来自AsynchronousChannel接口）导致所有未完成的操作通道上的异步操作以完成AsynchronousCloseException异常。 关闭通道后，进一步尝试立即启动异步I / O操作并完成原因
ClosedChannelException。
-   如果尝试读取，可能会导致NonReadableChannelException异常。尚未打开阅读通道。 尝试写作可能会导致NonWritableChannelException异常，如果尚未为此频道打开写作。
-   此Java虚拟机已经存在或已锁定时进行的锁定尝试已经是锁定区域的未决尝试，将导致OverlappingFileLockException异常。

### AsynchronousServerSocketChannel

AsynchronousServerSocketChannel类表示面向流的异步通道监听套接字。 

打开这样的渠道类型可以使我们将其绑定到具有关联线程池，任务将提交到该线程池以处理I / O操作（当没有指定）。 

打开后，该通道可以接受一个异步方式，这意味着我们可以在Future和CompletionHandler之间进行选择跟踪连接状态。 

诸如绑定和设置频道选项之类的重要任务是通过已实现的NetworkChannel接口提供。

当您使用AsynchronousServerSocketChannel通道时，请小心考虑下列：

-   通过显式调用继承的来关闭异步服务器套接字通道close（）方法（来自AsynchronousChannel接口）导致所有未完成的操作通道上的异步操作以完成AsynchronousCloseException异常。 关闭通道后，进一步尝试立即启动异步I / O操作并完成原因ClosedChannelException。
-   如果打开尝试将导致ShutdownChannelGroupException异常。频道组已关闭。
-   尝试在未绑定的通道上调用accept（）方法将导致引发NotYetBoundException异常。
-   如果线程在上一个接受操作执行之前启动了接受操作完成后，将引发一个AcceptPendingException异常。

### AsynchronousSocketChannel

AsynchronousSocketChannel类表示面向流的异步通道连接插座。

打开这样的渠道类型可以使我们将其绑定到具有关联线程池，任务将提交到该线程池以处理I / O操作（当没有指定）。

打开后，该通道可以连接到异步方式，这意味着我们可以在Future和CompletionHandler之间进行选择跟踪连接状态。

为了成功连接，此通道可以读取和写入以下内容的缓冲区通过一组read（）和write（）异步方法获得的字节（字节序列，ByteBuffers）—再次，我们可以在Future和CompletionHandler之间进行选择，以跟踪读取或写入状态。

诸如绑定和设置频道选项之类的重要任务是通过实现的NetworkChannel接口。

当您使用AsynchronousSocketChannel通道时，请小心考虑以下：

-   通过显式调用继承的close（）来关闭异步套接字通道方法（来自AsynchronousChannel接口）导致所有未完成的操作通道上的异步操作最终以AsynchronousCloseException异常。 进一步尝试启动异步封闭通道上的I / O操作将立即完成ClosedChannelException异常。

-   尝试在未连接的通道上调用I / O操作将导致引发NotYetConnectedException异常。
-   如果线程在上一次读取操作之前启动了读取操作完成后，将引发ReadPendingException异常。如果有螺纹在先前的写操作完成之前启动写操作，然后WritePendingException异常将被抛出。
-   尝试连接到频道可能会导致AlreadyConnectedException如果此通道已连接，则为异常。
-   尝试连接到频道可能会导致ConnectionPendingException如果此通道上的连接操作已在进行中，则异常。
-   AsynchronousSocketChannel类定义的read（）和write（）方法允许在启动读取或写入操作时指定超时，分别。如果在操作完成之前已超时，则InterruptedByTimeoutException异常将完成操作。超时时间可能会使通道或基础连接处于不一致状态。如果该实现不能保证没有从或读取过字节写入通道，然后将通道放入特定于实现的错误状态。随后尝试启动读取或写入操作会导致抛出未指定的运行时异常。

##  Groups

如本章简介中所述，异步API引入了一个名为AsynchronousChannelGroup，它提出了异步通道组的概念，其中每个异步通道都属于一个通道组（默认通道或指定通道），该通道组共享一个Java线程池。 

这些线程接收执行I / O事件的指令，然后分派结果交给完成处理程序。 

异步通道组封装了线程池，并且通道工作的所有线程共享的资源。 

而且，该频道实际上是由组，因此如果组关闭，则通道也将关闭。

异步通道可安全用于多个并发线程。 

一些频道实现可能支持并发读取和写入，但可能不允许多个读取并且在任何给定时间都要执行一次写操作。

### 默认 Group

除了开发人员创建的组之外，JVM还会维护一个系统范围的默认组，该组是由自动，对于简单的应用程序很有用。 

如果未指定组，或者改为传递null，异步通道在构造时绑定到默认组。 

默认组可以通过两个系统属性进行配置，其中第一个如下：

```Java
java.nio.channels.DefaultThreadPool.threadFactory
```

以下是官方Java Platform SE 7文档中有关此属性的描述AsynchronousChannelGroup类：
```
此属性的值被视为混凝土的全限定名称ThreadFactory类。 

该类使用系统类加载器加载并实例化。

调用工厂的newThread方法为默认组的每个线程创建每个线程线程池。 

如果加载和实例化属性值的过程失败，则在默认组的构造过程中会引发未指定的错误。
```

换句话说，此系统属性定义了一个java.util.concurrent.ThreadFactory来代替默认之一。

第二个系统属性是

```Java
java.nio.channels.DefaultThreadPool.initialSize
```

官方Java Platform SE 7文档提供了以下描述：

```
默认组的initialSize参数的值。

财产的价值被认为是Integer的String表示形式，它是初始大小参数。

如果无法将值解析为整数，则会引发未指定的错误在构造默认组时。
```

简而言之，此系统属性指定线程池的初始大小。

### 自定义 Groups

如果默认组不能满足您的需求，则AsynchronousChannelGroup类提供了三个创建自己的频道组的方法。 

对于AsynchronousServerSocketChannel，AsynchronousSocketChannel和AsynchronousDatagramChannel（在撰写本文时不可用），通道组是在每个组的open（）方法创建时传递的。 

AsynchronousFileChannel不同从其他渠道来看，为了使用自定义线程池，open（）方法采用了使用ExecutorService而不是AsynchronousChannelGroup。 

现在，让我们看看有哪些优势和每个受支持的线程池的缺点是； 这些特征将帮助您决定哪一个在您的情况下是正确的。

-   FixedThreadPool

您可以通过调用以下AsynchronousChannelGroup方法来请求固定线程池：

```Java
public static AsynchronousChannelGroup withFixedThreadPool(int nThreads,ThreadFactory threadFactory) throws IOException
```

此方法创建具有固定线程池的通道组。 

您必须指定要使用的工厂创建新线程和线程数时。

固定线程池中的生命周期遵循一个简单的场景：一个线程等待一个I / O事件，完成事件的I / O，调用完成处理程序，然后返回以等待更多I / O事件（内核将事件直接分派到这些线程）。 

当完成处理程序正常终止时，线程返回到线程池并等待下一个事件。 

但是如果完成处理程序没有及时完成,这样，就有可能进入无限期阻塞。 

如果所有线程在完成时“死锁”处理程序，然后应用程序被阻塞，直到有一个线程可以再次执行，并且任何新事件将被排队直到线程可用。 

在最坏的情况下，没有线程可以释放，内核也不能再释放执行任何事情。 

如果您在完成操作中不使用阻塞或长时间操作，则可以避免此问题处理程序。 

另外，您可以使用缓存的线程池或超时来避免此问题。

-   CachedThreadPool

您可以通过调用以下AsynchronousChannelGroup方法来请求缓存的线程池：

```Java
public static AsynchronousChannelGroup withCachedThreadPool(ExecutorService executor,int initialSize) throws IOException
```

此方法使用给定的线程池创建一个异步通道组，从而创建新的线程根据需要。

您只需要指定初始线程数和一个ExecutorService即可根据需要创建新线程。

当它们可能可用时，它可以重用以前构造的线程。

在这种情况下，异步通道组将事件提交到线程池，该事件只是调用完成处理程序。

但是，如果线程池只是调用完成处理程序，那么谁并完成I / O操作吗？

答案是隐藏的线程池。

这是一套等待传入I / O事件的单独线程。

更准确地说，内核I / O操作是由一个或多个不可见内部线程处理，这些内部线程将事件调度到缓存池，缓存池依次调用完成处理程序。

隐藏的线程池很重要，因为它大大降低了应用程序的概率将被阻止（它解决了固定线程池的问题）并保证内核将能够完成其I / O操作。

但是我们仍然有一个问题，因为缓存的线程池需要无限排队，这会使队列无限增长并导致OutOfMemoryError-因此监视队列（避免锁定所有线程，并避免永久填充队列）。

避免使用在完成处理程序中进行阻塞或长时间操作仍然是一个好主意。

-   DesignatedThreadPool

您还可以通过调用以下AsynchronousChannelGroup方法来请求线程池：

```Java
public static AsynchronousChannelGroup withThreadPool(ExecutorService executor) throws IOException
```

此方法使用指定的线程池创建一个异步通道组。 

线程池是通过ExecutorService对象提供的。

ExecutorService执行提交的任务以调度操作的完成结果在组中的异步通道上启动。 

使用这种方法时，需要格外小心配置ExecutorService-在这里至少要做两件事：提供对直接切换或提交任务的无界排队的支持，并且永远不允许调用execute（）方法的线程直接调用任务。

-   关闭小组

关闭组可以通过调用shutdown（）方法或shutdownNow（）来完成方法。

调用shutdown（）方法可通过标记来启动关闭组的过程该组作为关机。

进一步尝试构建绑定到组的通道将抛出ShutdownChannelGroupException。

一旦标记为关闭，该组便开始终止流程，该流程包括等待所有绑定的异步渠道关闭（即完成处理程序已运行且资源已释放）。

您可以通过使用指定的awaitTermination（）方法来阻止直到组终止超时-直到组终止，发生超时或当前线程为止，阻塞一直在负责被打断，以先发生的为准。

您可以通过以下方式检查群组是否已终止isTerminated（）方法，您可以通过调用isShutdown（）方法检查它是否关闭。

请记住，shutdown（）方法不会强制停止或中断正在执行的线程完成处理程序。

另外，可以通过调用shutdownNow（）来强制关闭组。

方法，它将完全关闭组中的所有通道，就像AsynchronousChannel.close（）方法关闭它们。

请记住，调用此方法将完成，但有例外 AsynchronousCloseException此通道上的任何未完成的异步操作。

之后通道已关闭，进一步尝试启动异步I / O操作将立即完成导致ClosedChannelException。

当指定了ServiceExecutor时，它只能由结果异步通道组。

组的终止导致执行器有序关闭服务;

如果执行程序服务由于其他原因而关闭，则会发生未指定的行为。

对于面向流的连接套接字的异步通道，还存在通过调用shutdownInupt（）方法来关闭读取连接的可能性（该方法将拒绝任何通过返回流结束指示符-1）进行进一步的读取尝试，并通过调用shutdownOutput（）方法（该方法将通过抛出ClosedChannelException拒绝任何写入尝试例外）。 这些方法都不会关闭通道。


##  ByteBuffer 注意事项

如您所知，ByteBuffers不是线程安全的。 

因此，您必须确保您没有访问I / O操作当前涉及的字节缓冲区。

避免此问题的一个不错的解决方案是使用ByteBuffer池。 

当I / O操作即将进行时，您将从池中获取字节缓冲区，然后执行I / O操作，然后将字节缓冲区返回到池中。

解决此问题还解决了有关内存不足错误的另一个问题。 

内存需求缓冲区的使用取决于未完成的I / O操作的数量，但是使用池将帮助您重用一组缓冲区，避免出现内存不足的问题。

##  ExecutorService

组的较早讨论引用了ExecutorService API。

如果您不熟悉此API，您应该查阅官方文档，网址为http://download.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html 该API是Java并发和多线程概念的重要组成部分，并且它由于它是一个庞大而复杂的API，因此超出我们的目标。

我建议你也请参阅Lars Vogel的“ Java并发/多线程”教程，网址为：http://www.vogella.de/articles/JavaConcurrency/article.html（2011年5月17日发布）。

为了给您做一个简短的介绍，执行器框架提供了一种方便的方法来创建通过java.util.concurrent.Executors类（包含factory和多线程API中涉及的不同类型接口的实用程序方法，例如java.util.concurrent.Executor和java.util.concurrent.ExecutorService）。

该课程包含方法，例如newFixedThreadPool（），newCachedThreadPool（）和newScheduledThreadPool（）。

每个方法都会创建一个数字（由开发人员指定或默认推导实施）。 

ExecutorService接口将生命周期方法添加到 Executor，它可以关闭Executor（shutdown（）方法）并等待终止（awaitTermination（）方法）。

在许多情况下，执行器框架可与可运行的任务配合使用，不返回结果，但是当您希望线程返回计算结果时，可以使用java.util.concurrent.Callable接口，该接口利用泛型定义对象的类型回到。

结果在Callable.call（）方法内部计算，应重写因此，如果无法计算结果，则抛出Exception。

每个可调用任务均已提交到执行器（submit（）方法），并返回表示未决结果的Future；用这个来检查结果状态并通过调用get（）方法检索结果。

----
