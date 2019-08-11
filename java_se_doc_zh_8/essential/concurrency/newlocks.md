# 锁定对象

同步代码依赖于简单的可重入锁定。这种锁易于使用，但有许多限制。[`java.util.concurrent.locks`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/package-summary.html)包装支持更复杂的锁定习语 。我们不会详细检查这个包，而是将重点放在它最基本的界面上 [`Lock`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/Lock.html)。

`Lock`对象的工作方式与同步代码使用的隐式锁非常相似。与隐式锁一样，一次只有一个线程可以拥有一个`Lock`对象。`Lock`对象还`wait/notify`通过其关联[`Condition`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/Condition.html)对象支持机制 。

`Lock`对象相对于隐式锁定的最大优点是它们能够退出获取锁定的尝试。`tryLock`如果锁定立即不可用或在超时到期之前（如果指定），则该方法退出。`lockInterruptibly`如果另一个线程在获取锁之前发送中断，则该方法退出。

让我们使用`Lock`对象来解决我们在[Liveness中](liveness.html)看到的死锁问题。当朋友即将鞠躬时，阿方斯和加斯顿已经训练自己注意到了。我们通过要求我们的`Friend`对象必须在继续执行弓之前获取*两个*参与者的锁来对此改进进行建模。以下是改进模型的源代码 [`Safelock`](examples/Safelock.java)。为了证明这个成语的多样性，我们假设阿尔方斯和加斯顿如此迷恋他们新发现的安全鞠躬能力，他们不能停止相互鞠躬：



```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class Safelock {
    static class Friend {
        private final String name;
        private final Lock lock = new ReentrantLock();

        public Friend(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public boolean impendingBow(Friend bower) {
            Boolean myLock = false;
            Boolean yourLock = false;
            try {
                myLock = lock.tryLock();
                yourLock = bower.lock.tryLock();
            } finally {
                if (! (myLock && yourLock)) {
                    if (myLock) {
                        lock.unlock();
                    }
                    if (yourLock) {
                        bower.lock.unlock();
                    }
                }
            }
            return myLock && yourLock;
        }
            
        public void bow(Friend bower) {
            if (impendingBow(bower)) {
                try {
                    System.out.format("%s: %s has"
                        + " bowed to me!%n", 
                        this.name, bower.getName());
                    bower.bowBack(this);
                } finally {
                    lock.unlock();
                    bower.lock.unlock();
                }
            } else {
                System.out.format("%s: %s started"
                    + " to bow to me, but saw that"
                    + " I was already bowing to"
                    + " him.%n",
                    this.name, bower.getName());
            }
        }

        public void bowBack(Friend bower) {
            System.out.format("%s: %s has" +
                " bowed back to me!%n",
                this.name, bower.getName());
        }
    }

    static class BowLoop implements Runnable {
        private Friend bower;
        private Friend bowee;

        public BowLoop(Friend bower, Friend bowee) {
            this.bower = bower;
            this.bowee = bowee;
        }
    
        public void run() {
            Random random = new Random();
            for (;;) {
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {}
                bowee.bow(bower);
            }
        }
    }
            

    public static void main(String[] args) {
        final Friend alphonse =
            new Friend("Alphonse");
        final Friend gaston =
            new Friend("Gaston");
        new Thread(new BowLoop(alphonse, gaston)).start();
        new Thread(new BowLoop(gaston, alphonse)).start();
    }
}
```

