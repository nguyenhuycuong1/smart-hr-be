package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.MailAccount;
import com.devcuong.smart_hr.dto.MailSenderDTO;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Properties;

@Service
public class MailService {

    @Autowired
    private MailAccountService mailAccountService;

    public void sendMail(MailSenderDTO mailSenderDTO) throws MessagingException {
        // Kiểm tra thông tin bắt buộc
        if (mailSenderDTO.getMailAccountId() == null) {
            throw new IllegalArgumentException("Người gửi không được để trống");
        }
        if (mailSenderDTO.getTo() == null || mailSenderDTO.getTo().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ email người nhận không được để trống");
        }
        if (mailSenderDTO.getSubject() == null || mailSenderDTO.getSubject().isEmpty()) {
            throw new IllegalArgumentException("Chủ đề email không được để trống");
        }
        if (mailSenderDTO.getContent() == null || mailSenderDTO.getContent().isEmpty()) {
            throw new IllegalArgumentException("Nội dung email không được để trống");
        }
        MailAccount mailAccount = mailAccountService.getMailAccountById(mailSenderDTO.getMailAccountId());
        String username = mailAccount.getUsername();
        String password = mailAccount.getPassword();

        // Cấu hình SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Tạo session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Tạo message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, parseEmailAddresses(mailSenderDTO.getTo()));

        if (mailSenderDTO.getCc() != null && !mailSenderDTO.getCc().isEmpty()) {
            message.setRecipients(Message.RecipientType.CC, parseEmailAddresses(mailSenderDTO.getCc()));
        }

        if (mailSenderDTO.getBcc() != null && !mailSenderDTO.getBcc().isEmpty()) {
            message.setRecipients(Message.RecipientType.BCC, parseEmailAddresses(mailSenderDTO.getBcc()));
        }

        message.setSubject(mailSenderDTO.getSubject());
        message.setContent(mailSenderDTO.getContent(), "text/html; charset=utf-8");

        Transport.send(message);
    }

    private InternetAddress[] parseEmailAddresses(String emails) throws AddressException {
        return Arrays.stream(emails.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .map(email -> {
                    try {
                        return new InternetAddress(email);
                    } catch (AddressException e) {
                        throw new RuntimeException("Invalid email address: " + email, e);
                    }
                })
                .toArray(InternetAddress[]::new);
    }
}

