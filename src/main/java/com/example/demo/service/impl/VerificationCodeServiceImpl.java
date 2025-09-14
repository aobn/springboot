package com.example.demo.service.impl;

import com.example.demo.entity.VerificationCode;
import com.example.demo.service.ExternalMailService;
import com.example.demo.service.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 文件名：VerificationCodeServiceImpl.java
 * 功能：验证码服务实现类，负责生成、发送和验证邮箱验证码
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationCodeServiceImpl.class);
    
    // 验证码有效期（分钟）
    private static final int EXPIRE_MINUTES = 5;
    
    // 验证码长度
    private static final int CODE_LENGTH = 6;
    
    // 使用内存存储验证码（实际项目中应该使用数据库或Redis）
    private final Map<String, VerificationCode> codeMap = new HashMap<>();
    
    @Autowired
    private ExternalMailService externalMailService;
    
    /**
     * 生成并发送验证码到指定邮箱
     * @param email 目标邮箱地址
     * @return 生成的验证码实体
     */
    @Override
    public VerificationCode generateAndSendCode(String email) {
        // 生成随机验证码
        String code = generateRandomCode(CODE_LENGTH);
        
        // 设置验证码有效期
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusMinutes(EXPIRE_MINUTES);
        
        // 创建验证码实体
        VerificationCode verificationCode = new VerificationCode(email, code, now, expireTime);
        
        // 存储验证码（实际项目中应该保存到数据库或Redis）
        codeMap.put(email, verificationCode);
        
        // 发送验证码邮件
        boolean sendResult = externalMailService.sendVerificationCode(email, code);
        if (!sendResult) {
            logger.error("验证码邮件发送失败: {}", email);
            throw new RuntimeException("验证码邮件发送失败");
        }
        
        logger.info("验证码已生成并发送至邮箱: {}, 验证码: {}", email, code);
        
        return verificationCode;
    }
    
    /**
     * 验证邮箱验证码是否有效
     * @param email 邮箱地址
     * @param code 验证码
     * @return 验证结果，true表示验证通过，false表示验证失败
     */
    @Override
    public boolean verifyCode(String email, String code) {
        VerificationCode storedCode = codeMap.get(email);
        
        if (storedCode == null) {
            logger.warn("未找到邮箱 {} 的验证码记录", email);
            return false;
        }
        
        if (storedCode.isExpired()) {
            logger.warn("邮箱 {} 的验证码已过期", email);
            return false;
        }
        
        if (storedCode.getUsed()) {
            logger.warn("邮箱 {} 的验证码已被使用", email);
            return false;
        }
        
        boolean isValid = storedCode.getCode().equals(code);
        if (!isValid) {
            logger.warn("邮箱 {} 提供的验证码不正确", email);
        }
        
        return isValid;
    }
    
    /**
     * 标记验证码为已使用
     * @param email 邮箱地址
     * @param code 验证码
     */
    @Override
    public void markCodeAsUsed(String email, String code) {
        VerificationCode storedCode = codeMap.get(email);
        if (storedCode != null && storedCode.getCode().equals(code)) {
            storedCode.setUsed(true);
            logger.info("邮箱 {} 的验证码已标记为已使用", email);
        }
    }
    
    /**
     * 生成指定长度的随机数字验证码
     * @param length 验证码长度
     * @return 生成的随机验证码
     */
    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    

}