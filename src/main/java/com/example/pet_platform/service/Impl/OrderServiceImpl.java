package com.example.pet_platform.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.mapper.OrderMapper;
import com.example.pet_platform.entity.Order;
import com.example.pet_platform.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
