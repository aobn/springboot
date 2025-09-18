package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.demo.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件名：Admin.java
 * 功能：管理员实体类，对应数据库admin表
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role = UserRole.ADMIN.name(); // 默认为普通管理员
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}