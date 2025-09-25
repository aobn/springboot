-- Cloudflare域名(Zone)信息表
CREATE TABLE `cloudflare_zone` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `zone_id` varchar(100) NOT NULL COMMENT 'Cloudflare Zone ID',
  `name` varchar(255) NOT NULL COMMENT '域名名称',
  `status` varchar(50) NOT NULL COMMENT '域名状态(active/pending/initializing/moved/deleted/deactivated)',
  `paused` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否暂停(0-否，1-是)',
  `type` varchar(20) NOT NULL COMMENT 'Zone类型(full/partial)',
  `development_mode` int NOT NULL DEFAULT 0 COMMENT '开发模式剩余时间(秒)',
  
  -- 名称服务器信息
  `name_servers` json DEFAULT NULL COMMENT 'Cloudflare名称服务器列表(JSON格式)',
  `original_name_servers` json DEFAULT NULL COMMENT '原始名称服务器列表(JSON格式)',
  `original_registrar` varchar(255) DEFAULT NULL COMMENT '原始注册商',
  `original_dnshost` varchar(255) DEFAULT NULL COMMENT '原始DNS主机',
  
  -- 时间信息
  `created_on` datetime NOT NULL COMMENT 'Cloudflare创建时间',
  `modified_on` datetime NOT NULL COMMENT 'Cloudflare修改时间',
  `activated_on` datetime DEFAULT NULL COMMENT 'Cloudflare激活时间',
  
  -- 虚荣名称服务器
  `vanity_name_servers` json DEFAULT NULL COMMENT '虚荣名称服务器列表(JSON格式)',
  `vanity_name_servers_ips` json DEFAULT NULL COMMENT '虚荣名称服务器IP列表(JSON格式)',
  
  -- Meta信息
  `meta_step` int DEFAULT NULL COMMENT '设置步骤',
  `meta_custom_certificate_quota` int DEFAULT NULL COMMENT '自定义证书配额',
  `meta_page_rule_quota` int DEFAULT NULL COMMENT '页面规则配额',
  `meta_phishing_detected` tinyint(1) DEFAULT 0 COMMENT '是否检测到钓鱼(0-否，1-是)',
  
  -- 所有者信息
  `owner_id` varchar(100) DEFAULT NULL COMMENT '所有者ID',
  `owner_type` varchar(50) DEFAULT NULL COMMENT '所有者类型',
  `owner_email` varchar(255) DEFAULT NULL COMMENT '所有者邮箱',
  
  -- 账户信息
  `account_id` varchar(100) NOT NULL COMMENT 'Cloudflare账户ID',
  `account_name` varchar(255) NOT NULL COMMENT 'Cloudflare账户名称',
  
  -- 租户信息
  `tenant_id` varchar(100) DEFAULT NULL COMMENT '租户ID',
  `tenant_name` varchar(255) DEFAULT NULL COMMENT '租户名称',
  `tenant_unit_id` varchar(100) DEFAULT NULL COMMENT '租户单元ID',
  
  -- 计划信息
  `plan_id` varchar(100) NOT NULL COMMENT '计划ID',
  `plan_name` varchar(255) NOT NULL COMMENT '计划名称',
  `plan_price` decimal(10,2) DEFAULT 0.00 COMMENT '计划价格',
  `plan_currency` varchar(10) DEFAULT 'USD' COMMENT '计划货币',
  `plan_frequency` varchar(50) DEFAULT '' COMMENT '计费频率',
  `plan_is_subscribed` tinyint(1) DEFAULT 0 COMMENT '是否已订阅(0-否，1-是)',
  `plan_can_subscribe` tinyint(1) DEFAULT 0 COMMENT '是否可订阅(0-否，1-是)',
  `plan_legacy_id` varchar(50) DEFAULT NULL COMMENT '旧版计划ID',
  `plan_legacy_discount` tinyint(1) DEFAULT 0 COMMENT '旧版折扣(0-否，1-是)',
  `plan_externally_managed` tinyint(1) DEFAULT 0 COMMENT '外部管理(0-否，1-是)',
  
  -- 权限信息
  `permissions` json DEFAULT NULL COMMENT '权限列表(JSON格式)',
  
  -- 同步状态
  `sync_status` varchar(20) NOT NULL DEFAULT 'SUCCESS' COMMENT '同步状态(SUCCESS-同步成功，FAILED-同步失败)',
  `sync_error` text DEFAULT NULL COMMENT '同步错误信息',
  `last_sync_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后同步时间',
  
  -- 本地管理字段
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '本地启用状态(0-禁用，1-启用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '本地创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '本地更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_zone_id` (`zone_id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_plan_id` (`plan_id`),
  KEY `idx_sync_status` (`sync_status`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_created_on` (`created_on`),
  KEY `idx_last_sync_time` (`last_sync_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cloudflare域名(Zone)信息表';

-- 创建索引以提高查询性能
CREATE INDEX `idx_cloudflare_zone_composite` ON `cloudflare_zone` (`status`, `is_active`, `account_id`);
CREATE INDEX `idx_cloudflare_zone_time_range` ON `cloudflare_zone` (`created_on`, `modified_on`);