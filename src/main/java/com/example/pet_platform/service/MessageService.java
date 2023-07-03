package com.example.pet_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Comment;
import com.example.pet_platform.entity.Message;
import com.example.pet_platform.entity.OrderBooks;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

public interface MessageService extends IService<Message> {
    R getAll(HttpServletRequest request);
    R delAll(HttpServletRequest request,  String username);
    R addAll(HttpServletRequest request,  String username);
    R addAll(HttpServletRequest request,  Comment comment);
    R addAll(HttpServletRequest request);
    DeferredResult<Boolean> sendMessage(HttpServletRequest request);
    R downMessage();
    R addRole(HttpServletRequest request,  String messages);
    R getRole();
}
