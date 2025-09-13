-- 为user表添加封禁相关字段
-- 执行时间：2025-09-13
-- 功能：支持管理员封禁用户账户功能

ALTER TABLE `user` 
ADD COLUMN `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态：ACTIVE-正常，BANNED-永久封禁' AFTER `role`,
ADD COLUMN `ban_reason` varchar(500) DEFAULT NULL COMMENT '封禁原因' AFTER `status`,
ADD COLUMN `ban_time` timestamp NULL DEFAULT NULL COMMENT '封禁时间' AFTER `ban_reason`,
ADD COLUMN `ban_admin_id` bigint DEFAULT NULL COMMENT '执行封禁的管理员ID' AFTER `ban_time`,
ADD INDEX `idx_status` (`status`),
ADD INDEX `idx_ban_time` (`ban_time`);

-- 添加外键约束（可选，如果需要严格的数据完整性）
-- ALTER TABLE `user` ADD CONSTRAINT `fk_user_ban_admin` FOREIGN KEY (`ban_admin_id`) REFERENCES `admin` (`id`);