#   Java NIO 中文版 

>   依据 《Java NIO(中文版)》- Ron Hitchens

NIO来源和主要内容：[NIO规范](https://jcp.org/en/jsr/detail?id=51)

##  内容概述
-   [前言](260x.md)
-   [NIO概述](250x.md)
-   [缓冲区](220x.md)
-   [通道](210x.md)
-   [选择器](270x.md)
-   [字符集](230x.md)
-   [套接字](240x.md)通道



本书介绍了 Java 平台上的高级输入/输出，具体点说，就是使用 J2SE 软件开发包(SDK) 1.4 及以后版本进行的输入/输出。这些新的 I/O 特性主要包含在 java.nio 软件包及其子包中，并被命名为 New I/O(NIO)。通过本书，将学会如何使用这些令人兴奋的新特性来极大的提升 Java 应用程序的 I/O 效率。

I/O 的终极目标是效率，而高效的 I/O 往往又无法与对象形成一一对应的关系。高效的 I/O 往往意味着要选择从 A 到 B 的最短路径，而执行大量 I/O 操作时，复杂性毁了执行效率。

具体的企业在具体的系统上配置具体的应用，这里无关乎抽象。在现实世界，效率是大事--头等大事。当企业的需求是以最快的速度传送大量数据时，样貌朴实但迅捷的解决方案让让胜过漂亮却动作迟缓的，一句话，`时间就是金钱`。

-   简介
    -   I/O 与 CPU 时间得比较：在 I/O 性能上的小小投入就可换来可观的回报
    -   CPU 已不再是束缚
        -   JVM运行字节码的速率已经接近本地编译代码
        -   借助动态运行时优化，其表现甚至还有所超越
        -   操作系统与 Java 基于流的 I/O 模型有些不匹配，系统移动大块数据(缓冲区)，JVM 的 I/O 类喜欢小数据块
    -   进入正题
    -   I/O 概念：操作系统层面的 I/O 函数
        -   缓冲区操作：所谓"输出/输出"就是把数据移进或移出缓冲区，缓冲区存在于内核空间与用户空间
            -   内核空间是操作系统所在区域，用户空间是常规进程所在区域，如：JVM
            -   内核代码有特别的权利：能与设备控制器通讯，控制着用户区域进程的运行状态，可以访问用户空间
            -   用户空间是非特权区域，不能直接访问硬件设备，只能直接或间接通过内核空间 I/O，不能访问内核空间
            -   用户进程请求 I/O 操作时，执行一个系统调用将控制权移交给内核，内核采取任何必要步骤找到进程所需数据读取到内核空间，然后把数据传送到用户空间内指定缓冲区，数据从内核空间到用户空间存在一次拷贝
            -   设备控制器不能通过 DMA 直接存储数据到用户空间
            -   磁盘这样基于块存储的硬件设备操作的是固定大小的数据块，而用户进程请求的可能是任意大小或非对齐的数据块
            -   在数据往来于用户空间与存储设备的过程中，内核负责数据的分解、再组合工作，充当中间人的角色 
            -   改进：进程只需一个系统调用，就把一连串缓冲区地址传递给操作系统，然后，内核就可以顺序填充或排干多个缓冲区，读的时候把数据发散到多个用户空间缓冲区，写的时候再从多个缓冲区把数据汇聚起来
        -   虚拟内存：虚拟地址取代物理内存地址，内存管理
            -   分类：1、多个虚拟地址指向同一个物理内存地址，2、虚拟内存空间可大于实际可用的硬件内存
            -   把内核空间地址与用户空间的虚拟地址映射到同一个物理地址，如此，DMA 硬件(只能访问物理内存地址)就可以填充对内核与用户空间进程`同时可见`的缓冲区
            -   同时可见的缓冲区 意味着 内核与用户空间往来拷贝没有了..条件：内核与用户缓冲区必须使用相同的`页对齐`，缓冲区大小必须是磁盘控制器块大小(512k)的倍数
            -   操作系统把内存地址空间划分为`页`，即固定大小的`字节组`，内存页的大小(1024/2048)是磁盘块大小的倍数
            -   虚拟和物理内存页的大小总是相同
        -   内存页面调度
            -   为了寻址空间大于物理内存，就必须进行虚拟内存分页
            -   虚拟内存空间的页面能够继续存在于外部磁盘存储，就为物理内存中其他虚拟页面腾出空间
            -   物理内存充当了分页区的高速缓存
            -   进程所属的虚拟页面，由 物理内存 和 分区页(磁盘) `组成`
            -   把内存页大小设定为磁盘块大小的倍数，内核就可直接向磁盘控制硬件发布命令，把内存页写入磁盘，在需要时再重新载入，所有磁盘I/O都在页层面完成
        -   文件 I/O
            -   文件 I/O 属文件系统范畴，是文件系统定义了文件名、路径、文件、文件属性等抽象概念
            -   所有 I/O 都是通过请求页面调度完成，页面调度非常底层仅发生与磁盘扇区与内存页之间的直接传输，磁盘扇区与内存页大小都固定而文件 I/O 则可以任意大小、任意定位
            -   文件系统把一连串大小一致的数据库组织到一起，有些块存储元信息、有些包含文件数据
            -   单个文件的元信息描述了那些块包含文件数据、数据在那里结束等等
            -   当用户进程请求读取文件数据时，文件系统需要确定数据具体在磁盘什么位置，然后着手把相关磁盘扇区读进内存
-   缓冲区
    -   概述
        -   一个 Buffer 对象是固定数量的数据的容器，其作用是一个存储器，或者分段运输区，在这里数据可被存储并在之后用于检索
        -   缓冲区作用于他们存储的原始数据类型，但十分倾向于`处理字节`
        -   通道是 I/O 传输发生时通过的入口，而缓冲区是这些数据传输的来源或目标
        -   对于离开缓冲区的传输，想传递出去的数据被置于一个缓冲区，被传送到通道
        -   对于传回缓冲区的传输，一个通道将数据放置在你所提供的缓冲区中
    -   缓冲区基础
        -   缓冲区是包在一个对象内的基本数据元数数组，将关于数据的数据内容和信息包含在一个单一的对象中
        -   属性：容量、上界、位置、标记
        -   缓冲区 API
        -   存取
        -   填充
        -   翻转
        -   释放
        -   压缩
        -   标记
        -   比较
        -   批量移动
    -   创建缓冲区
        -   这些类没有一种能够直接实例化，都是抽象类，但都包含静态工厂方法用来创建相应类的新实例
    -   复制缓冲区
        -   当一个管理其他缓冲器所包含的数据的缓存器被创建时，这个缓冲器被称为视图缓冲器
        -   视图存储器总是通过调用已存在的存储器实例中的函数来创建
    -   字节缓冲区
        -   字节是操作系统及其 I/O 设备使用的基本数据类型
        -   当在 JVM 和操作系统间传递数据时，将其他的数据类型拆分成构成他们的字节是十分必要的
        -   系统层次的 I/O 面向字节的性质可以在整个缓冲区的设计以及他们相互配合得服务中感受到
        -   字节顺序
        -   直接缓冲区
        -   视图缓冲区
        -   数据元数视图
        -   存取无符号数据
        -   内存映射缓冲区
-   通道：java.nio 是第二个主要创新
    -   概述
        -   Channel 用于在字节缓冲区和位于通道另一侧的实体(通常是一个文件或套接字)之间有效的传输数据
        -   通道是一种途径，借助该途径，可以用最小的总开销来访问操作系统本身的 `I/O服务`
        -   缓冲区则是通道内部用来发送和接收数据的端点
    -   通道基础
        -   操作系统都是以字节的形式实现底层 I/O 接口的
        -   打开通道：通道是访问 I/O 服务的导管
        -   使用通道
            -   示例：从一个通道复制数据到另一个通道
        -   关闭通道
    -   Scatter/Gather(矢量I/O)
        -   指在多个缓冲区上实现一个简单的 I/O 操作
        -   示例：按照不同的组合构建多个缓冲区阵列引用，各种数据区块就可以以不同的方式来组合
    -   文件通道
        -   概述
            -   FileChannel 对象是线程安全的
            -   FileChannel 是一个反映 Java 虚拟机外部一个具体对象的抽象
        -   访问文件
            -   每个 FileChannel 对象都同一个文件描述符有一对一的关系
        -   文件锁定
            -   锁的对象是文件而不是通道或线程
            -   锁最终是由操作系统或文件系统来判优并且几乎总是在进程级而非线程级上判优
    -   内存映射文件
        -   通过内存映射机制来访问一个文件会比使用常规方法读写高效得多，甚至比使用通道的效率都高
        -   操作系统的虚拟内存可以自动缓存内存页，不会消耗 JVM 内存堆
    -   Socket 通道
        -   非阻塞模式
        -   ServerSocketChannel
        -   SocketChannel
        -   DatagramChannel
    -   管道：一个用来在两个实体之间单向传输数据的导管
        -   Unix系统中，管道被用来连接一个进程的输出和另一个进程的输入
        -   Pipe类实现了一个管道范例，创建的管道是进程内(JVM)而非进程间使用
    -   通道工具类
-   选择器
    -   概述
        -   选择器提供选择执行已经就绪的任务的能力，使得多元 I/O 成为可能
        -   就绪选择和多元执行使得单线程能够有效率地同时管理多个 I/O 通道
        -   快速检查大量资源中的任意一个是否需要关注，而在某些东西没有准备好时又不必被迫等待的通用模式
    -   选择器基础
        -   概述
            -   将创建的一个或多个可选择的通道注册到选择器对象中
            -   一个表示通道和选择器的键将会被返回，选择键会记住你关心的通道
            -   选择键会追踪对应的通道是否已经就绪
            -   当调用一个选择器对象的 select() 方法时，相关的键会被更新，用来检查所有被注册到该选择器的通道
            -   通过遍历这些键，可以选择出每个从上次调用 select() 开始直到现在，已经就绪的通道
            -   真正的就绪选择必须由操作系统来做，操作系统的一项最重要的功能就是处理 I/O 请求并通知各个线程他们的数据已经准备好了
        -   选择器，可选择通道和选择键类
            -   `选择器`(Selector)：选择器类管理着一个被注册的通道集合的信息和他们的就绪状态，通道和选择器一起被注册，使用选择器来更新通道的就绪状态
            -   `可选择通道`(SelectableChannel)：提供了实现通道的可选择性所需要的公共方法，所有 socket 通道都是可选择的，包括从管道得到的通道
            -   `选择键`(SelectionKey)：选择键封装了特定的通道与特定的选择器的注册关系
        -   建立选择器
    -   使用选择键
    -   使用选择器
        -   选择过程
        -   停止选择过程
        -   管理选择键
        -   并发性
    -   异步关闭能力
    -   选择过程的可扩展性
-   字符集

