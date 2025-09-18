package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.BusinessException;
import com.example.demo.common.ResourceNotFoundException;
import com.example.demo.dto.UserChangePasswordRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 * 
 * @RestController - 组合了@Controller和@ResponseBody，返回JSON数据
 * @RequestMapping - 定义请求路径
 * @RequiredArgsConstructor - 自动生成带有final字段的构造函数，实现依赖注入
 * @Slf4j - 自动生成日志对象
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * 获取所有用户
     */
    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        log.info("获取所有用户");
        List<User> users = userService.findAllUsers();
        
        return ApiResponse.success(users);
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        log.info("获取用户接口获取用户接口获取用户接口获取用户接口获取用户接口获取用户接口");
        log.info("获取用户ID: {}", id);
        User user = userService.findUserById(id);
        if (user != null) {
            return ApiResponse.success(user);
        } else {
            throw new ResourceNotFoundException("用户");
        }
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public ApiResponse<User> createUser(@RequestBody User user) {
        log.info("创建用户: {}", user);
        System.out.println(user);
        User savedUser = userService.saveUser(user);
        return ApiResponse.created(savedUser);
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("更新用户接口更新用户接口更新用户接口更新用户接口更新用户接口");
        log.info("更新用户ID: {}, 用户信息: {}", id, user);
        
        User existingUser = userService.findUserById(id);
        if (existingUser == null) {
            throw new ResourceNotFoundException("用户");
        }
        
        // 设置用户ID
        user.setId(id);
        
        // 保留未提供的字段值
        if (user.getUsername() == null) {
            user.setUsername(existingUser.getUsername());
        }
        
        // 如果密码不为空，则对密码进行加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 如果密码为空，则使用原密码
            user.setPassword(existingUser.getPassword());
        }
        
        // 保留邮箱字段
        if (user.getEmail() == null) {
            user.setEmail(existingUser.getEmail());
        }
        
        // 保留角色字段
        if (user.getRole() == null) {
            user.setRole(existingUser.getRole());
        }
        
        // 保留时间字段
        user.setCreateTime(existingUser.getCreateTime());
        user.setUpdateTime(existingUser.getUpdateTime());
        
        User updatedUser = userService.updateUser(user);
        return ApiResponse.success("用户更新成功", updatedUser);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户删除用户删除用户删除用户删除用户删除用户");
        log.info("删除用户ID: {}", id);
        User existingUser = userService.findUserById(id);
        if (existingUser == null) {
            throw new ResourceNotFoundException("用户");
        }
        userService.deleteUserById(id);
        return ApiResponse.success("用户删除成功", null);
    }
    
    /**
     * 用户修改密码
     * 用户通过JWT令牌认证后，输入原密码和新密码来修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody UserChangePasswordRequest request, 
                                          HttpServletRequest httpRequest) {
        log.info("用户修改密码请求");
        
        // 从请求头中获取JWT令牌
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(401, "未提供有效的认证令牌");
        }
        
        String token = authHeader.substring(7);
        
        // 检查令牌是否为空或格式不正确
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(401, "认证令牌格式不正确");
        }
        
        // 验证令牌并获取用户ID
        Long userId;
        try {
            userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                throw new BusinessException(401, "无效的认证令牌");
            }
        } catch (Exception e) {
            log.error("JWT令牌解析失败", e);
            throw new BusinessException(401, "认证令牌已过期或无效");
        }
        
        // 调用服务层修改密码（服务层会进行所有验证并抛出具体异常）
        userService.changePassword(userId, request);
        
        log.info("用户密码修改成功: userId={}", userId);
        return ApiResponse.success("密码修改成功", null);
    }
}
