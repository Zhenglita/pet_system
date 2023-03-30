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
    @Resource
    private UserService userService;
    @GetMapping
    public R getAll(HttpServletRequest request)
    { String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Message> qw=new QueryWrapper<>();
        qw.eq("to_uid",Integer.parseInt(userid));
        qw.eq("enable",false);
        qw.select("DISTINCT username,toUsername,from_uid,to_uid");
        List<Message> list = messageService.list(qw);
        return new R(true,list);
    }
    @DeleteMapping("/{username}")
    public R delAll(HttpServletRequest request,@PathVariable String username)
    { String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Message> qw=new QueryWrapper<>();
        qw.eq("from_uid",Integer.parseInt(userid));
        qw.eq("toUsername",username);
        qw.eq("enable",false);
        messageService.remove(qw);
        return new R(true);
    }
    @GetMapping("/add/{username}")
    public R addAll(HttpServletRequest request,@PathVariable String username) throws IOException { String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<User> qw=new QueryWrapper<>();
        qw.eq("uid",Integer.parseInt(userid));
        User one = userService.getOne(qw);
        QueryWrapper<User> qw1=new QueryWrapper<>();
        qw1.eq("username",username);
        User one1 = userService.getOne(qw1);
        Message message = new Message();
        message.setFrom_uid(Integer.parseInt(userid));
        message.setToUsername(username);
        message.setUsername(one.getUsername());
        message.setTo_uid(one1.getUid());
        message.setConnect(1);
        messageService.save(message);
        if (StringUtils.isEmpty(message.getMessage())){

        }
        return new R(true);
    }
    @GetMapping("/enable")
    public R addAll(HttpServletRequest request)
    { String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Message> qw=new QueryWrapper<>();
        qw.eq("to_uid",Integer.parseInt(userid));
        qw.eq("enable",true);
        return new R(true, messageService.list(qw));
    }
    @GetMapping("/handleReqDefResult")
    public DeferredResult<Boolean> sendMessage(HttpServletRequest request)
    {  long timeoutValue = 5000;//超时时间.
        DeferredResult<Boolean> deferredResult = new DeferredResult<>(timeoutValue);
        Boolean a=true;
        new Thread(() -> {
            //执行耗时的逻辑
            try {
                //休眠n秒钟进行模拟业务代码.
                String token = request.getHeader("Authorization");
                DecodedJWT verify = JWTUtils.verify(token);
                String userid = verify.getClaim("userid").asString();
                QueryWrapper<Message> qw=new QueryWrapper<>();
                qw.eq("enable",false);
                qw.eq("connect",1);
                qw.eq("to_uid",Integer.parseInt(userid));
                List<Message> list = messageService.list(qw);
                if (list.size()>0){
                    deferredResult.setResult(true);
                }
                else {
                   deferredResult.setResult(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return deferredResult;

    }
    @PostMapping("/add")
    public R addAll(HttpServletRequest request, @RequestBody Comment comment)
    { String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<User> qw=new QueryWrapper<>();
        qw.eq("uid",Integer.parseInt(userid));
        User one = userService.getOne(qw);
        Message message = new Message();
        message.setFrom_uid(Integer.parseInt(userid));
        message.setToUsername(comment.getUsername());
        message.setUsername(one.getUsername());
        message.setTo_uid(comment.getUser_id());
        message.setEnable(true);
        message.setMessage(comment.getUsername()+"用户你好，你最近"+comment.getCreatetime().toString()+"在文章"+comment.getArticlename()+"的评论"+comment.getContent_type()+"异常，希望你下次注意评论");
        messageService.save(message);
        return new R(true);
    }
    @GetMapping("/zlt/{id}")
    public R delMessage(@PathVariable Integer id){
    return new R(messageService.removeById(id));
    }
    @GetMapping("/zlt")
    public R downMessage(){
        QueryWrapper<Message> qw=new QueryWrapper<>();
        qw.eq("connect",1);
        Message one = messageService.getOne(qw);
        if (one!=null){
            one.setConnect(2);
            messageService.updateById(one);
        }

        return new R();
    }
    @GetMapping("/add/quest/{messages}")
    public R addRole(HttpServletRequest request,@PathVariable String messages)
    {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Message> qw1=new QueryWrapper<>();
        qw1.isNull("toUsername").isNull("to_uid");
        qw1.eq("enable",true);
        qw1.eq("from_uid",Integer.parseInt(userid));
        Message one1 = messageService.getOne(qw1);
        if (one1==null){
            QueryWrapper<User> qw=new QueryWrapper<>();
            qw.eq("uid",Integer.parseInt(userid));
            User one = userService.getOne(qw);
            Message message = new Message();
            message.setFrom_uid(Integer.parseInt(userid));
            message.setUsername(one.getUsername());
            message.setEnable(true);
            message.setMessage(messages);
            messageService.save(message);
            return new R(true);
        }

        return new R(false);
    }
    @GetMapping("/getMessage")
    public R getRole()
    {
        QueryWrapper<Message> qw=new QueryWrapper<>();
        qw.isNull("toUsername").isNull("to_uid");
        qw.eq("enable",true);
        List<Message> list = messageService.list(qw);
        for (int i=0;i<list.size();i++){
            QueryWrapper<User> qw1=new QueryWrapper<>();
            qw1.eq("uid",list.get(i).getFrom_uid());
            User one = userService.getOne(qw1);
            list.get(i).setImageUrl(one.getAvatar());
        }
        return new R(true,list);
    }
}
