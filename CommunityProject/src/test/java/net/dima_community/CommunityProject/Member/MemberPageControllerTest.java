package net.dima_community.CommunityProject.Member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;

import ch.qos.logback.core.model.Model;
import net.dima_community.CommunityProject.controller.member.MemberPageController;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;

@SpringBootTest
public class MemberPageControllerTest {
        @Autowired
        public MemberPageController memberPageController;

        @Test
        public void update메소드를_통해_MemberPage와_MemberProject를_신규_저장_할_수_있다() {
                // given
                MemberPageDTO memberPage = MemberPageDTO.builder()
                                .memberId("ssehn9327")
                                .memberInterest("FULLSTACK DA")
                                .memberSelfInfo("안녕하세요 개발자 심세현입니다.")
                                .memberSkill("JAVA Python")
                                .memberGit("https://github.com/Sehyeon111")
                                .memberBlog(null)
                                .memberResume(null)
                                .build();
                MemberProjectDTO memberProject = MemberProjectDTO.builder()
                                .memberId("ssehn9327")
                                .projectTitle("무역 전문 웹 사이트 개발")
                                .projectSubtitle("spring boot를 이용한 웹 프로그래밍")
                                .projectGit("https://github.com/Sehyeon111/HSCZ-Refactoring")
                                .build();
                ExtendedModelMap model = new ExtendedModelMap();
                // when
                // memberPageController.updatepage2("ssehn9327", "심세현", "ssehn9327",
                // memberPage, memberProject, model);
                // then
        }

        @Test
        public void update메소드를_통해_MemberPage와_MemberProject를_갱신_할_수_있다() {
                // given
                MemberPageDTO memberPage = MemberPageDTO.builder()
                                .memberId("ssehn9327")
                                .memberInterest("Frontend UI/UX")
                                .memberSelfInfo("안녕하세요 개발자 심세현입니다.")
                                .memberSkill("nodeJs react")
                                .memberGit("https://github.com/Sehyeon111")
                                .memberBlog(null)
                                .memberResume(null)
                                .build();
                MemberProjectDTO memberProject = MemberProjectDTO.builder()
                                .memberId("ssehn9327")
                                .projectTitle("HSCZ")
                                .projectSubtitle("react를 이용해 프론트 담당")
                                .projectGit("https://github.com/Sehyeon111/HSCZ-Refactoring")
                                .build();
                ExtendedModelMap model = new ExtendedModelMap();
                // when
                // memberPageController.updatepage2("ssehn9327", "심세현", "ssehn9324@naver.com",
                // memberPage, memberProject, model);
                // then
        }
}
