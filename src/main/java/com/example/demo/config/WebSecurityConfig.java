package com.example.demo.config;


import com.example.demo.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * 文件名：WebSecurityConfig.java
 * 功能：Web安全配置类，配置HTTP安全规则（目前先不配置安全规则）
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    /**
     * 配置HTTP安全规则
     * 配置JWT认证过滤器，保护需要认证的API端点
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // 禁用CSRF保护，方便测试API
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // 启用CORS配置
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 使用无状态会话，不使用Session
            )
            .authorizeHttpRequests(authorize -> authorize
                // 公开接口，无需认证
                .requestMatchers("/api/auth/**", "/api/verification/**", "/api/test/**").permitAll()
                // 管理员登录接口，无需认证
                .requestMatchers("/api/admin/login", "/api/admin/register").permitAll()
                // 其他管理员接口，只允许ADMIN角色访问
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // DNSPod相关接口暂时允许访问，无需认证（便于测试）
                .requestMatchers("/api/dnspod/**").permitAll()
                // 用户相关接口，需要认证（USER或ADMIN角色）
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                // 其他接口需要认证
                .anyRequest().authenticated()
            )
            // 设置自定义认证入口点
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * CORS配置源
     * 
     * @return CorsConfigurationSource CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.addAllowedOriginPattern("*");
        
        // 允许的方法
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("PATCH");
        
        // 允许的请求头
        configuration.addAllowedHeader("*");
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求缓存时间
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
