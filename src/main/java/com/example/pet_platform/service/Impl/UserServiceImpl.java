package com.example.pet_platform.service.Impl;

import cn.hutool.core.bean.BeanUtil;
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
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;


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
            String jsonString = JSONObject.toJSONString(one);
            System.err.println(jsonString);
            BeanUtil.copyProperties(one, userDTO, true);//将查到的值传给userDTO，即本来userdto只有账号密码，现在user的内容它都有
            // 设置token
            String token = JWTUtils.getToken(one.getUid().toString(), one.getUsername());
            userDTO.setToken(token);
            return userDTO;
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
            String jsonString = JSONObject.toJSONString(one);
            System.err.println(jsonString);
            BeanUtil.copyProperties(one, userDTO, true);//将查到的值传给userDTO，即本来userdto只有账号密码，现在user的内容它都有
            // 设置token
            String token = JWTUtils.getToken(one.getUid().toString(), one.getUsername());
            userDTO.setToken(token);
            return userDTO;
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
