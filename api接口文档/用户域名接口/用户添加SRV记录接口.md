# 用户添加SRV记录接口文档

## 接口概述

本接口用于用户为自己的3级域名添加SRV（Service Record）解析记录，SRV记录用于指定服务的位置信息。

## 接口信息

- **接口标识**：`USER_ADD_SRV_RECORD`
- **请求路径**：`POST /api/user/dns-records`
- **接口描述**：用户添加3级域名SRV解析记录
- **认证要求**：需要JWT认证
- **适用业务单元**：用户域名解析记录管理

## SRV记录说明

SRV记录用于定义服务的位置，包含以下信息：
- **优先级（Priority）**：0-65535，数值越小优先级越高
- **权重（Weight）**：0-65535，相同优先级下的负载均衡权重
- **端口（Port）**：1-65535，服务监听的端口号
- **目标主机（Target）**：提供服务的主机名或"."表示服务不可用

## 请求参数

### 必填参数

| 参数名称 | 类型 | 描述 | 示例值 |
|---------|------|------|--------|
| subdomainId | Long | 用户3级域名ID | 123 |
| name | String | 服务名称（格式：_service._protocol） | _sip._tcp |
| type | String | 记录类型（固定为SRV） | SRV |
| value | String | SRV记录值（格式：优先级 权重 端口 目标主机） | 10 5 5060 sip.example.com |

### 可选参数

| 参数名称 | 类型 | 描述 | 默认值 | 示例值 |
|---------|------|------|--------|--------|
| line | String | 记录线路 | 默认 | 默认 |
| ttl | Integer | TTL值（秒，1-604800） | 600 | 600 |
| remark | String | 备注信息 | null | SIP服务器记录 |

## SRV记录格式详解

### 服务名称格式
服务名称必须以下划线开头，格式为：`_service._protocol`

常见的服务名称：
- `_sip._tcp` - SIP over TCP
- `_sip._udp` - SIP over UDP
- `_http._tcp` - HTTP服务
- `_https._tcp` - HTTPS服务
- `_ftp._tcp` - FTP服务
- `_smtp._tcp` - SMTP邮件服务
- `_imap._tcp` - IMAP邮件服务
- `_pop3._tcp` - POP3邮件服务
- `_xmpp-server._tcp` - XMPP服务器
- `_minecraft._tcp` - Minecraft服务器

### 记录值格式
SRV记录值格式：`优先级 权重 端口 目标主机`

**参数说明：**
- **优先级**：0-65535，客户端优先选择数值小的记录
- **权重**：0-65535，相同优先级下的负载均衡，权重越大被选中概率越高
- **端口**：1-65535，服务监听的端口号
- **目标主机**：域名或"."（点表示服务不可用）

## 请求示例

### 添加SIP服务记录

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 123,
    "name": "_sip._tcp",
    "type": "SRV",
    "value": "10 5 5060 sip.example.com",
    "ttl": 600,
    "remark": "SIP服务器记录"
  }'
```

### 添加Minecraft服务记录

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 123,
    "name": "_minecraft._tcp",
    "type": "SRV",
    "value": "0 10 25565 mc.example.com",
    "ttl": 300,
    "remark": "Minecraft服务器"
  }'
```

### 添加HTTPS服务记录

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 123,
    "name": "_https._tcp",
    "type": "SRV",
    "value": "10 0 443 web.example.com",
    "ttl": 600,
    "remark": "HTTPS服务"
  }'
```

### 禁用服务记录

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 123,
    "name": "_service._tcp",
    "type": "SRV",
    "value": "0 0 0 .",
    "ttl": 600,
    "remark": "服务不可用"
  }'
```

## 响应格式

