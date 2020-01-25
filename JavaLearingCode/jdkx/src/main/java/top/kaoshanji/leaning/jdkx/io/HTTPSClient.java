package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

/**
 * HTTPSClient
 * @author kaoshanji
 * @time 2020-01-22 14:15
 */
public class HTTPSClient {

    private final static Logger logger = LoggerFactory.getLogger(HTTPSClient.class);

    public static void main(String[] args) {

        int port = 443; // 默认端口
        String host = ""; // 远程主机

        SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket socket = null;

        try {
            socket = (SSLSocket)factory.createSocket(host, port);

            // 启用套件
            String [] supported = socket.getSupportedCipherSuites();
            socket.setEnabledCipherSuites(supported);

            Writer out = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            // https requires the full URL in the GET line
            out.write("GET http://" + host + "/ HTTP/1.1\r\n");
            out.write("Host: " + host + "\r\n");
            out.write("\r\n");
            out.flush();

            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // read the header
            String s;
            while (!(s = in.readLine()).equals("")) {
                System.out.println(s);
            }
            System.out.println();

            // read the length
            String contentLength = in.readLine();
            int length = Integer.MAX_VALUE;
            try {
                length = Integer.parseInt(contentLength.trim(), 16);
            } catch (NumberFormatException ex) {
                // This server doesn't send the content-length
                // in the first line of the response body
            }
            logger.info("contentLength："+contentLength);

            int c;
            int i = 0;
            while ((c = in.read()) != -1 && i++ < length) {
                System.out.write(c);
            }

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {}
        }




    }


}
