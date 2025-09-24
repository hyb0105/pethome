package com.example.PetHome.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtils {
    // 过期时间
    private static final long EXPIRE = 60 * 60 * 24 * 1000L; // 1天
    // 签名密钥
    private static final String SECRET = "your_secret_key_for_jwt";

    // 生成token
    public static String createToken(String username, Integer role) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE);
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    // 解析token
    public static Claims getClaimsByToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}