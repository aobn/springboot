package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 删除DNS解析记录请求DTO
 * 用于接收删除DNS记录的请求参数
 * 
 * @author CodeBuddy
 * @since 2025-08-29
 */
@Data
public class DeleteDnsRecordRequest {
    
    /**
     * 要删除的DNS解析记录ID
     */
    @NotNull(message = "记录ID不能为空")
    private Long recordId;
}