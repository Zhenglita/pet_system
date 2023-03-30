package com.example.pet_platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Order {
    private Integer id;
    private Integer total_price;
    private String no;
    private String name;
    private Integer user_id;
    private String state;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String time;
    private String pay_time;
    private String alipay_no;
    @TableField(exist = false)
    private List<Cart> carts;
    @TableField(exist = false)
    private List<OrderBooks> orderBooks;
    @TableField(exist = false)
    private List<Order> orders;
    @TableField(exist = false)
    private User users;

}
