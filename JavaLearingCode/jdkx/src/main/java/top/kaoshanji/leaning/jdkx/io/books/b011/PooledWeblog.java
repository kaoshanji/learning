package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程池版Weblog
 * 单机版编程并发版...
 * 考虑 生产者-消费者模式?
 * 考虑 读写锁队列模式?
 * @author kaoshanji
 * @time 2020/2/5 下午4:17
 */
public class PooledWeblog {

    private final static int NUM_THREADS = 4;

    public static void main(String[] args) throws IOException {

        // 固定线程数量的线程池
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // 应该使用并发版集合?
        Queue<LogEntry> results = new LinkedList<LogEntry>();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));) {

            for (String entry = in.readLine(); entry != null; entry = in.readLine()) {
                // 获得一个任务..
                LookupTask task = new LookupTask(entry);
                Future<String> future = executor.submit(task);

                // 处理结果
                LogEntry result = new LogEntry(entry, future);
                results.add(result);
            }
        }

        // 开始打印结果，每次结果未准备就绪时就会阻塞
        for (LogEntry result : results) {
            try {
                System.out.println(result.future.get());
            } catch (InterruptedException | ExecutionException ex) {
                System.out.println(result.original);
            }
        }
        executor.shutdown();
    }

    private static class LogEntry {
        String original;
        Future<String> future;

        LogEntry(String original, Future<String> future) {
            this.original = original;
            this.future = future;
        }
    }


}
