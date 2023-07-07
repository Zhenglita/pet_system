package com.example.pet_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Cart;
import org.springframework.web.bind.annotation.PathVariable;

import java.text.ParseException;

public interface CartService extends IService<Cart> {
    int add(Cart cart);
    R getStates( Integer booksid,  Integer uid) throws ParseException;
    R update(Integer booksid, Integer uid, Integer num);
    R getByUid(Integer uid);
    R del(Integer uid,  Integer bid);
}
