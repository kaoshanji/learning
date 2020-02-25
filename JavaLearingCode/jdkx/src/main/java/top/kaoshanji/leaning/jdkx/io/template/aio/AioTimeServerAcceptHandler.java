package top.kaoshanji.leaning.jdkx.io.template.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


/**
 * 连接请求结束之后处理类
 * 当异步 socket 服务接收到一个请求时，会回调此 Handler
 * 对接收到的请求进行处理
 * @author kaoshanji
 * @time 2020年2月24日 下午4:55:29
 */
public class AioTimeServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioTimeServerHandler>{

	// 连接建立成功
	@Override
	public void completed(AsynchronousSocketChannel asc, AioTimeServerHandler attachment) {
		// 继续接收其他客户端连接，就像是死循环一样继续
		attachment.assc.accept(attachment, this);
		
		// 创建新的 buf，用作读取数据
		ByteBuffer readBuff = ByteBuffer.allocate(1024);
		
		// 连接建立之后，可读可写，只是服务端一般是先读再写，操作有个对应的回调处理逻辑就行
		// 异步读，结束之后自动回调 AioTimeServerReadHandler
		asc.read(readBuff, readBuff, new AioTimeServerReadHandler(asc));
	}

	// 连接建立失败
	@Override
	public void failed(Throwable exc, AioTimeServerHandler attachment) {
		exc.printStackTrace();
		attachment.latch.countDown();
	}

}
