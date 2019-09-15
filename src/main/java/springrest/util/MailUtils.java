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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailUtils {

	private static String EMAILHOST;
	
	private static String EMAILPORT;
	
	private static String EMAILADRRESS;
	
	private static String PASSWORD;
	
	@Value("${scicafe.email.host}")
	public void setEMAILHOST(String host) {
		this.EMAILHOST = host;
	}

	@Value("${scicafe.email.port}")
	public void setEMAILPORT(String port) {
		this.EMAILPORT = port;
	}

	@Value("${scicafe.email.address}")
	public void setEMAILADRRESS(String address) {
		this.EMAILADRRESS = address;
	}

	@Value("${scicafe.email.password}")
	public void setPASSWORD( String password) {
		this.PASSWORD = password;
	}

	public static void sendMail(String to, String subject, String content) throws AddressException, MessagingException {
		
		
		//1. Create email connection
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", EMAILHOST);
		props.put("mail.smtp.port", EMAILPORT);
		try {
			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// TODO Auto-generated method stub
					return new PasswordAuthentication(EMAILADRRESS,PASSWORD);
				}
			});
			
			//2. Create email
			Message message = new MimeMessage(session);
			
			//2.1 Set sender
			message.setFrom(new InternetAddress(EMAILADRRESS));
			
			//2.2 Set Recipients
			message.setRecipient(RecipientType.TO, new InternetAddress(to));
		
			//2.3 Set Subject
			message.setSubject(subject);
			
			//2.4 Set content
			message.setContent(content, "text/html;charset=UTF-8");
			
			//3 Send email
			Transport.send(message);
			
			System.out.println("Sent email successfully!");
		}  catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
}
