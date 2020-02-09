package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 读写随机访问文件
 * @author kaoshanji
 * @time 2020/2/9 下午4:15
 */
public class UsingRandomAccessFile {

    static String file = "rtest.dat";

    static void display() throws IOException {
        RandomAccessFile rf = new RandomAccessFile(file, "r");
        for(int i = 0; i < 7; i++)
            System.out.println(
                    "Value " + i + ": " + rf.readDouble());
        System.out.println(rf.readUTF());
        rf.close();
    }
    public static void main(String[] args)
            throws IOException {
        RandomAccessFile rf = new RandomAccessFile(file, "rw");
        for(int i = 0; i < 7; i++)
            rf.writeDouble(i*1.414);
        rf.writeUTF("The end of the file");
        rf.close();
        display();
        rf = new RandomAccessFile(file, "rw");
        rf.seek(5*8);
        rf.writeDouble(47.0001);
        rf.close();
        display();
    }

}
