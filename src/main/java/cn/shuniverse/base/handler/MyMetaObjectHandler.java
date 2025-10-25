package cn.shuniverse.base.handler;

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
        String uid = SecurityUtils.getUserId();
        log.info("Start insert fill...Operation user: {}", uid);
        if (StringUtils.isNotBlank(uid)) {
            this.setFieldValByName("createdBy", uid, metaObject);
        }
        this.setFieldValByName("createdAt", new Date(), metaObject);
        this.setFieldValByName("updatedAt", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String uid = SecurityUtils.getUserId();
        log.info("Start update fill...Operation user:{}", uid);
        if (StringUtils.isNotBlank(uid)) {
            this.setFieldValByName("updatedBy", uid, metaObject);
        }
        this.setFieldValByName("updatedAt", new Date(), metaObject);
    }
}
