package cn.shuniverse.base.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 蛮小满Sama at 2025-10-19 13:46
 *
 * @author 蛮小满Sama
 * @description 解密请求数据
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptRequest {
}
