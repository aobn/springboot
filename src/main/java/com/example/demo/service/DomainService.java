package com.example.demo.service;

import com.example.demo.entity.Domain;
import com.tencentcloudapi.dnspod.v20210323.models.DomainListItem;

import java.util.List;

/**
 * 文件名：DomainService.java
 * 功能：域名服务接口，提供域名信息的业务逻辑处理
 * 作者：CodeBuddy
 * 创建时间：2025-08-21
 * 版本：v1.0.0
 */
public interface DomainService {
    
    /**
     * 保存域名信息
     * 
     * @param domain 域名实体
     * @return 保存后的域名实体
     */
    Domain saveDomain(Domain domain);
    
    /**
     * 根据DNSPod域名ID查询域名信息
     * 
     * @param domainId DNSPod域名ID
     * @return 域名实体
     */
    Domain getDomainByDomainId(Long domainId);
    
    /**
     * 根据域名名称查询域名信息
     * 
     * @param name 域名名称
     * @return 域名实体
     */
    Domain getDomainByName(String name);
    
    /**
     * 查询所有域名信息
     * 
     * @return 域名列表
     */
    List<Domain> getAllDomains();
    
    /**
     * 根据状态查询域名信息
     * 
     * @param status 域名状态
     * @return 域名列表
     */
    List<Domain> getDomainsByStatus(String status);
    
    /**
     * 根据分组ID查询域名信息
     * 
     * @param groupId 分组ID
     * @return 域名列表
     */
    List<Domain> getDomainsByGroupId(Integer groupId);
    
    /**
     * 根据关键字搜索域名
     * 
     * @param keyword 关键字
     * @return 域名列表
     */
    List<Domain> searchDomains(String keyword);
    
    /**
     * 分页查询域名信息
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 域名列表
     */
    List<Domain> getDomainsWithPagination(Integer offset, Integer limit);
    
    /**
     * 统计域名总数
     * 
     * @return 域名总数
     */
    int countAllDomains();
    
    /**
     * 根据状态统计域名数量
     * 
     * @param status 域名状态
     * @return 域名数量
     */
    int countDomainsByStatus(String status);
    
    /**
     * 更新域名信息
     * 
     * @param domain 域名实体
     * @return 更新后的域名实体
     */
    Domain updateDomain(Domain domain);
    
    /**
     * 根据DNSPod域名ID删除域名信息
     * 
     * @param domainId DNSPod域名ID
     * @return 是否删除成功
     */
    boolean deleteDomainByDomainId(Long domainId);
    
    /**
     * 从DNSPod API响应转换为Domain实体
     * 
     * @param domainListItem DNSPod API返回的域名项
     * @return Domain实体
     */
    Domain convertFromDnspodItem(DomainListItem domainListItem);
    
    /**
     * 批量保存或更新域名信息
     * 
     * @param domains 域名列表
     * @return 保存成功的数量
     */
    int batchSaveOrUpdateDomains(List<Domain> domains);
    
    /**
     * 同步DNSPod域名列表到本地数据库
     * 
     * @return 同步成功的域名数量
     */
    int syncDomainsFromDnspod();
    
    /**
     * 获取可用的域名后缀列表
     * 
     * @return 域名名称列表
     */
    List<String> getAvailableDomainSuffixes();
}