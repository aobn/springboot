# Cloudflare域名管理增强方案

## 概述

本方案实现了两个核心功能：
1. **DNS记录数量限制**：每个Cloudflare域名最多200条DNS记录
2. **用户前缀自动分配**：系统自动为用户分配十六进制前缀（0.1到6.4），每域名最多100用户

## 数据库设计

### 1. 核心表结构

#### 1.1 cloudflare_zone表增强
```sql
-- 新增字段
ALTER TABLE `cloudflare_zone` 
ADD COLUMN `dns_record_count` int NOT NULL DEFAULT 0 COMMENT 'DNS记录总数量',
ADD COLUMN `dns_record_limit` int NOT NULL DEFAULT 200 COMMENT 'DNS记录数量限制',
ADD COLUMN `user_count` int NOT NULL DEFAULT 0 COMMENT '已注册用户数量',
ADD COLUMN `user_limit` int NOT NULL DEFAULT 100 COMMENT '用户数量限制',
ADD COLUMN `is_full` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已满',
ADD COLUMN `auto_assign_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用自动分配前缀',
ADD COLUMN `next_prefix_hex` varchar(10) NOT NULL DEFAULT '0.1' COMMENT '下一个可分配的十六进制前缀';
```

#### 1.2 用户Cloudflare域名注册表
```sql
CREATE TABLE `user_cloudflare_domain` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `zone_id` varchar(100) NOT NULL COMMENT 'Cloudflare Zone ID',
  `zone_name` varchar(255) NOT NULL COMMENT '域名名称',
  `assigned_prefix` varchar(10) NOT NULL COMMENT '分配的十六进制前缀',
  `full_subdomain` varchar(255) NOT NULL COMMENT '完整子域名',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `dns_record_count` int NOT NULL DEFAULT 0 COMMENT 'DNS记录数量',
  `dns_record_limit` int NOT NULL DEFAULT 2 COMMENT 'DNS记录限制',
  -- 其他字段...
);
```

#### 1.3 十六进制前缀池表
```sql
CREATE TABLE `cloudflare_prefix_pool` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `zone_id` varchar(100) NOT NULL COMMENT 'Cloudflare Zone ID',
  `prefix_hex` varchar(10) NOT NULL COMMENT '十六进制前缀(0.1到6.4)',
  `prefix_decimal` int NOT NULL COMMENT '十进制值(用于排序)',
  `is_assigned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已分配',
  `assigned_user_id` bigint DEFAULT NULL COMMENT '分配给的用户ID',
  -- 其他字段...
);
```

### 2. 前缀分配规则

#### 2.1 十六进制前缀范围
- **起始**: 0.1 (十进制: 1)
- **结束**: 6.4 (十进制: 100)
- **总数**: 100个前缀
- **格式**: `{第一位}.{第二位}` (如: 0.1, 0.2, ..., 6.4)

#### 2.2 前缀生成逻辑
```
第一位: 0-6 (7个值)
第二位: 1-15 (15个值，但6.x只到6.4)

具体范围:
0.1, 0.2, 0.3, ..., 0.f
1.1, 1.2, 1.3, ..., 1.f
...
6.1, 6.2, 6.3, 6.4
```

#### 2.3 完整子域名格式
```
用户分配的子域名格式: {prefix}.{domain}
示例: 
- 0.1.example.com
- a.f.example.com
- 6.4.example.com
```

### 3. 核心功能函数

#### 3.1 前缀转换函数
```sql
-- 十六进制前缀转十进制
CREATE FUNCTION `hex_prefix_to_decimal`(hex_prefix VARCHAR(10)) RETURNS INT

-- 十进制转十六进制前缀
CREATE FUNCTION `decimal_to_hex_prefix`(decimal_val INT) RETURNS VARCHAR(10)

-- 获取下一个可用前缀
CREATE FUNCTION `get_next_available_prefix`(p_zone_id VARCHAR(100)) RETURNS VARCHAR(10)
```

#### 3.2 域名注册存储过程
```sql
CREATE PROCEDURE `register_user_cloudflare_domain`(
    IN p_user_id BIGINT,
    IN p_zone_id VARCHAR(100),
    OUT p_result_code INT,
    OUT p_result_message VARCHAR(500),
    OUT p_assigned_prefix VARCHAR(10),
    OUT p_full_subdomain VARCHAR(255)
)
```

### 4. 自动化触发器

#### 4.1 DNS记录数量控制
- **插入前检查**: 验证域名和用户记录数量限制
- **插入后更新**: 自动更新记录计数
- **删除后更新**: 自动减少记录计数

