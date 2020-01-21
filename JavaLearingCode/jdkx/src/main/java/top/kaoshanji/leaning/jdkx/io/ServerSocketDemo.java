package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * ServerSocket 示例
 * @author kaoshanji
 * @time 2020-01-20 20:55
 */
public class ServerSocketDemo {

    private final static Logger logger = LoggerFactory.getLogger(ServerSocketDemo.class);


    /**
     * daytime服务器
     */
    public void dayTimeServer() {
        int port = 1313; // 端口最好大于 1024
        try (ServerSocket server = new ServerSocket(port)){
            // 著名的死循环..
            while (true) {
                // 单个请求..循环内，不影响下一个请求
                try (Socket connection = server.accept()){
                    Writer out = new OutputStreamWriter(connection.getOutputStream());
                    Date now = new Date();
                    out.write(now.toString() +"\r\n");
                    out.flush();
                } catch (IOException ex) {
                    logger.error("..这个请求挂了..");
                }
            }
        } catch (IOException ex) {
            logger.error("整个服务挂了.."+ex.getMessage());
        }
    }

    /**
     * 时间服务器：遵循 RFC 868 时间协议的迭代时间服务器
     */
    public void timeServer() {
        int port = 1337;
        long differenceBetweenEpochs = 2208988800L;
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try (Socket connection = server.accept()) {
                    OutputStream out = connection.getOutputStream();
                    Date now = new Date();
                    long msSince1970 = now.getTime();
                    long secondsSince1970 = msSince1970/1000;
                    long secondsSince1900 = secondsSince1970
                            + differenceBetweenEpochs;
                    byte[] time = new byte[4];
                    time[0]
                            = (byte) ((secondsSince1900 & 0x00000000FF000000L) >> 24);
                    time[1]
                            = (byte) ((secondsSince1900 & 0x0000000000FF0000L) >> 16);
                    time[2]
                            = (byte) ((secondsSince1900 & 0x000000000000FF00L) >> 8);
                    time[3] = (byte) (secondsSince1900 & 0x00000000000000FFL);
                    out.write(time);
                    out.flush();
                } catch (IOException ex) {
                    logger.error("..这个请求挂了..");
                }
            }
        } catch (IOException ex) {
            logger.error("整个服务挂了.."+ex.getMessage());
        }

    }



    public static void main(String[] args) {
        ServerSocketDemo demo = new ServerSocketDemo();
        demo.dayTimeServer();

    }


}
