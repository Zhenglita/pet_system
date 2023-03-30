package com.example.pet_platform.controller.DTO;

import lombok.Data;

@Data
public class AliPay {
    private String traceNo;
    private String totalAmount;
    private String subject;
    private String alipayTraceNo;
}

