package com.example.pet_platform.config;


import com.example.pet_platform.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor(stringRedisTemplate))
                .excludePathPatterns("/users/**","/articles/**","/comments/**","/books/**","/alipay/**","/upload");
//                .addPathPatterns("/**")  // 拦截所有请求，通过判断token是否合法来决定是否需要登录
//                .excludePathPatterns("/users/**",,"/articles/**","/alipay/**","/imserver/**",);  // 放行静态文件
    }

//    @Bean
//    public JwtInterceptor jwtInterceptor() {
//        return new JwtInterceptor();
//    }

}
