package net.dima_community.CommunityProject.Member;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.dima_community.CommunityProject.dto.member.MemberDTO;
import net.dima_community.CommunityProject.service.member.MemberService;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    // @BeforeEach
    // public void init() {

    // MemberRepository memberRepository = new MemberRepositoryImpl(null);
    // memberService = MemberService.builder()
    // .memberRepository(new MemberRepositoryImpl(null))
    // .bCryptEncoderHolder(new BCryptEncoderHolderImpl())
    // .dbConnector(new DBConnector())
    // .build();
    // }

    // @Autowired
    // private MemberService memberService;

    @Test
    public void id를_이용해_Member를_찾을_수_있다() {
        // given
        // when
        MemberDTO result = memberService.findById("ssehn9327");
        // then
        assertThat(result.getMemberId()).isEqualTo("ssehn9327");
        assertThat(result.getMemberName()).isEqualTo("심세현");
    }

    @Test
    public void Member_를_업데이트_할_수_있다() {
        // given
        // when
        MemberDTO result = memberService.updateMember("ssehn9327", "심가현", "ssehn9324@naver.com");
        // then
        assertThat(result.getMemberId()).isEqualTo("ssehn9327");
        assertThat(result.getMemberName()).isEqualTo("심가현");
        assertThat(result.getMemberEmail()).isEqualTo("ssehn9324@naver.com");
    }

    @Test
    public void PreparedStatement를_이용해_중복된_Id를_보내면_False를_던진다() {
        // when
        boolean result = memberService.findByIdThroughConn("ssehn9327");
        // then
        // 사용 불가한 ID라 반환 값은 Fasle
        assertThat(result).isFalse();
    }
}
