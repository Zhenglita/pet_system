package com.example.pet_platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pet_platform.controller.DTO.SecKillVoucherDTO;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.SeckillVoucher;
import com.example.pet_platform.entity.Voucher;
import com.example.pet_platform.service.SeckillVoucherService;
import com.example.pet_platform.service.VoucherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/vouchers")
public class VoucherController {
    @Resource
    private VoucherService voucherService;


    @GetMapping
    public R getAll(){
        return new R(true,voucherService.list());
    }
    @GetMapping("/{book_id}")
    public R getBooksVoucher(@PathVariable Integer book_id) throws ParseException {

        return new R(true,voucherService.getSkillVoucher(book_id));
    }
    @GetMapping("/user")
    public R getBooksVoucher(HttpServletRequest request)  {

        return new R(true,voucherService.getUserVoucher(request));
    }
    @PostMapping
    public R save(@RequestBody Voucher voucher){
        return new R(true,voucherService.save(voucher));
    }
    @PutMapping
    public  R updata(@RequestBody Voucher voucher){
        return new R(true,voucherService.updateById(voucher));
    }
    @DeleteMapping
    public R del(@RequestBody Voucher voucher){
        return new R(true,voucherService.updateById(voucher));
    }
    @PostMapping("/kill")
    public R saveKill(@RequestBody SecKillVoucherDTO secKillVoucherDTO) throws ParseException {

        return new R(true,voucherService.saveSeckill(secKillVoucherDTO));
    }
}
