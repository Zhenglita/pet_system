package com.example.pet_platform.entity;

import com.example.pet_platform.controller.DTO.UserDTO;

public class UserHolder {
    private static final ThreadLocal<UserDTO> tl=new ThreadLocal<>();
    public static void saveUser(UserDTO userDTO){
        tl.set(userDTO);
    }
    public static UserDTO getUser(){
        return tl.get();
    }
    public static void removeUser(){
        tl.remove();
    }
}

