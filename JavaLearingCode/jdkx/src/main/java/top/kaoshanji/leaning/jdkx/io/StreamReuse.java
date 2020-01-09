package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * 输入流 复制
 * 使用 BufferedInputStream 实现
 * 基于 标记和重置 特性
 * @author kaoshanji
 * @time 2020/1/9 下午9:52
 */
public class StreamReuse {

    private final static Logger logger = LoggerFactory.getLogger(StreamReuse.class);

    private InputStream input;

    public StreamReuse(InputStream input) {
        if (!input.markSupported()) {
            this.input = new BufferedInputStream(input);
        } else {
            this.input = input;
        }
    }

    /**
     * 标记
     * @return
     */
    public InputStream getInputStream() {
        input.mark(Integer.MAX_VALUE);
        return input;
    }

    /**
     * 重置
     * @throws IOException
     */
    public void markUsed() throws IOException {
        input.read();
    }

    public static void useStream(InputStream input) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ReadableByteChannel channel = Channels.newChannel(input);
        int count = channel.read(buffer);

        logger.info("count:"+count);
    }

    public static void main(String[] args) throws IOException {
        FileInputStream input = new FileInputStream("");
        StreamReuse sr = new StreamReuse(input);
        InputStream reusable = sr.getInputStream();

        useStream(reusable);

        sr.markUsed();
        reusable = sr.getInputStream();
        useStream(reusable);
    }

}
