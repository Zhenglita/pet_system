package com.example.pet_platform.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.DTO.UserDTO;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.controller.util.TokenUtils;
import com.example.pet_platform.mapper.UserMapper;
import com.example.pet_platform.entity.User;
import com.example.pet_platform.service.UserService;
import com.example.pet_platform.util.JWTUtils;
import io.netty.util.internal.StringUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public R saveOwn(User user) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(true, User::getUsername, user.getUsername());
        List<User> list = userMapper.selectList(lqw);
        if (list.size() > 0) {
            return new R(false);
        } else {
            user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
            return new R(true, userMapper.insert(user));
        }
    }

    @Override
    public R getChangeUser(Integer uid) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(true, User::getUid, uid);
        User one = userMapper.selectOne(lambdaQueryWrapper);
        return new R(true, one.getRole());
    }

    @Override
    public UserDTO login(UserDTO userDTO) {
        QueryWrapper<User> qw=new QueryWrapper<>();
        qw.eq("username",userDTO.getUsername());
        qw.eq("password",userDTO.getPassword());
        User one = getOne(qw);
//        User one = getOne(qw);
        if (one!=null){
            UserDTO resultUser = BeanUtil.copyProperties(one, UserDTO.class);
            // 设置token
            String token = UUID.randomUUID().toString(true);
//            JWTUtils.getToken(one.getUid().toString(), one.getUsername());
            resultUser.setToken(token);
            if (StringUtils.isEmpty(one.getPlace())){
                resultUser.setPlace("");
            }
            String tokenKey="login:user:"+token;
            Map<String, Object> userMap = BeanUtil.beanToMap(resultUser);
            stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
            stringRedisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
            return resultUser;
        }else{
            return userDTO;
        }


    }

    @Override
    public UserDTO admin_login(UserDTO userDTO) {
        QueryWrapper<User> qw=new QueryWrapper<>();
        qw.eq("username",userDTO.getUsername());
        qw.eq("password",userDTO.getPassword());
        User one = getOne(qw);
        if (one!=null&& Objects.equals(one.getRole(), "ROLE_ADMIN")){
            UserDTO resultUser = BeanUtil.copyProperties(one, UserDTO.class);//将查到的值传给userDTO，即本来userdto只有账号密码，现在user的内容它都有
            // 设置token
            String token = UUID.randomUUID().toString(true);
            resultUser.setToken(token);
            String tokenKey="login:user:"+token;
            Map<String, Object> userMap = BeanUtil.beanToMap(resultUser);
            stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
            stringRedisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
            return resultUser;
        }else{
            return userDTO;
        }

    }

    @Override
    public IPage<User> getPage(int currentPage, int pageSize, User user) {
        LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(user.getUsername()),User::getUsername,user.getUsername());
        lqw.like(Strings.isNotEmpty(user.getRole()),User::getRole,user.getRole());
        IPage<User> page=new Page<>(currentPage,pageSize);
        userMapper.selectPage(page,lqw);
        return page;
    }
}
