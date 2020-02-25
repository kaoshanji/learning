package top.kaoshanji.leaning.jdkx.io.template.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;

/**
 * 异步 I/O 客户端
 * @author kaoshanji
 * @time 2020年2月24日 下午5:54:15
 */
public class AioTimeClient {

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("AioTimeClient客户端启动......");
		String host = "127.0.0.1";
		int port = 8899;
		
		AsynchronousSocketChannel asc = AsynchronousSocketChannel.open();
		
		// 连接到服务器并处理连接结果
		asc.connect(new InetSocketAddress(host, port), null, new CompletionHandler<Void, Void>() {

			@Override
			public void completed(Void result, Void attachment) {
				System.out.println("成功连接到服务器...");
				
				// 连接建立就可读可写，只是客户端一般先写后读，操作有个对应的回调处理逻辑就行
				try {
					// 向服务器发送信息并等待发送完成
					String req = "服务端你好，我是客户端A，请告诉我当前时间.";
					ByteBuffer writeBuff = ByteBuffer.wrap(req.getBytes(Charsets.UTF_8.name()));// 编码
					asc.write(writeBuff).get(); // 将来式
					
					// 阻塞等待接收服务端响应
					ByteBuffer readBuff = ByteBuffer.allocate(1024);
					asc.read(readBuff).get();
					readBuff.flip();
					String res = Charset.forName(Charsets.UTF_8.name()).decode(readBuff).toString(); // 解码
					if (StringUtils.isNotEmpty(res)) {
						System.out.println("服务端响应： "+ res);
					}
					
				} catch (InterruptedException | ExecutionException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				try {
					asc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		TimeUnit.MINUTES.sleep(Integer.MAX_VALUE);
	}
}
