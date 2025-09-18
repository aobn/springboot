package com.example.demo.mapper;

import com.example.demo.entity.Domain;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 文件名：DomainMapper.java
 * 功能：域名数据访问层接口
 * 作者：CodeBuddy
 * 创建时间：2025-08-21
 * 版本：v1.0.0
 */
@Mapper
public interface DomainMapper {
    
    /**
     * 插入域名信息
     * 
     * @param domain 域名实体
     * @return 影响行数
     */
    @Insert("INSERT INTO domain (domain_id, name, punycode, grade, grade_level, grade_title, " +
            "is_vip, owner, status, group_id, search_engine_push, record_count, ttl, " +
            "cname_speedup, dns_status, remark, vip_start_at, vip_end_at, vip_auto_renew, " +
            "created_on, updated_on) VALUES " +
            "(#{domainId}, #{name}, #{punycode}, #{grade}, #{gradeLevel}, #{gradeTitle}, " +
            "#{isVip}, #{owner}, #{status}, #{groupId}, #{searchEnginePush}, #{recordCount}, #{ttl}, " +
            "#{cnameSpeedup}, #{dnsStatus}, #{remark}, #{vipStartAt}, #{vipEndAt}, #{vipAutoRenew}, " +
            "#{createdOn}, #{updatedOn})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Domain domain);
    
    /**
     * 根据DNSPod域名ID更新域名信息
     * 
     * @param domain 域名实体
     * @return 影响行数
     */
    @Update("UPDATE domain SET name=#{name}, punycode=#{punycode}, grade=#{grade}, " +
            "grade_level=#{gradeLevel}, grade_title=#{gradeTitle}, is_vip=#{isVip}, " +
            "owner=#{owner}, status=#{status}, group_id=#{groupId}, " +
            "search_engine_push=#{searchEnginePush}, record_count=#{recordCount}, ttl=#{ttl}, " +
            "cname_speedup=#{cnameSpeedup}, dns_status=#{dnsStatus}, remark=#{remark}, " +
            "vip_start_at=#{vipStartAt}, vip_end_at=#{vipEndAt}, vip_auto_renew=#{vipAutoRenew}, " +
            "updated_on=#{updatedOn} WHERE domain_id=#{domainId}")
    int updateByDomainId(Domain domain);
    
    /**
     * 根据DNSPod域名ID查询域名信息
     * 
     * @param domainId DNSPod域名ID
     * @return 域名实体
     */
    @Select("SELECT * FROM domain WHERE domain_id = #{domainId}")
    Domain findByDomainId(Long domainId);
    
    /**
     * 根据域名名称查询域名信息
     * 
     * @param name 域名名称
     * @return 域名实体
     */
    @Select("SELECT * FROM domain WHERE name = #{name}")
    Domain findByName(String name);
    
    /**
     * 查询所有域名信息
     * 
     * @return 域名列表
     */
    @Select("SELECT * FROM domain ORDER BY create_time DESC")
    List<Domain> findAll();
    
    /**
     * 根据状态查询域名信息
     * 
     * @param status 域名状态
     * @return 域名列表
     */
    @Select("SELECT * FROM domain WHERE status = #{status} ORDER BY create_time DESC")
    List<Domain> findByStatus(String status);
    
    /**
     * 根据分组ID查询域名信息
     * 
     * @param groupId 分组ID
     * @return 域名列表
     */
    @Select("SELECT * FROM domain WHERE group_id = #{groupId} ORDER BY create_time DESC")
    List<Domain> findByGroupId(Integer groupId);
    
    /**
     * 根据所有者查询域名信息
     * 
     * @param owner 所有者
     * @return 域名列表
     */
    @Select("SELECT * FROM domain WHERE owner = #{owner} ORDER BY create_time DESC")
    List<Domain> findByOwner(String owner);
    
    /**
     * 根据关键字搜索域名（模糊匹配域名名称）
     * 
     * @param keyword 关键字
     * @return 域名列表
     */
    @Select("SELECT * FROM domain WHERE name LIKE CONCAT('%', #{keyword}, '%') ORDER BY create_time DESC")
    List<Domain> searchByKeyword(String keyword);
    
    /**
     * 分页查询域名信息
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 域名列表
     */
    @Select("SELECT * FROM domain ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Domain> findWithPagination(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 统计域名总数
     * 
     * @return 域名总数
     */
    @Select("SELECT COUNT(*) FROM domain")
    int countAll();
    
    /**
     * 根据状态统计域名数量
     * 
     * @param status 域名状态
     * @return 域名数量
     */
    @Select("SELECT COUNT(*) FROM domain WHERE status = #{status}")
    int countByStatus(String status);
    
    /**
     * 根据DNSPod域名ID删除域名信息
     * 
     * @param domainId DNSPod域名ID
     * @return 影响行数
     */
    @Delete("DELETE FROM domain WHERE domain_id = #{domainId}")
    int deleteByDomainId(Long domainId);
    
    /**
     * 批量插入或更新域名信息（MySQL特有语法）
     * 
     * @param domain 域名实体
     * @return 影响行数
     */
    @Insert("INSERT INTO domain (domain_id, name, punycode, grade, grade_level, grade_title, " +
            "is_vip, owner, status, group_id, search_engine_push, record_count, ttl, " +
            "cname_speedup, dns_status, remark, vip_start_at, vip_end_at, vip_auto_renew, " +
            "created_on, updated_on) VALUES " +
            "(#{domainId}, #{name}, #{punycode}, #{grade}, #{gradeLevel}, #{gradeTitle}, " +
            "#{isVip}, #{owner}, #{status}, #{groupId}, #{searchEnginePush}, #{recordCount}, #{ttl}, " +
            "#{cnameSpeedup}, #{dnsStatus}, #{remark}, #{vipStartAt}, #{vipEndAt}, #{vipAutoRenew}, " +
            "#{createdOn}, #{updatedOn}) " +
            "ON DUPLICATE KEY UPDATE " +
            "name=VALUES(name), punycode=VALUES(punycode), grade=VALUES(grade), " +
            "grade_level=VALUES(grade_level), grade_title=VALUES(grade_title), is_vip=VALUES(is_vip), " +
            "owner=VALUES(owner), status=VALUES(status), group_id=VALUES(group_id), " +
            "search_engine_push=VALUES(search_engine_push), record_count=VALUES(record_count), " +
            "ttl=VALUES(ttl), cname_speedup=VALUES(cname_speedup), dns_status=VALUES(dns_status), " +
            "remark=VALUES(remark), vip_start_at=VALUES(vip_start_at), vip_end_at=VALUES(vip_end_at), " +
            "vip_auto_renew=VALUES(vip_auto_renew), updated_on=VALUES(updated_on)")
    int insertOrUpdate(Domain domain);
    
    /**
     * 获取可用的域名后缀（从域名名称中提取）
     * 
     * @return 域名后缀列表
     */
    @Select("SELECT DISTINCT name FROM domain WHERE status = 'ENABLE' ORDER BY name ASC")
    List<String> findAvailableDomainSuffixes();
}