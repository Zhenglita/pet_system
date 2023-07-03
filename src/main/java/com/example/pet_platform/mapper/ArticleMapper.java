package com.example.pet_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pet_platform.entity.Article;
import com.example.pet_platform.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    List<Article> findAllByUserId(@Param("userId") Integer userId);

    List<Article> findAllByAbleAndUserId(@Param("enable") Boolean enable, @Param("userId") Integer userId);

    List<Article> findAllByAble(@Param("enable") Boolean enable);
}
