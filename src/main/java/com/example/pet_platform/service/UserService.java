package com.example.pet_platform.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.example.pet_platform.controller.DTO.UserDTO;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


public interface UserService extends IService<User> {
    R saveOwn(User user);
    R getChangeUser( Integer uid);
    UserDTO login(UserDTO userDTO);
    UserDTO admin_login(UserDTO userDTO);
    IPage<User> getPage(int currentPage, int pageSize, User user);
}
