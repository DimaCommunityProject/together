package net.dima_community.CommunityProject.handler;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		log.info("로그인 실패 {}", exception.getClass());
		String errMessage = "";
		
		if(exception instanceof BadCredentialsException) {	//아이디가 존재하지만 비번이 틀린 경우(계정이 존재하지 않는 경우를 따로 처리하는 것도 있지만 그냥 한 번에 처리)
			errMessage = exception.getMessage() + "\n아이디 또는 비밀번호가 일치하지 않습니다.";
		} else if (exception instanceof InternalAuthenticationServiceException) {	//스프링 시큐리티 내부에서 발생하는 예외
			errMessage = exception.getMessage() + "내부 시스템 문제로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
		}  else if (exception instanceof AuthenticationCredentialsNotFoundException) { //인증 요청이 잘못된 형식으로 제출되거나 필요한 인증 정보가 누락된 경우. => 프론트에서 키업으로 경고주고 백단에서도 예외처리 필요
			errMessage = exception.getMessage() + "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
		} else {	// 그 외 
			errMessage = exception.getMessage();
			errMessage += "\n 알 수 없는 오류로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
		}
		errMessage = URLEncoder.encode(errMessage, "UTF-8");
		
		setDefaultFailureUrl("/member/login?error=true&errMessage=" + errMessage);	//true가 가면 에러가 났다는 뜻
		
		super.onAuthenticationFailure(request, response, exception);
	}
}
