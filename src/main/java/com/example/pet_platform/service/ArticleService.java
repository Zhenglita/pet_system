package com.example.pet_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.entity.Article;


public interface ArticleService extends IService<Article> {
    IPage<Article> getPage(int currentPage, int pageSize);
    IPage<Article> getPage(int currentPage, int pageSize, Article article);
}
