package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.nio.ByteBuffer;

import static top.kaoshanji.leaning.jdkx.io.books.b002.util.Print.print;
import static top.kaoshanji.leaning.jdkx.io.books.b002.util.Print.printnb;

/**
 * ByteBuffer插入和抽取各种数值
 * @author kaoshanji
 * @time 2020/2/9 下午8:24
 */
public class GetData {

    private static final int BSIZE = 1024;

    public static void main(String[] args) {

        ByteBuffer bb = ByteBuffer.allocate(BSIZE);
        // Allocation automatically zeroes the ByteBuffer:
        int i = 0;
        while(i++ < bb.limit())
            if(bb.get() != 0)
                print("nonzero");
        print("i = " + i);
        bb.rewind();
        // Store and read a char array:
        bb.asCharBuffer().put("Howdy!");
        char c;
        while((c = bb.getChar()) != 0)
            printnb(c + " ");
        print();
        bb.rewind();
        // Store and read a short:
        bb.asShortBuffer().put((short)471142);
        print(bb.getShort());
        bb.rewind();
        // Store and read an int:
        bb.asIntBuffer().put(99471142);
        print(bb.getInt());
        bb.rewind();
        // Store and read a long:
        bb.asLongBuffer().put(99471142);
        print(bb.getLong());
        bb.rewind();
        // Store and read a float:
        bb.asFloatBuffer().put(99471142);
        print(bb.getFloat());
        bb.rewind();
        // Store and read a double:
        bb.asDoubleBuffer().put(99471142);
        print(bb.getDouble());
        bb.rewind();
    }


}
