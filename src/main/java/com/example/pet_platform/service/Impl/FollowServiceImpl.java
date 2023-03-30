package com.example.pet_platform.service.Impl;


//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.example.pet_platform.entity.Follow;
//
//import com.example.pet_platform.mapper.FollowMapper;
//
//import com.example.pet_platform.service.FollowService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.entity.Follow;
import com.example.pet_platform.mapper.FollowMapper;
import com.example.pet_platform.service.FollowService;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
}
