package cn.shuniverse.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 蛮小满Sama at 2025-07-07 19:51
 *
 * @author 蛮小满Sama
 * @description
 */
public class CronUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CronUtil.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 列出 Cron 表达式未来执行的时间
     *
     * @param expression Cron 表达式
     * @param runCount   要获取的执行次数
     * @return 执行时间列表，格式为 yyyy-MM-dd HH:mm:ss
     */
    public static List<String> listCronResult(String expression, int runCount) {
        if (!CronExpression.isValidExpression(expression)) {
            LOGGER.warn("无效的 Cron 表达式: {}", expression);
            return Collections.emptyList();
        }
        LOGGER.info("解析 Cron 表达式: {}, 预计输出 {} 次", expression, runCount);
        List<String> result = new ArrayList<>(runCount);
        LocalDateTime timeNext = LocalDateTime.now();
        CronExpression cron = CronExpression.parse(expression);
        for (int i = 0; i < runCount; i++) {
            timeNext = cron.next(timeNext);
            if (timeNext == null) {
                break;
            }
            result.add(FORMATTER.format(timeNext));
        }

        return result;
    }

    public static List<String> listCronResult(String expression) {
        return listCronResult(expression, 10);
    }

    public static void main(String[] args) {
        // 秒, 分, 时, 日, 月, 周, 年
        String cron = "0 0 8-11,14-18,0 * * ?";
        for (String time : listCronResult(cron)) {
            LOGGER.info(time);
        }
    }
}
