package com.example.demo.common;

import com.example.demo.exception.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * @author CodeBuddy
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     * @param ex 方法参数校验异常
     * @return 统一API响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("参数校验失败: {}", errors);
        return ApiResponse.error(400, "参数验证失败，请检查输入", errors);
    }

    /**
     * 处理表单绑定异常
     * @param ex 表单绑定异常
     * @return 统一API响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<List<Map<String, String>>> handleBindExceptions(BindException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("field", error.getField());
                    errorMap.put("message", error.getDefaultMessage());
                    return errorMap;
                })
                .collect(Collectors.toList());
        log.warn("表单绑定失败: {}", errors);
        return ApiResponse.error(400, "参数验证失败，请检查输入", errors);
    }

    /**
     * 处理业务异常
     * @param ex 业务异常
     * @return 统一API响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        log.warn("业务异常: {}", ex.getMessage());
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理资源未找到异常
     * @param ex 资源未找到异常
     * @return 统一API响应
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("资源未找到: {}", ex.getMessage());
        return ApiResponse.notFound(ex.getMessage());
    }

    /**
     * 处理JWT认证异常
     * @param ex JWT认证异常
     * @return 统一API响应
     */
    @ExceptionHandler(JwtAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        log.warn("JWT认证异常: {}", ex.getMessage());
        return ApiResponse.error(401, "认证失败: " + ex.getMessage());
    }
    
    /**
     * 处理JWT签名异常
     * @param ex JWT签名异常
     * @return 统一API响应
     */
    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleJwtSignatureException(SignatureException ex) {
        log.warn("JWT签名验证失败: {}", ex.getMessage());
        return ApiResponse.error(401, "令牌无效或已过期");
    }
    
    /**
     * 处理JWT过期异常
     * @param ex JWT过期异常
     * @return 统一API响应
     */
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleJwtExpiredException(ExpiredJwtException ex) {
        log.warn("JWT令牌已过期: {}", ex.getMessage());
        return ApiResponse.error(401, "令牌无效或已过期");
    }
    
    /**
     * 处理JWT格式异常
     * @param ex JWT格式异常
     * @return 统一API响应
     */
    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleJwtMalformedException(MalformedJwtException ex) {
        log.warn("JWT令牌格式错误: {}", ex.getMessage());
        return ApiResponse.error(401, "令牌无效或已过期");
    }
    
    /**
     * 处理JWT不支持异常
     * @param ex JWT不支持异常
     * @return 统一API响应
     */
    @ExceptionHandler(UnsupportedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleJwtUnsupportedException(UnsupportedJwtException ex) {
        log.warn("JWT令牌不支持: {}", ex.getMessage());
        return ApiResponse.error(401, "令牌无效或已过期");
    }
    
    /**
     * 处理所有JWT相关异常
     * @param ex JWT异常
     * @return 统一API响应
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleJwtException(JwtException ex) {
        log.warn("JWT处理异常: {}", ex.getMessage());
        return ApiResponse.error(401, "令牌无效或已过期");
    }
    
    /**
     * 处理Spring Security认证异常
     * @param ex 认证异常
     * @return 统一API响应
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleAuthenticationException(AuthenticationException ex) {
        log.warn("认证异常: {}", ex.getMessage());
        return ApiResponse.error(401, "认证失败");
    }
    
    /**
     * 处理Spring Security访问拒绝异常
     * @param ex 访问拒绝异常
     * @return 统一API响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("访问拒绝: {}", ex.getMessage());
        return ApiResponse.error(403, "访问被拒绝");
    }
    
    /**
     * 处理Spring Security凭证异常
     * @param ex 凭证异常
     * @return 统一API响应
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("凭证错误: {}", ex.getMessage());
        return ApiResponse.error(401, "用户名或密码错误");
    }
    
    /**
     * 处理接口未找到异常（Spring Boot 2.x）
     * @param ex 接口未找到异常
     * @return 统一API响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<Void> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String requestUrl = ex.getRequestURL();
        String httpMethod = ex.getHttpMethod();
        String message = String.format("%s %s 接口未实现", httpMethod, requestUrl);
        log.warn("接口未实现: {} {}", httpMethod, requestUrl);
        return ApiResponse.error(501, message);
    }
    
    /**
     * 处理资源未找到异常（Spring Boot 3.x）
     * @param ex 资源未找到异常
     * @return 统一API响应
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<Void> handleNoResourceFoundException(NoResourceFoundException ex) {
        String resourcePath = ex.getResourcePath();
        String message = String.format("%s 接口未实现", resourcePath);
        log.warn("接口未实现: {}", resourcePath);
        return ApiResponse.error(501, message);
    }
    
    /**
     * 处理所有未捕获的异常
     * @param ex 异常
     * @return 统一API响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleAllUncaughtException(Exception ex) {
        log.error("未捕获的异常", ex);
        return ApiResponse.error(500, "服务器内部错误", ex.getMessage());
    }
}