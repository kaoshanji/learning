package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 从Web网站下载二进制文件并保存到磁盘
 * @author kaoshanji
 * @time 2020/2/5 下午10:10
 */
public class BinarySaver {

    public static void main (String[] args) {

        String url = "";

        try {
            URL root = new URL(url);
            saveBinaryFile(root);
        } catch (MalformedURLException ex) {
            System.err.println(url+ " is not URL I understand.");
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }


    public static void saveBinaryFile(URL u) throws IOException {
        URLConnection uc = u.openConnection();

        // 响应数据类型，需要是二进制
        String contentType = uc.getContentType();

        // 响应数据长度，也就是待读数据长度
        int contentLength = uc.getContentLength();
        if (contentType.startsWith("text/") || contentLength == -1 ) {
            throw new IOException("This is not a binary file.");
        }

        // InputStream
        try (InputStream raw = uc.getInputStream()) {
            InputStream in  = new BufferedInputStream(raw);
            byte[] data = new byte[contentLength];

            // 已读数据长度
            int offset = 0;
            while (offset < contentLength) {
                int bytesRead = in.read(data, offset, data.length - offset);
                if (bytesRead == -1) break;
                offset += bytesRead;
            }

            // 已读与待读的数据应该相等
            if (offset != contentLength) {
                throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
            }

            // 文件名??
            String filename = u.getFile();
            filename = filename.substring(filename.lastIndexOf('/') + 1);
            try (FileOutputStream fout = new FileOutputStream(filename)) {
                // 写到数组里面去了，也就是内存里面去了，为何还要 flush 呢??
                fout.write(data);
                // 向 操作系统发起 write 请求，把数据从 JVM 堆(常规进程内存空间)里拷贝到 系统内存 --> 文件系统
                fout.flush();
            }

        }
    }

}
