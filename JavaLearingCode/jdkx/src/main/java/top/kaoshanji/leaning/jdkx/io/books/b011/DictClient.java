package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.*;
import java.net.Socket;

/**
 * 一个基于dict网络的英语-拉丁语翻译程序
 * @author kaoshanji
 * @time 2020/2/6 下午2:07
 */
public class DictClient {

    public static final String SERVER = "dict.org";
    public static final int PORT = 2628;
    public static final int TIMEOUT = 15000;

    public static void main(String[] args) {

        args = new String[]{"gold", "silver"};

        Socket socket = null;
        try {
            socket = new Socket(SERVER, PORT);
            socket.setSoTimeout(TIMEOUT);

            // 输出流向服务器提交数据
            OutputStream out = socket.getOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");
            writer = new BufferedWriter(writer);

            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            for (String word : args) {
                define(word, writer, reader);
            }

            writer.write("quit\r\n");
            writer.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        } finally { // dispose
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

    static void define(String word, Writer writer, BufferedReader reader)
            throws IOException, UnsupportedEncodingException {
        writer.write("DEFINE eng-lat " + word + "\r\n");
        writer.flush();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (line.startsWith("250 ")) { // OK
                return;
            } else if (line.startsWith("552 ")) { // no match
                System.out.println("No definition found for " + word);
                return;
            }
            else if (line.matches("\\d\\d\\d .*")) continue;
            else if (line.trim().equals(".")) continue;
            else System.out.println(line);
        }
    }


}
