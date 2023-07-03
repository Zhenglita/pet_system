package com.example.pet_platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Message {
    private Integer id;
    private Integer from_uid;
    private Integer to_uid;

    private String username;

    private String toUsername;
    private String message;
    private Boolean enable;
    private Integer connect;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createtime;
    @TableField(exist = false)
    private String imageUrl;
}
