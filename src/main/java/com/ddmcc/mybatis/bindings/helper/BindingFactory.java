package com.ddmcc.mybatis.bindings.helper;


import com.ddmcc.mybatis.bindings.annotation.Binding;
import com.ddmcc.mybatis.bindings.entity.BaseBinding;
import com.ddmcc.mybatis.bindings.entity.EnumBinding;
import com.ddmcc.mybatis.bindings.entity.ExpressionBinding;
import com.ddmcc.mybatis.bindings.entity.StaticVarBinding;
import com.ddmcc.mybatis.bindings.entity.UtilClassBinding;

/**
 * binding工厂
 *
 * @author jiangrz
 * @date 2021-11-04 11:06
 */
public class BindingFactory {

    public BaseBinding createBinding(Binding binding) {
        switch (binding.varType()) {
            case ENUM:
                return new EnumBinding(binding.alias(), binding.type());
            case UTIL:
                return new UtilClassBinding(binding.alias(), binding.type());
            case STATIC:
                return new StaticVarBinding(binding.alias(), binding.type(), binding.varName());
            case EXPRESSION:
                return new ExpressionBinding(binding.alias(), binding.expression());
            default:
                return null;
        }
    }

}
