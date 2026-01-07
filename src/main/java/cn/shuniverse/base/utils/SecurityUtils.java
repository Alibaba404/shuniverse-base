package cn.shuniverse.base.utils;

import cn.shuniverse.base.constants.SecurityConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 蛮小满Sama at 2025-06-21 18:25
 *
 * @author 蛮小满Sama
 * @description
 */
public class SecurityUtils {
    private SecurityUtils() {
    }

    /**
     * 获取用户ID
     */
    public static String getUserId() {
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取用户名称
     */
    public static String getUsername() {
        return SecurityContextHolder.getUserName();
    }

    /**
     * 获取用户key
     */
    public static String getUserKey() {
        return SecurityContextHolder.getUserKey();
    }

    /**
     * 获取登录用户信息
     */
    public static <T> T getLoginUser(Class<T> clazz) {
        Object user = SecurityContextHolder.get(SecurityConstants.LOGIN_USER);
        if (clazz.isInstance(user)) {
            return clazz.cast(user);
        }
        return null;
    }

    /**
     * 获取指定参数
     */
    public static String getString(String key) {
        return SecurityContextHolder.get(key);
    }

    /**
     * 获取请求对象
     */
    public static HttpServletRequest getRequest() {
        return SecurityContextHolder.get(SecurityConstants.HTTP_REQUEST, HttpServletRequest.class);
    }
}
