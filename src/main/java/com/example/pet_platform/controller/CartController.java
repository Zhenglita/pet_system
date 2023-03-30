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
    @Resource
    private BooksService booksService;

    @GetMapping("/s/{booksid}/{uid}")
    private R getStates(@PathVariable Integer booksid, @PathVariable Integer uid){

        LambdaQueryWrapper<Books> lqw_book=new LambdaQueryWrapper<>();
        lqw_book.eq(true,Books::getId,booksid);
        Books book = booksService.getOne(lqw_book);

        LambdaQueryWrapper<Cart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(true,Cart::getBooks_id,booksid);
        lqw.eq(true,Cart::getUser_id,uid);
        Cart one = cartService.getOne(lqw);
            if (one!=null){
                return new R(false,book,one.getNum());
            }else
                return new R(true,book);
}



    @GetMapping
    private R getAll(){
        return new R(true,cartService.list());
    }
    @PostMapping
    private  R add(@RequestBody Cart cart){
        LambdaQueryWrapper<Books> lqw=new LambdaQueryWrapper<>();
        lqw.eq(true,Books::getId,cart.getBooks_id());
        Books books = booksService.getOne(lqw);
        books.setQuantity(books.getQuantity()-cart.getNum());
        booksService.updateById(books);
        return new R(true,cartService.save(cart));
    }
    @GetMapping("/update/{booksid}/{uid}/{num}")
    public R update(@PathVariable Integer booksid, @PathVariable Integer uid, @PathVariable Integer num){
        LambdaQueryWrapper<Cart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(true,Cart::getBooks_id,booksid);
        lqw.eq(true,Cart::getUser_id,uid);
        Cart one = cartService.getOne(lqw);
        int i = num - Integer.parseInt(one.getNum().toString());
        LambdaQueryWrapper<Books> lqw1=new LambdaQueryWrapper<>();
        lqw1.eq(true,Books::getId,booksid);
        Books books = booksService.getOne(lqw1);
        int booksQuantity = Integer.parseInt(books.getQuantity().toString()) - i;
        if (booksQuantity<0){
         return new R(false,"库存不足");
        }
        books.setQuantity(booksQuantity);
        booksService.updateById(books);
        one.setNum(num);
        cartService.updateById(one);
        return new R(true);
    }
    @GetMapping("/user/{uid}")
    private R getByUid(@PathVariable Integer uid){
        LambdaQueryWrapper<Cart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(true,Cart::getUser_id,uid);
        List<Cart> list = cartService.list(lqw);
        for (int i=0;i<list.size();i++){
            Books byId = booksService.getById(list.get(i).getBooks_id());
            list.get(i).setPrice(byId.getPrice());
            list.get(i).setBookname(byId.getBookname());
            list.get(i).setImage(byId.getImage());
            System.err.println(list.get(i).getNum());
        }
        return new R(true,list);
    }
    @DeleteMapping("/{uid}/{bid}")
    public R del(@PathVariable Integer uid,@PathVariable Integer bid){
        LambdaQueryWrapper<Cart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(true,Cart::getUser_id,uid);
        lqw.eq(true,Cart::getBooks_id,bid);
        Cart one = cartService.getOne(lqw);
        if (one!=null){
            LambdaQueryWrapper<Books> lqw_books=new LambdaQueryWrapper<>();
            lqw_books.eq(true,Books::getId,bid);
            Books books = booksService.getOne(lqw_books);
            books.setQuantity(books.getQuantity()+one.getNum());
            booksService.updateById(books);
            cartService.removeById(one.getId());
            return new R(true);
        }else {
            return new R(false);
        }

    }
}
