package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * @author kaoshanji
 * @time 2020-01-20 21:53
 */
public class DaytimeTask implements Callable<Void> {

    private final static Logger logger = LoggerFactory.getLogger(DaytimeTask.class);

    private Socket connection;

    public DaytimeTask(Socket connection) {
        this.connection = connection;
    }

    @Override
    public Void call() {
        try {
            // 处理逻辑...
            logger.info("线程池版...其实是一样的....");
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
        return null;
    }
}