#### 4.2 用户注册管理
- **注册后更新**: 更新用户计数，标记前缀已分配
- **删除后释放**: 释放前缀，减少用户计数

### 5. 统计视图

#### 5.1 域名统计视图
```sql
CREATE VIEW `v_cloudflare_zone_stats` AS
SELECT 
    zone_id,
    zone_name,
    dns_record_count,
    dns_record_limit,
    dns_usage_percent,
    user_count,
    user_limit,
    user_usage_percent,
    is_full,
    available_prefixes
FROM cloudflare_zone;
```

#### 5.2 用户域名详情视图
```sql
CREATE VIEW `v_user_cloudflare_domain_details` AS
SELECT 
    user_id,
    username,
    zone_name,
    assigned_prefix,
    full_subdomain,
    dns_record_count,
    dns_usage_percent
FROM user_cloudflare_domain ucd
LEFT JOIN user u ON ucd.user_id = u.id;
```

## 使用流程

### 1. 初始化域名前缀池
```sql
-- 为新域名初始化前缀池
CALL init_cloudflare_prefix_pool('your_zone_id');
```

### 2. 用户注册域名
```sql
-- 用户注册Cloudflare域名
CALL register_user_cloudflare_domain(
    1001,  -- 用户ID
    'zone_id_123',  -- Zone ID
    @result_code,
    @result_message,
    @assigned_prefix,
    @full_subdomain
);

SELECT @result_code, @result_message, @assigned_prefix, @full_subdomain;
```

### 3. 查询域名状态
```sql
-- 查看域名统计信息
SELECT * FROM v_cloudflare_zone_stats WHERE zone_id = 'your_zone_id';

-- 查看用户域名详情
SELECT * FROM v_user_cloudflare_domain_details WHERE user_id = 1001;
```

## 限制和约束

### 1. DNS记录限制
- **域名级别**: 每个域名最多200条DNS记录
- **用户级别**: 每个用户在单个域名下最多2条DNS记录
- **自动检查**: 通过触发器自动验证和阻止超限操作

### 2. 用户数量限制
- **最大用户数**: 每个域名最多100个用户
- **前缀唯一性**: 每个前缀只能分配给一个用户
- **自动标记**: 达到限制时自动标记域名为已满

### 3. 前缀分配规则
- **顺序分配**: 按十进制值从小到大分配
- **自动回收**: 用户删除域名时自动释放前缀
- **冲突避免**: 通过唯一约束防止前缀冲突

## 监控和管理

### 1. 容量监控
```sql
-- 监控域名容量使用情况
SELECT 
    zone_name,
    CONCAT(dns_record_count, '/', dns_record_limit) AS dns_usage,
    CONCAT(user_count, '/', user_limit) AS user_usage,
    CASE WHEN is_full = 1 THEN '已满' ELSE '可用' END AS status
FROM v_cloudflare_zone_stats
ORDER BY dns_usage_percent DESC;
```

### 2. 用户分布
```sql
-- 查看用户前缀分布
SELECT 
    zone_name,
    assigned_prefix,
    username,
    dns_record_count,
    assigned_time
FROM v_user_cloudflare_domain_details
ORDER BY zone_name, assigned_prefix;
```

### 3. 异常处理
- **容量告警**: 当使用率超过80%时发出告警
- **错误日志**: 记录所有分配失败的原因
- **自动恢复**: 支持手动释放和重新分配前缀

## 扩展性考虑

### 1. 前缀范围扩展
如需支持更多用户，可以扩展前缀范围：
- 当前: 0.1-6.4 (100个前缀)
- 扩展: 0.0-f.f (256个前缀)

### 2. 多级前缀
支持更复杂的前缀结构：
- 二级: x.y.domain.com
- 三级: x.y.z.domain.com

### 3. 动态限制
支持根据域名类型动态调整限制：
- 免费域名: 100用户，200记录
- 付费域名: 500用户，1000记录

## 部署说明

1. **执行SQL脚本**: 运行 `cloudflare_domain_management.sql`
2. **初始化前缀池**: 为每个域名调用初始化存储过程
3. **更新应用代码**: 集成新的API接口
4. **测试验证**: 验证所有功能正常工作
5. **监控部署**: 设置容量监控和告警

## 注意事项

1. **数据一致性**: 所有操作通过存储过程和触发器保证数据一致性
2. **性能优化**: 添加了必要的索引优化查询性能
3. **错误处理**: 完善的错误处理和回滚机制
4. **向后兼容**: 保持与现有系统的兼容性
5. **安全考虑**: 防止SQL注入和并发冲突