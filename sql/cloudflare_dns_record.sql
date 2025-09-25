-- Cloudflare DNS记录表
-- 用于存储从Cloudflare API获取的DNS记录信息

CREATE TABLE `cloudflare_dns_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  
  -- 关联信息
  `zone_id` varchar(100) NOT NULL COMMENT 'Cloudflare Zone ID，关联cloudflare_zone表',
  `record_id` varchar(100) NOT NULL COMMENT 'Cloudflare DNS记录ID',
  
  -- DNS记录基本信息
  `name` varchar(255) NOT NULL COMMENT 'DNS记录名称（完整域名）',
  `type` varchar(20) NOT NULL COMMENT 'DNS记录类型（A、AAAA、CNAME、MX、TXT、NS、SRV等）',
  `content` text NOT NULL COMMENT 'DNS记录内容（IP地址、域名、文本等）',
  
  -- 代理和TTL设置
  `proxiable` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否可代理(0-否，1-是)',
  `proxied` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已代理(0-否，1-是)',
  `ttl` int NOT NULL DEFAULT 1 COMMENT 'TTL值（1表示自动）',
  
  -- 高级设置
  `priority` int DEFAULT NULL COMMENT 'MX记录优先级或SRV记录优先级',
  `weight` int DEFAULT NULL COMMENT 'SRV记录权重',
  `port` int DEFAULT NULL COMMENT 'SRV记录端口',
  `service` varchar(100) DEFAULT NULL COMMENT 'SRV记录服务',
  `proto` varchar(20) DEFAULT NULL COMMENT 'SRV记录协议',
  `target` varchar(255) DEFAULT NULL COMMENT 'SRV记录目标',
  
  -- 元数据和设置
  `settings` json DEFAULT NULL COMMENT 'DNS记录设置(JSON格式)',
  `meta` json DEFAULT NULL COMMENT 'DNS记录元数据(JSON格式)',
  `tags` json DEFAULT NULL COMMENT 'DNS记录标签列表(JSON格式)',
  
  -- 备注信息
  `comment` text DEFAULT NULL COMMENT 'DNS记录备注',
  `comment_modified_on` datetime DEFAULT NULL COMMENT '备注修改时间',
  
  -- Cloudflare时间戳
  `created_on` datetime NOT NULL COMMENT 'Cloudflare创建时间',
  `modified_on` datetime NOT NULL COMMENT 'Cloudflare修改时间',
  
  -- 同步状态管理
  `sync_status` varchar(20) NOT NULL DEFAULT 'SUCCESS' COMMENT '同步状态(SUCCESS-同步成功，FAILED-同步失败)',
  `sync_error` text DEFAULT NULL COMMENT '同步错误信息',
  `last_sync_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后同步时间',
  
  -- 本地管理字段
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '本地启用状态(0-禁用，1-启用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '本地备注信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '本地创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '本地更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  UNIQUE KEY `uk_zone_name_type_content` (`zone_id`, `name`, `type`, `content`(100)),
  KEY `idx_zone_id` (`zone_id`),
  KEY `idx_name` (`name`),
  KEY `idx_type` (`type`),
  KEY `idx_proxied` (`proxied`),
  KEY `idx_sync_status` (`sync_status`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_created_on` (`created_on`),
  KEY `idx_last_sync_time` (`last_sync_time`),
  KEY `idx_composite_query` (`zone_id`, `type`, `is_active`),
  KEY `idx_time_range` (`created_on`, `modified_on`),
  
  -- 外键约束
  CONSTRAINT `fk_cloudflare_dns_record_zone_id` FOREIGN KEY (`zone_id`) REFERENCES `cloudflare_zone` (`zone_id`) ON DELETE CASCADE ON UPDATE CASCADE
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cloudflare DNS记录表';

-- 创建索引以优化查询性能
CREATE INDEX `idx_cloudflare_dns_record_name_type` ON `cloudflare_dns_record` (`name`, `type`);
CREATE INDEX `idx_cloudflare_dns_record_content` ON `cloudflare_dns_record` (`content`(100));
CREATE INDEX `idx_cloudflare_dns_record_ttl` ON `cloudflare_dns_record` (`ttl`);