package com.example.demo.mapper;

import com.example.demo.entity.CloudflareZone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 管理员Cloudflare域名管理Mapper
 */
@Mapper
public interface AdminCloudflareZoneMapper {

    /**
     * 根据Zone ID查找域名
     */
    CloudflareZone findByZoneId(@Param("zoneId") String zoneId);

    /**
     * 统计域名前缀池数量
     */
    int countPrefixPool(@Param("zoneId") String zoneId);

    /**
     * 统计已分配前缀数量
     */
    int countAssignedPrefixes(@Param("zoneId") String zoneId);

    /**
     * 调用存储过程初始化前缀池
     */
    void callInitPrefixPool(@Param("zoneId") String zoneId);

    /**
     * 清空前缀池
     */
    void clearPrefixPool(@Param("zoneId") String zoneId);

    /**
     * 获取前缀池统计信息
     */
    Map<String, Object> getPrefixPoolStats(@Param("zoneId") String zoneId);

    /**
     * 获取所有启用自动分配的域名
     */
    List<CloudflareZone> findAutoAssignEnabledZones();

    /**
     * 获取所有域名的前缀池统计
     */
    List<Map<String, Object>> getAllZonePrefixStats();

    /**
     * 获取域名的用户注册信息
     */
    List<Map<String, Object>> getUserRegistrations(@Param("zoneId") String zoneId);
}