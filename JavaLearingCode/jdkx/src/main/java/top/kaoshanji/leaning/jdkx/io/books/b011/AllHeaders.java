package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 显示整个HTTP首部
 * @author kaoshanji
 * @time 2020/2/5 下午10:27
 */
public class AllHeaders {

    public static void main(String[] args) {

        // web 页面地址
        String url = "https://www.jd.com/";

        try {
            URL u = new URL(url);
            URLConnection uc = u.openConnection();
            for (int j = 1; ; j++) {
                String header = uc.getHeaderField(j);
                if (header == null) break;
                System.out.println(uc.getHeaderFieldKey(j) + ": " + header);
            }
        } catch (MalformedURLException ex) {
            System.err.println(url + " is not a URL I understand.");
        } catch (IOException ex) {
            System.err.println(ex);
        }
        System.out.println();

    }

}
