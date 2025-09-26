# 用户Cloudflare域名管理接口文档

## 概述

本文档描述了用户Cloudflare域名注册和管理的完整API接口，包括域名注册、查询、注销等功能。系统采用自动前缀分配机制，为每个用户分配唯一的十六进制前缀。

## 接口列表

### 1. 用户注册Cloudflare域名

**接口地址**: `POST /api/user/cloudflare/zones/register`

**功能描述**: 用户注册一个可用的Cloudflare域名，系统自动分配十六进制前缀

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**请求参数**:



```json
{
  "zoneId": "string",     // Cloudflare Zone ID (必填)
  "remark": "string"      // 备注信息 (可选)
}
```

**响应示例**:

成功响应 (201):
```json
{
  "code": 200,
  "message": "域名注册成功",
  "data": {
    "id": 1,
    "userId": 1001,
    "zoneId": "abc123def456",
    "zoneName": "example.com",
    "assignedPrefix": "0.1",
    "fullSubdomain": "0.1.example.com",
    "status": "ACTIVE",
    "dnsRecordCount": 0,
    "dnsRecordLimit": 2,
    "remark": "测试域名",
    "assignedTime": "2025-09-26 10:30:00",
    "resultCode": 201,
    "resultMessage": "域名注册成功"
  }
}
```

失败响应:
```json
{
  "code": 409,
  "message": "您已注册过该域名",
  "data": {
    "resultCode": 409,
    "resultMessage": "您已注册过该域名"
  }
}
```

**错误码说明**:
- `201`: 注册成功
- `404`: 域名不存在或已禁用
- `409`: 用户已注册该域名
- `403`: 域名暂不支持用户注册
- `423`: 域名用户数量已满或前缀已用完
- `500`: 系统错误

### 2. 获取用户域名列表

**接口地址**: `GET /api/user/cloudflare/zones/list`

**功能描述**: 获取用户已注册的域名列表和可用域名列表

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "获取域名列表成功",
  "data": {
    "userZones": [
      {
        "id": 1,
        "zoneId": "abc123def456",
        "zoneName": "example.com",
        "assignedPrefix": "0.1",
        "fullSubdomain": "0.1.example.com",
        "status": "ACTIVE",
        "dnsRecordCount": 1,
        "dnsRecordLimit": 2,
        "dnsUsagePercent": 50.0,
        "assignedTime": "2025-09-26 10:30:00"
      }
    ],
    "availableZones": [
      {
        "zoneId": "def456ghi789",
        "zoneName": "test.com",
        "status": "active",
        "userCount": 25,
        "userLimit": 100,
        "userUsagePercent": 25.0,
        "dnsRecordCount": 50,
        "dnsRecordLimit": 200,
        "dnsUsagePercent": 25.0,
        "isFull": false,
        "autoAssignEnabled": true,
        "availablePrefixes": 75
      }
    ]
  }
}
```

### 3. 获取可用域名列表

**接口地址**: `GET /api/user/cloudflare/zones/available`

**功能描述**: 获取所有可用的Cloudflare域名列表

**响应示例**:
```json
{
  "code": 200,
  "message": "获取可用域名列表成功",
  "data": [
    {
      "zoneId": "def456ghi789",
      "zoneName": "test.com",
      "status": "active",
      "userCount": 25,
      "userLimit": 100,
      "userUsagePercent": 25.0,
      "dnsRecordCount": 50,
      "dnsRecordLimit": 200,
      "dnsUsagePercent": 25.0,
      "isFull": false,
      "autoAssignEnabled": true,
      "availablePrefixes": 75
    }
  ]
}
```

### 4. 获取域名详情

**接口地址**: `GET /api/user/cloudflare/zones/{id}`

**功能描述**: 根据ID获取用户域名注册记录详情

**路径参数**:
- `id`: 域名记录ID

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "获取域名详情成功",
  "data": {
    "id": 1,
    "userId": 1001,
    "zoneId": "abc123def456",
    "zoneName": "example.com",
    "assignedPrefix": "0.1",
    "fullSubdomain": "0.1.example.com",
    "status": "ACTIVE",
    "dnsRecordCount": 1,
    "dnsRecordLimit": 2,
    "remark": "测试域名",
    "assignedTime": "2025-09-26 10:30:00",
    "createTime": "2025-09-26 10:30:00",
    "updateTime": "2025-09-26 10:30:00"
  }
}
```

