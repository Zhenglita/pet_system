package com.example.pet_platform.entity;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


import java.util.Date;
import java.util.List;

@Data

public class Article {
    @TableId(type = IdType.AUTO)
    private Integer aid;
    private String image;
    private String author;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date date;
    private String title;
    private String message;
    private String  agree;
    private Integer  comment;
    private Integer uid;
    private Boolean enable;
    private Integer enable_uid;
    private Integer enable_rate;
    /**
     * 管理员审批内容
     */
    private String enable_message;
    /**
     * 作者头像
     */
    @TableField(exist = false)
    private String uimage;
    /**
     * 管理员信息
     */
    @TableField(exist = false)
    private User enable_user;

}
