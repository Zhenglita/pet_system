package com.example.pet_platform.controller.DTO;


import lombok.Data;

import java.util.List;

/**
 * 接受前端登录请求的参数
 */
@Data
public class UserDTO {
    private Integer uid;
    private String username;
    private String password;
    private String sex;
    private String email;
    private String avatar;
    private String token;
    private String role;
    private String place;

}
