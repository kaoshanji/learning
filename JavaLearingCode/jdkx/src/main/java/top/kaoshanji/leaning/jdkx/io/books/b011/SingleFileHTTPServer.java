package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 提供同一个文件的HTTP服务器
 * 线程池
 * @author kaoshanji
 * @time 2020/2/6 下午4:56
 */
public class SingleFileHTTPServer {

    private final static Logger logger = LoggerFactory.getLogger(SingleFileHTTPServer.class);

    private final byte[] content;
    private final byte[] header;
    private final int port;
    private final String encoding;

    public SingleFileHTTPServer(String data, String encoding, String mimeType, int port) throws UnsupportedEncodingException {
        this(data.getBytes(encoding), encoding, mimeType, port);
    }

    public SingleFileHTTPServer(byte[] data, String encoding, String mimeType, int port) {
        this.content = data;
        this.port = port;
        this.encoding = encoding;
        String header = "HTTP/1.0 200 OK\r\n"
                + "Server: OneFile 2.0\r\n"
                + "Content-length: " + this.content.length + "\r\n"
                + "Content-type: " + mimeType + "; charset=" + encoding + "\r\n\r\n";
        this.header = header.getBytes(Charset.forName("US-ASCII"));
    }

    // 中转一下
    // 提供线程池、启动服务监听
    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        try (ServerSocket server = new ServerSocket(this.port)) {
            logger.info("Accepting connections on port " + server.getLocalPort());
            logger.info("Data to be sent:"+this.content + ", encoding:"+encoding);

            while (true) {
                try {
                    // 来活了..
                    Socket connection = server.accept();

                    // 具体干活
                    pool.submit(new HTTPHandler(connection));
                } catch (IOException ex) {
                    logger.error("Exception accepting connection："+ex.getLocalizedMessage());
                } catch (RuntimeException ex) {
                    logger.error("Unexpected error："+ex.getLocalizedMessage());
                }
            }
        } catch (IOException ex) {
            logger.error("Could not start server："+ex.getLocalizedMessage());
        }
    }

    // 具体逻辑都是在线程里处理
    private class HTTPHandler implements Callable<Void> {
        private final Socket connection;

        HTTPHandler(Socket connection) {
            this.connection = connection;
        }

        @Override
        public Void call() throws IOException {
            try {
                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                InputStream in = new BufferedInputStream(connection.getInputStream());

                // 就第一行就行了，请求地址..
                StringBuilder request = new StringBuilder(80);
                while (true) {
                    int c = in.read();
                    if (c == '\r' || c == '\n' || c == -1) break;
                    request.append((char) c);
                }
                // If this is HTTP/1.0 or later send a MIME header
                if (request.toString().indexOf("HTTP/") != -1) {
                    out.write(header);
                }
                out.write(content);
                out.flush();
            } catch (IOException ex) {
                logger.error("Error writing to client:" + ex.getLocalizedMessage());
            } finally {
                connection.close();
            }
            return null;
        }
    }

    public static void main(String[] args) {

        // set the port to listen on
        int port;
        try {
            port = Integer.parseInt(args[1]);
            if (port < 1 || port > 65535) port = 80;
        } catch (RuntimeException ex) {
            port = 80;
        }

        String encoding = "UTF-8";
        if (args.length > 2) encoding = args[2];

        try {
            Path path = Paths.get(args[0]);;
            byte[] data = Files.readAllBytes(path);

            String contentType = URLConnection.getFileNameMap().getContentTypeFor(args[0]);

            // 响应的文件数据已经被写死了..
            SingleFileHTTPServer server = new SingleFileHTTPServer(data, encoding, contentType, port);
            server.start();

        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Usage: java SingleFileHTTPServer filename port encoding");
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage());
        }
    }


}
