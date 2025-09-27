# 用户更新Cloudflare DNS记录接口文档

## 接口基本信息

- **接口标识**：用户更新Cloudflare DNS记录
- **接口路径**：`PUT /api/user/cloudflare/dns-records/{recordId}`
- **请求方法**：`PUT`
- **权限要求**：用户权限 (`USER`)
- **认证方式**：JWT Token (Bearer Token)
- **功能描述**：用户更新自己注册的Cloudflare域名下的DNS记录

## 路径参数

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| recordId | String | 是 | Cloudflare DNS记录ID | "869bb3fe8be216947d506c633c24db49" |

## 请求头

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Authorization | String | 是 | Bearer {JWT_TOKEN} |
| Content-Type | String | 是 | application/json |

## 请求体参数

| 参数名 | 类型 | 必填 | 说明 | 示例 | 备注 |
|--------|------|------|------|------|------|
| content | String | 是 | DNS记录内容（IP地址、域名、文本等） | "192.168.1.100" | 根据记录类型而定 |
| ttl | Integer | 是 | TTL值（1表示自动，其他为具体秒数） | 300 | 范围：1-86400 |
| proxied | Boolean | 否 | 是否启用Cloudflare代理 | false | 仅A、AAAA、CNAME记录支持 |
| priority | Integer | 否 | 优先级 | 10 | MX和SRV记录必填，范围：0-65535 |
| weight | Integer | 否 | 权重 | 5 | SRV记录必填，范围：0-65535 |
| port | Integer | 否 | 端口 | 80 | SRV记录必填，范围：1-65535 |
| service | String | 否 | 服务名 | "_http" | SRV记录必填 |
| proto | String | 否 | 协议 | "_tcp" | SRV记录必填 |
| target | String | 否 | 目标 | "target.example.com" | SRV记录必填 |
| comment | String | 否 | 记录注释 | "更新的测试记录" | 可选备注信息 |

## 请求示例

### 1. 更新A记录
```bash
curl -X PUT "http://localhost:8001/api/user/cloudflare/dns-records/869bb3fe8be216947d506c633c24db49" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "192.168.1.200",
    "ttl": 600,
    "proxied": false,
    "comment": "更新后的A记录"
  }'
```

### 2. 更新CNAME记录并启用代理
```bash
curl -X PUT "http://localhost:8001/api/user/cloudflare/dns-records/869bb3fe8be216947d506c633c24db49" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "new-target.example.com",
    "ttl": 1,
    "proxied": true,
    "comment": "更新CNAME并启用代理"
  }'
```

### 3. 更新MX记录
```bash
curl -X PUT "http://localhost:8001/api/user/cloudflare/dns-records/869bb3fe8be216947d506c633c24db49" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "new-mail.example.com",
    "ttl": 3600,
    "priority": 20,
    "comment": "更新邮件服务器记录"
  }'
```

### 4. 更新SRV记录
```bash
curl -X PUT "http://localhost:8001/api/user/cloudflare/dns-records/869bb3fe8be216947d506c633c24db49" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "new-target.example.com",
    "ttl": 300,
    "priority": 15,
    "weight": 10,
    "port": 443,
    "service": "_https",
    "proto": "_tcp",
    "target": "new-target.example.com",
    "comment": "更新HTTPS服务记录"
  }'
```

### 5. 更新TXT记录
```bash
curl -X PUT "http://localhost:8001/api/user/cloudflare/dns-records/869bb3fe8be216947d506c633c24db49" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "v=spf1 include:_spf.mailgun.org ~all",
    "ttl": 3600,
    "comment": "更新SPF记录"
  }'
```

## 响应格式

### 成功响应 (200)
```json
{
  "code": 200,
  "message": "DNS记录更新成功",
  "data": {
    "id": 35,
    "recordId": "869bb3fe8be216947d506c633c24db49",
    "userDomainId": 5,
    "userSubdomain": "0.1.example.com",
    "name": "test.0.1.example.com",
    "type": "A",
    "content": "192.168.1.200",
    "ttl": 600,
    "proxied": false,
    "proxiable": true,
    "priority": null,
    "weight": null,
    "port": null,
    "comment": "更新后的A记录",
    "syncStatus": "SUCCESS",
    "syncError": null,
    "isActive": true,
    "createdOn": "2025-09-26T01:00:00.000000",
    "modifiedOn": "2025-09-27T02:00:00.000000",
    "createTime": "2025-09-26T01:00:00.000000",
    "updateTime": "2025-09-27T02:00:00.000000"
  },
  "timestamp": "2025-09-27T02:00:00.000000"
}
```

