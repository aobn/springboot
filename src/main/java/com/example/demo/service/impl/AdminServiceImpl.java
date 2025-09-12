package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AdminUserDomainQueryRequest;
import com.example.demo.dto.PageRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.UserDomainInfo;
import com.example.demo.dto.UserSubdomainWithUser;
import com.example.demo.entity.Admin;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserSubdomainMapper;
import com.example.demo.service.AdminService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 文件名：AdminServiceImpl.java
 * 功能：管理员服务实现类
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserSubdomainMapper userSubdomainMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Admin register(Admin admin) {
        log.info("开始注册管理员: {}", admin.getUsername());
        
        // 检查用户名是否已存在
        if (adminMapper.findByUsername(admin.getUsername()) != null) {
            log.warn("管理员注册失败，用户名已存在: {}", admin.getUsername());
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (adminMapper.findByEmail(admin.getEmail()) != null) {
            log.warn("管理员注册失败，邮箱已存在: {}", admin.getEmail());
            throw new RuntimeException("邮箱已存在");
        }
        
        // 设置默认角色（如果未指定）
        if (admin.getRole() == null) {
            admin.setRole(UserRole.ADMIN.name());
        }
        
        // 加密密码
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        
        // 保存管理员信息
        adminMapper.insert(admin);
        log.info("管理员注册成功: {}", admin.getUsername());
        
        // 返回结果时清除密码
        Admin result = new Admin();
        result.setId(admin.getId());
        result.setUsername(admin.getUsername());
        result.setEmail(admin.getEmail());
        result.setRole(admin.getRole());
        result.setCreateTime(admin.getCreateTime());
        result.setUpdateTime(admin.getUpdateTime());
        
        return result;
    }

    @Override
    public Admin login(String username, String password) {
        log.info("管理员登录: {}", username);
        
        // 查询管理员信息
        Admin admin = adminMapper.findByUsername(username);
        if (admin == null) {
            log.warn("管理员登录失败，用户名不存在: {}", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            log.warn("管理员登录失败，密码错误: {}", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        log.info("管理员登录成功: {}", username);
        
        // 返回结果时清除密码
        Admin result = new Admin();
        result.setId(admin.getId());
        result.setUsername(admin.getUsername());
        result.setEmail(admin.getEmail());
        result.setRole(admin.getRole());
        result.setCreateTime(admin.getCreateTime());
        result.setUpdateTime(admin.getUpdateTime());
        
        return result;
    }

    @Override
    public Admin findById(Long id) {
        Admin admin = adminMapper.findById(id);
        if (admin != null) {
            admin.setPassword(null); // 清除密码
        }
        return admin;
    }

    @Override
    public Admin findByUsername(String username) {
        Admin admin = adminMapper.findByUsername(username);
        if (admin != null) {
            admin.setPassword(null); // 清除密码
        }
        return admin;
    }

    @Override
    public Admin update(Admin admin) {
        // 查询原管理员信息
        Admin existingAdmin = adminMapper.findById(admin.getId());
        if (existingAdmin == null) {
            throw new RuntimeException("管理员不存在");
        }
        
        // 如果修改了用户名，检查新用户名是否已存在
        if (!existingAdmin.getUsername().equals(admin.getUsername())) {
            if (adminMapper.findByUsername(admin.getUsername()) != null) {
                throw new RuntimeException("用户名已存在");
            }
        }
        
        // 如果修改了邮箱，检查新邮箱是否已存在
        if (!existingAdmin.getEmail().equals(admin.getEmail())) {
            if (adminMapper.findByEmail(admin.getEmail()) != null) {
                throw new RuntimeException("邮箱已存在");
            }
        }
        
        // 如果提供了新密码，则加密
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        } else {
            admin.setPassword(existingAdmin.getPassword());
        }
        
        // 更新管理员信息
        adminMapper.update(admin);
        
        // 返回结果时清除密码
        Admin result = adminMapper.findById(admin.getId());
        result.setPassword(null);
        
        return result;
    }
    
    @Override
    public PageResponse<User> getUserList(PageRequest pageRequest) {
        log.info("管理员获取用户列表，页码: {}, 每页大小: {}", pageRequest.getPage(), pageRequest.getSize());
        
        // 验证分页参数
        pageRequest.validate();
        
        // 查询用户列表
        List<User> users = userMapper.findUsersWithPagination(
            pageRequest.getOffset(), 
            pageRequest.getSize(),
            pageRequest.getSortBy(),
            pageRequest.getSortDir()
        );
        
        // 清除密码信息
        users.forEach(user -> user.setPassword(null));
        
        // 查询总数
        Long total = userMapper.countUsers();
        
        log.info("获取用户列表成功，共 {} 条记录，当前页 {} 条", total, users.size());
        
        return PageResponse.of(users, pageRequest.getPage(), pageRequest.getSize(), total);
    }
    
    @Override
    public PageResponse<User> searchUsers(String keyword, PageRequest pageRequest) {
        log.info("管理员搜索用户，关键词: {}, 页码: {}, 每页大小: {}", keyword, pageRequest.getPage(), pageRequest.getSize());
        
        // 验证分页参数
        pageRequest.validate();
        
        // 如果关键词为空，返回所有用户
        if (keyword == null || keyword.trim().isEmpty()) {
            return getUserList(pageRequest);
        }
        
        // 搜索用户列表
        List<User> users = userMapper.searchUsersWithPagination(
            keyword.trim(),
            pageRequest.getOffset(), 
            pageRequest.getSize(),
            pageRequest.getSortBy(),
            pageRequest.getSortDir()
        );
        
        // 清除密码信息
        users.forEach(user -> user.setPassword(null));
        
        // 查询搜索结果总数
        Long total = userMapper.countSearchUsers(keyword.trim());
        
        log.info("搜索用户成功，共 {} 条记录，当前页 {} 条", total, users.size());
        
        return PageResponse.of(users, pageRequest.getPage(), pageRequest.getSize(), total);
    }
    
    @Override
    public PageResponse<UserDomainInfo> getUserDomains(AdminUserDomainQueryRequest request) {
        log.info("管理员获取用户域名列表，页码: {}, 每页大小: {}, 关键词: {}", 
                request.getPage(), request.getSize(), request.getKeyword());
        
        // 验证并设置默认值
        request.validate();
        
        // 计算偏移量
        int offset = (request.getPage() - 1) * request.getSize();
        
        // 查询域名列表
        List<UserSubdomainWithUser> userSubdomains = userSubdomainMapper.selectUserDomainsWithPagination(
            request.getKeyword(),
            request.getUserId(),
            request.getStatus(),
            request.getDomain(),
            request.getSortBy(),
            request.getSortDir(),
            offset,
            request.getSize()
        );
        
        // 转换为DTO并设置用户信息
        List<UserDomainInfo> domainInfos = userSubdomains.stream()
            .map(userSubdomain -> {
                UserDomainInfo info = UserDomainInfo.fromUserSubdomain(userSubdomain);
                info.setUsername(userSubdomain.getUsername());
                info.setEmail(userSubdomain.getEmail());
                return info;
            })
            .collect(java.util.stream.Collectors.toList());
        
        // 查询总数
        Long total = userSubdomainMapper.countUserDomainsWithConditions(
            request.getKeyword(),
            request.getUserId(),
            request.getStatus(),
            request.getDomain()
        );
        
        log.info("获取用户域名列表成功，共 {} 条记录，当前页 {} 条", total, domainInfos.size());
        
        return PageResponse.of(domainInfos, request.getPage(), request.getSize(), total);
    }
}