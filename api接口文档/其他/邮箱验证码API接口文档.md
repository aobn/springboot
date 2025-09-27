# 邮箱验证码 API 接口文档

## 1. 发送邮箱验证码

### 基本信息

- **接口标识**：`VERIFICATION_SEND_CODE`
- **请求路径**：`POST /api/verification/send-code`
- **接口描述**：向指定邮箱发送验证码
- **认证要求**：无需认证（公开接口）
- **适用业务单元**：用户注册、密码重置、邮箱验证等需要验证码的场景

### 请求参数

```json
{
  "email": "user@example.com"  // 接收验证码的邮箱地址
}
```

### 参数验证规则

```json
{
  "email": {
    "required": true,
    "type": "email",
    "message": "请输入有效的邮箱地址"
  }
}
```

### 响应示例

#### 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "验证码已发送至邮箱: user@example.com",
  "data": {
    "email": "user@example.com",
    "expireTime": "2025-08-11T01:45:00"
  },
  "timestamp": "2025-08-11T01:40:00"
}
```

#### 失败响应

- **参数验证失败（400）**

```json
{
  "code": 400,
  "message": "邮箱地址不能为空",
  "data": null,
  "timestamp": "2025-08-11T01:40:00"
}
```

- **服务器错误（500）**

```json
{
  "code": 500,
  "message": "发送验证码失败: Connection refused",
  "data": null,
  "timestamp": "2025-08-11T01:40:00"
}
```

### 调试说明

- **测试用例 1（成功场景）**：
  - 请求体：`{"email":"allnotice@qq.com"}`
  - 预期响应：code=200，message 包含 "验证码已发送至邮箱"
  - 实际效果：接收邮箱会收到一封包含 6 位数字验证码的邮件

- **测试用例 2（失败场景 - 无效邮箱）**：
  - 请求体：`{"email":""}`
  - 预期响应：code=400，message="邮箱地址不能为空"

## 2. 验证邮箱验证码

### 基本信息

- **接口标识**：`VERIFICATION_VERIFY_CODE`
- **请求路径**：`POST /api/verification/verify-code`
- **接口描述**：验证用户提供的邮箱验证码是否有效
- **认证要求**：无需认证（公开接口）
- **适用业务单元**：用户注册、密码重置、邮箱验证等需要验证码的场景

### 请求参数

```json
{
  "email": "user@example.com",  // 接收验证码的邮箱地址
  "code": "123456"              // 用户输入的验证码
}
```

### 参数验证规则

```json
{
  "email": {
    "required": true,
    "type": "email",
    "message": "请输入有效的邮箱地址"
  },
  "code": {
    "required": true,
    "type": "string",
    "minLength": 6,
    "maxLength": 6,
    "message": "验证码必须是6位数字"
  }
}
```

### 响应示例

#### 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "验证码验证成功",
  "data": true,
  "timestamp": "2025-08-11T01:45:00"
}
```

#### 失败响应

- **验证码无效（400）**

```json
{
  "code": 400,
  "message": "验证码无效或已过期",
  "data": false,
  "timestamp": "2025-08-11T01:45:00"
}
```

- **参数验证失败（400）**

```json
{
  "code": 400,
  "message": "邮箱地址和验证码不能为空",
  "data": false,
  "timestamp": "2025-08-11T01:45:00"
}
```

### 调试说明

- **测试用例 1（成功场景）**：
  - 先调用发送验证码接口获取验证码
  - 请求体：`{"email":"allnotice@qq.com","code":"123456"}` (使用实际收到的验证码)
  - 预期响应：code=200，message="验证码验证成功"，data=true

- **测试用例 2（失败场景 - 验证码错误）**：
  - 请求体：`{"email":"allnotice@qq.com","code":"000000"}`
  - 预期响应：code=400，message="验证码无效或已过期"，data=false

- **测试用例 3（失败场景 - 验证码过期）**：
  - 等待超过5分钟后再验证
  - 请求体：`{"email":"allnotice@qq.com","code":"123456"}` (使用之前收到的验证码)
  - 预期响应：code=400，message="验证码无效或已过期"，data=false

## 注意事项

1. 验证码有效期为 5 分钟，超时需重新获取
2. 验证码为 6 位数字
3. 验证码验证成功后会被标记为已使用，不能重复使用
4. 同一邮箱重复请求发送验证码会使之前的验证码失效
5. 当前实现使用内存存储验证码，服务重启后验证码会丢失（生产环境应使用数据库或Redis存储）