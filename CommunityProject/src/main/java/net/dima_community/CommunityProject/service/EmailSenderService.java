package net.dima_community.CommunityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.domain.Email;
import net.dima_community.CommunityProject.domain.EmailProperties;

@Component
@Slf4j
public class EmailSenderService {

    private final EmailProperties emailProperties;
    private final JavaMailSender mailSender;

    @Autowired
    public EmailSenderService(EmailProperties emailProperties, JavaMailSender javaMailSender) {
        this.emailProperties = emailProperties;
        this.mailSender = javaMailSender;
    }

    public boolean sendMail(Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        log.info(email.toString());
        message.setTo(email.getTo());
        message.setFrom(emailProperties.getUsername());
        message.setSubject(email.getTitle());
        message.setText(email.getContent());

        try {
            mailSender.send(message);
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }

        return true;
    }
}
