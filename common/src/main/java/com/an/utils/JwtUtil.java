package com.an.utils;

import com.an.constant.JwtClaimsConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /**
     * 生成 JWT
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                .setExpiration(exp);

        return builder.compact();
    }

    /**
     * 解析 JWT
     */
    public static Claims parseJWT(String secretKey, String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 token 中解析 userId（你当前项目的需求）
     */
    public static Integer getUserIdFromToken(String token, String secretKey) {
        try {
            Claims claims = parseJWT(secretKey, token);
            Object id = claims.get(JwtClaimsConstant.USER_ID);

            if (id instanceof Integer) {
                return (Integer) id;
            } else if (id instanceof Long) {
                return ((Long) id).intValue();
            } else if (id instanceof String) {
                return Integer.parseInt((String) id);
            }
            return null;

        } catch (Exception e) {
            return null; // token 无效或过期
        }
    }
}