package top.kaoshanji.leaning.jdkx.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 多路复用 Selector
 * @author kaoshanji
 * @time 2020-01-05 23:19
 */
public class SelectorClientDemo {

    /**
     * 多路复用
     * 客户端 套接字
     * @param urls
     * @throws IOException
     */
    public void loadWebPageBySelector(Set<URL> urls) throws IOException {
        Map<SocketAddress, String> mapping = urlToSocketAddress(urls);
        Selector selector = Selector.open();
        for (SocketAddress address : mapping.keySet()) {
            register(selector, address);
        }

        int finished = 0, total = mapping.size();
        ByteBuffer buffer = ByteBuffer.allocate(32 * 1024);
        int len = -1;
        while (finished < total) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isValid() && key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    InetSocketAddress address = (InetSocketAddress)channel.getRemoteAddress();
                    String filename = address.getHostName() + ".txt";
                    FileChannel destChannel = FileChannel.open(Paths.get(filename), StandardOpenOption.APPEND,
                            StandardOpenOption.CREATE);

                    buffer.clear();

                    while ((len = channel.read(buffer)) > 0 || buffer.position() != 0) {
                        buffer.flip();
                        destChannel.write(buffer);
                        buffer.compact();
                    }

                    if (len == -1) {
                        finished++;
                        key.cancel();
                    }

                } else if (key.isReadable() && key.isConnectable()) {
                    SocketChannel channel = (SocketChannel)key.channel();
                    boolean success = channel.finishConnect();
                    if (!success) {
                        key.cancel();
                    } else {
                        InetSocketAddress address = (InetSocketAddress)channel.getRemoteAddress();
                        String path = mapping.get(address);
                        String request = "GET " + path + " HTTP/1.0\r\n\r\nHost: " + address.getHostString() + "\r\n\r\n";
                        ByteBuffer header = ByteBuffer.wrap(request.getBytes("UTF-8"));
                        channel.write(header);
                    }
                }
            }
        }
    }



    private void register(Selector selector, SocketAddress address) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
    }

    private Map<SocketAddress, String> urlToSocketAddress(Set<URL> urls) {
        Map<SocketAddress, String> mapping = new HashMap<>();
        for (URL url : urls) {
            int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();
            SocketAddress address = new InetSocketAddress(url.getHost(), port);
            String path = url.getPath();
            if (url.getQuery() != null) {
                path = path + "?" + url.getQuery();
            }
            mapping.put(address, path);
        }
        return mapping;
    }

}
