package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import top.kaoshanji.leaning.jdkx.io.books.b002.util.Directory;
import top.kaoshanji.leaning.jdkx.io.books.b002.util.PPrint;
import top.kaoshanji.leaning.jdkx.io.books.b002.util.Print;

import java.io.File;

/**
 * Directory示例
 * @author kaoshanji
 * @time 2020/2/9 下午12:46
 */
public class DirectoryDemo {

    public static void main(String[] args) {
        // All directories:
        PPrint.pprint(Directory.walk(".").dirs);
        // All files beginning with 'T'
        for(File file : Directory.local(".", "T.*"))
            Print.print(file);
        Print.print("----------------------");
        // All Java files beginning with 'T':
        for(File file : Directory.walk(".", "T.*\\.java"))
            Print.print(file);
        Print.print("======================");
        // Class files containing "Z" or "z":
        for(File file : Directory.walk(".",".*[Zz].*\\.class"))
            Print.print(file);
    }

}
