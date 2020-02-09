package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 旧IO类库三个类获取FileChannel
 * @author kaoshanji
 * @time 2020/2/9 下午7:46
 */
public class GetChannel {

    private static final int BSIZE = 1024;

    public static void main(String[] args) throws Exception {
        // Write a file:
        // 写入数据
        FileChannel fc = new FileOutputStream("data.txt").getChannel();
        fc.write(ByteBuffer.wrap("Some text ".getBytes()));
        fc.close();

        // Add to the end of the file:
        // 随机读写
        fc = new RandomAccessFile("data.txt", "rw").getChannel();

        // 设置写入的位置
        fc.position(fc.size()); // Move to the end
        fc.write(ByteBuffer.wrap("Some more".getBytes()));
        fc.close();

        // Read the file:
        // 读取数据
        fc = new FileInputStream("data.txt").getChannel();
        ByteBuffer buff = ByteBuffer.allocate(BSIZE);
        fc.read(buff);
        buff.flip();
        while(buff.hasRemaining())
            System.out.print((char)buff.get());
    }

}
