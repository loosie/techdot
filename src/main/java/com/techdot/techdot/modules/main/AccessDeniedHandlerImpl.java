package com.techdot.techdot.modules.main;

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

@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
	@Override
	public void handle( HttpServletRequest req, HttpServletResponse res,
		AccessDeniedException ex) throws IOException, ServletException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null){
			log.warn("user [" + authentication.getName() +"] attempted to access the protected URL: " + req.getRequestURL());
		}
		req.setAttribute("message", "(Access Denied) 해당 경로에 접근할 수 없습니다");
		req.getRequestDispatcher("/error/403").forward(req, res);
	}
}
