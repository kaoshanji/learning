package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * daytime服务器
 * 单线程版
 * 参见 MultithreadedDaytimeServer
 * 参见 PooledDaytimeServer
 * 还有：非阻塞多路复用版
 * 还有：异步回调或异步将来版
 * @author kaoshanji
 * @time 2020/2/6 下午3:38
 */
public class DaytimeServer {

    // 需要是 1024 以上的端口
    public final static int PORT = 1313;

    public static void main(String[] args) {
        // 可能关闭服务器
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                // 可能关闭当前连接
                try (Socket connection = server.accept()) { // 一直阻塞直到有请求..

                    // 向客户端响应数据..输出..逻辑
                    Writer out = new OutputStreamWriter(connection.getOutputStream());
                    Date now = new Date();
                    out.write(now.toString() +"\r\n");
                    out.flush();
                    connection.close();
                } catch (IOException ex) {}
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

}
