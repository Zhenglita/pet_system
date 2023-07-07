package com.example.pet_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.controller.DTO.SecKillVoucherDTO;
import com.example.pet_platform.controller.VO.VoucherVO;
import com.example.pet_platform.entity.Voucher;
import com.example.pet_platform.mapper.VoucherMapper;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

public interface VoucherService extends IService<Voucher> {
    Boolean saveSeckill(SecKillVoucherDTO secKillVoucherDTO) throws ParseException;
    List<VoucherVO> getSkillVoucher(Integer book_id) throws ParseException;
    List<SecKillVoucherDTO> getUserVoucher(HttpServletRequest request);

}
