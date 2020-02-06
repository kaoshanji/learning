package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 本机正在运行的TCP服务
 * @author kaoshanji
 * @time 2020/2/6 下午2:26
 */
public class LowPortScanner {

    public static void main(String[] args) {

        String host = "localhost";

        for (int i = 1; i < 1024; i++) {
            try {
                Socket s = new Socket(host, i);
                System.out.println("There is a server on port " + i + " of " + host);
                s.close();
            } catch (UnknownHostException ex) {
                System.err.println(ex);
                break;
            } catch (IOException ex) {
                // must not be a server on this port
            }
        }
    }

}
