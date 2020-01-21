package top.kaoshanji.leaning.jdkx.io.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * URLConnection 示例
 * @author kaoshanji
 * @time 2020-01-19 17:33
 */
public class URLConnectionDemo {

    private final static Logger logger = LoggerFactory.getLogger(URLConnectionDemo.class);

    /**
     * 使用 URLConnection 下载一个 Web 页面
     * @param uriStr
     * @throws IOException
     */
    public void sourceViewer(String uriStr) throws IOException {
        // 打开一个 URLConnection 用来读取
        URL url = new URL(uriStr);
        URLConnection uc = url.openConnection();

        InputStream raw = uc.getInputStream();
        InputStream buffer = new BufferedInputStream(raw);
        Reader reader = new InputStreamReader(buffer);

        int c;
        while ((c = reader.read()) != -1) {
            logger.info((char)c+"");
        }
        reader.close();
    }

    /**
     * 用正确的字符集下载一个 Web 页面
     * @param uriStr
     * @throws IOException
     */
    public void encodingAwareSourceViewer(String uriStr) throws IOException {
        // 设置默认编码
        String encoding = "ISO-8859-1";
        URL url = new URL(uriStr);
        URLConnection uc = url.openConnection();

        String contentType = uc.getContentType();
        int encodingStart = contentType.indexOf("charset=");
        if (encodingStart != -1) {
            // 获得页面的编码
            encoding = contentType.substring(encodingStart + 8);
        }

        InputStream in = new BufferedInputStream(uc.getInputStream());
        Reader reader = new InputStreamReader(in, encoding);
        int c;
        while ((c = reader.read()) != -1) {
            logger.info((char)c+"");
        }
        reader.close();
    }

    /**
     * 从 Web 网站下载二进制文件并保存到磁盘
     * @param url
     * @throws IOException
     */
    public void saveBinaryFile(URL url) throws IOException {
        URLConnection uc = url.openConnection();

        // 文件类型
        String contentType = uc.getContentType();

        // 文件大小
        int contentLength = uc.getContentLength();
        if (contentType.startsWith("text/") || contentLength == -1) {
            throw new IOException("这不是二进制文件");
        }

        InputStream raw = uc.getInputStream();
        InputStream in = new BufferedInputStream(raw);

        // 这个二进制文件所需的字节
        byte [] data = new byte[contentLength];

        // 当前读取的字节数量，不断累加
        int offset = 0;
        while (offset < contentLength) {
            // data 读取数据的缓冲区
            // offset 写入数据的数组 b中的起始偏移量
            // len 要读取的最大字节数 ..自然越来越少了..
            int bytesRead = in.read(data, offset, data.length - offset);
            // 结束了
            if (bytesRead == -1) break;
            // 越来越大
            offset += bytesRead;
        }

        if (offset != contentLength) {
            throw new IOException("只读取 " + offset + " bytes; 应该是 " + contentLength + " bytes");
        }

        String filename = url.getFile();
        filename = filename.substring(filename.lastIndexOf("/") + 1);

        try (FileOutputStream fout = new FileOutputStream(filename)) {
            fout.write(data);
            fout.flush();
        }
    }

    /**
     * 显式整个HTTP首部
     * @param urlStr
     * @throws IOException
     */
    public void allHeaders(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        URLConnection uc = url.openConnection();
        for (int j = 1; ; j++) {
            // 第n个首部字段的值
            String header = uc.getHeaderField(j);
            if (header == null) break;
            // 第n个首部字段的键
            logger.info(uc.getHeaderFieldKey(j) + " : " + header);
        }

    }

    /**
     * 提交一个表单
     * @param urlStr
     * @throws IOException
     */
    public void post(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        URLConnection uc = url.openConnection();
        uc.setDoOutput(true);

        OutputStreamWriter out = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        out.write("query..."); // 表单字符串
        out.write("\r\n");
        out.flush();
        out.close();

    }





}
