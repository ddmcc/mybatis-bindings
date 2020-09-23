package com.sunsharing.economic.mybatis.bindings.spring;

import com.sunsharing.economic.mybatis.bindings.helper.BindHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Set;

/**
 * @author jiangrz
 */
public class ClassPathMapperScanner extends org.mybatis.spring.mapper.ClassPathMapperScanner {


    private final BindHelper bindHelper = new BindHelper();

    public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        bindHelper.initCustomize(basePackages);
        doAfterScan(beanDefinitions);
        return beanDefinitions;
    }

    protected void doAfterScan(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            if (StringUtils.isNotBlank(definition.getBeanClassName())
                    && "org.mybatis.spring.mapper.MapperFactoryBean".equals(definition.getBeanClassName())) {
                definition.setBeanClass(MapperFactoryBean.class);
                definition.getPropertyValues().add("bindHelper", this.bindHelper);
            }
        }
    }
}
