package com.example.pet_platform.controller.util;


import lombok.Data;

@Data
public class R {
    private Boolean flag;
    private Object data;
    private Object things;
    private String msg;
    private String code;
    private Integer num;
    public  R(){

    }
    public  R(Boolean flag){
        this.flag=flag;
    }
    public  R(Boolean flag,Object data){
        this.flag=flag;
        this.data=data;
    }
    public  R(Boolean flag,Object data,Integer num){
        this.flag=flag;
        this.data=data;
        this.num=num;
    }
    public  R(Boolean flag,Object data,Object things){
        this.flag=flag;
        this.data=data;
        this.things=things;
    }
    public  R(Boolean flag,String msg){
        this.flag=flag;
        this.msg=msg;
    }
    public  R(String code,String msg){
        this.code=code;
        this.msg=msg;
    }
    public  R(String msg){
        this.msg=msg;
    }
}