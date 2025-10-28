package cn.shuniverse.base.handler;

import cn.hutool.core.date.DateUtil;
import cn.shuniverse.base.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by 蛮小满Sama at 2025-06-17 16:27
 *
 * @author 蛮小满Sama
 * @description
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        String username = SecurityUtils.getUsername();
        log.info("Start insert fill...Operation user: {}", username);
        if (StringUtils.isNotBlank(username)) {
            this.setFieldValByName("createdBy", username, metaObject);
        }
        this.setFieldValByName("createdAt", DateUtil.date(), metaObject);
        this.setFieldValByName("updatedAt", DateUtil.date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String username = SecurityUtils.getUsername();
        log.info("Start update fill...Operation user:{}", username);
        if (StringUtils.isNotBlank(username)) {
            this.setFieldValByName("updatedBy", username, metaObject);
        }
        this.setFieldValByName("updatedAt", DateUtil.date(), metaObject);
    }
}
