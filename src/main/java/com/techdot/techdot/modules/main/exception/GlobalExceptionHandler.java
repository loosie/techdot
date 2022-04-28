package com.techdot.techdot.modules.main.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sun.jdi.request.DuplicateRequestException;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.auth.CurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	// 400 : BAD REQUEST
	// 404 : NOT FOUND
	// 500 : INTERNAL SERVER ERROR

	@ExceptionHandler(DuplicateRequestException.class)
	public String handleDuplicateRequestException(DuplicateRequestException ex, HttpServletRequest req) {
		log.error("duplicated request - {}", ex.getMessage());
		req.setAttribute("message", ex.getMessage());
		return "error/400";
	}

	@ExceptionHandler(CategoryCanNotDeleteException.class)
	public String handleCategoryCanNotDeleteException(CategoryCanNotDeleteException ex, HttpServletRequest req) {
		log.error("category can not deleted - {}", ex.getMessage());
		req.setAttribute("message", ex.getMessage());
		return "error/400";
	}

	@ExceptionHandler({NullPointerException.class, CategoryNotExistedException.class})
	public String handleNullPointerException(NullPointerException ex, HttpServletRequest req) {
		log.error("data not existed - {}", ex.getMessage());
		req.setAttribute("message", ex.getMessage());
		return "error/404";
	}

	@ExceptionHandler(IllegalStateException.class)
	public String handleIllegalStateException(IllegalStateException ex, HttpServletRequest req) {
		log.error("data is invalid type  - {}", ex.getMessage());
		req.setAttribute("message", ex.getMessage());
		return "error/404";
	}


	@ExceptionHandler(UserNotExistedException.class)
	public String handleUserNotExistedException(UserNotExistedException ex, HttpServletRequest req) {
		log.error("user not existed - {}", ex.getMessage());
		req.setAttribute("message", ex.getMessage());
		return ex.getViewName();
	}

	@ExceptionHandler
	public String handleRuntimeException(RuntimeException ex, HttpServletRequest req, @CurrentUser Member member) {
		if (member != null) {
			log.info("{} requested -  {}", member.getEmail(), req.getRequestURI());
		} else {
			log.info("visitor requested -  {}", req.getRequestURI());
		}
		log.error("bad request - {}", ex.getMessage());
		req.setAttribute("message", "해당 요청이 올바르지 않습니다.");
		return "error/500";
	}

}
