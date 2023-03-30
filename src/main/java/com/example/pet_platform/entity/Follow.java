package com.example.pet_platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Follow {
    private Integer id;
    private Integer b_uid;
    private Integer f_uid;
    @TableField(exist = false)
    private User user;
}
