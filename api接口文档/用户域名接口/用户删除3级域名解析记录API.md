# 用户删除3级域名解析记录API

## 基本信息

- **接口标识**：`USER_DNS_RECORD_DELETE`
- **请求路径**：`POST /api/user/dns-records/delete`
- **接口描述**：用户删除指定的3级域名解析记录
- **认证要求**：需要JWT认证（用户登录后获取的token）
- **适用业务单元**：用户域名管理模块

## 请求参数

```typescript
interface DeleteDnsRecordRequest {
  recordId: number;  // 要删除的DNS解析记录ID
}
```

## 参数验证规则

```json
{
  "recordId": {
    "required": true,
    "type": "number",
    "min": 1,
    "message": "记录ID必须是大于0的整数"
  }
}
```

## 请求头

```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

## 响应示例

### 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true,
  "timestamp": "2025-08-27T12:30:00+08:00"
}
```

### 失败响应

#### 记录不存在（404）

```json
{
  "code": 404,
  "message": "DNS解析记录不存在",
  "data": null,
  "timestamp": "2025-08-27T12:30:00+08:00"
}
```

#### 无权限删除（403）

```json
{
  "code": 403,
  "message": "无权限删除该DNS解析记录",
  "data": null,
  "timestamp": "2025-08-27T12:30:00+08:00"
}
```

#### 未授权（401）

```json
{
  "code": 401,
  "message": "未授权，请先登录",
  "data": null,
  "timestamp": "2025-08-27T12:30:00+08:00"
}
```

#### 服务器错误（500）

```json
{
  "code": 500,
  "message": "删除DNS解析记录失败: {错误信息}",
  "data": null,
  "timestamp": "2025-08-27T12:30:00+08:00"
}
```

## 业务流程

1. 验证用户身份（JWT令牌）
2. 检查用户是否拥有该DNS记录
3. 检查记录是否存在
4. 从DNSPod删除记录（如果已同步到DNSPod）
5. 从本地数据库删除记录
6. 返回操作结果

## 特殊说明

1. 即使从DNSPod删除记录失败，也会继续删除本地记录，确保用户可以管理自己的记录
2. 删除操作是永久性的，无法撤销
3. DNS生效时间可能有延迟，取决于DNS缓存刷新时间

## 调试说明

### 测试用例1（成功场景）

- **请求体**：`{"recordId": 123}`
- **请求头**：`Authorization: Bearer {有效的JWT令牌}`
- **预期响应**：code=200，data=true

### 测试用例2（记录不存在）

- **请求体**：`{"recordId": 999999}`
- **请求头**：`Authorization: Bearer {有效的JWT令牌}`
- **预期响应**：code=404，message="DNS解析记录不存在"

### 测试用例3（无权限）

- **请求体**：`{"recordId": 456}`（属于其他用户的记录）
- **请求头**：`Authorization: Bearer {有效的JWT令牌}`
- **预期响应**：code=403，message="无权限删除该DNS解析记录"

### 测试用例4（未授权）

- **请求体**：`{"recordId": 123}`
- **请求头**：`Authorization: Bearer {无效的JWT令牌}`
- **预期响应**：code=401，message="未授权，请先登录"

## CURL测试命令

```bash
# 成功删除记录
curl -X POST http://localhost:8080/api/user/dns-records/delete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{"recordId": 123}'

# 记录不存在
curl -X POST http://localhost:8080/api/user/dns-records/delete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{"recordId": 999999}'

# 无权限删除
curl -X POST http://localhost:8080/api/user/dns-records/delete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{"recordId": 456}'

# 未授权（无效token）
curl -X POST http://localhost:8080/api/user/dns-records/delete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid_token" \
  -d '{"recordId": 123}'wt_token}" \
  -d '{"recordId": 456}'

# 未授权（无效token）
curl -X POST http://localhost:8080/api/user/dns-records/delete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid_token" \
  -d '{"recordId": 123}'lid_token" \
  -d '{"recordId": 123}'token}" \
  -d '{"recordId": 456}'

# 未授权（无效token）
curl -X POST http://localhost:8080/api/user/dns-records/delete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid_token" \
  -d '{"recordId": 123}'//localhost:8080/api/user/dns-records/delete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{"recordId": 456}'

# 未授权（无效token）
curl -X POST http://localhost:8080/api/user/dns-records/delete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid_token" \
  -d '{"recordId": 123}'