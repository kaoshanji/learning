package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Date;

/**
 * @author kaoshanji
 * @time 2020-01-21 16:50
 */
public class RequestProcessor implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    private File rootDirectory;
    private String indexFileName = "index.html";
    private Socket connection;

    public RequestProcessor(File rootDirectory, String indexFileName, Socket connection) {

        if (rootDirectory.isFile()) {
            throw new IllegalArgumentException("rootDirectory 必须是一个目录，而不是文件 ");
        }

        try {
            rootDirectory = rootDirectory.getCanonicalFile();
        } catch (IOException ex) {}

        this.rootDirectory = rootDirectory;

        if (indexFileName != null) {
            this.indexFileName = indexFileName;
        }
        this.connection = connection;
    }

    @Override
    public void run() {
        // 处理逻辑...
        String root = rootDirectory.getPath();

        try {
            // 也许是二进制文件
            OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
            Writer out = new OutputStreamWriter(raw);
            Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()), "US-ASCII");

            StringBuilder requestLine = new StringBuilder();
            while (true) {
                int c = in.read();
                if (c == '\r' || c == '\n') break;
                requestLine.append((char) c);
            }

            String get = requestLine.toString();
            logger.info("获得：" + connection.getRemoteSocketAddress());

            String [] tokens = get.split("\\s+");
            String method = tokens[0];
            String version = "";

            if (!method.equals("GET")) {
                String body = new StringBuilder("<HTML>\r\n")
                        .append("<HEAD><TITLE>Not Implemented</TITLE>\r\n")
                        .append("</HEAD>\r\n")
                        .append("<BODY>")
                        .append("<H1>HTTP Error 501: Not Implemented</H1>\r\n")
                        .append("</BODY></HTML>\r\n").toString();
                if (version.startsWith("HTTP/")) { // send a MIME header
                    sendHeader(out, "HTTP/1.0 501 Not Implemented",
                            "text/html; charset=utf-8", body.length());
                }
                out.write(body);
                out.flush();
            }

            if (method.equals("GET")) {
                String fileName = tokens[1];
                if (fileName.endsWith("/")) {
                    fileName += indexFileName;
                }
                String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
                if (tokens.length > 2) {
                    version = tokens[2];
                }

                File theFile = new File(rootDirectory, fileName.substring(1, fileName.length()));

                // 不能访问或者不是从根目录下进来的..
                if (!theFile.canRead() || !theFile.getCanonicalPath().startsWith(root)) {
                    String body = new StringBuilder("<HTML>\r\n")
                            .append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
                            .append("</HEAD>\r\n")
                            .append("<BODY>")
                            .append("<H1>HTTP Error 404: File Not Found</H1>\r\n")
                            .append("</BODY></HTML>\r\n").toString();
                    if (version.startsWith("HTTP/")) { // send a MIME header
                        sendHeader(out, "HTTP/1.0 404 File Not Found",
                                "text/html; charset=utf-8", body.length());
                    }
                    out.write(body);
                    out.flush();
                }

                // 能够访问，并且是在根目录之下..
                if (theFile.canRead() && theFile.getCanonicalPath().startsWith(root)) {
                    byte [] theData = Files.readAllBytes(theFile.toPath());
                    if (version.startsWith("HTTP/")) {
                        sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
                    }

                    raw.write(theData);
                    raw.flush();
                }
            }


        } catch (IOException ex) {
            logger.error("异常任务：" + connection.getRemoteSocketAddress() + ex.getMessage());
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {}
        }
    }

    private void sendHeader(Writer out, String responseCode, String contentType, int length)
            throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: JHTTP 2.0\r\n");
        out.write("Content-length: " + length + "\r\n");
        out.write("Content-type: " + contentType + "\r\n\r\n");
        out.flush();
    }
}
