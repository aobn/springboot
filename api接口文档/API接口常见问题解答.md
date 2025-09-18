# API接口常见问题解答 (FAQ)

## 一、认证相关问题

### Q1: 如何获取JWT令牌？
**A**: 通过调用登录接口获取JWT令牌：
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "your_email@example.com",
  "password": "your_password"
}
```
成功登录后，响应中的`data.token`字段包含JWT令牌。

### Q2: 如何使用JWT令牌访问受保护的API？
**A**: 在请求头中添加`Authorization`字段，格式为`Bearer {token}`：
```
GET /api/users
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Q3: JWT令牌有效期是多久？
**A**: JWT令牌的有效期为24小时。过期后需要重新登录获取新令牌。

### Q4: 如何处理令牌过期的情况？
**A**: 当令牌过期时，API会返回401状态码。前端应捕获此错误并引导用户重新登录。

### Q5: 登出后令牌会立即失效吗？
**A**: 是的，调用登出接口后，当前令牌会被加入黑名单，立即失效。

## 二、用户注册相关问题

### Q1: 用户注册为什么分两步？
**A**: 两步注册流程用于验证邮箱的真实性，防止恶意注册。第一步发送验证码，第二步验证并完成注册。

### Q2: 验证码有效期是多久？
**A**: 验证码有效期为5分钟。超时需要重新获取。

### Q3: 同一邮箱可以多次请求验证码吗？
**A**: 可以，但同一邮箱在短时间内（1分钟）只能请求一次验证码，且新的验证码会使旧验证码失效。

### Q4: 注册时的密码要求是什么？
**A**: 密码长度至少8位，必须包含大小写字母和数字。

## 三、用户管理相关问题

### Q1: 如何获取当前登录用户的信息？
**A**: 可以通过JWT令牌中的用户ID获取用户信息：
```
GET /api/users/{id}
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Q2: 如何更新用户信息？
**A**: 使用PUT请求更新用户信息：
```
PUT /api/users/{id}
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "username": "new_username",
  "email": "new_email@example.com",
  "password": "NewPassword123"
}
```

### Q3: 更新用户信息时必须提供所有字段吗？
**A**: 不是必须的。可以只提供需要更新的字段，未提供的字段将保持原值。

## 四、管理员相关问题

### Q1: 管理员和普通用户有什么区别？
**A**: 管理员拥有更高的权限，可以管理所有用户（查询、创建、更新、删除），而普通用户只能管理自己的信息。

### Q2: 如何创建管理员账户？
**A**: 通过管理员注册接口创建：
```
POST /api/admin/register
Content-Type: application/json

{
  "username": "admin123",
  "password": "Admin@123",
  "email": "admin@example.com"
}
```

### Q3: 管理员如何管理用户？
**A**: 管理员登录后，可以使用管理员用户管理接口：
- 获取所有用户：`GET /api/admin/users/`
- 创建用户：`POST /api/admin/users`
- 更新用户：`PUT /api/admin/users/{id}`
- 删除用户：`DELETE /api/admin/users/{id}`

## 五、DNSPod域名解析相关问题

### Q1: 如何查询域名的解析记录？
**A**: 使用GET请求查询解析记录：
```
GET /api/dnspod/records?domain=example.com
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Q2: 如何添加域名解析记录？
**A**: 使用POST请求添加解析记录：
```
POST /api/dnspod/records
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/x-www-form-urlencoded

domain=example.com&recordType=A&value=8.8.8.8&subDomain=www
```

### Q3: 添加解析记录后多久生效？
**A**: DNS解析记录通常在几分钟内生效，但完全生效可能需要24-48小时，取决于各地DNS缓存刷新时间。

### Q4: 如何修改解析记录？
**A**: 使用POST请求修改解析记录：
```
POST /api/dnspod/records/modify
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/x-www-form-urlencoded

domain=example.com&recordId=123456&recordType=A&value=8.8.8.8&subDomain=www
```

### Q5: 如何删除解析记录？
**A**: 使用DELETE请求删除解析记录：
```
DELETE /api/dnspod/records?domain=example.com&recordId=123456
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 六、错误处理相关问题

### Q1: 如何处理API返回的错误？
**A**: 所有API错误都遵循统一的响应格式：
```json
{
  "code": 400,
  "message": "错误描述信息",
  "data": null,
  "timestamp": "2025-08-11T00:30:00+08:00"
}
```
前端应根据`code`字段判断错误类型，并显示`message`字段的错误信息。

### Q2: 常见的错误码有哪些？
**A**:
- 400: 请求参数错误
- 401: 未授权（未登录或令牌无效）
- 403: 禁止访问（无权限）
- 404: 资源不存在
- 409: 资源冲突（如用户名已存在）
- 500: 服务器内部错误

### Q3: 如何处理401未授权错误？
**A**: 当收到401错误时，前端应清除本地存储的JWT令牌，并引导用户重新登录。

## 七、其他问题

### Q1: API的请求频率限制是多少？
**A**: 目前API没有严格的频率限制，但建议控制在合理范围内（如每秒不超过10次请求）。

### Q2: API支持跨域请求吗？
**A**: 是的，API已配置CORS支持，允许来自前端域名的跨域请求。

### Q3: 如何测试API接口？
**A**: 可以使用curl命令行工具、Postman等API测试工具，或者参考API文档中提供的测试用例。

### Q4: API响应时间有保证吗？
**A**: 在正常网络条件下，API响应时间通常在100ms以内。但可能受到网络延迟、服务器负载等因素影响。

### Q5: 如何报告API问题？
**A**: 如发现API问题，请提供以下信息：
- 请求的URL和方法
- 请求参数
- 错误响应
- 复现步骤
- 期望的行为