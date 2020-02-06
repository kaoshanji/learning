package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 提交一个表单
 * @author kaoshanji
 * @time 2020/2/5 下午10:40
 */
public class FormPoster {

    private URL url;
    // from Chapter 5, Example 5-8
    private QueryString query = new QueryString();

    public FormPoster (URL url) {
        if (!url.getProtocol().toLowerCase().startsWith("http")) {
            throw new IllegalArgumentException(
                    "Posting only works for http URLs");
        }
        this.url = url;
    }

    public void add(String name, String value) {
        query.add(name, value);
    }

    public URL getURL() {
        return this.url;
    }

    public InputStream post() throws IOException {

        // 打开连接，准备 POST
        URLConnection uc = url.openConnection();
        uc.setDoOutput(true);

        // 有了 OutputStreamWriter 就可以输出..
        try (OutputStreamWriter out = new OutputStreamWriter(uc.getOutputStream(), "UTF-8")) {

            // The POST line, the Content-type header,
            // and the Content-length headers are sent by the URLConnection.
            // 只发送数据
            out.write(query.toString());
            out.write("\r\n");
            out.flush();
        }

        // 返回响应
        return uc.getInputStream();
    }

    public static void main(String[] args) {
        URL url;
        if (args.length > 0) {
            try {
                url = new URL(args[0]);
            } catch (MalformedURLException ex) {
                System.err.println("Usage: java FormPoster url");
                return;
            }
        } else {
            try {
                url = new URL(
                        "http://www.cafeaulait.org/books/jnp4/postquery.phtml");
            } catch (MalformedURLException ex) { // shouldn't happen
                System.err.println(ex);
                return;
            }
        }

        FormPoster poster = new FormPoster(url);
        poster.add("name", "Elliotte Rusty Harold");
        poster.add("email", "elharo@ibiblio.org");

        try (InputStream in = poster.post()) {
            // Read the response
            Reader r = new InputStreamReader(in);
            int c;
            while((c = r.read()) != -1) {
                System.out.print((char) c);
            }
            System.out.println();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

}
