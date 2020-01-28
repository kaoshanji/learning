package top.kaoshanji.leaning.jdkx.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * 通道 示例
 * @author kaoshanji
 * @time 2020-01-26 15:23
 */
public class ChannelDemo {

    /**
     * 通道 复制
     * @param src
     * @param dest
     * @throws IOException
     */
    public void channelCopy1(ReadableByteChannel src, WritableByteChannel dest) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);

        while (src.read(buffer) != -1) {
            // 调整 buffer 属性..翻转
            buffer.flip();

            // 写入
            dest.write(buffer);

            // 压缩..释放??
            buffer.compact();
        }

        buffer.flip();
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    /**
     * 通道 复制
     * @param src
     * @param dest
     * @throws IOException
     */
    public void channelCopy2(ReadableByteChannel src, WritableByteChannel dest) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            buffer.flip();

            while (buffer.hasRemaining()) {
                dest.write(buffer);
            }
            buffer.clear();
        }
    }


}