### 5. 注销域名

**接口地址**: `DELETE /api/user/cloudflare/zones/{id}`

**功能描述**: 注销用户已注册的域名(需要先删除所有DNS记录)

**路径参数**:
- `id`: 域名记录ID

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "域名注销成功",
  "data": null
}
```

### 6. 同步DNS记录数量

**接口地址**: `POST /api/user/cloudflare/zones/{zoneId}/sync-dns-count`

**功能描述**: 同步用户在指定域名下的DNS记录数量

**路径参数**:
- `zoneId`: Cloudflare Zone ID

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "DNS记录数量同步成功",
  "data": null
}
```

### 7. 检查域名注册状态

**接口地址**: `GET /api/user/cloudflare/zones/check/{zoneId}`

**功能描述**: 检查用户是否已注册指定域名

**路径参数**:
- `zoneId`: Cloudflare Zone ID

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "用户已注册该域名",
  "data": true
}
```

## 域名注册逻辑说明

### 1. 前缀分配机制

#### 1.1 前缀范围
- **起始前缀**: 0.1 (十进制: 1)
- **结束前缀**: 6.4 (十进制: 100)
- **总前缀数**: 100个
- **前缀格式**: `{第一位}.{第二位}` (十六进制)

#### 1.2 前缀生成规则
```
第一位: 0-6 (7个值)
第二位: 1-15 (15个值，但6.x只到6.4)

具体前缀序列:
0.1, 0.2, 0.3, ..., 0.f
1.1, 1.2, 1.3, ..., 1.f
2.1, 2.2, 2.3, ..., 2.f
3.1, 3.2, 3.3, ..., 3.f
4.1, 4.2, 4.3, ..., 4.f
5.1, 5.2, 5.3, ..., 5.f
6.1, 6.2, 6.3, 6.4
```

#### 1.3 子域名格式
用户最终获得的完整子域名格式为: `{prefix}.{domain}`

**示例**:
- `0.1.example.com`
- `a.f.example.com`
- `6.4.example.com`

### 2. 注册流程

#### 2.1 注册前检查
1. **用户认证**: 验证JWT令牌有效性
2. **重复检查**: 检查用户是否已注册该域名
3. **域名验证**: 验证域名是否存在且启用
4. **容量检查**: 检查域名用户数量是否已满
5. **前缀可用性**: 检查是否有可用前缀

#### 2.2 自动分配逻辑
1. **前缀池查询**: 从前缀池中查找最小未分配的前缀
2. **原子性分配**: 通过数据库存储过程确保分配的原子性
3. **冲突避免**: 通过唯一约束防止前缀冲突
4. **状态更新**: 自动更新域名和前缀池状态

#### 2.3 注册成功后
1. **记录创建**: 在`user_cloudflare_domain`表中创建注册记录
2. **前缀标记**: 在`cloudflare_prefix_pool`表中标记前缀为已分配
3. **计数更新**: 更新域名的用户数量统计
4. **状态检查**: 检查域名是否达到用户上限

### 3. 限制和约束

#### 3.1 用户级别限制
- **域名数量**: 每个用户可注册多个不同的域名
- **DNS记录**: 每个用户在单个域名下最多2条DNS记录
- **前缀唯一**: 每个用户在每个域名下只能有一个前缀

#### 3.2 域名级别限制
- **用户数量**: 每个域名最多100个用户
- **DNS记录**: 每个域名最多200条DNS记录
- **前缀数量**: 每个域名最多100个前缀

#### 3.3 系统级别限制
- **并发安全**: 通过数据库锁和事务保证并发安全
- **数据一致性**: 通过触发器自动维护统计数据
- **错误恢复**: 支持事务回滚和错误恢复

### 4. 状态管理

#### 4.1 域名状态
- `ACTIVE`: 活跃状态，可正常使用
- `SUSPENDED`: 暂停状态，暂时不可用
- `DELETED`: 已删除状态，软删除

#### 4.2 前缀状态
- `is_assigned = 0`: 未分配，可用于新用户
- `is_assigned = 1`: 已分配，绑定到特定用户

#### 4.3 域名可用性
- `auto_assign_enabled = 1`: 启用自动分配
- `is_full = 0`: 未满，可接受新用户注册
- `is_active = 1`: 域名启用状态

### 5. 错误处理

#### 5.1 常见错误场景
1. **域名不存在**: 返回404错误
2. **重复注册**: 返回409错误
3. **容量已满**: 返回423错误
4. **权限不足**: 返回403错误
5. **系统异常**: 返回500错误

#### 5.2 错误恢复机制
1. **事务回滚**: 注册失败时自动回滚所有操作
2. **状态同步**: 定期同步DNS记录数量
3. **数据修复**: 支持手动修复数据不一致问题

### 6. 性能优化

#### 6.1 数据库优化
1. **索引优化**: 为常用查询字段添加索引
2. **分页查询**: 大数据量时使用分页
3. **连接池**: 使用数据库连接池提高性能

#### 6.2 缓存策略
1. **域名列表**: 可缓存可用域名列表
2. **用户状态**: 可缓存用户注册状态
3. **统计数据**: 可缓存域名统计信息

### 7. 监控和告警

#### 7.1 容量监控
- 监控域名用户使用率
- 监控DNS记录使用率
- 监控前缀池使用情况

#### 7.2 异常告警
- 注册失败率过高告警
- 域名容量接近上限告警
- 系统异常告警

## 使用示例

### 1. 完整注册流程

```javascript
// 1. 获取可用域名列表
const availableZones = await fetch('/api/user/cloudflare/zones/available', {
  headers: { 'Authorization': 'Bearer ' + token }
});

