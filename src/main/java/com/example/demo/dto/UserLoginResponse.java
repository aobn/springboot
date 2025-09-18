package com.example.demo.dto;

import com.example.demo.entity.User;

/**
 * 文件名：UserLoginResponse.java
 * 功能：用户登录响应数据传输对象
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public class UserLoginResponse {
    
    private Long id;
    private String username;
    private String email;
    private String token;
    
    public UserLoginResponse() {
    }
    
    public UserLoginResponse(User user, String token) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.token = token;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}