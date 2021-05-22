package com.ddmcc.mybatis.bindings.mapper;

import com.ddmcc.mybatis.bindings.annotation.Binding;
import com.ddmcc.mybatis.bindings.annotation.Bindings;
import com.ddmcc.mybatis.bindings.entity.Book;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author jiangrz
 */
@Mapper
public interface BookMapper {


    /**
     * 综合排序
     *
     * @return          List<Book>
     */
    @Bindings({
        @Binding(type = Calculator.class, alias = "v")
    })
    List<Book> select(Book book);
}
