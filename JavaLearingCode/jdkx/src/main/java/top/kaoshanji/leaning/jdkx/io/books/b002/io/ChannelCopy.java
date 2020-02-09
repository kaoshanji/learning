package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件复制
 * @author kaoshanji
 * @time 2020/2/9 下午8:06
 */
public class ChannelCopy {

    private static final int BSIZE = 1024;
    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.out.println("arguments: sourcefile destfile");
            System.exit(1);
        }
        FileChannel
                in = new FileInputStream(args[0]).getChannel(),
                out = new FileOutputStream(args[1]).getChannel();

        // 分配的空间
        ByteBuffer buffer = ByteBuffer.allocate(BSIZE);
        // 读入
        while(in.read(buffer) != -1) {
            // flip 一下
            buffer.flip(); // Prepare for writing

            // 写出
            out.write(buffer);

            // clear 一下 再才写出去..
            buffer.clear();  // Prepare for reading
        }
    }

}
