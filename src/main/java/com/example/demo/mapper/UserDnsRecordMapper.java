package com.example.demo.mapper;

import com.example.demo.entity.UserDnsRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户DNS解析记录Mapper接口
 * 提供用户DNS解析记录的数据库操作方法
 * 
 * @author CodeBuddy
 * @since 2025-08-25
 */
@Mapper
public interface UserDnsRecordMapper {
    
    /**
     * 插入用户DNS解析记录
     * 
     * @param record 用户DNS解析记录对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO user_dns_record (user_id, subdomain_id, record_id, name, type, value, line, line_id, ttl, mx, weight, status, remark, monitor_status, updated_on, sync_status, sync_error) " +
            "VALUES (#{userId}, #{subdomainId}, #{recordId}, #{name}, #{type}, #{value}, #{line}, #{lineId}, #{ttl}, #{mx}, #{weight}, #{status}, #{remark}, #{monitorStatus}, #{updatedOn}, #{syncStatus}, #{syncError})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserDnsRecord record);
    
    /**
     * 根据ID查询用户DNS解析记录
     * 
     * @param id 记录ID
     * @return 用户DNS解析记录对象
     */
    @Select("SELECT * FROM user_dns_record WHERE id = #{id}")
    UserDnsRecord selectById(Long id);
    
