package com.example.pet_platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.json.JSONArray;

import java.util.Date;
import java.util.List;

@Data
public class Books {
    private Integer id;
    private String image;
    private String author;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date date;
    private String bookname;
    private String press;
    private String type;
    private String authorcontent;
    private String bookcontent;
    private Integer price;
    private Integer quantity;
    private Integer uid;
    private Boolean enable;
    @TableField(exist = false)
    private Integer oid;
    @TableField(exist = false)
    private Integer ocount;
    @TableField(exist = false)
    private List<Books> list;
    @TableField(exist = false)
    private Integer value;
    @TableField(exist = false)
    private String name;

}
