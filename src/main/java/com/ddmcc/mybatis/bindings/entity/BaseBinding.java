package com.ddmcc.mybatis.bindings.entity;


import com.ddmcc.mybatis.bindings.helper.StringPool;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.scripting.xmltags.VarDeclSqlNode;

import java.util.List;

/**
 * File Description
 *
 * @author jiangrz
 * @date 2021-10-28 00:10
 */
public abstract class BaseBinding {


    public enum VarType {

        /**
         * 枚举
         */
        ENUM(1, "枚举"),

        /**
         * 静态
         */
        STATIC(3, "静态类"),

        /**
         * 工具类
         */
        UTIL(3, "工具类"),

        /**
         * 表达式
         */
        EXPRESSION(4, "表达式")
        ;


        private final int code;
        private final String name;

        VarType(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }


    protected final VarType varType;
    protected final String alias;
    protected final Class<?> type;


    protected BaseBinding(VarType varType, String alias, Class<?> type) {
        this.varType = varType;
        this.alias = alias;
        this.type = type;
    }

    protected BaseBinding(VarType varType, String alias) {
        this(varType, alias, null);
    }


    public final VarType getVarType() {
        return varType;
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
    public abstract List<VarDeclSqlNode> buildVarDeclSqlNode();


    public static String getSingletonExp(String className) {
        return String.format("@com.ddmcc.mybatis.bindings.helper.ObjectCache@getInstance('%s')", className);
    }

    public static String getSimpleName(String className) {
        return StringUtils.substringAfterLast(className, StringPool.DOT);
    }
}
