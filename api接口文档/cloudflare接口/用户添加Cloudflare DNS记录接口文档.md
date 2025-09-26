# 用户添加Cloudflare DNS记录接口文档

## 接口基本信息

- **接口标识**：`USER_CLOUDFLARE_DNS_RECORD_ADD`
- **请求路径**：`POST /api/user/cloudflare/dns-records`
- **接口描述**：用户在自己注册的Cloudflare域名下添加DNS记录
- **认证要求**：需要JWT令牌认证
- **适用业务单元**：用户DNS记录管理

## 请求参数

### 请求头
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### 请求体
```typescript
interface UserCloudflareDnsRecordRequest {
  userDomainId: number;        // 用户域名ID（user_cloudflare_domain表的ID）
  namePrefix: string;          // DNS记录名称前缀（如"www"、"@"）
  type: string;                // DNS记录类型（A、AAAA、CNAME、MX、TXT、NS、SRV等）
  content: string;             // DNS记录内容（IP地址、域名、文本等）
  ttl?: number;                // TTL值，默认为1（自动）
  proxied?: boolean;           // 是否启用Cloudflare代理，默认false
  priority?: number;           // 优先级（MX、SRV记录使用）
  weight?: number;             // 权重（SRV记录使用）
  port?: number;               // 端口（SRV记录使用）
  service?: string;            // 服务名（SRV记录使用）
  proto?: string;              // 协议（SRV记录使用）
  target?: string;             // 目标（SRV记录使用）
  comment?: string;            // 记录注释
}
```

### 参数验证规则
```json
{
  "userDomainId": {
    "required": true,
    "type": "number",
    "message": "用户域名ID不能为空"
  },
  "namePrefix": {
    "required": true,
    "type": "string",
    "message": "记录名称前缀不能为空"
  },
  "type": {
    "required": true,
    "type": "string",
    "enum": ["A", "AAAA", "CNAME", "MX", "TXT", "NS", "SRV"],
    "message": "记录类型不能为空"
  },
  "content": {
    "required": true,
    "type": "string",
    "message": "记录内容不能为空"
  },
  "ttl": {
    "type": "number",
    "min": 1,
    "max": 2147483647,
    "default": 1,
    "message": "TTL值必须在1-2147483647范围内"
  }
}
```

## 响应示例

### 成功响应（状态码 200）
```json
{
  "code": 200,
  "message": "DNS记录添加成功",
  "data": {
    "id": 123,
    "recordId": "cloudflare_record_id_123",
    "userDomainId": 456,
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
    "comment": "测试记录",
    "syncStatus": "SUCCESS",
    "syncError": null,
    "isActive": true,
    "createdOn": "2025-09-26T10:30:00",
    "modifiedOn": "2025-09-26T10:30:00",
    "createTime": "2025-09-26T10:30:00",
    "updateTime": "2025-09-26T10:30:00"
  },
  "timestamp": "2025-09-26T10:30:00Z"
}
```

### 失败响应

#### 用户域名不存在（404）
```json
{
  "code": 404,
  "message": "用户域名不存在或无权限访问",
  "data": null,
  "timestamp": "2025-09-26T10:30:00Z"
}
```

#### DNS记录数量限制（423）
```json
{
  "code": 423,
  "message": "DNS记录数量已达上限（2条），无法添加新记录",
  "data": null,
  "timestamp": "2025-09-26T10:30:00Z"
}
```

#### 参数验证失败（400）
```json
{
  "code": 400,
  "message": "参数验证失败：IPv4地址格式不正确",
  "data": null,
  "timestamp": "2025-09-26T10:30:00Z"
}
```

#### DNS记录已存在（409）
```json
{
  "code": 409,
  "message": "DNS记录已存在",
  "data": null,
  "timestamp": "2025-09-26T10:30:00Z"
}
```

#### Cloudflare API错误（500）
```json
{
  "code": 500,
  "message": "创建DNS记录失败: Cloudflare API返回错误",
  "data": null,
  "timestamp": "2025-09-26T10:30:00Z"
}
```

