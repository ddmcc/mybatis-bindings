package com.sunsharing.economic.mybatis.bindings.spring;

import com.sunsharing.economic.mybatis.bindings.helper.BindHelper;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangrz
 */
public class MapperFactoryBean<T> extends org.mybatis.spring.mapper.MapperFactoryBean<T> {

    private BindHelper bindHelper;

    private final List<Class<?>> knowsClass = new ArrayList<>();

    public MapperFactoryBean() {
    }

    public MapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();

        Configuration configuration = getSqlSession().getConfiguration();
        Class<?> mapper = getObjectType();
        Assert.notNull(mapper, "mapperInterface 为空");
        if (configuration.hasMapper(mapper) && !knowsClass.contains(mapper)) {
            knowsClass.add(mapper);
            bindHelper.processBinds(mapper, configuration);
        }
    }


    public void setBindHelper(BindHelper bindHelper) {
        this.bindHelper = bindHelper;
    }

}
