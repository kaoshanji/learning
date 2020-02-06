package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池版daytime服务器
 * @author kaoshanji
 * @time 2020/2/6 下午4:10
 */
public class PooledDaytimeServer {

    public final static int PORT = 1313;

    public static void main(String[] args) {

        // 线程池，固定长度，请求太多就会拒绝，但是服务器可以正常运行
        ExecutorService pool = Executors.newFixedThreadPool(50);

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket connection = server.accept();

                    // 线程里的任务逻辑与单线程里是一样的
                    Callable<Void> task = new DaytimeTask(connection);

                    // 提交任务..
                    // 任务被包装，提交到线程池里
                    pool.submit(task);
                } catch (IOException ex) {}
            }
        } catch (IOException ex) {
            System.err.println("Couldn't start server");
        }
    }

    private static class DaytimeTask implements Callable<Void> {

        private Socket connection;

        DaytimeTask(Socket connection) {
            this.connection = connection;
        }

        @Override
        public Void call() {
            try {
                Writer out = new OutputStreamWriter(connection.getOutputStream());
                Date now = new Date();
                out.write(now.toString() +"\r\n");
                out.flush();
            } catch (IOException ex) {
                System.err.println(ex);
            } finally {
                try {
                    connection.close();
                } catch (IOException e) {
                    // ignore;
                }
            }
            return null;
        }
    }
}

