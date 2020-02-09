package top.kaoshanji.leaning.jdkx.io.books.b002.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 简化了读取二进制文件的过程
 * @author kaoshanji
 * @time 2020/2/9 下午4:47
 */
public class BinaryFile {

    public static byte[] read(File bFile) throws IOException {
        BufferedInputStream bf = new BufferedInputStream(
                new FileInputStream(bFile));
        try {
            byte[] data = new byte[bf.available()];
            bf.read(data);
            return data;
        } finally {
            bf.close();
        }
    }

    public static byte[] read(String bFile) throws IOException {
        return read(new File(bFile).getAbsoluteFile());
    }

}
