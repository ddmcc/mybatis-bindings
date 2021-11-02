package com.yiautos.mybatis.bindings.entity;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.yiautos.mybatis.bindings.exception.BindingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.scripting.xmltags.VarDeclSqlNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 绑定枚举
 *
 * 例如：
 *
 *   @Bindings({
 *       @Binding(varType = BaseBinding.VarType.ENUM, type = DingStatusEnum.class, alias = "a")
 *   })
 *
 * xml：#{ENUM_VAR_NAME} or #{alias.ENUM_VAR_NAME}
 *
 *  AND table_column = #{a.APPROVALING}
 *  AND table_column = #{APPROVALING}
 *
 *
 * @author jiangrz
 * @date 2021-10-28 00:40
 */
public class EnumBinding extends BaseBinding {


    public EnumBinding(String alias, Class<?> enumClass) {
        super(VarType.ENUM, alias, enumClass);
        if (null == enumClass || enumClass == Object.class) {
            throw new BindingException("type 不能为空");
        }
    }


    @Override
    public List<VarDeclSqlNode> buildVarDeclSqlNode() {
        List<VarDeclSqlNode> varDeclSqlNodes = new ArrayList<>();
        Arrays.stream(type.getEnumConstants()).forEach(e -> {
            String join = StringUtils.join(
                StringPool.AT,
                type.getCanonicalName(),
                StringPool.AT,
                e.toString(),
                StringPool.DOT,
                "getCode()"
            );
            varDeclSqlNodes.add(new VarDeclSqlNode(e.toString(), join));
            if (StringUtils.isNotBlank(alias)) {
                varDeclSqlNodes.add(new VarDeclSqlNode(alias + StringPool.DOT + e.toString(), join));
            }
        });
        return varDeclSqlNodes;
    }

}
