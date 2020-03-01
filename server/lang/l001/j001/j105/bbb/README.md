#   Java 并发

编程模型有串行、并发、并行，发展趋势规模依次扩大。

Java 解决并发问题是原生支持多线程模型，基于共享内存实现通信，线程由操作系统调度使用CPU。

Java 借助第三方类库可以实现其他并发模型，如，借助 [Akka库](https://akka.io/) 实现 actor模型，借助 [Multiverse库](https://github.com/pveentjer/Multiverse) 实现STM模型。

##  多线程运行环境

硬件速度：CPU > 内存*1000 ，内存 > I/O设备 * 10000，为了合理利用CPU高性能，平衡三者的速度差异，CPU、操作系统、编译程序优化指令分别做了改进。

-   CPU 

CPU 增加了缓存，以均衡与内存的速度差异

缓存机制导致写入的可能是CPU缓存而不是内存

多核时代，每颗CPU都有自己的缓存，一致性更难解决了

这是个坑，会出现CPU缓存和内存数据不一致性，需要可见性解决，即一个线程对共享变量的修改，另外一个线程能够立即看到

-   操作系统

操作系统增加进程、线程，以分时复用CPU，均衡CPU和I/O设备的速度差异

一条高级编程语言的语句需要多条CPU指令完成，操作系统会在某条CPU指令执行完切换其他线程运行，即线程切换

为了保证高级语言里的语句可以被完整执行，需要原子性解决，即把一个或者多个操作在CPU执行的过程中不被中断的特性


并发编程是高性能程序的一种有效方案。

并发实用程序包为高性能线程实用程序（例如线程池和阻塞队列）提供了功能强大的可扩展框架。该软件包使程序员无需手工制作这些实用程序，这与集合框架对数据结构的处理方式几乎相同。此外，这些软件包为高级并发编程提供了低级原语。

----

##  [API规范](https://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/index.html)
-   [java.util.concurrent](111x.md)：并发编程有用工具类
-   [java.util.concurrent.atomic](112x.md)：一个小的类工具包，支持对单个变量进行无锁线程安全编程
-   [java.util.concurrent.locks](113x.md)：提供了用于锁定和等待条件的框架，这些条件不同于内置的同步和监视器
-   [JSR 166-并发实用程序](https://jcp.org/en/jsr/detail?id=166)
-   Doug Lea撰写的 Java并发编程：设计原理和模式（第二版）：领先专家的综合著作，他也是Java平台的并发框架的架构师
-   [Java Fork / Join框架](http://gee.cs.oswego.edu/dl/papers/fj.pdf)
-   [Java Concurrent Animated](https://sourceforge.net/projects/javaconcurrenta/)：显示并发功能用法的动画



##  目录

-   [官方：](https://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/overview.html)[并发使用工具概述](110x.md)


### 语法API

