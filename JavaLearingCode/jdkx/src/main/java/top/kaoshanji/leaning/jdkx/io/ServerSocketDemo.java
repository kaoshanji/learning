package top.kaoshanji.leaning.jdkx.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * ServerSocket 示例
 * @author kaoshanji
 * @time 2020-01-05 23:11
 */
public class ServerSocketDemo {

    /**
     * 套接字：阻塞式服务端
     */
    public void startSimpleServer() throws IOException {
        // 创建 服务端套接字对象
        ServerSocketChannel channel = ServerSocketChannel.open();
        // 绑定端口
        channel.bind(new InetSocketAddress("localhost", 10800));
        // 死循环中监听
        while (true) {
            SocketChannel sc = channel.accept();
            sc.write(ByteBuffer.wrap("Hello".getBytes("UTF-8")));
        }
    }


}
