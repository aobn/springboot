package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.mapper.DomainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件名：DomainController.java
 * 功能：域名控制器，提供域名相关API接口
 * 作者：CodeBuddy
 * 创建时间：2025-08-22
 * 版本：v1.0.0
 */
@RestController
@RequestMapping("/api/domains")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class DomainController {
    
    @Autowired
    private DomainMapper domainMapper;
    
    /**
     * 获取可用的域名后缀列表
     * 
     * @return 统一响应格式，包含域名后缀列表
     */
    @GetMapping("/suffixes")
    public ApiResponse<List<String>> getDomainSuffixes() {
        try {
            List<String> suffixes = domainMapper.findAvailableDomainSuffixes();
            return ApiResponse.success("获取域名后缀成功", suffixes);
        } catch (Exception e) {
            return ApiResponse.error(500, "系统繁忙，请稍后重试");
        }
    }
}