package com.example.demo.service;

import com.tencentcloudapi.dnspod.v20210323.models.CreateRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DeleteRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeDomainListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordFilterListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyRecordResponse;

/**
 * 文件名：DnspodService.java
 * 功能：腾讯云DNSPod服务接口，提供域名解析记录查询和管理功能
 * 作者：CodeBuddy
 * 创建时间：2025-08-16
 * 版本：v1.1.0
 * 备注：新增域名列表查询功能
 */
public interface DnspodService {
    
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
    DescribeDomainListResponse getDomainList(String type, Integer offset, Integer limit, 
                                           Integer groupId, String keyword);
    
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
    DescribeRecordFilterListResponse getRecordFilterList(String domain, String remark, 
                                                        String subDomain, String recordType,
                                                        Integer limit, Integer offset);
    
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
    DescribeRecordListResponse getRecordList(String domain, Integer domainId, String subdomain,
                                           String recordType, String recordLine, String recordLineId,
                                           Integer groupId, String keyword, String sortField,
                                           String sortType, Integer offset, Integer limit);
    
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
     * @return 创建记录响应
     */
    CreateRecordResponse createRecord(String domain, String recordType, String recordLine, 
                                    String value, String subDomain, Long ttl, Long mx, 
                                    Long weight, String status, String remark);
    
    /**
     * 删除域名解析记录
     * 
     * @param domain 域名，如 example.com
     * @param recordId 记录ID
     * @param domainId 域名ID（可选）
     * @return 删除记录响应
     */
    DeleteRecordResponse deleteRecord(String domain, Long recordId, Long domainId);
    
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
     * @param remark 备注（可选）
     * @return 修改记录响应
     */
    ModifyRecordResponse modifyRecord(String domain, Long recordId, String recordType, 
                                     String recordLine, String value, String subDomain, 
                                     Long domainId, Long ttl, Long mx, Long weight, 
                                     String status, String remark);
}