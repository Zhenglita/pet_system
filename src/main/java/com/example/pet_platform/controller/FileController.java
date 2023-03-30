package com.example.pet_platform.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pet_platform.entity.Files;
import com.example.pet_platform.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

//文件上传接口
@RestController
@CrossOrigin
@RequestMapping("/files")
public class FileController {
    @Value("${files.upload.path}")
    private String fileUploadPath;
    @Autowired
    private FileMapper fileMapper;
    @PostMapping
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type =
                FileUtil.extName(originalFilename);
        long size= file.getSize();
        //先存储数据库
        File uploadParentFile = new File(fileUploadPath);
        //判断配置文件的目录是否存在，不存在创建一个新的
        if (!uploadParentFile.exists()){
            boolean mkdirs = uploadParentFile.mkdirs();
        }
        //定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUuid=uuid+ StrUtil.DOT+type;
        File uploadFile = new File(fileUploadPath + fileUuid);

        String url;
        String md5;
        //保存到本地磁盘
        file.transferTo(uploadFile);
        //获取文件的md5，当md5为判断是否为重复的图片
        md5= SecureUtil.md5(uploadFile);
        //避免相同图片多次保存浪费磁盘空间
        Files dbFiles = getFileByMd5(md5);
        if (dbFiles!=null){
            url=dbFiles.getUrl();
            boolean delete = uploadFile.delete();
        }else {
            //把获得的文件存储到磁盘目录
            url="http://localhost/files/"+fileUuid;
        }

        Files saveFile=new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size/1024);
        saveFile.setMd5(md5);
        saveFile.setUrl(url);

        fileMapper.insert(saveFile);
        return url;
    }


    //    文件下载接口
    @GetMapping("{fileUuid}")
    public void download(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        //根据文件的唯一标识码获取文件
        File uploadFile=new File(fileUploadPath+fileUuid);
        //        设置输出流格式
        ServletOutputStream os=response.getOutputStream();
        response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileUuid,"UTF-8"));
        response.setContentType("application/octet-stream");
        //读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();

    }
    private Files getFileByMd5(String md5){
        QueryWrapper<Files> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        //判断这个文件的md5是否和数据库中一样，因为数据库设置了唯一索引，查询MD5相同会报错
        List<Files> files = fileMapper.selectList(queryWrapper);
        return files.size()==0?null:files.get(0);
    }
}
