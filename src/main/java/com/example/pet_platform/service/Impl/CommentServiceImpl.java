package com.example.pet_platform.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.entity.Comment;
import com.example.pet_platform.mapper.CommentMapper;
import com.example.pet_platform.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
