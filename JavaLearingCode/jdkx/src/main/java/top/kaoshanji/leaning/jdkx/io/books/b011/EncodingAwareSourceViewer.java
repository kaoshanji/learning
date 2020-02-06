package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 用正确的字符集下载一个Web页面
 * @author kaoshanji
 * @time 2020/2/5 下午10:06
 */
public class EncodingAwareSourceViewer {

    public static void main (String[] args) {
        for (int i = 0; i < args.length; i++) {
            try {
                // set default encoding
                String encoding = "ISO-8859-1";
                URL u = new URL(args[i]);
                URLConnection uc = u.openConnection();

                // 获取页面的编码
                String contentType = uc.getContentType();
                int encodingStart = contentType.indexOf("charset=");
                if (encodingStart != -1) {
                    encoding = contentType.substring(encodingStart + 8);
                }
                InputStream in = new BufferedInputStream(uc.getInputStream());

                // 使用页面的编码下载页面，就不会乱码
                Reader r = new InputStreamReader(in, encoding);
                int c;
                while ((c = r.read()) != -1) {
                    System.out.print((char) c);
                }
                r.close();
            } catch (MalformedURLException ex) {
                System.err.println(args[0] + " is not a parseable URL");
            } catch (UnsupportedEncodingException ex) {
                System.err.println(
                        "Server sent an encoding Java does not support: " + ex.getMessage());
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }

}
