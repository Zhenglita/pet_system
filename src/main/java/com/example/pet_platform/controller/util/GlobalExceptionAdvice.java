package com.example.pet_platform.controller.util;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//作为springmvc的异常处理器
@RestControllerAdvice
public class GlobalExceptionAdvice {
    //拦截所有的异常信息
    @ExceptionHandler(ServiceException.class)
    public R doException(ServiceException se){
        //记录日志，通知运维，通知开发
        se.printStackTrace();
        return new R(se.getCode(),se.getMessage());

    }
}
