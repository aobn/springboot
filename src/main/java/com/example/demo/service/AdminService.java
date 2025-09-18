package com.example.demo.service;

import com.example.demo.dto.AdminUserQueryRequest;
import com.example.demo.dto.BanUserRequest;
import com.example.demo.dto.PageRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.UnbanUserRequest;
import com.example.demo.dto.UserInfoResponse;
import com.example.demo.entity.Admin;
import com.example.demo.entity.User;

/**
 * 文件名：AdminService.java
 * 功能：管理员服务接口，定义管理员相关业务逻辑
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
public interface AdminService {
    
    /**
     * 管理员注册
     * @param admin 管理员信息
     * @return 注册成功的管理员信息（不含密码）
     * @throws RuntimeException 如果用户名或邮箱已存在
     */
    Admin register(Admin admin);
    
    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的管理员信息（不含密码）
     * @throws RuntimeException 如果用户名不存在或密码错误
     */
    Admin login(String username, String password);
    
    /**
     * 根据ID查询管理员
     * @param id 管理员ID
     * @return 管理员信息（不含密码）
     */
    Admin findById(Long id);
    
    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员信息（不含密码）
     */
    Admin findByUsername(String username);
    
    /**
     * 更新管理员信息
     * @param admin 管理员信息
     * @return 更新后的管理员信息（不含密码）
     */
    Admin update(Admin admin);
    
    /**
     * 分页获取用户列表
     * @param pageRequest 分页请求参数
     * @return 分页用户列表
     */
    PageResponse<User> getUserList(PageRequest pageRequest);
    
    /**
     * 搜索用户（分页）
     * @param keyword 搜索关键词
     * @param pageRequest 分页请求参数
     * @return 分页用户列表
     */
    PageResponse<User> searchUsers(String keyword, PageRequest pageRequest);
    
    /**
     * 管理员获取用户已注册域名列表（支持分页和模糊查询）
     * @param request 查询请求参数
     * @return 分页域名列表
     */
    PageResponse<com.example.demo.dto.UserDomainInfo> getUserDomains(com.example.demo.dto.AdminUserDomainQueryRequest request);
    
    /**
     * 管理员获取用户全部DNS记录列表（支持分页、按记录类型查询和模糊查询）
     * @param request 查询请求参数
     * @return 分页DNS记录列表
     */
    PageResponse<com.example.demo.dto.UserDnsRecordInfo> getUserDnsRecords(com.example.demo.dto.AdminDnsRecordQueryRequest request);
    
    /**
     * 管理员获取用户信息列表（支持分页和多条件查询）
     * @param request 查询请求参数
     * @return 分页用户信息列表
     */
    PageResponse<UserInfoResponse> getUsersInfo(AdminUserQueryRequest request);

    /**
     * 管理员封禁用户账户
     * @param request 封禁请求参数
     * @param adminId 执行封禁的管理员ID
     * @return 封禁结果信息
     * @throws RuntimeException 如果用户不存在或已被封禁
     */
    String banUser(BanUserRequest request, Long adminId);

    /**
     * 管理员解封用户账户
     * @param request 解封请求参数
     * @return 解封结果信息
     * @throws RuntimeException 如果用户不存在或未被封禁
     */
    String unbanUser(UnbanUserRequest request);

    /**
     * 检查用户是否被封禁
     * @param userId 用户ID
     * @return true表示用户被封禁，false表示用户正常
     */
    boolean isUserBanned(Long userId);

    /**
     * 获取用户封禁状态详情
     * @param userId 用户ID
     * @return 用户封禁状态信息
     */
    User getUserBanStatus(Long userId);
}