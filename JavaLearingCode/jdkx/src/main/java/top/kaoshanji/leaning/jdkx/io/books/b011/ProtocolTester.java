package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 虚拟机支持哪些协议?
 * @author kaoshanji
 * @time 2020/2/5 下午8:02
 */
public class ProtocolTester {

    public static void main(String[] args) {

        // HTTP 超文本传输协议
        testProtocol("http://www.adc.org");

        // HTTPS 安全http
        testProtocol("https://www.amazon.com/exec/obidos/order2/");

        // 文件传输协议
        testProtocol("ftp://ibiblio.org/pub/languages/java/javafaq/");

        // 简单邮件传输协议
        testProtocol("mailto:elharo@ibiblio.org");

        // telnet
        testProtocol("telnet://dibner.poly.edu/");

        // 本地文件访问
        testProtocol("file:///etc/passwd");

        // gopher
        testProtocol("gopher://gopher.anc.org.za/");

        // 轻量组目录访问协议
        testProtocol(
                "ldap://ldap.itd.umich.edu/o=University%20of%20Michigan,c=US?postalAddress");

        // JAR
        testProtocol(
                "jar:http://cafeaulait.org/books/javaio/ioexamples/javaio.jar!"
                        + "/com/macfaq/io/StreamCopier.class");

        // NFS, 网络文件系统
        testProtocol("nfs://utopia.poly.edu/usr/tmp/");

        // JDBC 定制协议 需要 java.sql 包
        testProtocol("jdbc:mysql://luna.ibiblio.org:3306/NEWS");

        // rmi, 远程方法调用的定制协议 需要 java.rmi 包
        testProtocol("rmi://ibiblio.org/RenderEngine");

        // HotJava 定制协议
        testProtocol("doc:/UsersGuide/release.html");
        testProtocol("netdoc:/UsersGuide/release.html");
        testProtocol("systemresource://www.adc.org/+/index.html");
        testProtocol("verbatim:http://www.adc.org/");
    }



    private static void testProtocol(String url) {
        try {
            URL u = new URL(url);
            System.out.println(u.getProtocol() + " is supported");
        } catch (MalformedURLException ex) {
            String protocol = url.substring(0, url.indexOf(':'));
            System.out.println(protocol + " is not supported");
        }
    }

}
