package com.example.pet_platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pet_platform.controller.util.CensorResult;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Article;
import com.example.pet_platform.entity.Comment;

import java.lang.reflect.Field;

import com.example.pet_platform.entity.User;
import com.example.pet_platform.mapper.CommentMapper;
import com.example.pet_platform.mapper.UserMapper;
import com.example.pet_platform.service.ArticleService;
import com.example.pet_platform.service.BaiduContentCensorService;
import com.example.pet_platform.service.CommentService;
import com.example.pet_platform.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import sun.dc.pr.PRError;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
@CrossOrigin
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping
    public R getall() {
        return new R(true, commentService.list());
    }

    @GetMapping("/admin")
    public R getSensitiveAll() {
      return commentService.getSensitiveAll();
    }

    @PostMapping
    public R add(@RequestBody Comment comment) throws IllegalAccessException {
      return commentService.add(comment);
    }

    @GetMapping("/s/{foreginId}")
    public R getByIdOwn(@PathVariable Integer foreginId) {
       return commentService.getByIdOwn(foreginId);
    }

    @DeleteMapping("{id}")
    public R deleteById(@PathVariable Integer id) {
      return  commentService.deleteByIdOwn(id);
    }

    @GetMapping("/a/{uid}")
    public R getByUserId(@PathVariable Integer uid) {
       return  commentService.getByUserId(uid);
    }

    @GetMapping("/home/new")
    public R getTenNewComment() {
       return commentService.getTenNewComment();

    }

    @GetMapping("/new/{id}")
    public R getTheArticleNewComment(@PathVariable Integer id) {
       return  commentService.getTheArticleNewComment(id);

    }
}
