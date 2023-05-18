package com.example.pet_platform.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.OrderBooks;
import com.example.pet_platform.mapper.BooksMapper;
import com.example.pet_platform.mapper.OrderBooksMapper;
import com.example.pet_platform.service.OrderBooksService;
import com.example.pet_platform.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderBooksServiceImpl extends ServiceImpl<OrderBooksMapper, OrderBooks> implements OrderBooksService {
    @Resource
    private BooksMapper booksMapper;
    @Override
    public R getAll(OrderBooks orderBooks) {
        List<OrderBooks> list = orderBooks.getOrderBooks();
        for (OrderBooks orderBooks1 : list) {
            Books byId = booksMapper.selectById(orderBooks1.getBooks_id());
            orderBooks1.setBookname(byId.getBookname());
            orderBooks1.setImage(byId.getImage());
            orderBooks1.setPrice(byId.getPrice());
            orderBooks1.setBookcontent(byId.getBookcontent());
        }
        return new R(true, list);
    }
}
