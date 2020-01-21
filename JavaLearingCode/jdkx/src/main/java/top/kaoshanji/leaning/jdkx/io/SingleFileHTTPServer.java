package top.kaoshanji.leaning.jdkx.io;

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
 * 线程池版的 单文件 HTTP服务器
 * @author kaoshanji
 * @time 2020-01-21 14:25
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

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(100);

        try (ServerSocket server = new ServerSocket(this.port)){
            logger.info("在" + server.getLocalPort() + " 上接入连接..");
            logger.info("发送数据：" + new String(this.content, encoding));

            while (true) {
                try {

                    // 接入连接Socket交给线程池..
                    Socket connection = server.accept();
                    // 线程池里面的线程处理逻辑..
                    pool.submit(new HTTPHandler(connection));

                } catch (IOException ex) {
                    logger.error("接入连接异常："+ex.getMessage());
                } catch (RuntimeException ex) {
                    logger.error("未知异常："+ex.getMessage());
                }
            }

        } catch (IOException ex) {
            logger.error("不能启动服务："+ex.getMessage());
        }
    }

    private class HTTPHandler implements Callable<Void> {
        private final Socket connection;

        public HTTPHandler(Socket connection) {
            this.connection = connection;
        }

        @Override
        public Void call() throws Exception {
            try {
                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                InputStream in = new BufferedInputStream(connection.getInputStream());

                // 读取第一行就行..
                StringBuilder request = new StringBuilder(80);
                while (true) {
                    int c = in.read();
                    if (c == '\r' || c == '\n' || c == -1) break;
                    request.append((char) c);
                }

                if (request.toString().indexOf("HTTP/") != -1) {
                    out.write(header);
                }

            } catch (IOException ex) {
                logger.error("等待客户端异常.."+ex.getMessage());
            } finally {
                connection.close();
            }

            return null;
        }
    }

    public static void main(String[] args) {

        int port = 80;
        String encoding = "UTF-8";
        String fileStr = "";

        try {
            Path path = Paths.get(fileStr);
            byte [] data = Files.readAllBytes(path);

            String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileStr);
            SingleFileHTTPServer server = new SingleFileHTTPServer(data, encoding, contentType, port);

            server.start();

        } catch (ArrayIndexOutOfBoundsException ex) {
            logger.error("Usage: java SingleFileHTTPServer filename port encoding");
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }


}
