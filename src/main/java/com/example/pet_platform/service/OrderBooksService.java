package com.example.pet_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.OrderBooks;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderBooksService extends IService<OrderBooks> {
    R getAll( OrderBooks orderBooks);
}
