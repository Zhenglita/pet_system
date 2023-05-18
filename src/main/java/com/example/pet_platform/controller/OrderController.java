package com.example.pet_platform.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.controller.util.TokenUtils;
import com.example.pet_platform.entity.*;
import com.example.pet_platform.mapper.OrderMapper;
import com.example.pet_platform.mapper.UserMapper;
import com.example.pet_platform.service.CartService;
import com.example.pet_platform.service.OrderBooksService;
import com.example.pet_platform.service.OrderService;
import com.example.pet_platform.service.UserService;
//import com.sun.org.apache.xpath.internal.operations.Or;

import com.example.pet_platform.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping
    private R getall(Order orders) {
      return orderService.getall(orders);
    }

    @PostMapping
    private R add(@RequestBody Order order, HttpServletRequest request) {
       return orderService.add(order,request);
    }

    @GetMapping("/{uid}")
    private R getAll(@PathVariable Integer uid) {
       return orderService.getAll(uid);
    }

    @DeleteMapping("/{id}")
    private R del(@PathVariable Integer id) {
       return orderService.del(id);
    }

    @PostMapping("/del")
    private R Del(@RequestBody Order order) {
       return orderService.Del(order);
    }

    @GetMapping("/select/{name}/{uid}")
    private R getByname(@PathVariable String name, @PathVariable Integer uid) {
       return orderService.getByname(name, uid);
    }

    @PutMapping("/{id}")
    private R updata(@PathVariable Integer id) {
       return orderService.updata(id);
    }

    @PutMapping("/del/{id}")
    private R delupdata(@PathVariable Integer id) {
       return orderService.delupdata(id);
    }

}
