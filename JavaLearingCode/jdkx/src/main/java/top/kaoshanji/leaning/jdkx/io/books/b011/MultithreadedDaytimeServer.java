package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * 多线程daytime服务器
 * 同时请求太多，JVM可能不能承受
 * @author kaoshanji
 * @time 2020/2/6 下午4:03
 */
public class MultithreadedDaytimeServer {

    public final static int PORT = 1313;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                try {
                    // 接收入站连接线程
                    Socket connection = server.accept();

                    // 线程里的任务逻辑与单线程里是一样的
                    // 接入一个请求就使用一个线程处理
                    Thread task = new DaytimeThread(connection);
                    task.start();
                } catch (IOException ex) {}
            }
        } catch (IOException ex) {
            System.err.println("Couldn't start server");
        }
    }

    // 每个线程处理一个连接
    private static class DaytimeThread extends Thread {

        private Socket connection;

        DaytimeThread(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
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
        }
    }


}
