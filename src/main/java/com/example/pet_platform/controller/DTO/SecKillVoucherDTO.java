package com.example.pet_platform.controller.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiniu.util.Json;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class SecKillVoucherDTO {
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
    private Integer voucher_id;
    private Integer stock;
    private Date begin_time;
    private Date end_time;
    private Object time;
    private Integer user_id;
    private Integer num;
    private String bookname;
}
