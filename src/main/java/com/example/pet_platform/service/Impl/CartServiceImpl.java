package com.example.pet_platform.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.Cart;
import com.example.pet_platform.mapper.BooksMapper;
import com.example.pet_platform.mapper.CartMapper;
import com.example.pet_platform.service.CartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper,Cart> implements CartService {
    @Resource
    private CartMapper cartMapper;
    @Resource
    private BooksMapper booksMapper;
    @Override
    public int add(Cart cart) {
        LambdaQueryWrapper<Books> lqw = new LambdaQueryWrapper<>();
        lqw.eq(true, Books::getId, cart.getBooks_id());
        Books books =booksMapper.selectOne(lqw);
        books.setQuantity(books.getQuantity() - cart.getNum());
        booksMapper.updateById(books);

        return cartMapper.insert(cart);
    }

    @Override
    public R getStates(Integer booksid,  Integer uid) {
        LambdaQueryWrapper<Books> lqw_book = new LambdaQueryWrapper<>();
        lqw_book.eq(true, Books::getId, booksid);
        Books book =booksMapper.selectOne(lqw_book);
        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(true, Cart::getBooks_id, booksid);
        lqw.eq(true, Cart::getUser_id, uid);
        Cart one =cartMapper.selectOne(lqw);
        if (one != null) {
            return new R(false, book, one.getNum());
        } else
            return new R(true, book);
    }

    @Override
    public R update(Integer booksid, Integer uid, Integer num) {
        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(true, Cart::getBooks_id, booksid);
        lqw.eq(true, Cart::getUser_id, uid);
        Cart one =cartMapper.selectOne(lqw);
        int i = num - Integer.parseInt(one.getNum().toString());
        LambdaQueryWrapper<Books> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(true, Books::getId, booksid);
        Books books =booksMapper.selectOne(lqw1);
        int booksQuantity = Integer.parseInt(books.getQuantity().toString()) - i;
        if (booksQuantity < 0) {
            return new R(false, "库存不足");
        }
        books.setQuantity(booksQuantity);
        booksMapper.updateById(books);
        one.setNum(num);
        cartMapper.updateById(one);
        return new R(true);
    }

    @Override
    public R getByUid(Integer uid) {
        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(true, Cart::getUser_id, uid);
        List<Cart> list =cartMapper.selectList(lqw);
        for (int i = 0; i < list.size(); i++) {
            Books byId =booksMapper.selectById(list.get(i).getBooks_id());
            list.get(i).setPrice(byId.getPrice());
            list.get(i).setBookname(byId.getBookname());
            list.get(i).setImage(byId.getImage());
            System.err.println(list.get(i).getNum());
        }
        return new R(true, list);
    }

    @Override
    public R del(Integer uid, Integer bid) {
        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(true, Cart::getUser_id, uid);
        lqw.eq(true, Cart::getBooks_id, bid);
        Cart one =cartMapper.selectOne(lqw);
        if (one != null) {
            LambdaQueryWrapper<Books> lqw_books = new LambdaQueryWrapper<>();
            lqw_books.eq(true, Books::getId, bid);
            Books books =booksMapper.selectOne(lqw_books);
            books.setQuantity(books.getQuantity() + one.getNum());
            booksMapper.updateById(books);
            cartMapper.deleteById(one.getId());
            return new R(true);
        } else {
            return new R(false);
        }
    }
}
