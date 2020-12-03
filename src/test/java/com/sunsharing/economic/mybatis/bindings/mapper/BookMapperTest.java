package com.sunsharing.economic.mybatis.bindings.mapper;

import com.sunsharing.economic.mybatis.bindings.entity.bo.Book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;

    @Test
    void select() {
        Book book = new Book();
        book.setBookName("wqewqewq3213");
        Assert.noNullElements(bookMapper.select(book), "");
    }
}