package cn.shuniverse.base.core.resp;


import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

/**
 * Created by 蛮小满Sama at 2024-02-16 12:37
 *
 * @author 蛮小满Sama
 * @description 响应类
 */
@Setter
@Getter
public class R<T> {
    /**
     * 响应代码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 是否报错
     */
    private Boolean success;
    /**
     * 响应数据
     */
    private T data;

    public R(Integer code, String message, Boolean success, T data) {
        this.code = code;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public static <T> R<T> success(Integer code, String message, T data) {
        return new R<>(code, message, Boolean.TRUE, data);
    }

    public static <T> R<T> success() {
        return new R<>(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMessage(), Boolean.TRUE, null);
    }

    public static <T> R<T> success(Integer code, String message) {
        return success(code, message, null);
    }

    public static <T> R<T> success(IBisRCode code, T data) {
        return success(code.getCode(), code.getMessage(), data);
    }

    public static <T> R<T> success(@Nullable T data) {
        return success(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMessage(), data);
    }

    /**
     * 失败的响应
     *
     * @param code    响应代码
     * @param message 响应消息
     * @return R
     */
    public static <T> R<T> failure(Integer code, String message) {
        return new R<>(code, message, Boolean.FALSE, null);
    }


    /**
     * 失败的响应
     *
     * @param code 响应代码
     * @return R
     */
    public static <T> R<T> failure(IBisRCode code) {
        return failure(code.getCode(), code.getMessage());
    }

}
