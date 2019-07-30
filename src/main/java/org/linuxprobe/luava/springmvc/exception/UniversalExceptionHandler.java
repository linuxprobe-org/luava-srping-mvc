package org.linuxprobe.luava.springmvc.exception;

import lombok.extern.slf4j.Slf4j;
import org.linuxprobe.luava.servlet.HttpServletUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通用异常处理,
 * 请在实现类上注解{@link org.springframework.web.bind.annotation.RestControllerAdvice}
 * <p>
 * 并重写{@link #handleAjaxRequest}方法, 可根据自己需求决定是否重写{@link #handleOtherRequest}方法来处理非ajax请求, 该方法默认调用{@link #handleAjaxRequest}
 * <p/>
 */
@Slf4j
public abstract class UniversalExceptionHandler {
    /**
     * 处理ajax请求异常
     */
    public abstract Object handleAjaxRequest(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, Throwable exception);

    /**
     * 处理非ajax请求异常
     */
    public Object handleOtherRequest(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, Throwable exception) {
        return this.handleAjaxRequest(request, response, handler, exception);
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public Object handleException(HttpServletRequest request, HttpServletResponse response, Throwable exception, HandlerMethod handlerMethod) {
        if (UniversalExceptionHandler.log.isErrorEnabled()) {
            UniversalExceptionHandler.log.error("", exception);
        }
        boolean isResponseBody = false;
        Class<?> returnType = handlerMethod.getMethod().getReturnType();
        Class<?> handlerType = handlerMethod.getBeanType();
        if (handlerType.isAnnotationPresent(RestController.class)) {
            //如果不是返回modelAndView
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
