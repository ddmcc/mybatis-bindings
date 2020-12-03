package com.sunsharing.economic.mybatis.bindings.mapper;

import com.sunsharing.economic.mybatis.bindings.annotation.Binding;
import com.sunsharing.economic.mybatis.bindings.annotation.Bindings;
import com.sunsharing.economic.mybatis.bindings.entity.bo.Book;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {


    /**
     * 综合排序
     *
     * @param param     搜索框参数
     * @return          List<Book>
     */
    @Bindings({
        @Binding(type = StringUtils.class, alias = "v")
    })
    List<Book> select(Book param);
}
