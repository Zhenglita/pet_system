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

    @PostMapping
    private R getAll(@RequestBody OrderBooks orderBooks) {
      return orderBooksService.getAll(orderBooks);
    }
}
