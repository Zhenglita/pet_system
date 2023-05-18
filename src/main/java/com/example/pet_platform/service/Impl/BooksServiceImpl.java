package com.example.pet_platform.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.mapper.BooksMapper;
import com.example.pet_platform.service.BooksService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements BooksService {
    @Autowired
    private BooksMapper booksMapper;

    @Override
    public IPage<Books> getPage(int currentPage, int pageSize, Books books) {
        LambdaQueryWrapper<Books> lqw = new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(books.getType()), Books::getType, books.getType());
        lqw.like(Strings.isNotEmpty(books.getAuthor()), Books::getAuthor, books.getAuthor());
        lqw.like(Strings.isNotEmpty(books.getBookname()), Books::getBookname, books.getBookname());
        lqw.eq(books.getEnable() != null, Books::getEnable, books.getEnable());
        IPage<Books> page = new Page<>(currentPage, pageSize);
        booksMapper.selectPage(page, lqw);
        return page;
    }
}
