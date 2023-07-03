package com.example.pet_platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Cart {
    private Integer id;
    private Integer books_id;
    private Integer user_id;
    private Integer num;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;
    @TableField(exist = false)
    private String bookname;
    @TableField(exist = false)
    private Integer price;
    @TableField(exist = false)
    private String image;


}
