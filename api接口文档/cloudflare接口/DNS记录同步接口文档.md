# Cloudflare DNS记录同步接口文档

## 接口概述
本文档描述了Cloudflare DNS记录数据同步和管理相关的API接口。这些接口允许管理员从Cloudflare API同步DNS记录到本地数据库，并进行查询和管理操作。

## 认证要求
所有接口都需要管理员权限，请求头中必须包含有效的JWT Token：
```
Authorization: Bearer <管理员JWT Token>
```

## 接口列表

### 1. 同步指定Zone的DNS记录

#### 接口信息
- **URL**: `POST /api/cloudflare/dns/sync/{zoneId}`
- **描述**: 从Cloudflare API同步指定Zone的所有DNS记录到本地数据库
- **权限**: 管理员

#### 请求参数
| 参数名 | 类型 | 位置 | 必填 | 描述 |
|--------|------|------|------|------|
| zoneId | String | Path | 是 | Cloudflare Zone ID |

#### 请求示例
```bash
curl -X POST "http://localhost:8001/api/cloudflare/dns/sync/3b9eb843842c7fd99d087fd1342dcb43" \
  -H "Authorization: Bearer <管理员JWT Token>" \
  -H "Content-Type: application/json"
```

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "成功同步 15 条DNS记录",
  "timestamp": "2025-09-25T23:00:00"
}
```

### 2. 同步所有Zone的DNS记录

#### 接口信息
- **URL**: `POST /api/cloudflare/dns/sync-all`
- **描述**: 从Cloudflare API同步所有Zone的DNS记录到本地数据库
- **权限**: 管理员

#### 请求示例
```bash
curl -X POST "http://localhost:8001/api/cloudflare/dns/sync-all" \
  -H "Authorization: Bearer <管理员JWT Token>" \
  -H "Content-Type: application/json"
```

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "成功同步 89 条DNS记录",
  "timestamp": "2025-09-25T23:00:00"
}
```

### 3. 获取指定Zone的DNS记录

#### 接口信息
- **URL**: `GET /api/cloudflare/dns/zone/{zoneId}`
- **描述**: 获取指定Zone的本地DNS记录列表
- **权限**: 管理员

#### 请求参数
| 参数名 | 类型 | 位置 | 必填 | 描述 |
|--------|------|------|------|------|
| zoneId | String | Path | 是 | Cloudflare Zone ID |

#### 请求示例
```bash
curl -X GET "http://localhost:8001/api/cloudflare/dns/zone/3b9eb843842c7fd99d087fd1342dcb43" \
  -H "Authorization: Bearer <管理员JWT Token>"
```

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "zoneId": "3b9eb843842c7fd99d087fd1342dcb43",
      "recordId": "869bb3fe8be216947d506c633c24db49",
      "name": "example.com",
      "type": "A",
      "content": "192.168.1.1",
      "proxiable": true,
      "proxied": false,
      "ttl": 300,
      "priority": null,
      "weight": null,
      "port": null,
      "comment": "主域名A记录",
      "createdOn": "2025-09-25 15:30:00",
      "modifiedOn": "2025-09-25 15:30:00",
      "syncStatus": "SUCCESS",
      "isActive": true
    }
  ],
  "timestamp": "2025-09-25T23:00:00"
}
```

### 4. 根据类型获取DNS记录

#### 接口信息
- **URL**: `GET /api/cloudflare/dns/zone/{zoneId}/type/{type}`
- **描述**: 获取指定Zone中指定类型的DNS记录
- **权限**: 管理员

#### 请求参数
| 参数名 | 类型 | 位置 | 必填 | 描述 |
|--------|------|------|------|------|
| zoneId | String | Path | 是 | Cloudflare Zone ID |
| type | String | Path | 是 | DNS记录类型（A、AAAA、CNAME、MX、TXT、NS等） |

#### 请求示例
```bash
curl -X GET "http://localhost:8001/api/cloudflare/dns/zone/3b9eb843842c7fd99d087fd1342dcb43/type/A" \
  -H "Authorization: Bearer <管理员JWT Token>"
