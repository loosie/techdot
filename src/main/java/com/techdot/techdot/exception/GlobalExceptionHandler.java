package com.techdot.techdot.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	// @ExceptionHandler
	// public String handleRuntimeException(@CurrentUser Member member, HttpServletRequest req, HttpServletResponse res, RuntimeException ex){
	// 	if(member != null){
	// 		log.info("'{}' requested '{}'", member.getNickname(), req.getRequestURI());
	// 	}else{
	// 		log.info("requested '{}'", req.getRequestURI());;
	// 	}
	//
	// 	log.error("bad request", ex);
	// 	return "error";
	// }
	@ExceptionHandler(NullPointerException.class)
	public String handleNullPointerException(NullPointerException ex, Model model){
		log.error("internal server error - {}", ex);
		model.addAttribute("error", "알 수 없는 서버 에러가 발생하였습니다.");
		return "error";
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
