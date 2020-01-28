package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 使用 select() 来为多个通道提供服务
 * @author kaoshanji
 * @time 2020-01-28 17:56
 */
public class SelectSockets {

    private final static Logger logger = LoggerFactory.getLogger(SelectSockets.class);

    public static int PORT_NUMBER = 1234;

    private ByteBuffer buffer = ByteBuffer.allocateDirect (1024);

    public static void main (String [] argv) throws Exception {
        new SelectSockets().go (argv);
    }

    public void go (String [] argv) throws Exception {

        int port = PORT_NUMBER;
        logger.info("监听端口："+port);

        // 向操作系统注册一个选择器，并标明感兴趣的 I/O 事件
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        ServerSocket serverSocket = serverChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));

        serverChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 接入连接..已注册的集合
            int n = selector.select();
            if (n == 0) {
                continue;
            }

            // 已注册的集合不为空
            Iterator it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey)it.next();

                // 接入一个新连接
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel)key.channel();
                    SocketChannel channel = server.accept();

                    // 等到通道可读
                    registerChannel(selector, channel, SelectionKey.OP_READ);
                    sayHello (channel);
                }

                if (key.isReadable()) {
                    readDataFromSocket(key);
                }

                it.remove();
            }
        }
    }


    protected void registerChannel (Selector selector, SelectableChannel channel, int ops) throws IOException {
        if (channel == null) {
            return;
        }

        channel.configureBlocking(false);
        channel.register(selector, ops);
    }


    protected void readDataFromSocket (SelectionKey key) throws IOException {

        SocketChannel socketChannel = (SocketChannel)key.channel();
        int count;

        buffer.clear();

        while ((count = socketChannel.read(buffer)) > 0) {
            buffer.flip();

            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }

            buffer.clear();
        }

        if (count < 0) {
            socketChannel.close();
        }
    }


    private void sayHello (SocketChannel channel) throws Exception {
        buffer.clear();
        buffer.put ("Hi there!\r\n".getBytes());
        buffer.flip();
        channel.write (buffer);
    }


}
