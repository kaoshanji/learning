package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 多线程 dayTime服务器
 * 为每个入站连接生成一个新线程处理
 * @author kaoshanji
 * @time 2020-01-20 21:44
 */
public class MultithreadedDaytimeServer {

    private final static Logger logger = LoggerFactory.getLogger(MultithreadedDaytimeServer.class);

    private final static int PORT = 1313;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)){
            while (true) {
                // 单个请求需要手动处理异常..
              try {
                  Socket connection = server.accept();
                  Thread task = new DayTimeThread(connection);
                  task.start();
              } catch (IOException ex) {
                  logger.error(ex.getMessage());
              }
            }
        } catch (IOException ex) {
            logger.error("不能启动服务..");
        }
    }


}
