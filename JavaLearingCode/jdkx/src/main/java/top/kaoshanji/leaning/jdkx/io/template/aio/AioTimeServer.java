package top.kaoshanji.leaning.jdkx.io.template.aio;

/**
 * 异步 I/O 服务端
 *  回调式
 * @author kaoshanji
 * @time 2020年2月24日 下午4:40:44
 */
public class AioTimeServer {

	public static void main(String[] args) {
		System.out.println("AioTimeServer服务端启动......");
		int port = 8899;
		
		AioTimeServerHandler timeServer = new AioTimeServerHandler(port);
		Thread t = new Thread(timeServer);
		t.start();
	}

}
