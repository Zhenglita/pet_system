package com.example.pet_platform.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.DTO.SecKillVoucherDTO;
import com.example.pet_platform.controller.VO.VoucherVO;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.*;
import com.example.pet_platform.mapper.*;
import com.example.pet_platform.service.OrderService;
import com.example.pet_platform.service.SeckillVoucherService;
import com.example.pet_platform.service.VoucherService;
import com.example.pet_platform.util.JWTUtils;
import com.example.pet_platform.util.SimpleRedisLock;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    @Resource
    private SeckillVoucherService seckillVoucherService;
    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;
    @Resource
    private VoucherMapper voucherMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
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

    @Transactional
    @Override
    public R addVoucher(Integer id, HttpServletRequest request)  {
        SecKillVoucherDTO secKillVoucherDTO = voucherMapper.selectAllById(id);
        if (secKillVoucherDTO.getType().equals(true)){
//            if (secKillVoucherDTO.getBegin_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isAfter((LocalDateTime.now()))){
//                return new R(false,"秒杀尚未开始");
//            }
//            if (secKillVoucherDTO.getEnd_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isAfter((LocalDateTime.now()))){
//                return new R(false,"秒杀已经结束了");
//            }
            if(secKillVoucherDTO.getStock()<=0){
                return new R(false,"库存不足");
            }
        }
        //秒杀券  先判断是否为秒杀请求-> 秒杀券库存>0->该用户是否购买过该券
        if(secKillVoucherDTO.getType().equals(true)&&secKillVoucherDTO.getStock()>0){
            String token = request.getHeader("Authorization");
            DecodedJWT verify = JWTUtils.verify(token);
            String userid = verify.getClaim("userid").asString();
            secKillVoucherDTO.setUser_id(Integer.parseInt(userid));
            SimpleRedisLock lock = new SimpleRedisLock("order:" + userid, stringRedisTemplate);
            boolean isLock = lock.tryLock(1200);
            if (!isLock){
                //获取锁失败
                return new R(false);
            }
            try{
                OrderService proxy=(OrderService) AopContext.currentProxy();
                return proxy.createKill(secKillVoucherDTO);
            }finally {
                lock.unlock();
            }
        }
        //普通券 且该用户
        else if (secKillVoucherDTO.getType().equals(false)){
            Date date = new Date();
            Order order =new Order();
            UserVoucher userVoucher = voucherMapper.getUserVoucher(secKillVoucherDTO);
            if (userVoucher==null){
                secKillVoucherDTO.setNum(1);
                voucherMapper.saveVoucher(secKillVoucherDTO);
                //保存订单
                order.setTime(DateUtil.formatDateTime(date));
                order.setTotal_price(secKillVoucherDTO.getPay_value());
                order.setUser_id(secKillVoucherDTO.getUser_id());
                order.setNo(DateUtil.format(date, "yyyyMHdd") + System.currentTimeMillis());
                order.setState("代金券交易");
                order.setName(secKillVoucherDTO.getTitle());
                orderMapper.insert(order);
                return new R(true,order);
            }else {
                secKillVoucherDTO.setNum(userVoucher.getNum()+1);
                voucherMapper.updatNum(secKillVoucherDTO);
                //保存订单
                order.setTime(DateUtil.formatDateTime(date));
                order.setTotal_price(secKillVoucherDTO.getPay_value());
                order.setUser_id(secKillVoucherDTO.getUser_id());
                order.setNo(DateUtil.format(date, "yyyyMHdd") + System.currentTimeMillis());
                order.setState("代金券交易");
                order.setName(secKillVoucherDTO.getTitle());
                orderMapper.insert(order);
                return new R(true,order);
            }


        }
        return new R(false);
    }
    @Transactional
    public  R createKill(SecKillVoucherDTO secKillVoucherDTO){
        UserVoucher userVoucher = voucherMapper.getUserVoucher(secKillVoucherDTO);
        if (userVoucher!=null){
            return new R(false,"一人只能买一单");
        }
        //修改库存
        boolean success=seckillVoucherService.update().setSql("stock=stock-1").eq("voucher_id",secKillVoucherDTO.getId()).gt("stock",0).update();
//            seckillVoucherMapper.updataKill(secKillVoucherDTO);
        if (!success){
            return new R(false,"库存不足，扣减失败");
        }
        secKillVoucherDTO.setNum(1);
        //保存到用户代金券
        voucherMapper.saveVoucher(secKillVoucherDTO);
        //保存订单
        Date date = new Date();
        Order order =new Order();
        order.setTime(DateUtil.formatDateTime(date));
        order.setTotal_price(secKillVoucherDTO.getPay_value());
        order.setNo(DateUtil.format(date, "yyyyMHdd") + System.currentTimeMillis());
        order.setState("代金券交易");
        order.setUser_id(secKillVoucherDTO.getUser_id());
        order.setName(secKillVoucherDTO.getTitle());
        orderMapper.insert(order);
        return new R(true,order);
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
