package top.kaoshanji.leaning.jdkx.io.template.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 同步阻塞 I/O 客户端
 * @author kaoshanji
 * @time 2020年2月22日 下午8:12:12
 */
public class BioTimeClient {

	public static void main(String[] args) {
		System.out.println("BioTimeClient客户端启动......");
		
		int port = 8080;
		
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			// 构造socket
			socket = new Socket("127.0.0.1", port);
			
			// 初始化 输入/输出 字符流
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			// 向服务端发送请求
			String req = "服务端你好，我是客户端A，请告诉我当前时间.";
			out.println(req);
			
			System.out.println("发送消息成功!");
			
			// 获取服务端响应
			String res = in.readLine();
			System.out.println("服务端响应： "+ res);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
			
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
			
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket = null;
			}
		}
	}
}
