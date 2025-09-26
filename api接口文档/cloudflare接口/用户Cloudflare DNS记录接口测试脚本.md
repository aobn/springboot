# 用户Cloudflare DNS记录接口测试脚本

## 测试环境配置

```bash
# 测试服务器地址
BASE_URL="http://localhost:8001"

# 测试用户JWT令牌（需要先登录获取）
JWT_TOKEN="your_jwt_token_here"

# 测试用户域名ID（需要先注册Cloudflare域名）
USER_DOMAIN_ID=1
```

## 1. 添加A记录测试

### 测试用例1：成功添加A记录
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "www",
    "type": "A",
    "content": "192.168.1.1",
    "ttl": 300,
    "comment": "测试A记录"
  }'
```

**预期响应**：
```json
{
  "code": 200,
  "message": "DNS记录添加成功",
  "data": {
    "id": 123,
    "recordId": "cloudflare_record_id",
    "name": "www.0.1.example.com",
    "type": "A",
    "content": "192.168.1.1",
    "ttl": 300
  }
}
```

### 测试用例2：IPv4地址格式错误
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "test",
    "type": "A",
    "content": "invalid.ip.address",
    "ttl": 300
  }'
```

**预期响应**：
```json
{
  "code": 400,
  "message": "参数验证失败：IPv4地址只能包含数字和点，不能包含字母或其他字符"
}
```

## 2. 添加AAAA记录测试

### 测试用例3：成功添加AAAA记录
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "ipv6",
    "type": "AAAA",
    "content": "2001:db8::1",
    "ttl": 300
  }'
```

## 3. 添加CNAME记录测试

### 测试用例4：成功添加CNAME记录（启用代理）
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "alias",
    "type": "CNAME",
    "content": "target.example.com",
    "ttl": 1,
    "proxied": true,
    "comment": "CNAME记录测试"
  }'
```

## 4. 添加MX记录测试

### 测试用例5：成功添加MX记录
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "@",
    "type": "MX",
    "content": "mail.example.com",
    "priority": 10,
    "ttl": 300
  }'
```

### 测试用例6：MX记录缺少优先级
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "@",
    "type": "MX",
    "content": "mail.example.com",
    "ttl": 300
  }'
```

**预期响应**：
```json
{
  "code": 400,
  "message": "MX记录缺少必需字段：priority"
}
```

## 5. 添加TXT记录测试

### 测试用例7：成功添加TXT记录
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "@",
    "type": "TXT",
    "content": "v=spf1 include:_spf.example.com ~all",
    "ttl": 300
  }'
```

## 6. 添加SRV记录测试

### 测试用例8：成功添加SRV记录
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "_sip._tcp",
    "type": "SRV",
    "content": "10 5 5060 sip.example.com",
    "priority": 10,
    "weight": 5,
    "port": 5060,
    "service": "_sip",
    "proto": "_tcp",
    "target": "sip.example.com",
    "ttl": 300
  }'
```

### 测试用例9：SRV记录缺少必需字段
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "_sip._tcp",
    "type": "SRV",
    "content": "10 5 5060 sip.example.com",
    "ttl": 300
  }'
```

**预期响应**：
```json
{
  "code": 400,
  "message": "SRV记录缺少必需字段：priority、weight、port、service、proto、target"
}
```

## 7. 权限和限制测试

### 测试用例10：无效的用户域名ID
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 99999,
    "namePrefix": "test",
    "type": "A",
    "content": "192.168.1.1",
    "ttl": 300
  }'
```

**预期响应**：
```json
{
  "code": 404,
  "message": "用户域名不存在或无权限访问"
}
```

### 测试用例11：DNS记录数量限制测试
```bash
# 先添加第一条记录
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "test1",
    "type": "A",
    "content": "192.168.1.1",
    "ttl": 300
  }'

# 添加第二条记录
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "test2",
    "type": "A",
    "content": "192.168.1.2",
    "ttl": 300
  }'

# 尝试添加第三条记录（应该失败）
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "test3",
    "type": "A",
    "content": "192.168.1.3",
    "ttl": 300
  }'
```

**第三条记录预期响应**：
```json
{
  "code": 423,
  "message": "DNS记录数量已达上限（2条），无法添加新记录"
}
```

## 8. 获取DNS记录列表测试

### 测试用例12：获取指定域名的DNS记录
```bash
curl -X GET "${BASE_URL}/api/user/cloudflare/dns-records?userDomainId=1" \
  -H "Authorization: Bearer ${JWT_TOKEN}"
```

### 测试用例13：按类型过滤DNS记录
```bash
curl -X GET "${BASE_URL}/api/user/cloudflare/dns-records?userDomainId=1&type=A" \
  -H "Authorization: Bearer ${JWT_TOKEN}"
```

### 测试用例14：获取所有DNS记录
```bash
curl -X GET "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer ${JWT_TOKEN}"
```

## 9. 获取DNS记录详情测试

### 测试用例15：根据记录ID获取详情
```bash
# 使用实际的记录ID
RECORD_ID="cloudflare_record_id_123"

curl -X GET "${BASE_URL}/api/user/cloudflare/dns-records/${RECORD_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}"
```

## 10. 删除DNS记录测试

### 测试用例16：成功删除DNS记录
```bash
RECORD_ID="cloudflare_record_id_123"

curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records/delete?recordId=${RECORD_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}"
```

**预期响应**：
```json
{
  "code": 200,
  "message": "DNS记录删除成功",
  "data": "DNS记录删除成功"
}
```

### 测试用例17：删除不存在的记录
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records/delete?recordId=invalid_record_id" \
  -H "Authorization: Bearer ${JWT_TOKEN}"
```

**预期响应**：
```json
{
  "code": 404,
  "message": "DNS记录不存在"
}
```

## 11. 认证测试

### 测试用例18：无效JWT令牌
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Authorization: Bearer invalid_token" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "test",
    "type": "A",
    "content": "192.168.1.1"
  }'
```

**预期响应**：
```json
{
  "code": 401,
  "message": "未授权访问"
}
```

### 测试用例19：缺少Authorization头
```bash
curl -X POST "${BASE_URL}/api/user/cloudflare/dns-records" \
  -H "Content-Type: application/json" \
  -d '{
    "userDomainId": 1,
    "namePrefix": "test",
    "type": "A",
    "content": "192.168.1.1"
  }'
```

## 测试结果验证

### 成功标准
1. **添加记录**：返回200状态码，包含完整的记录信息
2. **参数验证**：错误参数返回400状态码和具体错误信息
3. **权限控制**：无权限操作返回403或404状态码
4. **数量限制**：超出限制返回423状态码
5. **记录查询**：返回正确的记录列表和详情
6. **记录删除**：成功删除并返回确认信息

### 验证步骤
1. 执行所有测试用例
2. 检查响应状态码和消息
3. 验证Cloudflare控制台中的记录状态
4. 确认本地数据库记录同步正确
5. 测试DNS解析是否生效

### 注意事项
1. 测试前确保已注册Cloudflare域名
2. 替换实际的JWT令牌和域名ID
3. 某些测试可能需要清理测试数据
4. 注意DNS记录数量限制，避免超出配额
5. 测试完成后及时清理测试记录