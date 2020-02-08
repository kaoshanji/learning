package top.kaoshanji.leaning.jdkx.io.books.b001.randomAccess;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 字符 到 字符串
 * @author kaoshanji
 * @time 2020/2/8 下午2:59
 */
public class DataIO {


    // 从输入流中读入字符，直至读入 size 个码元，或在直至遇到具有0值的字符值
    public static String readFixedString(int size, DataInput in) throws IOException {
        StringBuilder b = new StringBuilder(size);
        int i = 0;
        boolean more = true;
        while (more && i < size) {
            char ch = in.readChar();
            i++;
            if (ch == 0) more = false;
            else b.append(ch);
        }
        in.skipBytes(2 * (size - i));
        return b.toString();
    }

    //写出从字符串开头开始的指定数量的码元(缺少的用0值补齐)
    public static void writeFixedString(String s, int size, DataOutput out) throws IOException {
        for (int i = 0; i < size; i++) {
            char ch = 0;
            if (i < s.length()) ch = s.charAt(i);
            out.writeChar(ch);
        }
    }

}
