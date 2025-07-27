// 작성자 : 방대혁
package org.example.stayd.common;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 이메일 발송을 위한 서비스 클래스. Gmail SMTP를 이용하여 이메일을 전송.
 */
public class EmailService {

    private final Session session;

    /**
     * 이메일 발송을 위한 세션 설정 (Gmail SMTP 사용).
     *
     * @param user        Gmail 주소
     * @param appPassword Gmail 앱 비밀번호
     */
    public EmailService(String user, String appPassword) {
        // 이메일 서버 설정
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // 인증 정보를 기반으로 세션 생성
        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, appPassword);
            }
        });
    }

    /**
     * 인증 코드 이메일 전송.
     *
     * @param toEmail 수신자 이메일 주소
     * @param code    인증 코드
     * @throws MessagingException 이메일 발송 중 오류 발생 시
     */
    public void sendVerificationCode(String toEmail, String code) throws MessagingException {

        String subject = "[StayD] 이메일 인증 코드";
        String text = String.format(
                "안녕하세요, StayD입니다.%n%n회원가입 인증 코드: %s%n%n10분 이내에 입력해주세요.",
                code
        );
        sendEmail(toEmail, subject, text);  // 이메일 발송
    }

    /**
     * 커스텀 메일 전송 (제목과 내용 지정 가능).
     *
     * @param toEmail 수신자 이메일 주소
     * @param subject 이메일 제목
     * @param body    이메일 내용
     * @throws MessagingException 이메일 발송 중 오류 발생 시
     */
    public void sendEmail(String toEmail, String subject, String body) throws MessagingException {

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("no-reply@stayd.com"));  // 발신자 이메일 설정
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)  // 수신자 이메일 설정
        );
        message.setSubject(subject);  // 이메일 제목 설정
        message.setText(body);  // 이메일 내용 설정

        Transport.send(message);  // 이메일 전송
    }
}
