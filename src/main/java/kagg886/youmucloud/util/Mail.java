package kagg886.youmucloud.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class Mail {

    private static final Session mailSession;
    private static final InternetAddress user;
    private static final Properties props;

    public static void sendMessage(String To ,String title,String... content) throws Exception {

        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(user);
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(To));
        message.setSubject(title);
        StringBuilder ss = new StringBuilder();
        for (String s : content) {
            ss.append(s.replace("\n", "<br>"));
        }
        message.setContent(ss.toString(), "text/html;charset=UTF-8");
        Transport.send(message);

    }

    static {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.user", "kagg886@qq.com");
        props.put("mail.password", "emdyvuzvcgmlcbea");
        mailSession = Session.getInstance(props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(props.getProperty("mail.user"), props.getProperty("mail.password"));
            }
        });
        try {
            user = new InternetAddress(props.getProperty("mail.user"));
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }
}
