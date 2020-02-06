package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JHTTP Web 服务器
 * @author kaoshanji
 * @time 2020/2/6 下午6:17
 */
public class JHTTP {

    private final static Logger logger = LoggerFactory.getLogger(JHTTP.class);

    private static final int NUM_THREADS = 50;
    private static final String INDEX_FILE = "index.html";

    private final File rootDirectory;
    private final int port;

    public JHTTP(File rootDirectory, int port) throws IOException {

        if (!rootDirectory.isDirectory()) {
            throw new IOException(rootDirectory + " does not exist as a directory");
        }
        this.rootDirectory = rootDirectory;
        this.port = port;
    }

    public void start() throws IOException {

        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("Accepting connections on port " + server.getLocalPort());
            logger.info("Document Root: " + rootDirectory);

            while (true) {
                try {
                    Socket request = server.accept();
                    Runnable r = new RequestProcessor(rootDirectory, INDEX_FILE, request);
                    pool.submit(r);
                } catch (IOException ex) {
                    logger.error("Error accepting connection："+ex.getLocalizedMessage());
                }
            }
        }
    }

    public static void main(String[] args) {

        // get the Document root
        File docroot;
        try {
            docroot = new File(args[0]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Usage: java JHTTP docroot port");
            return;
        }

        // set the port to listen on
        int port;
        try {
            port = Integer.parseInt(args[1]);
            if (port < 0 || port > 65535) port = 80;
        } catch (RuntimeException ex) {
            port = 80;
        }

        try {
            JHTTP webserver = new JHTTP(docroot, port);
            webserver.start();
        } catch (IOException ex) {
            logger.error("Server could not start："+ex.getLocalizedMessage());
        }
    }

}
