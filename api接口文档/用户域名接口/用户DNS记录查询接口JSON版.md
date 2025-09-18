# 用户DNS记录查询接口（JSON版）

## 接口概述

该接口用于查询当前登录用户的DNS解析记录，支持通过JSON格式传输查询参数，提供更灵活的查询条件和分页功能。

## 接口信息

- **接口地址**：`POST /api/user/dns-records/query`
- **请求方法**：POST
- **Content-Type**：`application/json`
- **认证要求**：需要JWT认证（用户登录后获取的token）

## 请求参数

### 请求头
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Authorization | String | 是 | JWT令牌，格式：`Bearer {token}` |
| Content-Type | String | 是 | 固定值：`application/json` |

### 请求体（JSON格式）
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| subdomainId | Long | 否 | 子域名ID，用于查询指定3级域名的DNS记录 | 8 |
| type | String | 否 | 记录类型过滤（A、CNAME、MX、TXT等） | "A" |
| status | String | 否 | 记录状态过滤（ENABLE、DISABLE等） | "ENABLE" |
| syncStatus | String | 否 | 同步状态过滤（SUCCESS、PENDING、FAILED） | "SUCCESS" |
| offset | Integer | 否 | 分页偏移量，默认0 | 0 |
| limit | Integer | 否 | 分页限制数量，默认100 | 10 |

## 请求示例

### 1. 查询指定3级域名的所有DNS记录
```bash
curl -X POST "http://localhost:8080/api/user/dns-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "subdomainId": 8
  }'
```

### 2. 查询所有A记录
```bash
curl -X POST "http://localhost:8080/api/user/dns-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "type": "A"
  }'
```

### 3. 复合查询（指定子域名的A记录，带分页）
```bash
curl -X POST "http://localhost:8080/api/user/dns-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "subdomainId": 8,
    "type": "A",
    "status": "ENABLE",
    "offset": 0,
    "limit": 10
  }'
```

### 4. 查询同步状态为成功的记录
```bash
curl -X POST "http://localhost:8080/api/user/dns-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "syncStatus": "SUCCESS"
  }'
```

## 响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "subdomainId": 8,
      "recordId": 12345,
      "name": "www",
      "type": "A",
      "value": "192.168.1.1",
      "line": "默认",
      "lineId": "0",
      "ttl": 600,
      "mx": null,
      "weight": null,
      "status": "ENABLE",
      "remark": "主站点",
      "monitorStatus": "Ok",
      "updatedOn": "2025-08-29T10:30:00",
      "syncStatus": "SUCCESS",
      "syncError": null,
      "createTime": "2025-08-29T10:00:00",
      "updateTime": "2025-08-29T10:30:00"
    }
  ]
}
```

### 错误响应

#### 无权限访问子域名
```json
{
  "code": 403,
  "message": "无权限访问该子域名的解析记录",
  "data": null
}
```

#### 参数验证失败
```json
{
  "code": 400,
  "message": "参数验证失败: 子域名ID必须大于0",
  "data": null
}
```

#### 未授权访问
```json
{
  "code": 401,
  "message": "未授权访问",
  "data": null
}
```

## 业务逻辑

1. **用户身份验证**：验证JWT令牌的有效性
2. **权限检查**：如果指定了subdomainId，验证用户是否拥有该子域名
3. **数据查询**：根据查询条件从数据库获取DNS记录
4. **结果过滤**：按照type、status、syncStatus等条件过滤结果
5. **分页处理**：根据offset和limit参数进行分页
6. **返回结果**：返回符合条件的DNS记录列表

## 与原接口的区别

| 特性 | 原接口（GET） | 新接口（POST JSON） |
|------|---------------|---------------------|
| 请求方法 | GET | POST |
| 参数传递 | URL查询参数 | JSON请求体 |
| Content-Type | 不需要 | application/json |
| 分页支持 | 无 | 支持offset/limit |
| 同步状态过滤 | 无 | 支持syncStatus |
| 复合查询 | 有限 | 完全支持 |

## 使用场景

1. **获取特定3级域名的DNS记录**：设置subdomainId参数
2. **按记录类型查询**：设置type参数（A、CNAME等）
3. **查询同步状态**：设置syncStatus参数，用于监控同步情况
4. **分页查询**：处理大量DNS记录时使用offset和limit
5. **复合条件查询**：同时使用多个过滤条件

## 注意事项

1. 所有查询都限制在当前用户的DNS记录范围内
2. subdomainId参数会进行权限验证，确保用户只能查询自己的子域名
3. 分页参数offset不能小于0，limit必须大于0
4. 如果不指定分页参数，默认返回前100条记录
5. 原有的GET接口仍然可用，两个接口可以并存使用

## 测试脚本

项目根目录下的 `test_dns_query.sh` 脚本提供了完整的测试示例。