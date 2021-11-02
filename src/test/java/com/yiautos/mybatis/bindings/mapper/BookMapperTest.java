package com.yiautos.mybatis.bindings.mapper;

import com.yiautos.mybatis.bindings.entity.Book;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;

@SpringBootTest
class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;


    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    void select() {

        Book book = new Book();
        book.setBookName("书籍2");
        System.out.println(bookMapper.select(book).stream().map(Object::toString).collect(Collectors.joining()));
        System.out.println(bookMapper.select(book).stream().map(Object::toString).collect(Collectors.joining()));

        // System.out.println(sqlSessionFactory.getConfiguration().getMappedStatement("com.yiautos.mybatis.bindings.mapper.BookMapper.select").getParameterMap());
      //  ObjectCache.OBJECT_MAP.putIfAbsent("StringUtils",  new StringUtils());
        //System.out.println(OgnlCache.getValue("\"@org.springframework.util.Assert@'\"", ObjectCache.OBJECT_MAP));
     //   System.out.println(OgnlCache.getValue("StringUtils", ObjectCache.OBJECT_MAP));
    }
}