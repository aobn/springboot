package com.example.demo.service.impl;

import com.example.demo.entity.Domain;
import com.example.demo.mapper.DomainMapper;
import com.example.demo.service.DomainService;
import com.example.demo.service.DnspodService;
import com.tencentcloudapi.dnspod.v20210323.models.DomainListItem;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeDomainListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件名：DomainServiceImpl.java
 * 功能：域名服务实现类，提供域名信息的业务逻辑处理
 * 作者：CodeBuddy
 * 创建时间：2025-08-21
 * 版本：v1.0.0
 */
@Service
@Transactional
public class DomainServiceImpl implements DomainService {
    
    @Autowired
    private DomainMapper domainMapper;
    
    @Autowired
    private DnspodService dnspodService;
    
    private static final DateTimeFormatter DNSPOD_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public List<String> getAvailableDomainSuffixes() {
        return domainMapper.findAvailableDomainSuffixes();
    }
    
    @Override
    public Domain saveDomain(Domain domain) {
        domainMapper.insert(domain);
        return domain;
    }
    
    @Override
    public Domain getDomainByDomainId(Long domainId) {
        return domainMapper.findByDomainId(domainId);
    }
    
    @Override
    public Domain getDomainByName(String name) {
        return domainMapper.findByName(name);
    }
    
    @Override
    public List<Domain> getAllDomains() {
        return domainMapper.findAll();
    }
    
    @Override
    public List<Domain> getDomainsByStatus(String status) {
        return domainMapper.findByStatus(status);
    }
    
    @Override
    public List<Domain> getDomainsByGroupId(Integer groupId) {
        return domainMapper.findByGroupId(groupId);
    }
    
    @Override
    public List<Domain> searchDomains(String keyword) {
        return domainMapper.searchByKeyword(keyword);
    }
    
    @Override
    public List<Domain> getDomainsWithPagination(Integer offset, Integer limit) {
        return domainMapper.findWithPagination(offset, limit);
    }
    
    @Override
    public int countAllDomains() {
        return domainMapper.countAll();
    }
    
    @Override
    public int countDomainsByStatus(String status) {
        return domainMapper.countByStatus(status);
    }
    
    @Override
    public Domain updateDomain(Domain domain) {
        domainMapper.updateByDomainId(domain);
        return domain;
    }
    
    @Override
    public boolean deleteDomainByDomainId(Long domainId) {
        return domainMapper.deleteByDomainId(domainId) > 0;
    }
    
    @Override
    public Domain convertFromDnspodItem(DomainListItem item) {
        Domain domain = new Domain();
        
        // 基本信息
        domain.setDomainId(item.getDomainId());
        domain.setName(item.getName());
        domain.setPunycode(item.getPunycode());
        domain.setGrade(item.getGrade());
        domain.setGradeLevel(item.getGradeLevel().intValue());
        domain.setGradeTitle(item.getGradeTitle());
        domain.setIsVip(item.getIsVip());
        domain.setOwner(item.getOwner());
        domain.setStatus(item.getStatus());
        domain.setGroupId(item.getGroupId().intValue());
        domain.setSearchEnginePush(item.getSearchEnginePush());
        domain.setRecordCount(item.getRecordCount().intValue());
        domain.setTtl(item.getTTL().intValue());
        domain.setCnameSpeedup(item.getCNAMESpeedup());
        domain.setDnsStatus(item.getDNSStatus());
        domain.setRemark(item.getRemark());
        domain.setVipAutoRenew(item.getVipAutoRenew());
        
        // 时间字段处理
        try {
            if (item.getCreatedOn() != null && !item.getCreatedOn().isEmpty()) {
                domain.setCreatedOn(LocalDateTime.parse(item.getCreatedOn(), DNSPOD_DATE_FORMATTER));
            }
            if (item.getUpdatedOn() != null && !item.getUpdatedOn().isEmpty()) {
                domain.setUpdatedOn(LocalDateTime.parse(item.getUpdatedOn(), DNSPOD_DATE_FORMATTER));
            }
            if (item.getVipStartAt() != null && !item.getVipStartAt().equals("0000-00-00 00:00:00")) {
                domain.setVipStartAt(LocalDateTime.parse(item.getVipStartAt(), DNSPOD_DATE_FORMATTER));
            }
            if (item.getVipEndAt() != null && !item.getVipEndAt().equals("0000-00-00 00:00:00")) {
                domain.setVipEndAt(LocalDateTime.parse(item.getVipEndAt(), DNSPOD_DATE_FORMATTER));
            }
        } catch (Exception e) {
            // 时间解析失败时记录日志但不影响其他字段
            System.err.println("解析域名时间字段失败: " + e.getMessage());
        }
        
        return domain;
    }
    
    @Override
    public int batchSaveOrUpdateDomains(List<Domain> domains) {
        int successCount = 0;
        for (Domain domain : domains) {
            try {
                domainMapper.insertOrUpdate(domain);
                successCount++;
            } catch (Exception e) {
                System.err.println("保存域名失败: " + domain.getName() + ", 错误: " + e.getMessage());
            }
        }
        return successCount;
    }
    
    @Override
    public int syncDomainsFromDnspod() {
        try {
            // 从DNSPod获取域名列表
            DescribeDomainListResponse response = dnspodService.getDomainList("ALL", 0, 100, null, null);
            
            if (response.getDomainList() == null || response.getDomainList().length == 0) {
                return 0;
            }
            
            // 转换为Domain实体列表
            List<Domain> domains = new ArrayList<>();
            for (DomainListItem item : response.getDomainList()) {
                Domain domain = convertFromDnspodItem(item);
                domains.add(domain);
            }
            
            // 批量保存或更新
            return batchSaveOrUpdateDomains(domains);
            
        } catch (Exception e) {
            throw new RuntimeException("同步DNSPod域名列表失败: " + e.getMessage(), e);
        }
    }
}