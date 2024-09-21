package net.dima_community.CommunityProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.handler.CustomFailureHandler;
import net.dima_community.CommunityProject.handler.CustomSuccessHandler;

@Configuration // SecurityConfig 클래스가 설정 클래스임을 나타내는 어노테이션
@EnableWebSecurity // 웹 보안은 모두 이 클래스의 설정에 따름을 나타내는 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomFailureHandler failureHandler; // 로그인 실패 시 처리 동작
	private final CustomSuccessHandler successHandler; // 로그인 성공 시 처리 동작

	// 예외처리할 url 설정
	// WebSecurityCustomizer : HTTP 요청에 대한 보안 구성을 커스터마이징. 웹 요청을 무시하도록 설정
	// web.ignoring() : 특정 경로 무시
	// requestMatchers : 경로 설정
	// favicon.ico : 아이콘 요청 시 경로 무시
	// error : 기본 오류 페이지 무시
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/favicon.ico", "/error");
	};

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// 웹 요청에 대한 접근권한 설정
		http
				.authorizeHttpRequests((auth) -> auth // 로그인을 안해도 누구나 다 볼 수 있는 설정
						.requestMatchers(
								"/", "/member/adminPageNote", "/member/adminPageDetail", "/member/findId",
								"/member/findPw", "/member/join", "/member/login", "/member/checkDuplicate"

								, "/email/**", "/main/**", "/board/list", "/ckeditor5/**", "/css/**", "/fonts/**",
								"/images/**", "/js/**", "/libs/**", "/script/**")
						.permitAll()

						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/memberpage/showpage", "/member/updatePage", "/member/changePw",
								"/board/detail")
						.hasAnyRole("ADMIN", "USER")
						// .requestMatchers("/member/memberPage", "/member/updatePage",
						// "/member/changePw", "/board/detail").authenticated()
						.anyRequest().authenticated());

		// Custom Login 설정
		http
				.formLogin((auth) -> auth
						.loginPage("/member/login") // 로그인 요청 페이지
						.failureHandler(failureHandler) // 로그인 실패시 처리할 핸들러 객체 등록
						.successHandler(successHandler) // 로그인 성공시 처리
						.usernameParameter("memberId") // 아이디
						.passwordParameter("memberPw") // 비번
						.loginProcessingUrl("/member/loginProc")// 요청을 처리함
						.defaultSuccessUrl("/").permitAll() // 성공시 첫화면으로감
				);

		// 로그아웃 설정
		http
				.logout((auth) -> auth
						.logoutUrl("/member/logout") // 로그아웃 처리 URL
						.logoutSuccessUrl("/") // 로그아웃 성공시 URL
						.invalidateHttpSession(true) // 세션 무효화
						.deleteCookies("JSESSIONID")); // 로그아웃 성공시 제거할 쿠키명

		// 배포 시 다시 확인
		http
				.csrf((auth) -> auth.disable());

		// HTTP 헤더 보안 설정
		// xss와 csp는 둘 다 xss 보완이지만 xssProtection는 구식 브라우저 보호이며 csp는 현대적이고 더 강력함.
		http.headers(headers -> headers.contentSecurityPolicy(
				cps -> cps.policyDirectives(
						"script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdn.ckeditor.com; " +
								"style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdn.ckeditor.com https://fonts.googleapis.com; "
								+
								"font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net data:; " + // 폰트 출처에
																												// jsdelivr
																												// 추가
								"img-src 'self' data: https://cdn.jsdelivr.net https://cdn.ckeditor.com; " + // 이미지 출처
																												// 허용
								"connect-src 'self'; " + // XMLHttpRequest, WebSocket 등을 위한 출처 제한
								"frame-src 'self'; " + // iframe을 허용할 출처
								"object-src 'none';" // object-src 제한
				)));

		return http.build();
	}// end filterchain

}// end class
