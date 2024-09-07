package net.dima_community.CommunityProject.Email;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import jakarta.mail.MessagingException;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.dto.member.MemberDTO;
import net.dima_community.CommunityProject.email.controller.EmailController;

@SpringBootTest
public class EmailControllerTest {

    @Autowired
    private EmailController emailController;

    @Test
    public void 받는사람_이메일을_보내_인증_코드_이메일을_보낼_수_있다() throws MessagingException {
        // given
        MemberDTO member = MemberDTO.builder()
                .memberId("ssehn9327")
                .memberPw("qqqqqqqq")
                .memberEnabled("N")
                .memberRole("user")
                .memberName("심세현")
                .memberGroup("3기")
                .memberPhone("010-5842-8584")
                .memberEmail("ssehn9327@gmail.com")
                .build();
        // when
        // boolean result = emailController.send(member);
        // then
        // assertThat(result).isTrue();
    }

    @Test
    public void 인증코드를_보내면_인증_된다() {
        // given
        // when
        boolean result = emailController.verifyCode("ssehn9327@gmail.com", "5KZoZ3QAkY4yRjlvlbq1");
        // then
        assertThat(result).isTrue();
    }

    @Test
    public void 유효하지_않은_인증코드를_보내면_인증_실패된다() {
        // given
        // when
        boolean result = emailController.verifyCode("ssehn9327@gmail.com", "aEn9pn5vUbNMfzQfk1aO");
        // then
        assertThat(result).isFalse();
    }

    @Test
    public void 유효하지_않은_이메일을_보내면_에러를_던진다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            emailController.verifyCode("ssehn9324@gmail.com", " ityPKMb1UIQlcrQVo69s");
        })
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // @Test
    // public void 관리자가_승인거절을_누르면_보완메일이_전송된다() {
    // // given
    // // when
    // boolean result = emailController.refuse("ssehn9327", "ssehn9327@gmail.com");
    // // then
    // assertThat(result).isTrue();
    // }

    // @Test
    // public void 관리자가_승인을_누르면_enabled가_Y로_바뀌고_승인메일이_전송된다() {
    // // given
    // // when
    // boolean result = emailController.approve("ssehn9327", "ssehn9327@gmail.com");
    // // then
    // assertThat(result).isTrue();
    // }

}
