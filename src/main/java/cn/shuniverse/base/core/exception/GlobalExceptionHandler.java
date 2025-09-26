package cn.shuniverse.base.core.exception;

import cn.shuniverse.base.handler.CachedBodyHttpServletRequest;
import cn.shuniverse.base.core.resp.R;
import cn.shuniverse.base.core.resp.RCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 蛮小满Sama at 2025-06-17 15:14
 *
 * @author 蛮小满Sama
 * @description 全局异常拦截器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*异常拦截路径:默认拦截cn.shuniverse*/
    @Value("${shuniverse.exception-package:cn.shuniverse}")
    private String exceptionPackage;

    @ExceptionHandler(Exception.class)
    public <T> R<T> globalExceptionHandler(Exception e, HttpServletRequest request) {
        StringBuilder builder = new StringBuilder("\n==============================Exception Begin==============================\n");
        String message = e.getMessage();
        // 系统异常:具体的错误信息不应该展示给用户
        BisException bisException = BisException.me(RCode.FAILED);
        if (e instanceof BisException bex) {
            //业务异常
            message = bex.getMessage();
            bisException = bex;
        } else if (e instanceof MethodArgumentNotValidException ex) {
            // 获取所有字段错误
            List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
            // 将错误信息拼接成字符串
            String errorMessage = fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
            bisException = BisException.me(RCode.PARAM_IS_INVALID.getCode(), errorMessage);
        } else if (e instanceof NoHandlerFoundException ex) {
            bisException = BisException.me(HttpStatus.NOT_FOUND.value(), "访问的接口不存在：" + ex.getRequestURL());
        }
        if (StringUtils.isBlank(message)) {
            message = e.getLocalizedMessage();
        }
        //请求的接口地址
        String uri = request.getRequestURI();
        // 请求的参数
        Map<String, String> params = getStringStringMap(request);
        // 读取请求体内容，如果请求体数据过大则截取一部分
        if (request instanceof CachedBodyHttpServletRequest cachedBodyHttpServletRequest) {
            String body = cachedBodyHttpServletRequest.getCachedBodyAsString();
            builder.append("[*] Body:").append(body.length() > 2048 ? body.substring(0, 2048) + "..." : body).append("\n");
        }
        builder.append("[*] ApiURL:").append(uri).append("\n");
        builder.append("[*] Params:").append(params).append("\n");
        builder.append("[*] Message:").append(message).append("\n");
        builder.append("[*] Exception:").append(e).append("\n");
        builder.append("[*] StackTrace:").append("\n");
        StackTraceElement[] stes = e.getStackTrace();
        for (StackTraceElement ste : stes) {
            String stackLine = ste.toString();
            // 拦截业务异常
            if (stackLine.startsWith(exceptionPackage)) {
                // 排除掉CGLIB代理的方法
                if (stackLine.contains("CGLIB")) {
                    continue;
                }
                builder.append("\t").append(ste).append("\n");
            }
        }
        builder.append("==============================Exception End==============================\n");
        String logContent = builder.toString();
        boolean isWarn = false;
        if (e instanceof BisException || e instanceof MethodArgumentNotValidException) {
            isWarn = true;
        } else {
            Throwable cause = e.getCause();
            if (cause != null && cause.getCause() != null) {
                String causeMessage = cause.getCause().getMessage();
                if ("已归档，禁止编辑".equals(causeMessage)) {
                    isWarn = true;
                }
            }
        }
        if (isWarn) {
            LOGGER.warn(logContent);
        } else {
            // 建议保留异常栈对象 e，便于 trace
            LOGGER.error(logContent, e);
        }
        return bisException.ret();
    }

    /**
     * 获取请求参数
     *
     * @param request 请求对象
     * @return 请求参数
     */
    private static Map<String, String> getStringStringMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
            String[] values = requestParameterMap.get(entry.getKey());
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(entry.getKey(), valueStr);
        }
        return params;
    }
}
