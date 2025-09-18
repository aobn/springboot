package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 文件名：JwtUtil.java
 * 功能：JWT工具类，用于生成和验证JWT令牌
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@Component
public class JwtUtil {

    // 默认密钥，实际应用中应该从配置文件中读取
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // 令牌有效期（毫秒）
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000; // 24小时
    
    /**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        String subject = getClaimFromToken(token, Claims::getSubject);
        return Long.parseLong(subject);
    }
    
    /**
     * 从令牌中获取用户邮箱
     * @param token JWT令牌
     * @return 用户邮箱
     */
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("email", String.class));
    }
    
    /**
     * 从令牌中获取用户角色
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }
    
    /**
     * 从令牌中获取过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * 从令牌中获取指定的声明
     * @param token JWT令牌
     * @param claimsResolver 声明解析函数
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 从令牌中获取所有声明
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 检查令牌是否已过期
     * @param token JWT令牌
     * @return 是否已过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * 为指定用户生成令牌，包含完整用户信息
     * 这是唯一的令牌生成方法，确保所有令牌都包含完整的用户信息
     * @param userId 用户ID
     * @param email 用户邮箱
     * @param role 用户角色
     * @return JWT令牌
     */
    public String generateToken(Long userId, String email, String role) {
        // 验证必要参数
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("用户邮箱不能为空");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("用户角色不能为空");
        }
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        return doGenerateToken(claims, userId.toString());
    }
    
    /**
     * 为指定用户生成令牌，可包含额外的声明
     * @param claims 额外的声明
     * @param subject 用户ID（字符串形式）
     * @return JWT令牌
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SECRET_KEY)
                .compact();
    }
    
    /**
     * 验证令牌
     * @param token JWT令牌
     * @param userId 用户ID
     * @return 是否有效
     */
    public Boolean validateToken(String token, Long userId) {
        final Long tokenUserId = getUserIdFromToken(token);
        return (tokenUserId.equals(userId) && !isTokenExpired(token));
    }

    /**
     * 验证令牌是否有效（仅检查过期时间）
     * @param token JWT令牌
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
