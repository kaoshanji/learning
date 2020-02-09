package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.*;

/**
 * 重定向字节流
 * @author kaoshanji
 * @time 2020/2/9 下午6:09
 */
public class Redirecting {

    public static void main(String[] args)
            throws IOException {
        PrintStream console = System.out;

        BufferedInputStream in = new BufferedInputStream(
                new FileInputStream("Redirecting.java"));

        PrintStream out = new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream("test.out")));
        System.setIn(in);
        System.setOut(out);
        System.setErr(out);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in));
        String s;
        while((s = br.readLine()) != null)
            System.out.println(s);
        out.close(); // Remember this!
        System.setOut(console);
    }


}
