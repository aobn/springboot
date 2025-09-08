package com.example.demo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户修改密码请求DTO
 * 
 * @Data - 自动生成getter、setter、toString、equals、hashCode方法
 */
@Data
public class UserChangePasswordRequest {
    
    /**
     * 原密码
     * @NotBlank - 不能为空或空白字符串
     */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    /**
     * 新密码
     * @NotBlank - 不能为空或空白字符串
     * @Size - 密码长度限制8-32字符
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 32, message = "新密码长度必须在8-32字符之间")
    private String newPassword;
    
    /**
     * 确认新密码
     * @NotBlank - 不能为空或空白字符串
     */
    @NotBlank(message = "确认新密码不能为空")
    private String confirmPassword;
}