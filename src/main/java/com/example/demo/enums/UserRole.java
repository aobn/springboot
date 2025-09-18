package com.example.demo.enums;

/**
 * 文件名：UserRole.java
 * 功能：用户角色枚举类，定义系统中的用户角色类型
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public enum UserRole {
    USER("普通用户"),
    ADMIN("管理员"),
    SUPER_ADMIN("超级管理员");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}