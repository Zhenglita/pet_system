package com.example.pet_platform.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.controller.util.TokenUtils;
import com.example.pet_platform.entity.*;
import com.example.pet_platform.mapper.ArticleMapper;
import com.example.pet_platform.mapper.CommentMapper;
import com.example.pet_platform.service.*;

import com.example.pet_platform.util.JWTUtils;
import com.example.pet_platform.util.RedisGetUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")
@CrossOrigin
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //    获取所有文章
    @GetMapping
    public R getAll() {
        return new R(true, articleService.list());
    }

    //    查找指定id的文章
    @GetMapping("/s/{id}")
    public R getById(@PathVariable Integer id, HttpServletRequest request) {
        Map<String, Object> map = articleService.getById(id, request);
        Object article = map.get("article");
        if (Integer.parseInt(String.valueOf(map.get("size"))) > 0) {
            return new R(true, article, "已关注");
        }
        return new R(true, article, "未关注");
    }

    //    @GetMapping("/{currentPage}/{pageSize}")
//    public R getPage(@PathVariable int currentPage, @PathVariable int pageSize){
//        IPage<Article> page=articleService.getPage(currentPage,pageSize);
//        if (currentPage>page.getPages()){
//            page=articleService.getPage((int)page.getPages(),pageSize);
//        }
//        return new R(true,page);
//    }
//    分页查询
//    @Cacheable(value = "R",key="'getPage'")
    @GetMapping("/{currentPage}/{pageSize}")
    public R getPage(@PathVariable int currentPage, @PathVariable int pageSize, Article article) {
        IPage<Article> page = articleService.getPage(currentPage, pageSize, article);
        if (currentPage > page.getPages()) {
            page = articleService.getPage((int) page.getPages(), pageSize, article);
        }
        return new R(true, page);
    }

    //            查找指定文章的作者名字
    @GetMapping("/a/{userId}")
    public R getByUserId(@PathVariable Integer userId) {
        return new R(true, articleService.allByUserId(userId));
    }

    //    保存上传文章
    @PostMapping
    public R save(@RequestBody Article article, HttpServletRequest request) {
        String userid = RedisGetUser.getUserid(stringRedisTemplate,request);
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("uid", Integer.parseInt(userid));
        User one = userService.getOne(qw);
        if (one.getRole().equals("ROLE_READER")) {
            return new R(false);
        }
        return new R(true, articleService.save(article));
    }

    @DeleteMapping("{id}")
    public R delete(@PathVariable Integer id) {
        return new R(true, articleService.removeById(id));
    }

    @PutMapping
    public R update(@RequestBody Article article, HttpServletRequest request) {
        return new R(true, articleService.update(article, request));
    }

    //用户查看自己发表文章是否被批准
    @GetMapping("/e/{enable}/{userId}")
    public R getByAbleAndUserId(@PathVariable boolean enable, @PathVariable Integer userId) {
        return new R(true, articleService.getByAbleAndUserId(enable, userId));
    }

    @GetMapping("/admin/{enable}")
    public R getByAble(@PathVariable boolean enable) {
        return new R(true, articleService.getByAble(enable));
    }

    //首页最新文章
    @GetMapping("/home/new")
    public R getTen() {
        return new R(true, articleService.getTen());
    }

    //热门文章
    @GetMapping("/home/comment")
    public R getTenCommentArticle(Integer size) {
        return new R(true, articleService.getTenCommentArticle(size).get("list"));
    }

    //首页随机推荐
    @GetMapping("/home/random")
    public R getTenRandomArticle() {
        return new R(true, articleService.getTenRandomArticle());
    }

    //管理员推荐
    @GetMapping("/home/mend")
    public R getTenMendArticle() {
        return new R(true, articleService.getTenMendArticle());
    }
}
