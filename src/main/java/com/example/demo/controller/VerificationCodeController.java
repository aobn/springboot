package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.VerificationCodeRequest;
import com.example.demo.dto.VerifyCodeRequest;
import com.example.demo.entity.VerificationCode;
import com.example.demo.service.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件名：VerificationCodeController.java
 * 功能：验证码控制器，处理验证码相关的API请求
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@RestController
@RequestMapping("/api/verification")
public class VerificationCodeController {

    private static final Logger logger = LoggerFactory.getLogger(VerificationCodeController.class);
    
    // IP限制：每分钟只能发送一次验证码
    private static final int RATE_LIMIT_MINUTES = 1;
    
    // 存储IP地址和最后发送时间的映射
    private final Map<String, LocalDateTime> ipLastSendTime = new ConcurrentHashMap<>();
    
    @Autowired
    private VerificationCodeService verificationCodeService;
    
    /**
     * 发送邮箱验证码
     * @param request 包含邮箱地址的请求
     * @param httpRequest HTTP请求对象，用于获取客户端IP
     * @return API响应，包含发送结果
     */
    @PostMapping("/send-code")
    public ApiResponse<Map<String, Object>> sendVerificationCode(@RequestBody VerificationCodeRequest request, 
                                                                HttpServletRequest httpRequest) {
        logger.info("收到发送验证码请求: {}", request);
        
        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            return ApiResponse.error(400, "邮箱地址不能为空");
        }
        
        // 获取客户端IP地址
        String clientIp = getClientIpAddress(httpRequest);
        logger.info("客户端IP: {}", clientIp);
        
        // 检查IP限制
        if (!checkRateLimit(clientIp)) {
            logger.warn("IP {} 发送验证码过于频繁，被限制", clientIp);
            return ApiResponse.error(429, "发送验证码过于频繁，请稍后再试");
        }
        
        try {
            // 生成并发送验证码
            VerificationCode verificationCode = verificationCodeService.generateAndSendCode(email);
            
            // 记录IP发送时间
            ipLastSendTime.put(clientIp, LocalDateTime.now());
            
            // 构建响应数据（不返回验证码本身，仅返回必要信息）
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("email", email);
            responseData.put("expireTime", verificationCode.getExpireTime());
            
            return ApiResponse.success("验证码已发送至邮箱: " + email, responseData);
        } catch (Exception e) {
            logger.error("发送验证码失败: {}", e.getMessage(), e);
            return ApiResponse.error(500, "发送验证码失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证邮箱验证码
     * @param request 包含邮箱和验证码的请求
     * @return API响应，包含验证结果
     */
    @PostMapping("/verify-code")
    public ApiResponse<Boolean> verifyCode(@RequestBody VerifyCodeRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        
        logger.info("收到验证码验证请求: email={}", email);
        
        if (email == null || email.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            return ApiResponse.error(400, "邮箱地址和验证码不能为空");
        }
        
        boolean isValid = verificationCodeService.verifyCode(email, code);
        
        if (isValid) {
            // 验证成功后标记验证码为已使用
            verificationCodeService.markCodeAsUsed(email, code);
            return ApiResponse.success("验证码验证成功", true);
        } else {
            return ApiResponse.error(400, "验证码无效或已过期", false);
        }
    }
    
    /**
     * 检查IP发送频率限制
     * @param clientIp 客户端IP地址
     * @return true表示可以发送，false表示被限制
     */
    private boolean checkRateLimit(String clientIp) {
        LocalDateTime lastSendTime = ipLastSendTime.get(clientIp);
        if (lastSendTime == null) {
            return true; // 首次发送
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime allowedTime = lastSendTime.plusMinutes(RATE_LIMIT_MINUTES);
        
        return now.isAfter(allowedTime);
    }
    
    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        // 尝试从各种代理头中获取真实IP
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For可能包含多个IP，取第一个
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        // 如果没有代理，直接获取远程地址
        return request.getRemoteAddr();
    }
}