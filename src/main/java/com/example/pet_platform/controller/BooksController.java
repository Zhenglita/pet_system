package com.example.pet_platform.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Article;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.OrderBooks;
import com.example.pet_platform.service.BooksService;
import com.example.pet_platform.service.OrderBooksService;
import com.example.pet_platform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@CrossOrigin
public class BooksController {
    @Autowired
    private BooksService booksService;

    @GetMapping
    public R getAll() {
        return new R(true, booksService.list());
    }

    @GetMapping("/{currentPage}/{pageSize}")
    public R getPage(@PathVariable int currentPage, @PathVariable int pageSize, Books books) {
        System.err.println(books.getBookname());
        IPage<Books> page = booksService.getPage(currentPage, pageSize, books);
        if (currentPage > page.getPages()) {
            page = booksService.getPage((int) page.getPages(), pageSize, books);
        }
        return new R(true, page);
    }

    @GetMapping("{id}")
    public R getById(@PathVariable Integer id) {

        return new R(true, booksService.getById(id));
    }

    @PostMapping
    public R save(@RequestBody Books books) {
        return new R(true, booksService.save(books));
    }

    @PutMapping
    public R updata(@RequestBody Books books) {
        return new R(true, booksService.updateById(books));
    }

    @DeleteMapping("{id}")
    public R del(@PathVariable Integer id) {
        return new R(true, booksService.removeById(id));
    }

    @PutMapping("/groudList")
    public R updataGroudList(@RequestBody List<Books> booksList) {
        for (int i = 0; i < booksList.size(); i++) {
            booksList.get(i).setEnable(true);
            booksService.updateById(booksList.get(i));
        }
        return new R(true);
    }

    @PutMapping("/underList")
    public R updataUnderList(@RequestBody List<Books> booksList) {
        for (int i = 0; i < booksList.size(); i++) {
            booksList.get(i).setEnable(false);
            booksService.updateById(booksList.get(i));
        }
        return new R(true);
    }

    @GetMapping("/admin")
    public R getTopFive() {

        return new R(true, booksService.getTopFive());
    }

    @GetMapping("/admin/order")
    public R getTop() {

            return new R(true, booksService.getTop());
    }

}
