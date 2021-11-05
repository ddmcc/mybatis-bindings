package com.ddmcc.mybatis.bindings.entity;

import com.ddmcc.mybatis.bindings.exception.BindingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.scripting.xmltags.VarDeclSqlNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 绑定表达式 等于xml中标签 <bind name="alias" value="expression" />
 *
 * 例如：
 * 1.绑定普通对象
 *
 * 接口或方法：
 *
 *   @Bindings({
 *       @Binding(varType = BaseBinding.VarType.EXPRESSION, alias = "c", expression = "new com.xxx.xxx.User()")
 *       或者
 *       @Binding(varType = BaseBinding.VarType.EXPRESSION, alias = "c", expression = "new com.xxx.xxx.User(1, 'name1')")
 *   })
 *
 * xml:
 *
 * if中使用变量： <if test="c.userName == 'xxx'">
 * 或者参数拼接： AND table_column = #{c.age}
 * 或者调用方法： <if test="c.method1()">  <if test="c.method2(c.userName)">
 *
 *
 * 2.绑定静态变量
 *
 * 接口或方法：
 *
 *   @Binding(varType = BaseBinding.VarType.EXPRESSION, alias = "c", expression = "@com.xxx.xxx.ClassName@CONTANT_NAME")
 *
 * @see StaticVarBinding
 *
 *
 * 3.绑定枚举变量
 *
 *    @Binding(varType = BaseBinding.VarType.EXPRESSION, alias = "d", expression = "@com.xxx.xxx.EnumClass@ENUM_NAME.getCode()")
 *
 * @see EnumBinding
 *
 * @author jiangrz
 * @date 2021-11-01 23:10
 */
public class ExpressionBinding extends BaseBinding {

    private final String expression;

    public ExpressionBinding(String alias, String exp) {
        super(VarType.EXPRESSION, alias);
        this.expression = exp;

        if (StringUtils.isBlank(alias) || StringUtils.isBlank(expression)) {
            throw new BindingException("alias or expression 不能为空");
        }
    }


    @Override
    public List<VarDeclSqlNode> buildVarDeclSqlNode() {
        List<VarDeclSqlNode> varDeclSqlNodes = new ArrayList<>(1);
        varDeclSqlNodes.add(new VarDeclSqlNode(alias, expression));
        return varDeclSqlNodes;
    }
}
