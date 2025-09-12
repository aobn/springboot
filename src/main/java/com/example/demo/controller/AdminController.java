package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.AdminLoginRequest;
import com.example.demo.dto.AdminRegisterRequest;
import com.example.demo.dto.AdminUpdateRequest;
import com.example.demo.dto.AdminUserDomainQueryRequest;
import com.example.demo.dto.PageRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.UserDomainInfo;
import com.example.demo.entity.Admin;
import com.example.demo.entity.User;
import com.example.demo.service.AdminService;
import com.example.demo.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件名：AdminController.java
 * 功能：管理员相关API接口控制器
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 管理员注册
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResponse<Admin> register(@RequestBody AdminRegisterRequest request) {
        log.info("接收到管理员注册请求: {}", request.getUsername());
        
        // 验证请求参数
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ApiResponse.error(400, "密码不能为空");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ApiResponse.error(400, "邮箱不能为空");
        }
        
        try {
            // 创建管理员对象
            Admin admin = new Admin();
            admin.setUsername(request.getUsername());
            admin.setPassword(request.getPassword());
            admin.setEmail(request.getEmail());
            admin.setRole(request.getRole());
            
            // 注册管理员
            Admin registeredAdmin = adminService.register(admin);
            
            return ApiResponse.success(registeredAdmin);
        } catch (Exception e) {
            log.error("管理员注册失败: {}", e.getMessage());
            return ApiResponse.error(409, e.getMessage());
        }
    }
    
    /**
     * 管理员登录
     * @param request 登录请求
     * @return 登录结果，包含JWT令牌
     */
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody AdminLoginRequest request) {
        log.info("接收到管理员登录请求: {}", request.getUsername());
        
        // 验证请求参数
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ApiResponse.error(400, "密码不能为空");
        }
        
        try {
            // 登录验证
            Admin admin = adminService.login(request.getUsername(), request.getPassword());
            
            // 生成JWT令牌，包含管理员ID、邮箱和角色信息
            String token = jwtUtil.generateToken(admin.getId(), admin.getEmail(), admin.getRole());
            
            // 构建响应对象
            com.example.demo.dto.AdminLoginResponse response = new com.example.demo.dto.AdminLoginResponse();
            response.setAdmin(admin);
            response.setToken(token);
            
            log.info("管理员登录成功: {} (ID: {}, 角色: {})", admin.getUsername(), admin.getId(), admin.getRole());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("管理员登录失败: {}", e.getMessage());
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    /**
     * 获取管理员信息
     * @param id 管理员ID
     * @return 管理员信息
     */
    @GetMapping("/{id}")
    public ApiResponse<Admin> getAdminById(@PathVariable Long id) {
        log.info("获取管理员信息: {}", id);
        
        try {
            Admin admin = adminService.findById(id);
            if (admin == null) {
                return ApiResponse.error(404, "管理员不存在");
            }
            return ApiResponse.success(admin);
        } catch (Exception e) {
            log.error("获取管理员信息失败: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    /**
     * 更新管理员信息
     * @param id 管理员ID
     * @param request 更新请求
     * @return 更新后的管理员信息
     */
    @PutMapping("/{id}")
    public ApiResponse<Admin> updateAdmin(@PathVariable Long id, @RequestBody AdminUpdateRequest request) {
        log.info("更新管理员信息: {}", id);
        
        try {
            // 查询管理员是否存在
            Admin existingAdmin = adminService.findById(id);
            if (existingAdmin == null) {
                return ApiResponse.error(404, "管理员不存在");
            }
            
            // 创建更新对象
            Admin admin = new Admin();
            admin.setId(id);
            admin.setUsername(request.getUsername());
            admin.setEmail(request.getEmail());
            admin.setPassword(request.getPassword());
            admin.setRole(request.getRole());
            
            // 更新管理员信息
            Admin updatedAdmin = adminService.update(admin);
            
            return ApiResponse.success(updatedAdmin);
        } catch (Exception e) {
            log.error("更新管理员信息失败: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    /**
     * 获取用户列表（分页）
     * @param page 页码，默认1
     * @param size 每页大小，默认3000
     * @param sortBy 排序字段，默认id
     * @param sortDir 排序方向，默认ASC
     * @return 分页用户列表
     */
    @GetMapping("/users")
    public ApiResponse<PageResponse<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "3000") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        log.info("管理员获取用户列表，页码: {}, 每页大小: {}, 排序: {} {}", page, size, sortBy, sortDir);
        
        try {
            // 创建分页请求对象
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setSize(size);
            pageRequest.setSortBy(sortBy);
            pageRequest.setSortDir(sortDir);
            
            // 获取用户列表
            PageResponse<User> result = adminService.getUserList(pageRequest);
            
            log.info("获取用户列表成功，总数: {}, 当前页: {} 条", result.getTotal(), result.getContent().size());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    /**
     * 搜索用户（分页）
     * @param keyword 搜索关键词
     * @param page 页码，默认1
     * @param size 每页大小，默认3000
     * @param sortBy 排序字段，默认id
     * @param sortDir 排序方向，默认ASC
     * @return 分页用户列表
     */
    @GetMapping("/users/search")
    public ApiResponse<PageResponse<User>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "3000") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        log.info("管理员搜索用户，关键词: {}, 页码: {}, 每页大小: {}", keyword, page, size);
        
        try {
            // 创建分页请求对象
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setSize(size);
            pageRequest.setSortBy(sortBy);
            pageRequest.setSortDir(sortDir);
            
            // 搜索用户
            PageResponse<User> result = adminService.searchUsers(keyword, pageRequest);
            
            log.info("搜索用户成功，总数: {}, 当前页: {} 条", result.getTotal(), result.getContent().size());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("搜索用户失败: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    /**
     * 管理员获取用户已注册域名列表（支持分页和模糊查询）
     * @param request 查询请求参数
     * @return 分页域名列表
     */
    @PostMapping("/users/domains")
    public ApiResponse<PageResponse<UserDomainInfo>> getUserDomains(@RequestBody AdminUserDomainQueryRequest request) {
        log.info("管理员获取用户域名列表，页码: {}, 每页大小: {}, 关键词: {}", 
                request.getPage(), request.getSize(), request.getKeyword());
        
        try {
            // 获取用户域名列表
            PageResponse<UserDomainInfo> result = adminService.getUserDomains(request);
            
            log.info("获取用户域名列表成功，总数: {}, 当前页: {} 条", result.getTotal(), result.getContent().size());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取用户域名列表失败: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }
}