package net.dima_community.CommunityProject.Email;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.mail.MessagingException;
import net.dima_community.CommunityProject.email.domain.Email;
import net.dima_community.CommunityProject.email.service.EmailSender;

@SpringBootTest
public class EmailSenderTest {

    @Autowired
    private EmailSender emailSender;

    @Test
    public void Email_객체를_이용해_메일을_보낼_수_있다() {
        // given
        Email email = Email.builder()
                .to("ssehn9327@gmail.com")
                .title("디마 커뮤니티 회원가입 인증코드입니다.")
                .content("인증번호는 1234입니다.")
                .build();
        // when
        String result = "Fail";
        try {
            result = emailSender.sendMail(email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        // then
        assertThat(result).isEqualTo("Success");
    }

}
