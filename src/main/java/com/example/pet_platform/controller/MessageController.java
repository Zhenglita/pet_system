package com.example.pet_platform.controller;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pet_platform.component.WebSocketServer;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.*;
import com.example.pet_platform.service.MessageService;
import com.example.pet_platform.service.UserService;
import com.example.pet_platform.util.JWTUtils;
//import com.sun.tools.sjavac.Log;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin
@RequestMapping("/messages")
public class MessageController {
    @Resource
    private MessageService messageService;

    @GetMapping
    public R getAll(HttpServletRequest request) {
      return messageService.getAll(request);
    }

    @DeleteMapping("/{username}")
    public R delAll(HttpServletRequest request, @PathVariable String username) {
       return messageService.delAll(request,username);
    }

    @GetMapping("/add/{username}")
    public R addAll(HttpServletRequest request, @PathVariable String username) {
      return messageService.addAll(request,username);
    }

    @GetMapping("/enable")
    public R addAll(HttpServletRequest request) {
       return messageService.addAll(request);
    }

    @GetMapping("/handleReqDefResult")
    public DeferredResult<Boolean> sendMessage(HttpServletRequest request) {
       return messageService.sendMessage(request);

    }

    @PostMapping("/add")
    public R addAll(HttpServletRequest request, @RequestBody Comment comment) {
       return messageService.addAll(request,comment);
    }

    @GetMapping("/zlt/{id}")
    public R delMessage(@PathVariable Integer id) {
        return new R(messageService.removeById(id));
    }

    @GetMapping("/zlt")
    public R downMessage() {
       return messageService.downMessage();
    }

    @GetMapping("/add/quest/{messages}")
    public R addRole(HttpServletRequest request, @PathVariable String messages) {
        return messageService.addRole(request,messages);
    }

    @GetMapping("/getMessage")
    public R getRole() {
        return messageService.getRole();
    }
}
