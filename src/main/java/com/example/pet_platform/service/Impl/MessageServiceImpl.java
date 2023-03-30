package com.example.pet_platform.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.entity.Message;
import com.example.pet_platform.mapper.MessageMapper;
import com.example.pet_platform.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}
