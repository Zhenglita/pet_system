package com.example.pet_platform.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Article;
import com.example.pet_platform.entity.Comment;
import com.example.pet_platform.entity.Follow;
import com.example.pet_platform.entity.User;
import com.example.pet_platform.service.ArticleService;
import com.example.pet_platform.service.FollowService;
import com.example.pet_platform.service.UserService;
import com.example.pet_platform.util.JWTUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/follows")
public class FollowController {
    @Resource
    private FollowService followService;
    @Resource
    private ArticleService articleService;
    @Resource
    private UserService userService;
    @GetMapping("/add/{uid}")
    private R add(@PathVariable Integer uid, HttpServletRequest request){
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Follow> qw=new QueryWrapper<>();
        qw.eq("b_uid",uid);
        qw.eq("f_uid",Integer.parseInt(userid));
        List<Follow> list = followService.list(qw);
        if (list.size()>0){
            boolean remove = followService.remove(qw);
            return new R(false,"取消关注","未关注");
        }
        Follow follow = new Follow();
        follow.setB_uid(uid);
        follow.setF_uid(Integer.parseInt(userid));
        boolean save = followService.save(follow);
        return new R(true,"关注成功","已关注");
    }
    @GetMapping
    private R getAll(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Follow> qw=new QueryWrapper<>();
        qw.eq("f_uid",Integer.parseInt(userid));
        List<Follow> list = followService.list(qw);
        for(Follow follow:list){
            QueryWrapper<User> qw1=new QueryWrapper<>();
            qw1.eq("uid",follow.getB_uid());
            User one = userService.getOne(qw1);
            follow.setUser(one);
        }
        List<User> collect = list.stream().map(Follow::getUser).collect(Collectors.toList());
        return new R(true,collect);
    }
    @GetMapping("/{uid}")
    private R getUserArticle(@PathVariable Integer uid){
        QueryWrapper<Article> qw=new QueryWrapper<>();
        qw.eq("uid",uid);
        List<Article> list = articleService.list(qw);
        return new R(true,list);
    }
}
