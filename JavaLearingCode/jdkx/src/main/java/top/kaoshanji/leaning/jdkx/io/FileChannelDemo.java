package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * FileChannel 示例
 * @author kaoshanji
 * @time 2020-01-05 16:01
 */
public class FileChannelDemo {

    private final static Logger logger = LoggerFactory.getLogger(FileChannelDemo.class);

    /**
     * 打开文件通道并写入数据
     */
    public void openAndWrite() throws IOException {
        // 打开一个文件通道，并设置权限：不存在就创建和写入
        FileChannel fc = FileChannel.open(Paths.get("FileChannelDemo.txt"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        // 通道需要借助 缓冲器
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.putChar('J').flip(); // 刷刷刷进去

        fc.write(buffer);
    }


    /**
     * 对文件通道的绝对位置进行读写操作
     */
    public void readWriteAbsolute() throws IOException {
        FileChannel fc = FileChannel.open(Paths.get("absolute.txt"), StandardOpenOption.READ, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);

        ByteBuffer writeBuffer = ByteBuffer.allocate(4).putChar('A').putChar('B');
        writeBuffer.flip();

        // 1024处 写入
        fc.write(writeBuffer, 1024);

        ByteBuffer readBuffer = ByteBuffer.allocate(2);
        // 1026处 读取
        fc.read(readBuffer, 1026);
        readBuffer.flip();

        char result = readBuffer.getChar(); // 值为 B

        logger.info("....result:"+result+".....");

    }

    /**
     * 使用文件通道保存网页内容
     * @param uri URI 地址
     */
    public void loadWebPage(String uri) throws IOException {
        logger.info("..1..加载 URI:"+uri+"....");

        FileChannel destChannel = FileChannel.open(Paths.get("content.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        InputStream input = new URL(uri).openStream();
        ReadableByteChannel srcChannel = Channels.newChannel(input);

        destChannel.transferFrom(srcChannel, 0, Integer.MAX_VALUE);
        logger.info("..2..加载 URI:"+uri+"....");
    }


    /**
     * 文件复制：字节缓冲区
     */
    public void copyByByteBuffer() throws IOException {
        String srcFilename = "src.data";
        String destFilename = "dest.data";

        ByteBuffer buffer = ByteBuffer.allocateDirect(32 * 1024);

        FileChannel src = FileChannel.open(Paths.get(srcFilename), StandardOpenOption.READ);
        FileChannel dest = FileChannel.open(Paths.get(destFilename), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        while (src.read(buffer) > 0 || buffer.position() != 0) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
    }

    /**
     * 文件复制：文件通道
     * 简洁..简单
     * @throws IOException
     */
    public void copyByChannelTransfer() throws IOException {
        String srcFilename = "src.data";
        String destFilename = "dest.data";

        FileChannel src = FileChannel.open(Paths.get(srcFilename), StandardOpenOption.READ);
        FileChannel dest = FileChannel.open(Paths.get(destFilename), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        src.transferTo(0, src.size(), dest);
    }


    /**
     * 内存映射文件
     */
    public void mapFile() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("src.data"), StandardOpenOption.READ, StandardOpenOption.WRITE);

        // 创建映射对象
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, channel.size());

        byte b = buffer.get(1024 * 1024);
        buffer.put(5 * 1024 * 1024, b);
        buffer.force(); // 强制要求更新同步到底层文件中
    }


    /**
     * 文件锁
     * 对文件进行加锁操作的主体是当前的JVM，是JVM与操作系统其它程序交互
     */
    public void updateWithLock() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("settings.config"), StandardOpenOption.READ, StandardOpenOption.WRITE);

        FileLock lock = channel.lock(); // 获取文件锁
        // 更新文件内容...


    }


    /**
     * 向异步文件通道中写入数据
     * 使用 Future 接收处理结果
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void asyncWrite() throws IOException, ExecutionException, InterruptedException {
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("large.bin"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(32 * 1024 * 1024);
        Future<Integer> result = channel.write(buffer, 0);

        // 其他操作
        Integer len = result.get();
    }




}
