package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于文件系统中静态文件的HTTP服务器
 * 处理 HTTP 请求时采用异步套接字通道
 * @author kaoshanji
 * @time 2020-01-11 20:13
 */
public class StaticFileHttpServer {

    private final static Logger logger = LoggerFactory.getLogger(StaticFileHttpServer.class);


    private static final Pattern PATH_EXTRACTOR = Pattern.compile("GET (.*?) HTTP");
    private static final String INDEX_PAGE = "index.html";

    public void start(final Path root) throws IOException {

        // 异步通道组与线程池绑定
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(10,
                Executors.defaultThreadFactory());
        final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(group)
                .bind(new InetSocketAddress(10080));

        // 监听请求...
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            // 接入一个请求 ==> 处理逻辑..
            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                serverChannel.accept(null, this);
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    clientChannel.read(buffer).get();
                    buffer.flip();

                    String request = new String(buffer.array());
                    // 文件路径
                    String requestPath = extractPath(request);

                    Path filePath = getFilePath(root, requestPath);
                    if (!Files.exists(filePath)) {
                        String error404 = generateErrorResponse(404, "Not Found");
                        clientChannel.write(ByteBuffer.wrap(error404.getBytes()));
                        return;
                    }

                    logger.info("处理请求："+requestPath);

                    // 响应头 HTTP 协议是一种数据格式
                    String header = generateFileContentResponseHeader(filePath);
                    clientChannel.write(ByteBuffer.wrap(header.getBytes())).get();

                    // 最后还是用流响应输出..
                    Files.copy(filePath, Channels.newOutputStream(clientChannel));

                } catch (Exception e) {
                    String error = generateErrorResponse(500, "Internal Server Error");
                    clientChannel.write(ByteBuffer.wrap(error.getBytes()));
                    logger.error(e.getMessage());
                } finally {
                    try {
                        clientChannel.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            @Override
            public void failed(Throwable throwable, Void attachment) {
                logger.error(throwable.getMessage());
            }
        });

    }


    private String extractPath(String request) {
        Matcher matcher = PATH_EXTRACTOR.matcher(request);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 处理请求地址到文件路径
     * @param root
     * @param requestPath
     * @return
     */
    private Path getFilePath(Path root, String requestPath) {
        if (requestPath == null || "/".equals(requestPath)) {
            requestPath = INDEX_PAGE;
        }

        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }

        int pos = requestPath.indexOf("?");
        if (pos != -1) {
            requestPath = requestPath.substring(0, pos);
        }
        return root.resolve(requestPath);
    }

    private String getContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

    /**
     * HTTP 正常响应
     * @param filePath
     * @return
     * @throws IOException
     */
    private String generateFileContentResponseHeader(Path filePath) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 200 OK\r\n");
        builder.append("Content-Type: ");
        builder.append(getContentType(filePath));
        builder.append("\r\n");
        builder.append("Content-Length: " + Files.size(filePath) + "\r\n");
        builder.append("\r\n");
        return builder.toString();

    }

    /**
     * HTTP 异常响应
     * @param statusCode
     * @param message
     * @return
     */
    private String generateErrorResponse(int statusCode, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 " + statusCode + " " + message + "\r\n");
        builder.append("Content-Type: text/plain\r\n");
        builder.append("Content-Length: " + message.length() + "\r\n");
        builder.append("\r\n");
        builder.append(message);
        return builder.toString();
    }

}
