# 管理员Cloudflare前缀池管理接口文档

## 接口概述

本文档描述了管理员用于管理Cloudflare域名前缀池的API接口。前缀池用于为用户分配十六进制前缀（0.1到6.f），每个域名最多支持105个前缀。

## 基础信息

- **Base URL**: `http://localhost:8080/api/admin/cloudflare/zones`
- **认证方式**: JWT Token (需要管理员权限)
- **Content-Type**: `application/json`
- **字符编码**: `UTF-8`

## 前缀池说明

### 前缀范围
- **第一位**: 0-6 (7个选择)
- **第二位**: 1-f (15个选择，排除0)
- **总计**: 7 × 15 = 105个前缀
- **示例**: 0.1, 0.2, 0.3, ..., 0.f, 1.1, 1.2, ..., 6.f

### 前缀池状态
- **未分配**: 可以分配给新用户
- **已分配**: 已经分配给用户，不可重复使用
- **使用率**: 已分配前缀数量 / 总前缀数量

---

## 1. 初始化域名前缀池

### 基本信息
- **接口标识**: `ADMIN_INIT_PREFIX_POOL`
- **请求路径**: `POST /{zoneId}/prefix-pool/init`
- **接口描述**: 为指定域名初始化前缀池，生成105个前缀
- **认证要求**: 管理员权限

### 请求参数
- **路径参数**:
  - `zoneId` (string, required): Cloudflare Zone ID

### 请求头
```http
Authorization: Bearer {admin_jwt_token}
Content-Type: application/json
```

### 响应示例

#### 成功响应 (200)
```json
{
  "code": 200,
  "message": "Prefix pool initialized successfully",
  "data": {
    "zoneId": "c4ccaa1a6f609fcaef41c9a881230a85",
    "zoneName": "example.com",
    "status": "INITIALIZED",
    "message": "前缀池初始化完成",
    "totalPrefixes": 105,
    "availablePrefixes": 105,
    "assignedPrefixes": 0
  },
  "timestamp": "2025-09-26T10:30:00Z"
}
```

#### 前缀池已存在 (200)
```json
{
  "code": 200,
  "message": "Prefix pool initialized successfully",
  "data": {
    "zoneId": "c4ccaa1a6f609fcaef41c9a881230a85",
    "zoneName": "example.com",
    "status": "ALREADY_EXISTS",
    "message": "前缀池已存在，无需初始化",
    "existingPrefixes": 105
  },
  "timestamp": "2025-09-26T10:30:00Z"
}
```

#### 错误响应
```json
{
  "code": 404,
  "message": "Failed to initialize prefix pool: 域名不存在: invalid_zone_id",
  "data": null,
  "timestamp": "2025-09-26T10:30:00Z"
}
```

---

## 2. 重置域名前缀池

### 基本信息
- **接口标识**: `ADMIN_RESET_PREFIX_POOL`
- **请求路径**: `POST /{zoneId}/prefix-pool/reset`
- **接口描述**: 清空现有前缀池并重新初始化
- **认证要求**: 管理员权限

### 请求参数
- **路径参数**:
  - `zoneId` (string, required): Cloudflare Zone ID

### 响应示例

#### 成功响应 (200)
```json
{
  "code": 200,
  "message": "Prefix pool reset successfully",
  "data": {
    "zoneId": "c4ccaa1a6f609fcaef41c9a881230a85",
    "zoneName": "example.com",
    "status": "RESET",
    "message": "前缀池重置完成",
    "totalPrefixes": 105,
    "availablePrefixes": 105,
    "assignedPrefixes": 0
  },
  "timestamp": "2025-09-26T10:30:00Z"
}
```

#### 错误响应 - 有已分配前缀
```json
{
  "code": 500,
  "message": "Failed to reset prefix pool: 该域名有 5 个已分配的前缀，无法重置。请先处理用户注册记录。",
  "data": null,
  "timestamp": "2025-09-26T10:30:00Z"
}
```

---

## 3. 查看域名前缀池状态

### 基本信息
- **接口标识**: `ADMIN_GET_PREFIX_POOL_STATUS`
- **请求路径**: `GET /{zoneId}/prefix-pool/status`
- **接口描述**: 获取指定域名的前缀池详细状态
- **认证要求**: 管理员权限

### 请求参数
- **路径参数**:
  - `zoneId` (string, required): Cloudflare Zone ID

### 响应示例

#### 成功响应 (200)
```json
{
  "code": 200,
  "message": "Get prefix pool status successfully",
  "data": {
    "zoneId": "c4ccaa1a6f609fcaef41c9a881230a85",
    "zoneName": "example.com",
    "zoneStatus": "active",
    "autoAssignEnabled": true,
    "totalPrefixes": 105,
    "availablePrefixes": 100,
    "assignedPrefixes": 5,
    "usagePercentage": 4.76,
    "userCount": 5,
    "userLimit": 100,
    "dnsRecordCount": 15,
    "dnsRecordLimit": 200,
    "isFull": false
  },
  "timestamp": "2025-09-26T10:30:00Z"
}
```

---

## 4. 批量初始化所有域名前缀池

### 基本信息
- **接口标识**: `ADMIN_BATCH_INIT_PREFIX_POOLS`
- **请求路径**: `POST /prefix-pool/batch-init`
- **接口描述**: 为所有启用自动分配的域名批量初始化前缀池
- **认证要求**: 管理员权限

