package com.project.demo.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动填充值
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("新增自动填充");
        // 自动填充 create_date 字段
        this.setFieldValByName("createDate", new Date(), metaObject);
        // 自动填充 update_date 字段
        this.setFieldValByName("updateDate", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新自动填充");
        // 自动填充 update_date 字段
        this.setFieldValByName("updateDate", new Date(), metaObject);
    }
}
