package com.example.demo.dto;

import lombok.Data;

/**
 * 文件名：CaptchaRequest.java
 * 功能：图片验证码验证请求DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-03
 * 版本：v1.0.0
 */
@Data
public class CaptchaRequest {
    
    /**
     * 验证码ID
     */
    private String captchaId;
    
    /**
     * 用户输入的验证码
     */
    private String code;
}