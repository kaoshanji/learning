package top.kaoshanji.leaning.jdkx.io.books.b002.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * 用一个ArrayList保存文件的若干行
 * @author kaoshanji
 * @time 2020/2/9 下午4:40
 */
public class TextFile extends ArrayList<String> {

    // Read a file as a single string:
    // 读取文件里的行
    // 将每行添加到 StringBuilder 并加上换行符
    public static String read(String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader in= new BufferedReader(new FileReader(
                    new File(fileName).getAbsoluteFile()));
            try {
                String s;
                while((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    // Write a single file in one method call:
    // 写入文件一些数据
    // 打开文件并将其写入文件
    public static void write(String fileName, String text) {
        try {
            PrintWriter out = new PrintWriter(
                    new File(fileName).getAbsoluteFile());
            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Read a file, split by any regular expression:
    public TextFile(String fileName, String splitter) {
        super(Arrays.asList(read(fileName).split(splitter)));
        // Regular expression split() often leaves an empty
        // String at the first position:
        if(get(0).equals("")) remove(0);
    }

    // Normally read by lines:
    public TextFile(String fileName) {
        this(fileName, "\n");
    }

    public void write(String fileName) {
        try {
            PrintWriter out = new PrintWriter(
                    new File(fileName).getAbsoluteFile());
            try {
                for(String item : this)
                    out.println(item);
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Simple test:
    public static void main(String[] args) {
        String file = read("TextFile.java");
        write("test.txt", file);
        TextFile text = new TextFile("test.txt");
        text.write("test2.txt");
        // Break into unique sorted list of words:
        TreeSet<String> words = new TreeSet<String>(
                new TextFile("TextFile.java", "\\W+"));
        // Display the capitalized words:
        System.out.println(words.headSet("a"));
    }


}