## 业务逻辑说明

### 1. 权限验证
- 验证JWT令牌有效性
- 验证用户是否拥有指定域名的权限
- 验证域名状态是否为ACTIVE

### 2. 数量限制检查
- 检查用户在该域名下的DNS记录数量
- 默认限制为每个域名2条记录
- 达到限制时拒绝添加新记录

### 3. 记录格式验证
- A记录：验证IPv4地址格式
- AAAA记录：验证IPv6地址格式
- CNAME/MX/NS记录：验证域名格式
- TXT记录：验证长度限制
- SRV记录：验证完整格式和必需字段

### 4. 重复记录检查
- 普通记录：检查同名同类型记录是否存在
- NS记录：允许多条同名记录，但不允许相同内容

### 5. 完整域名构建
- 用户输入"www" → 实际记录名称"www.0.1.example.com"
- 用户输入"@" → 实际记录名称"0.1.example.com"

### 6. Cloudflare API调用
- 调用Cloudflare API创建DNS记录
- 处理API错误和异常情况
- 确保API调用成功后才保存本地记录

### 7. 本地数据同步
- 保存DNS记录到本地数据库
- 关联用户ID和用户域名ID
- 更新同步状态和时间戳

## 支持的DNS记录类型

### A记录
```json
{
  "userDomainId": 1,
  "namePrefix": "www",
  "type": "A",
  "content": "192.168.1.1",
  "ttl": 300
}
```

### AAAA记录
```json
{
  "userDomainId": 1,
  "namePrefix": "ipv6",
  "type": "AAAA",
  "content": "2001:db8::1",
  "ttl": 300
}
```

### CNAME记录
```json
{
  "userDomainId": 1,
  "namePrefix": "alias",
  "type": "CNAME",
  "content": "target.example.com",
  "ttl": 300,
  "proxied": true
}
```

### MX记录
```json
{
  "userDomainId": 1,
  "namePrefix": "@",
  "type": "MX",
  "content": "mail.example.com",
  "priority": 10,
  "ttl": 300
}
```

### TXT记录
```json
{
  "userDomainId": 1,
  "namePrefix": "@",
  "type": "TXT",
  "content": "v=spf1 include:_spf.example.com ~all",
  "ttl": 300
}
```

### SRV记录
```json
{
  "userDomainId": 1,
  "namePrefix": "_sip._tcp",
  "type": "SRV",
  "content": "10 5 5060 sip.example.com",
  "priority": 10,
  "weight": 5,
  "port": 5060,
  "target": "sip.example.com",
  "ttl": 300
}
```

## 错误码说明

| 错误码 | 含义 | 处理建议 |
|--------|------|----------|
| 400 | 参数验证失败 | 检查请求参数格式和必需字段 |
| 401 | 未授权 | 检查JWT令牌是否有效 |
| 403 | 禁止访问 | 用户无权限操作该域名 |
| 404 | 资源不存在 | 用户域名不存在或已删除 |
| 409 | 资源冲突 | DNS记录已存在 |
| 423 | 资源锁定 | DNS记录数量已达上限 |
| 500 | 服务器错误 | Cloudflare API错误或系统异常 |

## 注意事项

1. **域名前缀规则**：
   - 输入"@"表示根域名
   - 输入"www"会自动拼接为"www.用户子域名"
   - 不支持跨域名操作

2. **代理设置**：
   - 只有A、AAAA、CNAME记录支持Cloudflare代理
   - 启用代理后TTL会被Cloudflare自动管理

3. **记录限制**：
   - 每个用户在单个域名下最多2条DNS记录
   - NS记录允许多条，但内容不能重复

4. **同步机制**：
   - 先调用Cloudflare API创建记录
   - API成功后才保存到本地数据库
   - 确保数据一致性

5. **错误处理**：
   - Cloudflare API失败时不会保存本地记录
   - 提供详细的错误信息便于排查问题