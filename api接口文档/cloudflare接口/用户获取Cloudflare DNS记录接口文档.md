# 用户获取Cloudflare DNS记录接口文档

## 接口基本信息

- **接口标识**：`USER_CLOUDFLARE_DNS_RECORD_LIST`
- **请求路径**：`GET /api/user/cloudflare/dns-records`
- **接口描述**：用户获取自己注册的Cloudflare域名下的DNS记录列表
- **认证要求**：需要JWT令牌认证
- **适用业务单元**：用户DNS记录查询

## 请求参数

### 请求头
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### 查询参数
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| userDomainId | Long | 否 | 用户域名ID（user_cloudflare_domain表的id字段），不传则获取所有域名下的记录 | 123 |
| type | String | 否 | DNS记录类型过滤 | "A", "CNAME", "MX" |

## 请求示例

### 1. 获取所有DNS记录
```bash
curl -X GET "http://localhost:8001/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. 获取指定域名下的DNS记录
```bash
curl -X GET "http://localhost:8001/api/user/cloudflare/dns-records?userDomainId=123" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. 获取指定类型的DNS记录
```bash
curl -X GET "http://localhost:8001/api/user/cloudflare/dns-records?type=A" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. 获取指定域名下指定类型的DNS记录
```bash
curl -X GET "http://localhost:8001/api/user/cloudflare/dns-records?userDomainId=123&type=CNAME" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 响应格式

### 成功响应（状态码 200）
```json
{
  "code": 200,
  "message": "获取DNS记录列表成功",
  "data": [
    {
      "id": 1,
      "recordId": "cloudflare_record_id_123",
      "userDomainId": 123,
      "userSubdomain": "0.1.example.com",
      "name": "www.0.1.example.com",
      "type": "A",
      "content": "192.168.1.1",
      "ttl": 300,
      "proxied": false,
      "proxiable": true,
      "priority": null,
      "weight": null,
      "port": null,
      "comment": "网站主页",
      "syncStatus": "SUCCESS",
      "syncError": null,
      "isActive": true,
      "createdOn": "2025-09-26T10:30:00",
      "modifiedOn": "2025-09-26T10:30:00",
      "createTime": "2025-09-26T10:30:00",
      "updateTime": "2025-09-26T10:30:00"
    },
    {
      "id": 2,
      "recordId": "cloudflare_record_id_456",
      "userDomainId": 123,
      "userSubdomain": "0.1.example.com",
      "name": "mail.0.1.example.com",
      "type": "MX",
      "content": "mail.example.com",
      "ttl": 3600,
      "proxied": false,
      "proxiable": false,
      "priority": 10,
      "weight": null,
      "port": null,
      "comment": "邮件服务器",
      "syncStatus": "SUCCESS",
      "syncError": null,
      "isActive": true,
      "createdOn": "2025-09-26T11:00:00",
      "modifiedOn": "2025-09-26T11:00:00",
      "createTime": "2025-09-26T11:00:00",
      "updateTime": "2025-09-26T11:00:00"
    }
  ],
  "timestamp": "2025-09-26T12:00:00Z"
}
```

### 空结果响应（状态码 200）
```json
{
  "code": 200,
  "message": "获取DNS记录列表成功",
  "data": [],
  "timestamp": "2025-09-26T12:00:00Z"
}
```

### 失败响应

#### 用户域名不存在（404）
```json
{
  "code": 404,
  "message": "用户域名不存在或无权限访问",
  "data": null,
  "timestamp": "2025-09-26T12:00:00Z"
}
```

#### 未授权访问（401）
```json
{
  "code": 401,
  "message": "未授权访问",
  "data": null,
  "timestamp": "2025-09-26T12:00:00Z"
}
```

#### 服务器错误（500）
```json
{
  "code": 500,
  "message": "系统错误，请稍后重试",
  "data": null,
  "timestamp": "2025-09-26T12:00:00Z"
}
```

## 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 本地记录ID |
| recordId | String | Cloudflare记录ID |
| userDomainId | Long | 用户域名ID |
| userSubdomain | String | 用户子域名 |
| name | String | 完整的DNS记录名称 |
| type | String | DNS记录类型 |
| content | String | DNS记录内容 |
| ttl | Integer | TTL值（秒） |
| proxied | Boolean | 是否启用Cloudflare代理 |
| proxiable | Boolean | 是否支持代理 |
| priority | Integer | 优先级（MX/SRV记录） |
| weight | Integer | 权重（SRV记录） |
| port | Integer | 端口（SRV记录） |
| comment | String | 记录注释 |
| syncStatus | String | 同步状态（SUCCESS/FAILED） |
| syncError | String | 同步错误信息 |
| isActive | Boolean | 记录是否激活 |
| createdOn | LocalDateTime | Cloudflare创建时间 |
| modifiedOn | LocalDateTime | Cloudflare修改时间 |
| createTime | LocalDateTime | 本地创建时间 |
| updateTime | LocalDateTime | 本地更新时间 |

