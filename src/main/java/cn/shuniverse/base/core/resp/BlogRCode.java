package cn.shuniverse.base.core.resp;

import cn.shuniverse.base.core.exception.IBisRCode;

/**
 * Created by 蛮小满Sama at 2025-06-19 15:54
 *
 * @author 蛮小满Sama
 * @description #6000～6999 博客相关
 */
public enum BlogRCode implements IBisRCode {
    BLOG_ARTICLE_NOT_EXIST(6000, "博客文章不存在"),
    BLOG_CLASSIFY_EXISTED(6001, "文章分类已存在"),
    ;

    private final Integer code;
    private final String message;

    BlogRCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
