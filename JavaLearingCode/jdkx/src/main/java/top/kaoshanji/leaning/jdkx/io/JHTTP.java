package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池版 JHTTP Web 服务器
 * @author kaoshanji
 * @time 2020-01-21 17:17
 */
public class JHTTP {

    private final static Logger logger = LoggerFactory.getLogger(JHTTP.class);

    private static final int NUM_THREADS = 50;
    private static final String INDEX_FILE = "index.html";

    private final File rootDirectory;
    private final int port;

    public JHTTP(File rootDirectory, int port) throws IOException {
        if (!rootDirectory.isDirectory()) {
            throw new IOException(rootDirectory + "不是一个文件夹");
        }
        this.rootDirectory = rootDirectory;
        this.port = port;
    }

    public void start() throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(port)){
            logger.info("端口：" + server.getLocalPort() + " 接入连接");
            logger.info("文件根目录：" + rootDirectory);

            while (true) {
                try {
                    Socket request = server.accept();
                    Runnable r = new RequestProcessor(rootDirectory, INDEX_FILE, request);
                    pool.submit(r);
                } catch (IOException ex) {
                    logger.error("接入连接异常："+ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {

        String fileStr = "";
        File docroot = new File(fileStr);

        int port = 80;

        try {
            JHTTP webserver = new JHTTP(docroot, port);
            webserver.start();
        } catch (IOException ex) {
            logger.error("服务不能启动："+ex.getMessage());
        }
    }
}
