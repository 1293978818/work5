package com.ygh.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

/**
 * JWT工具类
 * @author
 */
@Component
public class JwtUtil {

    @Value("${my.secret}")
    private String key;
    
    public String createToken(String userInfo){

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + 1000 * 60 * 60 * 24 * 2);
        Map<String,Object> map = new HashMap<>(16);
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        return JWT.create()
            .withHeader(map)
            .withIssuer("ygh")
            .withIssuedAt(issuedDate)
            .withExpiresAt(expiredDate)
            .withClaim("userInfo", userInfo)
            .sign(Algorithm.HMAC256(key));
    }

    public boolean verifyJwt(String jwt){
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(key)).build();
            jwtVerifier.verify(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserInfo(String jwt){
        DecodedJWT decodedJwt = JWT.decode(jwt);
        return decodedJwt.getClaim("userInfo").asString();
    }
}
