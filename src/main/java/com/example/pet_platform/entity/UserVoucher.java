package com.example.pet_platform.entity;

import lombok.Data;

@Data
public class UserVoucher {
    private Integer id;
    private Integer user_id;
    private Integer voucher_id;
    private Boolean type;
    private Integer num;
}
