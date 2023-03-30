package com.example.pet_platform.util;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class JWTUtils {

    private  static final  String SING="!QW@#W213QEQWE";
    /*
    生成token
     */
    public  static String getToken(String userid,String username){
        return   JWT.create()
                .withClaim("userid",userid)
                .withClaim("username",username)//将user id保存到token里面，作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(),2))//new Date是当前时间，2小时有效期过期
                .sign(Algorithm.HMAC256(SING));
    }
    /*
    验证token
     */
    public static DecodedJWT verify(String token){
        return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);

    }
}
