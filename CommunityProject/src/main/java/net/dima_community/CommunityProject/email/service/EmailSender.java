package net.dima_community.CommunityProject.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.email.domain.Email;
import net.dima_community.CommunityProject.email.domain.EmailProperties;

@Component
@Slf4j
public class EmailSender {

    private final EmailProperties emailProperties;
    private final JavaMailSender mailSender;

    @Autowired
    public EmailSender(EmailProperties emailProperties, JavaMailSender javaMailSender) {
        this.emailProperties = emailProperties;
        this.mailSender = javaMailSender;
    }

    public String sendMail(Email email) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        log.info(email.toString());
        message.setTo(email.getTo());
        message.setFrom(emailProperties.getUsername());
        message.setSubject(email.getTitle());
        message.setText(email.getContent());

        mailSender.send(message);
        return "Success";
    }
}
