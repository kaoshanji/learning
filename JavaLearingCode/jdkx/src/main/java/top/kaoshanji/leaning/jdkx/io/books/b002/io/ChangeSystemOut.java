package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.PrintWriter;

/**
 * 将System.out转换成PrintWriter
 * @author kaoshanji
 * @time 2020/2/9 下午5:52
 */
public class ChangeSystemOut {

    public static void main(String[] args) {

        // 第二个参数需要设为 true，开启自动清空
        PrintWriter out = new PrintWriter(System.out, true);
        out.println("Hello, world");
    }

}
