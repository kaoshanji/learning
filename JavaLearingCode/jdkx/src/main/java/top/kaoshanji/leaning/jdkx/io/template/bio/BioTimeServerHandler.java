package top.kaoshanji.leaning.jdkx.io.template.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 时间 逻辑处理
 * @author kaoshanji
 * @time 2020年2月22日 下午7:41:48
 */

public class BioTimeServerHandler implements Runnable {
	
	private Socket socket;
	
	public BioTimeServerHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			// 从 socket 中获得 输入和输出 字符流 来初始化
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			out = new PrintWriter(this.socket.getOutputStream(), true); // 自动冲刷
			
			String req = null;
			
			while (true) {
				// 获得 请求内容
				req = in.readLine();
				if (null == req) {
					break;
				}
				
				System.out.println("服务器接收的内容：" + req);
				String res = "客户端好，现在服务端的时间是：" + DateFormatUtils.ISO_DATETIME_FORMAT.format(System.currentTimeMillis());
				// 响应请求
				out.println(res);
			}
			
		} catch (Exception e) {
			
			if (in != null) {
				try {
					in.close();
				} catch (IOException el) {
					el.printStackTrace();
				}
			}
			
			if (out != null) {
				out.close();
				out = null;
			}
			
			if (this.socket != null) {
				try {
					this.socket.close();
				} catch (IOException el) {
					el.printStackTrace();
				}
				this.socket = null;
			}
		}
	}
}
