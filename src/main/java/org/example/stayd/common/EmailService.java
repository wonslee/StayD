package org.example.stayd.common;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/** 이메일 발송*/
public class EmailService {
    private final Session session;

    /**
     * Gmail SMTP
     *
     * @param user Gmail 주소
     * @param appPassword 앱 비밀번호
     */
    public EmailService(String user, String appPassword) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, appPassword);
            }
        });
    }

    /**
     * 인증 코드 이메일 전송
     */
    public void sendVerificationCode(String toEmail, String code) throws MessagingException {
        String subject = "[StayD] 이메일 인증 코드";
        String text = String.format(
                "안녕하세요, StayD입니다.%n%n회원가입 인증 코드: %s%n%n10분 이내에 입력해주세요.",
                code
        );
        sendEmail(toEmail, subject, text);
    }

    /**
     * 커스텀 메일 전송 (제목/내용 지정)
     */
    public void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("no-reply@stayd.com"));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
        );
        message.setSubject(subject);
        message.setText(body);
        Transport.send(message);
    }
}
