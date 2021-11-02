package com.yiautos.mybatis.bindings.helper;


import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.yiautos.mybatis.bindings.annotation.Binding;
import com.yiautos.mybatis.bindings.annotation.Bindings;
import com.yiautos.mybatis.bindings.entity.BaseBinding;
import com.yiautos.mybatis.bindings.entity.EnumBinding;
import com.yiautos.mybatis.bindings.entity.ExpressionBinding;
import com.yiautos.mybatis.bindings.entity.StaticVarBinding;
import com.yiautos.mybatis.bindings.entity.UtilClassBinding;
import com.yiautos.mybatis.bindings.exception.BindingException;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.VarDeclSqlNode;
import org.apache.ibatis.session.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jiangrz
 */
public class BindHelper {

    /**
     * 已解析或非动态sql
     */
    private final Map<String, Boolean> skips = new HashMap<>();


    public void processBinds(Class<?> mapper, Configuration configuration) {
        if (mapper.isAnnotationPresent(Bindings.class)) {
            Annotation[] annotations = mapper.getAnnotationsByType(Bindings.class);
            List<Binding> bindEntities = resolveAnnotations(annotations);
            if (!bindEntities.isEmpty()) {
                processAnnotationMethod(mapper, configuration, convertContext(bindEntities));
            } else {
                throw new BindingException("找不到 @Binding 节点");
            }

        } else {
            processAnnotationMethod(mapper, configuration, null);
        }
    }


    /**
     * 获取方法注解
     *
     * @param mapperClass       接口mapper
     * @param configuration     configuration
     * @param list              接口的注解list
     */
    public void processAnnotationMethod(Class<?> mapperClass, Configuration configuration, List<VarDeclSqlNode> list) {
        Method[] methods = mapperClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Bindings.class)) {
                Annotation[] annotations = method.getAnnotationsByType(Bindings.class);
                List<Binding> methodAnnotations = resolveAnnotations(annotations);
                List<VarDeclSqlNode> methodSqlNode = new ArrayList<>();
                if (!methodAnnotations.isEmpty()) {
                    methodSqlNode.addAll(convertContext(methodAnnotations));
                }

                if (list != null) {
                    methodSqlNode.addAll(list);
                }

                setSqlContents(configuration, mapperClass.getCanonicalName() + StringPool.DOT + method.getName(), distinct(methodSqlNode));
            } else if (list != null) {
                setSqlContents(configuration, mapperClass.getCanonicalName() + StringPool.DOT + method.getName(), list);
            }
        }

    }


    /**
     * 工具类绑定到sql上下文
     *
     * @param configuration     configuration
     * @param msId              msId
     * @param declSqlNodes      declSqlNodes
     */
    private void setSqlContents(Configuration configuration, String msId, List<VarDeclSqlNode> declSqlNodes) {
        if (skips.containsKey(msId)) {
            return;
        }

        MappedStatement ms = configuration.getMappedStatement(msId);
        if (isDynamicSqlSource(ms)) {
            DynamicSqlSource dynamicSqlSource = getSqlSource(ms);
            List<SqlNode> sqlNodes = getSqlContents(dynamicSqlSource);
            sqlNodes.addAll(0, declSqlNodes);
            setSqlContents(dynamicSqlSource, sqlNodes);
        }

        skips.put(msId, true);
    }

    /**
     * 工具类绑定到sql上下文
     *
     * @param dynamicSqlSource  dynamicSqlSource
     * @param list              list
     */
    private void setSqlContents(DynamicSqlSource dynamicSqlSource, List<SqlNode> list) {
        try {
            Object sqlNode;
            if ((sqlNode = SystemMetaObject.forObject(dynamicSqlSource).getValue("rootSqlNode")) instanceof SqlNode) {
                SystemMetaObject.forObject((sqlNode)).setValue("contents", list);
            }
        } catch (Exception e) {
            throw new BindingException("设置接口sql节点出错", e);
        }
    }


    /**
     * 注解数组
     *
     * @param annotations   annotations
     * @return              bindEntity
     */
    private List<Binding> resolveAnnotations(Annotation[] annotations) {
        AnnotatedElement annotatedElement = AnnotatedElementUtils.forAnnotations(annotations);
        Bindings[] binds = annotatedElement.getAnnotationsByType(Bindings.class);
        return Stream.of(binds[0].value()).collect(Collectors.toList());
    }


    /**
     * 原始动态sql
     *
     * @param ms    ms
     * @return      DynamicSqlSource
     */
    private DynamicSqlSource getSqlSource(MappedStatement ms) {
        SqlSource sqlSource;
        if ((sqlSource = ms.getSqlSource()) instanceof DynamicSqlSource) {
            return (DynamicSqlSource) sqlSource;
        }

        throw new BindingException(String.format("获取接口 %s sql错误，应该是动态sql", ms.getId()));
    }


    /**
     * 是否动态sql
     *
     * @param ms        ms
     * @return          true/false
     */
    private boolean isDynamicSqlSource(MappedStatement ms) {
        return ms.getSqlSource() instanceof DynamicSqlSource;
    }

    /**
     * 动态sql节点
     *
     * @param dynamicSqlSource      dynamicSqlSource
     * @return                      List<SqlNode>
     */
    private List<SqlNode> getSqlContents(DynamicSqlSource dynamicSqlSource) {
        Object sqlNode;
        if ((sqlNode = SystemMetaObject.forObject(dynamicSqlSource).getValue("rootSqlNode")) instanceof SqlNode) {
            return (List<SqlNode>) SystemMetaObject.forObject((sqlNode)).getValue("contents");
        }

        throw new BindingException("获取接口sql节点出错");
    }


    /**
     * 注解转上下文sql节点
     *
     * @param bindEntities  注解列表
     * @return              list
     */
    private List<VarDeclSqlNode> convertContext(List<Binding> bindEntities) {
        List<VarDeclSqlNode> result = new ArrayList<>();
        for (Binding binding : bindEntities) {
            if (binding.type().isEnum()) {
                EnumBinding enumVar = new EnumBinding(binding.alias(), binding.type());
                result.addAll(0, enumVar.buildVarDeclSqlNode());
            } else if (binding.varType() == BaseBinding.VarType.UTIL){
                UtilClassBinding utilVar = new UtilClassBinding(binding.alias(), binding.type());
                result.addAll(0, utilVar.buildVarDeclSqlNode());
            } else if (binding.varType() == BaseBinding.VarType.STATIC) {
                StaticVarBinding staticVar = new StaticVarBinding(binding.alias(), binding.type(), binding.varName());
                result.addAll(0, staticVar.buildVarDeclSqlNode());
            } else if (binding.varType() == BaseBinding.VarType.EXPRESSION) {
                ExpressionBinding expressionBinding = new ExpressionBinding(binding.alias(), binding.expression());
                result.addAll(0, expressionBinding.buildVarDeclSqlNode());
            }

        }

        return result;
    }

    /**
     * 去重
     *
     * @param list1     list1
     * @return          list
     */
    private List<VarDeclSqlNode> distinct(List<VarDeclSqlNode> list1) {
        return list1.stream().filter(distinctByKey(b -> SystemMetaObject.forObject(b).getValue("name"))).collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
