package cn.shuniverse.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 蛮小满Sama at 2025-06-23 15:09
 *
 * @author 蛮小满Sama
 * @description
 */
@Slf4j
public class IPUtil {
    private static final String UNKNOWN = "unknown";
    private static final String LOCAL_IP = "127.0.0.1";
    private static Searcher searcher;

    private IPUtil() {
    }


    /**
     * 判断是否为合法 IP
     */
    public static boolean isLegal(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    /**
     * 获取客户端IP地址的方法
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个代理的情况, X-Forwarded-For的格式如：X-Forwarded-For: client1, proxy1, proxy2
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? LOCAL_IP : ip;
    }


    /**
     * 加载ip2region
     */
    @PostConstruct
    private static void initIp2Region() {
        try {
            InputStream inputStream = new ClassPathResource("/ip2region.xdb").getInputStream();
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            searcher = Searcher.newWithBuffer(bytes);
        } catch (Exception e) {
            log.error("加载ip2region失败", e);
        }
    }


    /**
     * 获取 ip 所属地址
     *
     * @param ip ip
     * @return
     */
    public static String getIPRegion(String ip) {
        boolean legal = isLegal(ip);
        if (legal) {
            initIp2Region();
            try {
                String searchIpInfo = searcher.search(ip);
                String[] strings = searchIpInfo.split("\\|");
                if (strings.length > 0) {
                    if ("中国".equals(strings[0])) {
                        return strings[2];
                    } else if ("0".equals(strings[0])) {
                        if ("内网IP".equals(strings[4])) {
                            return "内网";
                        } else {
                            return "未知";
                        }
                    } else {
                        return strings[0];
                    }
                }

            } catch (Exception e) {
                log.error("获取ip所属地址失败", e);
            }
            return "未知";
        } else {
            return ip;
        }
    }
}
