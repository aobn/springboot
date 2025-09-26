-- =====================================================
-- Cloudflare域名管理增强方案
-- 功能：DNS记录数量限制 + 用户前缀自动分配
-- 作者：系统自动生成
-- 日期：2025-09-26
-- =====================================================

-- 1. 修改cloudflare_zone表，添加记录统计和用户限制字段
ALTER TABLE `cloudflare_zone` 
ADD COLUMN `dns_record_count` int NOT NULL DEFAULT 0 COMMENT 'DNS记录总数量',
ADD COLUMN `dns_record_limit` int NOT NULL DEFAULT 200 COMMENT 'DNS记录数量限制',
ADD COLUMN `user_count` int NOT NULL DEFAULT 0 COMMENT '已注册用户数量',
ADD COLUMN `user_limit` int NOT NULL DEFAULT 100 COMMENT '用户数量限制',
ADD COLUMN `is_full` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已满(0-未满，1-已满)',
ADD COLUMN `auto_assign_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用自动分配前缀(0-否，1-是)',
ADD COLUMN `next_prefix_hex` varchar(10) NOT NULL DEFAULT '0.1' COMMENT '下一个可分配的十六进制前缀';

-- 添加相关索引
ALTER TABLE `cloudflare_zone`
ADD KEY `idx_dns_record_count` (`dns_record_count`),
ADD KEY `idx_user_count` (`user_count`),
ADD KEY `idx_is_full` (`is_full`),
ADD KEY `idx_auto_assign` (`auto_assign_enabled`);

-- 2. 创建用户Cloudflare域名注册表
CREATE TABLE `user_cloudflare_domain` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `zone_id` varchar(100) NOT NULL COMMENT 'Cloudflare Zone ID',
  `zone_name` varchar(255) NOT NULL COMMENT '域名名称',
  `assigned_prefix` varchar(10) NOT NULL COMMENT '分配的十六进制前缀(如0.1, a.f等)',
  `full_subdomain` varchar(255) NOT NULL COMMENT '完整子域名(如0.1.example.com)',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE-活跃，SUSPENDED-暂停，DELETED-已删除)',
  `dns_record_count` int NOT NULL DEFAULT 0 COMMENT '该用户在此域名下的DNS记录数量',
  `dns_record_limit` int NOT NULL DEFAULT 2 COMMENT '该用户在此域名下的DNS记录限制',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `assigned_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_zone` (`user_id`, `zone_id`),
  UNIQUE KEY `uk_zone_prefix` (`zone_id`, `assigned_prefix`),
  UNIQUE KEY `uk_full_subdomain` (`full_subdomain`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_zone_id` (`zone_id`),
  KEY `idx_status` (`status`),
  KEY `idx_assigned_prefix` (`assigned_prefix`),
  KEY `idx_dns_record_count` (`dns_record_count`),
  
  CONSTRAINT `fk_user_cloudflare_domain_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_cloudflare_domain_zone_id` FOREIGN KEY (`zone_id`) REFERENCES `cloudflare_zone` (`zone_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户Cloudflare域名注册表';

-- 3. 创建十六进制前缀管理表
CREATE TABLE `cloudflare_prefix_pool` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `zone_id` varchar(100) NOT NULL COMMENT 'Cloudflare Zone ID',
  `prefix_hex` varchar(10) NOT NULL COMMENT '十六进制前缀(0.1到6.4)',
  `prefix_decimal` int NOT NULL COMMENT '十进制值(用于排序)',
  `is_assigned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已分配(0-未分配，1-已分配)',
  `assigned_user_id` bigint DEFAULT NULL COMMENT '分配给的用户ID',
  `assigned_time` timestamp NULL DEFAULT NULL COMMENT '分配时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_zone_prefix` (`zone_id`, `prefix_hex`),
  KEY `idx_zone_id` (`zone_id`),
  KEY `idx_is_assigned` (`is_assigned`),
  KEY `idx_assigned_user_id` (`assigned_user_id`),
  KEY `idx_prefix_decimal` (`prefix_decimal`),
  
  CONSTRAINT `fk_cloudflare_prefix_pool_zone_id` FOREIGN KEY (`zone_id`) REFERENCES `cloudflare_zone` (`zone_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cloudflare_prefix_pool_user_id` FOREIGN KEY (`assigned_user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cloudflare十六进制前缀池表';

-- 4. 修改cloudflare_dns_record表，添加用户关联和限制检查
ALTER TABLE `cloudflare_dns_record`
ADD COLUMN `user_id` bigint DEFAULT NULL COMMENT '关联的用户ID(系统记录为NULL)',
ADD COLUMN `user_domain_id` bigint DEFAULT NULL COMMENT '关联的用户域名ID',
ADD KEY `idx_user_id` (`user_id`),
ADD KEY `idx_user_domain_id` (`user_domain_id`);

-- 添加外键约束
ALTER TABLE `cloudflare_dns_record`
ADD CONSTRAINT `fk_cloudflare_dns_record_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
ADD CONSTRAINT `fk_cloudflare_dns_record_user_domain_id` FOREIGN KEY (`user_domain_id`) REFERENCES `user_cloudflare_domain` (`id`) ON DELETE CASCADE;

-- 5. 创建函数：十六进制前缀转换
DELIMITER $$

-- 将十六进制前缀转换为十进制（用于排序）
CREATE FUNCTION `hex_prefix_to_decimal`(hex_prefix VARCHAR(10)) 
RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE first_part VARCHAR(5);
    DECLARE second_part VARCHAR(5);
    DECLARE dot_pos INT;
    DECLARE result INT;
    
    SET dot_pos = LOCATE('.', hex_prefix);
    IF dot_pos = 0 THEN
        RETURN 0;
    END IF;
    
    SET first_part = SUBSTRING(hex_prefix, 1, dot_pos - 1);
    SET second_part = SUBSTRING(hex_prefix, dot_pos + 1);
    
    SET result = CONV(first_part, 16, 10) * 16 + CONV(second_part, 16, 10);
    
    RETURN result;
END$$

-- 将十进制转换为十六进制前缀
CREATE FUNCTION `decimal_to_hex_prefix`(decimal_val INT) 
RETURNS VARCHAR(10)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE first_part INT;
    DECLARE second_part INT;
    DECLARE result VARCHAR(10);
    
    SET first_part = decimal_val DIV 16;
    SET second_part = decimal_val MOD 16;
    
    SET result = CONCAT(CONV(first_part, 10, 16), '.', CONV(second_part, 10, 16));
    
    RETURN LOWER(result);
END$$

-- 获取下一个可用的十六进制前缀
CREATE FUNCTION `get_next_available_prefix`(p_zone_id VARCHAR(100)) 
RETURNS VARCHAR(10)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE next_prefix VARCHAR(10);
    DECLARE current_decimal INT;
    DECLARE max_decimal INT DEFAULT 100; -- 6*16+4 = 100 (对应6.4)
    
    -- 查找最小的未分配前缀
    SELECT prefix_hex INTO next_prefix
    FROM cloudflare_prefix_pool 
    WHERE zone_id = p_zone_id AND is_assigned = 0 
    ORDER BY prefix_decimal ASC 
    LIMIT 1;
    
    -- 如果没有找到，说明已满
    IF next_prefix IS NULL THEN
        RETURN NULL;
    END IF;
    
    RETURN next_prefix;
END$$

DELIMITER ;

-- 6. 创建存储过程：初始化域名前缀池
DELIMITER $$

CREATE PROCEDURE `init_cloudflare_prefix_pool`(IN p_zone_id VARCHAR(100))
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE j INT DEFAULT 1;
    DECLARE hex_prefix VARCHAR(10);
    DECLARE decimal_val INT;
    
    -- 清空该域名的现有前缀池
    DELETE FROM cloudflare_prefix_pool WHERE zone_id = p_zone_id;
    
    -- 生成0.1到6.4的所有前缀组合
    WHILE i <= 6 DO
        SET j = 1;
        WHILE j <= (CASE WHEN i = 6 THEN 4 ELSE 15 END) DO
            SET hex_prefix = CONCAT(CONV(i, 10, 16), '.', CONV(j, 10, 16));
            SET decimal_val = i * 16 + j;
            
            INSERT INTO cloudflare_prefix_pool (zone_id, prefix_hex, prefix_decimal)
            VALUES (p_zone_id, LOWER(hex_prefix), decimal_val);
            
            SET j = j + 1;
        END WHILE;
        SET i = i + 1;
    END WHILE;
    
END$$

DELIMITER ;

-- 7. 创建触发器：DNS记录数量控制
DELIMITER $$

-- DNS记录插入前检查
CREATE TRIGGER `tr_cloudflare_dns_record_before_insert`
BEFORE INSERT ON `cloudflare_dns_record`
FOR EACH ROW
BEGIN
    DECLARE zone_record_count INT DEFAULT 0;
    DECLARE zone_record_limit INT DEFAULT 200;
    DECLARE user_record_count INT DEFAULT 0;
    DECLARE user_record_limit INT DEFAULT 2;
    
    -- 检查域名总记录数量限制
    SELECT dns_record_count, dns_record_limit 
    INTO zone_record_count, zone_record_limit
    FROM cloudflare_zone 
    WHERE zone_id = NEW.zone_id;
    
    IF zone_record_count >= zone_record_limit THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '域名DNS记录数量已达上限，无法添加新记录';
    END IF;
    
    -- 如果是用户记录，检查用户记录数量限制
    IF NEW.user_id IS NOT NULL AND NEW.user_domain_id IS NOT NULL THEN
        SELECT dns_record_count, dns_record_limit 
        INTO user_record_count, user_record_limit
        FROM user_cloudflare_domain 
        WHERE id = NEW.user_domain_id;
        
        IF user_record_count >= user_record_limit THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '用户DNS记录数量已达上限，无法添加新记录';
        END IF;
    END IF;
END$$

-- DNS记录插入后更新计数
CREATE TRIGGER `tr_cloudflare_dns_record_after_insert`
AFTER INSERT ON `cloudflare_dns_record`
FOR EACH ROW
BEGIN
    -- 更新域名记录计数
    UPDATE cloudflare_zone 
    SET dns_record_count = dns_record_count + 1
    WHERE zone_id = NEW.zone_id;
    
    -- 更新用户域名记录计数
    IF NEW.user_domain_id IS NOT NULL THEN
        UPDATE user_cloudflare_domain 
        SET dns_record_count = dns_record_count + 1
        WHERE id = NEW.user_domain_id;
    END IF;
END$$

-- DNS记录删除后更新计数
CREATE TRIGGER `tr_cloudflare_dns_record_after_delete`
AFTER DELETE ON `cloudflare_dns_record`
FOR EACH ROW
BEGIN
    -- 更新域名记录计数
    UPDATE cloudflare_zone 
    SET dns_record_count = GREATEST(dns_record_count - 1, 0)
    WHERE zone_id = OLD.zone_id;
    
    -- 更新用户域名记录计数
    IF OLD.user_domain_id IS NOT NULL THEN
        UPDATE user_cloudflare_domain 
        SET dns_record_count = GREATEST(dns_record_count - 1, 0)
        WHERE id = OLD.user_domain_id;
    END IF;
END$$

DELIMITER ;

-- 8. 创建触发器：用户域名注册管理
DELIMITER $$

-- 用户域名注册后更新计数
CREATE TRIGGER `tr_user_cloudflare_domain_after_insert`
AFTER INSERT ON `user_cloudflare_domain`
FOR EACH ROW
BEGIN
    -- 更新域名用户计数
    UPDATE cloudflare_zone 
    SET user_count = user_count + 1,
        is_full = CASE WHEN user_count + 1 >= user_limit THEN 1 ELSE 0 END
    WHERE zone_id = NEW.zone_id;
    
    -- 标记前缀为已分配
    UPDATE cloudflare_prefix_pool 
    SET is_assigned = 1, 
        assigned_user_id = NEW.user_id,
        assigned_time = NOW()
    WHERE zone_id = NEW.zone_id AND prefix_hex = NEW.assigned_prefix;
END$$

-- 用户域名删除后更新计数
CREATE TRIGGER `tr_user_cloudflare_domain_after_delete`
AFTER DELETE ON `user_cloudflare_domain`
FOR EACH ROW
BEGIN
    -- 更新域名用户计数
    UPDATE cloudflare_zone 
    SET user_count = GREATEST(user_count - 1, 0),
        is_full = CASE WHEN user_count - 1 < user_limit THEN 0 ELSE 1 END
    WHERE zone_id = OLD.zone_id;
    
    -- 释放前缀
    UPDATE cloudflare_prefix_pool 
    SET is_assigned = 0, 
        assigned_user_id = NULL,
        assigned_time = NULL
    WHERE zone_id = OLD.zone_id AND prefix_hex = OLD.assigned_prefix;
END$$

DELIMITER ;

-- 9. 创建存储过程：用户注册Cloudflare域名
DELIMITER $$

CREATE PROCEDURE `register_user_cloudflare_domain`(
    IN p_user_id BIGINT,
    IN p_zone_id VARCHAR(100),
    OUT p_result_code INT,
    OUT p_result_message VARCHAR(500),
    OUT p_assigned_prefix VARCHAR(10),
    OUT p_full_subdomain VARCHAR(255)
)
proc_label: BEGIN
    DECLARE v_zone_name VARCHAR(255);
    DECLARE v_user_count INT;
    DECLARE v_user_limit INT;
    DECLARE v_is_full TINYINT;
    DECLARE v_auto_assign_enabled TINYINT;
    DECLARE v_next_prefix VARCHAR(10);
    DECLARE v_existing_count INT DEFAULT 0;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = 500;
        SET p_result_message = '系统错误，请稍后重试';
        SET p_assigned_prefix = NULL;
        SET p_full_subdomain = NULL;
    END;
    
    START TRANSACTION;
    
    -- 检查用户是否已注册该域名
    SELECT COUNT(*) INTO v_existing_count
    FROM user_cloudflare_domain 
    WHERE user_id = p_user_id AND zone_id = p_zone_id AND status = 'ACTIVE';
    
    IF v_existing_count > 0 THEN
        SET p_result_code = 409;
        SET p_result_message = '您已注册过该域名';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- 获取域名信息
    SELECT name, user_count, user_limit, is_full, auto_assign_enabled
    INTO v_zone_name, v_user_count, v_user_limit, v_is_full, v_auto_assign_enabled
    FROM cloudflare_zone 
    WHERE zone_id = p_zone_id AND is_active = 1;
    
    -- 检查域名是否存在
    IF v_zone_name IS NULL THEN
        SET p_result_code = 404;
        SET p_result_message = '域名不存在或已禁用';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- 检查是否启用自动分配
    IF v_auto_assign_enabled = 0 THEN
        SET p_result_code = 403;
        SET p_result_message = '该域名暂不支持用户注册';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- 检查域名是否已满
    IF v_is_full = 1 OR v_user_count >= v_user_limit THEN
        SET p_result_code = 423;
        SET p_result_message = '该域名用户数量已满，请选择其他域名';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- 获取下一个可用前缀
    SET v_next_prefix = get_next_available_prefix(p_zone_id);
    
    IF v_next_prefix IS NULL THEN
        SET p_result_code = 423;
        SET p_result_message = '该域名前缀已用完，请选择其他域名';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- 生成完整子域名
    SET p_full_subdomain = CONCAT(v_next_prefix, '.', v_zone_name);
    
    -- 插入用户域名注册记录
    INSERT INTO user_cloudflare_domain (
        user_id, zone_id, zone_name, assigned_prefix, full_subdomain, status
    ) VALUES (
        p_user_id, p_zone_id, v_zone_name, v_next_prefix, p_full_subdomain, 'ACTIVE'
    );
    
    SET p_result_code = 201;
    SET p_result_message = '域名注册成功';
    SET p_assigned_prefix = v_next_prefix;
    
    COMMIT;
    
END$$

DELIMITER ;

-- 10. 创建视图：域名统计信息
CREATE VIEW `v_cloudflare_zone_stats` AS
SELECT 
    cz.zone_id,
    cz.name AS zone_name,
    cz.status AS zone_status,
    cz.dns_record_count,
    cz.dns_record_limit,
    ROUND((cz.dns_record_count / cz.dns_record_limit) * 100, 2) AS dns_usage_percent,
    cz.user_count,
    cz.user_limit,
    ROUND((cz.user_count / cz.user_limit) * 100, 2) AS user_usage_percent,
    cz.is_full,
    cz.auto_assign_enabled,
    cz.next_prefix_hex,
    (SELECT COUNT(*) FROM cloudflare_prefix_pool WHERE zone_id = cz.zone_id AND is_assigned = 0) AS available_prefixes,
    cz.create_time,
    cz.last_sync_time
FROM cloudflare_zone cz
WHERE cz.is_active = 1;

-- 11. 创建视图：用户域名详情
CREATE VIEW `v_user_cloudflare_domain_details` AS
SELECT 
    ucd.id,
    ucd.user_id,
    u.username,
    u.email AS user_email,
    ucd.zone_id,
    ucd.zone_name,
    ucd.assigned_prefix,
    ucd.full_subdomain,
    ucd.status,
    ucd.dns_record_count,
    ucd.dns_record_limit,
    ROUND((ucd.dns_record_count / ucd.dns_record_limit) * 100, 2) AS dns_usage_percent,
    ucd.assigned_time,
    ucd.create_time,
    (SELECT COUNT(*) FROM cloudflare_dns_record WHERE user_domain_id = ucd.id AND is_active = 1) AS active_dns_records
FROM user_cloudflare_domain ucd
LEFT JOIN user u ON ucd.user_id = u.id
WHERE ucd.status = 'ACTIVE';

-- 12. 插入示例数据和初始化
-- 注意：实际使用时需要根据真实的zone_id进行初始化

-- 示例：为现有域名初始化前缀池（需要替换为真实的zone_id）
-- CALL init_cloudflare_prefix_pool('your_real_zone_id_here');

-- 更新现有域名的统计信息
UPDATE cloudflare_zone SET 
    dns_record_count = (
        SELECT COUNT(*) FROM cloudflare_dns_record 
        WHERE zone_id = cloudflare_zone.zone_id AND is_active = 1
    ),
    user_count = 0,
    is_full = 0
WHERE zone_id IS NOT NULL;

-- 创建管理员查询索引
CREATE INDEX `idx_cloudflare_zone_stats` ON `cloudflare_zone` (`dns_record_count`, `user_count`, `is_full`);
CREATE INDEX `idx_user_cloudflare_domain_stats` ON `user_cloudflare_domain` (`dns_record_count`, `status`);

-- 完成提示
SELECT 'Cloudflare域名管理增强方案部署完成！' AS message,
       '请使用 CALL init_cloudflare_prefix_pool(zone_id) 初始化域名前缀池' AS note;