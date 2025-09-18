package com.example.demo.dto;

import com.example.demo.entity.Admin;

import lombok.Data;

/**
 * 文件名：AdminLoginResponse.java
 * 功能：管理员登录响应DTO
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@Data
public class AdminLoginResponse {
    private Admin admin;
    private String token;
}