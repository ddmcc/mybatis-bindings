package com.sunsharing.economic.mybatis.bindings.annotation;

import java.lang.annotation.*;

/**
 * @author jiangrz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BindEntity {

    /**
     * 类型
     *
     * @return  class
     */
    Class<?> type();

    /**
     * 自定义别名
     *
     * @return  别名
     */
    String alias();


}
