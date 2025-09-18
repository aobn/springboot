package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.Data;

/**
 * 文件名：LoginResult.java
 * 功能：用户登录结果DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-14
 * 版本：v1.0.0
 */
@Data
public class LoginResult {
    
    /**
     * 登录是否成功
     */
    private boolean success;
    
    /**
     * 用户信息（登录成功时返回）
     */
    private User user;
    
    /**
     * 是否被封禁
     */
    private boolean banned;
    
    /**
     * 封禁原因
     */
    private String banReason;
    
    /**
     * 错误消息
     */
    private String errorMessage;
    
    /**
     * 创建成功登录结果
     */
    public static LoginResult success(User user) {
        LoginResult result = new LoginResult();
        result.setSuccess(true);
        result.setUser(user);
        result.setBanned(false);
        return result;
    }
    
    /**
     * 创建封禁登录结果
     */
    public static LoginResult banned(String banReason) {
        LoginResult result = new LoginResult();
        result.setSuccess(false);
        result.setBanned(true);
        result.setBanReason(banReason);
        result.setErrorMessage("账户已被封禁，无法登录");
        return result;
    }
    
    /**
     * 创建失败登录结果
     */
    public static LoginResult failure(String errorMessage) {
        LoginResult result = new LoginResult();
        result.setSuccess(false);
        result.setBanned(false);
        result.setErrorMessage(errorMessage);
        return result;
    }
}