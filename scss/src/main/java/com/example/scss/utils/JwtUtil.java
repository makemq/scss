package com.example.scss.utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Component
public class JwtUtil {
    private static final String SECRET_STRING = "scss-secret-key-2024-very-long-and-secure-123456";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // 设置主题（存什么都可以，比如用户名或用户ID）
                .issuedAt(new Date()) // 设置签发时间
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                .signWith(KEY) // 使用密钥签名 (0.12.x 新写法，自动识别 HS256)
                .compact(); // 生成最终字符串
    }
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY) // 使用密钥验证签名
                .build()
                .parseSignedClaims(token) // 解析
                .getPayload(); // 获取载荷部分
    }
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            // 如果解析失败（过期、伪造等），返回 null
            return null;
        }


}}
