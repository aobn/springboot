-- 初始化测试数据
-- 解决API测试中"域名前缀已用完"的问题

USE webdom;

-- 1. 插入测试Cloudflare域名
INSERT IGNORE INTO `cloudflare_zone` (
    `zone_id`, `name`, `status`, `paused`, `type`, `development_mode`,
    `created_on`, `modified_on`, `account_id`, `account_name`,
    `plan_id`, `plan_name`, `plan_price`, `plan_currency`,
    `dns_record_count`, `dns_record_limit`, `user_count`, `user_limit`,
    `is_full`, `auto_assign_enabled`, `next_prefix_hex`,
    `is_active`, `remark`
) VALUES (
    'c4ccaa1a6f609fcaef41c9a881230a85',
    'test.example.com',
    'active',
    0,
    'full',
    0,
    '2025-09-26 00:00:00',
    '2025-09-26 00:00:00',
    'test_account_id',
    'Test Account',
    'free',
    'Free Plan',
    0.00,
    'USD',
    0,
    200,
    0,
    100,
    0,
    1,
    '0.1',
    1,
    '测试域名用于API接口测试'
);

-- 2. 清空可能存在的旧前缀池数据
DELETE FROM `cloudflare_prefix_pool` WHERE `zone_id` = 'c4ccaa1a6f609fcaef41c9a881230a85';

-- 3. 初始化前缀池（前10个前缀用于测试）
INSERT INTO `cloudflare_prefix_pool` (`zone_id`, `prefix_hex`, `prefix_decimal`, `is_assigned`) VALUES
('c4ccaa1a6f609fcaef41c9a881230a85', '0.1', 1, 0),
('c4ccaa1a6f609fcaef41c9a881230a85', '0.2', 2, 0),
('c4ccaa1a6f609fcaef41c9a881230a85', '0.3', 3, 0),
('c4ccaa1a6f609fcaef41c9a881230a85', '0.4', 4, 0),
('c4ccaa1a6f609fcaef41c9a881230a85', '0.5', 5, 0),
('c4ccaa1a6f609fcaef41c9a881230a85', '0.6', 6, 0),
('c4ccaa1a6f609fcaef41c9a881230a85', '0.7', 7, 0),
('c4ccaa1a6f609fcaef41c9a881230a85', '0.8', 8, 0),
('c4ccaa1a6f609fcaef41c9a881230a85', '0.9', 9, 0),
('c4ccaa1a6f609fcaef41c9a881230a85', '0.a', 10, 0);

-- 4. 验证数据
SELECT 'Cloudflare Zone Count:' as info, COUNT(*) as count FROM cloudflare_zone WHERE zone_id = 'c4ccaa1a6f609fcaef41c9a881230a85';
SELECT 'Prefix Pool Count:' as info, COUNT(*) as count FROM cloudflare_prefix_pool WHERE zone_id = 'c4ccaa1a6f609fcaef41c9a881230a85';
SELECT 'Available Prefixes:' as info, COUNT(*) as count FROM cloudflare_prefix_pool WHERE zone_id = 'c4ccaa1a6f609fcaef41c9a881230a85' AND is_assigned = 0;