package com.sunsharing.economic.mybatis.bindings.annotation;


import java.lang.annotation.*;

/**
 * @author jiangrz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Bindings {

    /**
     * 绑定工具类数组
     *
     * @return  array
     */
    BindEntity[] value();

}
