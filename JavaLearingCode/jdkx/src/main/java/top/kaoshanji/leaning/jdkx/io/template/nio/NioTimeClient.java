package top.kaoshanji.leaning.jdkx.io.template.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;

/**
 * 多路复用 I/O 客户端
 * @author kaoshanji
 * @time 2020年2月22日 下午10:45:58
 */
public class NioTimeClient {
	
	public static void main(String[] args) {
		System.out.println("NioTimeClient客户端启动......");
		String host = "127.0.0.1";
		int port = 8899;
		SocketChannel sc = null;
		
		try {
			sc = SocketChannel.open();
			sc.configureBlocking(true); // 客户端使用阻塞模式
			sc.connect(new InetSocketAddress(host, port));
			
			if (!sc.finishConnect()) {
				System.out.println("NioTimeServer服务端还未启动......");
				return;
			}
		
			// 向服务端发送请求
			String req = "服务端你好，我是客户端A，请告诉我当前时间.";
			ByteBuffer writebuf = ByteBuffer.wrap(req.getBytes(Charsets.UTF_8.name()));// 编码
			sc.write(writebuf); // 写入数据
			System.out.println("请求发送成功!");

			// 读取服务端响应
			ByteBuffer readbuf = ByteBuffer.allocate(1024);
			int bufSize = sc.read(readbuf); // 读取数据
			
			String res = null;
			if (bufSize > 0) {
				readbuf.flip();
				res = Charset.forName(Charsets.UTF_8.name()).decode(readbuf).toString(); // 解码
			}
			if (StringUtils.isNotEmpty(res)) {
				System.out.println("服务端响应： "+ res);
			}
			
		} catch (IOException e) {
			try {
				sc.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
