package top.kaoshanji.leaning.jdkx.io;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Socket 示例
 * @author kaoshanji
 * @time 2020-01-20 11:52
 */
public class SocketDemo {

    private final static Logger logger = LoggerFactory.getLogger(SocketDemo.class);

    /**
     * Daytime协议客户端
     * @param hostName
     */
    public void dayTimeClient(String hostName) {

        if (StringUtils.isBlank(hostName)) {
            hostName = "time.nist.gov";
        }

        Socket socket = null;
        try {
            socket = new Socket(hostName, 13);
            // 超时时间很有必要。。15 秒
            socket.setSoTimeout(15000);

            InputStream in = socket.getInputStream();
            StringBuilder time = new StringBuilder();
            // 指定编码..根据请求的服务器设置
            InputStreamReader reader = new InputStreamReader(in, "ASCII");

            for (int c = reader.read(); c != -1; c = reader.read()) {
                time.append((char)c);
            }

            logger.info("此时的时间："+time);

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ////
                }
            }
        }
    }


    /**
     * 一个基于网络的英语-拉丁语翻译程序
     */
    public void dictClient() {
        String SERVER = "dict.org";
        int PORT = 2628;
        int TIMEOUT = 15000;

        Socket socket = null;
        try {
            socket = new Socket(SERVER, PORT);
            socket.setSoTimeout(TIMEOUT);

            OutputStream out = socket.getOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");
            writer = new BufferedWriter(writer);

            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String args [] = {"gold","uranium","silver","copper","lead"};
            for (String word : args) {
                define(word, writer, reader);
            }

            writer.write("quit\r\n");
            writer.flush();

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ////
                }
            }
        }
    }

    private void define(String word, Writer writer, BufferedReader reader) throws IOException {
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

    /**
     * 查看指定主机上前 1024 个端口中那些安装有 TCP 服务器
     * @param host
     */
    public void lowPortScanner(String host) {
        if (StringUtils.isBlank(host)) {
            host = "localhost";
        }

        for (int i = 1; i < 1024; i++) {
            try {
                Socket socket = new Socket(host, i);

                logger.info(host + "的端口：" + i + " 是一种服务.");
                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    public static void main(String[] args) {
        SocketDemo demo = new SocketDemo();
        //demo.dayTimeClient("");
        demo.lowPortScanner("");

    }


}
