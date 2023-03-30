package com.example.pet_platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class Comment {
    private Integer id;
    private String content;
    private String real_content;
    private String content_type;
    private String username;
    private Integer user_id;
    private BigDecimal rate;
    private Integer foregin_id;
    private Integer pid;
    private String target;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createtime;
    @TableField(exist = false)
    private List<Comment> children;
    @TableField(exist = false)
    private Integer count;
    @TableField(exist = false)
    private String articlename;
    @TableField(exist = false)
    private String avatar;

}
