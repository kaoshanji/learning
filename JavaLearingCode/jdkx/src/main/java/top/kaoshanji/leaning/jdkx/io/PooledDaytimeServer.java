package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池版 dayTime服务器
 * @author kaoshanji
 * @time 2020-01-20 21:56
 */
public class PooledDaytimeServer {

    private final static Logger logger = LoggerFactory.getLogger(PooledDaytimeServer.class);

    private final static int PORT = 1313;

    public static void main(String[] args) {
        // 线程池长度..操作系统特定端口接入连接队列一般这么多..?
        ExecutorService pool = Executors.newFixedThreadPool(50);

        try (ServerSocket server = new ServerSocket(PORT)){
            while (true) {
                try {
                    // 接入一个连接
                    Socket connection = server.accept();
                    Callable<Void> task = new DaytimeTask(connection);
                    // 提交任务?
                    pool.submit(task);
                } catch (IOException ex) {}
            }
        } catch (IOException ex) {
            logger.error(".."+ex.getMessage());
        }
    }

}
