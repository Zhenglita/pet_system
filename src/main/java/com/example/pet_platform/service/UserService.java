package com.example.pet_platform.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.example.pet_platform.controller.DTO.UserDTO;
import com.example.pet_platform.entity.User;

import java.util.Map;


public interface UserService extends IService<User> {
    UserDTO login(UserDTO userDTO);
    UserDTO admin_login(UserDTO userDTO);
    IPage<User> getPage(int currentPage, int pageSize, User user);
}
