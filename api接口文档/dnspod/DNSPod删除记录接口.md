# DNSPod删除记录接口

## 1. 接口描述

- **接口标识**：`DNSPOD_DELETE_RECORD`
- **请求路径**：`POST /api/dnspod/records/delete`
- **请求方法**：`POST`
- **Content-Type**：`application/json`
- **接口描述**：删除指定域名的DNS解析记录
- **认证要求**：需要认证（管理员权限）
- **适用业务单元**：域名管理模块

## 2. 请求参数

### 2.1 请求体格式（JSON）

```json
{
  "domain": "example.com",
  "recordId": 162,
  "domainId": 123456
}
```

### 2.2 参数说明

| 参数名称 | 必选 | 类型   | 描述                                                         |
| -------- | ---- | ------ | ------------------------------------------------------------ |
| domain   | 是   | String | 域名，如 example.com                                         |
| recordId | 是   | Long   | 记录ID，可通过查询接口获取                                   |
| domainId | 否   | Long   | 域名ID，参数domainId优先级比参数domain高，如果传递参数domainId将忽略参数domain |

## 3. 响应格式

### 3.1 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "RequestId": "ab4f1426-ea15-42ea-8183-dc1b44151166"
  },
  "timestamp": "2025-08-17T08:00:00Z"
}
```

### 3.2 失败响应

#### 3.2.1 参数错误（状态码 400）

```json
{
  "code": 400,
  "message": "参数验证失败，请检查输入",
  "data": {
    "errors": [
      {
        "field": "recordId",
        "message": "记录ID不能为空"
      }
    ]
  },
  "timestamp": "2025-08-17T08:00:00Z"
}
```

#### 3.2.2 认证失败（状态码 401）

```json
{
  "code": 401,
  "message": "未授权，请先登录",
  "data": null,
  "timestamp": "2025-08-17T08:00:00Z"
}
```

#### 3.2.3 权限不足（状态码 403）

```json
{
  "code": 403,
  "message": "权限不足，需要管理员权限",
  "data": null,
  "timestamp": "2025-08-17T08:00:00Z"
}
```

#### 3.2.4 记录不存在（状态码 404）

```json
{
  "code": 404,
  "message": "记录不存在或已被删除",
  "data": null,
  "timestamp": "2025-08-17T08:00:00Z"
}
```

#### 3.2.5 服务器错误（状态码 500）

```json
{
  "code": 500,
  "message": "删除域名解析记录失败: 服务器内部错误",
  "data": null,
  "timestamp": "2025-08-17T08:00:00Z"
}
```

## 4. 调试说明

### 4.1 测试用例

#### 4.1.1 成功场景

- **请求参数**：
  ```
  domain=example.com&recordId=162
  ```
- **预期响应**：
  - 状态码：200
  - 响应体：包含RequestId

#### 4.1.2 失败场景 - 记录不存在

- **请求参数**：
  ```
  domain=example.com&recordId=99999
  ```
- **预期响应**：
  - 状态码：404
  - 响应体：记录不存在或已被删除

#### 4.1.1 成功场景

- **请求参数**：
  ```json
  {
    "domain": "example.com",
    "recordId": 162
  }
  ```
- **预期响应**：
  - 状态码：200
  - 响应体：包含RequestId

#### 4.1.2 失败场景 - 记录不存在

- **请求参数**：
  ```json
  {
    "domain": "example.com",
    "recordId": 99999
  }
  ```
- **预期响应**：
  - 状态码：404
  - 响应体：记录不存在或已被删除

### 4.2 CURL测试命令

```bash
# 删除记录（JSON格式）
curl -X POST "http://localhost:8080/api/dnspod/records/delete" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "example.com",
    "recordId": 162
  }'
```

## 5. 注意事项

1. 删除操作不可恢复，请谨慎操作
2. 需要管理员权限才能执行删除操作
3. 建议在删除前先通过查询接口确认记录ID的正确性
4. 删除成功后，DNS生效可能需要一定时间（通常几分钟到几小时不等）
5. **接口格式变更**：现在使用POST方法和JSON请求体，而非DELETE方法和URL参数
