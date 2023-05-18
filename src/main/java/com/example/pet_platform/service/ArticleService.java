package com.example.pet_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.entity.Article;
import com.example.pet_platform.entity.Comment;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public interface ArticleService extends IService<Article> {
    //    查找指定id的文章
    Map<String,Object> getById(Integer id, HttpServletRequest request);

    //            查找指定文章的作者名字
    List<Article> allByUserId(Integer userId);
    //管理员审核通知消息
    int update(Article article,HttpServletRequest request);
    //用户查看自己发表文章是否被批准
    List<Article> getByAbleAndUserId(boolean enable,Integer userId);
    List<Article> getByAble(boolean enable);
    //首页最新文章
    List<Article> getTen();
    //热门文章
    Map<String,List<Comment>> getTenCommentArticle(Integer size);
    //首页随机推荐
    List<Article> getTenRandomArticle();
    //管理员推荐
    List<Article> getTenMendArticle();
    IPage<Article> getPage(int currentPage, int pageSize);
    //分页条件查询
    IPage<Article> getPage(int currentPage, int pageSize, Article article);
}
