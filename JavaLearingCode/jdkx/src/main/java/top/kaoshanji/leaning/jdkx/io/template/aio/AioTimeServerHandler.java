package top.kaoshanji.leaning.jdkx.io.template.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * 异步通道主流程
 * @author kaoshanji
 * @time 2020年2月24日 下午4:42:14
 */
public class AioTimeServerHandler implements Runnable {

	public CountDownLatch latch;
	public AsynchronousServerSocketChannel assc;
	
	public AioTimeServerHandler(int port) {
		
		try {
			// 创建一个异步通道
			// 使用默认线程池
			assc = AsynchronousServerSocketChannel.open();
			assc.bind(new InetSocketAddress(port));
			
			System.out.println("服务器在： " + port + " 端口启动");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// CountDownLatch ：在完成一组正在执行的操作之前，允许当前的线程一直阻塞
		// 此处是为了防止服务端执行完成后退出..仅在示例中使用
		latch = new CountDownLatch(1);
		
		// 连接建立回调 AioTimeServerAcceptHandler
		// 回调式
		assc.accept(this, new AioTimeServerAcceptHandler());
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
