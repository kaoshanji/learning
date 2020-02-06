package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 下载一个 Web 页面
 * @author kaoshanji
 * @time 2020/2/5 下午8:17
 */
public class SourceViewer {

    public static void main (String[] args) {

        // web 页面地址
        String url = "";

        InputStream in = null;
        try {
            // 打开URL进行读取
            URL u = new URL(url);
            in = u.openStream();

            // 缓存输入以提高性能
            in = new BufferedInputStream(in);
            // 将 InputStream 串链到一个 Reader
            Reader r = new InputStreamReader(in);
            int c;
            while ((c = r.read()) != -1) {
                System.out.print((char) c);
            }
        } catch (MalformedURLException ex) {
            System.err.println(args[0] + " is not a parseable URL");
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }


}
