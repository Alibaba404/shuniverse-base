package cn.shuniverse.base.core.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 蛮小满Sama at 2024-08-30 11:00
 *
 * @author 蛮小满Sama
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 操作描述
     *
     * @return 描述
     */
    String value() default "";

    /**
     * 是否保存参数
     *
     * @return true 保存 false 不保存
     */
    boolean saveParams() default true;
}
