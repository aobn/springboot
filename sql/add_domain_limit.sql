-- 用户域名数量限制功能实现
-- 作者：CodeBuddy
-- 创建时间：2025-09-10
-- 功能：为用户表添加域名数量限制字段，并创建触发器自动管理

-- 1. 为用户表添加域名数量限制字段
ALTER TABLE `user` 
ADD COLUMN `dom_num` int NOT NULL DEFAULT 2 COMMENT '可注册域名数量，默认为2个';

-- 2. 创建触发器：用户成功注册域名时自动减少dom_num
DELIMITER $$

CREATE TRIGGER `tr_user_subdomain_insert`
AFTER INSERT ON `user_subdomain`
FOR EACH ROW
BEGIN
    -- 当新增用户子域名且状态为ACTIVE时，减少用户的可注册域名数量
    IF NEW.status = 'ACTIVE' THEN
        UPDATE `user` 
        SET `dom_num` = `dom_num` - 1 
        WHERE `id` = NEW.user_id AND `dom_num` > 0;
    END IF;
END$$

-- 3. 创建触发器：用户删除域名时自动增加dom_num
CREATE TRIGGER `tr_user_subdomain_delete`
AFTER DELETE ON `user_subdomain`
FOR EACH ROW
BEGIN
    -- 当删除用户子域名且状态为ACTIVE时，增加用户的可注册域名数量
    IF OLD.status = 'ACTIVE' THEN
        UPDATE `user` 
        SET `dom_num` = `dom_num` + 1 
        WHERE `id` = OLD.user_id;
    END IF;
END$$

-- 4. 创建触发器：用户域名状态变更时自动调整dom_num
CREATE TRIGGER `tr_user_subdomain_update`
AFTER UPDATE ON `user_subdomain`
FOR EACH ROW
BEGIN
    -- 如果域名状态从非ACTIVE变为ACTIVE，减少可注册数量
    IF OLD.status != 'ACTIVE' AND NEW.status = 'ACTIVE' THEN
        UPDATE `user` 
        SET `dom_num` = `dom_num` - 1 
        WHERE `id` = NEW.user_id AND `dom_num` > 0;
    END IF;
    
    -- 如果域名状态从ACTIVE变为非ACTIVE，增加可注册数量
    IF OLD.status = 'ACTIVE' AND NEW.status != 'ACTIVE' THEN
        UPDATE `user` 
        SET `dom_num` = `dom_num` + 1 
        WHERE `id` = NEW.user_id;
    END IF;
END$$

DELIMITER ;

-- 5. 创建检查函数：验证用户是否还能注册域名
DELIMITER $$

CREATE FUNCTION `fn_check_user_domain_limit`(p_user_id BIGINT) 
RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_dom_num INT DEFAULT 0;
    
    -- 获取用户当前可注册域名数量
    SELECT `dom_num` INTO v_dom_num 
    FROM `user` 
    WHERE `id` = p_user_id;
    
    -- 返回可注册数量（0表示不能注册，大于0表示还能注册的数量）
    RETURN IFNULL(v_dom_num, 0);
END$$

DELIMITER ;

-- 6. 为现有用户初始化dom_num字段（根据已注册的活跃域名数量计算）
UPDATE `user` u 
SET `dom_num` = 2 - (
    SELECT COUNT(*) 
    FROM `user_subdomain` us 
    WHERE us.user_id = u.id AND us.status = 'ACTIVE'
)
WHERE u.`dom_num` = 2;

-- 7. 创建视图：用户域名使用情况统计
CREATE OR REPLACE VIEW `v_user_domain_stats` AS
SELECT 
    u.id AS user_id,
    u.username,
    u.email,
    u.dom_num AS available_domains,
    IFNULL(active_count.cnt, 0) AS used_domains,
    (2 - IFNULL(active_count.cnt, 0)) AS calculated_available,
    CASE 
        WHEN u.dom_num > 0 THEN 'CAN_REGISTER'
        ELSE 'LIMIT_REACHED'
    END AS registration_status
FROM `user` u
LEFT JOIN (
    SELECT user_id, COUNT(*) as cnt
    FROM `user_subdomain` 
    WHERE status = 'ACTIVE'
    GROUP BY user_id
) active_count ON u.id = active_count.user_id;

-- 8. 添加索引优化查询性能
CREATE INDEX `idx_user_dom_num` ON `user` (`dom_num`);

-- 验证脚本执行结果
SELECT 'Domain limit feature installed successfully!' as result;