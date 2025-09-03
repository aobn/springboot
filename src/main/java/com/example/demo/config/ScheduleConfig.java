package com.example.demo.config;

import com.example.demo.util.CaptchaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 文件名：ScheduleConfig.java
 * 功能：定时任务配置类，用于清理过期验证码等定时任务
 * 作者：CodeBuddy
 * 创建时间：2025-09-03
 * 版本：v1.0.0
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);
    
    @Autowired
    private CaptchaUtil captchaUtil;
    
    /**
     * 定时清理过期的图片验证码
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void cleanExpiredCaptcha() {
        logger.debug("开始清理过期的图片验证码");
        captchaUtil.cleanExpiredCaptcha();
        logger.debug("清理过期图片验证码完成");
    }
}