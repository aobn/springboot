# API接口关系图

## 一、接口关系总览

```mermaid
graph TD
    A[用户] -->|注册| B[用户注册]
    A -->|登录| C[用户登录]
    A -->|登出| D[用户登出]
    
    C -->|获取JWT令牌| E[JWT认证]
    E -->|访问受保护资源| F[用户管理]
    E -->|访问受保护资源| G[DNSPod域名解析]
    
    H[管理员] -->|注册| I[管理员注册]
    H -->|登录| J[管理员登录]
    J -->|获取JWT令牌| K[管理员权限]
    K -->|管理用户| L[管理员用户管理]
    
    B -->|发送验证码| M[邮箱验证码]
    B -->|验证验证码| M
```

## 二、用户认证流程

```mermaid
sequenceDiagram
    participant 用户
    participant 前端
    participant 后端
    participant 数据库
    
    用户->>前端: 输入邮箱和密码
    前端->>后端: POST /api/auth/login
    后端->>数据库: 验证用户凭据
    数据库-->>后端: 返回用户信息
    后端->>后端: 生成JWT令牌
    后端-->>前端: 返回用户信息和JWT令牌
    前端->>前端: 存储JWT令牌
    前端-->>用户: 显示登录成功
    
    用户->>前端: 请求受保护资源
    前端->>后端: 请求 + Authorization头(JWT)
    后端->>后端: 验证JWT令牌
    后端-->>前端: 返回受保护资源
    前端-->>用户: 显示资源
    
    用户->>前端: 登出
    前端->>后端: POST /api/auth/logout + JWT
    后端->>后端: 将JWT加入黑名单
    后端-->>前端: 返回登出成功
    前端->>前端: 清除JWT令牌
    前端-->>用户: 显示登出成功
```

## 三、用户注册流程

```mermaid
sequenceDiagram
    participant 用户
    participant 前端
    participant 后端
    participant 邮箱服务
    
    用户->>前端: 输入邮箱和密码
    前端->>后端: POST /api/auth/register/step1
    后端->>后端: 验证邮箱是否已注册
    后端->>邮箱服务: 发送验证码
    邮箱服务-->>用户: 发送包含验证码的邮件
    后端-->>前端: 返回发送成功
    前端-->>用户: 提示查看邮箱
    
    用户->>前端: 输入收到的验证码
    前端->>后端: POST /api/auth/register/step2
    后端->>后端: 验证验证码
    后端->>数据库: 创建用户
    后端-->>前端: 返回注册成功
    前端-->>用户: 显示注册成功
```

## 四、管理员用户管理流程

```mermaid
graph TD
    A[管理员登录] -->|获取JWT令牌| B[管理员权限]
    B -->|查询用户| C[GET /api/admin/users/]
    B -->|查询单个用户| D[GET /api/admin/users/{id}]
    B -->|创建用户| E[POST /api/admin/users]
    B -->|更新用户| F[PUT /api/admin/users/{id}]
    B -->|删除用户| G[DELETE /api/admin/users/{id}]
    B -->|邮箱查询用户| H[GET /api/admin/users/email/{email}]
```

## 五、DNSPod域名解析流程

```mermaid
graph TD
    A[用户/管理员登录] -->|获取JWT令牌| B[JWT认证]
    B -->|查询解析记录| C[GET /api/dnspod/records]
    B -->|添加解析记录| D[POST /api/dnspod/records]
    B -->|修改解析记录| E[POST /api/dnspod/records/modify]
    B -->|删除解析记录| F[DELETE /api/dnspod/records]
```

## 六、接口依赖关系

### 1. 用户认证依赖
- 用户登录 → JWT令牌 → 访问受保护资源
- 用户注册 → 邮箱验证码 → 用户创建

### 2. 管理员功能依赖
- 管理员登录 → JWT令牌 → 管理员权限 → 用户管理操作

### 3. DNSPod功能依赖
- 用户/管理员登录 → JWT令牌 → DNSPod操作权限 → DNS记录管理

## 七、接口调用顺序建议

### 用户注册与登录
1. POST /api/auth/register/step1 (发送验证码)
2. POST /api/auth/register/step2 (验证并完成注册)
3. POST /api/auth/login (登录获取JWT令牌)

### 用户管理
1. GET /api/users (获取用户列表)
2. GET /api/users/{id} (获取单个用户)
3. PUT /api/users/{id} (更新用户信息)

### 管理员操作
1. POST /api/admin/login (管理员登录)
2. GET /api/admin/users/ (获取所有用户)
3. POST /api/admin/users (创建用户)
4. PUT /api/admin/users/{id} (更新用户)
5. DELETE /api/admin/users/{id} (删除用户)

### DNSPod域名解析
1. GET /api/dnspod/records (查询解析记录)
2. POST /api/dnspod/records (添加解析记录)
3. POST /api/dnspod/records/modify (修改解析记录)
4. DELETE /api/dnspod/records (删除解析记录)