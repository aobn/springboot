package com.example.demo.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 文件名：JwtAuthenticationException.java
 * 功能：JWT认证异常类，用于处理JWT认证过程中的异常
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public class JwtAuthenticationException extends AuthenticationException {
    
    private static final long serialVersionUID = 1L;
    
    public JwtAuthenticationException(String msg) {
        super(msg);
    }
    
    public JwtAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}