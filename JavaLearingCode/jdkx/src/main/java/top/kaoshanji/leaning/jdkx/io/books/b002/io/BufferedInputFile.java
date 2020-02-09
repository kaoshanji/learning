package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 缓冲输入文件
 * @author kaoshanji
 * @time 2020/2/9 下午3:35
 */
public class BufferedInputFile {

    // Throw exceptions to console:
    public static String read(String filename) throws IOException {
        // Reading input by lines:
        // 读入每行
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String s;

        // 暂存结果
        StringBuilder sb = new StringBuilder();
        while((s = in.readLine())!= null)
            sb.append(s + "\n");

        in.close();
        return sb.toString();
    }
    public static void main(String[] args) throws IOException {
        System.out.print(read("BufferedInputFile.java"));
    }

}
