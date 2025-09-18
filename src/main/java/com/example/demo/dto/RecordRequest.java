package com.example.demo.dto;

import lombok.Data;

/**
 * 文件名：RecordRequest.java
 * 功能：DNSPod解析记录请求数据传输对象
 * 作者：CodeBuddy
 * 创建时间：2025-08-16
 * 版本：v1.0.0
 * 备注：支持解析记录的增删改查操作参数
 */
@Data
public class RecordRequest {
    
    // 基础参数
    private String domain;          // 域名（必填）
    private Integer domainId;       // 域名ID（可选）
    private Long recordId;          // 记录ID（修改/删除时必填）
    
    // 记录信息
    private String recordType;      // 记录类型（必填，如A、CNAME等）
    private String value;           // 记录值（必填，如IP地址）
    private String recordLine;      // 记录线路（可选，默认"默认"）
    private String recordLineId;    // 线路ID（可选，优先级比recordLine高）
    private String subDomain;       // 主机记录（可选，如www）
    
    // 查询参数
    private Integer groupId;        // 分组ID（可选）
    private String keyword;         // 关键字搜索（可选，支持搜索主机头和记录值）
    private String sortField;       // 排序字段（可选，支持name,line,type,value,weight,mx,ttl,updated_on）
    private String sortType;        // 排序方式（可选，ASC或DESC，默认ASC）
    private Integer offset;         // 偏移量（可选，默认0）
    private Integer limit;          // 限制数量（可选，默认100，最大3000）
    
    // 可选参数
    private Long ttl;               // TTL值（可选，默认600）
    private Long mx;                // MX优先级（可选）
    private Long weight;            // 权重（可选）
    private String status;          // 记录状态（可选，默认ENABLE）
    private String remark;          // 备注（可选）
    
}
