package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * 文本文件输出的便捷方式
 * @author kaoshanji
 * @time 2020/2/9 下午3:59
 */
public class FileOutputShortcut {

    static String file = "FileOutputShortcut.out";

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(
                new StringReader(
                        BufferedInputFile.read("FileOutputShortcut.java")));
        // Here's the shortcut:
        // 便捷...
        PrintWriter out = new PrintWriter(file);
        int lineCount = 1;
        String s;
        while((s = in.readLine()) != null )
            out.println(lineCount++ + ": " + s);
        out.close();
        // Show the stored file:
        System.out.println(BufferedInputFile.read(file));
    }


}
