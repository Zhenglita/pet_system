package com.example.pet_platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.Comment;
import com.example.pet_platform.mapper.BooksMapper;
import com.example.pet_platform.service.BooksService;
import com.example.pet_platform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin
public class BooksController {
    @Autowired
    private BooksService booksService;
    @GetMapping
    public R getAll(){
        return new R(true,booksService.list());
    }
    @GetMapping("/{currentPage}/{pageSize}")
    public R getPage(@PathVariable int currentPage, @PathVariable int pageSize, Books books){
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
        for (int i = 0; i < list.size(); i++) {
            int maxIndex = i;
            // 把当前遍历的数和后面所有的数进行比较，并记录下最小的数的下标
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).getQuantity() > list.get(maxIndex).getQuantity()) {
                    // 记录最小的数的下标
                    maxIndex = j;
                }
            }
            // 如果最小的数和当前遍历的下标不一致，则交换
            if (i != maxIndex) {
                Books books = list.get(i);
                list.set(i,list.get(maxIndex));
                list.set(maxIndex,books);
            }
        }
        if (list.size()>5){
            List<Books> books=list.subList(0,5);
            for (Books books1:books){
                books1.setName(books1.getBookname());
                books1.setValue(books1.getQuantity());
            }
            return new R (true,books);}
        else {
            List<Books> books=list.subList(0, list.size());
            for (Books books1:books){
                books1.setName(books1.getBookname());
                books1.setValue(books1.getQuantity());
            }
            return new R(true,books);
        }
    }
}
