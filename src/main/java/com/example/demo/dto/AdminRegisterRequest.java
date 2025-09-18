package com.example.demo.dto;

import lombok.Data;

/**
 * 文件名：AdminRegisterRequest.java
 * 功能：管理员注册请求DTO
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@Data
public class AdminRegisterRequest {
    private String username;
    private String password;
    private String email;
    private String role;
}