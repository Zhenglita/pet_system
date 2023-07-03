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

    @GetMapping("/add/{uid}")
    private R add(@PathVariable Integer uid, HttpServletRequest request) {
      return  followService.add(uid,request);
    }

    @GetMapping
    private R getAll(HttpServletRequest request) {
       return followService.getAll(request);
    }

    @GetMapping("/{uid}")
    private R getUserArticle(@PathVariable Integer uid) {
       return followService.getUserArticle(uid);
    }
}
