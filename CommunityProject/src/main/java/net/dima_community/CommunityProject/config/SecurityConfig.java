package net.dima_community.CommunityProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.handler.CustomFailureHandler;
import net.dima_community.CommunityProject.handler.CustomSuccessHandler;

@Configuration		//SecurityConfig 클래스가 설정 클래스임을 나타내는 어노테이션
@EnableWebSecurity	//웹 보안은 모두 이 클래스의 설정에 따름을 나타내는 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomFailureHandler failureHandler;	//로그인 실패 시 처리 동작
	private final CustomSuccessHandler successHandler;	//로그인 성공 시 처리 동작
	
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
		
		 //웹 요청에 대한 접근권한 설정
//		 http 
//		 	.authorizeHttpRequests((auth) -> auth	//로그인을 안해도 누구나 다 볼 수 있는 설정
//		 			.requestMatchers( 
//		 					"/"
//		 					, "/member/confirmId"		
//		 					, "/member/join"			
//		 					, "/member/joinProc"		
//		 					, "/member/login" 
//		 					, "/member/loginProc"
//		 					, "/member/findId"
//		 					, "/member/findPw"
//		 					, "/member/findPwResult"
//		 					
//		 					, "/images/**"
//		 					, "/css/**"
//		 					, "script/**").permitAll()	//permitAll : 인증절차 없이도 접근가능한 요청
//		 	
//		 	.requestMatchers("/admin/**").hasRole("ADMIN")			//hasRole  : 인증절차 필요
//		 	.requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")	//hasAnyRole : 여러 개의 역할 중 하나 이상을 가진 사용자만 해당 경로에 접근할 수 있도록 권한을 설정
//		 	.anyRequest().authenticated()	//기타 다른 경로는 인증된 사용자만 접근가능. anyRequest는 가장 마지막에 와야함
//		 	);
		 http
         .authorizeHttpRequests(authorizeRequests ->
             authorizeRequests
                 .anyRequest().permitAll() // 모든 요청에 대해 접근 허용
         );
		 
		 // Custom Login 설정 
		 http
		 	.formLogin((auth) -> auth	
		 			.loginPage("/member/login")				//로그인 요청 페이지
		 			.failureHandler(failureHandler) 		//로그인 실패시 처리할 핸들러 객체 등록
		 			.successHandler(successHandler)			//로그인 성공시 처리
		 			.usernameParameter("memberId")			//아이디
		 			.passwordParameter("memberPw")			//비번
		 			.loginProcessingUrl("/member/loginProc")//요청을 처리함
		 			.defaultSuccessUrl("/").permitAll()		//성공시 첫화면으로감
		 );
		 
		// 로그아웃 설정
		http
			.logout((auth) -> auth
						.logoutUrl("/member/logout")		//로그아웃 처리 URL
						.logoutSuccessUrl("/")				//로그아웃 성공시 URL
						.invalidateHttpSession(true)		//세션 무효화
						.deleteCookies("JSESSIONID"));		//로그아웃 성공시 제거할 쿠키명
		
		//배포 시 다시 확인
		http
	 	.csrf((auth) -> auth.disable());
		
		//HTTP 헤더 보안 설정
		//xss와 csp는 둘 다 xss 보완이지만 xssProtection는 구식 브라우저 보호이며 csp는 현대적이고 더 강력함. 
		http.headers(headers ->
        headers.xssProtection(
                xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
        ).contentSecurityPolicy(
                cps -> cps.policyDirectives("script-src 'self' 'unsafe-inline'")
        ));
		
		return http.build();
	}//end filterchain
	
	@Bean	
	 BCryptPasswordEncoder bCryptPasswordEncoder() {	//암호화 해주는 인코더 반환
		 return new BCryptPasswordEncoder();			//단방향 암호화가 되어 우리에게 특수한 암호로 보여줌
	 }
}//end class
