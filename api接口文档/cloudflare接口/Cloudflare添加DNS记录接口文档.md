# Cloudflare添加DNS记录接口文档

## 接口概述
本接口用于向Cloudflare添加DNS记录，并同步保存到本地数据库。支持所有DNS记录类型，包括A、AAAA、CNAME、MX、TXT、SRV、NS等。

## 接口信息
- **接口路径**: `/api/cloudflare/dns/create/{zoneId}`
- **请求方法**: `POST`
- **权限要求**: 管理员权限 (`ADMIN`)
- **认证方式**: JWT Token (Bearer Token)

## 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| zoneId | String | 是 | Cloudflare Zone ID，可从cloudflare_zone表获取 |

## 请求头
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Authorization | String | 是 | Bearer {JWT_TOKEN} |
| Content-Type | String | 是 | application/json |

## 请求体参数
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| namePrefix | String | 是 | DNS记录名称前缀，会自动拼接域名 | "www", "mail", "@" |
| type | String | 是 | DNS记录类型 | "A", "CNAME", "MX", "TXT", "SRV", "NS" |
| content | String | 是 | DNS记录内容 | "192.168.1.1", "example.com" |
| ttl | Integer | 是 | TTL值，1表示自动，其他为具体秒数 | 1, 300, 3600 |
| proxied | Boolean | 否 | 是否启用Cloudflare代理(仅A/AAAA/CNAME) | true, false |
| priority | Integer | 否 | 优先级(MX/SRV记录必填) | 10, 20 |
| weight | Integer | 否 | 权重(SRV记录必填) | 5, 10 |
| port | Integer | 否 | 端口(SRV记录必填) | 80, 443 |
| service | String | 否 | 服务名(SRV记录必填) | "_http", "_https" |
| proto | String | 否 | 协议(SRV记录必填) | "_tcp", "_udp" |
| target | String | 否 | 目标(SRV记录必填) | "target.example.com" |
| comment | String | 否 | 记录注释 | "测试记录" |

## 请求示例

### 1. 创建A记录
```bash
curl -X POST "http://localhost:8001/api/cloudflare/dns/create/72a832c4cf5f8b5afc1aba4e8b37af02" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "namePrefix": "test",
    "type": "A",
    "content": "192.168.1.100",
    "ttl": 300,
    "proxied": false,
    "comment": "测试A记录"
  }'
```

### 2. 创建CNAME记录
```bash
curl -X POST "http://localhost:8001/api/cloudflare/dns/create/72a832c4cf5f8b5afc1aba4e8b37af02" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "namePrefix": "www",
    "type": "CNAME",
    "content": "example.com",
    "ttl": 1,
    "proxied": true,
    "comment": "网站CNAME记录"
  }'
```

### 3. 创建MX记录
```bash
curl -X POST "http://localhost:8001/api/cloudflare/dns/create/72a832c4cf5f8b5afc1aba4e8b37af02" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "namePrefix": "@",
    "type": "MX",
    "content": "mail.example.com",
    "ttl": 3600,
    "priority": 10,
    "comment": "邮件服务器记录"
  }'
```

### 4. 创建SRV记录
```bash
curl -X POST "http://localhost:8001/api/cloudflare/dns/create/72a832c4cf5f8b5afc1aba4e8b37af02" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "namePrefix": "_http._tcp",
    "type": "SRV",
    "content": "target.example.com",
    "ttl": 300,
    "priority": 10,
    "weight": 5,
    "port": 80,
    "service": "_http",
    "proto": "_tcp",
    "target": "target.example.com",
    "comment": "HTTP服务记录"
  }'
```

### 5. 创建TXT记录
```bash
curl -X POST "http://localhost:8001/api/cloudflare/dns/create/72a832c4cf5f8b5afc1aba4e8b37af02" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "namePrefix": "@",
    "type": "TXT",
    "content": "v=spf1 include:_spf.google.com ~all",
    "ttl": 3600,
    "comment": "SPF记录"
  }'
```

## 响应格式

### 成功响应 (200)
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 35,
    "zoneId": "72a832c4cf5f8b5afc1aba4e8b37af02",
    "recordId": "869bb3fe8be216947d506c633c24db49",
    "name": "test.0345678.xyz",
    "type": "A",
    "content": "192.168.1.100",
    "proxiable": true,
    "proxied": false,
    "ttl": 300,
    "priority": null,
    "weight": null,
    "port": null,
    "service": null,
    "proto": null,
    "target": null,
    "settings": {},
    "meta": {},
    "tags": [],
    "comment": "测试A记录",
    "commentModifiedOn": "2025-09-26T01:00:00.000000",
    "createdOn": "2025-09-26T01:00:00.000000",
    "modifiedOn": "2025-09-26T01:00:00.000000",
    "syncStatus": "SUCCESS",
    "syncError": null,
    "lastSyncTime": "2025-09-26T01:00:00.000000",
    "isActive": true,
    "remark": null,
    "createTime": "2025-09-26T01:00:00.000000",
    "updateTime": "2025-09-26T01:00:00.000000"
  },
  "timestamp": "2025-09-26T01:00:00.000000"
}
```

### 参数错误响应 (400)
```json
{
  "code": 400,
  "message": "参数错误: 记录名称前缀不能为空",
  "data": null,
  "timestamp": "2025-09-26T01:00:00.000000"
}
```

### 权限不足响应 (403)
```json
{
  "code": 403,
  "message": "权限不足",
  "data": null,
  "timestamp": "2025-09-26T01:00:00.000000"
}
```

### 服务器错误响应 (500)
```json
{
  "code": 500,
  "message": "创建DNS记录失败: Cloudflare API调用异常",
  "data": null,
  "timestamp": "2025-09-26T01:00:00.000000"
}
```

## 特殊说明

### 1. 记录名称处理
- `namePrefix`为"@"时，表示根域名记录
- `namePrefix`会自动拼接Zone域名，如输入"www"，实际记录名为"www.0345678.xyz"
- 如果`namePrefix`已包含完整域名，则直接使用

### 2. DNS记录类型说明
- **A记录**: IPv4地址解析，`content`为IP地址
- **AAAA记录**: IPv6地址解析，`content`为IPv6地址
- **CNAME记录**: 域名别名，`content`为目标域名
- **MX记录**: 邮件服务器，需设置`priority`优先级
- **TXT记录**: 文本记录，常用于SPF、DKIM等
- **SRV记录**: 服务记录，需设置`priority`、`weight`、`port`、`service`、`proto`、`target`
- **NS记录**: 名称服务器记录

### 3. Cloudflare代理功能
- 仅A、AAAA、CNAME记录支持`proxied`参数
- 启用代理后，流量会通过Cloudflare CDN
- 代理状态会影响记录的实际解析结果

### 4. TTL设置
- TTL为1时表示自动TTL（由Cloudflare决定）
- 其他值为具体的秒数（如300=5分钟，3600=1小时）
- 启用代理的记录TTL会被Cloudflare自动管理

## 数据库同步
创建成功的DNS记录会自动同步到本地数据库的`cloudflare_dns_record`表中，包含：
- Cloudflare记录ID和本地记录ID的映射
- 完整的DNS记录信息
- 同步状态和时间戳
- 本地管理字段（备注、启用状态等）

## 错误处理
接口包含完整的错误处理机制：
- 参数验证错误（400）
- 权限验证错误（403）
- Cloudflare API调用错误（500）
- 数据库操作错误（500）
- 网络连接错误（500）

## 日志记录
所有操作都会记录详细日志，包括：
- 管理员操作记录
- API调用过程
- 数据转换过程
- 错误信息和堆栈
- 性能监控数据