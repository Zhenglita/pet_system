package com.example.pet_platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class OrderBooks {
    private Integer id;
    private Integer num;
    private Integer books_id;
    private Integer order_id;
    private Boolean enable;
    @TableField(exist = false)
    private List<OrderBooks> orderBooks;
    @TableField(exist = false)
    private  Integer price;
    @TableField(exist = false)
    private String bookname;
    @TableField(exist = false)
    private String bookcontent;
    @TableField(exist = false)
    private String image;

}

