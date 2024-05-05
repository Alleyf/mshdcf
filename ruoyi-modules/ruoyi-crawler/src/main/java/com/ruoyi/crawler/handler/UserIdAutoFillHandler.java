package com.ruoyi.crawler.handler;


import com.ruoyi.common.satoken.utils.LoginHelper;
import com.tangzc.mpe.annotation.handler.IOptionByAutoFillHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 全局获取用户ID
 *
 * @author csFan
 * @version 1.0
 * @description <ul>
 * <li>此处实现IOptionByAutoFillHandler接口和AutoFillHandler接口均可</li>
 * <li>实现IOptionByAutoFillHandler接口，可以兼容框架内的BaseEntity</li>
 * <li>BaseEntity默认需要IOptionByAutoFillHandler的实现。后面会讲到BaseEntity的使用</li>
 * </ul>
 * @since 2024/3/23 17:00
 */
@Component
public class UserIdAutoFillHandler implements IOptionByAutoFillHandler<Long> {

    /**
     * @param object 当前操作的数据对象
     * @param clazz  当前操作的数据对象的class
     * @param field  当前操作的数据对象上的字段
     * @return userId 用户ID
     */
    @Override
    public Long getVal(Object object, Class<?> clazz, Field field) {
        return LoginHelper.getUserId();
    }
}
