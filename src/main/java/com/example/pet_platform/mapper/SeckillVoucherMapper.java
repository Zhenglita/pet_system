package com.example.pet_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pet_platform.controller.DTO.SecKillVoucherDTO;
import com.example.pet_platform.entity.SeckillVoucher;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucher> {
    int updataKill(SecKillVoucherDTO secKillVoucherDTO);
}
