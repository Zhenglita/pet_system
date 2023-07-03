package com.example.pet_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Comment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface CommentService extends IService<Comment> {
    R getSensitiveAll();
    R add(Comment comment);
    R getByIdOwn(Integer foreginId);
    R deleteByIdOwn(Integer id);
    R getByUserId(Integer uid);
    R getTenNewComment();
    R getTheArticleNewComment( Integer id);
}
