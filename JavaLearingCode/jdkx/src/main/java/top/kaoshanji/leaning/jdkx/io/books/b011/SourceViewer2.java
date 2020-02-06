package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 用 URLConnection 下载一个Web页面
 * @author kaoshanji
 * @time 2020/2/5 下午9:59
 */
public class SourceViewer2 {

    public static void main (String[] args) {

        // web 页面地址
        String url = "";

        try {
            // 打开 URLConnection 进行读取
            URL u = new URL(url);

            // 提供了对 HTTP 首部的访问
            // 配置发送给服务器的请求参数
            // 不仅可以读还可以写数据
            URLConnection uc = u.openConnection();

            try (InputStream raw = uc.getInputStream()) { // 自动关闭
                InputStream buffer = new BufferedInputStream(raw);
                // chain the InputStream to a Reader
                Reader reader = new InputStreamReader(buffer);
                int c;
                while ((c = reader.read()) != -1) {
                    System.out.print((char) c);
                }
            }

        } catch (MalformedURLException ex) {
            System.err.println(args[0] + " is not a parseable URL");
        } catch (IOException ex) {
            System.err.println(ex);
        }



    }

}
