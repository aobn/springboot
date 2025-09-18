package com.example.demo.service.impl;

import com.example.demo.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件名：TokenBlacklistServiceImpl.java
 * 功能：令牌黑名单服务实现类，管理已失效的JWT令牌
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@Service
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    
    // 使用ConcurrentHashMap存储黑名单令牌及其过期时间
    private final Map<String, Long> tokenBlacklist = new ConcurrentHashMap<>();
    
    @Override
    public void addToBlacklist(String token, long expirationTime) {
        tokenBlacklist.put(token, expirationTime);
        log.info("令牌已添加到黑名单，过期时间: {}", expirationTime);
    }
    
    @Override
    public boolean isBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }
    
    /**
     * 定时清理过期的黑名单令牌
     * 每小时执行一次
     */
    @Override
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        int count = 0;
        
        // 遍历黑名单，移除已过期的令牌
        for (Map.Entry<String, Long> entry : tokenBlacklist.entrySet()) {
            if (entry.getValue() < currentTime) {
                tokenBlacklist.remove(entry.getKey());
                count++;
            }
        }
        
        if (count > 0) {
            log.info("已清理 {} 个过期的黑名单令牌", count);
        }
    }
}