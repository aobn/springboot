package com.example.demo.service.impl;

import com.example.demo.entity.UserDnsRecord;
import com.example.demo.mapper.UserDnsRecordMapper;
import com.example.demo.service.UserDnsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户DNS解析记录服务实现类
 * 提供用户DNS解析记录的业务逻辑处理
 * 
 * @author CodeBuddy
 * @since 2025-08-25
 */
@Service
public class UserDnsRecordServiceImpl implements UserDnsRecordService {
    
    @Autowired
    private UserDnsRecordMapper userDnsRecordMapper;
    
    @Override
    public UserDnsRecord createRecord(UserDnsRecord record) {
        // 检查记录是否已存在
        if (userDnsRecordMapper.existsByUserIdAndSubdomainIdAndNameAndType(
                record.getUserId(), record.getSubdomainId(), record.getName(), record.getType()) > 0) {
            throw new RuntimeException("DNS解析记录已存在");
        }
        
        userDnsRecordMapper.insert(record);
        return record;
    }
    
    @Override
    public UserDnsRecord getRecordById(Long id) {
        return userDnsRecordMapper.selectById(id);
    }
    
    @Override
    public List<UserDnsRecord> getRecordsByUserId(Long userId) {
        return userDnsRecordMapper.selectByUserId(userId);
    }
    
    @Override
    public List<UserDnsRecord> getRecordsBySubdomainId(Long subdomainId) {
        return userDnsRecordMapper.selectBySubdomainId(subdomainId);
    }
    
    @Override
    public List<UserDnsRecord> getRecordsByUserIdAndSubdomainId(Long userId, Long subdomainId) {
        return userDnsRecordMapper.selectByUserIdAndSubdomainId(userId, subdomainId);
    }
    
    @Override
    public UserDnsRecord getRecordByRecordId(Long recordId) {
        return userDnsRecordMapper.selectByRecordId(recordId);
    }
    
    @Override
    public List<UserDnsRecord> getRecordsBySyncStatus(String syncStatus) {
        return userDnsRecordMapper.selectBySyncStatus(syncStatus);
    }
    
    @Override
    public UserDnsRecord updateRecord(UserDnsRecord record) {
        userDnsRecordMapper.update(record);
        return record;
    }
    
    @Override
    public boolean updateSyncStatus(Long id, String syncStatus, String syncError) {
        return userDnsRecordMapper.updateSyncStatus(id, syncStatus, syncError) > 0;
    }
    
    @Override
    public boolean updateRecordId(Long id, Long recordId) {
        return userDnsRecordMapper.updateRecordId(id, recordId) > 0;
    }
    
    @Override
    public boolean deleteRecord(Long id) {
        return userDnsRecordMapper.deleteById(id) > 0;
    }
    
    @Override
    public int deleteRecordsByUserId(Long userId) {
        return userDnsRecordMapper.deleteByUserId(userId);
    }
    
    @Override
    public int deleteRecordsBySubdomainId(Long subdomainId) {
        return userDnsRecordMapper.deleteBySubdomainId(subdomainId);
    }
    
    @Override
    public int countRecordsByUserId(Long userId) {
        return userDnsRecordMapper.countByUserId(userId);
    }
    
    @Override
    public int countRecordsBySubdomainId(Long subdomainId) {
        return userDnsRecordMapper.countBySubdomainId(subdomainId);
    }
    
    @Override
    public boolean existsRecord(Long userId, Long subdomainId, String name, String type) {
        return userDnsRecordMapper.existsByUserIdAndSubdomainIdAndNameAndType(userId, subdomainId, name, type) > 0;
    }
    
    @Override
    public boolean syncRecordToDnsPod(Long recordId) {
        // TODO: 实现同步到DNSPod的逻辑
        // 这里可以调用DNSPod API进行同步
        return false;
    }
    
    @Override
    public int batchSyncPendingRecords() {
        // TODO: 实现批量同步待同步记录的逻辑
        // 获取所有PENDING状态的记录，逐个同步到DNSPod
        List<UserDnsRecord> pendingRecords = getRecordsBySyncStatus("PENDING");
        int successCount = 0;
        
        for (UserDnsRecord record : pendingRecords) {
            if (syncRecordToDnsPod(record.getId())) {
                successCount++;
            }
        }
        
        return successCount;
    }
}