package net.dima_community.CommunityProject.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private final RequestCache requestCache = new HttpSessionRequestCache();			//로그인 요청 전. 사용자가 요청한 URL을 저장
	private final RedirectStrategy redirectStrategy= new DefaultRedirectStrategy();		//로그인 후 리다이렉션할 URL을 설정
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		clearSession(request);	//로그인 성공 후 세션에서 에러 관련 정보를 제거
		
		SavedRequest savedRequest = requestCache.getRequest(request, response);	//로그인 전 url 가져옴
	
		//이전 url이 존재하면
		String prevPage = (String) request.getSession().getAttribute("prevPage");
		if (prevPage != null) {
			request.getSession().removeAttribute("prevPage");
		}
		
		String URL = "/";
	
		if (savedRequest != null) {
			URL = savedRequest.getRedirectUrl();
		} else if (prevPage != null && !prevPage.equals("")) {
			// 회원가입 - 로그인으로 넘어온경우는 메인페이지로 redirect
			if (prevPage.contains("/member/login")) {
				URL = "";
			} else {
				URL = prevPage;
			}
		}
		
		redirectStrategy.sendRedirect(request, response, URL);
	}//end onAuthenticationSuccess

	// 로그인 실패 후 성공 시 남아있는 에러 세션 제거
	private void clearSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
		
	}//end clearSession
	
	
}//end class

