package cn.shuniverse.base.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 蛮小满Sama at 2025-10-23 10:32
 *
 * @author 蛮小满Sama
 * @description Map工具类
 */
@Slf4j
public class MapUtil {
    private MapUtil() {
    }

    /**
     * 将对象转为Map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> toMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 跳过静态字段
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                try {
                    // 仅在无法访问时才尝试设置可访问
                    if (!field.canAccess(obj)) {
                        field.setAccessible(true);
                    }
                    map.put(field.getName(), field.get(obj));
                } catch (Exception e) {
                    log.error("MapUtil.toMap error: {}", field.getName(), e);
                }
            }
            // 向上遍历父类
            clazz = clazz.getSuperclass();
        }
        return map;
    }
}
