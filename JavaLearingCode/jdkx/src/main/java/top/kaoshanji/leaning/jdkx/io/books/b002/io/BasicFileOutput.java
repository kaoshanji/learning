package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.*;

/**
 * 基本的文件输出
 * @author kaoshanji
 * @time 2020/2/9 下午3:54
 */
public class BasicFileOutput {

    static String file = "BasicFileOutput.out";
    public static void main(String[] args) throws IOException {

        BufferedReader in = new BufferedReader(
                new StringReader(
                        BufferedInputFile.read("BasicFileOutput.java")));

        PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(file)));
        int lineCount = 1;
        String s;
        while((s = in.readLine()) != null )
            out.println(lineCount++ + ": " + s);
        out.close();
        // Show the stored file:
        System.out.println(BufferedInputFile.read(file));
    }


}
