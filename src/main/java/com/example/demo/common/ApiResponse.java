package com.example.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统一API响应结构
 * @author CodeBuddy
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /**
     * 业务状态码
     */
    private int code;
    
    /**
     * 响应描述信息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 服务器响应时间戳（ISO 8601格式）
     */
    private String timestamp;
    
    /**
     * 创建成功响应（状态码200）
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(200, "操作成功", data, getCurrentTimestamp());
    }
    
    /**
     * 创建成功响应（状态码200），自定义消息
     * @param message 成功消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>(200, message, data, getCurrentTimestamp());
    }
    
    /**
     * 创建资源创建成功响应（状态码201）
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<T>(201, "创建成功", data, getCurrentTimestamp());
    }
    
    /**
     * 创建错误响应
     * @param code 错误状态码
     * @param message 错误消息
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<T>(code, message, null, getCurrentTimestamp());
    }
    
    /**
     * 创建错误响应，带数据
     * @param code 错误状态码
     * @param message 错误消息
     * @param data 错误详情数据
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<T>(code, message, data, getCurrentTimestamp());
    }
    
    /**
     * 请求参数错误响应（状态码400）
     * @param message 错误消息
     * @return API响应对象
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<T>(400, message, null, getCurrentTimestamp());
    }
    
    /**
     * 未授权响应（状态码401）
     * @return API响应对象
     */
    public static <T> ApiResponse<T> unauthorized() {
        return new ApiResponse<T>(401, "未授权，请先登录", null, getCurrentTimestamp());
    }
    
    /**
     * 禁止访问响应（状态码403）
     * @return API响应对象
     */
    public static <T> ApiResponse<T> forbidden() {
        return new ApiResponse<T>(403, "无权限访问该资源", null, getCurrentTimestamp());
    }
    
    /**
     * 资源不存在响应（状态码404）
     * @param resourceName 资源名称
     * @return API响应对象
     */
    public static <T> ApiResponse<T> notFound(String resourceName) {
        return new ApiResponse<T>(404, resourceName + "不存在", null, getCurrentTimestamp());
    }
    
    /**
     * 资源冲突响应（状态码409）
     * @param message 冲突描述
     * @return API响应对象
     */
    public static <T> ApiResponse<T> conflict(String message) {
        return new ApiResponse<T>(409, message, null, getCurrentTimestamp());
    }
    
    /**
     * 服务器错误响应（状态码500）
     * @return API响应对象
     */
    public static <T> ApiResponse<T> serverError() {
        return new ApiResponse<T>(500, "服务器内部错误", null, getCurrentTimestamp());
    }
    
    /**
     * 获取当前时间戳，ISO 8601格式
     * @return 格式化的时间字符串
     */
    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
}