package top.kaoshanji.leaning.jdkx.io.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * URI 示例
 * @author kaoshanji
 * @time 2020/1/15 15:38
 */
public class URIDemo {

    private final static Logger logger = LoggerFactory.getLogger(URIDemo.class);


    /**
     * 下载一个对象
     * @param url
     * @throws IOException
     */
    public void contentGetter(String url) throws IOException {
        URL u = new URL(url);
        Object o = u.getContent();
        logger.info("I got a " + o.getClass().getName());
    }

    /**
     * 下载一个Web页面
     * @param url
     */
    public void sourceViewer(String url) {
        InputStream in = null;

        try {
            // 打开 URL 进行读取
            URL u = new URL(url);
            in = u.openStream();

            // 缓冲输入以提高性能
            in = new BufferedInputStream(in);

            // 将 InputStream 串链到一个 Reader
            Reader r = new InputStreamReader(in);
            int c;
            while ((c = r.read()) != -1) {
                logger.info(String.valueOf((char)c));
            }

        } catch (MalformedURLException ex) {
            logger.error(url + " 不是一个 URL 协议");
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }


    /**
     * 虚拟机支持那些协议?
     */
    public void prorocol() {

        // hypertext transfer protocol
        testProtocol("http://www.adc.org");

        // secure http
        testProtocol("https://www.amazon.com/exec/obidos/order2/");

        // file transfer protocol
        testProtocol("ftp://ibiblio.org/pub/languages/java/javafaq/");

        // Simple Mail Transfer Protocol
        testProtocol("mailto:elharo@ibiblio.org");

        // telnet
        testProtocol("telnet://dibner.poly.edu/");

        // local file access
        testProtocol("file:///etc/passwd");

        // gopher
        testProtocol("gopher://gopher.anc.org.za/");

        // Lightweight Directory Access Protocol
        testProtocol(
                "ldap://ldap.itd.umich.edu/o=University%20of%20Michigan,c=US?postalAddress");

        // JAR
        testProtocol(
                "jar:http://cafeaulait.org/books/javaio/ioexamples/javaio.jar!"
                        + "/com/macfaq/io/StreamCopier.class");

        // NFS, Network File System
        testProtocol("nfs://utopia.poly.edu/usr/tmp/");

        // a custom protocol for JDBC 由 java.sql支持
        testProtocol("jdbc:mysql://luna.ibiblio.org:3306/NEWS");

        // rmi, a custom protocol for remote method invocation 由java.rmi支持
        testProtocol("rmi://ibiblio.org/RenderEngine");

        // custom protocols for HotJava
        testProtocol("doc:/UsersGuide/release.html");
        testProtocol("netdoc:/UsersGuide/release.html");
        testProtocol("systemresource://www.adc.org/+/index.html");
        testProtocol("verbatim:http://www.adc.org/");

    }


    private void testProtocol(String url) {
        try {
            URL u = new URL(url);
            System.out.println(u.getProtocol() + " is supported");
        } catch (MalformedURLException ex) {
            String protocol = url.substring(0, url.indexOf(':'));
            System.out.println(protocol + " is not supported");
        }
    }


}