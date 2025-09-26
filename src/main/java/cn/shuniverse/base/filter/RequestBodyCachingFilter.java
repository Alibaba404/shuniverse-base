package cn.shuniverse.base.filter;

import cn.shuniverse.base.handler.CachedBodyHttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by 蛮小满Sama at 2025-06-17 15:15
 *
 * @author 蛮小满Sama
 * @description
 */
@Component
@Order(1)
public class RequestBodyCachingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpServletRequest) {
            // 获取请求内容类型
            String contentType = httpServletRequest.getContentType();
            // 跳过 multipart/form-data 请求（即上传文件）
            if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
                chain.doFilter(request, response);
                return;
            }
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            chain.doFilter(cachedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
