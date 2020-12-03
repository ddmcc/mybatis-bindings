package com.sunsharing.economic.mybatis.bindings.spring;

import com.sunsharing.economic.mybatis.bindings.helper.BindHelper;
import com.sunsharing.economic.mybatis.bindings.helper.Utils;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * @author jiangrz
 */
public class BindingProcessor {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired(required = false)
    private List<Utils> utils;

    private final BindHelper bindHelper = new BindHelper();

    public void init() {
        if (utils != null) {
            bindHelper.initCustomize(utils);
        }

        Configuration configuration = sqlSessionFactory.getConfiguration();
        Collection<Class<?>> mappers = configuration.getMapperRegistry().getMappers();
        for (Class<?> mapper : mappers) {
            if (configuration.hasMapper(mapper)) {
                bindHelper.processBinds(mapper, configuration);
            }
        }
    }

}
