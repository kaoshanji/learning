package top.kaoshanji.leaning.jdkx.io.template.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 同步阻塞 I/O 服务端
 * 每个请求对应一个线程
 * 考虑使用线程池，但是不能避免自身的问题
 *  读和写操作都是同步阻塞，阻塞时间取决于对方I/O线程的处理速度和网络I/O的传输速度
 *  线程池只是对I/O线程模型做了改进，但同步I/O还是会导致通信线程阻塞
 * @author kaoshanji
 * @time 2020年2月22日 下午8:02:40
 */
public class BioTimeServer {
	
	public static void main(String[] args) throws IOException {
		System.out.println("BioTimeServer服务端启动......");
		int port = 8080;
		
		ServerSocket server = null;
		try {
			// 绑定端口，大于 1024 即可
			server = new ServerSocket(port);
			System.out.println("服务器在端口： "+port+" 启动");
			// 每个请求都代表一个连接
			Socket socket = null;
			// 死循环等待连接
			while (true) {
				// 阻塞，等待请求到达
				socket = server.accept();
				
				// 每一个连接都交给一个线程处理
				Thread t = new Thread(new BioTimeServerHandler(socket));
				t.start();
			}
		} finally {
			if (server != null) {
				System.out.println("服务器关闭");
				server.close();
				server = null;
			}
		}
	}

}
