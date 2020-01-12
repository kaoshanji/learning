package top.kaoshanji.leaning.jdkx.io;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;

/**
 * DatagramChannel 客户端
 * 接收组播消息的客户端实现
 * @author kaoshanji
 * @time 2020-01-11 18:37
 */
public class DatagramTimeClientDemo {

    public void start() throws IOException {
        NetworkInterface ni = NetworkInterface.getByName("eth1");
        int port = 5000;

        DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(port))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);

        InetAddress group = InetAddress.getByName("224.0.0.2");
        MembershipKey key = dc.join(group, ni);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        dc.receive(buffer);

        buffer.flip();
        byte [] data = new byte[buffer.limit()];
        buffer.get(data);

        String str = new String(data);
        System.out.println(str);

        key.drop();
    }


}
