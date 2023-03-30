package com.example.pet_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.entity.Books;

public interface BooksService extends IService<Books> {
    IPage<Books> getPage(int currentPage, int pageSize, Books books);
}
