package com.example.pet_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.controller.DTO.SecKillVoucherDTO;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Order;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

public interface OrderService extends IService<Order> {
    R getall(Order orders);
    R add(Order order, HttpServletRequest request);
    R addVoucher(Integer id, HttpServletRequest request);
    R getAll( Integer uid);
    R del( Integer id);
    R Del( Order order);
    R getByname( String name,  Integer uid);
    R updata( Integer id);
    R delupdata( Integer id);

    R createKill(SecKillVoucherDTO secKillVoucherDTO);
}
