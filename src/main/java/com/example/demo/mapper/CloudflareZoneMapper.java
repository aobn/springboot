package com.example.demo.mapper;

import com.example.demo.entity.CloudflareZone;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 文件名：CloudflareZoneMapper.java
 * 功能：Cloudflare域名(Zone)信息数据访问层
 * 作者：CodeBuddy
 * 创建时间：2025-09-25
 * 版本：v1.0.0
 */
@Mapper
public interface CloudflareZoneMapper {
    
    /**
     * 插入Cloudflare域名信息
     * @param zone Cloudflare域名信息
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO cloudflare_zone (
            zone_id, name, status, paused, type, development_mode,
            name_servers, original_name_servers, original_registrar, original_dnshost,
            created_on, modified_on, activated_on,
            vanity_name_servers, vanity_name_servers_ips,
            meta_step, meta_custom_certificate_quota, meta_page_rule_quota, meta_phishing_detected,
            owner_id, owner_type, owner_email,
            account_id, account_name, tenant_id, tenant_name, tenant_unit_id,
            plan_id, plan_name, plan_price, plan_currency, plan_frequency,
            plan_is_subscribed, plan_can_subscribe, plan_legacy_id, plan_legacy_discount, plan_externally_managed,
            permissions, sync_status, sync_error, last_sync_time, is_active, remark,
            dns_record_count, dns_record_limit, user_count, user_limit, is_full, auto_assign_enabled, next_prefix_hex
        ) VALUES (
            #{zoneId}, #{name}, #{status}, #{paused}, #{type}, #{developmentMode},
            #{nameServers,typeHandler=com.example.demo.config.JsonTypeHandler}, 
            #{originalNameServers,typeHandler=com.example.demo.config.JsonTypeHandler}, 
            #{originalRegistrar}, #{originalDnshost},
            #{createdOn}, #{modifiedOn}, #{activatedOn},
            #{vanityNameServers,typeHandler=com.example.demo.config.JsonTypeHandler}, 
            #{vanityNameServersIps,typeHandler=com.example.demo.config.JsonTypeHandler},
            #{metaStep}, #{metaCustomCertificateQuota}, #{metaPageRuleQuota}, #{metaPhishingDetected},
            #{ownerId}, #{ownerType}, #{ownerEmail},
            #{accountId}, #{accountName}, #{tenantId}, #{tenantName}, #{tenantUnitId},
            #{planId}, #{planName}, #{planPrice}, #{planCurrency}, #{planFrequency},
            #{planIsSubscribed}, #{planCanSubscribe}, #{planLegacyId}, #{planLegacyDiscount}, #{planExternallyManaged},
            #{permissions,typeHandler=com.example.demo.config.JsonTypeHandler}, 
            #{syncStatus}, #{syncError}, #{lastSyncTime}, #{isActive}, #{remark},
            #{dnsRecordCount}, #{dnsRecordLimit}, #{userCount}, #{userLimit}, 
            #{isFull}, #{autoAssignEnabled}, #{nextPrefixHex}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CloudflareZone zone);
    
    /**
     * 根据Zone ID更新域名信息
     * @param zone Cloudflare域名信息
     * @return 影响行数
     */
    @Update("""
        UPDATE cloudflare_zone SET
            name = #{name}, status = #{status}, paused = #{paused}, type = #{type}, 
            development_mode = #{developmentMode},
            name_servers = #{nameServers,typeHandler=com.example.demo.config.JsonTypeHandler},
            original_name_servers = #{originalNameServers,typeHandler=com.example.demo.config.JsonTypeHandler},
            original_registrar = #{originalRegistrar}, original_dnshost = #{originalDnshost},
            created_on = #{createdOn}, modified_on = #{modifiedOn}, activated_on = #{activatedOn},
            vanity_name_servers = #{vanityNameServers,typeHandler=com.example.demo.config.JsonTypeHandler},
            vanity_name_servers_ips = #{vanityNameServersIps,typeHandler=com.example.demo.config.JsonTypeHandler},
            meta_step = #{metaStep}, meta_custom_certificate_quota = #{metaCustomCertificateQuota},
            meta_page_rule_quota = #{metaPageRuleQuota}, meta_phishing_detected = #{metaPhishingDetected},
            owner_id = #{ownerId}, owner_type = #{ownerType}, owner_email = #{ownerEmail},
            account_id = #{accountId}, account_name = #{accountName},
            tenant_id = #{tenantId}, tenant_name = #{tenantName}, tenant_unit_id = #{tenantUnitId},
            plan_id = #{planId}, plan_name = #{planName}, plan_price = #{planPrice},
            plan_currency = #{planCurrency}, plan_frequency = #{planFrequency},
            plan_is_subscribed = #{planIsSubscribed}, plan_can_subscribe = #{planCanSubscribe},
            plan_legacy_id = #{planLegacyId}, plan_legacy_discount = #{planLegacyDiscount},
            plan_externally_managed = #{planExternallyManaged},
            permissions = #{permissions,typeHandler=com.example.demo.config.JsonTypeHandler},
            sync_status = #{syncStatus}, sync_error = #{syncError}, last_sync_time = #{lastSyncTime},
            is_active = #{isActive}, remark = #{remark},
            dns_record_count = #{dnsRecordCount}, dns_record_limit = #{dnsRecordLimit},
            user_count = #{userCount}, user_limit = #{userLimit}, is_full = #{isFull},
            auto_assign_enabled = #{autoAssignEnabled}, next_prefix_hex = #{nextPrefixHex}
        WHERE zone_id = #{zoneId}
    """)
    int updateByZoneId(CloudflareZone zone);
    
