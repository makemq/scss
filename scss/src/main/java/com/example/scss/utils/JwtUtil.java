package com.example.scss.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${JWT_SECRET}")
    private String secretString;
    @Value("${JWT_ACCESS_EXPIRATION:900000}")
    private long accessExpiration;
    @Value("${JWT_REFRESH_EXPIRATION:604800000}")
    private long refreshExpiration;
    //字符串密钥变成签名密钥
    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }
    //生成token
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .claim("tokenType", "ACCESS")//标记访问token
                .issuedAt(new Date())//签发时间
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getKey())//使用密钥签名
                .compact();//生成字符串
    }
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .claim("tokenType", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getKey())
                .compact();
    }
    //获取token
    public String getTokenType(String token) {
        return parseToken(token).get("tokenType", String.class);
    }
    //解析token
    public Claims parseToken(String token){
        return Jwts.parser()//创建 JWT 解析器
                .verifyWith(getKey())//指定验证签名使用的密钥
                .build()//构建解析器
                .parseSignedClaims(token)//解析并验证 Token
                .getPayload();//获取 Token 中的载荷信息
    }
    public String getUsernameFromToken(String token){
        return parseToken(token).getSubject();
    }



}