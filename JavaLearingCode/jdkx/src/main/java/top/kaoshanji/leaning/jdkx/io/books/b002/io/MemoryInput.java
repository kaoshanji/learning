package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.IOException;
import java.io.StringReader;

/**
 * 从内存输入
 * @author kaoshanji
 * @time 2020/2/9 下午3:40
 */
public class MemoryInput {

    public static void main(String[] args) throws IOException {
        StringReader in = new StringReader(BufferedInputFile.read("MemoryInput.java"));
        int c;
        while((c = in.read()) != -1)
            System.out.print((char)c);
    }

}