## 业务逻辑说明

### 1. 权限验证
- 验证JWT令牌有效性
- 用户只能查看自己创建的DNS记录
- 如果指定userDomainId，验证用户是否拥有该域名

### 2. 数据过滤
- 只返回isActive=true的记录
- 只返回userId匹配当前用户的记录
- 支持按域名ID和记录类型过滤

### 3. 数据来源
- 从本地数据库cloudflare_dns_record表查询
- 关联user_cloudflare_domain表获取域名信息
- 确保数据的一致性和完整性

### 4. 排序规则
- 按创建时间倒序排列
- 同一域名下的记录按类型分组

## 支持的DNS记录类型

- **A记录**: IPv4地址解析
- **AAAA记录**: IPv6地址解析
- **CNAME记录**: 域名别名
- **MX记录**: 邮件服务器（包含priority字段）
- **TXT记录**: 文本记录
- **NS记录**: 名称服务器
- **SRV记录**: 服务记录（包含priority、weight、port字段）

## 使用场景

### 1. 域名管理面板
用户查看自己所有域名下的DNS记录，进行统一管理。

### 2. 单域名记录查看
用户查看特定域名下的所有DNS记录，便于针对性管理。

### 3. 记录类型筛选
用户查看特定类型的DNS记录，如只查看A记录或MX记录。

### 4. 记录状态监控
用户查看DNS记录的同步状态，确保记录正常生效。

## 注意事项

1. **权限隔离**：
   - 用户只能查看自己创建的DNS记录
   - 无法查看其他用户的记录或管理员创建的记录

2. **数据实时性**：
   - 返回的是本地数据库中的记录
   - 如需最新状态，可配合同步接口使用

3. **分页处理**：
   - 当前版本不支持分页
   - 如记录数量较多，建议使用过滤参数

4. **记录状态**：
   - 只返回激活状态的记录
   - 已删除的记录不会出现在列表中

5. **错误处理**：
   - 提供详细的错误信息
   - 区分不同类型的错误场景

## 相关接口

- [用户添加Cloudflare DNS记录](./用户添加Cloudflare%20DNS记录接口文档.md)
- [用户删除Cloudflare DNS记录](./用户删除Cloudflare%20DNS记录接口文档.md)
- [用户获取单个DNS记录详情](#获取单个dns记录详情)
- [用户Cloudflare域名管理](./用户Cloudflare域名接口.md)

## 获取单个DNS记录详情

### 接口信息
- **请求路径**：`GET /api/user/cloudflare/dns-records/{recordId}`
- **接口描述**：根据记录ID获取DNS记录详情

### 请求示例
```bash
curl -X GET "http://localhost:8001/api/user/cloudflare/dns-records/cloudflare_record_id_123" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 响应示例
```json
{
  "code": 200,
  "message": "获取DNS记录详情成功",
  "data": {
    "id": 1,
    "recordId": "cloudflare_record_id_123",
    "userDomainId": 123,
    "userSubdomain": "0.1.example.com",
    "name": "www.0.1.example.com",
    "type": "A",
    "content": "192.168.1.1",
    "ttl": 300,
    "proxied": false,
    "proxiable": true,
    "priority": null,
    "weight": null,
    "port": null,
    "comment": "网站主页",
    "syncStatus": "SUCCESS",
    "syncError": null,
    "isActive": true,
    "createdOn": "2025-09-26T10:30:00",
    "modifiedOn": "2025-09-26T10:30:00",
    "createTime": "2025-09-26T10:30:00",
    "updateTime": "2025-09-26T10:30:00"
  },
  "timestamp": "2025-09-26T12:00:00Z"
}
```

## 测试用例

### 测试用例1：获取所有DNS记录
```bash
# 请求
GET /api/user/cloudflare/dns-records
Authorization: Bearer valid_jwt_token

# 预期响应
HTTP 200 OK
返回用户所有域名下的DNS记录列表
```

### 测试用例2：获取指定域名的DNS记录
```bash
# 请求
GET /api/user/cloudflare/dns-records?userDomainId=123
Authorization: Bearer valid_jwt_token

# 预期响应
HTTP 200 OK
返回指定域名下的DNS记录列表
```

### 测试用例3：获取指定类型的DNS记录
```bash
# 请求
GET /api/user/cloudflare/dns-records?type=A
Authorization: Bearer valid_jwt_token

# 预期响应
HTTP 200 OK
返回所有A类型的DNS记录
```

### 测试用例4：无权限访问域名
```bash
# 请求
GET /api/user/cloudflare/dns-records?userDomainId=999
Authorization: Bearer valid_jwt_token

# 预期响应
HTTP 404 Not Found
{"code": 404, "message": "用户域名不存在或无权限访问"}
```

### 测试用例5：未授权访问
```bash
# 请求
GET /api/user/cloudflare/dns-records
Authorization: Bearer invalid_jwt_token

# 预期响应
HTTP 401 Unauthorized
{"code": 401, "message": "未授权访问"}