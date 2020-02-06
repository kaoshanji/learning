package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 获取Socket的信息
 * @author kaoshanji
 * @time 2020/2/6 下午2:40
 */
public class SocketInfo {

    public static void main(String[] args) {

        // 获取 host
        for (String host : args) {
            try {
                Socket theSocket = new Socket(host, 80);
                System.out.println("Connected to " + theSocket.getInetAddress()
                        + " on port "  + theSocket.getPort() + " from port "
                        + theSocket.getLocalPort() + " of "
                        + theSocket.getLocalAddress());
            }  catch (UnknownHostException ex) {
                System.err.println("I can't find " + host);
            } catch (SocketException ex) {
                System.err.println("Could not connect to " + host);
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }


}
