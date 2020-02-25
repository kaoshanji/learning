package top.kaoshanji.leaning.jdkx.io.template.nio;

/**
 * 多路复用 I/O 服务端
 * 	接入请求、处理读写事件，分别使用线程池
 *  Reactor模型
 * @author kaoshanji
 * @time 2020年2月22日 下午10:11:07
 */
public class NioTimeServer {
	
	public static void main(String[] args) {
		System.out.println("NioTimeServer服务端启动......");
		int port = 8899;
		
		NioTimeServerHandler timeServer = new NioTimeServerHandler(port);
		Thread t = new Thread(timeServer);
		t.start();
	}

}
