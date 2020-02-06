package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Date;

/**
 * 处理HTTP请求的runnable类
 * @author kaoshanji
 * @time 2020/2/6 下午5:45
 */
public class RequestProcessor implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    private File rootDirectory;
    private String indexFileName = "index.html";
    private Socket connection;

    public RequestProcessor(File rootDirectory, String indexFileName, Socket connection) {

        if (rootDirectory.isFile()) {
            throw new IllegalArgumentException("rootDirectory must be a directory, not a file");
        }
        try {
            rootDirectory = rootDirectory.getCanonicalFile();
        } catch (IOException ex) {
        }
        this.rootDirectory = rootDirectory;

        if (indexFileName != null) this.indexFileName = indexFileName;
        this.connection = connection;
    }

    @Override
    public void run() {
        // for security checks
        String root = rootDirectory.getPath();
        try {
            OutputStream raw = new BufferedOutputStream(connection.getOutputStream());

            Writer out = new OutputStreamWriter(raw);
            Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()),"US-ASCII");

            StringBuilder requestLine = new StringBuilder();
            while (true) {
                int c = in.read();
                if (c == '\r' || c == '\n') break;
                requestLine.append((char) c);
            }

            String get = requestLine.toString();

            logger.info(connection.getRemoteSocketAddress() + " " + get);

            String[] tokens = get.split("\\s+");

            // 解析参数..
            String method = tokens[0];

            // 文件名称
            String fileName = tokens[1];
            if (fileName.endsWith("/")) {
                fileName += indexFileName;
            }

            // HTTP 版本
            String version = "";
            if (tokens.length > 2) {
                version = tokens[2];
            }

            // 不是 GET 请求
            if (!method.equals("GET")) {
                String body = new StringBuilder("<HTML>\r\n")
                        .append("<HEAD><TITLE>Not Implemented</TITLE>\r\n")
                        .append("</HEAD>\r\n")
                        .append("<BODY>")
                        .append("<H1>HTTP Error 501: Not Implemented</H1>\r\n")
                        .append("</BODY></HTML>\r\n").toString();

                if (version.startsWith("HTTP/")) { // send a MIME header
                    sendHeader(out, "HTTP/1.0 501 Not Implemented", "text/html; charset=utf-8", body.length());
                }
                out.write(body);
                out.flush();
            }

            File theFile = new File(rootDirectory, fileName.substring(1, fileName.length()));
            if (!theFile.canRead() || theFile.getCanonicalPath().startsWith(root)) {
                String body = new StringBuilder("<HTML>\r\n")
                        .append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
                        .append("</HEAD>\r\n")
                        .append("<BODY>")
                        .append("<H1>HTTP Error 404: File Not Found</H1>\r\n")
                        .append("</BODY></HTML>\r\n").toString();

                if (version.startsWith("HTTP/")) { // send a MIME header
                    sendHeader(out, "HTTP/1.0 404 File Not Found", "text/html; charset=utf-8", body.length());
                }

                out.write(body);
                out.flush();
            }


            if (method.equals("GET")) {

                // 字符流写入响应头
                // 字节流写入响应数据

                String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);

                // 文件内容
                byte[] theData = Files.readAllBytes(theFile.toPath());
                if (version.startsWith("HTTP/")) { // send a MIME header
                    sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
                }

                // 响应数据
                raw.write(theData);
                raw.flush();

            }

        } catch (IOException ex) {
            logger.error("Error talking to " + connection.getRemoteSocketAddress() + ","+ex.getLocalizedMessage());

        } finally {
            try {
                connection.close();
            }
            catch (IOException ex) {}
        }
    }

    // 添加响应头
    private void sendHeader(Writer out, String responseCode, String contentType, int length)
            throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: JHTTP 2.0\r\n");
        out.write("Content-length: " + length + "\r\n");
        out.write("Content-type: " + contentType + "\r\n\r\n");
        // out.flush(); 应该去掉??重复响应会异常
    }
}
