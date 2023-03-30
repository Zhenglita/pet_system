package com.example.pet_platform.controller.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CensorResult {

    /**
     * 内容是否审核通过
     */
    Boolean isPass;

    /**
     * 审核结果
     */
    ContentWithCensorStateEnum contentWithCensorStateEnum;

    /**
     * 文字审核结果的Json字符串
     */
    String textCensorJson;

    /**
     * 图片审核结果的Json字符串
     */
    String imageCensorJson;
}
    /**
     * 内容审核状态
     */






