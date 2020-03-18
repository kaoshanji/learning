package top.kaoshanji.learning.scenes.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * MailDemo
 * @author kaoshanji
 * @time 2020年3月18日 上午10:41:14
 */
public class MailDemo {

	public static void main(String[] args) throws MessagingException {
		
	    String sendUserName = "test@aesglobalonline.com"; 
	    String sendPassword = "test@aesglobalonline.com"; 
	      
	    Properties properties = new Properties(); 
	    properties.setProperty("mail.transport.protocol", "smtp");//指定邮件发送的协议，参数是规范规定的
	    properties.setProperty("mail.smtp.auth", "true");//服务器需要认证 	   
	  //  properties.setProperty("mail.host", "imap-ox.front.d0m.de");//指定发件服务器的地址，参数是规范规定的
	  //  properties.setProperty("mail.smtp.port", "1143");
	    
	  //发送邮件时使用的环境配置
	    Session session = Session.getInstance(properties); 
	    session.setDebug(true);//同意在当前线程的控制台打印与服务器对话信息 
	    
	  //设置邮件的头
	    MimeMessage message = new MimeMessage(session);//构建发送的信息 
	    message.setFrom(new InternetAddress("test@aesglobalonline.com")); //发件人 
	    message.setRecipients(Message.RecipientType.TO, "1127102203@qq.com"); // 收件人
	    message.setText("你好，我是Champion.Wong！");//信息内容  纯文本
	    message.saveChanges();
	    
	    Transport transport = session.getTransport(); 
	    
	 //   transport.connect(sendUserName, sendPassword);//连接发件人使用发件的服务器  // 密码为授权码不是邮箱的登录密码
	    transport.connect("smtp-ox.front.d0m.de", 465, sendUserName, sendPassword);
	    transport.sendMessage(message, message.getAllRecipients());
	    transport.close(); 
	}

}
