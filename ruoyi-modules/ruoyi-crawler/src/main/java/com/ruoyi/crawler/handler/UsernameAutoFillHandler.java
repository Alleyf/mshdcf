package com.ruoyi.crawler.handler;

import com.ruoyi.common.satoken.utils.LoginHelper;
import com.tangzc.mpe.annotation.handler.AutoFillHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 全局获取用户名
 *
 * @author csFan
 * @version 1.0
 * @description: TODO
 * @since 2024/3/23 16:53
 */
@Component
public class UsernameAutoFillHandler implements AutoFillHandler<String> {

    /**
     * @param object 当前操作的数据对象
     * @param clazz  当前操作的数据对象的class
     * @param field  当前操作的数据对象上的字段
     * @return 当前登录用户id
     */
    @Override
    public String getVal(Object object, Class<?> clazz, Field field) {
        return LoginHelper.getUsername();
    }
}
