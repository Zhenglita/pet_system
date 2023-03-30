package com.example.pet_platform.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@MapperScan("com.example.pet_platform.dao")
public class MPConfig {
    @Bean
//    配置mp的拦截器，为了在sql后面加语句
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
//        套壳
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//       加入分页的拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
