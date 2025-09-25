-- 创建管理员用户
-- 密码: admin (BCrypt加密后)
INSERT INTO user (email, username, password, role, created_at, updated_at, is_banned, ban_reason, ban_until) 
VALUES (
    'admin@test.com', 
    'admin', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
    'ADMIN', 
    NOW(), 
    NOW(), 
    0, 
    NULL, 
    NULL
);