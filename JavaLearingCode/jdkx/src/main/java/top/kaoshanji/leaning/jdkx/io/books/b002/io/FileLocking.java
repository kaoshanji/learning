package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;

/**
 * 文件加锁的示例
 * @author kaoshanji
 * @time 2020/2/9 下午9:03
 */
public class FileLocking {

    public static void main(String[] args) throws Exception {

        FileOutputStream fos= new FileOutputStream("file.txt");

        // 文件锁从 通道 上面获取
        FileLock fl = fos.getChannel().tryLock();

        if(fl != null) {
            System.out.println("Locked File");
            TimeUnit.MILLISECONDS.sleep(100);
            // 释放锁..
            fl.release();
            System.out.println("Released Lock");
        }
        fos.close();
    }

}
