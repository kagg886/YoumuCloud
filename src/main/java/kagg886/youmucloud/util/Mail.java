package kagg886.youmucloud.util;

import kagg886.qinternet.Interface.MsgIterator;
import kagg886.qinternet.Message.MsgCollection;

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

    static {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.user", "kagg886@qq.com");
        props.put("mail.password", "hkumwfxjckxacafh");
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

    public static void sendMessage(String To, String title, MsgCollection col) throws Exception {
        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(user);
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(To));
        message.setSubject(title);
        StringBuilder h5 = new StringBuilder();
        col.iterator(new MsgIterator() {
            @Override
            public void onText(String s) {
                h5.append(col.getTexts().replace("\n", "<br>"));
            }

            @Override
            public void onImage(String s) {
                h5.append("<img src='" + s + "'><br>");
            }

            @Override
            public void onXml(String s) {
                h5.append("不支持的xml消息:" + s + "\n");
            }

            @Override
            public void onJson(String s) {
                h5.append("不支持的json消息:" + s + "\n");
            }

            @Override
            public void onPtt(String s) {

            }

            @Override
            public void onAt(long l) {

            }
        });
        message.setContent(h5.toString(), "text/html;charset=UTF-8");
        Transport.send(message);
    }
}
