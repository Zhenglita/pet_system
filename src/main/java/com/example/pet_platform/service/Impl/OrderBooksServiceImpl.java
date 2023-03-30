package com.example.pet_platform.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.entity.OrderBooks;
import com.example.pet_platform.mapper.OrderBooksMapper;
import com.example.pet_platform.service.OrderBooksService;
import com.example.pet_platform.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderBooksServiceImpl extends ServiceImpl<OrderBooksMapper, OrderBooks> implements OrderBooksService {
}
