package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 使用ServerSocketChannel的非阻塞accept()方法
 * @author kaoshanji
 * @time 2020-01-27 20:43
 */
public class ChannelAccept {

    private final static Logger logger = LoggerFactory.getLogger(ChannelAccept.class);

    public static final String GREETING = "Hello I must be going.\r\n";

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 1234;

        ByteBuffer buffer = ByteBuffer.wrap(GREETING.getBytes());

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);

        // 阻塞起来，监听连接
        while (true) {
            logger.info("Waiting for connections");

            SocketChannel sc = ssc.accept();
            if (sc == null) {
                Thread.sleep(2000);
            }

            if (sc != null) {
                logger.info("Incoming connection from: " + sc.socket().getRemoteSocketAddress());

                buffer.rewind();
                sc.write(buffer);
                sc.close();

            }
        }
    }

}
