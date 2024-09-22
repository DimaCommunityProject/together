package net.dima_community.CommunityProject.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

	@Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 권한이 없을 때 처리할 로직
        // 예: 로그를 남기거나, 사용자에게 특정 페이지로 리다이렉트
        response.sendRedirect("/main/access-denied");  // "권한이 없습니다" 페이지로 리다이렉트
    }
}
