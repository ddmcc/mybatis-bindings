package com.ddmcc.mybatis.bindings.entity;


import com.ddmcc.mybatis.bindings.exception.BindingException;
import com.ddmcc.mybatis.bindings.helper.StringBuilderHolder;
import com.ddmcc.mybatis.bindings.helper.StringPool;


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
        StringBuilder builder = StringBuilderHolder.get();

        Arrays.stream(type.getEnumConstants()).forEach(e -> {
            Enum anEnum = (Enum) e;
            String join = builder
                .append(StringPool.AT)
                .append(type.getCanonicalName())
                .append(StringPool.AT)
                .append(anEnum.name())
                .append(StringPool.DOT)
                .append("getCode()").toString();

            builder.setLength(0);
            if (StringUtils.isNotBlank(alias)) {
                varDeclSqlNodes.add(new VarDeclSqlNode(StringUtils.join(alias, StringPool.DOT, anEnum.name()), join));
            } else {
                varDeclSqlNodes.add(new VarDeclSqlNode(anEnum.name(), join));
            }
        });
        return varDeclSqlNodes;
    }

}
