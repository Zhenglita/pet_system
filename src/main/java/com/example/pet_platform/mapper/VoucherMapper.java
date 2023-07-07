package com.example.pet_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pet_platform.controller.DTO.SecKillVoucherDTO;
import com.example.pet_platform.controller.VO.VoucherVO;
import com.example.pet_platform.entity.Follow;
import com.example.pet_platform.entity.UserVoucher;
import com.example.pet_platform.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Mapper
public interface VoucherMapper  extends BaseMapper<Voucher> {
       Integer saveSkill( Voucher voucher);

       List<VoucherVO> getAllVoucher(@Param("book_id") Integer book_id);
       int saveVoucher(SecKillVoucherDTO secKillVoucherDTO);
       UserVoucher getUserVoucher(SecKillVoucherDTO secKillVoucherDTO);
       int updatNum(SecKillVoucherDTO secKillVoucherDTO);
       SecKillVoucherDTO selectAllById(@Param("id") Integer id);
       List<SecKillVoucherDTO> selectAllByUserId(@Param("user_id") Integer user_id);
}
