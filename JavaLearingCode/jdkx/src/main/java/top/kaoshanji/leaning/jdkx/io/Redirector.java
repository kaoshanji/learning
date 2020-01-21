package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * 多线程版 重定向 HTTP 服务器
 * 无论你从那里来，我都转到指定的网站去
 * @author kaoshanji
 * @time 2020-01-21 16:29
 */
public class Redirector {

    private final static Logger logger = LoggerFactory.getLogger(Redirector.class);

    private final int port;
    private final String newSite;

    public Redirector(int port, String newSite) {
        this.port = port;
        this.newSite = newSite;
    }

    public void start() {
        try (ServerSocket server = new ServerSocket(port)){
            logger.info("重定向连接的端口：" + server.getLocalPort() + " 到 " + newSite);

            while (true) {
                try {
                    Socket s = server.accept();
                    Thread t = new RedirectThread(s);
                    t.start();
                } catch (IOException ex) {
                    logger.error("接入连接异常："+ex.getMessage());
                } catch (RuntimeException ex) {
                    logger.error("不可修复异常："+ex.getMessage());
                }
            }

        } catch (BindException ex) {
            logger.error("不能启动服务："+ex.getMessage());
        } catch (IOException ex) {
            logger.error("打开 socket 失败：" + ex.getMessage());
        }
    }


    private class RedirectThread extends Thread {
        private final Socket connection;

        RedirectThread(Socket s) {
            this.connection = s;
        }

        @Override
        public void run() {
            try {
                Writer out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "US-ASCII"));
                Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()));

                StringBuilder request = new StringBuilder(80);
                while (true) {
                    int c = in.read();
                    if (c == '\r' || c == '\n' || c == -1) break;
                    request.append((char) c);
                }

                String get = request.toString();
                String [] pieces = get.split("\\w*");
                String theFile = pieces[1];

                if (get.indexOf("HTTP") != -1) {
                    out.write("HTTP/1.0 302 FOUND\r\n");
                    Date now = new Date();
                    out.write("Date: " + now + "\r\n");
                    out.write("Server: Redirector 1.1\r\n");
                    out.write("Location: " + newSite + theFile + "\r\n");
                    out.write("Content-type: text/html\r\n\r\n");
                    out.flush();
                }

                // 不支持重定向的浏览器给出转到那里
                out.write("<HTML><HEAD><TITLE>Document moved</TITLE></HEAD>\r\n");
                out.write("<BODY><H1>Document moved</H1>\r\n");
                out.write("The document " + theFile
                        + " has moved to\r\n<A HREF=\"" + newSite + theFile + "\">"
                        + newSite  + theFile
                        + "</A>.\r\n Please update your bookmarks<P>");
                out.write("</BODY></HTML>\r\n");
                out.flush();

                logger.info("重定向："+connection.getRemoteSocketAddress());
            } catch (IOException ex) {
                logger.error("任务异常：" + connection.getRemoteSocketAddress());
            } finally {
                try {
                    connection.close();
                } catch (IOException ex) {}
            }


        }
    }

}
