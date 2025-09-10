package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.demo.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件名：User.java
 * 功能：用户实体类，对应数据库user表
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 * 
 * @Data - 自动生成getter、setter、equals、hashCode和toString方法
 * @NoArgsConstructor - 自动生成无参构造函数
 * @AllArgsConstructor - 自动生成全参构造函数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role = UserRole.USER.name(); // 默认为普通用户
    private Integer domNum = 2; // 可注册域名数量，默认为2个
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
