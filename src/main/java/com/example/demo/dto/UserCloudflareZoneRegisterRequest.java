package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文件名：UserCloudflareZoneRegisterRequest.java
 * 功能：用户注册Cloudflare域名请求DTO
 * 作者：CodeBuddy
 * 创建时间：2025-09-26
 * 版本：v1.0.0
 */
@Data
public class UserCloudflareZoneRegisterRequest {
    
    /**
     * Cloudflare Zone ID
     */
    @NotBlank(message = "Zone ID不能为空")
    private String zoneId;
    
    /**
     * 备注信息(可选)
     */
    private String remark;
}