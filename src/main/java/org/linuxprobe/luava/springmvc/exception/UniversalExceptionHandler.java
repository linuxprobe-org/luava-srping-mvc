package org.linuxprobe.luava.springmvc.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.linuxprobe.luava.servlet.HttpServletUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用异常处理,
 * 请在实现类上注解{@link org.springframework.web.bind.annotation.ControllerAdvice}
 */
@Slf4j
public abstract class UniversalExceptionHandler {
	/**
	 * 处理ajax请求异常
	 */
	public abstract Object handleAjaxRequest(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handler, Throwable exception);

	/**
	 * 处理非ajax请求异常
	 */
	public Object handleOtherRequest(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler,
			Throwable exception) {
		return this.handleAjaxRequest(request, response, handler, exception);
	}

	@ResponseBody
	@ExceptionHandler(Throwable.class)
	public Object handleMissingServletRequestParameterException(HttpServletRequest request,
			HttpServletResponse response, Throwable exception, HandlerMethod handlerMethod) {
		if (log.isErrorEnabled()) {
			log.error("", exception);
		}
		boolean isResponseBody = false;
		Class<?> returnType = handlerMethod.getMethod().getReturnType();
		Class<?> handlerType = handlerMethod.getBeanType();
		if (handlerType.isAnnotationPresent(RestController.class)) {
			/** 如果不是返回modelAndView */
			if (!returnType.isAssignableFrom(ModelAndView.class)) {
				if (handlerMethod.getMethod().isAnnotationPresent(ResponseBody.class)) {
					isResponseBody = true;
				}
			}
		} else if (handlerType.isAnnotationPresent(Controller.class)
				&& handlerType.isAnnotationPresent(ResponseBody.class)) {
			isResponseBody = true;
		}
		if (isResponseBody || HttpServletUtils.isAjax(request)) {
			return this.handleAjaxRequest(request, response, handlerMethod, exception);
		} else {
			return this.handleOtherRequest(request, response, handlerMethod, exception);
		}
	}
}
