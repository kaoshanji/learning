package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

import static top.kaoshanji.leaning.jdkx.io.books.b002.util.Print.print;

/**
 * 压缩多个文件
 * 计算和校验文件值
 * @author kaoshanji
 * @time 2020/2/9 下午9:23
 */
public class ZipCompress {

    public static void main(String[] args) throws IOException {

        FileOutputStream f = new FileOutputStream("test.zip");

        CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
        ZipOutputStream zos = new ZipOutputStream(csum);
        BufferedOutputStream out = new BufferedOutputStream(zos);
        zos.setComment("A test of Java Zipping");

        // No corresponding getComment(), though.
        // 压缩多个文件
        for(String arg : args) {
            print("Writing file " + arg);
            BufferedReader in = new BufferedReader(new FileReader(arg));
            // 每个待压缩的文件
            zos.putNextEntry(new ZipEntry(arg));
            int c;
            while((c = in.read()) != -1)
                out.write(c);
            in.close();
            out.flush();
        }
        out.close();
        // Checksum valid only after the file has been closed!
        print("Checksum: " + csum.getChecksum().getValue());
        // Now extract the files:
        print("Reading file");
        FileInputStream fi = new FileInputStream("test.zip");
        CheckedInputStream csumi =
                new CheckedInputStream(fi, new Adler32());
        ZipInputStream in2 = new ZipInputStream(csumi);
        BufferedInputStream bis = new BufferedInputStream(in2);
        ZipEntry ze;
        while((ze = in2.getNextEntry()) != null) {
            print("Reading file " + ze);
            int x;
            while((x = bis.read()) != -1)
                System.out.write(x);
        }
        if(args.length == 1)
            print("Checksum: " + csumi.getChecksum().getValue());
        bis.close();
        // Alternative way to open and read Zip files:
        ZipFile zf = new ZipFile("test.zip");
        Enumeration e = zf.entries();
        while(e.hasMoreElements()) {
            ZipEntry ze2 = (ZipEntry)e.nextElement();
            print("File: " + ze2);
            // ... and extract the data as before
        }
        /* if(args.length == 1) */
    }


}
