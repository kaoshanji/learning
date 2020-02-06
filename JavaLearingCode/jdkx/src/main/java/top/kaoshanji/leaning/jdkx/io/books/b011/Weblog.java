package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 处理 Web 服务器日志文件
 * @author kaoshanji
 * @time 2020/2/5 下午4:08
 */
public class Weblog {

    public static void main(String[] args) {

        try (FileInputStream fin =  new FileInputStream(args[0]);
             Reader in = new InputStreamReader(fin);
             BufferedReader bin = new BufferedReader(in);) {

            for (String entry = bin.readLine(); entry != null; entry = bin.readLine()) {
                // 分析 IP 地址
                int index = entry.indexOf(' ');
                String ip = entry.substring(0, index);
                String theRest = entry.substring(index);

                // 向 NDS 请求主机名并显示
                try {
                    InetAddress address = InetAddress.getByName(ip);
                    System.out.println(address.getHostName() + theRest);
                } catch (UnknownHostException ex) {
                    System.err.println(entry);
                }
            }

        } catch (IOException ex) {
            System.out.println("Exception: " + ex);
        }
    }

}
