package com.techdot.techdot.exception;

import static com.techdot.techdot.controller.MemberController.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public String handleRuntimeException(@CurrentUser Member member, HttpServletRequest req, HttpServletResponse res, RuntimeException ex){
		if(member != null){
			log.info("'{}' requested '{}'", member.getNickname(), req.getRequestURI());
		}else{
			log.info("requested '{}'", req.getRequestURI());;
		}

		log.error("bad request", ex);
		return "error";
	}

	@ExceptionHandler(UserNotExistedException.class)
	public String handleUserNotExistedException(UserNotExistedException ex, Model model){
		log.error("user not existed - {}", ex);
		model.addAttribute("error", ex.getMessage());
		return ex.getViewName();
	}
}
