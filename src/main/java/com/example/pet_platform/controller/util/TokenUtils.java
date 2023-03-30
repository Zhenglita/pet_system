package com.example.pet_platform.controller.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class TokenUtils {
    public  static String getToken(String userid,String sign){
      return   JWT.create().withAudience(userid)//将user id保存到token里面，作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(),2))//new Date是当前时间，2小时有效期过期
                .sign(Algorithm.HMAC256(sign));//用户名字作为密钥
    }
}
