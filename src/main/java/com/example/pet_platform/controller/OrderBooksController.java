package com.example.pet_platform.controller;

import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.OrderBooks;
import com.example.pet_platform.service.BooksService;
import com.example.pet_platform.service.OrderBooksService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/orderbooks")
public class OrderBooksController {
    @Resource
    private OrderBooksService orderBooksService;
    @Resource
    private BooksService booksService;
    @PostMapping
    private R getAll(@RequestBody OrderBooks orderBooks){
        List<OrderBooks> list=orderBooks.getOrderBooks();
        for (OrderBooks orderBooks1:list){
            Books byId = booksService.getById(orderBooks1.getBooks_id());
            orderBooks1.setBookname(byId.getBookname());
            orderBooks1.setImage(byId.getImage());
            orderBooks1.setPrice(byId.getPrice());
            orderBooks1.setBookcontent(byId.getBookcontent());
        }
        return new R(true,list);
    }
}
