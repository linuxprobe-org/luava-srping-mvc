package org.linuxprobe.luava.springmvc.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.linuxprobe.luava.http.HttpServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public abstract class GlobalExceptionHandler implements HandlerExceptionResolver {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * 处理ajax请求异常
	 */
	public abstract ModelAndView handleAjaxRequest(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception exception);

	/**
	 * 处理非ajax请求异常
	 */
	public ModelAndView handleOtherRequest(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) {
		return this.handleAjaxRequest(request, response, handler, exception);
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) {
		logger.error("", exception);
		HandlerMethod handlerMethod = (HandlerMethod) handler;
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
			return this.handleAjaxRequest(request, response, handlerType, exception);
		} else {
			return this.handleOtherRequest(request, response, handlerType, exception);
		}
	}
}
