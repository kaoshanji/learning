package top.kaoshanji.leaning.jdkx.io.template.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 同步非阻塞主流程
 * @author kaoshanji
 * @time 2020年2月22日 下午9:21:21
 */
public class NioTimeServerHandler implements Runnable {
	
	// 多路复用器
	private	Selector selector;
	private ServerSocketChannel serverChannel;
	private volatile boolean stop = true;
	
	/**
	 * 初始化属性
	 * 绑定监听端口
	 * @param port
	 */
	public NioTimeServerHandler(int port) {
		try {
			// 不是new，而是 open，open
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			
			// 设置非阻塞
			serverChannel.configureBlocking(false);
			// 绑定监听的端口，设置 连接队列最大为 1024
			serverChannel.socket().bind(new InetSocketAddress(port), 1024);
			// 注册复用器，指定接收 连接事件
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("服务器在： " + port + " 端口启动");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public void run() {
		while(stop) {
			try {
				selector.select(1000); // 堵塞时间
				
				// 把事件封装成了键
				// 如果有事件产生，就取出这些事件，并遍历
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				
				// 每个元素key表示一个连接
				while(it.hasNext()) {
					SelectionKey key = it.next();
					// 如果此事件已处理，就需要从现有集合中删除
					it.remove();
					try {
						
						// 有效的连接
						if (key.isValid()) {
							// 服务端一般是先读后写
							// 判断是否是一个有效的 连接事件
							if (key.isAcceptable()) {
								this.doAcceptable(key);
							}
							// 判断是否是一个有效的 可读事件
							if (key.isReadable()) {
								this.doReadable(key);
							}
							// 判断是否是一个有效的 可写事件
							if (key.isWritable()) {
								this.doWritable(key);
							}
						}
						
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
				
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		// 复用器关闭后，所有注册在上面的 channel 都会被自动去注册并关闭，所以不需要重复释放资源
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 处理 连接事件
	 * @param key
	 * @throws IOException 
	 */
	private void doAcceptable(SelectionKey key) {
		
		// OP_ACCEPT 事件返回时的 ServerSocketChannel
		ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
		SocketChannel sc = null;
		try {
			// 获得连接
			sc = ssc.accept();
			if (null != sc) {
				System.out.println("接入客户端请求："+sc.getRemoteAddress());
				sc.configureBlocking(false);
				// 向新建的 socket 通道上注册可读事件，读完数据才会向客户端返回
				// 第三个参数可以是 ByteBuffer，当做附件传递，在后面事件再获取，这样一个连接一个 ByteBuffer，也不需要创建
				sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
			}
			
		} catch (Exception e) {
			try {
				key.cancel();
				sc.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * 处理 可读事件
	 * @param key
	 */
	private void doReadable(SelectionKey key) {
		// 可读事件返回的是 SocketChannel
		SocketChannel sc = (SocketChannel)key.channel();
		
		// 处理连接事件时传递过来的附件
		ByteBuffer readBuff = (ByteBuffer)key.attachment();
		
		try {
			int readByteSize = 0;
			readBuff.clear();
			while((readByteSize = sc.read(readBuff)) > 0) {
				System.out.println("服务器接收中......");
			}
			readBuff.flip(); // 读取数据之后第一行
			
			// 转换请求内容..解码
			String body = Charset.forName(Charsets.UTF_8.name()).decode(readBuff).toString();
			System.out.println("服务器接收的内容：" + body);
			readBuff.clear(); // 清空 buf，用于下次读取
			
			// 读取数据结束后注册可写事件
			sc.register(key.selector(), SelectionKey.OP_WRITE);
		} catch (Exception e) {
			try {
				key.cancel();
				sc.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * 处理 可写事件
	 * @param key
	 */
	private void doWritable(SelectionKey key) {
		// 可写事件返回的是 SocketChannel
		SocketChannel sc = (SocketChannel)key.channel();
		// 响应的内容
		String res = "客户端好，现在服务端的时间是：" + DateFormatUtils.ISO_DATETIME_FORMAT.format(System.currentTimeMillis());
		
		try {
			// 转换请求内容..编码
			ByteBuffer writeBuffer = ByteBuffer.wrap(res.getBytes(Charsets.UTF_8.name()));
		
			int writeByteSize = 0;
			while(writeBuffer.hasRemaining() && (writeByteSize = sc.write(writeBuffer)) > 0) {
				System.out.println("服务器发送中......");
			}

		} catch (Exception e) {
			try {
				key.cancel();
				sc.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
}
