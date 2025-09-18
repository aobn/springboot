package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件名：AdminUserController.java
 * 功能：管理员对用户进行增删改查的API接口控制器
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 管理员获取所有用户
     * @return 用户列表
     */
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<List<User>> getAllUsers() {
        log.info("管理员获取所有用户");
        List<User> users = userService.findAllUsers();
        
        // 处理用户列表，移除密码字段
        List<User> safeUsers = users.stream()
                .map(this::removePassword)
                .collect(Collectors.toList());
        
        return ApiResponse.success(safeUsers);
    }
    
    /**
     * 管理员根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        log.info("管理员获取用户ID: {}", id);
        User user = userService.findUserById(id);
        if (user != null) {
            return ApiResponse.success(removePassword(user));
        } else {
            return ApiResponse.error(404, "用户不存在");
        }
    }
    
    /**
     * 管理员创建用户
     * @param user 用户信息
     * @return 创建的用户信息
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<User> createUser(@RequestBody User user) {
        log.info("管理员创建用户: {}", user.getUsername());
        
        // 验证请求参数
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ApiResponse.error(400, "密码不能为空");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ApiResponse.error(400, "邮箱不能为空");
        }
        
        try {
            // 检查邮箱是否已存在
            User existingUser = userService.findUserByEmail(user.getEmail());
            if (existingUser != null) {
                return ApiResponse.error(409, "该邮箱已被注册");
            }
            
            // 加密密码
            String rawPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(rawPassword));
            
            // 创建用户
            User savedUser = userService.saveUser(user);
            
            return ApiResponse.created(removePassword(savedUser));
        } catch (Exception e) {
            log.error("管理员创建用户失败: {}", e.getMessage());
            return ApiResponse.error(500, "创建用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 管理员更新用户
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("管理员更新用户ID: {}, 用户名: {}", id, user.getUsername());
        
        try {
            // 检查用户是否存在
            User existingUser = userService.findUserById(id);
            if (existingUser == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            // 设置用户ID
            user.setId(id);
            
            // 保留未提供的字段值
            if (user.getUsername() == null) {
                user.setUsername(existingUser.getUsername());
            }
            
            // 处理密码：如果提供了新密码，则加密；否则保留原密码
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(existingUser.getPassword());
            }
            
            if (user.getEmail() == null) {
                user.setEmail(existingUser.getEmail());
            }
            
            if (user.getRole() == null) {
                user.setRole(existingUser.getRole());
            }
            
            // 保留时间字段
            user.setCreateTime(existingUser.getCreateTime());
            user.setUpdateTime(existingUser.getUpdateTime());
            
            // 更新用户
            User updatedUser = userService.updateUser(user);
            
            return ApiResponse.success("用户更新成功", removePassword(updatedUser));
        } catch (Exception e) {
            log.error("管理员更新用户失败: {}", e.getMessage());
            return ApiResponse.error(500, "更新用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 管理员删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        log.info("管理员删除用户ID: {}", id);
        
        try {
            // 检查用户是否存在
            User existingUser = userService.findUserById(id);
            if (existingUser == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            // 删除用户
            boolean deleted = userService.deleteUserById(id);
            if (deleted) {
                return ApiResponse.success("用户删除成功", null);
            } else {
                return ApiResponse.error(500, "删除用户失败");
            }
        } catch (Exception e) {
            log.error("管理员删除用户失败: {}", e.getMessage());
            return ApiResponse.error(500, "删除用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 管理员根据邮箱查询用户
     * @param email 邮箱地址
     * @return 用户信息
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<User> getUserByEmail(@PathVariable String email) {
        log.info("管理员根据邮箱查询用户: {}", email);
        
        try {
            User user = userService.findUserByEmail(email);
            if (user != null) {
                return ApiResponse.success(removePassword(user));
            } else {
                return ApiResponse.error(404, "用户不存在");
            }
        } catch (Exception e) {
            log.error("管理员根据邮箱查询用户失败: {}", e.getMessage());
            return ApiResponse.error(500, "查询用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 移除用户密码，返回安全的用户对象
     * @param user 原始用户对象
     * @return 不包含密码的用户对象
     */
    private User removePassword(User user) {
        if (user == null) return null;
        
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setEmail(user.getEmail());
        safeUser.setRole(user.getRole());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(user.getUpdateTime());
        // 不设置密码字段
        
        return safeUser;
    }
}