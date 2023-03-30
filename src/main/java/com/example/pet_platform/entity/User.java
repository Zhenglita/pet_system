package com.example.pet_platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.swing.*;
import java.util.List;

//springboot中通过@Data即可实现get，set，所以不用再生成get，set，直接写个@Data
@Data
public class User {
    @TableId
    private Integer uid;
    private String username;
    private String password;
    private String email;
    private String sex;
    private String avatar;
    private String role;


}
