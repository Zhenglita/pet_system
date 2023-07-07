package com.example.pet_platform.service.Impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.DTO.SecKillVoucherDTO;
import com.example.pet_platform.controller.VO.VoucherVO;
import com.example.pet_platform.entity.Books;
import com.example.pet_platform.entity.SeckillVoucher;
import com.example.pet_platform.entity.Voucher;
import com.example.pet_platform.mapper.SeckillVoucherMapper;
import com.example.pet_platform.mapper.VoucherMapper;
import com.example.pet_platform.service.BooksService;
import com.example.pet_platform.service.SeckillVoucherService;
import com.example.pet_platform.service.VoucherService;
import com.example.pet_platform.util.JWTUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {
    @Resource
    private SeckillVoucherService seckillVoucherService;
    @Resource
    private VoucherMapper voucherMapper;
    @Resource
    private BooksService booksService;
    @Transactional
    @Override
    public Boolean saveSeckill(SecKillVoucherDTO secKillVoucherDTO) throws ParseException {
        Voucher voucher=new Voucher();
        voucher.setRules(secKillVoucherDTO.getRules());
        voucher.setBook_id(secKillVoucherDTO.getBook_id());
        voucher.setPay_value(secKillVoucherDTO.getPay_value());
        voucher.setActual_value(secKillVoucherDTO.getActual_value());
        voucher.setTitle(secKillVoucherDTO.getTitle());
        voucher.setType(true);
        voucherMapper.saveSkill(voucher);
        SeckillVoucher seckillVoucher=new SeckillVoucher();
        seckillVoucher.setVoucher_id(voucher.getId());
        seckillVoucher.setStock(seckillVoucher.getStock());
        Object time = secKillVoucherDTO.getTime();
        List<String> list=(List<String>) time;
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
        Date begin = fmt.parse(list.get(0));
        Date end=fmt.parse(list.get(1));
        seckillVoucher.setBegin_time(begin);
        seckillVoucher.setEnd_time(end);
        boolean save = seckillVoucherService.save(seckillVoucher);
        return true;
    }

    @Override
    public List<VoucherVO> getSkillVoucher(Integer book_id) throws ParseException {
        List<VoucherVO> allVoucher = voucherMapper.getAllVoucher(book_id);
        ZoneId timeZone = ZoneId.systemDefault();
        for (VoucherVO voucherVO:allVoucher){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (voucherVO.getBegin_time()!=null&&voucherVO.getEnd_time()!=null&&voucherVO.getType().equals(true)){
                voucherVO.setBegin(format.format(voucherVO.getBegin_time()));
                voucherVO.setEnd(format.format(voucherVO.getEnd_time()));
            }else {
                LocalDate getLocalDate = voucherVO.getCreate_time().toInstant().atZone(timeZone).toLocalDate();
                voucherVO.setTime(getLocalDate.getYear());
            }
        }
        return allVoucher;
    }

    @Override
    public List<SecKillVoucherDTO> getUserVoucher(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        List<SecKillVoucherDTO> secKillVoucherDTOS = voucherMapper.selectAllByUserId(Integer.parseInt(userid));
        ZoneId timeZone = ZoneId.systemDefault();
        for (SecKillVoucherDTO secKillVoucherDTO:secKillVoucherDTOS){
            Books byId = booksService.getById(secKillVoucherDTO.getBook_id());
            secKillVoucherDTO.setBookname(byId.getBookname());
            LocalDate getLocalDate = secKillVoucherDTO.getCreate_time().toInstant().atZone(timeZone).toLocalDate();
            secKillVoucherDTO.setTime(getLocalDate.getYear());
        }

        return secKillVoucherDTOS;
    }
}
