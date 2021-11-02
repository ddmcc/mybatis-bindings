package com.yiautos.mybatis.bindings.entity;

import com.yiautos.mybatis.bindings.exception.BindingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.scripting.xmltags.VarDeclSqlNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 绑定工具类变量
 *
 * 例如：
 *
 * @Bindings({
 *      @Binding(type = StringUtils.class, alias = "v", varType = BaseBinding.VarType.UTIL),
 *      @Binding(type = DateUtils.class, alias = "d", varType = BaseBinding.VarType.UTIL)
 *  })
 *
 *
 * xml: #{UTIL_CLASS.method()} or #{alias.VAR_NAME}
 *
 *
 *  <if test="v.isNotBlank(other_var)">
 *      AND table_column like CONCAT('%', #{other_var}, '%')
 *  </if>
 *
 *  <if test="StringUtils.isBlank(other_var)">
 *      AND table_column like CONCAT('%', #{other_var}, '%')
 *  </if>
 *
 * @author jiangrz
 * @date 2021-11-01 17:00
 */
public class UtilClassBinding extends BaseBinding {


    public UtilClassBinding(String alias, Class<?> type) {
        super(VarType.UTIL, alias, type);
        if (type == null) {
            throw new BindingException("type 不能为空");
        }
    }

    @Override
    public List<VarDeclSqlNode> buildVarDeclSqlNode() {
        List<VarDeclSqlNode> varDeclSqlNodes = new ArrayList<>();
        String typeName = type.getCanonicalName();
        varDeclSqlNodes.add(new VarDeclSqlNode(getSimpleName(typeName), getSingletonExp(typeName)));
        if (StringUtils.isNotBlank(alias)) {
            varDeclSqlNodes.add(new VarDeclSqlNode(alias , getSingletonExp(typeName)));
        }
        return varDeclSqlNodes;
    }
}
