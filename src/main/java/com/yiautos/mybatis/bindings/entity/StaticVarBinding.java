package com.yiautos.mybatis.bindings.entity;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.yiautos.mybatis.bindings.exception.BindingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.scripting.xmltags.VarDeclSqlNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 绑定静态变量
 *
 * 例如：
 *
 *  @Bindings({
 *      @Binding(varType = BaseBinding.VarType.STATIC, type = ObjectCache.class, alias = "b", varName = {"TEST_NAME", "NAME2"})
 *  })
 *
 * xml：#{CLASS_NAME.VAR_NAME} or #{alias.VAR_NAME}
 *
 *  AND table_column = #{b.TEST_NAME}
 *  AND table_column = #{ObjectCache.NAME2}
 *
 * @author jiangrz
 * @date 2021-11-01 20:21
 */
public class StaticVarBinding extends BaseBinding {


    private final String[] varName;


    public StaticVarBinding(String alias, Class<?> type, String[] varName) {
        super(VarType.STATIC, alias, type);
        this.varName = varName;
        if (null == varName || varName.length == 0 || null == type) {
            throw new BindingException("varName or type 不能为空");
        }
    }

    /**
     * 获取 VarDeclSqlNode 对象
     * name: 累名，别名
     * expression: 获取值的ognl表达式
     *
     * @return VarDeclSqlNode
     * @author jiangrz
     * @date 2021-11-01 19:39
     * @see VarDeclSqlNode
     */
    @Override
    public List<VarDeclSqlNode> buildVarDeclSqlNode() {
        List<VarDeclSqlNode> varDeclSqlNodes = new ArrayList<>();
        StringBuilder builder = new StringBuilder().append(StringPool.AT).append(type.getCanonicalName()).append(StringPool.AT);
        int count = builder.length();
        Stream.of(varName).forEach(name -> {
            String string = builder.append(name).toString();
            varDeclSqlNodes.add(new VarDeclSqlNode(getSimpleName(type.getCanonicalName())+ StringPool.DOT + name, string));
            if (StringUtils.isNotBlank(alias)) {
                varDeclSqlNodes.add(new VarDeclSqlNode(alias + StringPool.DOT + name, string));
            }
            builder.delete(count, count + name.length());
        });
        return varDeclSqlNodes;
    }
}