```

### 5. 获取DNS记录详情

#### 接口信息
- **URL**: `GET /api/cloudflare/dns/record/{recordId}`
- **描述**: 根据记录ID获取DNS记录详情
- **权限**: 管理员

#### 请求参数
| 参数名 | 类型 | 位置 | 必填 | 描述 |
|--------|------|------|------|------|
| recordId | String | Path | 是 | Cloudflare DNS记录ID |

#### 请求示例
```bash
curl -X GET "http://localhost:8001/api/cloudflare/dns/record/869bb3fe8be216947d506c633c24db49" \
  -H "Authorization: Bearer <管理员JWT Token>"
```

### 6. 删除DNS记录

#### 接口信息
- **URL**: `DELETE /api/cloudflare/dns/record/{recordId}`
- **描述**: 删除指定的本地DNS记录（仅删除本地数据，不影响Cloudflare）
- **权限**: 管理员

#### 请求参数
| 参数名 | 类型 | 位置 | 必填 | 描述 |
|--------|------|------|------|------|
| recordId | String | Path | 是 | Cloudflare DNS记录ID |

#### 请求示例
```bash
curl -X DELETE "http://localhost:8001/api/cloudflare/dns/record/869bb3fe8be216947d506c633c24db49" \
  -H "Authorization: Bearer <管理员JWT Token>"
```

### 7. 统计DNS记录数量

#### 接口信息
- **URL**: `GET /api/cloudflare/dns/zone/{zoneId}/count`
- **描述**: 统计指定Zone的DNS记录数量
- **权限**: 管理员

#### 请求参数
| 参数名 | 类型 | 位置 | 必填 | 描述 |
|--------|------|------|------|------|
| zoneId | String | Path | 是 | Cloudflare Zone ID |

#### 请求示例
```bash
curl -X GET "http://localhost:8001/api/cloudflare/dns/zone/3b9eb843842c7fd99d087fd1342dcb43/count" \
  -H "Authorization: Bearer <管理员JWT Token>"
```

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 15,
  "timestamp": "2025-09-25T23:00:00"
}
```

## 数据库表结构

### cloudflare_dns_record 表
DNS记录表包含以下主要字段：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | bigint | 主键ID |
| zone_id | varchar(100) | 关联的Zone ID |
| record_id | varchar(100) | Cloudflare DNS记录ID |
| name | varchar(255) | DNS记录名称 |
| type | varchar(20) | DNS记录类型 |
| content | text | DNS记录内容 |
| proxiable | tinyint(1) | 是否可代理 |
| proxied | tinyint(1) | 是否已代理 |
| ttl | int | TTL值 |
| priority | int | 优先级（MX、SRV记录） |
| weight | int | 权重（SRV记录） |
| port | int | 端口（SRV记录） |
| settings | json | 记录设置 |
| meta | json | 元数据 |
| tags | json | 标签列表 |
| comment | text | 备注 |
| created_on | datetime | Cloudflare创建时间 |
| modified_on | datetime | Cloudflare修改时间 |
| sync_status | varchar(20) | 同步状态 |
| is_active | tinyint(1) | 本地启用状态 |

## 错误码说明

| 错误码 | 描述 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 使用流程

1. **首次同步**: 使用 `POST /api/cloudflare/dns/sync-all` 同步所有Zone的DNS记录
2. **增量同步**: 使用 `POST /api/cloudflare/dns/sync/{zoneId}` 同步特定Zone的DNS记录
3. **查询记录**: 使用各种GET接口查询和统计DNS记录
4. **数据管理**: 使用DELETE接口清理不需要的本地记录

## 注意事项

1. 同步操作会清空指定Zone的旧记录，然后插入最新数据
2. 删除操作仅删除本地数据库记录，不会影响Cloudflare上的实际DNS记录
3. 所有时间字段都使用北京时间（UTC+8）
4. JSON字段支持复杂数据结构的存储和查询
5. 建议定期同步以保持数据最新