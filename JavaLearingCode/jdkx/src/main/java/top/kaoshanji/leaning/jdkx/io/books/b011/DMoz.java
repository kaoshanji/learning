package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 发送搜索GET请求
 * @author kaoshanji
 * @time 2020/2/5 下午9:08
 */
public class DMoz {

    public static void main(String[] args) {

        String query = "";
        try {
            URL u = new URL("http://www.dmoz.org/search/q?" + query);
            try (InputStream in = new BufferedInputStream(u.openStream())) {
                InputStreamReader theHTML = new InputStreamReader(in);
                int c;
                while ((c = theHTML.read()) != -1) {
                    System.out.print((char) c);
                }
            }
        } catch (MalformedURLException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }


}
