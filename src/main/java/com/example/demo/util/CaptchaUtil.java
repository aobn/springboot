package com.example.demo.util;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件名：CaptchaUtil.java
 * 功能：图片验证码工具类，提供验证码生成、验证等功能
 * 作者：CodeBuddy
 * 创建时间：2025-09-03
 * 版本：v1.0.0
 */
@Component
public class CaptchaUtil {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaUtil.class);
    
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    
    // 验证码存储（实际项目中应使用Redis）
    private final Map<String, CaptchaInfo> captchaStore = new ConcurrentHashMap<>();
    
    // 验证码有效期（分钟）
    private static final int EXPIRE_MINUTES = 5;
    
    /**
     * 验证码信息内部类
     */
    public static class CaptchaInfo {
        private String code;
        private LocalDateTime createTime;
        private LocalDateTime expireTime;
        private boolean used;
        
        public CaptchaInfo(String code, LocalDateTime createTime, LocalDateTime expireTime) {
            this.code = code;
            this.createTime = createTime;
            this.expireTime = expireTime;
            this.used = false;
        }
        
        public String getCode() {
            return code;
        }
        
        public LocalDateTime getCreateTime() {
            return createTime;
        }
        
        public LocalDateTime getExpireTime() {
            return expireTime;
        }
        
        public boolean isUsed() {
            return used;
        }
        
        public void setUsed(boolean used) {
            this.used = used;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expireTime);
        }
    }
    
    /**
     * 验证码响应结果类
     */
    public static class CaptchaResult {
        private String captchaId;
        private String imageBase64;
        private LocalDateTime expireTime;
        
        public CaptchaResult(String captchaId, String imageBase64, LocalDateTime expireTime) {
            this.captchaId = captchaId;
            this.imageBase64 = imageBase64;
            this.expireTime = expireTime;
        }
        
        public String getCaptchaId() {
            return captchaId;
        }
        
        public String getImageBase64() {
            return imageBase64;
        }
        
        public LocalDateTime getExpireTime() {
            return expireTime;
        }
    }
    
    /**
     * 生成图片验证码
     * @return 验证码结果，包含验证码ID和Base64编码的图片
     */
    public CaptchaResult generateCaptcha() {
        try {
            // 生成验证码文本
            String captchaText = defaultKaptcha.createText();
            
            // 生成验证码图片
            BufferedImage captchaImage = defaultKaptcha.createImage(captchaText);
            
            // 将图片转换为Base64编码
            String imageBase64 = imageToBase64(captchaImage);
            
            // 生成唯一的验证码ID
            String captchaId = generateCaptchaId();
            
            // 设置过期时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expireTime = now.plusMinutes(EXPIRE_MINUTES);
            
            // 存储验证码信息
            CaptchaInfo captchaInfo = new CaptchaInfo(captchaText.toUpperCase(), now, expireTime);
            captchaStore.put(captchaId, captchaInfo);
            
            logger.info("生成图片验证码成功，ID: {}, 验证码: {}", captchaId, captchaText);
            
            return new CaptchaResult(captchaId, "data:image/png;base64," + imageBase64, expireTime);
            
        } catch (Exception e) {
            logger.error("生成图片验证码失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成图片验证码失败", e);
        }
    }
    
    /**
     * 验证图片验证码
     * @param captchaId 验证码ID
     * @param userInput 用户输入的验证码
     * @return 验证结果
     */
    public boolean verifyCaptcha(String captchaId, String userInput) {
        if (captchaId == null || userInput == null) {
            logger.warn("验证码ID或用户输入为空");
            return false;
        }
        
        CaptchaInfo captchaInfo = captchaStore.get(captchaId);
        if (captchaInfo == null) {
            logger.warn("未找到验证码ID: {}", captchaId);
            return false;
        }
        
        if (captchaInfo.isExpired()) {
            logger.warn("验证码已过期，ID: {}", captchaId);
            captchaStore.remove(captchaId); // 清理过期验证码
            return false;
        }
        
        if (captchaInfo.isUsed()) {
            logger.warn("验证码已被使用，ID: {}", captchaId);
            return false;
        }
        
        boolean isValid = captchaInfo.getCode().equalsIgnoreCase(userInput.trim());
        if (isValid) {
            captchaInfo.setUsed(true);
            logger.info("验证码验证成功，ID: {}", captchaId);
        } else {
            logger.warn("验证码验证失败，ID: {}, 期望: {}, 实际: {}", 
                       captchaId, captchaInfo.getCode(), userInput);
        }
        
        return isValid;
    }
    
    /**
     * 验证图片验证码并返回详细的验证结果
     * @param captchaId 验证码ID
     * @param userInput 用户输入的验证码
     * @return 验证结果：SUCCESS-成功，INVALID_CODE-验证码错误，EXPIRED-已过期，NOT_FOUND-未找到，USED-已使用
     */
    public String validateCaptcha(String captchaId, String userInput) {
        if (captchaId == null || userInput == null) {
            logger.warn("验证码ID或用户输入为空");
            return "NOT_FOUND";
        }
        
        CaptchaInfo captchaInfo = captchaStore.get(captchaId);
        if (captchaInfo == null) {
            logger.warn("未找到验证码ID: {}", captchaId);
            return "NOT_FOUND";
        }
        
        if (captchaInfo.isExpired()) {
            logger.warn("验证码已过期，ID: {}", captchaId);
            captchaStore.remove(captchaId); // 清理过期验证码
            return "EXPIRED";
        }
        
        if (captchaInfo.isUsed()) {
            logger.warn("验证码已被使用，ID: {}", captchaId);
            return "USED";
        }
        
        boolean isValid = captchaInfo.getCode().equalsIgnoreCase(userInput.trim());
        if (isValid) {
            captchaInfo.setUsed(true);
            logger.info("验证码验证成功，ID: {}", captchaId);
            return "SUCCESS";
        } else {
            logger.warn("验证码验证失败，ID: {}, 期望: {}, 实际: {}", 
                       captchaId, captchaInfo.getCode(), userInput);
            return "INVALID_CODE";
        }
    }
    
    /**
     * 清理过期的验证码
     */
    public void cleanExpiredCaptcha() {
        captchaStore.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            if (expired) {
                logger.debug("清理过期验证码，ID: {}", entry.getKey());
            }
            return expired;
        });
    }
    
    /**
     * 将BufferedImage转换为Base64编码字符串
     * @param image 图片对象
     * @return Base64编码字符串
     * @throws IOException IO异常
     */
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
    
    /**
     * 生成唯一的验证码ID
     * @return 验证码ID
     */
    private String generateCaptchaId() {
        return "captcha_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }
}