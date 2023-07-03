package com.example.pet_platform.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.Cart;
import com.example.pet_platform.entity.User;
import com.example.pet_platform.service.BooksService;
import com.example.pet_platform.service.CartService;
import com.example.pet_platform.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/carts")

public class CartController {
    @Resource
    private CartService cartService;

    @GetMapping("/s/{booksid}/{uid}")
    private R getStates(@PathVariable Integer booksid, @PathVariable Integer uid) {

            return cartService.getStates(booksid,uid);
    }


    @GetMapping
    private R getAll() {
        return new R(true, cartService.list());
    }

    @PostMapping
    private R add(@RequestBody Cart cart) {

        return new R(true, cartService.add(cart));
    }

    @GetMapping("/update/{booksid}/{uid}/{num}")
    public R update(@PathVariable Integer booksid, @PathVariable Integer uid, @PathVariable Integer num) {

        return cartService.update(booksid,uid,num);
    }

    @GetMapping("/user/{uid}")
    private R getByUid(@PathVariable Integer uid) {
        return cartService.getByUid(uid);
    }

    @DeleteMapping("/{uid}/{bid}")
    public R del(@PathVariable Integer uid, @PathVariable Integer bid) {
       return cartService.del(uid,bid);

    }
}
