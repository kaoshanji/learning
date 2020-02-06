package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 包含响应码和消息的SourceViewer
 * @author kaoshanji
 * @time 2020/2/5 下午10:48
 */
public class SourceViewer3 {

    public static void main (String[] args) {
        for (int i = 0; i < args.length; i++) {
            try {
                // 打开 URLConnection 进行读取
                URL u = new URL(args[i]);
                HttpURLConnection uc = (HttpURLConnection) u.openConnection();

                // 响应码
                int code = uc.getResponseCode();

                // 响应消息
                String response = uc.getResponseMessage();
                System.out.println("HTTP/1.x " + code + " " + response);
                for (int j = 1; ; j++) {
                    String header = uc.getHeaderField(j);
                    String key = uc.getHeaderFieldKey(j);
                    if (header == null || key == null) break;
                    System.out.println(uc.getHeaderFieldKey(j) + ": " + header);
                }
                System.out.println();

                try (InputStream in = new BufferedInputStream(uc.getInputStream())) {
                    // chain the InputStream to a Reader
                    Reader r = new InputStreamReader(in);
                    int c;
                    while ((c = r.read()) != -1) {
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

}
