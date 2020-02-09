package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * 视图读取数据
 * @author kaoshanji
 * @time 2020/2/9 下午8:32
 */
public class IntBufferDemo {

    private static final int BSIZE = 1024;
    public static void main(String[] args) {
        ByteBuffer bb = ByteBuffer.allocate(BSIZE);

        IntBuffer ib = bb.asIntBuffer();
        // Store an array of int:
        ib.put(new int[]{ 11, 42, 47, 99, 143, 811, 1016 });
        // Absolute location read and write:
        System.out.println(ib.get(3));
        ib.put(3, 1811);
        // Setting a new limit before rewinding the buffer.
        ib.flip();
        while(ib.hasRemaining()) {
            int i = ib.get();
            System.out.println(i);
        }
    }


}
