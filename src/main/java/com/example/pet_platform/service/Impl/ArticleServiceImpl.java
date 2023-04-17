package com.example.pet_platform.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.mapper.ArticleMapper;
import com.example.pet_platform.entity.Article;
import com.example.pet_platform.service.ArticleService;
import com.example.pet_platform.service.CommentService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Override
    public IPage<Article> getPage(int currentPage, int pageSize) {
        IPage<Article> page=new Page<>(currentPage,pageSize);
        articleMapper.selectPage(page,null);
        return page;
    }

    @Override
    public IPage<Article> getPage(int currentPage, int pageSize, Article article) {
        LambdaQueryWrapper<Article> lqw=new LambdaQueryWrapper<>();
        if (article!=null){
            lqw.like(Strings.isNotEmpty(article.getTitle()),Article::getTitle,article.getTitle());
            lqw.eq(true,Article::getEnable,true);
        }
        IPage<Article> page=new Page<>(currentPage,pageSize);
        articleMapper.selectPage(page,lqw);
        return page;
    }

}
