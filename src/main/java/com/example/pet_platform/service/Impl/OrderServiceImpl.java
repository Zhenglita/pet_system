package com.example.pet_platform.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Cart;
import com.example.pet_platform.entity.OrderBooks;
import com.example.pet_platform.entity.User;
import com.example.pet_platform.mapper.CartMapper;
import com.example.pet_platform.mapper.OrderBooksMapper;
import com.example.pet_platform.mapper.OrderMapper;
import com.example.pet_platform.entity.Order;
import com.example.pet_platform.mapper.UserMapper;
import com.example.pet_platform.service.OrderService;
import com.example.pet_platform.util.JWTUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Resource
    private OrderBooksMapper orderBooksMapper;
    @Resource
    private  OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CartMapper cartMapper;

    @Override
    public R getall(Order orders) {
        LambdaQueryWrapper<Order> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(StrUtil.isNotEmpty(orders.getState()), Order::getState, orders.getState());
        lqw1.eq(true, Order::getEnable, false);
//    QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
//    queryWrapper.eq("state",orders.getState());
        List<Order> list = orderMapper.selectList(lqw1);
        for (Order order : list) {
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(true, User::getUid, order.getUser_id());
            User one = userMapper.selectOne(lambdaQueryWrapper);
            order.setUsers(one);
            LambdaQueryWrapper<OrderBooks> lqw = new LambdaQueryWrapper<>();
            lqw.eq(true, OrderBooks::getOrder_id, order.getId());
            List<OrderBooks> orderBooks = orderBooksMapper.selectList(lqw);
            order.setOrderBooks(orderBooks);
        }
        return new R(true, list);
    }

    @Override
    public R add(Order order, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", Integer.parseInt(userid));
        User one = userMapper.selectOne(queryWrapper);
        if (order.getId() == null) {
            Date date = new Date();
            order.setTime(DateUtil.formatDateTime(date));
            order.setTotal_price(order.getTotal_price());
            order.setUser_id(order.getUser_id());
            order.setNo(DateUtil.format(date, "yyyyMHdd") + System.currentTimeMillis());
            order.setUser_id(order.getUser_id());
            order.setPlace(one.getPlace());
            order.setState("待发货");
            orderMapper.insert(order);
            List<Cart> carts = order.getCarts();
            for (Cart cart : carts) {
                OrderBooks orderBooks = new OrderBooks();
                orderBooks.setOrder_id(order.getId());
                orderBooks.setBooks_id(cart.getBooks_id());
                orderBooks.setNum(cart.getNum());
                orderBooksMapper.insert(orderBooks);
                cartMapper.deleteById(cart.getId());

            }
        } else {
            orderMapper.updateById(order);

        }
        return new R(true, order);
    }

    @Override
    public R getAll(Integer uid) {
        LambdaQueryWrapper<Order> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(true, Order::getUser_id, uid);
        lqw1.eq(true, Order::getEnable, false);
        List<Order> list = orderMapper.selectList(lqw1);
        for (Order order : list) {
            LambdaQueryWrapper<OrderBooks> lqw = new LambdaQueryWrapper<>();
            lqw.eq(true, OrderBooks::getOrder_id, order.getId());
            List<OrderBooks> orderBooks = orderBooksMapper.selectList(lqw);
            order.setOrderBooks(orderBooks);
        }
        return new R(true, list);
    }

    @Override
    public R del(Integer id) {
        Order byId = orderMapper.selectById(id);
        byId.setEnable(true);
        orderMapper.updateById(byId);
        LambdaQueryWrapper<OrderBooks> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(true, OrderBooks::getOrder_id, id);
        List<OrderBooks> list =orderBooksMapper.selectList(lambdaQueryWrapper);
        for (OrderBooks orderBooks : list) {
            orderBooks.setEnable(true);
            orderBooksMapper.updateById(orderBooks);
        }

        return new R(true);
    }

    @Override
    public R Del(Order order) {
        List<Order> orders = order.getOrders();
        for (Order order1 : orders) {
            Order byId = orderMapper.selectById(order1.getId());
            byId.setEnable(true);
            orderMapper.updateById(byId);
            LambdaQueryWrapper<OrderBooks> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(true, OrderBooks::getOrder_id, order1.getId());
            List<OrderBooks> list =orderBooksMapper.selectList(lambdaQueryWrapper);
            for (OrderBooks orderBooks : list) {
                orderBooks.setEnable(true);
                orderBooksMapper.updateById(orderBooks);
            }
        }

        return new R(true);
    }

    @Override
    public R getByname(String name, Integer uid) {
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.like(true, Order::getName, name);
        lqw.like(true, Order::getUser_id, uid);
        List<Order> list = orderMapper.selectList(lqw);
        return new R(true, list);
    }

    @Override
    public R updata(Integer id) {
        Order byId = orderMapper.selectById(id);;
        if (byId != null) {
            byId.setState("已发货");
            orderMapper.updateById(byId);
            return new R(true);
        } else {
            return new R(false);
        }

    }

    @Override
    public R delupdata(Integer id) {
        Order byId = orderMapper.selectById(id);
        if (byId != null) {
            byId.setState("交易被取消");
            orderMapper.updateById(byId);
            return new R(true);
        } else {
            return new R(false);
        }

    }
}
