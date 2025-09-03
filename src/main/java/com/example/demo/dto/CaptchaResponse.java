package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文件名：CaptchaResponse.java
 * 功能：图片验证码响应DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-03
 * 版本：v1.0.0
 */
@Data
public class CaptchaResponse {
    
    /**
     * 验证码ID，用于后续验证
     */
    private String captchaId;
    
    /**
     * Base64编码的验证码图片
     */
    private String imageBase64;
    
    /**
     * 验证码过期时间
     */
    private LocalDateTime expireTime;
    
    public CaptchaResponse(String captchaId, String imageBase64, LocalDateTime expireTime) {
        this.captchaId = captchaId;
        this.imageBase64 = imageBase64;
        this.expireTime = expireTime;
    }
}