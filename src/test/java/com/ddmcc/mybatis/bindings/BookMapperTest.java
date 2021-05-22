package com.ddmcc.mybatis.bindings;

import com.ddmcc.mybatis.bindings.entity.Book;
import com.ddmcc.mybatis.bindings.mapper.BookMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;

    @Test
    void select() {
        Book book = new Book();
        book.setType(2);
        book.setBookName("111");
        bookMapper.select(book);
    }
}