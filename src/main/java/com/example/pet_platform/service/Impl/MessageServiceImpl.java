package com.example.pet_platform.service.Impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Comment;
import com.example.pet_platform.entity.Message;
import com.example.pet_platform.entity.User;
import com.example.pet_platform.mapper.MessageMapper;
import com.example.pet_platform.mapper.UserMapper;
import com.example.pet_platform.service.MessageService;
import com.example.pet_platform.util.JWTUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public R getAll(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Message> qw = new QueryWrapper<>();
        qw.eq("to_uid", Integer.parseInt(userid));
        qw.eq("enable", false);
        qw.select("DISTINCT username,toUsername,from_uid,to_uid");
        List<Message> list = messageMapper.selectList(qw);
        return new R(true, list);
    }

    @Override
    public R delAll(HttpServletRequest request, String username) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Message> qw = new QueryWrapper<>();
        qw.eq("from_uid", Integer.parseInt(userid));
        qw.eq("toUsername", username);
        qw.eq("enable", false);
        messageMapper.delete(qw);
        return new R(true);
    }

    @Override
    public R addAll(HttpServletRequest request, String username) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("uid", Integer.parseInt(userid));
        User one = userMapper.selectOne(qw);
        QueryWrapper<User> qw1 = new QueryWrapper<>();
        qw1.eq("username", username);
        User one1 = userMapper.selectOne(qw1);
        Message message = new Message();
        message.setFrom_uid(Integer.parseInt(userid));
        message.setToUsername(username);
        message.setUsername(one.getUsername());
        message.setTo_uid(one1.getUid());
        message.setConnect(1);
        messageMapper.insert(message);
        if (StringUtils.isEmpty(message.getMessage())) {

        }
        return new R(true);
    }

    @Override
    public R addAll(HttpServletRequest request, Comment comment) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("uid", Integer.parseInt(userid));
        User one = userMapper.selectOne(qw);
        Message message = new Message();
        message.setFrom_uid(Integer.parseInt(userid));
        message.setToUsername(comment.getUsername());
        message.setUsername(one.getUsername());
        message.setTo_uid(comment.getUser_id());
        message.setEnable(true);
        message.setMessage(comment.getUsername() + "用户你好，你最近" + comment.getCreatetime().toString() + "在文章" + comment.getArticlename() + "的评论" + comment.getContent_type() + "异常，希望你下次注意评论");
        messageMapper.insert(message);
        return new R(true);

    }

    @Override
    public R addAll(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Message> qw = new QueryWrapper<>();
        qw.eq("to_uid", Integer.parseInt(userid));
        qw.eq("enable", true);
        return new R(true, messageMapper.selectList(qw));
    }

    @Override
    public DeferredResult<Boolean> sendMessage(HttpServletRequest request) {
        long timeoutValue = 5000;//超时时间.
        DeferredResult<Boolean> deferredResult = new DeferredResult<>(timeoutValue);
        Boolean a = true;
        new Thread(() -> {
            //执行耗时的逻辑
            try {
                //休眠n秒钟进行模拟业务代码.
                String token = request.getHeader("Authorization");
                DecodedJWT verify = JWTUtils.verify(token);
                String userid = verify.getClaim("userid").asString();
                QueryWrapper<Message> qw = new QueryWrapper<>();
                qw.eq("enable", false);
                qw.eq("connect", 1);
                qw.eq("to_uid", Integer.parseInt(userid));
                List<Message> list =messageMapper.selectList(qw);
                if (list.size() > 0) {
                    deferredResult.setResult(true);
                } else {
                    deferredResult.setResult(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return deferredResult;
    }

    @Override
    public R downMessage() {
        QueryWrapper<Message> qw = new QueryWrapper<>();
        qw.eq("connect", 1);
        Message one = messageMapper.selectOne(qw);
        if (one != null) {
            one.setConnect(2);
            messageMapper.updateById(one);
        }

        return new R();
    }

    @Override
    public R addRole(HttpServletRequest request, String messages) {
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Message> qw1 = new QueryWrapper<>();
        qw1.isNull("toUsername").isNull("to_uid");
        qw1.eq("enable", true);
        qw1.eq("from_uid", Integer.parseInt(userid));
        Message one1 = messageMapper.selectOne(qw1);
        if (one1 == null) {
            QueryWrapper<User> qw = new QueryWrapper<>();
            qw.eq("uid", Integer.parseInt(userid));
            User one = userMapper.selectOne(qw);
            Message message = new Message();
            message.setFrom_uid(Integer.parseInt(userid));
            message.setUsername(one.getUsername());
            message.setEnable(true);
            message.setMessage(messages);
            messageMapper.insert(message);
            return new R(true);
        }

        return new R(false);
    }

    @Override
    public R getRole() {
        QueryWrapper<Message> qw = new QueryWrapper<>();
        qw.isNull("toUsername").isNull("to_uid");
        qw.eq("enable", true);
        List<Message> list = messageMapper.selectList(qw);
        for (int i = 0; i < list.size(); i++) {
            QueryWrapper<User> qw1 = new QueryWrapper<>();
            qw1.eq("uid", list.get(i).getFrom_uid());
            User one = userMapper.selectOne(qw1);
            list.get(i).setImageUrl(one.getAvatar());
        }
        return new R(true, list);
    }
}
