package com.yiautos.mybatis.bindings.spring;

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

    private volatile boolean initialized = false;

    @Bean
    public BindingProcessor bindingProcessor() {
        return new BindingProcessor();
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
