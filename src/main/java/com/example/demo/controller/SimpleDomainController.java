package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.mapper.DomainMapper;

import java.util.List;

/**
 * 文件名：SimpleDomainController.java
 * 功能：简化的域名控制器，用于测试域名后缀接口
 * 作者：CodeBuddy
 * 创建时间：2025-08-21
 * 版本：v1.0.0
 */
@RestController
@RequestMapping("/api/simple")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class SimpleDomainController {
    
    @Autowired
    private DomainMapper domainMapper;
    
    /**
     * 获取可用的域名后缀列表
     * 
     * @return 域名名称列表
     */
    @GetMapping("/domain-suffixes")
    public List<String> getAvailableDomainSuffixes() {
        return domainMapper.findAvailableDomainSuffixes();
    }
    
    /**
     * 测试接口连通性
     * 
     * @return 测试消息
     */
    @GetMapping("/test")
    public String test() {
        return "Simple Domain Controller is working!";
    }
}