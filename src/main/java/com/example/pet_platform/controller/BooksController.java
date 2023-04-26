package com.example.pet_platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.OrderBooks;
import com.example.pet_platform.service.BooksService;
import com.example.pet_platform.service.OrderBooksService;
import com.example.pet_platform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@CrossOrigin
public class BooksController {
    @Autowired
    private BooksService booksService;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderBooksService orderBooksService;
    @GetMapping
    public R getAll(){
        return new R(true,booksService.list());
    }
    @GetMapping("/{currentPage}/{pageSize}")
    public R getPage(@PathVariable int currentPage, @PathVariable int pageSize, Books books){
        System.err.println(books.getBookname());
        IPage<Books> page=booksService.getPage(currentPage,pageSize,books);
        if (currentPage>page.getPages()){
            page=booksService.getPage((int)page.getPages(),pageSize,books);
        }
        return new R(true,page);
    }
    @GetMapping("{id}")
    public  R getById(@PathVariable Integer id){

        return new R(true,booksService.getById(id));
    }
    @PostMapping
    public  R  save(@RequestBody Books books){
        return new R(true,booksService.save(books));
    }
    @PutMapping
    public R updata(@RequestBody Books books){
        return new R(true,booksService.updateById(books));
    }
    @DeleteMapping("{id}")
    public R del(@PathVariable Integer id){
        return new R(true,booksService.removeById(id));
    }
    @PutMapping("/groudList")
    public R updataGroudList(@RequestBody List<Books> booksList){
      for (int i=0;i<booksList.size();i++){
          booksList.get(i).setEnable(true);
          booksService.updateById(booksList.get(i));
      }
      return new R(true);
    }
    @PutMapping("/underList")
    public R updataUnderList(@RequestBody List<Books> booksList){
        for (int i=0;i<booksList.size();i++){
            booksList.get(i).setEnable(false);
            booksService.updateById(booksList.get(i));
        }
        return new R(true);
    }
    @GetMapping("/admin")
    public R getTopFive(){
        List<Books> list = booksService.list();
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
        for (Books books:list){
            books.setName(books.getBookname());
            books.setValue(books.getQuantity());
           }
        return new R(true,list);
    }
    @GetMapping("/admin/order")
    public R getTop(){
        List<OrderBooks> list = orderBooksService.list();
        List<Books> bookslist=new ArrayList<>();
        Map<Integer, List<OrderBooks>> collect = list.stream().collect(Collectors.groupingBy(OrderBooks::getBooks_id));
        Set<Integer> set = collect.keySet();

        //遍历迭代器并输出元素
        for (Integer integer : set) {
            Books byId = booksService.getById(integer);
            List<OrderBooks> list1 = collect.get(integer);
            Integer sum=0;
            for (OrderBooks orderBooks:list1){
                sum=orderBooks.getNum()+sum;
            }
            Books books=new Books();
            books.setName(byId.getBookname());
            books.setValue(sum);
            books.setId(byId.getId());
            books.setImage(byId.getImage());
            books.setPrice(byId.getPrice());
            bookslist.add(books);
        }
        for (int i = 0; i < bookslist.size(); i++) {
            int maxIndex = i;
            // 把当前遍历的数和后面所有的数进行比较，并记录下最小的数的下标
            for (int j = i + 1; j < bookslist.size(); j++) {
                if (bookslist.get(j).getValue() > bookslist.get(maxIndex).getValue()) {
                    // 记录最小的数的下标
                    maxIndex = j;
                }
            }
            // 如果最小的数和当前遍历的下标不一致，则交换
            if (i != maxIndex) {
                Books books = bookslist.get(i);
                bookslist.set(i,bookslist.get(maxIndex));
                bookslist.set(maxIndex,books);
            }
        }
        if (bookslist.size()>6){
            List<Books> books=bookslist.subList(0,6);
            for (Books books1:books){
                books1.setName(books1.getName());
                books1.setValue(books1.getValue());
            }
            return new R (true,books);}
        else {
            List<Books> books = bookslist.subList(0, bookslist.size());
            for (Books books1 : books) {
                books1.setName(books1.getName());
                books1.setValue(books1.getValue());
            }
            Collections.reverse(books);
            return new R(true,books);
        }


    }
}
