package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * HTTP重定向器
 * 多线程
 * @author kaoshanji
 * @time 2020/2/6 下午5:11
 */
public class Redirector {

    private final static Logger logger = LoggerFactory.getLogger(Redirector.class);

    private final int port;
    private final String newSite;

    public Redirector(String newSite, int port) {
        this.port = port;
        this.newSite = newSite;
    }

    public void start() {
        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("Redirecting connections on port " + server.getLocalPort() + " to " + newSite);

            while (true) {
                try {
                    Socket s = server.accept();
                    Thread t = new RedirectThread(s);
                    t.start();
                } catch (IOException ex) {
                    logger.error("Exception accepting connection");
                } catch (RuntimeException ex) {
                    logger.error("Unexpected error, " + ex.getLocalizedMessage());
                }
            }
        } catch (BindException ex) {
            logger.error("Could not start server., " + ex.getLocalizedMessage());
        } catch (IOException ex) {
            logger.error("Error opening server socket, " + ex.getLocalizedMessage());
        }
    }

    private class RedirectThread extends Thread {

        private final Socket connection;

        RedirectThread(Socket s) {
            this.connection = s;
        }

        public void run() {
            try {
                Writer out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "US-ASCII"));
                Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()));

                // read the first line only; that's all we need
                StringBuilder request = new StringBuilder(80);
                while (true) {
                    int c = in.read();
                    if (c == '\r' || c == '\n' || c == -1) break;
                    request.append((char) c);
                }

                String get = request.toString();
                String[] pieces = get.split("\\w*");
                String theFile = pieces[1];

                // If this is HTTP/1.0 or later send a MIME header
                if (get.indexOf("HTTP") != -1) {
                    out.write("HTTP/1.0 302 FOUND\r\n");
                    Date now = new Date();
                    out.write("Date: " + now + "\r\n");
                    out.write("Server: Redirector 1.1\r\n");
                    out.write("Location: " + newSite + theFile + "\r\n");
                    out.write("Content-type: text/html\r\n\r\n");
                    out.flush();
                } else {
                    // Not all browsers support redirection so we need to
                    // produce HTML that says where the document has moved to.
                    out.write("<HTML><HEAD><TITLE>Document moved</TITLE></HEAD>\r\n");
                    out.write("<BODY><H1>Document moved</H1>\r\n");
                    out.write("The document " + theFile
                            + " has moved to\r\n<A HREF=\"" + newSite + theFile + "\">"
                            + newSite  + theFile
                            + "</A>.\r\n Please update your bookmarks<P>");
                    out.write("</BODY></HTML>\r\n");
                    out.flush();
                }

                logger.info("Redirected " + connection.getRemoteSocketAddress());
            } catch(IOException ex) {
                logger.error("Error talking to " + connection.getRemoteSocketAddress());
            } finally {
                try {
                    connection.close();
                } catch (IOException ex) {}
            }
        }
    }

    public static void main(String[] args) {

        int thePort;
        String theSite;

        try {
            theSite = args[0];
            // trim trailing slash
            if (theSite.endsWith("/")) {
                theSite = theSite.substring(0, theSite.length() - 1);
            }
        } catch (RuntimeException ex) {
            System.out.println("Usage: java Redirector http://www.newsite.com/ port");
            return;
        }

        try {
            thePort = Integer.parseInt(args[1]);
        } catch (RuntimeException ex) {
            thePort = 80;
        }

        Redirector redirector = new Redirector(theSite, thePort);
        redirector.start();
    }


}
