package com.example.pet_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.entity.Books;

import java.util.List;

public interface BooksService extends IService<Books> {
    IPage<Books> getPage(int currentPage, int pageSize, Books books);
    //前五销量图书
    List<Books> getTopFive();
    //首页展示图书
    List<Books> getTop();
}
