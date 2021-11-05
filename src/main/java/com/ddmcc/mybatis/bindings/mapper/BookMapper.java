package com.ddmcc.mybatis.bindings.mapper;

import com.ddmcc.mybatis.bindings.annotation.Binding;
import com.ddmcc.mybatis.bindings.annotation.Bindings;
import com.ddmcc.mybatis.bindings.entity.BaseBinding;
import com.ddmcc.mybatis.bindings.entity.Book;
import com.ddmcc.mybatis.bindings.helper.ObjectCache;
import com.ddmcc.mybatis.bindings.model.enums.DingStatusEnum;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Bindings({
    @Binding(type = StringUtils.class, alias = "ccccc", varType = BaseBinding.VarType.UTIL)
})
@Mapper
public interface BookMapper {


    /**
     * 综合排序
     *
     * @param param     搜索框参数
     * @return          List<Book>
     */
    @Bindings({
        @Binding(type = StringUtils.class, alias = "v", varType = BaseBinding.VarType.UTIL),
        @Binding(type = DateUtils.class, alias = "vvv", varType = BaseBinding.VarType.UTIL),
        @Binding(type = DingStatusEnum.class, alias = "a", varType = BaseBinding.VarType.ENUM),
        @Binding(type = ObjectCache.class, alias = "b", varType = BaseBinding.VarType.STATIC, varName = {"TEST_NAME"}),
        @Binding(alias = "e", varType = BaseBinding.VarType.EXPRESSION, expression = "@com.ddmcc.mybatis.bindings.helper.ObjectCache@TEST_NAME"),
        @Binding(alias = "c", varType = BaseBinding.VarType.EXPRESSION, expression = "new com.ddmcc.mybatis.bindings.entity.User('数据', 2)"),
        @Binding(alias = "d", varType = BaseBinding.VarType.EXPRESSION, expression = "@com.ddmcc.mybatis.bindings.model.enums.DingStatusEnum@APPROVALING.getCode()")
    })
    List<Book> select(@Param("book") Book param);


    List<Book> get();
}
