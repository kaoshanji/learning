package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 下载一个对象
 * @author kaoshanji
 * @time 2020/2/5 下午8:27
 */
public class ContentGetter {


    public static void main(String[] args) {
        // web 页面地址
        String url = "";

        // 打开 URL 进行读取
        try {
            URL u = new URL(url);
            Object o = u.getContent();
            System.out.println("I got a " + o.getClass().getName());
        } catch (MalformedURLException ex) {
            System.err.println(args[0] + " is not a parseable URL");
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

}
