package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * 格式化的内存输入
 * @author kaoshanji
 * @time 2020/2/9 下午3:45
 */
public class FormattedMemoryInput {

    public static void main(String[] args) throws IOException {
        try {
            DataInputStream in = new DataInputStream(
                    new ByteArrayInputStream(
                            BufferedInputFile.read(
                                    "FormattedMemoryInput.java").getBytes()));

            // 一个字节一个字节的读，任何字节都是合法
            // 返回值不能检测输入是否结束
            while(true)
                System.out.print((char)in.readByte());
        } catch(EOFException e) {
            System.err.println("End of stream");
        }
    }

}
