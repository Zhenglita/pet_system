package com.example.pet_platform.service.Impl;


//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.example.pet_platform.entity.Follow;
//
//import com.example.pet_platform.mapper.FollowMapper;
//
//import com.example.pet_platform.service.FollowService;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Article;
import com.example.pet_platform.entity.Follow;
import com.example.pet_platform.entity.User;
import com.example.pet_platform.mapper.ArticleMapper;
import com.example.pet_platform.mapper.FollowMapper;
import com.example.pet_platform.mapper.UserMapper;
import com.example.pet_platform.service.FollowService;
import com.example.pet_platform.util.JWTUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    @Resource
    private FollowMapper followMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public R add(Integer uid, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Follow> qw = new QueryWrapper<>();
        qw.eq("b_uid", uid);
        qw.eq("f_uid", Integer.parseInt(userid));
        List<Follow> list = followMapper.selectList(qw);
        if (list.size() > 0) {
            followMapper.delete(qw);
            return new R(false, "取消关注", "未关注");
        }
        Follow follow = new Follow();
        follow.setB_uid(uid);
        follow.setF_uid(Integer.parseInt(userid));
        followMapper.insert(follow);
        return new R(true, "关注成功", "已关注");
    }

    @Override
    public R getAll(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Follow> qw = new QueryWrapper<>();
        qw.eq("f_uid", Integer.parseInt(userid));
        List<Follow> list =followMapper.selectList(qw);
        for (Follow follow : list) {
            QueryWrapper<User> qw1 = new QueryWrapper<>();
            qw1.eq("uid", follow.getB_uid());
            User one =userMapper.selectOne(qw1);
            follow.setUser(one);
        }
        List<User> collect = list.stream().map(Follow::getUser).collect(Collectors.toList());
        return new R(true, collect);
    }

    @Override
    public R getUserArticle(Integer uid) {
        QueryWrapper<Article> qw = new QueryWrapper<>();
        qw.eq("uid", uid);
        List<Article> list =articleMapper.selectList(qw);
        return new R(true, list);
    }
}
