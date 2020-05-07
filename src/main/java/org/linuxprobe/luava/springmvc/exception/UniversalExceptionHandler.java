package org.linuxprobe.luava.springmvc.exception;

import org.linuxprobe.luava.servlet.HttpServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 通用异常处理,
 * 请在实现类上注解{@link org.springframework.web.bind.annotation.RestControllerAdvice}
 * <p>
 * 并重写{@link #handleAjaxRequest}方法, 可根据自己需求决定是否重写{@link #handleOtherRequest}方法来处理非ajax请求, 该方法默认调用{@link #handleAjaxRequest}
 * <p/>
 */
public abstract class UniversalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UniversalExceptionHandler.class);

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
        if (log.isErrorEnabled()) {
            log.error("Controller:{}, Method:{}, URI:{}, Content-Type:{} ,异常信息:{}",
                    handlerMethod.getBeanType().getName(),
                    handlerMethod.getMethod().getName(),
                    request.getRequestURI(),
                    request.getHeader("Content-Type"),
                    getStackTrace(exception));
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

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }
}
