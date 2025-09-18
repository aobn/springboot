package com.example.demo.service;

import com.example.demo.dto.UserDomainStats;
import com.example.demo.entity.UserSubdomain;

import java.util.List;

/**
 * 用户3级域名服务接口
 * 
 * @author CodeBuddy
 * @since 2025-08-24
 */
public interface UserSubdomainService {
    
    /**
     * 创建用户子域名
     * 
     * @param userId 用户ID
     * @param subdomain 子域名前缀
     * @param domain 主域名
     * @param remark 备注信息
     * @return 创建的用户子域名信息
     */
    UserSubdomain createSubdomain(Long userId, String subdomain, String domain, String remark);
    
    /**
     * 根据ID查询用户子域名
     * 
     * @param id 主键ID
     * @return 用户子域名信息
     */
    UserSubdomain getById(Long id);
    
    /**
     * 根据用户ID查询子域名列表
     * 
     * @param userId 用户ID
     * @return 用户子域名列表
     */
    List<UserSubdomain> getByUserId(Long userId);
    
    /**
     * 根据完整域名查询
     * 
     * @param fullDomain 完整域名
     * @return 用户子域名信息
     */
    UserSubdomain getByFullDomain(String fullDomain);
    
    /**
     * 检查域名是否可用
     * 
     * @param fullDomain 完整域名
     * @return 是否可用
     */
    boolean isDomainAvailable(String fullDomain);
    
    /**
     * 更新用户子域名状态
     * 
     * @param id 主键ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * 更新用户子域名备注
     * 
     * @param id 主键ID
     * @param remark 备注信息
     * @return 是否更新成功
     */
    boolean updateRemark(Long id, String remark);
    
    /**
     * 删除用户子域名（软删除）
     * 
     * @param id 主键ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);
    
    /**
     * 根据用户ID和状态查询子域名列表
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 用户子域名列表
     */
    List<UserSubdomain> getByUserIdAndStatus(Long userId, String status);
    
    /**
     * 查询所有活跃的子域名
     * 
     * @return 活跃的用户子域名列表
     */
    List<UserSubdomain> getAllActive();
    
    /**
     * 查询域名是否已注册
     * 
     * @param subdomain 子域名前缀
     * @param domain 主域名
     * @return 域名注册信息，如果未注册则返回null
     */
    UserSubdomain checkDomainRegistration(String subdomain, String domain);
    
    /**
     * 获取用户域名使用统计
     * 
     * @param userId 用户ID
     * @return 用户域名统计信息
     */
    UserDomainStats getUserDomainStats(Long userId);
    
    /**
     * 检查用户是否可以注册域名
     * 
     * @param userId 用户ID
     * @return 是否可以注册
     */
    boolean canUserRegisterDomain(Long userId);
    
    /**
     * 获取用户剩余可注册域名数量
     * 
     * @param userId 用户ID
     * @return 剩余可注册数量
     */
    Integer getUserRemainingDomainCount(Long userId);
}