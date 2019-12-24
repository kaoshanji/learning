# 加盟

该`join`方法允许一个线程等待另一个线程的完成。如果`t`是`Thread`其线程当前正在执行的对象，

```
t.join（）;
```

导致当前线程暂停执行，直到`t`线程终止。过载`join`允许程序员指定等待期。然而，与`sleep`，`join`是依赖于操作系统的时间，所以你不应该假设`join`将等待确切只要你指定。

比如`sleep`，`join`通过退出来响应中断`InterruptedException`。
