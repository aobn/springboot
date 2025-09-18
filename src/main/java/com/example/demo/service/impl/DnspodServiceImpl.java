package com.example.demo.service.impl;

import com.example.demo.service.DnspodService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.CreateRecordRequest;
import com.tencentcloudapi.dnspod.v20210323.models.CreateRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DeleteRecordRequest;
import com.tencentcloudapi.dnspod.v20210323.models.DeleteRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeDomainListRequest;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeDomainListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordFilterListRequest;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordFilterListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordListRequest;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyRecordRequest;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyRecordResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 文件名：DnspodServiceImpl.java
 * 功能：腾讯云DNSPod服务实现类，提供域名解析记录查询、添加、修改和删除功能
 * 作者：CodeBuddy
 * 创建时间：2025-08-16
 * 版本：v1.0.0
 */
@Service
public class DnspodServiceImpl implements DnspodService {

    @Value("${tencent.cloud.secret-id}")
    private String secretId;

    @Value("${tencent.cloud.secret-key}")
    private String secretKey;

    @Value("${tencent.cloud.region}")
    private String region;

    /**
     * 获取域名列表
     * 
     * @param type 域名分组类型（可选，默认为ALL）
     * @param offset 记录开始的偏移（可选，默认为0）
     * @param limit 要获取的域名数量（可选，默认为20）
     * @param groupId 分组ID（可选）
     * @param keyword 根据关键字搜索域名（可选）
     * @return 域名列表响应
     */
    @Override
    public DescribeDomainListResponse getDomainList(String type, Integer offset, Integer limit, 
                                                   Integer groupId, String keyword) {
        try {
            // 调试信息：检查配置是否正确加载
            System.out.println("=== DNSPod API 配置检查 ===");
            System.out.println("SecretId: " + (secretId != null ? secretId.substring(0, Math.min(10, secretId.length())) + "..." : "null"));
            System.out.println("SecretKey: " + (secretKey != null ? secretKey.substring(0, Math.min(10, secretKey.length())) + "..." : "null"));
            System.out.println("Region: " + region);
            
            if (secretId == null || secretId.trim().isEmpty()) {
                throw new RuntimeException("SecretId 配置为空，请检查 application.properties 中的 tencent.cloud.secret-id 配置");
            }
            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new RuntimeException("SecretKey 配置为空，请检查 application.properties 中的 tencent.cloud.secret-key 配置");
            }
            
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
            Credential cred = new Credential(secretId, secretKey);
            
            // 实例化一个http选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            
            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            
            // 实例化要请求产品的client对象
            DnspodClient client = new DnspodClient(cred, region, clientProfile);
            
            // 实例化一个请求对象
            DescribeDomainListRequest req = new DescribeDomainListRequest();
            
            // 设置可选参数
            if (type != null && !type.isEmpty()) {
                req.setType(type);
            } else {
                req.setType("ALL"); // 默认获取所有域名
            }
            if (offset != null) {
                req.setOffset(offset.longValue());
            } else {
                req.setOffset(0L); // 默认从第一条记录开始
            }
            if (limit != null) {
                req.setLimit(limit.longValue());
            } else {
                req.setLimit(20L); // 默认获取20个域名
            }
            if (groupId != null) {
                req.setGroupId(groupId.longValue());
            }
            if (keyword != null && !keyword.isEmpty()) {
                req.setKeyword(keyword);
            }
            
            // 返回的resp是一个DescribeDomainListResponse的实例
            return client.DescribeDomainList(req);
            
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("调用腾讯云DNSPod获取域名列表API失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取域名的解析记录列表
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
    @Override
    public DescribeRecordListResponse getRecordList(String domain, Integer domainId, String subdomain,
                                                   String recordType, String recordLine, String recordLineId,
                                                   Integer groupId, String keyword, String sortField,
                                                   String sortType, Integer offset, Integer limit) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
            Credential cred = new Credential(secretId, secretKey);
            
            // 实例化一个http选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            
            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            
            // 实例化要请求产品的client对象
            DnspodClient client = new DnspodClient(cred, region, clientProfile);
            
            // 实例化一个请求对象
            DescribeRecordListRequest req = new DescribeRecordListRequest();
            
            // 设置必填参数 - domain是必填的
            req.setDomain(domain);
            
            // 设置可选参数
            if (domainId != null) {
                req.setDomainId(domainId.longValue());
            }
            if (subdomain != null && !subdomain.isEmpty()) {
                req.setSubdomain(subdomain);
            }
            if (recordType != null && !recordType.isEmpty()) {
                req.setRecordType(recordType);
            }
            if (recordLine != null && !recordLine.isEmpty()) {
                req.setRecordLine(recordLine);
            }
            if (recordLineId != null && !recordLineId.isEmpty()) {
                req.setRecordLineId(recordLineId);
            }
            if (groupId != null) {
                req.setGroupId(groupId.longValue());
            }
            if (keyword != null && !keyword.isEmpty()) {
                req.setKeyword(keyword);
            }
            if (sortField != null && !sortField.isEmpty()) {
                req.setSortField(sortField);
            }
            if (sortType != null && !sortType.isEmpty()) {
                req.setSortType(sortType);
            } else {
                req.setSortType("ASC"); // 默认正序排列
            }
            if (offset != null) {
                req.setOffset(offset.longValue());
            } else {
                req.setOffset(0L); // 默认从第一条记录开始
            }
            if (limit != null) {
                req.setLimit(limit.longValue());
            } else {
                req.setLimit(100L); // 默认获取100条记录
            }
            
            // 返回的resp是一个DescribeRecordListResponse的实例
            return client.DescribeRecordList(req);
            
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("调用腾讯云DNSPod获取解析记录列表API失败: " + e.getMessage(), e);
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
    @Override
    public DescribeRecordFilterListResponse getRecordFilterList(String domain, String remark, 
                                                               String subDomain, String recordType,
                                                               Integer limit, Integer offset) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
            Credential cred = new Credential(secretId, secretKey);
            
            // 实例化一个http选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            
            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            
            // 实例化要请求产品的client对象
            DnspodClient client = new DnspodClient(cred, region, clientProfile);
            
            // 实例化一个请求对象
            DescribeRecordFilterListRequest req = new DescribeRecordFilterListRequest();
            req.setDomain(domain);
            
            // 设置可选参数
            // 注意：ModifyRecordRequest不支持设置remark参数
            if (subDomain != null && !subDomain.isEmpty()) {
                req.setSubDomain(subDomain);
            }
            if (recordType != null && !recordType.isEmpty()) {
                req.setRecordType(new String[]{recordType});
            }
            if (limit != null) {
                req.setLimit(limit.longValue());
            }
            if (offset != null) {
                req.setOffset(offset.longValue());
            }
            
            // 返回的resp是一个DescribeRecordFilterListResponse的实例
            return client.DescribeRecordFilterList(req);
            
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("调用腾讯云DNSPod API失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 添加域名解析记录
     * 
     * @param domain 域名，如 example.com
     * @param recordType 记录类型，如 A、CNAME、MX等
     * @param recordLine 记录线路，如 "默认"
     * @param value 记录值，如 IP地址
     * @param subDomain 主机记录，如 www（可选，默认为@）
     * @param ttl TTL值（可选，默认600）
     * @param mx MX优先级（可选，MX记录时必填）
     * @param weight 权重（可选，0-100）
     * @param status 记录状态（可选，ENABLE或DISABLE）
     * @param remark 备注（可选）
     * @return 创建记录响应
     */
    @Override
    public CreateRecordResponse createRecord(String domain, String recordType, String recordLine, 
                                           String value, String subDomain, Long ttl, Long mx, 
                                           Long weight, String status, String remark) {
        try {
            // 调试信息：检查配置是否正确加载
            System.out.println("=== CreateRecord DNSPod API 配置检查 ===");
            System.out.println("SecretId: " + (secretId != null ? secretId.substring(0, Math.min(10, secretId.length())) + "..." : "null"));
            System.out.println("SecretKey: " + (secretKey != null ? secretKey.substring(0, Math.min(10, secretKey.length())) + "..." : "null"));
            System.out.println("Region: " + region);
            
            if (secretId == null || secretId.trim().isEmpty()) {
                throw new RuntimeException("SecretId 配置为空，请检查 application.properties 中的 tencent.cloud.secret-id 配置");
            }
            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new RuntimeException("SecretKey 配置为空，请检查 application.properties 中的 tencent.cloud.secret-key 配置");
            }
            
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
            Credential cred = new Credential(secretId, secretKey);
            
            // 实例化一个http选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            
            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            
            // 实例化要请求产品的client对象
            DnspodClient client = new DnspodClient(cred, region, clientProfile);
            
            // 实例化一个请求对象
            CreateRecordRequest req = new CreateRecordRequest();
            req.setDomain(domain);
            req.setRecordType(recordType);
            req.setRecordLine(recordLine != null ? recordLine : "默认");
            req.setValue(value);
            
            // 设置可选参数
            if (subDomain != null && !subDomain.isEmpty()) {
                req.setSubDomain(subDomain);
            }
            if (ttl != null) {
                req.setTTL(ttl);
            } else {
                req.setTTL(600L); // 默认TTL为600秒
            }
            if (mx != null) {
                req.setMX(mx);
            }
            if (weight != null) {
                req.setWeight(weight);
            }
            if (status != null && !status.isEmpty()) {
                req.setStatus(status);
            } else {
                req.setStatus("ENABLE"); // 默认启用
            }
            if (remark != null && !remark.isEmpty()) {
                req.setRemark(remark);
            }
            
            // 返回的resp是一个CreateRecordResponse的实例
            return client.CreateRecord(req);
            
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("调用腾讯云DNSPod创建记录API失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除域名解析记录
     * 
     * @param domain 域名，如 example.com
     * @param recordId 记录ID
     * @param domainId 域名ID（可选）
     * @return 删除记录响应
     */
    @Override
    public DeleteRecordResponse deleteRecord(String domain, Long recordId, Long domainId) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
            Credential cred = new Credential(secretId, secretKey);
            
            // 实例化一个http选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            
            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            
            // 实例化要请求产品的client对象
            DnspodClient client = new DnspodClient(cred, region, clientProfile);
            
            // 实例化一个请求对象
            DeleteRecordRequest req = new DeleteRecordRequest();
            req.setDomain(domain);
            req.setRecordId(recordId);
            
            // 设置可选参数
            if (domainId != null) {
                req.setDomainId(domainId);
            }
            
            // 返回的resp是一个DeleteRecordResponse的实例
            return client.DeleteRecord(req);
            
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("调用腾讯云DNSPod删除记录API失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 修改域名解析记录
     * 
     * @param domain 域名，如 example.com
     * @param recordId 记录ID
     * @param recordType 记录类型，如 A、CNAME、MX等
     * @param recordLine 记录线路，如 "默认"
     * @param value 记录值，如 IP地址
     * @param subDomain 主机记录，如 www（可选）
     * @param domainId 域名ID（可选）
     * @param ttl TTL值（可选）
     * @param mx MX优先级（可选，MX记录时必填）
     * @param weight 权重（可选，0-100）
     * @param status 记录状态（可选，ENABLE或DISABLE）
     * @return 修改记录响应
     */
    @Override
    public ModifyRecordResponse modifyRecord(String domain, Long recordId, String recordType, 
                                           String recordLine, String value, String subDomain, 
                                           Long domainId, Long ttl, Long mx, Long weight, 
                                           String status, String remark) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
            Credential cred = new Credential(secretId, secretKey);
            
            // 实例化一个http选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            
            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            
            // 实例化要请求产品的client对象
            DnspodClient client = new DnspodClient(cred, region, clientProfile);
            
            // 实例化一个请求对象
            ModifyRecordRequest req = new ModifyRecordRequest();
            req.setDomain(domain);
            req.setRecordId(recordId);
            req.setRecordType(recordType);
            req.setRecordLine(recordLine != null ? recordLine : "默认");
            req.setValue(value);
            
            // 设置可选参数
            if (subDomain != null && !subDomain.isEmpty()) {
                req.setSubDomain(subDomain);
            }
            if (domainId != null) {
                req.setDomainId(domainId);
            }
            if (ttl != null) {
                req.setTTL(ttl);
            }
            if (mx != null) {
                req.setMX(mx);
            }
            if (weight != null) {
                req.setWeight(weight);
            }
            if (status != null && !status.isEmpty()) {
                req.setStatus(status);
            }
            // 注意：ModifyRecordRequest不支持设置remark参数，腾讯云API限制
            // if (remark != null && !remark.isEmpty()) {
            //     req.setRemark(remark);
            // }
            
            // 返回的resp是一个ModifyRecordResponse的实例
            return client.ModifyRecord(req);
            
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("调用腾讯云DNSPod修改记录API失败: " + e.getMessage(), e);
        }
    }
}