### 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 456,
    "userId": 789,
    "subdomainId": 123,
    "recordId": 162,
    "name": "_sip._tcp",
    "type": "SRV",
    "value": "10 5 5060 sip.example.com",
    "line": "默认",
    "lineId": "0",
    "ttl": 600,
    "mx": null,
    "weight": null,
    "status": "ENABLE",
    "remark": "SIP服务器记录",
    "monitorStatus": null,
    "updatedOn": null,
    "syncStatus": "SUCCESS",
    "syncError": null,
    "createTime": "2025-09-18T10:00:00",
    "updateTime": "2025-09-18T10:00:00"
  },
  "timestamp": "2025-09-18T10:00:00Z"
}
```

### 失败响应

#### SRV记录格式错误（400）

```json
{
  "code": 400,
  "message": "参数验证失败：SRV记录格式不正确。正确格式：优先级 权重 端口 目标主机，例如：10 5 443 target.example.com，当前值：invalid-format",
  "data": null,
  "timestamp": "2025-09-18T10:00:00Z"
}
```

#### 服务名称格式错误（400）

```json
{
  "code": 400,
  "message": "参数验证失败：主机记录格式不正确，SRV记录的服务名称必须以下划线开头，格式如：_service._protocol",
  "data": null,
  "timestamp": "2025-09-18T10:00:00Z"
}
```

#### 端口范围错误（400）

```json
{
  "code": 400,
  "message": "参数验证失败：SRV记录端口必须在1-65535范围内，当前值：70000",
  "data": null,
  "timestamp": "2025-09-18T10:00:00Z"
}
```

## 常见SRV记录配置示例

### 1. SIP服务配置

```json
{
  "subdomainId": 123,
  "name": "_sip._tcp",
  "type": "SRV",
  "value": "10 5 5060 sip.mydomain.com",
  "remark": "SIP over TCP"
}
```

### 2. 游戏服务器配置

```json
{
  "subdomainId": 123,
  "name": "_minecraft._tcp",
  "type": "SRV",
  "value": "0 10 25565 game.mydomain.com",
  "remark": "Minecraft服务器"
}
```

### 3. 邮件服务配置

```json
{
  "subdomainId": 123,
  "name": "_submission._tcp",
  "type": "SRV",
  "value": "10 0 587 mail.mydomain.com",
  "remark": "邮件提交服务"
}
```

### 4. 负载均衡配置

```json
[
  {
    "name": "_http._tcp",
    "value": "10 50 80 web1.mydomain.com",
    "remark": "主Web服务器"
  },
  {
    "name": "_http._tcp", 
    "value": "10 30 80 web2.mydomain.com",
    "remark": "备用Web服务器"
  },
  {
    "name": "_http._tcp",
    "value": "20 0 80 backup.mydomain.com",
    "remark": "备份服务器"
  }
]
```

## 验证规则

### 服务名称验证
- 必须以下划线开头
- 格式：`_service._protocol`
- 服务名和协议名只能包含字母、数字、连字符
- 总长度不超过100个字符

### SRV记录值验证
- 必须包含4个部分，用空格分隔
- 优先级：0-65535的整数
- 权重：0-65535的整数
- 端口：1-65535的整数
- 目标主机：有效域名或"."

### 目标主机验证
- 符合域名格式规范
- 或者是"."表示服务不可用
- 域名长度不超过253个字符

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|-------|------|----------|
| 400 | SRV记录格式错误 | 检查记录值格式：优先级 权重 端口 目标主机 |
| 400 | 服务名称格式错误 | 使用正确格式：_service._protocol |
| 400 | 参数范围错误 | 检查优先级、权重、端口的数值范围 |
| 401 | 未授权 | 检查JWT令牌是否有效 |
| 403 | 权限不足 | 确保操作的是自己的子域名 |
| 409 | 记录冲突 | 相同服务名的SRV记录已存在 |

## 注意事项

1. **服务发现**：SRV记录主要用于服务发现，客户端通过查询SRV记录找到服务位置
2. **优先级规则**：客户端优先选择优先级数值小的记录
3. **负载均衡**：相同优先级的记录按权重进行负载均衡
4. **服务禁用**：目标主机设为"."表示服务不可用
5. **协议支持**：常见协议包括tcp、udp、tls等
6. **端口映射**：端口号必须与实际服务监听端口一致
7. **DNS缓存**：SRV记录变更可能需要等待DNS缓存过期

## 测试用例

### 测试用例1：成功添加SIP服务记录

**请求参数**：
```json
{
  "subdomainId": 123,
  "name": "_sip._tcp",
  "type": "SRV",
  "value": "10 5 5060 sip.example.com",
  "ttl": 600,
  "remark": "SIP服务器"
}
```

**预期响应**：
- code=200
- data包含完整的SRV记录信息
- syncStatus="SUCCESS"

### 测试用例2：SRV记录格式错误

**请求参数**：
```json
{
  "subdomainId": 123,
  "name": "_sip._tcp",
  "type": "SRV",
  "value": "invalid format"
}
```

**预期响应**：
- code=400
- message包含"SRV记录格式不正确"

### 测试用例3：端口超出范围

**请求参数**：
```json
{
  "subdomainId": 123,
  "name": "_test._tcp",
  "type": "SRV",
  "value": "10 5 70000 test.example.com"
}
```

**预期响应**：
- code=400
- message包含"端口必须在1-65535范围内"

### 测试用例4：服务名称格式错误

**请求参数**：
```json
{
  "subdomainId": 123,
  "name": "invalid-service-name",
  "type": "SRV",
  "value": "10 5 80 web.example.com"
}
```

**预期响应**：
- code=400
- message包含"主机记录格式不正确"

## 相关接口

- [用户添加DNS解析记录](./用户添加3级域名解析记录接口.md)
- [用户修改DNS解析记录](./用户修改3级域名解析记录接口.md)
- [用户删除DNS解析记录](./用户删除3级域名解析记录API.md)
- [获取用户DNS解析记录列表](./用户DNS记录查询接口JSON版.md)