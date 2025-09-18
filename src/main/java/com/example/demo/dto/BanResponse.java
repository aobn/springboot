package com.example.demo.dto;

import lombok.Data;

/**
 * 文件名：BanResponse.java
 * 功能：用户封禁响应DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-14
 * 版本：v1.0.0
 */
@Data
public class BanResponse {
    
    /**
     * 封禁原因
     */
    private String banReason;
    
    public BanResponse(String banReason) {
        this.banReason = banReason;
    }
}