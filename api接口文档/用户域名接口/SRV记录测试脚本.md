# SRV记录测试脚本

## 测试环境准备

### 1. 获取JWT令牌
```bash
# 用户登录获取JWT令牌
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "allnotice@qq.com",
    "password": "12345678"
  }'
```

### 2. 获取用户域名列表
```bash
# 获取当前用户的3级域名列表
curl -X GET "http://localhost:8080/api/user/subdomains" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## SRV记录测试用例

### 测试用例1：添加SIP服务记录

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_sip._tcp",
    "type": "SRV",
    "value": "10 5 5060 sip.example.com",
    "ttl": 600,
    "remark": "SIP服务器记录"
  }'
```

**预期结果**：
- HTTP状态码：200
- 响应code：200
- 返回完整的SRV记录信息

### 测试用例2：添加Minecraft服务记录

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_minecraft._tcp",
    "type": "SRV",
    "value": "0 10 25565 mc.example.com",
    "ttl": 300,
    "remark": "Minecraft游戏服务器"
  }'
```

**预期结果**：
- HTTP状态码：200
- 响应code：200
- 记录成功创建

### 测试用例3：添加HTTPS服务记录

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_https._tcp",
    "type": "SRV",
    "value": "10 0 443 web.example.com",
    "ttl": 600,
    "remark": "HTTPS Web服务"
  }'
```

**预期结果**：
- HTTP状态码：200
- 响应code：200
- 记录成功创建

### 测试用例4：服务不可用记录

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_ftp._tcp",
    "type": "SRV",
    "value": "0 0 0 .",
    "ttl": 600,
    "remark": "FTP服务不可用"
  }'
```

**预期结果**：
- HTTP状态码：200
- 响应code：200
- 目标主机为"."表示服务不可用

## 错误测试用例

### 错误测试1：SRV记录格式错误

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_test._tcp",
    "type": "SRV",
    "value": "invalid format",
    "remark": "格式错误测试"
  }'
```

**预期结果**：
- HTTP状态码：400
- 响应code：400
- 错误信息：SRV记录格式不正确

### 错误测试2：优先级超出范围

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_test._tcp",
    "type": "SRV",
    "value": "70000 5 80 web.example.com",
    "remark": "优先级超范围测试"
  }'
```

**预期结果**：
- HTTP状态码：400
- 响应code：400
- 错误信息：优先级必须在0-65535范围内

### 错误测试3：端口超出范围

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_test._tcp",
    "type": "SRV",
    "value": "10 5 70000 web.example.com",
    "remark": "端口超范围测试"
  }'
```

**预期结果**：
- HTTP状态码：400
- 响应code：400
- 错误信息：端口必须在1-65535范围内

### 错误测试4：服务名称格式错误

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "invalid-service",
    "type": "SRV",
    "value": "10 5 80 web.example.com",
    "remark": "服务名格式错误测试"
  }'
```

**预期结果**：
- HTTP状态码：400
- 响应code：400
- 错误信息：主机记录格式不正确

### 错误测试5：目标主机格式错误

```bash
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_test._tcp",
    "type": "SRV",
    "value": "10 5 80 invalid..domain",
    "remark": "目标主机格式错误测试"
  }'
```

**预期结果**：
- HTTP状态码：400
- 响应code：400
- 错误信息：目标主机格式不正确

### 错误测试6：重复记录

```bash
# 先添加一条记录
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_duplicate._tcp",
    "type": "SRV",
    "value": "10 5 80 web.example.com"
  }'

# 再次添加相同记录
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_duplicate._tcp",
    "type": "SRV",
    "value": "20 10 8080 web2.example.com"
  }'
```

**预期结果**：
- 第一次：HTTP状态码200，记录创建成功
- 第二次：HTTP状态码409，记录已存在错误

## 负载均衡测试

### 添加多个相同服务的SRV记录

```bash
# 主服务器 - 高优先级
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_http._tcp",
    "type": "SRV",
    "value": "10 50 80 web1.example.com",
    "remark": "主Web服务器"
  }'

# 备用服务器 - 相同优先级，不同权重
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_http._tcp",
    "type": "SRV", 
    "value": "10 30 80 web2.example.com",
    "remark": "备用Web服务器"
  }'

# 备份服务器 - 低优先级
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_http._tcp",
    "type": "SRV",
    "value": "20 0 80 backup.example.com",
    "remark": "备份服务器"
  }'
```

## 查询SRV记录

### 获取用户所有DNS记录

```bash
curl -X GET "http://localhost:8080/api/user/dns-records?subdomainId=1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 过滤SRV记录

```bash
curl -X GET "http://localhost:8080/api/user/dns-records?subdomainId=1&type=SRV" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 常见SRV服务配置示例

### 邮件服务配置

```bash
# SMTP提交服务
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_submission._tcp",
    "type": "SRV",
    "value": "10 0 587 mail.example.com",
    "remark": "SMTP提交服务"
  }'

# IMAP服务
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_imap._tcp",
    "type": "SRV",
    "value": "10 0 143 mail.example.com",
    "remark": "IMAP邮件服务"
  }'

# IMAPS服务
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_imaps._tcp",
    "type": "SRV",
    "value": "10 0 993 mail.example.com",
    "remark": "IMAPS安全邮件服务"
  }'
```

### 即时通讯服务配置

```bash
# XMPP服务器到服务器
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_xmpp-server._tcp",
    "type": "SRV",
    "value": "5 0 5269 xmpp.example.com",
    "remark": "XMPP服务器间通信"
  }'

# XMPP客户端连接
curl -X POST "http://localhost:8080/api/user/dns-records" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomainId": 1,
    "name": "_xmpp-client._tcp",
    "type": "SRV",
    "value": "5 0 5222 xmpp.example.com",
    "remark": "XMPP客户端连接"
  }'
```

## 测试结果验证

### 成功响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 456,
    "userId": 789,
    "subdomainId": 1,
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

### 错误响应示例

```json
{
  "code": 400,
  "message": "参数验证失败：SRV记录格式不正确。正确格式：优先级 权重 端口 目标主机，例如：10 5 443 target.example.com，当前值：invalid format",
  "data": null,
  "timestamp": "2025-09-18T10:00:00Z"
}
```

## 测试检查清单

- [ ] SRV记录格式验证正常
- [ ] 优先级范围验证（0-65535）
- [ ] 权重范围验证（0-65535）
- [ ] 端口范围验证（1-65535）
- [ ] 目标主机域名格式验证
- [ ] 服务名称格式验证（_service._protocol）
- [ ] 重复记录检测
- [ ] 权限验证（只能操作自己的域名）
- [ ] DNSPod同步状态正常
- [ ] 数据库记录保存正确
- [ ] 错误信息提示准确

## 注意事项

1. **替换变量**：测试前请替换`YOUR_JWT_TOKEN`和`subdomainId`为实际值
2. **域名权限**：确保测试的域名属于当前登录用户
3. **DNSPod配置**：确保DNSPod API配置正确
4. **网络连接**：确保服务器能够访问腾讯云DNSPod API
5. **数据清理**：测试完成后可删除测试记录避免污染数据