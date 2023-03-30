package com.example.pet_platform.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pet_platform.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
