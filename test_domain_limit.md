# 用户域名数量限制功能测试

## 测试概述
本文档用于测试用户域名数量限制功能的实现效果。

## 功能说明
- 每个用户默认可注册2个域名
- 数据库触发器自动管理域名数量
- 应用层验证用户注册权限
- 提供域名使用统计API

## 测试步骤

### 1. 执行数据库脚本
```bash
# 连接到MySQL数据库
mysql -h gz-cdb-nj3xoput.sql.tencentcdb.com -P 28942 -u root -p demo

# 执行域名限制功能脚本
source sql/add_domain_limit.sql;
```

### 2. 验证数据库变更
```sql
-- 检查用户表是否添加了dom_num字段
DESC user;

-- 检查触发器是否创建成功
SHOW TRIGGERS LIKE 'user_subdomain';

-- 检查函数是否创建成功
SHOW FUNCTION STATUS WHERE Name = 'fn_check_user_domain_limit';

-- 检查视图是否创建成功
SHOW TABLES LIKE 'v_user_domain_stats';

-- 查看现有用户的dom_num值
SELECT id, username, email, dom_num FROM user;
```

### 3. 测试API接口

#### 3.1 获取用户域名统计
```bash
curl -X GET "http://localhost:8080/api/user/subdomains/stats/mine" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### 3.2 检查是否可以注册域名
```bash
curl -X GET "http://localhost:8080/api/user/subdomains/can-register" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### 3.3 获取剩余可注册数量
```bash
curl -X GET "http://localhost:8080/api/user/subdomains/remaining-count" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### 3.4 注册域名（测试限制）
```bash
# 第一次注册（应该成功）
curl -X POST "http://localhost:8080/api/user/subdomains/register" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomain": "test1",
    "domain": "cblog.eu"
  }'

# 第二次注册（应该成功）
curl -X POST "http://localhost:8080/api/user/subdomains/register" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomain": "test2",
    "domain": "cblog.eu"
  }'

# 第三次注册（应该失败，提示达到上限）
curl -X POST "http://localhost:8080/api/user/subdomains/register" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomain": "test3",
    "domain": "cblog.eu"
  }'
```

### 4. 验证触发器功能

#### 4.1 测试域名删除后数量恢复
```sql
-- 查看用户当前域名数量
SELECT u.id, u.username, u.dom_num, 
       COUNT(us.id) as active_domains
FROM user u 
LEFT JOIN user_subdomain us ON u.id = us.user_id AND us.status = 'ACTIVE'
WHERE u.id = 1
GROUP BY u.id;

-- 删除一个域名
DELETE FROM user_subdomain WHERE user_id = 1 AND status = 'ACTIVE' LIMIT 1;

-- 再次查看，dom_num应该增加1
SELECT u.id, u.username, u.dom_num, 
       COUNT(us.id) as active_domains
FROM user u 
LEFT JOIN user_subdomain us ON u.id = us.user_id AND us.status = 'ACTIVE'
WHERE u.id = 1
GROUP BY u.id;
```

#### 4.2 测试域名状态变更
```sql
-- 将域名状态从ACTIVE改为INACTIVE
UPDATE user_subdomain SET status = 'INACTIVE' 
WHERE user_id = 1 AND status = 'ACTIVE' LIMIT 1;

-- 查看dom_num是否增加
SELECT dom_num FROM user WHERE id = 1;

-- 将域名状态从INACTIVE改回ACTIVE
UPDATE user_subdomain SET status = 'ACTIVE' 
WHERE user_id = 1 AND status = 'INACTIVE' LIMIT 1;

-- 查看dom_num是否减少
SELECT dom_num FROM user WHERE id = 1;
```

### 5. 测试管理员功能

#### 5.1 管理员可以调整用户域名配额
```sql
-- 管理员将某用户的域名配额提升到5个
UPDATE user SET dom_num = dom_num + 3 WHERE id = 1;

-- 验证用户现在可以注册更多域名
SELECT * FROM v_user_domain_stats WHERE user_id = 1;
```

## 预期结果

### 1. 数据库层面
- ✅ user表成功添加dom_num字段，默认值为2
- ✅ 创建3个触发器：插入、删除、更新
- ✅ 创建检查函数fn_check_user_domain_limit
- ✅ 创建统计视图v_user_domain_stats
- ✅ 现有用户的dom_num根据已注册域名数量正确初始化

### 2. 应用层面
- ✅ 用户注册域名前会检查剩余配额
- ✅ 达到上限时返回明确的错误信息
- ✅ 提供域名使用统计API
- ✅ 提供剩余配额查询API
- ✅ 支持管理员调整用户配额

### 3. 业务逻辑
- ✅ 用户最多注册2个域名（默认配置）
- ✅ 删除域名后配额自动恢复
- ✅ 域名状态变更时配额自动调整
- ✅ 管理员可以灵活调整用户配额
- ✅ 所有操作都有完整的日志记录

## 错误处理

### 常见错误信息
1. `您已达到域名注册数量上限，当前可注册数量：0`
2. `用户不存在`
3. `域名 xxx.cblog.eu 已存在`
4. `JWT解析失败`

### 故障排除
1. 检查数据库连接
2. 验证JWT令牌有效性
3. 确认触发器正常工作
4. 检查用户权限设置

## 性能考虑
- 添加了索引优化查询性能
- 触发器逻辑简单，不会显著影响性能
- 统计视图提供快速的数据汇总
- 缓存机制可进一步优化响应速度