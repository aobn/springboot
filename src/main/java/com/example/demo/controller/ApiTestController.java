package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.BusinessException;
import com.example.demo.common.ResourceNotFoundException;
import com.example.demo.entity.User;
import com.example.demo.service.DnspodService;
import com.tencentcloudapi.dnspod.v20210323.models.DeleteRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyRecordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API测试控制器
 * @author CodeBuddy
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class ApiTestController {

    private final DnspodService dnspodService;

    /**
     * 测试成功响应
     * @return 成功响应
     */
    @GetMapping("/success")
    public ApiResponse<String> testSuccess() {
        log.info("测试成功响应");
        return ApiResponse.success("这是一个成功的响应");
    }

    /**
     * 测试带数据的成功响应
     * @return 带数据的成功响应
     */
    @GetMapping("/data")
    public ApiResponse<Map<String, Object>> testData() {
        log.info("测试带数据的成功响应");
        Map<String, Object> data = new HashMap<>();
        data.put("name", "测试用户");
        data.put("age", 25);
        data.put("email", "test@example.com");
        return ApiResponse.success("获取数据成功", data);
    }

    /**
     * 测试创建资源响应
     * @return 创建资源响应
     */
    @PostMapping("/create")
    public ApiResponse<User> testCreate() {
        log.info("测试创建资源响应");
        User user = new User();
        user.setId(100L);
        user.setUsername("newUser");
        user.setPassword("password123");
        user.setEmail("new@example.com");
        return ApiResponse.created(user);
    }

    /**
     * 测试列表数据响应
     * @return 列表数据响应
     */
    @GetMapping("/list")
    public ApiResponse<List<String>> testList() {
        log.info("测试列表数据响应");
        List<String> items = Arrays.asList("项目1", "项目2", "项目3");
        return ApiResponse.success(items);
    }

    /**
     * 测试业务异常
     * @return 不会返回，会抛出异常
     */
    @GetMapping("/business-error")
    public ApiResponse<Void> testBusinessError() {
        log.info("测试业务异常");
        throw new BusinessException(400, "业务处理失败：参数不符合业务规则");
    }

    /**
     * 测试资源未找到异常
     * @param id 资源ID
     * @return 不会返回，会抛出异常
     */
    @GetMapping("/not-found/{id}")
    public ApiResponse<Void> testResourceNotFound(@PathVariable Long id) {
        log.info("测试资源未找到异常，ID: {}", id);
        throw new ResourceNotFoundException("ID为" + id + "的资源");
    }

    /**
     * 测试服务器错误
     * @return 不会返回，会抛出异常
     */
    @GetMapping("/server-error")
    public ApiResponse<Void> testServerError() {
        log.info("测试服务器错误");
        throw new RuntimeException("服务器内部错误测试");
    }
    
    /**
     * 健康检查接口
     * @return 包含健康状态的ApiResponse
     */
    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("服务运行正常", "OK");
    }

    /**
     * 测试DNSPod删除记录接口
     * 
     * @param domain 域名
     * @param recordId 记录ID
     * @param domainId 域名ID（可选）
     * @return 删除记录响应
     */
    @GetMapping("/dnspod/delete-record")
    public ApiResponse<DeleteRecordResponse> testDeleteDnspodRecord(
            @RequestParam String domain,
            @RequestParam Long recordId,
            @RequestParam(required = false) Long domainId) {
        
        log.info("测试DNSPod删除记录接口 - 域名: {}, 记录ID: {}, 域名ID: {}", domain, recordId, domainId);
        
        try {
            DeleteRecordResponse response = dnspodService.deleteRecord(domain, recordId, domainId);
            log.info("DNSPod删除记录成功 - RequestId: {}", response.getRequestId());
            return ApiResponse.success("删除记录成功", response);
        } catch (Exception e) {
            log.error("DNSPod删除记录失败", e);
            return ApiResponse.error(500, "删除记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试DNSPod修改记录接口
     * 
     * @param domain 域名
     * @param recordId 记录ID
     * @param recordType 记录类型
     * @param value 记录值
     * @param recordLine 记录线路（可选，默认"默认"）
     * @param subDomain 主机记录（可选）
     * @param domainId 域名ID（可选）
     * @return 修改记录响应
     */
    @GetMapping("/dnspod/modify-record")
    public ApiResponse<ModifyRecordResponse> testModifyDnspodRecord(
            @RequestParam String domain,
            @RequestParam Long recordId,
            @RequestParam String recordType,
            @RequestParam String value,
            @RequestParam(required = false, defaultValue = "默认") String recordLine,
            @RequestParam(required = false) String subDomain,
            @RequestParam(required = false) Long domainId) {
        
        log.info("测试DNSPod修改记录接口 - 域名: {}, 记录ID: {}, 记录类型: {}, 记录值: {}, 记录线路: {}, 主机记录: {}, 域名ID: {}", 
                domain, recordId, recordType, value, recordLine, subDomain, domainId);
        
        try {
            ModifyRecordResponse response = dnspodService.modifyRecord(
                domain, recordId, recordType, recordLine, value, subDomain,
                domainId, null, null, null, null, null
            );
            log.info("DNSPod修改记录成功 - RequestId: {}", response.getRequestId());
            return ApiResponse.success("修改记录成功", response);
        } catch (Exception e) {
            log.error("DNSPod修改记录失败", e);
            return ApiResponse.error(500, "修改记录失败: " + e.getMessage());
        }
    }
}
