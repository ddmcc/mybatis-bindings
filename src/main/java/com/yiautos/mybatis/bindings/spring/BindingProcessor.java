package com.yiautos.mybatis.bindings.spring;

import com.yiautos.mybatis.bindings.helper.BindHelper;

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

    private final BindHelper bindHelper = new BindHelper();

    public void init() {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        Collection<Class<?>> mappers = configuration.getMapperRegistry().getMappers();
        for (Class<?> mapper : mappers) {
            if (configuration.hasMapper(mapper)) {
                bindHelper.processBinds(mapper, configuration);
            }
        }
    }

}