### 记录不存在响应 (404)
```json
{
  "code": 404,
  "message": "DNS记录不存在",
  "data": null,
  "timestamp": "2025-09-27T02:00:00.000000"
}
```

### 权限不足响应 (403)
```json
{
  "code": 403,
  "message": "无权限修改该DNS记录",
  "data": null,
  "timestamp": "2025-09-27T02:00:00.000000"
}
```

### 参数错误响应 (400)
```json
{
  "code": 400,
  "message": "参数验证失败：DNS记录内容格式不正确",
  "data": null,
  "timestamp": "2025-09-27T02:00:00.000000"
}
```

### 域名状态异常响应 (400)
```json
{
  "code": 400,
  "message": "域名状态异常，无法更新DNS记录",
  "data": null,
  "timestamp": "2025-09-27T02:00:00.000000"
}
```

### 服务器错误响应 (500)
```json
{
  "code": 500,
  "message": "更新DNS记录失败",
  "data": null,
  "timestamp": "2025-09-27T02:00:00.000000"
}
```

## 业务逻辑说明

### 1. 权限验证
- 验证JWT令牌有效性
- 确认DNS记录属于当前用户
- 检查用户域名状态是否为ACTIVE

### 2. 参数验证
- 验证DNS记录内容格式（根据记录类型）
- 验证TTL值范围（1-86400秒）
- 验证特殊记录类型的必需字段：
  - MX记录：必须提供priority
  - SRV记录：必须提供priority、weight、port、service、proto、target
- 验证代理设置（仅A、AAAA、CNAME记录支持）

### 3. 更新流程
1. 获取现有DNS记录信息
2. 构建Cloudflare API更新请求
3. 调用Cloudflare API更新记录
4. 同步更新本地数据库记录
5. 返回更新后的记录信息

### 4. 同步机制
- 成功更新后同步状态设为SUCCESS
- 更新失败时同步状态设为FAILED并记录错误信息
- 保持创建时间不变，更新修改时间

## 字段说明

### 响应字段详解
- **id**: 本地数据库记录ID
- **recordId**: Cloudflare DNS记录ID
- **userDomainId**: 用户域名ID（来自user_cloudflare_domain表）
- **userSubdomain**: 用户完整子域名
- **name**: DNS记录完整名称
- **type**: DNS记录类型（不可更新）
- **content**: DNS记录内容
- **ttl**: TTL值（1表示自动）
- **proxied**: 是否启用Cloudflare代理
- **proxiable**: 是否支持代理功能
- **priority**: 优先级（MX/SRV记录）
- **weight**: 权重（SRV记录）
- **port**: 端口（SRV记录）
- **comment**: 记录注释
- **syncStatus**: 同步状态（SUCCESS/FAILED）
- **syncError**: 同步错误信息
- **isActive**: 本地启用状态
- **createdOn**: Cloudflare创建时间
- **modifiedOn**: Cloudflare修改时间
- **createTime**: 本地创建时间
- **updateTime**: 本地更新时间

## 注意事项

### 1. 不可更新字段
- DNS记录名称（name）：由系统根据用户子域名自动生成
- DNS记录类型（type）：创建后不可修改
- 用户域名ID（userDomainId）：不可修改

### 2. 记录类型限制
- **A/AAAA记录**：content必须是有效的IP地址
- **CNAME记录**：content必须是有效的域名
- **MX记录**：必须提供priority，content为邮件服务器域名
- **TXT记录**：content为文本内容
- **SRV记录**：必须提供完整的SRV记录参数
- **NS记录**：content为名称服务器域名

### 3. 代理功能
- 仅A、AAAA、CNAME记录支持Cloudflare代理
- 启用代理后，TTL会被Cloudflare自动管理
- 代理状态影响实际解析结果

### 4. 同步状态
- SUCCESS：与Cloudflare同步成功
- FAILED：同步失败，查看syncError字段获取详细错误信息

## 使用场景

1. **IP地址变更**：更新A/AAAA记录的IP地址
2. **域名指向调整**：修改CNAME记录的目标域名
3. **邮件服务器变更**：更新MX记录的服务器和优先级
4. **服务配置调整**：修改SRV记录的服务参数
5. **代理状态切换**：启用或禁用Cloudflare代理功能
6. **TTL优化**：调整DNS记录的缓存时间
7. **备注更新**：修改记录的注释信息

## 错误处理

接口提供完整的错误处理机制：
- 参数验证错误（400）
- 权限验证错误（403）
- 记录不存在错误（404）
- Cloudflare API调用错误（500）
- 数据库操作错误（500）
- 网络连接错误（500）

所有错误都会记录详细日志，便于问题排查和系统监控。