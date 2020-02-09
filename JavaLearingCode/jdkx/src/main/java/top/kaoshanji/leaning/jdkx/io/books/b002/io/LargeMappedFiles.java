package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static top.kaoshanji.leaning.jdkx.io.books.b002.util.Print.print;
import static top.kaoshanji.leaning.jdkx.io.books.b002.util.Print.printnb;

/**
 * 内存映射文件示例
 * @author kaoshanji
 * @time 2020/2/9 下午8:51
 */
public class LargeMappedFiles {

    static int length = 0x8FFFFFF; // 128 MB
    public static void main(String[] args) throws Exception {
        MappedByteBuffer out =
                new RandomAccessFile("test.dat", "rw").getChannel()
                        .map(FileChannel.MapMode.READ_WRITE, 0, length);

        for(int i = 0; i < length; i++)
            out.put((byte)'x');
        print("Finished writing");
        for(int i = length/2; i < length/2 + 6; i++)
            printnb((char)out.get(i));
    }

}
