package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.RecordRequest;
import com.example.demo.service.DnspodService;
import com.tencentcloudapi.dnspod.v20210323.models.CreateRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DeleteRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeDomainListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordFilterListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyRecordResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文件名：DnspodController.java
 * 功能：腾讯云DNSPod控制器，提供域名解析记录查询API接口
 * 作者：CodeBuddy
 * 创建时间：2025-08-16
 * 版本：v1.0.0
 */
@RestController
@RequestMapping("/api/dnspod")
public class DnspodController {

    @Autowired
    private DnspodService dnspodService;

    /**
     * 获取域名列表
     * 
     * @param type 域名分组类型（可选，默认为ALL）
     * @param offset 记录开始的偏移（可选，默认为0）
     * @param limit 要获取的域名数量（可选，默认为20）
     * @param groupId 分组ID（可选）
     * @param keyword 根据关键字搜索域名（可选）
     * @return 域名列表
     */
    @GetMapping("/domains")
    public ApiResponse<DescribeDomainListResponse> getDomainList(
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) String keyword) {
        
        try {
            DescribeDomainListResponse response = dnspodService.getDomainList(
                type, offset, limit, groupId, keyword);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取域名列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取域名的解析记录列表（完整版）
     * 
     * @param domain 域名，如 example.com
     * @param domainId 域名ID（可选，优先级比domain高）
     * @param subdomain 解析记录的主机头（可选）
     * @param recordType 记录类型（可选，如A、CNAME、NS等）
     * @param recordLine 线路名称（可选）
     * @param recordLineId 线路ID（可选，优先级比recordLine高）
     * @param groupId 分组ID（可选）
     * @param keyword 关键字搜索（可选，支持搜索主机头和记录值）
     * @param sortField 排序字段（可选，支持name,line,type,value,weight,mx,ttl,updated_on）
     * @param sortType 排序方式（可选，ASC或DESC，默认ASC）
     * @param offset 偏移量（可选，默认0）
     * @param limit 限制数量（可选，默认100，最大3000）
     * @return 解析记录列表响应
     */
    @GetMapping("/records/list")
    public ApiResponse<DescribeRecordListResponse> getRecordList(
            @RequestParam String domain,
            @RequestParam(required = false) Integer domainId,
            @RequestParam(required = false) String subdomain,
            @RequestParam(required = false) String recordType,
            @RequestParam(required = false) String recordLine,
            @RequestParam(required = false) String recordLineId,
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortType,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit) {
        
        try {
            DescribeRecordListResponse response = dnspodService.getRecordList(
                domain, domainId, subdomain, recordType, recordLine, recordLineId,
                groupId, keyword, sortField, sortType, offset, limit);
            return ApiResponse.success("获取解析记录列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取解析记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取域名的解析记录列表（JSON格式）
     * 
     * @param requestBody 包含查询参数的JSON对象
     * @return 解析记录列表响应
     */
    @PostMapping(value = "/records/list", consumes = "application/json")
    public ApiResponse<DescribeRecordListResponse> getRecordListJson(@RequestBody RecordRequest requestBody) {
        try {
            DescribeRecordListResponse response = dnspodService.getRecordList(
                requestBody.getDomain(),
                requestBody.getDomainId(),
                requestBody.getSubDomain(),
                requestBody.getRecordType(),
                requestBody.getRecordLine(),
                requestBody.getRecordLineId(),
                requestBody.getGroupId(),
                requestBody.getKeyword(),
                requestBody.getSortField(),
                requestBody.getSortType(),
                requestBody.getOffset(),
                requestBody.getLimit());
            return ApiResponse.success("获取解析记录列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取解析记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取域名的解析记录筛选列表
     * 
     * @param domain 域名，如 example.com
     * @param remark 备注信息（可选）
     * @param subDomain 子域名（可选）
     * @param recordType 记录类型（可选）
     * @param limit 限制数量（可选）
     * @param offset 偏移量（可选）
     * @return 解析记录列表响应
     */
    @GetMapping("/records")
    public ApiResponse<DescribeRecordFilterListResponse> getRecords(
            @RequestParam String domain,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String subDomain,
            @RequestParam(required = false) String recordType,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        
        try {
            DescribeRecordFilterListResponse response = dnspodService.getRecordFilterList(
                domain, remark, subDomain, recordType, limit, offset);
            return ApiResponse.success("获取解析记录列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取解析记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定域名的所有解析记录（简化版）
     * 
     * @param domain 域名
     * @return 解析记录列表
     */
    @GetMapping("/records/{domain}")
    public ApiResponse<DescribeRecordFilterListResponse> getRecordsByDomain(@PathVariable String domain) {
        try {
            DescribeRecordFilterListResponse response = dnspodService.getRecordFilterList(
                domain, null, null, null, 100, 0);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取域名解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加域名解析记录
     * 
     * @param domain 域名（必填）
     * @param recordType 记录类型（必填，如A、CNAME等）
     * @param value 记录值（必填，如IP地址）
     * @param recordLine 记录线路（可选，默认"默认"）
     * @param subDomain 主机记录（可选，如www）
     * @param ttl TTL值（可选，默认600）
     * @param mx MX优先级（可选）
     * @param weight 权重（可选）
     * @param status 记录状态（可选，默认ENABLE）
     * @param remark 备注（可选）
     * @return ApiResponse<CreateRecordResponse> 统一响应格式
     */
    @PostMapping(value = "/records", consumes = "application/x-www-form-urlencoded")
    public ApiResponse<CreateRecordResponse> createRecord(
            @RequestParam String domain,
            @RequestParam String recordType,
            @RequestParam String value,
            @RequestParam(required = false, defaultValue = "默认") String recordLine,
            @RequestParam(required = false) String subDomain,
            @RequestParam(required = false) Long ttl,
            @RequestParam(required = false) Long mx,
            @RequestParam(required = false) Long weight,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String remark) {
        
        try {
            CreateRecordResponse response = dnspodService.createRecord(
                domain, recordType, recordLine, value, subDomain, 
                ttl, mx, weight, status, remark
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "创建域名解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加域名解析记录（JSON格式）
     * 
     * @param requestBody 包含所有参数的JSON对象
     * @return ApiResponse<CreateRecordResponse> 统一响应格式
     */
    @PostMapping(value = "/records", consumes = "application/json")
    public ApiResponse<CreateRecordResponse> createRecordJson(@RequestBody RecordRequest requestBody) {
        try {
            CreateRecordResponse response = dnspodService.createRecord(
                requestBody.getDomain(), 
                requestBody.getRecordType(), 
                requestBody.getRecordLine() != null ? requestBody.getRecordLine() : "默认", 
                requestBody.getValue(), 
                requestBody.getSubDomain(), 
                requestBody.getTtl(), 
                requestBody.getMx(), 
                requestBody.getWeight(), 
                requestBody.getStatus(), 
                requestBody.getRemark()
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "创建域名解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除域名解析记录
     * 
     * @param domain 域名（必填）
     * @param recordId 记录ID（必填）
     * @param domainId 域名ID（可选）
     * @return ApiResponse<DeleteRecordResponse> 统一响应格式
     */
    @PostMapping(value = "/records/delete", consumes = "application/x-www-form-urlencoded")
    public ApiResponse<DeleteRecordResponse> deleteRecord(
            @RequestParam String domain,
            @RequestParam Long recordId,
            @RequestParam(required = false) Long domainId) {
        
        try {
            DeleteRecordResponse response = dnspodService.deleteRecord(domain, recordId, domainId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "删除域名解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除域名解析记录（JSON格式）
     * 
     * @param requestBody 包含所有参数的JSON对象
     * @return ApiResponse<DeleteRecordResponse> 统一响应格式
     */
    @PostMapping(value = "/records/delete", consumes = "application/json")
    public ApiResponse<DeleteRecordResponse> deleteRecordJson(@RequestBody RecordRequest requestBody) {
        try {
            DeleteRecordResponse response = dnspodService.deleteRecord(
                requestBody.getDomain(), 
                requestBody.getRecordId(), 
                requestBody.getDomainId() != null ? requestBody.getDomainId().longValue() : null
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "删除域名解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改域名解析记录
     * 
     * @param domain 域名（必填）
     * @param recordId 记录ID（必填）
     * @param recordType 记录类型（必填，如A、CNAME等）
     * @param value 记录值（必填，如IP地址）
     * @param recordLine 记录线路（可选，默认"默认"）
     * @param subDomain 主机记录（可选，如www）
     * @param domainId 域名ID（可选）
     * @param ttl TTL值（可选）
     * @param mx MX优先级（可选）
     * @param weight 权重（可选）
     * @param status 记录状态（可选）
     * @param remark 备注（可选）
     * @return ApiResponse<ModifyRecordResponse> 统一响应格式
     */
    @PostMapping(value = "/records/modify", consumes = "application/x-www-form-urlencoded")
    public ApiResponse<ModifyRecordResponse> modifyRecord(
            @RequestParam String domain,
            @RequestParam Long recordId,
            @RequestParam String recordType,
            @RequestParam String value,
            @RequestParam(required = false, defaultValue = "默认") String recordLine,
            @RequestParam(required = false) String subDomain,
            @RequestParam(required = false) Long domainId,
            @RequestParam(required = false) Long ttl,
            @RequestParam(required = false) Long mx,
            @RequestParam(required = false) Long weight,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String remark) {
        
        try {
            ModifyRecordResponse response = dnspodService.modifyRecord(
                domain, recordId, recordType, recordLine, value, subDomain,
                domainId, ttl, mx, weight, status, remark
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "修改域名解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改域名解析记录（JSON格式）
     * 
     * @param requestBody 包含所有参数的JSON对象
     * @return ApiResponse<ModifyRecordResponse> 统一响应格式
     */
    @PostMapping(value = "/records/modify", consumes = "application/json")
    public ApiResponse<ModifyRecordResponse> modifyRecordJson(@RequestBody RecordRequest requestBody) {
        try {
            ModifyRecordResponse response = dnspodService.modifyRecord(
                requestBody.getDomain(),
                requestBody.getRecordId(),
                requestBody.getRecordType(),
                requestBody.getRecordLine() != null ? requestBody.getRecordLine() : "默认",
                requestBody.getValue(),
                requestBody.getSubDomain(),
                requestBody.getDomainId() != null ? requestBody.getDomainId().longValue() : null,
                requestBody.getTtl(),
                requestBody.getMx(),
                requestBody.getWeight(),
                requestBody.getStatus(),
                requestBody.getRemark()
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "修改域名解析记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询指定三级域名是否可用（未被注册）- GET方式
     * 
     * @param subDomain 三级域名前缀（如test，将查询test.cblog.eu）
     * @param domain 主域名（如cblog.eu）
     * @return ApiResponse 包含域名可用状态的响应
     */
    @GetMapping("/available-subdomain")
    public ApiResponse<?> checkSubdomainAvailability(
            @RequestParam String subDomain,
            @RequestParam(defaultValue = "cblog.eu") String domain) {
        
        try {
            // 构建完整的三级域名
            String fullSubdomain = subDomain + "." + domain;
            
            // 查询该子域名是否存在解析记录
            DescribeRecordFilterListResponse response = dnspodService.getRecordFilterList(
                domain, null, subDomain, null, 10, 0);
            
            // 检查是否有记录
            boolean isAvailable = response.getRecordCountInfo().getTotalCount() == 0;
            
            if (isAvailable) {
                return ApiResponse.success("域名 " + fullSubdomain + " 可用，未被注册");
            } else {
                return ApiResponse.error(409, "域名 " + fullSubdomain + " 已被注册");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "查询域名可用性失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询指定三级域名是否可用（未被注册）- JSON格式
     * 
     * @param requestBody 包含查询参数的JSON对象
     * @return ApiResponse 包含域名可用状态的响应
     */
    @PostMapping(value = "/available-subdomain", consumes = "application/json")
    public ApiResponse<?> checkSubdomainAvailabilityJson(@RequestBody RecordRequest requestBody) {
        try {
            String subDomain = requestBody.getSubDomain();
            String domain = requestBody.getDomain() != null ? requestBody.getDomain() : "cblog.eu";
            
            if (subDomain == null || subDomain.isEmpty()) {
                return ApiResponse.error(400, "子域名前缀不能为空");
            }
            
            // 构建完整的三级域名
            String fullSubdomain = subDomain + "." + domain;
            
            // 查询该子域名是否存在解析记录
            DescribeRecordFilterListResponse response = dnspodService.getRecordFilterList(
                domain, null, subDomain, null, 10, 0);
            
            // 检查是否有记录
            boolean isAvailable = response.getRecordCountInfo().getTotalCount() == 0;
            
            if (isAvailable) {
                return ApiResponse.success("域名 " + fullSubdomain + " 可用，未被注册");
            } else {
                return ApiResponse.error(409, "域名 " + fullSubdomain + " 已被注册");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "查询域名可用性失败: " + e.getMessage());
        }
    }
}