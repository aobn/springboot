-- =====================================================
-- 简化的前缀池管理SQL脚本
-- 功能：修正前缀数量计算，实际为105个前缀
-- 作者：系统自动生成
-- 日期：2025-09-26
-- =====================================================

-- 修正存储过程：生成正确的105个前缀（0.1到6.f）
DROP PROCEDURE IF EXISTS `init_cloudflare_prefix_pool`;

DELIMITER $$
CREATE PROCEDURE `init_cloudflare_prefix_pool`(IN p_zone_id VARCHAR(100))
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE j INT DEFAULT 1;
    DECLARE hex_prefix VARCHAR(10);
    DECLARE decimal_val INT;
    
    -- 清空该域名的现有前缀池
    DELETE FROM cloudflare_prefix_pool WHERE zone_id = p_zone_id;
    
    -- 生成0.1到6.f的所有前缀组合（共105个）
    WHILE i <= 6 DO
        SET j = 1;
        WHILE j <= 15 DO  -- 1-f (排除0)
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

-- 验证前缀数量的查询
SELECT 
    '前缀池数量验证' as info,
    COUNT(*) as total_prefixes,
    MIN(prefix_hex) as min_prefix,
    MAX(prefix_hex) as max_prefix
FROM cloudflare_prefix_pool 
WHERE zone_id = 'c4ccaa1a6f609fcaef41c9a881230a85';

-- 显示前缀分布
SELECT 
    SUBSTRING(prefix_hex, 1, 1) as first_digit,
    COUNT(*) as count_per_digit
FROM cloudflare_prefix_pool 
WHERE zone_id = 'c4ccaa1a6f609fcaef41c9a881230a85'
GROUP BY SUBSTRING(prefix_hex, 1, 1)
ORDER BY first_digit;

-- 重新初始化测试域名的前缀池
CALL init_cloudflare_prefix_pool('c4ccaa1a6f609fcaef41c9a881230a85');

-- 验证结果
SELECT 
    '初始化后验证' as info,
    COUNT(*) as total_prefixes,
    COUNT(CASE WHEN is_assigned = 0 THEN 1 END) as available_prefixes,
    COUNT(CASE WHEN is_assigned = 1 THEN 1 END) as assigned_prefixes
FROM cloudflare_prefix_pool 
WHERE zone_id = 'c4ccaa1a6f609fcaef41c9a881230a85';