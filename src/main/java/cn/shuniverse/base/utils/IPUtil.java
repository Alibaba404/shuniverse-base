package cn.shuniverse.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private static final Pattern IP_PATTERN = Pattern.compile("([1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");

    /**
     * 判断是否为合法 IP
     */
    public static boolean isValid(String ip) {
        if (ip == null) {
            return false;
        }
        return IP_PATTERN.matcher(ip).matches();
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
    private static void initIp2Region(String xdbPath) {
        try {
            if (StringUtils.isBlank(xdbPath)) {
                xdbPath = "/ip2region.xdb";
            }
            InputStream inputStream = new ClassPathResource(xdbPath).getInputStream();
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            searcher = Searcher.newWithBuffer(bytes);
        } catch (Exception e) {
            log.error("加载ip2region失败", e);
        }
    }

    public static String[] ipRegion(String ip) throws Exception {
        return ipRegion(ip, null);
    }

    public static String[] ipRegion(String ip, String xdbPath) throws Exception {
        boolean legal = isValid(ip);
        if (legal) {
            initIp2Region(xdbPath);
            String infoStr = searcher.search(ip);
            log.info("ip2region: {}", infoStr);
            return infoStr.split("\\|");
        }
        return new String[]{};
    }

    public static String ipSimpleRegion(String ip) {
        return ipSimpleRegion(ip, null);
    }

    /**
     * 获取 ip 简单所属地址
     *
     * @param ip ip
     * @return ip所属地址
     */
    public static String ipSimpleRegion(String ip, String xdbPath) {
        try {
            String[] infos = ipRegion(ip, xdbPath);
            if (infos.length > 0) {
                if ("中国".equals(infos[0])) {
                    return infos[2];
                } else if ("0".equals(infos[0])) {
                    if ("内网IP".equals(infos[4])) {
                        return "内网";
                    } else {
                        return "未知";
                    }
                } else {
                    return infos[0];
                }
            }
        } catch (Exception e) {
            log.error("获取ip所属地址失败", e);
        }
        return "未知";
    }
}
