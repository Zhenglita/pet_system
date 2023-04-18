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
import com.sun.org.apache.xpath.internal.operations.Or;
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
  private OrderBooksService orderBooksService;
  @Resource
  private CartService cartService;
  @Resource
  private UserService userService;
  @Resource
  private OrderService orderService;
  @GetMapping
  private R getall(Order orders){
   LambdaQueryWrapper<Order> lqw1=new LambdaQueryWrapper<>();
   lqw1.eq(StrUtil.isNotEmpty(orders.getState()),Order::getState,orders.getState());
//    QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
//    queryWrapper.eq("state",orders.getState());
    List<Order> list = orderService.list(lqw1);
    for (Order order:list)
    {
      LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
      lambdaQueryWrapper.eq(true,User::getUid,order.getUser_id());
      User one = userService.getOne(lambdaQueryWrapper);
      order.setUsers(one);
      LambdaQueryWrapper<OrderBooks> lqw=new LambdaQueryWrapper<>();
      lqw.eq(true,OrderBooks::getOrder_id,order.getId());
      List<OrderBooks> orderBooks = orderBooksService.list(lqw);
      order.setOrderBooks(orderBooks);
    }
      return new R(true,list);
  }
  @PostMapping
  private R add(@RequestBody Order order, HttpServletRequest request){
    String token = request.getHeader("Authorization");
    DecodedJWT verify = JWTUtils.verify(token);
    String userid = verify.getClaim("userid").asString();
    QueryWrapper<User> queryWrapper=new QueryWrapper<>();
    queryWrapper.eq("uid",Integer.parseInt(userid));
    User one = userService.getOne(queryWrapper);
    if (order.getId()==null){
      Date date=new Date();
      order.setTime(DateUtil.formatDateTime(date));
      order.setTotal_price(order.getTotal_price());
      order.setUser_id(order.getUser_id());
      order.setNo(DateUtil.format(date,"yyyyMHdd")+System.currentTimeMillis());
      order.setUser_id(order.getUser_id());
      order.setPlace(one.getPlace());
      order.setState("待发货");
    orderService.save(order);
    List<Cart> carts=order.getCarts();
    for (Cart cart:carts){
      OrderBooks orderBooks=new OrderBooks();
      orderBooks.setOrder_id(order.getId());
      orderBooks.setBooks_id(cart.getBooks_id());
      orderBooks.setNum(cart.getNum());
      orderBooksService.save(orderBooks);
      cartService.removeById(cart.getId());
    }
    }else{
      orderService.updateById(order);
    }
    return new R(true,order);
  }
  @GetMapping("/{uid}")
  private R getAll(@PathVariable Integer uid){
    LambdaQueryWrapper<Order> lqw1=new LambdaQueryWrapper<>();
    lqw1.eq(true,Order::getUser_id,uid);
    List<Order> list = orderService.list(lqw1);
    for (Order order:list){
      LambdaQueryWrapper<OrderBooks> lqw=new LambdaQueryWrapper<>();
      lqw.eq(true,OrderBooks::getOrder_id,order.getId());
      List<OrderBooks> orderBooks = orderBooksService.list(lqw);
      order.setOrderBooks(orderBooks);
    }
    return new R(true,list);
  }
  @DeleteMapping("/{id}")
  private R del(@PathVariable Integer id){
    boolean b = orderService.removeById(id);
    LambdaQueryWrapper<OrderBooks> lambdaQueryWrapper=new LambdaQueryWrapper<>();
    lambdaQueryWrapper.eq(true,OrderBooks::getOrder_id,id);
    boolean remove = orderBooksService.remove(lambdaQueryWrapper);

   return new R(b&&remove);}
  @PostMapping("/del")
  private R Del(@RequestBody Order order){
    List<Order> orders = order.getOrders();
    for (Order order1:orders){
      orderService.removeById(order1.getId());
      LambdaQueryWrapper<OrderBooks> lambdaQueryWrapper=new LambdaQueryWrapper<>();
      lambdaQueryWrapper.eq(true,OrderBooks::getOrder_id,order1.getId());
      orderBooksService.remove(lambdaQueryWrapper);
    }

    return new R(true);}
  @GetMapping("/select/{name}/{uid}")
  private R getByname(@PathVariable String name,@PathVariable Integer uid){
    LambdaQueryWrapper<Order> lqw=new LambdaQueryWrapper<>();
    lqw.like(true,Order::getName,name);
    lqw.like(true,Order::getUser_id,uid);
    List<Order> list = orderService.list(lqw);
    return new R(true,list);
  }
  @PutMapping("/{id}")
  private R updata(@PathVariable Integer id){
    Order byId = orderService.getById(id);
    if (byId!=null){
      byId.setState("已发货");
      orderService.updateById(byId);
      return new R(true);
    }else {
      return new R(false);
    }

  }
  @PutMapping("/del/{id}")
  private R delupdata(@PathVariable Integer id){
    Order byId = orderService.getById(id);
    if (byId!=null){
      byId.setState("交易被取消");
      orderService.updateById(byId);
      return new R(true);
    }else {
      return new R(false);
    }

  }

}
