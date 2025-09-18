package com.example.demo.service.impl;

import com.example.demo.service.ExternalMailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件名：ExternalMailServiceImpl.java
 * 功能：外部邮件服务实现类，调用第三方邮件发送API
 * 作者：CodeBuddy
 * 创建时间：2025-09-15
 * 版本：v1.0.0
 */
@Service
public class ExternalMailServiceImpl implements ExternalMailService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalMailServiceImpl.class);
    
    // 外部邮件API配置
    private static final String MAIL_API_URL = "https://mail.webdom.cn:41443/api/batch_mail/api/send";
    private static final String API_KEY = "e70a3ac47d0631360c4b11f25d2a431e3264ba45ac2bbbecc9a8003f3b2ae1e7";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public ExternalMailServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 发送验证码邮件
     * @param recipient 收件人邮箱地址
     * @param verificationCode 验证码
     * @return 发送结果，true表示成功，false表示失败
     */
    @Override
    public boolean sendVerificationCode(String recipient, String verificationCode) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-Key", API_KEY);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", recipient);
            
            Map<String, String> attribs = new HashMap<>();
            attribs.put("verification_code", verificationCode);
            requestBody.put("attribs", attribs);
            
            // 创建HTTP请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            logger.info("发送验证码邮件请求: recipient={}, code={}", recipient, verificationCode);
            
            // 发送POST请求
            ResponseEntity<String> response = restTemplate.exchange(
                MAIL_API_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                
                boolean success = jsonNode.get("success").asBoolean();
                int code = jsonNode.get("code").asInt();
                String message = jsonNode.get("msg").asText();
                
                logger.info("邮件API响应: success={}, code={}, message={}", success, code, message);
                
                if (success && code == 0) {
                    logger.info("验证码邮件发送成功: {}", recipient);
                    return true;
                } else {
                    logger.error("验证码邮件发送失败: code={}, message={}", code, message);
                    return false;
                }
            } else {
                logger.error("邮件API请求失败: HTTP状态码={}", response.getStatusCode());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("发送验证码邮件异常: recipient={}, error={}", recipient, e.getMessage(), e);
            return false;
        }
    }
}