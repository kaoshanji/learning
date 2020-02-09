package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 检测还有多少字节未读
 * @author kaoshanji
 * @time 2020/2/9 下午3:49
 */
public class TestEOF {

    public static void main(String[] args) throws IOException {
        DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("TestEOF.java")));

        while(in.available() != 0)
            System.out.print((char)in.readByte());
    }

}
