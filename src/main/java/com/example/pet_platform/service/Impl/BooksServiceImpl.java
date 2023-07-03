package com.example.pet_platform.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.OrderBooks;
import com.example.pet_platform.mapper.BooksMapper;
import com.example.pet_platform.mapper.OrderBooksMapper;
import com.example.pet_platform.service.BooksService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements BooksService {
    @Autowired
    private BooksMapper booksMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private OrderBooksMapper orderBooksMapper;

    @Override
    public IPage<Books> getPage(int currentPage, int pageSize, Books books) {
        LambdaQueryWrapper<Books> lqw = new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(books.getType()), Books::getType, books.getType());
        lqw.like(Strings.isNotEmpty(books.getAuthor()), Books::getAuthor, books.getAuthor());
        lqw.like(Strings.isNotEmpty(books.getBookname()), Books::getBookname, books.getBookname());
        lqw.eq(books.getEnable() != null, Books::getEnable, books.getEnable());
        IPage<Books> page = new Page<>(currentPage, pageSize);
        booksMapper.selectPage(page, lqw);
        return page;
    }

    @Override
    public List<Books> getTopFive() {
        List<Books> list = booksMapper.selectList(null);
        //        for (int i = 0; i < list.size(); i++) {
//            int maxIndex = i;
//            // 把当前遍历的数和后面所有的数进行比较，并记录下最小的数的下标
//            for (int j = i + 1; j < list.size(); j++) {
//                if (list.get(j).getQuantity() > list.get(maxIndex).getQuantity()) {
//                    // 记录最小的数的下标
//                    maxIndex = j;
//                }
//            }
//            // 如果最小的数和当前遍历的下标不一致，则交换
//            if (i != maxIndex) {
//                Books books = list.get(i);
//                list.set(i,list.get(maxIndex));
//                list.set(maxIndex,books);
//            }
//        }
//        if (list.size()>5){
//            List<Books> books=list.subList(0,5);
//            for (Books books1:books){
//                books1.setName(books1.getBookname());
//                books1.setValue(books1.getQuantity());
//            }
//            return new R (true,books);}
//        else {
//            List<Books> books=list.subList(0, list.size());
//            for (Books books1:books){
//                books1.setName(books1.getBookname());
//                books1.setValue(books1.getQuantity());
//            }
//            return new R(true,books);
//        }
        for (Books books : list) {
            books.setName(books.getBookname());
            books.setValue(books.getQuantity());
        }
        return list;
    }

    @Override
    public List<Books> getTop() {
        String key = "cache:booksTen";
        String booksTen = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(booksTen)) {
            JSONArray jsonArray = JSONUtil.parseArray(booksTen);
            List<Books> list = JSONUtil.toList(jsonArray, Books.class);
            return list;
        }
        List<OrderBooks> list = orderBooksMapper.selectList(null);
        List<Books> bookslist = new ArrayList<>();
        Map<Integer, List<OrderBooks>> collect = list.stream().collect(Collectors.groupingBy(OrderBooks::getBooks_id));
        Set<Integer> set = collect.keySet();

        //遍历迭代器并输出元素
        for (Integer integer : set) {
            Books byId = booksMapper.selectById(integer);
            List<OrderBooks> list1 = collect.get(integer);
            Integer sum = 0;
            for (OrderBooks orderBooks : list1) {
                sum = orderBooks.getNum() + sum;
            }
            Books books = new Books();
            books.setName(byId.getBookname());
            books.setValue(sum);
            books.setId(byId.getId());
            books.setImage(byId.getImage());
            books.setPrice(byId.getPrice());
            bookslist.add(books);
        }
//        for (int i = 0; i < bookslist.size(); i++) {
//            int maxIndex = i;
//            // 把当前遍历的数和后面所有的数进行比较，并记录下最小的数的下标
//            for (int j = i + 1; j < bookslist.size(); j++) {
//                if (bookslist.get(j).getValue() > bookslist.get(maxIndex).getValue()) {
//                    // 记录最小的数的下标
//                    maxIndex = j;
//                }
//            }
//            // 如果最小的数和当前遍历的下标不一致，则交换
//            if (i != maxIndex) {
//                Books books = bookslist.get(i);
//                bookslist.set(i,bookslist.get(maxIndex));
//                bookslist.set(maxIndex,books);
//            }
//        }
        //快排
        //
        if (bookslist.size() > 6) {
            List<Books> books = bookslist.subList(0, 6);
            for (Books books1 : books) {
                books1.setName(books1.getName());
                books1.setValue(books1.getValue());
            }
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(books), 30, TimeUnit.MINUTES);
            return books;
        } else {
            List<Books> books = bookslist.subList(0, bookslist.size());
            for (Books books1 : books) {
                books1.setName(books1.getName());
                books1.setValue(books1.getValue());
            }
            Collections.reverse(books);
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(books), 30, TimeUnit.MINUTES);
            return books;
        }



    }
}
