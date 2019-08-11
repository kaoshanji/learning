# 线程对象

每个线程都与该类的实例相关联 [`Thread`](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html)。使用`Thread`对象创建并发应用程序有两种基本策略。

- 要直接控制线程创建和管理，只需`Thread`在应用程序每次启动异步任务时进行实例化。
- 要从应用程序的其余部分抽象线程管理，请将应用程序的任务传递给*执行程序*。

本节介绍了`Thread`对象的使用。与其他[高级并发对象](highlevel.html)讨论执行程序。