package com.example.demo.service.impl;

import com.example.demo.common.BusinessException;
import com.example.demo.dto.LoginResult;
import com.example.demo.dto.UserChangePasswordRequest;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.dto.UserRegisterVerifyRequest;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现类
 * 
 * @Service - 标记为服务层组件
 * @RequiredArgsConstructor - 自动生成带有final字段的构造函数，实现依赖注入
 * @Slf4j - 自动生成日志对象
 * @Transactional - 事务管理
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    // 使用final + @RequiredArgsConstructor代替@Autowired，更加安全和简洁
    private final UserMapper userMapper;
    private final VerificationCodeService verificationCodeService;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 用户登录
     * @param email 邮箱
     * @param password 密码
     * @return 登录成功返回用户信息，失败返回null
     */
    @Override
    public LoginResult login(String email, String password) {
        log.info("用户登录: {}", email);
        
        // 根据邮箱查询用户
        User user = userMapper.findByEmail(email);
        
        // 用户不存在或密码不匹配
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            log.warn("登录失败: 用户不存在或密码错误 - {}", email);
            return LoginResult.failure("邮箱或密码错误");
        }
        
        // 检查用户是否被封禁
        if ("BANNED".equals(user.getStatus())) {
            log.warn("登录失败: 用户已被封禁 - {} (用户ID: {}), 封禁原因: {}", email, user.getId(), user.getBanReason());
            return LoginResult.banned(user.getBanReason() != null ? user.getBanReason() : "账户已被封禁");
        }
        
        log.info("用户登录成功: {}", email);
        return LoginResult.success(user);
    }
    
    @Override
    public List<User> findAllUsers() {
        log.debug("查询所有用户");
        return userMapper.findAll();
    }
    
    @Override
    public User findUserById(Long id) {
        log.debug("根据ID查询用户: {}", id);
        return userMapper.findById(id);
    }
    
    @Override
    @Transactional
    public User saveUser(User user) {
        log.debug("保存用户: {}", user);
        userMapper.save(user);
        return user;
    }
    
    @Override
    @Transactional
    public User updateUser(User user) {
        log.debug("更新用户: {}", user);
        userMapper.update(user);
        return user;
    }
    
    @Override
    @Transactional
    public boolean deleteUserById(Long id) {
        log.debug("删除用户: {}", id);
        return userMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean registerStep1(UserRegisterRequest request) {
        log.info("开始用户注册第一步，发送验证码到邮箱: {}", request.getEmail());
        
        // 检查邮箱是否已被注册
        User existingUser = userMapper.findByEmail(request.getEmail());
        if (existingUser != null) {
            log.warn("邮箱已被注册: {}", request.getEmail());
            return false;
        }
        
        // 生成并发送验证码
        verificationCodeService.generateAndSendCode(request.getEmail());
        
        return true;
    }
    
    @Override
    @Transactional
    public User registerStep2(UserRegisterVerifyRequest request) {
        log.info("开始用户注册第二步，验证邮箱验证码: {}", request.getEmail());
        
        // 验证验证码
        boolean isCodeValid = verificationCodeService.verifyCode(request.getEmail(), request.getCode());
        if (!isCodeValid) {
            log.warn("验证码验证失败: {}", request.getEmail());
            return null;
        }
        
        // 检查邮箱是否已被注册（再次检查，防止并发注册）
        User existingUser = userMapper.findByEmail(request.getEmail());
        if (existingUser != null) {
            log.warn("邮箱已被注册: {}", request.getEmail());
            return null;
        }
        
        // 创建新用户
        User newUser = new User();
        // 用户名默认为邮箱账户名部分
        String username = request.getEmail().split("@")[0];
        newUser.setUsername(username);
        newUser.setEmail(request.getEmail());
        // 密码加密存储
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 保存用户
        userMapper.save(newUser);
        
        // 标记验证码为已使用
        verificationCodeService.markCodeAsUsed(request.getEmail(), request.getCode());
        
        log.info("用户注册成功: {}", newUser.getUsername());
        
        return newUser;
    }
    
    @Override
    public User findUserByEmail(String email) {
        log.debug("根据邮箱查询用户: {}", email);
        return userMapper.findByEmail(email);
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, UserChangePasswordRequest request) {
        log.info("用户修改密码: userId={}", userId);
        
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("新密码和确认密码不一致: userId={}", userId);
            throw new BusinessException("新密码和确认密码不一致");
        }
        
        // 查询用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            throw new BusinessException("用户不存在");
        }
        
        // 验证原密码是否正确
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("原密码验证失败: userId={}", userId);
            throw new BusinessException("原密码不正确");
        }
        
        // 检查新密码是否与原密码相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            log.warn("新密码不能与原密码相同: userId={}", userId);
            throw new BusinessException("新密码不能与当前密码相同");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        int updateResult = userMapper.update(user);
        
        if (updateResult > 0) {
            log.info("用户密码修改成功: userId={}", userId);
        } else {
            log.error("用户密码修改失败: userId={}", userId);
            throw new BusinessException("密码修改失败，请稍后重试");
        }
    }
}
