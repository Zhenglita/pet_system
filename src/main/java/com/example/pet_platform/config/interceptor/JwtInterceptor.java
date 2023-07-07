package com.example.pet_platform.config.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.pet_platform.controller.DTO.UserDTO;
import com.example.pet_platform.controller.util.ServiceException;
import com.example.pet_platform.entity.User;
import com.example.pet_platform.entity.UserHolder;
import com.example.pet_platform.service.UserService;
import com.example.pet_platform.util.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class JwtInterceptor implements HandlerInterceptor {


    private StringRedisTemplate stringRedisTemplate;
    public JwtInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate=stringRedisTemplate;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 执行认证
        if (StrUtil.isBlank(token)) {

            throw new ServiceException("401", "无token，请重新登录");
        }
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries("login:user:" + token);

        if (userMap.isEmpty()){
            throw new ServiceException("401", "该用户不存在");
        }
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        UserHolder.saveUser(userDTO);
        stringRedisTemplate.expire("login:user:" + token,30, TimeUnit.MINUTES);
        // 获取 token 中的 user id
//        String userId;
//        try {
//            userId = JWT.decode(token).getAudience().get(0);
//        } catch (JWTDecodeException j) {
//            throw new ServiceException("401", "token验证失败，请重新登录");
//        }
        // 用户密码加签验证 token
//        try {
//            JWTUtils.verify(token);
//        } catch (JWTVerificationException e) {
//            throw new ServiceException("401", "token验证失败，请重新登录");
//        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
            UserHolder.removeUser();
    }
}
