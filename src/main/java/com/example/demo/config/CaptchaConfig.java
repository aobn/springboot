package com.example.demo.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 文件名：CaptchaConfig.java
 * 功能：图片验证码配置类，配置Kaptcha验证码生成器
 * 作者：CodeBuddy
 * 创建时间：2025-09-03
 * 版本：v1.0.0
 */
@Configuration
public class CaptchaConfig {

    /**
     * 配置图片验证码生成器
     * @return DefaultKaptcha实例
     */
    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // 图片边框
        properties.setProperty("kaptcha.border", "yes");
        // 边框颜色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        // 图片宽度
        properties.setProperty("kaptcha.image.width", "110");
        // 图片高度
        properties.setProperty("kaptcha.image.height", "40");
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        // session key
        properties.setProperty("kaptcha.session.key", "code");
        // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 字体
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        // 验证码字符范围
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // 干扰线颜色
        properties.setProperty("kaptcha.noise.color", "white");
        // 干扰实现类
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
        // 图片样式
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        // 文字间隔
        properties.setProperty("kaptcha.textproducer.char.space", "3");
        
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        
        return defaultKaptcha;
    }
}