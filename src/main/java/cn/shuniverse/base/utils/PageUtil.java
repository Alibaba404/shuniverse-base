package cn.shuniverse.base.utils;

import java.util.Collections;
import java.util.List;

public class PageUtil {

    /**
     * 对列表进行分页，安全处理 start/pageSize 越界
     *
     * @param list     原始数据列表
     * @param start    起始下标（0 开始）
     * @param pageSize 每页条数（>=0）
     * @return 分页后的子列表（安全，永不抛异常）
     */
    public static <T> List<T> safeSubList(List<T> list, int start, int pageSize) {
        if (list == null || list.isEmpty() || pageSize <= 0 || start >= list.size()) {
            return Collections.emptyList();
        }

        if (start < 0) {
            start = 0;
        }
        int end = Math.min(start + pageSize, list.size());
        return list.subList(start, end);
    }

    /**
     * 基于页码分页
     *
     * @param list     原始列表
     * @param page     页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 子列表
     */
    public static <T> List<T> safeSubListByPage(List<T> list, int page, int pageSize) {
        if (page <= 0) {
            page = 1;
        }
        int start = (page - 1) * pageSize;
        return safeSubList(list, start, pageSize);
    }
}
