package com.example.pet_platform.controller;

import cn.hutool.core.lang.UUID;
import com.example.pet_platform.controller.util.QiniuUtils;
import com.example.pet_platform.controller.util.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/upload")
public class UploadController {
    @Resource
    private QiniuUtils qiniuUtils;
    @PostMapping
    public String upload(@RequestParam MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        //唯一的文件名称 使用UUID
        String fileName =  UUID.randomUUID().toString()+"." + StringUtils.substringAfterLast(originalFilename,".");
        //上传文件 长传到哪呢？ 七牛云
        boolean upload = qiniuUtils.upload(file, fileName);
        if(upload){
            return QiniuUtils.url  + fileName;
        }
        return"上传失败";

    }
}
