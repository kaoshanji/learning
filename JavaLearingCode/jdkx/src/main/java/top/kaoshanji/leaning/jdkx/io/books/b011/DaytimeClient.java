package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Daytime 协议客户端
 * 可以在命令行试试 telnet time.nist.gov 13
 * @author kaoshanji
 * @time 2020/2/6 下午1:00
 */
public class DaytimeClient {

    public static void main(String[] args) {

        String hostname =  "localhost";//"time.nist.gov";
        Socket socket = null;
        try {
            socket = new Socket(hostname, 1313);

            // 超时时间设置，很重要，可以避免一些问题
            socket.setSoTimeout(15000);

            // 输入流从服务器读取数据..
            InputStream in = socket.getInputStream();
            StringBuilder time = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(in, "ASCII");
            for (int c = reader.read(); c != -1; c = reader.read()) {
                time.append((char) c);
            }
            System.out.println(time);
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }


}
