package com.example.pet_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pet_platform.entity.Books;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BooksMapper extends BaseMapper<Books> {

}
