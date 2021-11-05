package com.ddmcc.mybatis.bindings.spring;

import com.ddmcc.mybatis.bindings.helper.BindHelper;
import com.ddmcc.mybatis.bindings.helper.BindingFactory;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author jiangrz
 */
@Configuration
@AutoConfigureAfter(SqlSessionFactory.class)
public class BindingAutoConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private static volatile boolean initialized = false;

    @Bean
    public BindingProcessor bindingProcessor() {
        BindingFactory factory = new BindingFactory();
        BindHelper bindHelper = new BindHelper(factory);
        return new BindingProcessor(bindHelper);
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        synchronized (BindingAutoConfiguration.class) {
            if (!initialized) {
                initialized = true;
                bindingProcessor().init();
            }
        }
    }
}
