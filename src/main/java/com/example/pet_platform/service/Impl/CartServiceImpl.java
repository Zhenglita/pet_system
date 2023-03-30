package com.example.pet_platform.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.Cart;
import com.example.pet_platform.mapper.CartMapper;
import com.example.pet_platform.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper,Cart> implements CartService {
}
