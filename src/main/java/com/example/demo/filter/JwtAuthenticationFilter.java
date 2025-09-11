package com.example.demo.filter;

import com.example.demo.service.TokenBlacklistService;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 文件名：JwtAuthenticationFilter.java
 * 功能：JWT认证过滤器，验证请求中的令牌
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取Authorization头
        final String authorizationHeader = request.getHeader("Authorization");
        
        Long userId = null;
        String jwt = null;
        String email = null;
        String role = null;
        
        // 检查Authorization头是否存在且以"Bearer "开头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 提取JWT令牌
            jwt = authorizationHeader.substring(7);
            
            // 检查token是否在黑名单中
            if (tokenBlacklistService.isBlacklisted(jwt)) {
                log.warn("JWT令牌已在黑名单中，拒绝访问");
                filterChain.doFilter(request, response);
                return;
            }
            
            try {
                // 从JWT令牌中提取用户ID
                userId = jwtUtil.getUserIdFromToken(jwt);
                // 从JWT令牌中提取用户邮箱
                email = jwtUtil.getEmailFromToken(jwt);
                // 从JWT令牌中提取用户角色
                role = jwtUtil.getRoleFromToken(jwt);
                log.info("JWT令牌解析成功: 用户ID {}, 邮箱 {}, 角色 {}", userId, email, role);
            } catch (Exception e) {
                log.error("JWT令牌解析失败: {}", e.getMessage());
            }
        }
        
        // 如果成功提取用户ID且当前SecurityContext中没有认证信息
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 根据用户ID和角色加载用户详情
                String userIdentifier = userId.toString() + ":" + (role != null ? role : "USER");
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userIdentifier);
                
                // 验证JWT令牌
                if (jwtUtil.validateToken(jwt, userId)) {
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    
                    // 设置认证详情
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 设置认证信息到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    log.info("用户ID {} 认证成功", userId);
                } else {
                    log.warn("JWT令牌验证失败: 用户ID {}", userId);
                }
            } catch (Exception e) {
                log.error("JWT认证过程中发生错误: {}", e.getMessage());
            }
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}