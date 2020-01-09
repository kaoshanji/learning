package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Buffer 示例
 * @author kaoshanji
 * @time 2020/1/9 下午10:24
 */
public class BufferDemo {

    private final static Logger logger = LoggerFactory.getLogger(BufferDemo.class);

    /**
     * ByteBuffer 示例
     */
    public void useByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put((byte) 1);
        buffer.put(new byte[3]);
        buffer.putChar('A');
        buffer.putFloat(0.0F);
        buffer.putLong(10, 100L);

        logger.info(String.valueOf(buffer.getChar(4)));
        logger.info(String.valueOf(buffer.remaining()));
    }

    /**
     * 字节缓存区的字节顺序
     */
    public void byteOrder() {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        logger.info(String.valueOf(buffer.getInt(0)));//值为16777216
    }

    /**
     * 字节缓存区的压缩操作
     */
    public void compact() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put(new byte[16]);
        buffer.flip();
        buffer.getInt(); // 当前读取位置为 4
        buffer.compact();
        int pos = buffer.position();
        logger.info(String.valueOf(pos)); // 值为 12
    }

    /**
     * 字节缓存区视图
     */
    public void viewBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.putInt(1); // 读取位置为4

        IntBuffer intBuffer = buffer.asIntBuffer();
        intBuffer.put(2);
        int value = buffer.getInt();

        logger.info(String.valueOf(value)); // 值为2
    }

}
