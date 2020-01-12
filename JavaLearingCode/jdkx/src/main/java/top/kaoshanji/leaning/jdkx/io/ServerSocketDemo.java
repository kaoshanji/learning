package top.kaoshanji.leaning.jdkx.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.Executors;

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


    /**
     * 套接字：异步套接字通道
     * @throws IOException
     */
    public void startAsyncSimpleServer() throws IOException {

        // 异步通道的分组，关联一个线程池
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());
        final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(10080));

        // 接受来自客户端的连接
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            /**
             * 当有新连接建立时
             * @param clientChannel
             * @param attachment
             */
            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                serverChannel.accept(null, this);

                try {
                    clientChannel.write(ByteBuffer.wrap("Hello".getBytes("UTF-8")));
                    clientChannel.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            /**
             * 出现错误了..
             * @param throwable
             * @param attachment
             */
            @Override
            public void failed(Throwable throwable, Void attachment) {
                throwable.printStackTrace();
            }
        });
    }





}
