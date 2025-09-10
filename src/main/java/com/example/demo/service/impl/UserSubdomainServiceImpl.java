package com.example.demo.service.impl;

import com.example.demo.dto.UserDomainStats;
import com.example.demo.entity.User;
import com.example.demo.entity.UserSubdomain;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserSubdomainMapper;
import com.example.demo.service.UserSubdomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户3级域名服务实现类
 * 
 * @author CodeBuddy
 * @since 2025-08-24
 */
@Service
public class UserSubdomainServiceImpl implements UserSubdomainService {
    
    @Autowired
    private UserSubdomainMapper userSubdomainMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public UserSubdomain createSubdomain(Long userId, String subdomain, String domain, String remark) {
        // 参数验证
        if (userId == null || !StringUtils.hasText(subdomain) || !StringUtils.hasText(domain)) {
            throw new IllegalArgumentException("用户ID、子域名前缀和主域名不能为空");
        }
        
        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 检查用户域名注册数量限制
        if (user.getDomNum() == null || user.getDomNum() <= 0) {
            throw new IllegalArgumentException("您已达到域名注册数量上限，当前可注册数量：0");
        }
        
        // 构建完整域名
        String fullDomain = subdomain + "." + domain;
        
        // 检查域名是否已存在
        if (!isDomainAvailable(fullDomain)) {
            throw new IllegalArgumentException("域名 " + fullDomain + " 已存在");
        }
        
        // 创建用户子域名对象
        UserSubdomain userSubdomain = new UserSubdomain(userId, subdomain, domain, fullDomain);
        userSubdomain.setRemark(remark);
        
        // 插入数据库（触发器会自动减少用户的dom_num）
        int result = userSubdomainMapper.insert(userSubdomain);
        if (result > 0) {
            return userSubdomain;
        } else {
            throw new RuntimeException("创建用户子域名失败");
        }
    }
    
    @Override
    public UserSubdomain getById(Long id) {
        if (id == null) {
            return null;
        }
        return userSubdomainMapper.selectById(id);
    }
    
    @Override
    public List<UserSubdomain> getByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userSubdomainMapper.selectByUserId(userId);
    }
    
    @Override
    public UserSubdomain getByFullDomain(String fullDomain) {
        if (!StringUtils.hasText(fullDomain)) {
            return null;
        }
        return userSubdomainMapper.selectByFullDomain(fullDomain);
    }
    
    @Override
    public boolean isDomainAvailable(String fullDomain) {
        if (!StringUtils.hasText(fullDomain)) {
            return false;
        }
        int count = userSubdomainMapper.countByFullDomain(fullDomain);
        return count == 0;
    }
    
    @Override
    public boolean updateStatus(Long id, String status) {
        if (id == null || !StringUtils.hasText(status)) {
            return false;
        }
        
        // 验证状态值
        if (!"ACTIVE".equals(status) && !"INACTIVE".equals(status) && !"DELETED".equals(status)) {
            throw new IllegalArgumentException("无效的状态值：" + status);
        }
        
        int result = userSubdomainMapper.updateStatus(id, status);
        return result > 0;
    }
    
    @Override
    public boolean updateRemark(Long id, String remark) {
        if (id == null) {
            return false;
        }
        int result = userSubdomainMapper.updateRemark(id, remark);
        return result > 0;
    }
    
    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        int result = userSubdomainMapper.deleteById(id);
        return result > 0;
    }
    
    @Override
    public List<UserSubdomain> getByUserIdAndStatus(Long userId, String status) {
        if (userId == null || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("用户ID和状态不能为空");
        }
        return userSubdomainMapper.selectByUserIdAndStatus(userId, status);
    }
    
    @Override
    public List<UserSubdomain> getAllActive() {
        return userSubdomainMapper.selectAllActive();
    }
    
    @Override
    public UserSubdomain checkDomainRegistration(String subdomain, String domain) {
        if (!StringUtils.hasText(subdomain) || !StringUtils.hasText(domain)) {
            return null;
        }
        return userSubdomainMapper.selectBySubdomainAndDomain(subdomain, domain);
    }
    
    @Override
    public UserDomainStats getUserDomainStats(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userMapper.getUserDomainStats(userId);
    }
    
    @Override
    public boolean canUserRegisterDomain(Long userId) {
        if (userId == null) {
            return false;
        }
        Integer remainingCount = getUserRemainingDomainCount(userId);
        return remainingCount != null && remainingCount > 0;
    }
    
    @Override
    public Integer getUserRemainingDomainCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        return userMapper.getUserDomainLimit(userId);
    }
}