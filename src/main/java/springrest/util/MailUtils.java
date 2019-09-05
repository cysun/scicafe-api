package springrest.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailUtils {

	public static void sendMail(String to, String subject, String content) throws AddressException, MessagingException {
		
		
		//1. Create email connection
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		try {
			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// TODO Auto-generated method stub
					return new PasswordAuthentication("chenyii426@gmail.com","wjdh85247890");
				}
			});
			
			//2. Create email
			Message message = new MimeMessage(session);
			
			//2.1 Set sender
			message.setFrom(new InternetAddress("18060487484@163.com"));
			
			//2.2 Set Recipients
			message.setRecipient(RecipientType.TO, new InternetAddress(to));
		
			//2.3 Set Subject
			message.setSubject(subject);
			
			//2.4 Set content
			message.setContent(content, "text/html;charset=UTF-8");
			
			//3 Send email
			Transport.send(message);
			
			System.out.println("邮件成功发送!");
		}  catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
}
