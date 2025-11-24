package cn.shuniverse.base.core.resp;

/**
 * Created by 蛮小满Sama at 2024-02-16 13:05
 *
 * @author xiaozhi
 * @description 响应码
 * #1000～1999 区间表示参数错误
 * #2000～2999 区间表示用户错误
 * #3000～3999 区间表示接口异常
 */
public enum RCode implements IBisRCode {
    // #201～999 区间系统内部错误
    FAILED(-1, "未知异常,请联系管理员"),
    SUCCESS(200, "成功!"),
    // 请求成功，返回的数据要解密,
    NEED_DECRYPTION(201, "成功!"),
    TRIGGER_LIMIT(203, "访问频繁，请稍候再试！"),
    SSL_HANDSHAKE_ERROR(204, "建立SSL连接失败！"),


    // #1000～1999 业务异常码
    PARAM_IS_INVALID(1000, "参数无效"),
    PARAM_HEADER_MISSING_OR_INVALID(1001, "缺失或无效的授权头"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),
    PARAM_TOKEN_BLANK(1005, "TOKEN无效"),
    PARAM_TOKEN_EXPIRED(1006, "TOKEN已过期,请重新登录"),
    AES_ENCRYPT_ERROR(1007, "AES加密失败"),
    AES_DECRYPT_ERROR(1008, "AES解密失败"),
    SYS_DICT_TYPE_EXISTED(1009, "字典类型已存在"),
    USER_CAPTCHA_EXPIRED(1010, "验证码已过期"),
    USER_CAPTCHA_NOT_MATCHED(1011, "验证码不匹配"),
    USER_CAPTCHA_SEND_ERROR(1012, "验证码发送失败"),
    CAPTCHA_CLASSIFY_ERROR(1013, "不支持的验证码类型"),
    FILE_EMPTY(1014, "文件不存在！T_T"),
    FILE_UPLOAD_ERROR(1015, "文件上传失败！T_T"),
    FILE_DELETE_ERROR(1016, "文件删除失败！T_T"),
    FILE_DOWNLOAD_ERROR(1017, "文件下载错误！T_T");

    // 响应码
    private final Integer code;
    // 响应信息
    private final String message;

    RCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
