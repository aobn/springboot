package com.example.demo.common;

import lombok.Getter;

/**
 * 资源未找到异常
 * @author CodeBuddy
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * 创建资源未找到异常
     * @param resourceName 资源名称
     */
    public ResourceNotFoundException(String resourceName) {
        super(resourceName);
    }
}