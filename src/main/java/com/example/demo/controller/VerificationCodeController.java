package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.CaptchaRequest;
import com.example.demo.dto.CaptchaResponse;
import com.example.demo.dto.VerificationCodeRequest;
import com.example.demo.dto.VerifyCodeRequest;
import com.example.demo.entity.VerificationCode;
import com.example.demo.service.VerificationCodeService;
import com.example.demo.util.CaptchaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
    
    @Autowired
    private VerificationCodeService verificationCodeService;
    
    @Autowired
    private CaptchaUtil captchaUtil;
    
    /**
     * 发送邮箱验证码
     * @param request 包含邮箱地址的请求
     * @return API响应，包含发送结果
     */
    @PostMapping("/send-code")
    public ApiResponse<Map<String, Object>> sendVerificationCode(@RequestBody VerificationCodeRequest request) {
        logger.info("收到发送验证码请求: {}", request);
        
        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            return ApiResponse.error(400, "邮箱地址不能为空");
        }
        
        try {
            // 生成并发送验证码
            VerificationCode verificationCode = verificationCodeService.generateAndSendCode(email);
            
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
     * 生成图片验证码
     * @return API响应，包含验证码ID和Base64编码的图片
     */
    @GetMapping("/captcha")
    public ApiResponse<CaptchaResponse> generateCaptcha() {
        logger.info("收到生成图片验证码请求");
        
        try {
            CaptchaUtil.CaptchaResult result = captchaUtil.generateCaptcha();
            
            CaptchaResponse response = new CaptchaResponse(
                result.getCaptchaId(),
                result.getImageBase64(),
                result.getExpireTime()
            );
            
            return ApiResponse.success("图片验证码生成成功", response);
        } catch (Exception e) {
            logger.error("生成图片验证码失败: {}", e.getMessage(), e);
            return ApiResponse.error(500, "生成图片验证码失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证图片验证码
     * @param request 包含验证码ID和用户输入的验证码
     * @return API响应，包含验证结果
     */
    @PostMapping("/verify-captcha")
    public ApiResponse<Boolean> verifyCaptcha(@RequestBody CaptchaRequest request) {
        String captchaId = request.getCaptchaId();
        String code = request.getCode();
        
        logger.info("收到图片验证码验证请求: captchaId={}", captchaId);
        
        if (captchaId == null || captchaId.trim().isEmpty() || 
            code == null || code.trim().isEmpty()) {
            return ApiResponse.error(400, "验证码ID和验证码不能为空", false);
        }
        
        boolean isValid = captchaUtil.verifyCaptcha(captchaId, code);
        
        if (isValid) {
            return ApiResponse.success("图片验证码验证成功", true);
        } else {
            return ApiResponse.error(400, "图片验证码无效或已过期", false);
        }
    }
}