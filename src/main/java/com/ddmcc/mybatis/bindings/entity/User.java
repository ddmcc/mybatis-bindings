package com.ddmcc.mybatis.bindings.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * File Description
 *
 * @author jiangrz
 * @date 2021-11-01 23:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String userName;

    private Integer age;

    public boolean method1(String param) {
        System.out.println(param);
        this.userName = param;
        return true;
    }
}
