package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.*;

/**
 * 存储和恢复数据
 * @author kaoshanji
 * @time 2020/2/9 下午4:03
 */
public class StoringAndRecoveringData {


    public static void main(String[] args) throws IOException {

        DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream("Data.txt")));
        out.writeDouble(3.14159);

        // 针对字符串
        out.writeUTF("That was pi");
        out.writeDouble(1.41413);
        out.writeUTF("Square root of 2");
        out.close();

        DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("Data.txt")));
        System.out.println(in.readDouble());
        // Only readUTF() will recover the
        // Java-UTF String properly:
        System.out.println(in.readUTF());
        System.out.println(in.readDouble());
        System.out.println(in.readUTF());
    }

}
