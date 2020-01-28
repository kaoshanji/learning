package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Echo 服务器
 * NIO 好不??
 * @author kaoshanji
 * @time 2020-01-20 22:06
 */
public class EchoServer {

    private final static Logger logger = LoggerFactory.getLogger(EchoServer.class);

    public static int DEFAULT_PORT = 7;

    public static void main(String[] args) {

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException ex) {
            port = DEFAULT_PORT;
        }

        logger.info("监听连接的端口：" + port);
        ServerSocketChannel serverChannel;
        Selector selector;
        try {
            serverChannel = ServerSocketChannel.open();
            ServerSocket ss = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            ss.bind(address);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            // 注册事件..接入连接
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return;
        }

        while (true) {
            try {
                selector.select();
            } catch (IOException ex) {
                logger.error(ex.getMessage());
                break;
            }

            // 接受连接事件...一遍又一遍的遍历..要是遍历过了才有事件不得下次遍历才能处理?
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            // 就绪状态通道集合有值了...
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove(); // 处理了就删了..下一个
                try {

                    // 接入连接..
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel)key.channel();
                        SocketChannel client = server.accept();
                        logger.info("Accepted connection from " + client);
                        client.configureBlocking(false);

                        // 读、、写、、
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_WRITE);
                        ByteBuffer buffer = ByteBuffer.allocate(100);
                        clientKey.attach(buffer);
                    }

                    // 读..
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel)key.channel();
                        ByteBuffer output = (ByteBuffer)key.attachment();
                        client.read(output);
                    }

                    // 写
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel)key.channel();
                        ByteBuffer output = (ByteBuffer)key.attachment();
                        output.flip();
                        client.write(output);
                        output.compact();
                    }

                } catch (IOException ex) {
                    // 终止 通道与选择器 绑定的关系
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {}
                }
            }
        }
    }
}
