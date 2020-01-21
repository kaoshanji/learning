package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Date;

/**
 * @author kaoshanji
 * @time 2020-01-20 21:19
 */
public class DayTimeThread extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(DayTimeThread.class);

    private Socket connection;

    public DayTimeThread(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            // 处理逻辑...
            logger.info("多线程版....并没有什么特别...");
            Writer out = new OutputStreamWriter(connection.getOutputStream());
            Date now = new Date();
            out.write(now.toString() +"\r\n");
            out.flush();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                ////
            }
        }
    }


}
