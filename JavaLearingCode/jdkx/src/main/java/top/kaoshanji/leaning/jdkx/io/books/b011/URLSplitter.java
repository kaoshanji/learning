package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URL的组成部分
 * @author kaoshanji
 * @time 2020/2/5 下午8:31
 */
public class URLSplitter {

    public static void main (String[] args) {

        // web 页面地址
        String url = "";

        try {
            URL u = new URL(url);
            System.out.println("The URL is " + u);
            System.out.println("The scheme is " + u.getProtocol());
            System.out.println("The user info is " + u.getUserInfo());

            String host = u.getHost();
            if (host != null) {
                int atSign = host.indexOf('@');
                if (atSign != -1) host = host.substring(atSign+1);
                System.out.println("The host is " + host);
            } else {
                System.out.println("The host is null.");
            }

            System.out.println("The port is " + u.getPort());
            System.out.println("The path is " + u.getPath());
            System.out.println("The ref is " + u.getRef());
            System.out.println("The query string is " + u.getQuery());
        } catch (MalformedURLException ex) {
            System.err.println(url + " is not a URL I understand.");
        }
        System.out.println();
    }


}
