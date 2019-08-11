# 高级并发对象

到目前为止，本课程重点关注从一开始就是Java平台一部分的低级API。这些API适用于非常基本的任务，但更高级的任务需要更高级别的构建块。对于充分利用当今多处理器和多核系统的大规模并发应用程序尤其如此。

在本节中，我们将介绍Java平台5.0版中引入的一些高级并发功能。大多数这些功能都在新`java.util.concurrent`包中实现。Java Collections Framework中还有新的并发数据结构。

- [锁定对象](newlocks.html)支持锁定习惯用法，简化了许多并发应用程序。
- [执行程序](executors.html)定义用于启动和管理线程的高级API。提供的执行程序实现提供`java.util.concurrent`适用于大规模应用程序的线程池管理。
- [并发集合](collections.html)使管理大量数据更容易，并且可以大大减少同步需求。
- [原子变量](atomicvars.html)具有最小化同步并有助于避免内存一致性错误的功能。
- [`ThreadLocalRandom`](threadlocalrandom.html) （在JDK 7中）提供了从多个线程有效生成伪随机数。