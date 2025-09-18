package com.example.demo.service;

/**
 * 文件名：TokenBlacklistService.java
 * 功能：令牌黑名单服务，用于管理已失效的JWT令牌
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public interface TokenBlacklistService {
    
    /**
     * 将令牌添加到黑名单
     * @param token JWT令牌
     * @param expirationTime 令牌过期时间（毫秒时间戳）
     */
    void addToBlacklist(String token, long expirationTime);
    
    /**
     * 检查令牌是否在黑名单中
     * @param token JWT令牌
     * @return 如果令牌在黑名单中返回true，否则返回false
     */
    boolean isBlacklisted(String token);
    
    /**
     * 清理过期的黑名单令牌
     * 此方法应定期调用，以防止黑名单无限增长
     */
    void cleanupExpiredTokens();
}