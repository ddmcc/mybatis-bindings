package com.ddmcc.mybatis.bindings.spring;

import com.ddmcc.mybatis.bindings.helper.BindHelper;
import com.ddmcc.mybatis.bindings.helper.StringBuilderHolder;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * @author jiangrz
 */
public class BindingProcessor {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private final BindHelper bindHelper;

    public BindingProcessor(BindHelper bindHelper) {
        this.bindHelper = bindHelper;
    }

    public void init() {
        try {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            Collection<Class<?>> mappers = configuration.getMapperRegistry().getMappers();
            for (Class<?> mapper : mappers) {
                if (configuration.hasMapper(mapper)) {
                    bindHelper.processBinds(mapper, configuration);
                }
            }
        } finally {
            StringBuilderHolder.remove();
        }

    }

}
