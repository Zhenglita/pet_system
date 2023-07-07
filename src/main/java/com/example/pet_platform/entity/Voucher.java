package com.example.pet_platform.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Voucher {
    private Integer id;
    private Integer book_id;
    private String title;

    private String sub_title;

    private String rules;
    private Integer pay_value;
    private Integer actual_value;
    private Boolean type;
    private Boolean status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;
}