// 2. 选择域名并注册
const registerResponse = await fetch('/api/user/cloudflare/zones/register', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    zoneId: 'selected_zone_id',
    remark: '我的测试域名'
  })
});

// 3. 获取注册结果
const result = await registerResponse.json();
if (result.code === 200 && result.data.resultCode === 201) {
  console.log('注册成功，分配的子域名:', result.data.fullSubdomain);
}
```

### 2. 查询用户域名

```javascript
// 获取用户所有域名
const userZones = await fetch('/api/user/cloudflare/zones/list', {
  headers: { 'Authorization': 'Bearer ' + token }
});

const zones = await userZones.json();
console.log('用户域名:', zones.data.userZones);
console.log('可用域名:', zones.data.availableZones);
```

### 3. 域名管理操作

```javascript
// 同步DNS记录数量
await fetch(`/api/user/cloudflare/zones/${zoneId}/sync-dns-count`, {
  method: 'POST',
  headers: { 'Authorization': 'Bearer ' + token }
});

// 注销域名
await fetch(`/api/user/cloudflare/zones/${recordId}`, {
  method: 'DELETE',
  headers: { 'Authorization': 'Bearer ' + token }
});
```

## 注意事项

1. **前置条件**: 用户必须先登录获取有效的JWT令牌
2. **域名初始化**: 管理员需要先初始化域名的前缀池
3. **DNS记录限制**: 用户在注销域名前必须删除所有DNS记录
4. **并发安全**: 系统支持多用户并发注册，但同一前缀只能分配给一个用户
5. **数据一致性**: 系统通过触发器自动维护统计数据，无需手动更新

## 相关文档

- [Cloudflare域名管理增强方案](../rule/Cloudflare域名管理增强方案.md)
- [数据库表结构文档](../rule/数据库表.md)
- [API开发规范](../rule/api开发文档.md)