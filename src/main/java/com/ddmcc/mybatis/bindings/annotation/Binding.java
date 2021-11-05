package com.ddmcc.mybatis.bindings.annotation;

import com.ddmcc.mybatis.bindings.entity.BaseBinding;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jiangrz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Binding {

    /**
     * 类型
     *
     * @return  class
     */
    Class<?> type() default Object.class;

    /**
     * 自定义别名
     *
     * @return  别名
     */
    String alias() default "";

    BaseBinding.VarType varType() default BaseBinding.VarType.UTIL;

    String expression() default "";

    String[] varName() default {};
}
