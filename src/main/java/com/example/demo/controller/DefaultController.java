package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 默认控制器 - 处理未实现的请求和错误页面
 * 
 * @author CodeBuddy
 * @date 2025-09-05
 */
@RestController
public class DefaultController implements ErrorController {

    /**
     * 处理根路径访问
     * 
     * @return API响应
     */
    @RequestMapping("/")
    public ResponseEntity<ApiResponse<String>> handleRoot() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "请求的资源不存在"));
    }

    /**
     * 处理错误页面
     * 
     * @param request HTTP请求
     * @return API响应
     */
    @RequestMapping("/error")
    public ResponseEntity<ApiResponse<String>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            switch (statusCode) {
                case 404:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(404, "请求的资源不存在"));
                case 405:
                    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                            .body(ApiResponse.error(405, "请求方法不被允许"));
                case 500:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.error(500, "服务器内部错误"));
                default:
                    return ResponseEntity.status(statusCode)
                            .body(ApiResponse.error(statusCode, "请求处理失败"));
            }
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "未知错误"));
    }

    /**
     * 处理所有未匹配的路径
     * 
     * @return API响应
     */
    @RequestMapping("/**")
    public ResponseEntity<ApiResponse<String>> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "请求的资源不存在"));
    }
}