### 响应示例

#### 成功响应 (200)
```json
{
  "code": 200,
  "message": "Batch initialize prefix pools successfully",
  "data": {
    "totalZones": 10,
    "initializedCount": 3,
    "skippedCount": 7,
    "message": "批量初始化完成：共10个域名，成功初始化3个，跳过7个"
  },
  "timestamp": "2025-09-26T10:30:00Z"
}
```

---

## 5. 查看所有域名前缀池状态

### 基本信息
- **接口标识**: `ADMIN_GET_ALL_PREFIX_POOL_STATUS`
- **请求路径**: `GET /prefix-pool/status-all`
- **接口描述**: 获取所有域名的前缀池统计信息
- **认证要求**: 管理员权限

### 响应示例

#### 成功响应 (200)
```json
{
  "code": 200,
  "message": "Get all prefix pool status successfully",
  "data": {
    "totalZones": 5,
    "zonesWithPool": 3,
    "zonesWithoutPool": 2,
    "totalPrefixes": 315,
    "totalAssigned": 25,
    "totalAvailable": 290,
    "overallUsagePercentage": 7.94,
    "zoneDetails": [
      {
        "zone_id": "zone1",
        "zone_name": "example1.com",
        "zone_status": "active",
        "auto_assign_enabled": true,
        "user_count": 5,
        "user_limit": 100,
        "total_prefixes": 105,
        "assigned_prefixes": 5,
        "available_prefixes": 100,
        "usage_percentage": 4.76,
        "pool_status": "ACTIVE"
      },
      {
        "zone_id": "zone2",
        "zone_name": "example2.com",
        "zone_status": "active",
        "auto_assign_enabled": true,
        "user_count": 0,
        "user_limit": 100,
        "total_prefixes": 0,
        "assigned_prefixes": 0,
        "available_prefixes": 0,
        "usage_percentage": 0,
        "pool_status": "NO_POOL"
      }
    ]
  },
  "timestamp": "2025-09-26T10:30:00Z"
}
```

---

## 6. 获取域名详细信息

### 基本信息
- **接口标识**: `ADMIN_GET_ZONE_DETAILS`
- **请求路径**: `GET /{zoneId}/details`
- **接口描述**: 获取域名的完整信息，包括前缀池和用户注册详情
- **认证要求**: 管理员权限

### 请求参数
- **路径参数**:
  - `zoneId` (string, required): Cloudflare Zone ID

### 响应示例

#### 成功响应 (200)
```json
{
  "code": 200,
  "message": "Get zone details successfully",
  "data": {
    "zoneInfo": {
      "id": 1,
      "zoneId": "c4ccaa1a6f609fcaef41c9a881230a85",
      "name": "example.com",
      "status": "active",
      "userCount": 5,
      "userLimit": 100,
      "dnsRecordCount": 15,
      "dnsRecordLimit": 200,
      "autoAssignEnabled": true,
      "isFull": false
    },
    "prefixPoolStats": {
      "total_prefixes": 105,
      "assigned_prefixes": 5,
      "available_prefixes": 100,
      "usage_percentage": 4.76
    },
    "userRegistrations": [
      {
        "id": 1,
        "user_id": 2,
        "username": "testuser",
        "email": "test@example.com",
        "assigned_prefix": "0.1",
        "full_subdomain": "0.1.example.com",
        "status": "ACTIVE",
        "dns_record_count": 2,
        "dns_record_limit": 2,
        "assigned_time": "2025-09-26T10:00:00",
        "create_time": "2025-09-26T10:00:00"
      }
    ],
    "userRegistrationCount": 1
  },
  "timestamp": "2025-09-26T10:30:00Z"
}
```

---

## 错误码说明

| 错误码 | 含义 | 处理建议 |
|--------|------|----------|
| 403 | 权限不足 | 检查JWT Token是否有效且具有管理员权限 |
| 404 | 域名不存在 | 检查Zone ID是否正确 |
| 500 | 服务器错误 | 查看错误消息，可能是数据库连接或业务逻辑错误 |

## 使用流程建议

### 新域名前缀池初始化流程
1. 添加新的Cloudflare域名到系统
2. 调用 `POST /{zoneId}/prefix-pool/init` 初始化前缀池
3. 调用 `GET /{zoneId}/prefix-pool/status` 验证初始化结果

### 批量管理流程
1. 调用 `GET /prefix-pool/status-all` 查看所有域名状态
2. 对于没有前缀池的域名，调用 `POST /prefix-pool/batch-init` 批量初始化
3. 再次调用 `GET /prefix-pool/status-all` 确认初始化结果

### 问题排查流程
1. 调用 `GET /{zoneId}/details` 获取域名完整信息
2. 检查前缀池状态和用户注册情况
3. 如需重置，先处理用户注册记录，再调用重置接口

## 注意事项

1. **前缀池重置**: 只有在没有已分配前缀时才能重置，否则会影响现有用户
2. **权限验证**: 所有接口都需要管理员权限，确保JWT Token有效
3. **并发安全**: 前缀分配使用数据库事务保证原子性
4. **监控建议**: 定期检查前缀池使用率，及时处理满池情况