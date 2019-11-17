#   Fork/Join

fork / join框架是`ExecutorService`接口的实现，可帮助您利用多个处理器。它专为可以递归分解成小块的工作而设计。目标是使用所有可用的处理能力来提高应用程序的性能。

与任何`ExecutorService`实现一样，fork / join框架将任务分配给线程池中的工作线程。fork / join框架是不同的，因为它使用了*工作窃取*算法。用尽要做的事情的工作线程可以从仍然忙碌的其他线程中窃取任务。

fork / join框架的中心是[`ForkJoinPool`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html)类，是 类的扩展`AbstractExecutorService`。`ForkJoinPool`实现核心工作窃取算法并可以执行 [`ForkJoinTask`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinTask.html)进程。

## 基本用法

使用fork / join框架的第一步是编写执行一部分工作的代码。您的代码应类似于以下伪代码：

```java
if (my portion of the work is small enough)
  do the work directly
else
  split my work into two pieces
  invoke the two pieces and wait for the results
```

将此代码包装在`ForkJoinTask`子类中，通常使用其中一种更专业的类型 [`RecursiveTask`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RecursiveTask.html)（可以返回结果）或 [`RecursiveAction`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RecursiveAction.html)。

在您的`ForkJoinTask`子类是准备好了，创建一个表示要完成所有的工作对象，把它传递给`invoke()`一个方法`ForkJoinPool`实例。

## 模糊为Clarity

为了帮助您了解fork / join框架的工作原理，请考虑以下示例。假设您想模糊图像。原始*源*图像由整数数组表示，其中每个整数包含单个像素的颜色值。模糊的*目标*图像也由与源相同大小的整数数组表示。

通过一次一个像素地处理源阵列来完成模糊。每个像素的平均周围像素（红色，绿色和蓝色分量被平均），结果放在目标数组中。由于图像是大型数组，因此此过程可能需要很长时间。通过使用fork / join框架实现算法，您可以利用多处理器系统上的并发处理。这是一个可能的实现：

```java
public class ForkBlur extends RecursiveAction {
    private int[] mSource;
    private int mStart;
    private int mLength;
    private int[] mDestination;
  
    // Processing window size; should be odd.
    private int mBlurWidth = 15;
  
    public ForkBlur(int[] src, int start, int length, int[] dst) {
        mSource = src;
        mStart = start;
        mLength = length;
        mDestination = dst;
    }

    protected void computeDirectly() {
        int sidePixels = (mBlurWidth - 1) / 2;
        for (int index = mStart; index < mStart + mLength; index++) {
            // Calculate average.
            float rt = 0, gt = 0, bt = 0;
            for (int mi = -sidePixels; mi <= sidePixels; mi++) {
                int mindex = Math.min(Math.max(mi + index, 0),
                                    mSource.length - 1);
                int pixel = mSource[mindex];
                rt += (float)((pixel & 0x00ff0000) >> 16)
                      / mBlurWidth;
                gt += (float)((pixel & 0x0000ff00) >>  8)
                      / mBlurWidth;
                bt += (float)((pixel & 0x000000ff) >>  0)
                      / mBlurWidth;
            }
          
            // Reassemble destination pixel.
            int dpixel = (0xff000000     ) |
                   (((int)rt) << 16) |
                   (((int)gt) <<  8) |
                   (((int)bt) <<  0);
            mDestination[index] = dpixel;
        }
    }
```

现在，您实现了抽象`compute()`方法，该方法直接执行模糊或将其拆分为两个较小的任务。简单的数组长度阈值有助于确定是执行还是拆分工作。

```java
protected static int sThreshold = 100000;

protected void compute() {
    if (mLength < sThreshold) {
        computeDirectly();
        return;
    }
    
    int split = mLength / 2;
    
    invokeAll(new ForkBlur(mSource, mStart, split, mDestination),
              new ForkBlur(mSource, mStart + split, mLength - split,
                           mDestination));
}
```

如果以前的方法在类的子`RecursiveAction`类中，那么将任务设置为在a中运行`ForkJoinPool`很简单，并涉及以下步骤：

1. 创建一个代表要完成的所有工作的任务。

   ```java
   // source image pixels are in src
   // destination image pixels are in dst
   ForkBlur fb = new ForkBlur(src, 0, src.length, dst);
   ```

2. 创建`ForkJoinPool`将运行任务的那个。

   ```java
   ForkJoinPool pool = new ForkJoinPool();
   ```

3. 运行任务。

   ```java
   pool.invoke(fb);
   ```

有关完整源代码，包括一些创建目标映像文件的额外代码，请参阅 [`ForkBlur`](examples/ForkBlur.java)示例。

## 标准实施

除了使用fork / join框架来实现在多处理器系统上同时执行的任务的自定义算法（例如`ForkBlur.java`上一节中的示例）之外，Java SE中还有一些通常有用的功能，它们已经使用fork / join实现了框架。在Java SE 8中引入的一种这样的实现被 [`java.util.Arrays`](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html)类用于其`parallelSort()`方法。这些方法类似于`sort()`，但通过fork / join框架利用并发性。在多处理器系统上运行时，大型数组的并行排序比顺序排序更快。但是，这些方法如何利用fork / join框架超出了Java Tutorials的范围。有关此信息，请参阅Java API文档。

fork / join框架的另一个实现由`java.util.streams`包中的方法使用，该方法是为Java SE 8发布而安排的[Project Lambda的](http://openjdk.java.net/projects/lambda/)一部分 。有关更多信息，请参阅 [Lambda表达式](../../java/javaOO/lambdaexpressions.html)部分。