package top.kaoshanji.leaning.jdkx.io.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;

/**
 * IP 地址 --> 主机名
 * Web服务器日志文件
 * 优化
 *  1、DNS查询缓存
 *  2、使用多线程：一个主线程读取日志文件，将各个日志项传递给其他线程处理
 *  PooledWeblog 多线程版
 * @author kaoshanji
 * @time 2020/1/14 16:28
 */
public class Weblog {

    private final static Logger logger = LoggerFactory.getLogger(Weblog.class);

    public static void main(String [] args) {

        try (FileInputStream fin = new FileInputStream(args[0]);
             Reader in = new InputStreamReader(fin);
             BufferedReader bin = new BufferedReader(in);){

            for (String entry = bin.readLine(); entry != null; entry = bin.readLine()) {

                int index = entry.indexOf(" ");

                // ip 地址..第一个空格前面
                String ip = entry.substring(0, index);

                // 第一个空格后面全部内容
                String theRest = entry.substring(index);

                InetAddress address = InetAddress.getByName(ip);
                logger.info(address.getAddress() + theRest);
            }

        } catch (IOException ex) {
            logger.error("Exception: " + ex);
        }
    }



}