    /**
     * 根据Zone ID查询域名信息
     * @param zoneId Cloudflare Zone ID
     * @return Cloudflare域名信息
     */
    @Select("""
        SELECT * FROM cloudflare_zone WHERE zone_id = #{zoneId}
    """)
    @Results({
        @Result(property = "nameServers", column = "name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "originalNameServers", column = "original_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServers", column = "vanity_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServersIps", column = "vanity_name_servers_ips", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "permissions", column = "permissions", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "dnsRecordCount", column = "dns_record_count"),
        @Result(property = "dnsRecordLimit", column = "dns_record_limit"),
        @Result(property = "userCount", column = "user_count"),
        @Result(property = "userLimit", column = "user_limit"),
        @Result(property = "isFull", column = "is_full"),
        @Result(property = "autoAssignEnabled", column = "auto_assign_enabled"),
        @Result(property = "nextPrefixHex", column = "next_prefix_hex")
    })
    CloudflareZone findByZoneId(String zoneId);
    
    /**
     * 根据域名名称查询域名信息
     * @param name 域名名称
     * @return Cloudflare域名信息
     */
    @Select("""
        SELECT * FROM cloudflare_zone WHERE name = #{name}
    """)
    @Results({
        @Result(property = "nameServers", column = "name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "originalNameServers", column = "original_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServers", column = "vanity_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServersIps", column = "vanity_name_servers_ips", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "permissions", column = "permissions", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "dnsRecordCount", column = "dns_record_count"),
        @Result(property = "dnsRecordLimit", column = "dns_record_limit"),
        @Result(property = "userCount", column = "user_count"),
        @Result(property = "userLimit", column = "user_limit"),
        @Result(property = "isFull", column = "is_full"),
        @Result(property = "autoAssignEnabled", column = "auto_assign_enabled"),
        @Result(property = "nextPrefixHex", column = "next_prefix_hex")
    })
    CloudflareZone findByName(String name);
    
    /**
     * 查询所有域名信息
     * @return Cloudflare域名信息列表
     */
    @Select("""
        SELECT * FROM cloudflare_zone WHERE is_active = 1 ORDER BY create_time DESC
    """)
    @Results({
        @Result(property = "nameServers", column = "name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "originalNameServers", column = "original_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServers", column = "vanity_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServersIps", column = "vanity_name_servers_ips", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "permissions", column = "permissions", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "dnsRecordCount", column = "dns_record_count"),
        @Result(property = "dnsRecordLimit", column = "dns_record_limit"),
        @Result(property = "userCount", column = "user_count"),
        @Result(property = "userLimit", column = "user_limit"),
        @Result(property = "isFull", column = "is_full"),
        @Result(property = "autoAssignEnabled", column = "auto_assign_enabled"),
        @Result(property = "nextPrefixHex", column = "next_prefix_hex")
    })
    List<CloudflareZone> findAll();
    
    /**
     * 查询所有活跃的域名信息（别名方法）
     * @return 活跃的Cloudflare域名信息列表
     */
    default List<CloudflareZone> findAllActive() {
        return findAll();
    }
    
    /**
     * 根据状态查询域名信息
     * @param status 域名状态
     * @return Cloudflare域名信息列表
     */
    @Select("""
        SELECT * FROM cloudflare_zone WHERE status = #{status} AND is_active = 1 ORDER BY create_time DESC
    """)
    @Results({
        @Result(property = "nameServers", column = "name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "originalNameServers", column = "original_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServers", column = "vanity_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServersIps", column = "vanity_name_servers_ips", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "permissions", column = "permissions", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "dnsRecordCount", column = "dns_record_count"),
        @Result(property = "dnsRecordLimit", column = "dns_record_limit"),
        @Result(property = "userCount", column = "user_count"),
        @Result(property = "userLimit", column = "user_limit"),
        @Result(property = "isFull", column = "is_full"),
        @Result(property = "autoAssignEnabled", column = "auto_assign_enabled"),
        @Result(property = "nextPrefixHex", column = "next_prefix_hex")
    })
    List<CloudflareZone> findByStatus(String status);
    
    /**
     * 根据账户ID查询域名信息
     * @param accountId 账户ID
     * @return Cloudflare域名信息列表
     */
    @Select("""
        SELECT * FROM cloudflare_zone WHERE account_id = #{accountId} AND is_active = 1 ORDER BY create_time DESC
    """)
    @Results({
        @Result(property = "nameServers", column = "name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "originalNameServers", column = "original_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServers", column = "vanity_name_servers", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "vanityNameServersIps", column = "vanity_name_servers_ips", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "permissions", column = "permissions", typeHandler = com.example.demo.config.JsonTypeHandler.class),
        @Result(property = "dnsRecordCount", column = "dns_record_count"),
        @Result(property = "dnsRecordLimit", column = "dns_record_limit"),
        @Result(property = "userCount", column = "user_count"),
        @Result(property = "userLimit", column = "user_limit"),
        @Result(property = "isFull", column = "is_full"),
        @Result(property = "autoAssignEnabled", column = "auto_assign_enabled"),
        @Result(property = "nextPrefixHex", column = "next_prefix_hex")
    })
    List<CloudflareZone> findByAccountId(String accountId);
    
    /**
     * 删除域名信息(软删除)
     * @param zoneId Cloudflare Zone ID
     * @return 影响行数
     */
    @Update("""
        UPDATE cloudflare_zone SET is_active = 0 WHERE zone_id = #{zoneId}
    """)
    int deleteByZoneId(String zoneId);
    
    /**
     * 批量插入或更新域名信息
     * @param zones Cloudflare域名信息列表
     * @return 影响行数
     */
    int batchInsertOrUpdate(List<CloudflareZone> zones);
    
    /**
     * 统计域名数量
     * @return 域名总数
     */
    @Select("""
        SELECT COUNT(*) FROM cloudflare_zone WHERE is_active = 1
    """)
    int countAll();
    
    /**
     * 根据状态统计域名数量
     * @param status 域名状态
     * @return 域名数量
     */
    @Select("""
        SELECT COUNT(*) FROM cloudflare_zone WHERE status = #{status} AND is_active = 1
    """)
    int countByStatus(String status);
}