    /**
     * 根据用户ID查询DNS解析记录列表
     * 
     * @param userId 用户ID
     * @return DNS解析记录列表
     */
    @Select("SELECT * FROM user_dns_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserDnsRecord> selectByUserId(Long userId);
    
    /**
     * 根据子域名ID查询DNS解析记录列表
     * 
     * @param subdomainId 子域名ID
     * @return DNS解析记录列表
     */
    @Select("SELECT * FROM user_dns_record WHERE subdomain_id = #{subdomainId} ORDER BY create_time DESC")
    List<UserDnsRecord> selectBySubdomainId(Long subdomainId);
    
    /**
     * 根据用户ID和子域名ID查询DNS解析记录列表
     * 
     * @param userId 用户ID
     * @param subdomainId 子域名ID
     * @return DNS解析记录列表
     */
    @Select("SELECT * FROM user_dns_record WHERE user_id = #{userId} AND subdomain_id = #{subdomainId} ORDER BY create_time DESC")
    List<UserDnsRecord> selectByUserIdAndSubdomainId(Long userId, Long subdomainId);
    
    /**
     * 根据DNSPod记录ID查询用户DNS解析记录
     * 
     * @param recordId DNSPod记录ID
     * @return 用户DNS解析记录对象
     */
    @Select("SELECT * FROM user_dns_record WHERE record_id = #{recordId}")
    UserDnsRecord selectByRecordId(Long recordId);
    
    /**
     * 根据同步状态查询DNS解析记录列表
     * 
     * @param syncStatus 同步状态
     * @return DNS解析记录列表
     */
    @Select("SELECT * FROM user_dns_record WHERE sync_status = #{syncStatus} ORDER BY create_time ASC")
    List<UserDnsRecord> selectBySyncStatus(String syncStatus);
    
    /**
     * 更新用户DNS解析记录
     * 
     * @param record 用户DNS解析记录对象
     * @return 影响的行数
     */
    @Update("UPDATE user_dns_record SET record_id = #{recordId}, name = #{name}, type = #{type}, value = #{value}, " +
            "line = #{line}, line_id = #{lineId}, ttl = #{ttl}, mx = #{mx}, weight = #{weight}, status = #{status}, " +
            "remark = #{remark}, monitor_status = #{monitorStatus}, updated_on = #{updatedOn}, sync_status = #{syncStatus}, " +
            "sync_error = #{syncError} WHERE id = #{id}")
    int update(UserDnsRecord record);
    
    /**
     * 更新同步状态
     * 
     * @param id 记录ID
     * @param syncStatus 同步状态
     * @param syncError 同步错误信息
     * @return 影响的行数
     */
    @Update("UPDATE user_dns_record SET sync_status = #{syncStatus}, sync_error = #{syncError} WHERE id = #{id}")
    int updateSyncStatus(Long id, String syncStatus, String syncError);
    
    /**
     * 更新DNSPod记录ID
     * 
     * @param id 记录ID
     * @param recordId DNSPod记录ID
     * @return 影响的行数
     */
    @Update("UPDATE user_dns_record SET record_id = #{recordId}, sync_status = 'SUCCESS' WHERE id = #{id}")
    int updateRecordId(Long id, Long recordId);
    
    /**
     * 根据ID删除用户DNS解析记录
     * 
     * @param id 记录ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM user_dns_record WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据用户ID删除所有DNS解析记录
     * 
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM user_dns_record WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 根据子域名ID删除所有DNS解析记录
     * 
     * @param subdomainId 子域名ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM user_dns_record WHERE subdomain_id = #{subdomainId}")
    int deleteBySubdomainId(Long subdomainId);
    
    /**
     * 统计用户的DNS解析记录数量
     * 
     * @param userId 用户ID
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM user_dns_record WHERE user_id = #{userId}")
    int countByUserId(Long userId);
    
    /**
     * 统计子域名的DNS解析记录数量
     * 
     * @param subdomainId 子域名ID
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM user_dns_record WHERE subdomain_id = #{subdomainId}")
    int countBySubdomainId(Long subdomainId);
    
    /**
     * 检查记录是否存在（用户ID + 子域名ID + 主机记录 + 记录类型）
     * 
     * @param userId 用户ID
     * @param subdomainId 子域名ID
     * @param name 主机记录
     * @param type 记录类型
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM user_dns_record WHERE user_id = #{userId} AND subdomain_id = #{subdomainId} AND name = #{name} AND type = #{type}")
    int existsByUserIdAndSubdomainIdAndNameAndType(Long userId, Long subdomainId, String name, String type);
    
    /**
     * 检查记录是否存在（用户ID + 子域名ID + 主机记录 + 记录类型 + 记录值）
     * 
     * @param userId 用户ID
     * @param subdomainId 子域名ID
     * @param name 主机记录
     * @param type 记录类型
     * @param value 记录值
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM user_dns_record WHERE user_id = #{userId} AND subdomain_id = #{subdomainId} AND name = #{name} AND type = #{type} AND value = #{value}")
    int existsByUserIdAndSubdomainIdAndNameAndTypeAndValue(Long userId, Long subdomainId, String name, String type, String value);
    
    /**
     * 管理员分页查询用户DNS记录列表（支持模糊搜索和多条件过滤）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @param keyword 搜索关键词
     * @param userId 用户ID
     * @param recordType 记录类型
     * @param status 记录状态
     * @param syncStatus 同步状态
     * @param domain 主域名
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return DNS记录列表
     */
    @Select("<script>" +
            "SELECT " +
            "    udr.id, udr.user_id, udr.subdomain_id, udr.record_id, udr.name, udr.type, udr.value, " +
            "    udr.line, udr.line_id, udr.ttl, udr.mx, udr.weight, udr.status, udr.remark, " +
            "    udr.monitor_status, udr.updated_on, udr.sync_status, udr.sync_error, " +
            "    udr.create_time, udr.update_time, " +
            "    u.username, u.email, " +
            "    us.subdomain, us.domain, us.full_domain " +
            "FROM user_dns_record udr " +
            "LEFT JOIN user u ON udr.user_id = u.id " +
            "LEFT JOIN user_subdomain us ON udr.subdomain_id = us.id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "    AND (" +
            "        u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR u.email LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR us.subdomain LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR us.domain LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR us.full_domain LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR udr.name LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR udr.value LIKE CONCAT('%', #{keyword}, '%') " +
            "    ) " +
            "</if>" +
            "<if test='userId != null'>" +
            "    AND udr.user_id = #{userId} " +
            "</if>" +
            "<if test='recordType != null and recordType != \"\"'>" +
            "    AND udr.type = #{recordType} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "    AND udr.status = #{status} " +
            "</if>" +
            "<if test='syncStatus != null and syncStatus != \"\"'>" +
            "    AND udr.sync_status = #{syncStatus} " +
            "</if>" +
            "<if test='domain != null and domain != \"\"'>" +
            "    AND us.domain = #{domain} " +
            "</if>" +
            "ORDER BY " +
            "<choose>" +
            "    <when test='sortBy == \"update_time\"'>udr.update_time</when>" +
            "    <when test='sortBy == \"name\"'>udr.name</when>" +
            "    <when test='sortBy == \"type\"'>udr.type</when>" +
            "    <when test='sortBy == \"status\"'>udr.status</when>" +
            "    <otherwise>udr.create_time</otherwise>" +
            "</choose> " +
            "<choose>" +
            "    <when test='sortDir == \"ASC\"'>ASC</when>" +
            "    <otherwise>DESC</otherwise>" +
            "</choose> " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<com.example.demo.dto.UserDnsRecordWithUser> selectUserDnsRecordsForAdmin(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("keyword") String keyword,
            @Param("userId") Long userId,
            @Param("recordType") String recordType,
            @Param("status") String status,
            @Param("syncStatus") String syncStatus,
            @Param("domain") String domain,
            @Param("sortBy") String sortBy,
            @Param("sortDir") String sortDir
    );
    
    /**
     * 管理员查询用户DNS记录总数（支持模糊搜索和多条件过滤）
     * 
     * @param keyword 搜索关键词
     * @param userId 用户ID
     * @param recordType 记录类型
     * @param status 记录状态
     * @param syncStatus 同步状态
     * @param domain 主域名
     * @return 总记录数
     */
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM user_dns_record udr " +
            "LEFT JOIN user u ON udr.user_id = u.id " +
            "LEFT JOIN user_subdomain us ON udr.subdomain_id = us.id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "    AND (" +
            "        u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR u.email LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR us.subdomain LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR us.domain LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR us.full_domain LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR udr.name LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR udr.value LIKE CONCAT('%', #{keyword}, '%') " +
            "    ) " +
            "</if>" +
            "<if test='userId != null'>" +
            "    AND udr.user_id = #{userId} " +
            "</if>" +
            "<if test='recordType != null and recordType != \"\"'>" +
            "    AND udr.type = #{recordType} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "    AND udr.status = #{status} " +
            "</if>" +
            "<if test='syncStatus != null and syncStatus != \"\"'>" +
            "    AND udr.sync_status = #{syncStatus} " +
            "</if>" +
            "<if test='domain != null and domain != \"\"'>" +
            "    AND us.domain = #{domain} " +
            "</if>" +
            "</script>")
    int countUserDnsRecordsForAdmin(
            @Param("keyword") String keyword,
            @Param("userId") Long userId,
            @Param("recordType") String recordType,
            @Param("status") String status,
            @Param("syncStatus") String syncStatus,
            @Param("domain") String domain
    );
}