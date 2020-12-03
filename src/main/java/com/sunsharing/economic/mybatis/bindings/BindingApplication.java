package com.sunsharing.economic.mybatis.bindings;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.sunsharing.economic.mapper")
@SpringBootApplication
public class BindingApplication {


    public static void main(String[] args) {
        SpringApplication.run(BindingApplication.class, args);
    }

}
