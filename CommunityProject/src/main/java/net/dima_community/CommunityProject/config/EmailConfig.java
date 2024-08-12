package net.dima_community.CommunityProject.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.domain.EmailProperties;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {

    private final EmailProperties emailProperties;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.emailProperties.getHost());
        mailSender.setPort(this.emailProperties.getPort());
        mailSender.setUsername(this.emailProperties.getUsername());
        mailSender.setPassword(this.emailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
//mailSender  :진짜 메일 보내는 애. 메일 보내려면 보내는 사람을 설정해야하는데 properties꺼 가져와서 설정해주는거임. 