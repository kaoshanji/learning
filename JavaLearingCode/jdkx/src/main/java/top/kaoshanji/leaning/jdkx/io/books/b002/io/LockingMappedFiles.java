package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * 两个线程分别加锁文件不同部分
 * @author kaoshanji
 * @time 2020/2/9 下午9:10
 */
public class LockingMappedFiles {

    static final int LENGTH = 0x8FFFFFF; // 128 MB
    static FileChannel fc;

    public static void main(String[] args) throws Exception {

        // 打开通道..
        fc = new RandomAccessFile("test.dat", "rw").getChannel();

        // 获得锁
        MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_WRITE, 0, LENGTH);
        for(int i = 0; i < LENGTH; i++)
            out.put((byte)'x');
        new LockAndModify(out, 0, 0 + LENGTH/3);
        new LockAndModify(out, LENGTH/2, LENGTH/2 + LENGTH/4);
    }


    private static class LockAndModify extends Thread {

        private ByteBuffer buff;
        private int start, end;

        LockAndModify(ByteBuffer mbb, int start, int end) {
            this.start = start;
            this.end = end;
            mbb.limit(end);
            mbb.position(start);
            buff = mbb.slice();
            start();
        }
        public void run() {
            try {
                // Exclusive lock with no overlap:
                FileLock fl = fc.lock(start, end, false);
                System.out.println("Locked: "+ start +" to "+ end);
                // Perform modification:
                while(buff.position() < buff.limit() - 1)
                    buff.put((byte)(buff.get() + 1));

                // 释放锁..
                fl.release();
                System.out.println("Released: "+start+" to "+ end);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
