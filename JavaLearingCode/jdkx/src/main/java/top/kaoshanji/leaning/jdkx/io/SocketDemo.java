package top.kaoshanji.leaning.jdkx.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Socket 示例
 * @author kaoshanji
 * @time 2020-01-05 20:42
 */
public class SocketDemo {


    /**
     * 套接字：阻塞式客户端
     */
    public void loadWebPageBySocket() throws IOException {
        // 文件通道
        FileChannel destChannel = FileChannel.open(Paths.get("context.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        // socket
        SocketChannel sc = SocketChannel.open(new InetSocketAddress("www.baidu.com", 80));

        // 请求地址
        String request = "GET / HTTP/1.1\r\n\\r\nHost: www.baidu.com\r\n\r\n";
        ByteBuffer header = ByteBuffer.wrap(request.getBytes("UTF-8"));

        sc.write(header);
        destChannel.transferFrom(sc, 0, Integer.MAX_VALUE);

    }



}
