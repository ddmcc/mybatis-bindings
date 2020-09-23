package com.sunsharing.economic.mybatis.bindings.helper;


import com.sunsharing.economic.mybatis.bindings.annotation.BindEntity;
import com.sunsharing.economic.mybatis.bindings.annotation.Bindings;
import com.sunsharing.economic.mybatis.bindings.exception.BindingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.VarDeclSqlNode;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
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
     * 默认的所有工具 继承Utils接口而来
     * @see Utils
     */
    private final List<VarDeclSqlNode> defaultUtils = new ArrayList<>();

    /**
     * 已解析或非动态sql
     */
    private final Map<String, Boolean> skips = new HashMap<>();

    /**
     * 初始化
     */
    private boolean initialized = false;


    public void processBinds(Class<?> mapper, Configuration configuration) {
        if (mapper.isAnnotationPresent(Bindings.class)) {
            Annotation[] annotations = mapper.getAnnotationsByType(Bindings.class);
            List<BindEntity> bindEntities = resolveAnnotations(annotations);
            if (bindEntities.isEmpty()) {
                processAnnotationMethod(mapper, configuration, defaultUtils);
            } else {
                processAnnotationMethod(mapper, configuration, convertContext(bindEntities));
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
                List<BindEntity> methodAnnotations = resolveAnnotations(annotations);
                List<VarDeclSqlNode> methodSqlNode = new ArrayList<>();
                if (methodAnnotations.isEmpty()) {
                    methodSqlNode.addAll(this.defaultUtils);
                } else {
                    methodSqlNode.addAll(convertContext(methodAnnotations));
                }

                if (list != null) {
                    methodSqlNode.addAll(list);
                }

                setSqlContents(configuration, mapperClass.getCanonicalName() + "." + method.getName(), distinct(methodSqlNode));
            } else if (list != null) {
                setSqlContents(configuration, mapperClass.getCanonicalName() + "." + method.getName(), list);
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
    private List<BindEntity> resolveAnnotations(Annotation[] annotations) {
        AnnotatedElement annotatedElement = AnnotatedElementUtils.forAnnotations(annotations);
        Bindings[] binds = annotatedElement.getAnnotationsByType(Bindings.class);
        return Stream.of(binds[0].value()).collect(Collectors.toList());
    }


    /**
     * 初始化自定义utils
     *
     * @param basePackages  包路径
     */
    public void initCustomize(String...basePackages) {
        if (!initialized) {
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AssignableTypeFilter(Utils.class));
            Set<BeanDefinition> components = new HashSet<>();

            for (String basePackage : basePackages) {
                components.addAll(provider.findCandidateComponents(basePackage));
            }

            List<Utils> utils = new ArrayList<>();
            for (BeanDefinition component : components) {
                try {
                    Class<Utils> clazz = (Class<Utils>) Class.forName(component.getBeanClassName());
                    if (clazz.isEnum() || clazz.isInterface()) {
                        continue;
                    }

                    utils.add(clazz.newInstance());
                    initialized = true;
                } catch (ClassNotFoundException e) {
                    throw new BindingException(String.format("在 %s 中未找到 %s 类", Arrays.toString(basePackages), component.getBeanClassName()));
                } catch (IllegalAccessException e) {
                    throw new BindingException(String.format("类 %s 强转 Utils 出错", component.getBeanClassName()), e);
                } catch (InstantiationException e) {
                    throw new BindingException(String.format("创建 %s 对象出错", component.getBeanClassName()), e);
                }
            }

            this.defaultUtils.addAll(convertDefaultContext(utils));
        }
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
    private List<VarDeclSqlNode> convertContext(List<BindEntity> bindEntities) {
        List<VarDeclSqlNode> result = new ArrayList<>();
        for (BindEntity bindEntity : bindEntities) {
            covertContext(result, StringUtils.substringAfterLast(bindEntity.type().getCanonicalName(), "."),
                    bindEntity.type().getCanonicalName(), bindEntity.alias());
        }

        return result;
    }

    /**
     * 实现类列表转上下文sql节点，空注解时会绑定上
     *
     * @param utils     utils
     * @return          list
     */
    private List<VarDeclSqlNode> convertDefaultContext(List<Utils> utils) {
        List<VarDeclSqlNode> result = new ArrayList<>();
        for (Utils util : utils) {
            covertContext(result, util.type().getName(), util.type().getCanonicalName(), util.alias());
        }

        return result;
    }

    private void covertContext(List<VarDeclSqlNode> result, String keyword, String canonicalName, String alias) {
        result.add(new VarDeclSqlNode(keyword, String.format("new %s()", canonicalName)));
        if (StringUtils.isNotBlank(alias) && !keyword.equals(alias)) {
            result.add(new VarDeclSqlNode(alias, String.format("new %s()", canonicalName)));
        }
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
