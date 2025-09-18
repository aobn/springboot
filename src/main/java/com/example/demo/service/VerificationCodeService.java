package com.example.demo.service;

import com.example.demo.entity.VerificationCode;

/**
 * 文件名：VerificationCodeService.java
 * 功能：验证码服务接口，提供验证码生成、发送、验证等功能
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public interface VerificationCodeService {
    
    /**
     * 生成并发送验证码到指定邮箱
     * @param email 目标邮箱地址
     * @return 生成的验证码实体
     */
    VerificationCode generateAndSendCode(String email);
    
    /**
     * 验证邮箱验证码是否有效
     * @param email 邮箱地址
     * @param code 验证码
     * @return 验证结果，true表示验证通过，false表示验证失败
     */
    boolean verifyCode(String email, String code);
    
    /**
     * 标记验证码为已使用
     * @param email 邮箱地址
     * @param code 验证码
     */
    void markCodeAsUsed(String email, String code);
}