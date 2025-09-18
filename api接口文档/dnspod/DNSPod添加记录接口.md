# DNSPod添加记录接口文档

## 接口概述

本接口用于向指定域名添加DNS解析记录，支持A、CNAME、MX等多种记录类型。

## 接口信息

- **接口标识**：`DNSPOD_CREATE_RECORD`
- **请求路径**：`POST /api/dnspod/records`
- **接口描述**：添加域名解析记录到腾讯云DNSPod
- **认证要求**：需要JWT认证
- **适用业务单元**：域名解析记录管理

## 请求参数

### 必填参数

| 参数名称 | 类型 | 描述 | 示例值 |
|---------|------|------|--------|
| domain | String | 域名 | cblog.eu |
| recordType | String | 记录类型（A、CNAME、MX、TXT等） | A |
| value | String | 记录值（如IP地址、域名等） | 8.8.8.8 |

### 可选参数

| 参数名称 | 类型 | 描述 | 默认值 | 示例值 |
|---------|------|------|--------|--------|
| recordLine | String | 记录线路 | 默认 | 默认 |
| subDomain | String | 主机记录（子域名） | @ | www |
| ttl | Long | TTL值（秒） | 600 | 600 |
| mx | Long | MX优先级（MX记录时必填） | null | 10 |
| weight | Long | 权重（0-100） | null | 50 |
| status | String | 记录状态 | ENABLE | ENABLE/DISABLE |
| remark | String | 备注信息 | null | 测试记录 |

## 请求格式

### JSON请求体格式

```json
{
  "domain": "cblog.eu",
  "recordType": "A",
  "value": "8.8.8.8",
  "recordLine": "默认",
  "subDomain": "test",
  "ttl": 600,
  "mx": null,
  "weight": null,
  "status": "ENABLE",
  "remark": "测试A记录"
}
```

## 请求示例

### 添加A记录

```bash
curl -X POST "http://localhost:8080/api/dnspod/records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "cblog.eu",
    "recordType": "A",
    "value": "8.8.8.8",
    "subDomain": "test",
    "ttl": 600,
    "remark": "测试A记录"
  }'
```

### 添加CNAME记录

```bash
curl -X POST "http://localhost:8080/api/dnspod/records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "cblog.eu",
    "recordType": "CNAME",
    "value": "example.com",
    "subDomain": "www",
    "ttl": 600
  }'
```

### 添加MX记录

```bash
curl -X POST "http://localhost:8080/api/dnspod/records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "cblog.eu",
    "recordType": "MX",
    "value": "mail.example.com",
    "mx": 10,
    "ttl": 600
  }'
```

## 响应格式

### 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "skipSign": false,
    "requestId": "ab4f1426-ea15-42ea-8183-dc1b44151166",
    "recordId": 162
  },
  "timestamp": "2025-08-16T15:30:00Z"
}
```

### 失败响应

#### 参数错误（400）

```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": null,
  "timestamp": "2025-08-16T15:30:00Z"
}
```

#### 未授权（401）

```json
{
  "code": 401,
  "message": "未授权：需要有效的JWT令牌",
  "data": null,
  "timestamp": "2025-08-16T15:30:00Z"
}
```

#### 记录已存在（409）

```json
{
  "code": 500,
  "message": "创建域名解析记录失败: 记录已经存在，无需再次添加",
  "data": null,
  "timestamp": "2025-08-16T15:30:00Z"
}
```

#### 服务器错误（500）

```json
{
  "code": 500,
  "message": "创建域名解析记录失败: 调用腾讯云DNSPod创建记录API失败",
  "data": null,
  "timestamp": "2025-08-16T15:30:00Z"
}
```

## 记录类型说明

| 记录类型 | 描述 | 值格式示例 | 特殊要求 |
|---------|------|-----------|----------|
| A | IPv4地址记录 | 192.168.1.1 | 无 |
| AAAA | IPv6地址记录 | 2001:db8::1 | 无 |
| CNAME | 别名记录 | example.com. | 值需以.结尾 |
| MX | 邮件交换记录 | mail.example.com. | 需要设置mx优先级 |
| TXT | 文本记录 | "v=spf1 include:_spf.example.com ~all" | 无 |
| NS | 域名服务器记录 | ns1.example.com. | 值需以.结尾 |
| SRV | 服务记录 | 10 5 443 target.example.com. | 特殊格式 |

## 线路类型说明

| 线路名称 | 描述 |
|---------|------|
| 默认 | 默认线路，适用于所有地区 |
| 电信 | 中国电信用户访问 |
| 联通 | 中国联通用户访问 |
| 移动 | 中国移动用户访问 |
| 海外 | 海外用户访问 |

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|-------|------|----------|
| 400 | 参数错误 | 检查必填参数是否完整，参数格式是否正确 |
| 401 | 未授权 | 检查JWT令牌是否有效 |
| 403 | 禁止访问 | 检查域名权限，确保有操作该域名的权限 |
| 409 | 记录冲突 | 记录已存在，请检查是否重复添加 |
| 500 | 服务器错误 | 检查腾讯云API配置，查看详细错误信息 |

## 注意事项

1. **记录延迟**：新添加的解析记录存在短暂的索引延迟，如果查询不到新增记录，请在30秒后重试
2. **权限要求**：需要对目标域名有管理权限
3. **记录限制**：不同套餐对记录数量和类型有不同限制
4. **TTL限制**：TTL值范围为1-604800秒，不同套餐最小值不同
5. **MX记录**：添加MX记录时必须设置mx参数（优先级）
6. **CNAME限制**：CNAME记录不能与其他记录类型共存于同一主机记录下

## 测试用例

### 测试用例1：成功添加A记录

**请求参数**：
```json
{
  "domain": "cblog.eu",
  "recordType": "A",
  "value": "8.8.8.8",
  "subDomain": "test",
  "ttl": 600,
  "remark": "测试记录"
}
```

**预期响应**：
- code=200
- data包含recordId

### 测试用例2：添加重复记录

**请求参数**：
```json
{
  "domain": "cblog.eu",
  "recordType": "A",
  "value": "8.8.8.8",
  "subDomain": "test"
}
```

**预期响应**：
- code=500
- message包含"记录已经存在"

### 测试用例3：无效域名

**请求参数**：
```json
{
  "domain": "invalid-domain",
  "recordType": "A",
  "value": "8.8.8.8"
}
```

**预期响应**：
- code=500
- message包含域名相关错误信息

## 接口格式变更说明

**重要变更**：
1. **Content-Type**: 从 `application/x-www-form-urlencoded` 改为 `application/json`
2. **请求体格式**: 从URL参数格式改为JSON格式
3. **向后兼容**: 建议新项目使用JSON格式，提供更好的可读性和安全性
