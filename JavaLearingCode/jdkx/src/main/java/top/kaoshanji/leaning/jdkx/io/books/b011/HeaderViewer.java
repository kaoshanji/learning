package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * 返回首部
 * @author kaoshanji
 * @time 2020/2/5 下午10:22
 */
public class HeaderViewer {

    public static void main(String[] args) {

        // web 页面地址
        String url = "";

        try {
            URL u = new URL(url);
            URLConnection uc = u.openConnection();

            // 编码
            System.out.println("Content-type: " + uc.getContentType());
            if (uc.getContentEncoding() != null) {
                System.out.println("Content-encoding: " + uc.getContentEncoding());
            }

            // 发送时间
            if (uc.getDate() != 0) {
                System.out.println("Date: " + new Date(uc.getDate()));
            }

            // 最后修改日期
            if (uc.getLastModified() != 0) {
                System.out.println("Last modified: " + new Date(uc.getLastModified()));
            }

            // 过期时间
            if (uc.getExpiration() != 0) {
                System.out.println("Expiration date: " + new Date(uc.getExpiration()));
            }
            if (uc.getContentLength() != -1) {
                System.out.println("Content-length: " + uc.getContentLength());
            }
        } catch (MalformedURLException ex) {
            System.err.println(url + " is not a URL I understand");
        } catch (IOException ex) {
            System.err.println(ex);
        }
        System.out.println();

    }


}
