package com.techdot.techdot.infra.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NullPointerException.class)
	public String handleNullPointerException(NullPointerException ex, Model model){
		log.error("null error - {}", ex);
		model.addAttribute("message", "정확한 값이 입력되지 않았습니다");
		return "error/500";
	}

	@ExceptionHandler(UserNotExistedException.class)
	public String handleUserNotExistedException(UserNotExistedException ex, Model model){
		log.error("user not existed - {}", ex);
		model.addAttribute("error", ex.getMessage());
		return ex.getViewName();
	}

	@ExceptionHandler(UserNotVerifiedEmailException.class)
	public String handleUserNotVerifiedEmailException(UserNotVerifiedEmailException ex, Model model){
		log.error("user not verified email - {}", ex);
		model.addAttribute("error", ex.getMessage());
		model.addAttribute("email", ex.getEmail());
		return ex.getViewName();
	}

}
