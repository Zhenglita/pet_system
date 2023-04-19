package com.example.pet_platform.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.pet_platform.component.WebSocketServer;
import com.example.pet_platform.controller.DTO.UserDTO;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.User;
import com.example.pet_platform.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/users")

public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/all")
    public R getAll(){
        return   new R(true,userService.list());
    }
    @DeleteMapping("{uid}")
    public R delete(@PathVariable Integer uid){
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(true,User::getUid,uid);

        return  new R(true,userService.remove(lambdaQueryWrapper));
    }

    @PostMapping
    public R save(@RequestBody User user){
        LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<>();
        lqw.eq(true,User::getUsername,user.getUsername());
        List<User> list = userService.list(lqw);
        if (list.size()>0){
            return new R(false);
        }
        else {
            user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
            return new R(true,userService.save(user));
        }
    }
    @PutMapping
    public R update(@RequestBody User user){
        System.out.println(user.getUsername());

        return new R(true,userService.updateById(user));
    }
    @GetMapping("/s/{uid}")
    public R getById(@PathVariable Integer uid){
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(true,User::getUid,uid);
        return  new R(true,userService.getOne(lambdaQueryWrapper));
    }
    @GetMapping("/remove/{username}")
    public R closeChat(@PathVariable String username){
        WebSocketServer.sessionMap.remove(username);
        return  new R(true);
    }
//    @GetMapping
//    public  R getOneUser(User user){
//        System.out.println(user.getUsername());
//        LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<>();
//        lqw.eq(Strings.isNotEmpty(user.getUsername()),User::getUsername,user.getUsername());
//        return new R(true,userService.getOne(lqw));
//    }
        @GetMapping("/admin/login")
        public  R getAdminUser(UserDTO userDTO){
            UserDTO dto = userService.admin_login(userDTO);
            if (dto.getToken()!=null){
                String jsonString = JSONObject.toJSONString(dto);
                return new R(true,jsonString,userDTO.getToken());
            }else {
                return new R(false);
                }

            }

        @GetMapping
        public  R getOneUser(UserDTO userDTO){
            UserDTO dto = userService.login(userDTO);
            if (dto.getToken()!=null){
                String jsonString = JSONObject.toJSONString(dto);
//            System.err.println(jsonString);
                return new R(true,jsonString,userDTO.getToken());
            }else {
                return new R(false);
            }

            }

    @GetMapping("/{currentPage}/{pageSize}")
    public R getPage(@PathVariable int currentPage, @PathVariable int pageSize, User user){
        IPage<User> page=userService.getPage(currentPage,pageSize,user);
        if (currentPage>page.getPages()){
            page=userService.getPage((int)page.getPages(),pageSize,user);
        }
        return new R(true,page);
    }
    @GetMapping("/change/{uid}")
    public R getChangeUser(@PathVariable Integer uid){
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(true,User::getUid,uid);
        User one = userService.getOne(lambdaQueryWrapper);
        return  new R(true,one.getRole());
    }
}