package com.example.pet_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Follow;
import com.example.pet_platform.entity.Message;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

public interface FollowService extends IService<Follow> {
    R add( Integer uid, HttpServletRequest request);
    R getAll(HttpServletRequest request);
    R getUserArticle( Integer uid);
}
