package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.BanResponse;
import com.example.demo.dto.LoginResult;
import com.example.demo.dto.UserForgotPasswordRequest;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserLoginResponse;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.dto.UserRegisterVerifyRequest;
import com.example.demo.entity.User;
import com.example.demo.service.TokenBlacklistService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 文件名：AuthController.java
 * 功能：认证控制器，处理用户注册、登录等认证相关请求
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    
    /**
     * 用户注册第一步：发送验证码
     * @param request 包含邮箱和密码的注册请求
     * @return 统一响应格式
     */
    @PostMapping("/register/step1")
    public ApiResponse<Void> registerStep1(@RequestBody UserRegisterRequest request) {
        log.info("收到用户注册第一步请求: {}", request);
        
        // 参数校验
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "邮箱不能为空");
        }
        
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "密码不能为空");
        }
        
        // 检查密码强度
        if (request.getPassword().length() < 8) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "密码长度不能少于8位");
        }
        
        // 检查邮箱是否已被注册
        User existingUser = userService.findUserByEmail(request.getEmail());
        if (existingUser != null) {
            return ApiResponse.error(HttpStatus.CONFLICT.value(), "该邮箱已被注册");
        }
        
        // 发送验证码
        boolean success = userService.registerStep1(request);
        
        if (success) {
            return ApiResponse.success("验证码已发送至您的邮箱，请查收", (Void) null);
        } else {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 用户注册第二步：验证验证码并完成注册
     * @param request 包含邮箱、密码和验证码的注册验证请求
     * @return 统一响应格式，包含注册成功的用户信息
     */
    @PostMapping("/register/step2")
    public ApiResponse<User> registerStep2(@RequestBody UserRegisterVerifyRequest request) {
        log.info("收到用户注册第二步请求: {}", request);
        
        // 参数校验
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "邮箱不能为空");
        }
        
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "密码不能为空");
        }
        
        if (request.getCode() == null || request.getCode().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "验证码不能为空");
        }
        
        // 验证验证码并完成注册
        User newUser = userService.registerStep2(request);
        
        if (newUser != null) {
            // 注册成功，返回用户信息（不包含密码）
            newUser.setPassword(null);
            return ApiResponse.success("注册成功", newUser);
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "注册失败，验证码无效或已过期");
        }
    }
    
    /**
     * 用户登录
     * @param request 包含邮箱和密码的登录请求
     * @return 统一响应格式，包含登录成功的用户信息和JWT令牌
     */
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody UserLoginRequest request) {
        log.info("收到用户登录请求: {}", request);
        
        // 参数校验
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "邮箱不能为空");
        }
        
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "密码不能为空");
        }
        
        // 验证用户凭据
        LoginResult loginResult = userService.login(request.getEmail(), request.getPassword());
        
        if (loginResult.isSuccess()) {
            User user = loginResult.getUser();
            // 生成JWT令牌，包含用户ID、邮箱和角色信息
            String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
            
            // 创建登录响应
            UserLoginResponse response = new UserLoginResponse(user, token);
            
            log.info("用户登录成功: {} (ID: {}, 角色: {})", user.getUsername(), user.getId(), user.getRole());
            return ApiResponse.success("登录成功", response);
        } else if (loginResult.isBanned()) {
            // 用户被封禁
            log.warn("用户登录失败: 账户已被封禁 - {}, 封禁原因: {}", request.getEmail(), loginResult.getBanReason());
            return ApiResponse.error(403, "账户已被封禁，无法登录", loginResult.getBanReason());
        } else {
            // 其他登录失败情况
            log.warn("用户登录失败: {}", request.getEmail());
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), loginResult.getErrorMessage());
        }
    }
    
    /**
     * 用户登出
     * @param authHeader 请求头中的Authorization值，包含JWT令牌
     * @return 统一响应格式
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("收到用户登出请求");
        
        // 检查Authorization头是否存在
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "未授权");
        }
        
        // 提取JWT令牌
        String token = authHeader.substring(7);
        
        try {
            // 从令牌中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 验证令牌有效性
            if (!jwtUtil.validateToken(token, userId)) {
                return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "无效的令牌");
            }
            
            // 获取令牌过期时间
            Date expirationDate = jwtUtil.getExpirationDateFromToken(token);
            long expirationTime = expirationDate.getTime();
            
            // 将令牌添加到黑名单
            tokenBlacklistService.addToBlacklist(token, expirationTime);
            
            log.info("用户登出成功");
            return ApiResponse.success("登出成功", null);
        } catch (Exception e) {
            log.error("登出过程中发生错误", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "登出失败");
        }
    }

    /**
     * 验证JWT令牌是否有效
     * @param authHeader 请求头中的Authorization值，包含JWT令牌
     * @return 统一响应格式，指示令牌是否有效
     */
    @GetMapping("/validate-token")
    public ApiResponse<String> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("收到验证令牌请求");

        // 检查Authorization头是否存在
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "未提供有效的Bearer令牌");
        }

        // 提取JWT令牌
        String token = authHeader.substring(7);

        try {
            // 验证令牌有效性
            if (jwtUtil.validateToken(token)) {
                // 检查令牌是否在黑名单中
                if (tokenBlacklistService.isBlacklisted(token)) {
                    return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "令牌已失效（已登出）");
                }
                log.info("令牌有效");
                return ApiResponse.success("令牌有效", "OK");
            } else {
                log.warn("令牌无效或已过期");
                return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "令牌无效或已过期");
            }
        } catch (Exception e) {
            log.error("验证令牌过程中发生错误", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "验证令牌失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户忘记密码修改密码
     * @param request 包含邮箱、验证码、新密码和确认密码的请求
     * @return 统一响应格式
     */
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody UserForgotPasswordRequest request) {
        log.info("收到用户忘记密码修改密码请求: email={}", request.getEmail());
        
        // 参数校验
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "邮箱不能为空");
        }
        
        if (request.getVerificationCode() == null || request.getVerificationCode().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "验证码不能为空");
        }
        
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "新密码不能为空");
        }
        
        if (request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "确认密码不能为空");
        }
        
        // 检查密码强度
        if (request.getNewPassword().length() < 8) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "密码长度不能少于8位");
        }
        
        // 检查新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "新密码和确认密码不一致");
        }
        
        try {
            // 执行忘记密码修改
            boolean success = userService.forgotPassword(request);
            
            if (success) {
                log.info("用户忘记密码修改成功: email={}", request.getEmail());
                return ApiResponse.success("密码修改成功", null);
            } else {
                return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "密码修改失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("用户忘记密码修改过程中发生错误: email={}", request.getEmail(), e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
