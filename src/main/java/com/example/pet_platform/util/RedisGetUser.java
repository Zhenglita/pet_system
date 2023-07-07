package com.example.pet_platform.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.pet_platform.controller.DTO.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;


public class RedisGetUser {

    private  StringRedisTemplate stringRedisTemplate;
    public RedisGetUser(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate=stringRedisTemplate;
    }
    public static String getUserid(StringRedisTemplate stringRedisTemplate, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries("login:user:" + token);
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        String userid = userDTO.getUid();
        return userid;
    }
}
