package cn.shuniverse.base.core.exception;


import cn.shuniverse.base.core.resp.R;
import cn.shuniverse.base.core.resp.RCode;

import java.io.Serial;

/**
 * Created by 蛮小满Sama at 2024-02-16 13:00
 *
 * @author 蛮小满Sama
 * @description 自定义异常
 */
public class BisException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8863339790253662109L;

    private final Integer code;
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 私有化无参构造器以保护其内部属性
     */
    private BisException() {
        this.code = RCode.FAILED.getCode();
        this.message = RCode.FAILED.getMessage();
    }

    private BisException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static BisException me(Integer code, String message) {
        return new BisException(code, message);
    }

    public static BisException me(String message) {
        return me(RCode.FAILED.getCode(), message);
    }

    public static BisException me(IBisRCode rCode) {
        return me(rCode.getCode(), rCode.getMessage());
    }

    public <T> R<T> ret() {
        return R.failure(this.code, this.message);
    }
}

