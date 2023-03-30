package com.example.pet_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pet_platform.entity.Files;
import com.example.pet_platform.entity.Follow;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FollowMapper extends BaseMapper<Follow> {
}
