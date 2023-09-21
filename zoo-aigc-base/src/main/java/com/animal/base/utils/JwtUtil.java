package com.animal.base.utils;

import com.animal.user.model.vo.UserVO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.HashMap;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/7/21 1:10
 */
public class JwtUtil {

    private static final String TOKEN = "!ZOOPARTY";

    //jwt生成令牌
    public static String generateToken(UserVO user){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,7);

        String token = JWT.create()
                .withHeader(new HashMap<>())
                .withClaim("Name",user.getName())
                .withClaim("Email",user.getEmail())
                .withClaim("Phone",user.getPhone())
                .withClaim("Avatar",user.getAvatar())
                .withClaim("Money",user.getMoney().toString())
                .withClaim("Description",user.getDescription())
                .withClaim("Id",user.getId())
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256(TOKEN));

        return token;
    }

    //校验jwt
    public static Boolean checkToken(String jwtToken){
        try {
            JWT.require(Algorithm.HMAC256(TOKEN)).build().verify(jwtToken);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    //获取token值
    public static DecodedJWT getToken(String jwtToken){
        return JWT.require(Algorithm.HMAC256(TOKEN)).build().verify(jwtToken);
    }
}
