# 暂停执行与睡眠

`Thread.sleep`导致当前线程暂停执行指定的时间段。这是使处理器时间可用于应用程序的其他线程或可能在计算机系统上运行的其他应用程序的有效方法。该`sleep`方法还可以用于调步，如下面的示例所示，并等待具有被理解为具有时间要求的职责的另一个线程，如`SimpleThreads`稍后部分中的示例所示。

提供了两个重载版本`sleep`：一个指定睡眠时间为毫秒，另一个指定睡眠时间为纳秒。但是，这些睡眠时间并不能保证精确，因为它们受到底层操作系统提供的设施的限制。此外，睡眠周期可以通过中断终止，我们将在后面的部分中看到。在任何情况下，您都不能假设调用`sleep`将在指定的时间段内暂停线程。

该 [`SleepMessages`](examples/SleepMessages.java)示例用于`sleep`以四秒为间隔打印消息：

```java
public class SleepMessages {
    public static void main(String args[])
        throws InterruptedException {
        String importantInfo[] = {
            "Mares eat oats",
            "Does eat oats",
            "Little lambs eat ivy",
            "A kid will eat ivy too"
        };

        for (int i = 0;
             i < importantInfo.length;
             i++) {
            //Pause for 4 seconds
            Thread.sleep(4000);
            //Print a message
            System.out.println(importantInfo[i]);
        }
    }
}
```

请注意，`main`声明它`throws InterruptedException`。`sleep`当另一个线程在`sleep`活动时中断当前线程时，抛出此异常。由于此应用程序尚未定义另一个引起中断的线程，因此无需捕获`InterruptedException`。