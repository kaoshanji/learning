package top.kaoshanji.leaning.jdkx.io.template.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 异步读取消息结之后作处理逻辑
 * 当系统将数据读取到 buff 中，会回调此 Handler
 * @author kaoshanji
 * @time 2020年2月24日 下午5:07:35
 */
public class AioTimeServerReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	
	// 异步通道：读取消息和发送应答
	private AsynchronousSocketChannel asc;
	
	public AioTimeServerReadHandler(AsynchronousSocketChannel asc) {
		this.asc = asc;
	}

	// 读取消息成功
	@Override
	public void completed(Integer byteNum, ByteBuffer readBuff) {
		if (byteNum <= 0) {
			return;
		}
		// buf 已经填充好数据，只需处理就行
		readBuff.flip();
		
		// 转换请求内容..解码
		String body = Charset.forName(Charsets.UTF_8.name()).decode(readBuff).toString();
		System.out.println("服务器接收的内容：" + body);
		readBuff.clear(); // 清空 buf，用于下次读取
		
		// 向客户端发送消息
		doWritable();
	}

	// 读取消息失败
	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.asc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 发送消息
	private void doWritable() {

		// 响应的内容
		String res = "客户端好，现在服务端的时间是：" + DateFormatUtils.ISO_DATETIME_FORMAT.format(System.currentTimeMillis());
		
		try {
			// 转换响应内容..编码
			ByteBuffer writeBuff = ByteBuffer.wrap(res.getBytes(Charsets.UTF_8.name()));
		
			// 异步写数据，与前面 read 一样
			asc.write(writeBuff, writeBuff, new CompletionHandler<Integer, ByteBuffer>() {
				
				// 写操作结束回调 handler
				@Override
				public void completed(Integer result, ByteBuffer buffer) {
					// 如果没有发送完，就继续发送直到完成
					if (buffer.hasRemaining()) {
						asc.write(buffer, buffer, this);
					} else {
						// 创建新的 buff
						ByteBuffer readBuffer = ByteBuffer.allocate(1024);
						// 异步读，第三个参数为接收消息回调的业务 handler
						asc.read(readBuffer, readBuffer, new AioTimeServerReadHandler(asc));
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer buffer) {
					try {
						asc.close();
					} catch (IOException e) {
						e.printStackTrace();
					}					
				}
				
			});
		
		} catch (Exception e) {
			try {
				asc.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}


}
