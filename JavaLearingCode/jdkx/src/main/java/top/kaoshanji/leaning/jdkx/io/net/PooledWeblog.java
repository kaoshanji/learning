package top.kaoshanji.leaning.jdkx.io.net;

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
 * 读取文件，并为每一行创建一个 LookupTask
 * 各个任务提交给一个 executor，可以并行和串行运行多个(不是全部)任务
 * Weblog 线程版
 * 优化
 *  日志文件可能很大..把输出放在一个单独的线程中，与输入线程共享一个队列
 *  需要一个单独的信号指示输出已经完成
 * @author kaoshanji
 * @time 2020/1/14 17:21
 */
public class PooledWeblog {

    private final static int NUM_THREADS = 4;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        Queue<LogEntry> results = new LinkedList<>();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"))){

            for (String entry = in.readLine(); entry != null; entry = in.readLine()) {
                // 处理逻辑
                LookupTask task = new LookupTask(entry);

                // 获取结果
                Future<String> future = executor.submit(task);

                // 原始数据、处理结果
                LogEntry result = new LogEntry(entry, future);
                results.add(result);
            }

        } catch (IOException ex) {
            ////
        }

        // 保持日志文件原来的顺序
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

        public LogEntry(String original, Future<String> future) {
            this.original = original;
            this.future = future;
        }
    }

}
