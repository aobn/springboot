package com.example.demo.service;

/**
 * 文件名：ExternalMailService.java
 * 功能：外部邮件服务接口，用于调用第三方邮件发送API
 * 作者：CodeBuddy
 * 创建时间：2025-09-15
 * 版本：v1.0.0
 */
public interface ExternalMailService {
    
    /**
     * 发送验证码邮件
     * @param recipient 收件人邮箱地址
     * @param verificationCode 验证码
     * @return 发送结果，true表示成功，false表示失败
     */
    boolean sendVerificationCode(String recipient, String verificationCode);
}