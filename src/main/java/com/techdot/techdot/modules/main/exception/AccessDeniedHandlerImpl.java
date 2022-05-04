package com.techdot.techdot.modules.main.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Security 접근 권한 거부 핸들러 커스텀
 * - 만약 유저가 권한이 없는 경로로 접근하면 403 에러 뷰로 이동
 */
@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest req, HttpServletResponse res,
		AccessDeniedException ex) throws IOException, ServletException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			log.warn("user [" + authentication.getName() + "] attempted to access the protected URL: "
				+ req.getRequestURL());
		}
		req.setAttribute("message", "(Access Denied) 해당 경로에 접근할 수 없습니다");
		req.getRequestDispatcher("/error/403").forward(req, res);
	}
}
